package com.company;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class connection {
    //Username and password to access the database.
    private final String username;
    private final String password;
    private final String dbName;
    private Connection conn=null;

    public connection() {
        this.username = "root";
        this.password = "root";
        this.dbName = "cse360project";
    }
    public connection(String username, String password, String dbName) {
        this.username = username;
        this.password = password;
        this.dbName = dbName;
    }

    public Connection getdbconnection()
    {
        try {
            this.conn=DriverManager.getConnection("jdbc:mysql://localhost:3306/" + dbName ,
                    username,
                    password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getDbName() {
        return dbName;
    }
}
