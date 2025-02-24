public class RoomType {
    private final int roomTypeId;
    private final String roomTypeName;
    private final int numSingleBeds;
    private final int numDoubleBeds;
    private final Double price;

    // Constructor
    public RoomType(int roomTypeId, String roomTypeName, int numSingleBeds, int numDoubleBeds, Double price) {
        this.roomTypeId = roomTypeId;
        this.roomTypeName = roomTypeName;
        this.numSingleBeds = numSingleBeds;
        this.numDoubleBeds = numDoubleBeds;
        this.price = price;
    }

    public int getRoomTypeId(){
        return roomTypeId;
    }

    public String getRoomTypeName() {
        return roomTypeName;
    }

    public int getNumDoubleBeds() {
        return numDoubleBeds;
    }

    public int getNumSingleBeds(){
        return numSingleBeds;
    }

    public Double getPrice(){
        return price;
    }
}
