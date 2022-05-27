package com.example.server;

import java.sql.*;

public class AuthServer {
    private static Connection connection;
    private static Statement statement;

    public static void connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            try {
                connection = DriverManager.getConnection("jdbc:sqlite:users.db");

                statement = connection.createStatement();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static String getNickByLoginPass(String login, String password) {
        String sql = String.format("SELECT nickname FROM users WHERE login = '%s' AND password = '%s';", login, password);
        try {
            ResultSet resultSet = statement.executeQuery(sql);

            if(resultSet.next()) {
                return resultSet.getString("nickname");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void disconnect() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
