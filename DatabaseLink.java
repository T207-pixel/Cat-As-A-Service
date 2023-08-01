package org.example;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class DatabaseLink {
    private  final String jdbcURL;
    private final String username;
    private final String password;
    private Connection connection;

    public DatabaseLink(String jdbcURL, String username, String password) {
        this.jdbcURL = jdbcURL;
        this.username = username;
        this.password = password;
    }
    
    private void createConnection() {
        try{
            connection = DriverManager.getConnection(jdbcURL, username, password);
            System.out.println("Connection is established.");
        } catch (SQLException e){
            System.out.println("Connection to PostgreSQL server is failed.");
            e.printStackTrace();
        }
    }

    private static DatabaseLink dataBaseLinkCreate() {
        DatabaseLink databaseLink = new DatabaseLink(PrivateInformation.getJdbcURL(), PrivateInformation.getUserName(),
                PrivateInformation.getPassword());
        databaseLink.createConnection();
        return databaseLink;
    }

    ////////////////////////////////////////////////---EMBEDDED METHODS---//////////////////////////////////////////////
    private void fillCatsMenu() throws SQLException {
        String sql = "INSERT INTO cats_menu (dishName) VALUES ('milk'), ('fish'), ('meet'), ('martini')," +
                " ('biscuit'), ('porridge'), ('bear'), ('coffee'), ('ret'), ('udon'), ('dumplings')";
        Statement statement = connection.createStatement();
        int rows = statement.executeUpdate(sql);
        if (rows > 0) {
            System.out.println("Entered " + rows + " new rows");
        }
        System.out.println("Connection is disconnected.");
        connection.close();
    }

    private void addRecord(String userName, String dish) throws SQLException  {
        String sql = "INSERT INTO clients_fed (userName, dishName) VALUES ('" + userName + "', " + "'" + dish + "')";
        Statement statement = connection.createStatement();
        int rows = statement.executeUpdate(sql);
        if (rows > 0) {
            System.out.println("Entered " + rows + " new rows");
        }
        System.out.println("Connection is disconnected.");
        connection.close();
    }

    private void addRecordInStrokersList(String username) throws SQLException {
        username = username.replaceAll("^.", "");
        String sql = "INSERT INTO feeders_strokers (userName) VALUES ('" + username + "')";
        Statement statement = connection.createStatement();
        int rows = statement.executeUpdate(sql);
        if (rows > 0) {
            System.out.println("Entered " + rows + " new rows");
        }
        System.out.println("Connection is disconnected.");
        connection.close();
    }

    private Map<Integer, String> selectCatsMenuQuery() throws SQLException {
        String sql = "SELECT * FROM cats_menu";
        Statement statement = connection.createStatement();
        ResultSet result = statement.executeQuery(sql);
        Map<Integer, String> dishesSet = new HashMap<Integer, String>();
        while (result.next()) {
            dishesSet.put(result.getInt("id"), result.getString("dishName"));
        }
        System.out.println("Connection is disconnected.");
        connection.close();
        return dishesSet;
    }

    private String getFeedersRecords() throws SQLException{
        String formedString = "";
        StringBuffer strBuffer = new StringBuffer(formedString);
        String sql = "SELECT * FROM clients_fed";
        Statement statement = connection.createStatement();
        ResultSet result = statement.executeQuery(sql);
        while (result.next()) {
            strBuffer.append("<li>").append(result.getString("userName")).append(" ");
            strBuffer.append(result.getString("dishName")).append("\n").append("</li>");
        }
        connection.close();
        return strBuffer.toString();
    }

    private String getStrokersRecords() throws SQLException{
        String formedString = "";
        StringBuffer strBuffer = new StringBuffer(formedString);
        String sql = "SELECT * FROM feeders_strokers";
        Statement statement = connection.createStatement();
        ResultSet result = statement.executeQuery(sql);
        while (result.next()) {
            strBuffer.append("<li>").append(result.getString("userName")).append(" ");
        }
        connection.close();
        return strBuffer.toString();
    }

    private boolean acceptedOrNotByCat(String feeder) throws SQLException{
        try {
            feeder = feeder.replaceAll("^.", "");
            String sql = "SELECT userName FROM clients_fed WHERE userName = '"+ feeder + "'";
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery(sql);
            if (result.next()) {
                connection.close();
                return true;
            } else {
                connection.close();
                return false;
            }
        } catch (NullPointerException e){
            e.printStackTrace();
            return false;
        }
    }

    ///////////////////////////////////////////////////---OUTER METHODS---//////////////////////////////////////////////
    public static void initCatsMenu() throws SQLException {
        DatabaseLink databaseLink = DatabaseLink.dataBaseLinkCreate();
        databaseLink.fillCatsMenu();
    }

    public static void addRecordInFeedList(String userName, String dish) throws SQLException {
        DatabaseLink databaseLink = DatabaseLink.dataBaseLinkCreate();
        databaseLink.addRecord(userName, dish);
    }

    public static void addRecordInStrokersListOuter(String userName) throws SQLException {
        DatabaseLink databaseLink = DatabaseLink.dataBaseLinkCreate();
        databaseLink.addRecordInStrokersList(userName);
    }

    public static Map<Integer, String> getCatsMenu() throws SQLException {
        DatabaseLink databaseLink = DatabaseLink.dataBaseLinkCreate();
        return databaseLink.selectCatsMenuQuery();
    }

    public static String generateFeedersList() throws SQLException {
        DatabaseLink databaseLink = DatabaseLink.dataBaseLinkCreate();
        return databaseLink.getFeedersRecords();
    }

    public static String generateStrokersList() throws SQLException {
        DatabaseLink databaseLink = DatabaseLink.dataBaseLinkCreate();
        return databaseLink.getStrokersRecords();
    }

    public static boolean catsPermission(String userName) throws SQLException {
        DatabaseLink databaseLink = DatabaseLink.dataBaseLinkCreate();
        if (userName != null)
            return databaseLink.acceptedOrNotByCat(userName);
        else return false;
    }

}
