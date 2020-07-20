package Server;

import java.sql.*;
import java.util.ArrayList;

public class DataBase {

    private Connection connection;
    private Statement stmt;
    private PreparedStatement psAddUser;
    private PreparedStatement psGetUser;
    private PreparedStatement psChangeNickname;

    private void connect() throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection("jdbc:sqlite:users.db");
        stmt = connection.createStatement();
    }

    private void disconnect() {
        try {
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void prepareAllStatement() throws SQLException {
        psAddUser = connection.prepareStatement("INSERT INTO userInfo (username, login, password) VALUES (?, ?, ?);");
        psGetUser = connection.prepareStatement("SELECT username FROM userInfo WHERE login = ? AND password = ?;");
        psChangeNickname = connection.prepareStatement("UPDATE userInfo SET username = ? WHERE login = ?;");
    }

    public void addUser(String username, String login, String password) {
        try {
            connect();
            prepareAllStatement();
            psAddUser.setString(1, username);
            psAddUser.setString(2, login);
            psAddUser.setString(3, password);
            psAddUser.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            disconnect();
        }
    }

    public void changeNickname(String login, String newNickname) {
        try {
            connect();
            prepareAllStatement();
            psChangeNickname.setString(1, newNickname);
            psChangeNickname.setString(2, login);
            psChangeNickname.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            disconnect();
        }
    }

    public ArrayList<String[]> getUsers() throws SQLException {
        ArrayList<String[]> users = new ArrayList<String[]>();
        ResultSet rs = stmt.executeQuery("SELECT username, login, password FROM userInfo;");

        while (rs.next()) {
            users.add(new String[3]);
            users.get(rs.getInt("id"))[0] = rs.getString("login");
            users.get(rs.getInt("id"))[1] = rs.getString("password");
            users.get(rs.getInt("id"))[2] = rs.getString("username");
        }
        rs.close();
        return users;
    }

    public String getNickname(String login, String password) {
        String nickname = null;
        try {
            connect();
            prepareAllStatement();
            psGetUser.setString(1, login);
            psGetUser.setString(2, password);
            ResultSet rs = psGetUser.executeQuery();
            if (rs.next()) {
                nickname = rs.getString("username");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            disconnect();
        }
        return nickname;
    }

//    private static void rollback() throws SQLException {
//        stmt.executeUpdate("INSERT INTO students (name, score) VALUES ('Bob1', 75);");
//        Savepoint sp1 = connection.setSavepoint();
//        stmt.executeUpdate("INSERT INTO students (name, score) VALUES ('Bob2', 75);");
//        stmt.executeUpdate("INSERT INTO students (name, score) VALUES ('Bob6', 75);");
//        stmt.executeUpdate("INSERT INTO students (name, score) VALUES ('Bob4', 75);");
//        connection.rollback(sp1);
//        stmt.executeUpdate("INSERT INTO students (name, score) VALUES ('Bob3', 75);");
//        connection.setAutoCommit(true);
//    }
//
//    public static void batchFillTable() throws SQLException {
//        long start = System.currentTimeMillis();
//        connection.setAutoCommit(false);
//        for (int i = 1; i <= 2000; i++) {
//            psInsert.setString(1, "Bob" + i);
//            psInsert.setInt(2, (i * 5) % 100);
//            psInsert.addBatch();
//        }
//        psInsert.executeBatch();
//        connection.setAutoCommit(true);
//
//        long end = System.currentTimeMillis();
//        System.out.printf("time: %d ms", end - start);
//    }
//
//    public static void fillTable() throws SQLException {
//        long start = System.currentTimeMillis();
//        connection.setAutoCommit(false);
//        for (int i = 1; i <= 2000; i++) {
//            psInsert.setString(1, "Bob" + i);
//            psInsert.setInt(2, (i * 5) % 100);
//            psInsert.executeUpdate();
//        }
//        connection.setAutoCommit(true);
////        connection.commit();
//
//        long end = System.currentTimeMillis();
//        System.out.printf("time: %d ms", end - start);
//    }
//
//    //  CRUD create read update delete
//    private static void selectEx() throws SQLException {
//        ResultSet rs = stmt.executeQuery("SELECT name,score FROM students \n" +
//                "WHERE score >50;");
//
//        while (rs.next()) {
//            System.out.println(rs.getString("name") + " " + rs.getInt("score"));
//        }
//        rs.close();
//    }
//
//    private static void clearTable() throws SQLException {
//        stmt.executeUpdate("DELETE FROM students;");
//    }
//
//    private static void deleteEx() throws SQLException {
//        stmt.executeUpdate("DELETE FROM students WHERE id = 2;");
//    }
//
//    private static void updateEx() throws SQLException {
//        stmt.executeUpdate("UPDATE students SET score = 60 WHERE score = 100;");
//    }
//
//    private static void insertEx() throws SQLException {
//        stmt.executeUpdate("INSERT INTO students (name, score) VALUES ('Bob3', 75);");
//    }


}

