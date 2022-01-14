/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aeropop.aeropop;

/**
 *
 * @author TODO
 */
import java.sql.*;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConnectionDB {

    private static Connection conn;
    private static ConnectionDB instance;

    /**
     * Usamos patrón Singleton para garantizar una única conexión a la BBDD.
     *
     * @return Objeto Singleton de conexión.
     */
    public static ConnectionDB getInstance() {
        if (instance == null) {
            instance = new ConnectionDB();
        }
        return instance;
    }

    /**
     * Devuelve la conexión actual.
     * @return conexión actual
     */
    public Connection getConn() {
        return conn;
    }
    /**
     * Cierra la conexión con la BBDD.
     */
    public void closeConn() {
        try {
            conn.close();
        } catch (SQLException sqle) {
            sqle.getLocalizedMessage();
        }

    }

    /**
     * Instancia el objeto creando una conexión con la BBDD
     */
    private ConnectionDB() {
        try {
            //Obtenemos el Driver
            Class.forName("org.postgresql.Driver");
            String dbUrl = "jdbc:postgresql://easybyte.club:2224/AeroPOP";
            Properties props = new Properties();
            props.setProperty("user", "jdbcAeroPOP");
            props.setProperty("password", "jdbc@AeroPOP2021*");
            props.setProperty("ssl", "false");
            conn = DriverManager.getConnection(dbUrl, props);

            if (conn != null) {
                System.out.println(">>> La conexión a la Base de Datos se ha creado con éxito <<<");
            }

        } catch (SQLException e) {
            System.out.println(e.getLocalizedMessage());
            System.out.println("ERROR: No se puede conectar con la base de datos");
            System.exit(1);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ConnectionDB.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(1);
        }

    }

}
