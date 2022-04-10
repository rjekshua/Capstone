package helper;
import java.sql.Connection;
import java.sql.DriverManager;
// Need to add mySQL DRIVER to library for the connection to establish
/** The methods provides all the information to establish and close the database connection to the mysql server.
 *
 * */
public abstract class JDBC {
    private static final String protocol = "jdbc";
    private static final String vendor = ":mysql:";
    private static final String location = "//localhost/";
    private static final String databaseName = "icms";
    private static final String jdbcUrl = protocol + vendor + location + databaseName + "?connectionTimeZone = SERVER"; // LOCAL
    private static final String driver = "com.mysql.cj.jdbc.Driver"; // Driver reference
    private static final String userName = "root"; // Username "sqlUser:
    public static Connection conn;
    private static String password = "Autochem1!"; // Password "Passw0rd!"
    public static Connection connection;  // Connection Interface


    /** Creates an open connection to the mysql server, where the user can retrieve and send data.
     *
     * @return conn
     * */
    public static Connection openConnection()
    {
        try {
            Class.forName(driver); // Locate Driver
            conn = DriverManager.getConnection(jdbcUrl, userName, password); // Reference Connection object
            System.out.println("Connection successful!");
            return conn;
        }
        catch(Exception e)
        {
            System.out.println("Error:" + e.getMessage());
        }
        return null;
    }

    /** The function ends the connection to the mysql server.
     *
     * */
    public static void closeConnection() {
        try {
            connection.close();
            System.out.println("Connection closed!");
        }
        catch(Exception e)
        {
            System.out.println("Error:" + e.getMessage());
        }
    }
}
