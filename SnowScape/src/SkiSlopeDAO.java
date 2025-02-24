import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SkiSlopeDAO {
    private final String DB_URL = "jdbc:postgresql://localhost:5432/SnowScape";
    private final String DB_USER = "postgres";
    private final String DB_PASSWORD = "mariaeliza";

    // Method to retrieve ski slopes by ski center ID
    public List<SkiSlope> getSkiSlopesByCenterId(int centerId) {
        List<SkiSlope> skiSlopes = new ArrayList<>();
        String query = "SELECT * FROM skislopes WHERE centerid = ?";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, centerId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    SkiSlope skiSlope = new SkiSlope(
                            resultSet.getInt("slopeid"),
                            resultSet.getInt("centerid"),
                            resultSet.getString("slopename"),
                            resultSet.getString("difficultylevel"),
                            resultSet.getDouble("length"),
                            resultSet.getString("lift")
                    );
                    skiSlopes.add(skiSlope);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return skiSlopes;
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }
}