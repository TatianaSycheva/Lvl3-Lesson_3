package com.example.server;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class RegServer {
    private static Connection connection;
    private static Statement statement;
    private static PreparedStatement preparedStatement;


    public static void connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:users.db");
            statement = connection.createStatement();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void registration(String login, String password, String nickname) {
        try (PreparedStatement statement = connection.prepareStatement("INSERT INTO users (login, password, nickname) " +
                "VALUES (?,?, ?);")) {
            statement.setObject(1, login);
            statement.setObject(2, password);
            statement.setObject(3, nickname);
            statement.execute();
        } catch (SQLException e) {
            System.err.println("connection");
            e.printStackTrace();
        }
    }

    public static boolean uniqueLogin(String login) {
        ArrayList<String> logins = new ArrayList<>();
        try {
            ResultSet resultSet = statement.executeQuery("SELECT login FROM users;");
            while (resultSet.next()) {
                logins.add(resultSet.getString("login"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return logins.contains(login);
    }

    public static boolean uniqueNick(String nickname) {
        ArrayList<String> nicknames = new ArrayList<>();
        try {
            ResultSet resultSet = statement.executeQuery("SELECT nickname FROM users;");
            while (resultSet.next()) {
                nicknames.add(resultSet.getString("nickname"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return nicknames.contains(nickname);
    }

    public static void disconnect() {

        try {
            if (statement != null) {
                statement.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }


        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
