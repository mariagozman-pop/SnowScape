public class SkiSlope {
    private final int slopeId;
    private final int centerId; // The ID of the ski center associated with the slope
    private final String slopeName;
    private final String difficultyLevel;
    private final double length; // Length of the slope in meters or other appropriate units
    private final String lift; // Indicates whether the slope has a lift or not

    public SkiSlope(int slopeId, int centerId, String slopeName, String difficultyLevel, double length, String lift) {
        this.slopeId = slopeId;
        this.centerId = centerId;
        this.slopeName = slopeName;
        this.difficultyLevel = difficultyLevel;
        this.length = length;
        this.lift = lift;
    }

    public int getSlopeId(){ return slopeId; }
    public int getCenterId(){ return centerId; }
    public String getName() { return slopeName; }
    public String getDifficulty() { return difficultyLevel; }
    public Double getLength() { return length; }
    public String getLift() { return lift; }

    @Override
    public String toString() {
        return "Ski Slope ID: " + slopeId +
                "\nCenter ID: " + centerId +
                "\nSlope Name: " + slopeName +
                "\nDifficulty Level: " + difficultyLevel +
                "\nLength: " + length +
                "\nHas Lift: " + lift;
    }
}
