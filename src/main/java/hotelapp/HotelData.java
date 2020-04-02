package hotelapp;

import com.google.gson.*;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.*;

/**
 * This class will populate data for hotel details ,tourist attraction and description details of hotel
 */
public class HotelData {

    private static final String ASTERISKS = "********************";
    private Map<Integer, Hotel> hotelDetailsMap = new HashMap<>();
    private Map<Integer, List<TouristAttraction>> touristAttractionMap = new HashMap<>();
    private Map<Integer, Map<String, String>> descriptionsMap = new HashMap<>();

    /**
     * This method will create a Hotel with the given  parameters, and add it to the appropriate data
     * structure.
     *
     * @param hotelId       Hotel Id
     * @param hotelName     Hotel Name
     * @param city          City
     * @param state         State
     * @param streetAddress Street Address of hotel
     * @param lat           Latitude
     * @param lon           Longitude
     */
    public void addHotel(String hotelId, String hotelName, String city, String state, String streetAddress, double lat,
                         double lon) {
        Hotel hotel = new Hotel(hotelId, hotelName, city, state, streetAddress, lat, lon);
        hotelDetailsMap.put(Integer.parseInt(hotelId), hotel);
    }

    /**
     * This will add tourist attraction the the map
     *
     * @param hotelId hotel id
     * @param id      attraction
     * @param name    attraction name
     * @param rating  rating of attraction
     * @param address address of attraction
     */
    protected void addTouristAttraction(String hotelId, String id, String name, double rating, String address) {
        TouristAttraction attraction = new TouristAttraction(id, name, rating, address);
        int h = Integer.parseInt(hotelId);
        if (touristAttractionMap.containsKey(h)) {
            List<TouristAttraction> touristAttractionList = touristAttractionMap.get(h);
            touristAttractionList.add(attraction);
        } else {
            List<TouristAttraction> touristAttractionList = new ArrayList<>();
            touristAttractionList.add(attraction);
            touristAttractionMap.put(h, touristAttractionList);
        }
    }


    /**
     * This method will add description details into the descriptionsMap map
     *
     * @param hotelId hotel id
     * @param descMap map with property and area desc
     */
    protected void addDescriptions(String hotelId, Map<String, String> descMap) {
        int h = Integer.parseInt(hotelId);
        if (!descriptionsMap.containsKey(h)) {
            descriptionsMap.put(h, descMap);
        }
    }


    /**
     * This method will return list of Hotel Ids
     *
     * @return Return an alphabetized list of the ids of all hotels
     */
    public List<String> getHotels() {
        List<String> hotelIds = new ArrayList<>();
        for (Integer hotelId : hotelDetailsMap.keySet()) {
            hotelIds.add(String.valueOf(hotelId));
        }
        return hotelIds;
    }


    /**
     * This method will return hotel details if we provide hotel id
     *
     * @param id hotel id
     */
    protected void findHotelById(String id) {
        int hotelId = id != null ? isInteger(id) : 0;
        if (hotelId > 0) {
            StringBuilder sb = new StringBuilder();
            Hotel hotel = hotelDetailsMap.get(hotelId);
            if (hotel != null) {
                sb.append("Hotel details of hotelId -- ");
                sb.append(hotelId);
                sb.append(hotel);
                System.out.println(sb.toString());
            } else {
                sb.append("No hotel details available for this hotelId - ");
                sb.append(hotelId);
                System.out.println(sb.toString());
            }
        }

    }

    /**
     * This method will retrieve attraction of particular hotel id
     *
     * @param id hotel id
     */
    protected void findAttractionById(String id) {
        int hotelId = id != null ? isInteger(id) : 0;
        if (hotelId > 0) {
            String attractionString = toStringAttraction(id);
            if (attractionString != null && attractionString.length() > 0) {
                System.out.println(toStringAttraction(id));
            } else {
                System.out.println("No Tourist Attraction found for Hotel :" + hotelId);
            }
        }
    }

    /**
     * This method will retrieve description of particular hotel id
     *
     * @param id hotel id
     */
    void findDescriptionById(String id) {
        int hotelId = id != null ? isInteger(id) : 0;
        if (hotelId > 0) {
            String text = toStringDescription(id);
            if (text.length() > 0) {
                System.out.println(text);
            } else {
                System.out.println("No Description  found for Hotel :" + hotelId);
            }
        }
    }

    /**
     * This method will save the string representation of the hotel data to the file specified by
     * filename in the following format: an empty line A line of 20 asterisks
     * ******************** on the next line information for each hotel, printed
     * in the format described in the toString method of this class.
     * The hotels should be sorted by hotel ids
     *
     * @param filename Path specifying where to save the output.
     */
    public void printToFile(Path filename) {
        try {
            if (filename == null) {
                return;
            }
            PrintWriter print = new PrintWriter(filename.toFile());
            List<String> lisOfHotelIds = getHotels();
            Collections.sort(lisOfHotelIds);
            for (String hotelId : lisOfHotelIds) {
                print.write(System.lineSeparator());
                print.write(ASTERISKS);
                print.write(System.lineSeparator());
                print.write(toString(hotelId));
            }
            print.flush();
            print.close();
        } catch (FileNotFoundException e) {
            System.out.println("Error occurred while printing the file" + filename);
        }
    }


    /**
     * Returns a string representing information about the hotel with the given
     * id, including all the reviews for this hotel separated by
     * --------------------
     * Format of the string: HoteName: hotelId
     * streetAddress city, state
     * --------------------
     * Review by username: rating
     * ReviewTitle ReviewText
     * --------------------
     * Review by username: rating
     * ReviewTitle ReviewText ...
     *
     * @param hotelId Hotel Id
     * @return - output string.
     */
    public String toString(String hotelId) {
        Hotel hotel = hotelDetailsMap.get(Integer.parseInt(hotelId));
        StringBuilder builder = new StringBuilder();
        if (hotel != null) {
            builder.append(hotel.getF());
            builder.append(":");
            builder.append(" ");
            builder.append(hotelId);
            builder.append(System.lineSeparator());
            builder.append(hotel.getAd());
            builder.append(System.lineSeparator());
            builder.append(hotel.getCi());
            builder.append(",");
            builder.append(" ");
            builder.append(hotel.getPr());
            builder.append(System.lineSeparator());
        }
        return builder.toString();
    }


    /**
     * This method will parse tourist attraction json  of particular hotel id
     *
     * @param json    json file
     * @param hotelId hotel id
     */
    void parseTouristAttractionJson(String json, String hotelId) {
        if (json != null && json.length() > 0) {
            JsonElement ele = new JsonParser().parse(json);
            JsonObject jsonObject = ele.getAsJsonObject();
            JsonArray ar = jsonObject.getAsJsonArray("results");
            for (int i = 0; i < ar.size(); i++) {
                JsonObject jsonObject1 = ar.get(i).getAsJsonObject();
                String id = jsonObject1.get("id").getAsString();
                String name = jsonObject1.get("name").getAsString();
                String address = jsonObject1.get("formatted_address").getAsString();
                double rating = jsonObject1.get("rating").getAsDouble();
                addTouristAttraction(hotelId, id, name, rating, address);
            }
        }
    }


    /**
     * Print string representation of description of this hotel Id
     *
     * @param hotelId id of hotel
     * @return string value
     */

    String toStringDescription(String hotelId) {
        Hotel hotel = getHotel(Integer.parseInt(hotelId));
        StringBuilder builder = new StringBuilder();
        Map<String, String> descMap = descriptionsMap.get(Integer.parseInt(hotelId));
        if (hotel != null && descMap != null) {
            builder.append(hotelId);
            String propertyStr = descMap.get("PropDesc");
            if (propertyStr.length() > 0) {
                builder.append(System.lineSeparator());
                builder.append(propertyStr);
            }
            builder.append(System.lineSeparator());
            String areaStr = descMap.get("AreaDesc");
            if (areaStr.length() > 0) {
                builder.append(System.lineSeparator());
                builder.append(areaStr);
            }
        }
        return builder.toString();
    }


    /**
     * Print string representation of attraction of this hotel Id
     *
     * @param hotelId id of hotel
     * @return String value
     */
    String toStringAttraction(String hotelId) {
        Hotel hotel = getHotel(Integer.parseInt(hotelId));
        StringBuilder builder = new StringBuilder();
        List<TouristAttraction> touristAttractionList = touristAttractionMap.get(Integer.parseInt(hotelId));
        if (hotel != null && touristAttractionList != null) {
            builder.append("Attractions near ").append(hotelId).append(",").append(" ").append(hotel.getF());
            for (TouristAttraction attraction : touristAttractionList) {
                builder.append(System.lineSeparator());
                builder.append(attraction.getName());
            }
        }
        return builder.toString();
    }


    /**
     * Checking id is valid Integer or not
     *
     * @param id hotel id passed by the user
     * @return int value of id
     */
    private int isInteger(String id) {
        try {
            return Integer.parseInt(id);
        } catch (NumberFormatException ex) {
            System.out.println("Please provide valid hotelId  ");
            return 0;
        }
    }

    /**
     * Provides hotel details with holtel id
     *
     * @param hotelId id of the hotel
     * @return Hotel
     */
    Hotel getHotel(int hotelId) {
        Map<Integer, Hotel> map = new HashMap<>();
        map.putAll(hotelDetailsMap);
        return map.get(hotelId);
    }
}


