public class Accommodation {
    private int accommodationId;
    private String name;
    private String category;
    private double distanceFromSkiCenter;
    private String website;
    private String address;

    public Accommodation(int accommodationId, String name, String category, double distanceFromSkiCenter, String website, String address) {
        this.accommodationId = accommodationId;
        this.name = name;
        this.category = category;
        this.distanceFromSkiCenter = distanceFromSkiCenter;
        this.website = website;
        this.address = address;
    }

    public int getAccommodationId(){
        return accommodationId;
    }
    public String getName(){
        return name;
    }

    public String getCategory(){
        return category;
    }

    public double getDistanceFromSkiCenter() {
        return distanceFromSkiCenter;
    }

    public String getWebsite(){
        return website;
    }

    public String getAddress(){
        return address;
    }
}