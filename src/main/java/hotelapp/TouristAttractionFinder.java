package hotelapp;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Class responsible for getting tourist attractions near each hotel from the Google Places API.
 *  Also scrapes some data about hotels from expedia html webpage.
 */
public class TouristAttractionFinder {

    private static final String host = "maps.googleapis.com";
    private static final String path = "/maps/api/place/textsearch/json";
    private static final String query = "https://"+host+path+"?query=tourist%20attractions+in";
    private static final String ASTERISKS = "++++++++++++++++++++";
    private static final double MILES_TO_METRES = 1609.344;
    private ThreadSafeHotelData hotelData;
    private static final String FIND_HOTEL_BY_ID_CMD = "find";
    private static final String FIND_ATTRACTIONS = "findAttraction";
    private static final String FIND_DESCRIPTIONS = "findDescriptions";
    private static final String REGEX_PATTERN_STRING_AREA = "(About this area.+?<h4+.*?>)(.*?(?=<))(.*?<p+.*?>)(.*?(?=<))";
    private static final String REGEX_PATTERN_STRING_PROPERTY = "(About this property.+?<h4+.*?>)(.*?(?=<))(.*?<p+.*?>)(.*?(?=<))";


    /** Constructor for TouristAttractionFinder
     * @param hdata ThreadSafeHotelData object
     */
    public TouristAttractionFinder(ThreadSafeHotelData hdata) {
        this.hotelData = hdata;
    }


    /**
     * Creates a secure socket to communicate with Google Places API server,
     * sends a GET request (to find attractions close to
     * the hotel within a given radius), and gets a response as a string.
     * Removes headers from the response string and parses the remaining json to
     * get Attractions info. Adds attractions to the corresponding data structure that supports
     * efficient search for tourist attractions given the hotel id.
     *
     */
    public void fetchAttractions(int radiusInMiles) {
        String apiKey = getAPIKey();
        if (apiKey != null && apiKey.length() > 0) {
            for (String hotelId : hotelData.getHotels()) {
                Hotel hotel = hotelData.getHotel(Integer.parseInt(hotelId));
                String jsonStringWithHeader = getJsonString(radiusInMiles, hotel, apiKey);
                String jsonStringWithoutHeader = removeHeaders(jsonStringWithHeader);
                hotelData.parseTouristAttractionJson(jsonStringWithoutHeader, hotelId);
            }
        } else {
            System.out.println("Please provide API key!!!!!!!!");
        }
    }

    /**
     * Print attractions near the hotels to a file.
     * The format is described in the project description.
     *
     * @param filename File path provided for printing attractions
     */
    public void printAttractions(Path filename) {
        try {
            if (filename == null) {
                return;
            }
            Files.deleteIfExists(filename);
            PrintWriter print = new PrintWriter(filename.toFile());
            List<String> lisOfHotelIds = hotelData.getHotels();
            Collections.sort(lisOfHotelIds);
            for (String hotelId : lisOfHotelIds) {
                print.write(hotelData.toStringAttraction(hotelId));
                print.write(System.lineSeparator());
                print.write(System.lineSeparator());
                print.write(ASTERISKS);
                print.write(System.lineSeparator());
            }
            print.flush();
            print.close();
        } catch (FileNotFoundException e) {
            System.out.println("File not found" + filename);
        } catch (IOException e) {
            System.out.println("IO Exception occurred while printing attraction to file :" + filename);
        }
    }

    /**
     * Takes an html file from expedia for a particular hotelId, and scrapes it for some data about this hotel:
     * About this area and About this property descriptions. Stores this information in ThreadSafeHotelData so that
     * we are able to efficiently access it given the hotel Id.
     *
     * @param filename File Name
     */
    public void parseHTML(String hotelId, Path filename) {
        String fileData = readHTMLFile(filename);
        Map<String, String> descMap = scrapeHTMLFile(fileData);
        hotelData.addDescriptions(hotelId, descMap);
    }

    /**
     * Prints property descriptions and area descriptions for each hotel from
     * the ThreadSafeHotelData to the given file. Format specified in the project description.
     *
     * @param filename output file
     */
    public void printDescriptions(Path filename) {
        try {
            if (filename == null) {
                return;
            }
            Files.deleteIfExists(filename);
            PrintWriter print = new PrintWriter(filename.toFile());
            List<String> lisOfHotelIds = hotelData.getHotels();
            Collections.sort(lisOfHotelIds);
            for (String hotelId : lisOfHotelIds) {
                String text = hotelData.toStringDescription(hotelId);
                if (text.length() > 0) {
                    print.write(hotelData.toStringDescription(hotelId));
                    print.write(System.lineSeparator());
                    print.write(ASTERISKS);
                    print.write(System.lineSeparator());
                }
            }
            print.flush();
            print.close();
        } catch (FileNotFoundException e) {
            System.out.println("File not found" + filename);
        } catch (IOException e) {
            System.out.println("IO Exception occurred while printing description" + filename);
        }
    }


    /**
     * This method will make get request call to the api with the radius value passed
     *
     * @param radiusInMiles radius in miles
     * @param h             Hotel obj
     * @param apiKey        api key
     * @return json String
     */
    private String getJsonString(int radiusInMiles, Hotel h, String apiKey) {
        double radiusInMetres = radiusInMiles * MILES_TO_METRES;
        String s = "";
        String urlString = query + URLEncoder.encode(h.getCi(), StandardCharsets.UTF_8) + "&location=" + h.getLat()+","+h.getLng()+"&radius="+radiusInMetres+"&key="+apiKey;
        URL url;
        PrintWriter out = null;
        BufferedReader in = null;
        SSLSocket socket = null;
        try {
            url = new URL(urlString);
            SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            socket = (SSLSocket) factory.createSocket(url.getHost(), 443);
            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
            String request = getRequest(url.getHost(), url.getPath() + "?" + url.getQuery());
            out.println(request); // send a request to the server
            out.flush();
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            // use input stream to read server's response
            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = in.readLine()) != null) {
                sb.append(line);
            }
            s = sb.toString();
        } catch (IOException e) {
            System.out.println(
                    "An IOException occured while writing to the socket stream or reading from the stream: " + e);
        } finally {
            try {
                if (out != null && in != null) {
                    out.close();
                    in.close();
                    socket.close();
                }
            } catch (IOException e) {
                System.out.println("An exception occured while trying to close the streams or the socket: " + e);
            }
        }
        return s;
    }


    /** Will create getRequest for the url and host provided
     * @param host  host name
     * @param pathResourceQuery query passed
     * @return string
     */
    private String getRequest(String host, String pathResourceQuery) {
        return "GET " + pathResourceQuery + " HTTP/1.1" + System.lineSeparator()
                + "Host: " + host + System.lineSeparator()
                + "Connection: close" + System.lineSeparator()
                + System.lineSeparator();
    }


    /** This method will read HTML file provided and append it to fileData
     * @param filename fileName
     * @return string
     */
    private String readHTMLFile(Path filename) {
        StringBuilder fileData = new StringBuilder();
        String line;
        try (BufferedReader br = new BufferedReader(new FileReader(filename.toFile()))) {
            while ((line = br.readLine()) != null) {
                fileData.append(line);
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found" + filename);
        } catch (IOException e) {
            System.out.println("IO Exception occurred while reading HTML file" + filename);
        }
        return fileData.toString();
    }

    /**
     * This method will extract Area desc and Property desc and stored it into map using regex pattern
     *
     * @param fileData html file
     * @return map with description details
     */
    private Map<String, String> scrapeHTMLFile(String fileData) {
        Map<String, String> descMap = new HashMap<>();
        StringBuilder areaDesc = new StringBuilder();
        Pattern pattern = Pattern.compile(REGEX_PATTERN_STRING_AREA);
        Matcher matcher = pattern.matcher(fileData);
        while (matcher.find()) {
            areaDesc.append(matcher.group(2));
            areaDesc.append(System.lineSeparator());
            areaDesc.append(matcher.group(4));
        }
        descMap.put("AreaDesc", areaDesc.toString());
        StringBuilder propDesc = new StringBuilder();
        Pattern pattern1 = Pattern.compile(REGEX_PATTERN_STRING_PROPERTY);
        Matcher matcher1 = pattern1.matcher(fileData);
        while (matcher1.find()) {
            propDesc.append(matcher1.group(2));
            propDesc.append(System.lineSeparator());
            propDesc.append(matcher1.group(4));
        }
        descMap.put("PropDesc", propDesc.toString());
        return descMap;
    }


    /** This method will remove headers from the  response using regex
     * @param s String response with headers
     * @return string after removing headers from response
     */
    private String removeHeaders(String s) {
        String patternString1 = "(.*?)\\{(.*)";
        String json = "";
        Pattern pattern = Pattern.compile(patternString1);
        Matcher matcher = pattern.matcher(s);
        while (matcher.find()) {
            json = "{" + matcher.group(2);
        }
        return json;
    }

    /** This method will parse user command input provided by the user
     * @param input  String input
     */
    void parseUserInput(String input) {
        String cmdKey = input.contains(" ") ? input.split(" ")[0] : null;
        if (cmdKey != null && input.split(" ").length > 1) {
            String strId = input.split(" ")[1];
            switch (cmdKey) {
                case FIND_HOTEL_BY_ID_CMD:
                    hotelData.findHotelById(strId);
                    break;
                case FIND_ATTRACTIONS:
                    hotelData.findAttractionById(strId);
                    break;
                case FIND_DESCRIPTIONS:
                    hotelData.findDescriptionById(strId);
                    break;
            }
        } else {
            System.out.println("Please provide correct command !!!!!!!!!!");
        }
    }

    /** This method returns API key from the config.json file
     * @return string api key
     */
    private String getAPIKey() {
        String apiKey = "";
        try (JsonReader jsonReader = new JsonReader(new FileReader("input" + File.separator + "config.json"))) {
            JsonElement jsonElement = new JsonParser().parse(jsonReader).getAsJsonObject();
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            apiKey = jsonObject.get("apikey").getAsString();
        } catch (IOException e) {
            System.out.println("IO Exception occurred while retrieving API key");
        }
        return apiKey;
    }

    /**
     * This method will process html files from the map and call parse html on each of the file path
     */
    public void processHTMLFiles() {
        for (String hotelId : hotelData.getHotels()) {
            String fileName = "h" + hotelId + ".html";
            if (getHTMLFilePaths().size() > 0) {
                Path p = getHTMLFilePaths().get(fileName);
                parseHTML(hotelId, p);
            }
        }
    }

    /**
     * This method will iterate over html folder and add file paths to the map
     *
     * @return map with file name as key and path as value
     */
    private Map<String, Path> getHTMLFilePaths() {
        Map<String, Path> mapOfHTMLPaths = new HashMap<>();
        Path actual = Paths.get("input" + File.separator + "html");
        try (DirectoryStream<Path> filesList = Files.newDirectoryStream(actual)) {
            for (Path file : filesList) {
                if (!Files.isDirectory(file)) {
                    mapOfHTMLPaths.put(file.getFileName().toString(), file);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mapOfHTMLPaths;
    }
}