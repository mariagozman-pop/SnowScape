public class Amenity {
    private int amenityId;
    private String amenityName;
    private Double price;
    private boolean isFree;

    // Constructor
    public Amenity(int amenityId, String amenityName, Double price, boolean isFree) {
        this.amenityId = amenityId;
        this.amenityName = amenityName;
        this.price = price;
        this.isFree = isFree;
    }

    public String getAmenityName(){
        return amenityName;
    }

    public Double getPrice(){
        return price;
    }

    public boolean isFree(){
        return isFree;
    }

}
