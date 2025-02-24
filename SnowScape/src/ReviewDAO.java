import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ReviewDAO {
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/SnowScape";
    private static final String DB_USER = "postgres";
    private static final String DB_PASSWORD = "mariaeliza";
    private Connection connection;

    // Constructor to initialize the connection
    public ReviewDAO() {
        try {
            connection = getConnection();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to connect to the database", e);
        }
    }

    // Method to establish a database connection
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    // Method to add a general review to the database
    public boolean addReview(Review review) {
        String sql = "INSERT INTO Reviews (user_id, review_text, rating) VALUES (?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, review.getUserId());
            statement.setString(2, review.getReviewText());
            statement.setInt(3, review.getRating());

            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to add the review to the database", e);
        }
    }

    // Method to add an accommodation review for ski centers to the database
    public boolean addAccommodationReview(Review review) {
        // Insert the review into the general Reviews table first
        if (!addReview(review)) {
            return false;
        }

        // Now, insert the accommodation-specific details
        String sql = "INSERT INTO Accommodation_Reviews (review_id, accommodation_id, room_comfort_rating, location_convenience_rating, staff_service_rating) " +
                "VALUES (currval('reviews_review_id_seq'), ?, ?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, review.getAccommodationId());

            // Check if the fields are null before setting them in the statement
            if (review.getRoomComfortRating() != null) {
                statement.setInt(2, review.getRoomComfortRating());
            } else {
                statement.setNull(2, Types.INTEGER);
            }

            if (review.getLocationConvenienceRating() != null) {
                statement.setInt(3, review.getLocationConvenienceRating());
            } else {
                statement.setNull(3, Types.INTEGER);
            }

            if (review.getStaffServiceRating() != null) {
                statement.setInt(4, review.getStaffServiceRating());
            } else {
                statement.setNull(4, Types.INTEGER);
            }

            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to add the accommodation review to the database", e);
        }
    }

    public boolean addSkiCenterReview(Review review) {
        if (!addReview(review)) {
            return false;
        }

        // Now, insert the ski center-specific details
        String sql = "INSERT INTO Ski_Center_Reviews (review_id, ski_center_id, slope_conditions_rating, facilities_quality_rating, scenery_views_rating) " +
                "VALUES (currval('reviews_review_id_seq'), ?, ?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, review.getSkiCenterId());

            // Check if the fields are null before setting them in the statement
            if (review.getSlopeConditionsRating() != null) {
                statement.setInt(2, review.getSlopeConditionsRating());
            } else {
                statement.setNull(2, Types.INTEGER);
            }

            if (review.getFacilitiesQualityRating() != null) {
                statement.setInt(3, review.getFacilitiesQualityRating());
            } else {
                statement.setNull(3, Types.INTEGER);
            }

            if (review.getSceneryViewsRating() != null) {
                statement.setInt(4, review.getSceneryViewsRating());
            } else {
                statement.setNull(4, Types.INTEGER);
            }

            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to add the ski center review to the database", e);
        }
    }

    public List<Review> getReviewsByEntityId(ReviewType entityType, int entityId) {
        List<Review> reviews = new ArrayList<>();

        String sql;
        if (entityType == ReviewType.SKI_CENTER) {
            sql = "SELECT Reviews.*, Ski_Center_Reviews.slope_conditions_rating, Ski_Center_Reviews.facilities_quality_rating, Ski_Center_Reviews.scenery_views_rating FROM Reviews JOIN Ski_Center_Reviews ON Reviews.review_id = Ski_Center_Reviews.review_id WHERE ski_center_id = ?";
        } else if (entityType == ReviewType.ACCOMMODATION) {
            sql = "SELECT Reviews.*, Accommodation_Reviews.room_comfort_rating, Accommodation_Reviews.location_convenience_rating, Accommodation_Reviews.staff_service_rating FROM Reviews JOIN Accommodation_Reviews ON Reviews.review_id = Accommodation_Reviews.review_id WHERE accommodation_id = ?";
        } else {
            throw new IllegalArgumentException("Invalid review type");
        }

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, entityId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Review review = new Review(0,
                            resultSet.getInt("user_id"),
                            resultSet.getString("review_text"),
                            resultSet.getInt("rating"),
                            entityType,
                            entityType == ReviewType.SKI_CENTER ? entityId : 0,
                            entityType == ReviewType.ACCOMMODATION ? entityId : 0,
                            entityType == ReviewType.ACCOMMODATION ? resultSet.getObject("room_comfort_rating", Integer.class) : null,
                            entityType == ReviewType.ACCOMMODATION ? resultSet.getObject("location_convenience_rating", Integer.class) : null,
                            entityType == ReviewType.ACCOMMODATION ? resultSet.getObject("staff_service_rating", Integer.class) : null,
                            entityType == ReviewType.SKI_CENTER ? resultSet.getObject("slope_conditions_rating", Integer.class) : null,
                            entityType == ReviewType.SKI_CENTER ? resultSet.getObject("facilities_quality_rating", Integer.class) : null,
                            entityType == ReviewType.SKI_CENTER ? resultSet.getObject("scenery_views_rating", Integer.class) : null
                    );
                    reviews.add(review);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to retrieve reviews from the database", e);
        }

        return reviews;
    }

    public String getUsernameById(int userId) {
        String sql = "SELECT username FROM users WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("username");
                } else {
                    return null; // or throw an exception, depending on how you want to handle this case
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to retrieve username from the database", e);
        }
    }

    public Map<Review, String> getReviewsByUserId(int userId) {
        Map<Review, String> reviewsWithNames = new HashMap<>();

        String sql = "SELECT " +
                "    r.review_id, r.review_text, r.rating, " +
                "    scr.slope_conditions_rating, scr.facilities_quality_rating, scr.scenery_views_rating, scr.ski_center_id, " +
                "    sc.name AS ski_center_name, " +
                "    ar.room_comfort_rating, ar.location_convenience_rating, ar.staff_service_rating, ar.accommodation_id, " +
                "    a.name AS accommodation_name " +
                "FROM " +
                "    reviews r " +
                "LEFT JOIN " +
                "    ski_center_reviews scr ON r.review_id = scr.review_id " +
                "LEFT JOIN " +
                "    skicenters sc ON scr.ski_center_id = sc.centerid " +
                "LEFT JOIN " +
                "    accommodation_reviews ar ON r.review_id = ar.review_id " +
                "LEFT JOIN " +
                "    accommodations a ON ar.accommodation_id = a.accommodationid " +
                "WHERE " +
                "    r.user_id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, userId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    int reviewId = resultSet.getInt("review_id");
                    String reviewText = resultSet.getString("review_text");
                    int rating = resultSet.getInt("rating");

                    Integer skiCenterId = resultSet.getInt("ski_center_id");
                    if (resultSet.wasNull()) skiCenterId = null;

                    Integer accommodationId = resultSet.getInt("accommodation_id");
                    if (resultSet.wasNull()) accommodationId = null;

                    // Initialize ratings
                    Integer slopeConditionsRating = null;
                    Integer facilitiesQualityRating = null;
                    Integer sceneryViewsRating = null;
                    Integer roomComfortRating = null;
                    Integer locationConvenienceRating = null;
                    Integer staffServiceRating = null;

                    // Set ratings based on the type of review
                    if (skiCenterId != null) {
                        slopeConditionsRating = resultSet.getInt("slope_conditions_rating");
                        facilitiesQualityRating = resultSet.getInt("facilities_quality_rating");
                        sceneryViewsRating = resultSet.getInt("scenery_views_rating");
                    } else if (accommodationId != null) {
                        roomComfortRating = resultSet.getInt("room_comfort_rating");
                        locationConvenienceRating = resultSet.getInt("location_convenience_rating");
                        staffServiceRating = resultSet.getInt("staff_service_rating");
                    }

                    Review review = new Review(reviewId,
                            userId,
                            reviewText,
                            rating,
                            ReviewType.UNKNOWN,
                            skiCenterId != null ? skiCenterId : 0,
                            accommodationId != null ? accommodationId : 0,
                            roomComfortRating,
                            locationConvenienceRating,
                            staffServiceRating,
                            slopeConditionsRating,
                            facilitiesQualityRating,
                            sceneryViewsRating
                    );

                    String name = skiCenterId != null ? resultSet.getString("ski_center_name") :
                            accommodationId != null ? resultSet.getString("accommodation_name") : null;

                    reviewsWithNames.put(review, name);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to retrieve reviews from the database", e);
        }

        return reviewsWithNames;
    }

    public boolean deleteReviewById(int reviewId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        boolean success = false;

        try {
            conn = getConnection();
            conn.setAutoCommit(false); // Start transaction

            // Attempt to delete from Ski_Center_Reviews
            String skiCenterSql = "DELETE FROM Ski_Center_Reviews WHERE review_id = ?";
            pstmt = conn.prepareStatement(skiCenterSql);
            pstmt.setInt(1, reviewId);
            pstmt.executeUpdate();
            pstmt.close(); // Close the PreparedStatement

            // Attempt to delete from Accommodation_Reviews
            String accommodationSql = "DELETE FROM Accommodation_Reviews WHERE review_id = ?";
            pstmt = conn.prepareStatement(accommodationSql);
            pstmt.setInt(1, reviewId);
            pstmt.executeUpdate();
            pstmt.close(); // Close the PreparedStatement

            // Delete from the general Reviews table
            String generalSql = "DELETE FROM Reviews WHERE review_id = ?";
            pstmt = conn.prepareStatement(generalSql);
            pstmt.setInt(1, reviewId);
            pstmt.executeUpdate();

            conn.commit(); // Commit transaction
            success = true;
        } catch (SQLException e) {
            // Rollback transaction in case of error
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
        } finally {
            // Close PreparedStatement and Connection
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            if (conn != null) {
                try {
                    conn.setAutoCommit(true); // Reset auto-commit
                    conn.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return success;
    }

    public boolean deleteUserAccount(int userId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        boolean success = false;

        try {
            conn = getConnection();
            conn.setAutoCommit(false); // Start transaction

            // First, delete all reviews by the user
            if (!deleteAllReviewsByUserId(userId)) {
                conn.rollback(); // Rollback if deleting reviews fails
                return false;
            }

            // Then, delete the user account
            String sql = "DELETE FROM Users WHERE id = ?"; // Replace 'Users' with your actual user table name
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                conn.commit(); // Commit transaction
                success = true;
            } else {
                conn.rollback(); // Rollback if no rows are affected
            }
        } catch (SQLException e) {
            // Rollback transaction in case of error
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
        } finally {
            // Close PreparedStatement and Connection
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            if (conn != null) {
                try {
                    conn.setAutoCommit(true); // Reset auto-commit
                    conn.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return success;
    }

    public boolean changeUsername(int userId, String newUsername) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        boolean success = false;

        try {
            conn = getConnection(); // Assuming you have a method to get a database connection
            String sql = "UPDATE Users SET username = ? WHERE id = ?"; // Replace 'Users' with your actual user table name
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, newUsername);
            pstmt.setInt(2, userId);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                success = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return success;
    }

    public boolean deleteAllReviewsByUserId(int userId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        boolean success = false;

        try {
            conn = getConnection();
            conn.setAutoCommit(false); // Start transaction

            // Delete from specific review tables
            String[] specificTables = {"Ski_Center_Reviews", "Accommodation_Reviews"};
            for (String table : specificTables) {
                String sql = "DELETE FROM " + table + " WHERE review_id IN (SELECT review_id FROM Reviews WHERE user_id = ?)";
                pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, userId);
                pstmt.executeUpdate();
                pstmt.close();
            }

            // Delete from the general Reviews table
            String generalSql = "DELETE FROM Reviews WHERE user_id = ?";
            pstmt = conn.prepareStatement(generalSql);
            pstmt.setInt(1, userId);
            pstmt.executeUpdate();

            conn.commit(); // Commit transaction
            success = true;
        } catch (SQLException e) {
            // Rollback transaction in case of error
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
        } finally {
            // Close PreparedStatement and Connection
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return success;
    }

    public ReviewType findReviewTypeByReviewId(int reviewId) {
        try {
            // Check if the review ID exists in SkiCenterReviews
            String skiCenterSql = "SELECT review_id FROM Ski_Center_Reviews WHERE review_id = ?";
            try (PreparedStatement skiCenterStatement = connection.prepareStatement(skiCenterSql)) {
                skiCenterStatement.setInt(1, reviewId);
                try (ResultSet skiCenterResult = skiCenterStatement.executeQuery()) {
                    if (skiCenterResult.next()) {
                        return ReviewType.SKI_CENTER;
                    }
                }
            }

            // Check if the review ID exists in AccommodationReviews
            String accommodationSql = "SELECT review_id FROM Accommodation_Reviews WHERE review_id = ?";
            try (PreparedStatement accommodationStatement = connection.prepareStatement(accommodationSql)) {
                accommodationStatement.setInt(1, reviewId);
                try (ResultSet accommodationResult = accommodationStatement.executeQuery()) {
                    if (accommodationResult.next()) {
                        return ReviewType.ACCOMMODATION;
                    }
                }
            }

            return ReviewType.UNKNOWN;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to determine review type by review ID", e);
        }
    }

    public Review getSkiReview(int userId, int reviewId) {
        // Check if the review ID is associated with a SkiCenter review
        if (findReviewTypeByReviewId(reviewId) == ReviewType.SKI_CENTER) {
            String sql = "SELECT * FROM Ski_Center_Reviews WHERE review_id = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, reviewId);

                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        Review skiReview = new Review(0,
                                userId,
                                resultSet.getString("review_text"),
                                resultSet.getInt("rating"),
                                ReviewType.UNKNOWN,
                                0,
                                0,
                                null,
                                null,
                                null,
                                resultSet.getInt("slope_conditions_rating"),
                                resultSet.getInt("facilities_quality_rating"),
                                resultSet.getInt("scenery_views_rating")
                        );
                        skiReview.setSlopeConditionsRating(resultSet.getInt("slope_conditions_rating"));
                        skiReview.setFacilitiesQualityRating(resultSet.getInt("facilities_quality_rating"));
                        skiReview.setSceneryViewsRating(resultSet.getInt("scenery_views_rating"));
                        // Set other ski center review properties

                        return skiReview;
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException("Failed to retrieve SkiCenter review from the database", e);
            }
        }

        // If the review ID is not associated with a SkiCenter review, return null
        return null;
    }

    public Review getAccommodationReview(int userId, int reviewId) {
        // Check if the review ID is associated with an Accommodation review
        if (findReviewTypeByReviewId(reviewId) == ReviewType.ACCOMMODATION) {
            String sql = "SELECT * FROM Accommodation_Reviews WHERE review_id = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, reviewId);

                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        Review accommodationReview = new Review(0,
                                userId,
                                resultSet.getString("review_text"),
                                resultSet.getInt("rating"),
                                ReviewType.UNKNOWN,
                                0,
                                0,
                                resultSet.getInt("room_comfort_rating"),
                                resultSet.getInt("location_convenience_rating"),
                                resultSet.getInt("staff_service_rating"),
                                null,
                                null,
                                null
                        );
                        accommodationReview.setRoomComfortRating(resultSet.getInt("room_comfort_rating"));
                        accommodationReview.setLocationConvenienceRating(resultSet.getInt("location_convenience_rating"));
                        accommodationReview.setStaffServiceRating(resultSet.getInt("staff_service_rating"));
                        // Set other accommodation review properties

                        return accommodationReview;
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException("Failed to retrieve Accommodation review from the database", e);
            }
        }

        // If the review ID is not associated with an Accommodation review, return null
        return null;
    }

    public String getEntityNameByReviewIdAndType(int reviewId, ReviewType entityType) {
        String entityName = null;
        String tableName = null;

        if (entityType == ReviewType.SKI_CENTER) {
            tableName = "Ski_Centers";
        } else if (entityType == ReviewType.ACCOMMODATION) {
            tableName = "Accommodations";
        }

        if (tableName != null) {
            String sql = "SELECT name FROM " + tableName + " WHERE id = (SELECT ski_center_id FROM Ski_Center_Reviews WHERE review_id = ?)";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, reviewId);

                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        entityName = resultSet.getString("name");
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException("Failed to retrieve entity name from the database", e);
            }
        }

        return entityName;
    }
}