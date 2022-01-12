/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aeropop.aeropop;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author TODO
 */
public class Queries {
    
     // Realiza una Query y guarda los datos en un ResultSet
    public static ResultSet cargaDatos(ConnectionDB bbdd, String query) {
        ResultSet rs;
        try {
            Statement stmt = bbdd.getConn().createStatement();
            rs = stmt.executeQuery(query);
        } catch (SQLException e) {
            System.out.println(e.getLocalizedMessage());
            return null;
        }
        return rs;
    }
    
    /**
    * Muestra los datos de la tabla vuelos
    */
    public static void mostrarVuelos(ConnectionDB bbdd) {
        ResultSet rs = cargaDatos(bbdd, "SELECT * FROM vuelos");
        System.out.println("CODIGO      FECHA-HORA      PROCEDENCIA  DESTINO");
        try {
            while (rs.next()) {
                String codigoVuelo = rs.getString("CODIGO_VUELO");
                String fecha = rs.getString("HORA_SALIDA");
                String procedencia = rs.getString("PROCEDENCIA");
                String destino = rs.getString("DESTINO");
                System.out.println(codigoVuelo + "   " + fecha + "   " + procedencia + "    " + destino);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        System.out.println("****************************************************");
    }
    
     /**
     * Muestra los datos de la tabla pasajeros
     */
    public static void mostrarPasajeros(ConnectionDB bbdd) {
        ResultSet rs = cargaDatos(bbdd, "SELECT * FROM pasajeros");
        System.out.println("PASAJERO  VUELO  PLAZA  FUMADOR");
        try {
            while (rs.next()) {
                String codigo = Integer.toString(rs.getInt("ID"));
                String codigoVuelo = rs.getString("CODIGO_VUELO");
                String plaza = rs.getString("TIPO_PLAZA");
                boolean fumador = rs.getBoolean("fumador");
                System.out.println(codigo + "   " + codigoVuelo + "   " + plaza + "   " + fumador);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        System.out.println("****************************************************");
    }
    
    public static void mostrarPasajerosVuelo(ConnectionDB bbdd, String codigo) {
        ResultSet rs = cargaDatos(bbdd, "SELECT * FROM pasajeros WHERE  codigo_vuelo='" + codigo + "'");
        System.out.println("PASAJERO  VUELO  PLAZA  FUMADOR");
        try {
            while (rs.next()) {
                String numero = Integer.toString(rs.getInt("ID"));
                String codigoVuelo = rs.getString("CODIGO_VUELO");
                String plaza = rs.getString("TIPO_PLAZA");
                boolean fumador = rs.getBoolean("fumador");
                System.out.println(numero + "   " + codigoVuelo + "   " + plaza + "   " + fumador);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        System.out.println("****************************************************");
    }

    /**
     * Inserta un vuelo en la BBDD
     */
    public static void insertarVuelo(ConnectionDB bbdd, String codigo, Date hora, String destino, String procede, int pF, int pNof, int pT, int pP) {
        Timestamp timestamp = new Timestamp(hora.getTime());
        String insertDatosQL = "INSERT INTO vuelos VALUES "
                + "(?,?,?,?,?,?,?,?)";
        try {
            // preparedStatement = dbConnection.prepareStatement(insertTableSQL);
            PreparedStatement stmt = bbdd.getConn().prepareStatement(insertDatosQL);
            stmt.setString(1, codigo);
            stmt.setTimestamp(2,timestamp);
            stmt.setString(3, destino);
            stmt.setString(4, procede);            
            stmt.setInt(5, pF);
            stmt.setInt(6, pNof);
            stmt.setInt(7, pT);
            stmt.setInt(8, pP);
            stmt.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(ConnectionDB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
