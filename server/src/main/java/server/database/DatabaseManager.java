package server.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {
    public static String URL;
    public static String USER;
    public static String PASSWORD;

    public DatabaseManager(String url, String user, String password)
    {
        URL = url;
        USER = user;
        PASSWORD = password;
    }

    public static Connection getConnection()
    {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        }
        catch (SQLException ex)
        {
            System.out.println(ex.toString());
            return null;
        }
    }
}
