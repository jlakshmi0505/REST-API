package hotelapp;

/**
 *  Hotel class that represents a hotel that has id,address,hotelName
 */
public class Hotel implements Comparable<Hotel> {
    private String f;
    private String id;
    private double lat;
    private double lng;
    private String ad;
    private String ci;
    private String pr;

    public Hotel(String id, String f, String ci, String pr, String ad, double lat, double lng) {
        this.f = f;
        this.id = id;
        this.ad = ad;
        this.ci = ci;
        this.pr = pr;
        this.lat = lat;
        this.lng = lng;
    }

    public String getAd() {
        return ad;
    }

    String getCi() {
        return ci;
    }

    String getPr() {
        return pr;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    @Override
    public String toString() {
        return System.lineSeparator()  +
                "HotelName=" + f + System.lineSeparator() +
                "HotelId=" + id + System.lineSeparator() +
                "Latitude=" + lat + System.lineSeparator() +
                "Longitude=" + lng + System.lineSeparator() +
                "Street=" + ad + System.lineSeparator() +
                "City=" + ci + System.lineSeparator() +
                "State=" + pr  + System.lineSeparator();
    }

    public String getF() {
        return f;
    }

    public String getId() {
        return id;
    }

    /**
     * It will compare hotel ids
     * @param o Other Hotel Obj
     * @return positive if id of hotel is more that other hotel id
     */
    @Override
    public int compareTo(Hotel o) {
        return (id.compareTo(o.id));
    }
}


