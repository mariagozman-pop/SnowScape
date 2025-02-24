public class SkiCenter {
    private int centerId;
    private String name;
    private String city;
    private String county;
    private String region;
    private String phoneNumber;
    private String email;
    private String operatingTime;

    // Constructor
    public SkiCenter(int centerId, String name, String city, String county, String region, String phoneNumber, String email, String operatingTime) {
        this.centerId = centerId;
        this.name = name;
        this.city = city;
        this.county = county;
        this.region = region;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.operatingTime = operatingTime;
    }

    public int getCenterId() { return centerId; }
    public String getName() { return name; }
    public String getCity() { return city; }
    public String getCounty() { return county; }
    public String getRegion() { return region; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getEmail() { return email; }
    public String getOperatingTime() { return operatingTime; }

    @Override
    public String toString() {
        return "SkiCenter{" +
                "centerId=" + centerId +
                ", name='" + name + '\'' +
                ", city='" + city + '\'' +
                ", county='" + county + '\'' +
                ", region='" + region + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", email='" + email + '\'' +
                ", operatingTime='" + operatingTime + '\'' +
                '}';
    }
}