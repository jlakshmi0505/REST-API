package hotelapp;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Class ThreadSafeHotelData - extends class HotelData (rename your class from project 1 as needed).
 * Thread-safe, uses ReentrantReadWriteLock to synchronize access to all data structures.
 */
public class ThreadSafeHotelData extends HotelData {
	private ReentrantReadWriteLock lock;


	/**
	 * Constructor for ThreadsafeHotelData which initializes the lock
	 */
	public ThreadSafeHotelData() {
		super();
		this.lock = new ReentrantReadWriteLock();
	}

	/**
	 * Overrides addHotel method from HotelData class to make it thread-safe; uses the lock.
	 * Create a Hotel given the parameters, and add it to the appropriate data
	 * structure(s).
	 * 
	 * @param hotelId
	 *            - the id of the hotel
	 * @param hotelName
	 *            - the name of the hotel
	 * @param city
	 *            - the city where the hotel is located
	 * @param state
	 *            - the state where the hotel is located.
	 * @param streetAddress
	 *            - the building number and the street
	 * @param lat latitude
	 * @param lon longitude
	 */

	public void addHotel(String hotelId, String hotelName, String city, String state, String streetAddress, double lat,
						 double lon) {
		try {
			lock.writeLock().lock();
			super.addHotel(hotelId, hotelName, city, state, streetAddress, lat, lon);
		} finally {
			lock.writeLock().unlock();
		}
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
	public void addTouristAttraction(String hotelId, String id, String name, double rating, String address) {
		try {
			lock.writeLock().lock();
			super.addTouristAttraction(hotelId, id, name, rating, address);
		} finally {
			lock.writeLock().unlock();
		}
	}


	/**
	 * This method will add description details into the descriptionsMap map
	 *
	 * @param hotelId hotel id
	 * @param descMap map with property and area desc
	 */
	public void addDescriptions(String hotelId, Map<String, String> descMap) {
		try {
			lock.writeLock().lock();
			super.addDescriptions(hotelId, descMap);
		} finally {
			lock.writeLock().unlock();
		}
	}


	/**
	 * Overrides a method of the parent class to make it thread-safe.
	 * Return an alphabetized list of the ids of all hotels
	 * @return List Of Hotel Ids
	 */
	public List<String> getHotels() {
		try {
			lock.readLock().lock();
			return super.getHotels();
		} finally {
			lock.readLock().unlock();
		}
	}

	/**
	 * This method will return hotel details if we provide hotel id
	 *
	 * @param id hotel id
	 */
	public void findHotelById(String id) {
		super.findHotelById(id);
	}


	/**
	 * This method will retrieve attraction of particular hotel id
	 *
	 * @param id hotel id
	 */
	public void findAttractionById(String id) {
		super.findAttractionById(id);
	}

	/**
	 * This method will retrieve description of particular hotel id
	 *
	 * @param id hotel id
	 */
	public void findDescriptionById(String id) {
	super.findDescriptionById(id);
	}
}
