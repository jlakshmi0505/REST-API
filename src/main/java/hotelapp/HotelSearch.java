package hotelapp;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

/**
 * The main class for project 1.
 * The main function should take the following 4 command line arguments:
 * -hotels hotelFile -reviews reviewsDirectory
 * <p>
 * and read general information about the hotels from the hotelFile (a JSON file)
 * and read hotel reviews from the json files in reviewsDirectory.
 * The data should be loaded into data structures that allow efficient search.
 * See Readme for details.
 * You are expected to add other classes and methods to this project.
 */
public class HotelSearch {
    private static final String HOTELS_FLAG = "-hotels";

    public static void main(String[] args) {
        // If less than 2 args passed ...
        if (args.length < 2) {
            System.out.println("No correct input file provided");
        } else {
            // populating command line args values into map
            Map<String, String> commandLineArgMap = populateCommandLineArgMap(args);
            if (commandLineArgMap.size() > 0) {
                Scanner scanObj = new Scanner(System.in);
                ThreadSafeHotelData hotelData = new ThreadSafeHotelData();
                HotelDataBuilder builder = new HotelDataBuilder(hotelData, 1);
                builder.processFiles(commandLineArgMap);
                TouristAttractionFinder finder = new TouristAttractionFinder(hotelData);
                finder.fetchAttractions(2);
                finder.processHTMLFiles();
                // Taking user command...
                System.out.println("Enter one of following commands: find <hotelId> or findAttraction <hotelId> or findDescriptions <hotelId> or exit");
                String command = scanObj.nextLine();
                //Iterate till the user presses exit...
                while (!command.equalsIgnoreCase("exit")) {
                    finder.parseUserInput(command);
                    System.out.println("Enter one of following commands: find <hotelId> or findAttraction <hotelId> or findDescriptions <hotelId> or exit");
                    command = scanObj.nextLine();
                }
            }
        }
    }

    /**
     * This method will populate commandLineArgMap with the key as flag and path as value..
     * key should be any of these flag { -hotels or -reviews}
     * @param args command line args value
     * @return map with flags as key and commandLineArg as values
     */
    private static Map<String, String> populateCommandLineArgMap(String[] args) {
        Map<String, String> inputFileDetailsMap = new TreeMap<>();
        // add first flag and its value to map
        if (args[0].equals(HOTELS_FLAG)) {
            inputFileDetailsMap.put(args[0], args[1]);
        } else {
            System.out.println("Please provide correct input flag in commandLineArg");
        }
        return inputFileDetailsMap;
    }
}
