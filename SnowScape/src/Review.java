public class Review {
    private int userId;
    private int skiCenterId;
    private int accommodationId;
    private String reviewText;
    private int rating;
    private Integer roomComfortRating;
    private Integer locationConvenienceRating;
    private Integer staffServiceRating;
    private Integer slopeConditionsRating;
    private Integer facilitiesQualityRating;
    private Integer sceneryViewsRating;
    private ReviewType reviewType;
    private int reviewId;

    public Review(int reviewId, int userId, String reviewText, int rating, ReviewType reviewType, int skiCenterId, int accommodationId, Integer roomComfortRating, Integer locationConvenienceRating, Integer staffServiceRating, Integer slopeConditionsRating, Integer facilitiesQualityRating, Integer sceneryViewsRating) {
        this.reviewId = reviewId;
        this.userId = userId;
        this.reviewText = reviewText;
        this.rating = rating;
        this.reviewType = reviewType;

        // Initialize ratings based on the review type
        if (reviewType == ReviewType.ACCOMMODATION) {
            this.accommodationId = accommodationId;
            this.skiCenterId = 0;
            this.roomComfortRating = roomComfortRating;
            this.locationConvenienceRating = locationConvenienceRating;
            this.staffServiceRating = staffServiceRating;
            this.slopeConditionsRating = null;
            this.facilitiesQualityRating = null;
            this.sceneryViewsRating = null;
        } else if (reviewType == ReviewType.SKI_CENTER) {
            this.skiCenterId = skiCenterId;
            this.accommodationId = 0;
            this.roomComfortRating = null;
            this.locationConvenienceRating = null;
            this.staffServiceRating = null;
            this.slopeConditionsRating = slopeConditionsRating;
            this.facilitiesQualityRating = facilitiesQualityRating;
            this.sceneryViewsRating = sceneryViewsRating;
        } else {
            // Default values for other types of reviews
            this.accommodationId = 0;
            this.skiCenterId = 0;
            this.roomComfortRating = null;
            this.locationConvenienceRating = null;
            this.staffServiceRating = null;
            this.slopeConditionsRating = null;
            this.facilitiesQualityRating = null;
            this.sceneryViewsRating = null;
        }
    }

    public int getReviewId() {
        return reviewId;
    }

    public void setReviewId(int reviewId) {
        this.reviewId = reviewId;
    }

    public int getAccommodationId() {
        return accommodationId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getSkiCenterId() {
        return skiCenterId;
    }

    public void setSkiCenterId(int skiCenterId) {
        this.skiCenterId = skiCenterId;
    }

    public String getReviewText() {
        return reviewText;
    }

    public void setReviewText(String reviewText) {
        this.reviewText = reviewText;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public Integer getRoomComfortRating() {
        return roomComfortRating;
    }

    public void setRoomComfortRating(Integer roomComfortRating) {
        this.roomComfortRating = roomComfortRating;
    }

    public Integer getLocationConvenienceRating() {
        return locationConvenienceRating;
    }

    public void setLocationConvenienceRating(Integer locationConvenienceRating) {
        this.locationConvenienceRating = locationConvenienceRating;
    }

    public Integer getStaffServiceRating() {
        return staffServiceRating;
    }

    public void setStaffServiceRating(Integer staffServiceRating) {
        this.staffServiceRating = staffServiceRating;
    }

    public Integer getSlopeConditionsRating() {
        return slopeConditionsRating;
    }

    public void setSlopeConditionsRating(Integer slopeConditionsRating) {
        this.slopeConditionsRating = slopeConditionsRating;
    }

    public Integer getFacilitiesQualityRating() {
        return facilitiesQualityRating;
    }

    public void setFacilitiesQualityRating(Integer facilitiesQualityRating) {
        this.facilitiesQualityRating = facilitiesQualityRating;
    }

    public Integer getSceneryViewsRating() {
        return sceneryViewsRating;
    }

    public void setSceneryViewsRating(Integer sceneryViewsRating) {
        this.sceneryViewsRating = sceneryViewsRating;
    }

    public ReviewType getReviewType() {
        return reviewType;
    }

    public void setReviewType(ReviewType reviewType) {
        this.reviewType = reviewType;
    }

    public boolean isAccommodationReview() {
        return reviewType == ReviewType.ACCOMMODATION;
    }

    public boolean isSkiCenterReview() {
        return reviewType == ReviewType.SKI_CENTER;
    }

    public void setAccommodationId(int entityId) {
        this.accommodationId = entityId;
    }

}
