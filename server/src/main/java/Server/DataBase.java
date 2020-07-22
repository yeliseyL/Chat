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
}

