import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AmenityDAO {

    private Connection getConnection() throws SQLException {
        // Database connection details
        String DB_URL = "jdbc:postgresql://localhost:5432/SnowScape";
        String DB_USER = "postgres";
        String DB_PASSWORD = "mariaeliza";
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    public List<Amenity> getAmenitiesForAccommodation(int accommodationId) {
        List<Amenity> amenities = new ArrayList<>();
        String sql = "SELECT a.amenityid, a.amenityname, p.price, p.isfree " +
                "FROM amenities a " +
                "JOIN pricing p ON a.amenityid = p.amenityid " +
                "WHERE p.accommodationid = ?";

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, accommodationId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    int amenityId = resultSet.getInt("amenityid");
                    String amenityName = resultSet.getString("amenityname");
                    Double price = resultSet.getDouble("price");
                    if (resultSet.wasNull()) price = null; // Handle null price
                    boolean isFree = resultSet.getBoolean("isfree");

                    amenities.add(new Amenity(amenityId, amenityName, price, isFree));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return amenities;
    }
    public List<Amenity> getAmenitiesForSkiCenter(int skiCenterId) {
        List<Amenity> amenities = new ArrayList<>();
        String sql = "SELECT a.amenityid, a.amenityname, spa.isfree, spa.price " +
                "FROM amenities a " +
                "JOIN amenities sca ON a.amenityid = sca.amenityid " +
                "JOIN pricing spa ON sca.amenityid = spa.amenityid " +
                "WHERE spa.skicenterid = ?";

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, skiCenterId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    int amenityId = resultSet.getInt("amenityid");
                    String amenityName = resultSet.getString("amenityname");
                    boolean isFree = resultSet.getBoolean("isfree");
                    Double price = resultSet.wasNull() ? null : resultSet.getDouble("price");

                    amenities.add(new Amenity(amenityId, amenityName, price, isFree));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return amenities;
    }
}