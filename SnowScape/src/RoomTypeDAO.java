import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoomTypeDAO {

    private Connection getConnection() throws SQLException {
        // Database connection details
        String DB_URL = "jdbc:postgresql://localhost:5432/SnowScape";
        String DB_USER = "postgres";
        String DB_PASSWORD = "mariaeliza";
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    public List<RoomType> getRoomTypesForAccommodation(int accommodationId) {
        List<RoomType> roomTypes = new ArrayList<>();
        String sql = "SELECT rt.roomtypeid, rt.roomtypename, rt.numsinglebeds, rt.numdoublebeds, pr.price " +
                "FROM roomtypes rt " +
                "JOIN pricing_rooms pr ON rt.roomtypeid = pr.roomtypeid " +
                "WHERE pr.accommodationid = ?";

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, accommodationId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    int roomTypeId = resultSet.getInt("roomtypeid");
                    String roomTypeName = resultSet.getString("roomtypename");
                    int numSingleBeds = resultSet.getInt("numsinglebeds");
                    int numDoubleBeds = resultSet.getInt("numdoublebeds");
                    BigDecimal priceBD = resultSet.getBigDecimal("price");
                    Double price = priceBD != null ? priceBD.doubleValue() : null;

                    roomTypes.add(new RoomType(roomTypeId, roomTypeName, numSingleBeds, numDoubleBeds, price));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return roomTypes;
    }
}