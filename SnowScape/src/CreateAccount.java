import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CreateAccount {
    private final String DB_URL = "jdbc:postgresql://localhost:5432/SnowScape";
    private final String DB_USER = "postgres";
    private final String DB_PASSWORD = "mariaeliza";

    public boolean checkUsernameExists(String username) {
        boolean exists = false;
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT * FROM users WHERE username = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, username);

                try (ResultSet resultSet = statement.executeQuery()) {
                    exists = resultSet.next();
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return exists;
    }

    public int createUserAndGetId(String username, String password) {
        int userId = -1; // Default value indicating failure
        if (checkUsernameExists(username)) {
            System.out.println("Username already exists! Please choose another username.");
            // You can display an error message or handle it as needed
        } else {
            try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                String query = "INSERT INTO users (username, password) VALUES (?, ?) RETURNING id";
                try (PreparedStatement statement = connection.prepareStatement(query)) {
                    statement.setString(1, username);
                    statement.setString(2, password);

                    try (ResultSet resultSet = statement.executeQuery()) {
                        if (resultSet.next()) {
                            userId = resultSet.getInt("id");
                            System.out.println("User registered successfully! User ID: " + userId);
                        } else {
                            System.out.println("User registration failed!");
                        }
                    }
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return userId; // Return the ID of the newly created user
    }
}
