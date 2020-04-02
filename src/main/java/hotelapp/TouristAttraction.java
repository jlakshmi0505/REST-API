package hotelapp;

public class TouristAttraction {
    private String id;
    private String name;
    private double rating;
    private String address;


    /** Constructor for TouristAttraction
     *
     * @param id id of the attraction
     * @param name name of the attraction
     * @param rating overall rating of the attraction
     * @param address address of the attraction
     */
    public TouristAttraction(String id, String name, double rating, String address) {
        this.id = id;
        this.name=name;
        this.rating=rating;
        this.address=address;
    }

    /** toString() method
     * @return a String representing this TouristAttraction
     */
    @Override
    public String toString() {
        return "TouristAttraction " +
                "id=" + id + "," + System.lineSeparator() +
                "name=" + name + ","+  System.lineSeparator() +
                "rating=" + rating + ","+ System.lineSeparator() +
                "address=" + address + System.lineSeparator();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getRating() {
        return rating;
    }

    public String getAddress() {
        return address;
    }
}
