package com.example.ibrapp;

import android.os.StrictMode;
import  android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;

public class ConnectionHelper {
    String user, pass, ip, port, database, ConnectionURL;
    Connection connection = null;

    public Connection getConnection(){
        ip = "192.168.1.78";
        port = "3306";
        database = "questionnaire";
        user = "android";
        pass = "into1812";

        try{
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            Class.forName("com.mysql.jdbc.Driver");
            ConnectionURL = "jdbc:mysql://" + ip + ":" + port + "/" + database+
                    "?verifyServerCertificate=false&useSSL=false&requireSSL=false";
            connection = DriverManager.getConnection(ConnectionURL, user, pass);
        } catch (Exception e){
            Log.e("Error", e.getMessage());
        }

        return connection;
    }
}
