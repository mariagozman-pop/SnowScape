import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AccommodationDAO {

    private Connection getConnection() throws SQLException {
        // Ideally, these should be stored in a configuration file or environment variables for security reasons.
        String DB_URL = "jdbc:postgresql://localhost:5432/SnowScape";
        String DB_USER = "postgres";
        String DB_PASSWORD = "mariaeliza";
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    public List<Accommodation> getAccommodationsBySkiCenterId(int skiCenterId) {
        List<Accommodation> accommodations = new ArrayList<>();
        String sql = "SELECT * FROM accommodations WHERE centerid = ?";

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, skiCenterId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    int accommodationId = resultSet.getInt("accommodationid");
                    String name = resultSet.getString("name");
                    String category = resultSet.getString("category");
                    double distanceFromSkiCenter = resultSet.getDouble("distancefromskicenter");
                    String website = resultSet.getString("website");
                    String address = resultSet.getString("address");

                    accommodations.add(new Accommodation(accommodationId, name, category, distanceFromSkiCenter, website, address));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // In a production environment, you would want to log this exception, not print it.
        }
        return accommodations;
    }
    // Additional methods for other CRUD operations could be added here
}