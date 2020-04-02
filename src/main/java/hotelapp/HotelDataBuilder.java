package hotelapp;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;


/**
 * Class HotelDataBuilder. Loads hotel info from input files to ThreadSafeHotelData (using multithreading).
 */
public class HotelDataBuilder {
    private static final String HOTELS_FLAG = "-hotels";
    private ThreadSafeHotelData hdata;
    private int numOfThreads;
    private Gson gson = new Gson();


    /**
     * Constructor for class HotelDataBuilder.
     *
     * @param data ThreadSafeHotelData
     */
    public HotelDataBuilder(ThreadSafeHotelData data) {
        this(data, 1);
        this.hdata = data;
    }

    /**
     * Constructor for class HotelDataBuilder that takes ThreadSafeHotelData and
     * the number of threads to create as a parameter.
     *
     * @param data       ThreadSafeHotelData
     * @param numThreads no of Threads
     */
    public HotelDataBuilder(ThreadSafeHotelData data, int numThreads) {
        this.hdata = data;
        this.numOfThreads = numThreads;
    }

    /**
     * Read the json file with information about the hotels and load it into the
     * appropriate data structure(s).
     *
     * @param jsonFilename file name of Hotel json
     */
    public void loadHotelInfo(String jsonFilename) {
        try (JsonReader jsonReader = new JsonReader(new FileReader(jsonFilename))) {
            JsonElement jsonElement = new JsonParser().parse(jsonReader).getAsJsonObject();
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            JsonElement hotelElem = jsonObject.getAsJsonObject().get("sr");
            if (hotelElem.isJsonArray()) {
                JsonArray hotelElemAsJsonArray = hotelElem.getAsJsonArray();
                for (JsonElement ht : hotelElemAsJsonArray) {
                    double lat = 0;
                    double lng = 0;
                    JsonObject r = ht.getAsJsonObject();
                    Hotel hotel = gson.fromJson(r, Hotel.class);
                    if (r.get("ll").isJsonObject()) {
                        lat = r.get("ll").getAsJsonObject().get("lat").getAsDouble();
                        lng = r.get("ll").getAsJsonObject().get("lng").getAsDouble();
                    }
                    hdata.addHotel(hotel.getId(), hotel.getF(), hotel.getCi(), hotel.getPr(), hotel.getAd(), lat, lng);
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found" + jsonFilename);
        } catch (IOException e) {
            System.out.println("IO Exception occurred while loading hotel data");
        }
    }


    /**
     * This method will process hotel flag passed as command line argument
     *
     * @param inputFileDetailsMap map with command line arg details
     */
    void processFiles(Map<String, String> inputFileDetailsMap) {
        for (String inputKey : inputFileDetailsMap.keySet()) {
            if (inputKey.equals(HOTELS_FLAG)) {
                String path = inputFileDetailsMap.get(inputKey);
                loadHotelInfo(path);
            }
        }
    }

    /**
     * Prints all hotel info to the file.
     */
    public void printToFile(Path filename) {
        hdata.printToFile(filename);
    }

}
