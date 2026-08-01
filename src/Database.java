import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

 public class Database {
    private static final String DB_NAME = "expense_income_db";
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/" + DB_NAME;
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public static void main(String[] args) {
        System.out.println("Connection request...");
        getconnection();
    }

    public static Connection getconnection() {
        Connection connection = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
            System.out.println("Connected to database.");
        } catch (ClassNotFoundException e) {
            System.out.println("MySQL JDBC Driver not found.");
        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
        }

        return connection;
    }
}