import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SkiCenterDAO {

    private Connection getConnection() throws SQLException {
        String DB_URL = "jdbc:postgresql://localhost:5432/SnowScape";
        String DB_USER = "postgres";
        String DB_PASSWORD = "mariaeliza";
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    public List<SkiCenter> getAllSkiCenters() {
        List<SkiCenter> skiCenters = new ArrayList<>();
        String query = "SELECT * FROM skicenters";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                SkiCenter skiCenter = new SkiCenter(
                        resultSet.getInt("centerid"),
                        resultSet.getString("name"),
                        resultSet.getString("city"),
                        resultSet.getString("county"),
                        resultSet.getString("region"),
                        resultSet.getString("phonenumber"),
                        resultSet.getString("email"),
                        resultSet.getString("operatingtime")
                );
                skiCenters.add(skiCenter);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return skiCenters;
    }

    public List<SkiCenter> getSkiCentersByLocation(String searchColumn, String searchTerm) {
        List<SkiCenter> skiCenters = new ArrayList<>();
        String query = "SELECT * FROM skicenters WHERE " + searchColumn + " LIKE ?";
        searchTerm = "%" + searchTerm + "%"; // Add wildcards for partial matching

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, searchTerm);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    SkiCenter skiCenter = new SkiCenter(
                            resultSet.getInt("centerid"),
                            resultSet.getString("name"),
                            resultSet.getString("city"),
                            resultSet.getString("county"),
                            resultSet.getString("region"),
                            resultSet.getString("phonenumber"),
                            resultSet.getString("email"),
                            resultSet.getString("operatingtime")
                    );
                    skiCenters.add(skiCenter);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return skiCenters;
    }
    public List<SkiCenter> getSkiCentersByName(String name) {
        List<SkiCenter> skiCenters = new ArrayList<>();
        String query = "SELECT * FROM skicenters WHERE name LIKE ?";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, "%" + name + "%"); // Use LIKE for partial matching
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    SkiCenter skiCenter = new SkiCenter(
                            resultSet.getInt("centerid"),
                            resultSet.getString("name"),
                            resultSet.getString("city"),
                            resultSet.getString("county"),
                            resultSet.getString("region"),
                            resultSet.getString("phonenumber"),
                            resultSet.getString("email"),
                            resultSet.getString("operatingtime")
                    );
                    skiCenters.add(skiCenter);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return skiCenters;
    }

    public List<SkiCenter> getSkiCentersByRegion(String region) {
        List<SkiCenter> skiCenters = new ArrayList<>();
        String query = "SELECT * FROM skicenters WHERE region = ?";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, region); // Use exact matching for "Region"
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    SkiCenter skiCenter = new SkiCenter(
                            resultSet.getInt("centerid"),
                            resultSet.getString("name"),
                            resultSet.getString("city"),
                            resultSet.getString("county"),
                            resultSet.getString("region"),
                            resultSet.getString("phonenumber"),
                            resultSet.getString("email"),
                            resultSet.getString("operatingtime")
                    );
                    skiCenters.add(skiCenter);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return skiCenters;
    }
}