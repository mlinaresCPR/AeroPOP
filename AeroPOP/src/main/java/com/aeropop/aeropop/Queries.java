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
import java.util.Scanner;
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
        System.out.println("*******************TABLA VUELOS********************");
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
        System.out.println("******************TABLA PASAJEROS**********************");
        System.out.println("PASAJERO  VUELO  PLAZA  FUMADOR");
        try {
            while (rs.next()) {
                String codigo = Integer.toString(rs.getInt("ID"));
                String codigoVuelo = rs.getString("CODIGO_VUELO");
                String plaza = rs.getString("TIPO_PLAZA");
                String fumador;
                if(rs.getBoolean("fumador")) {
                    fumador = "SI";
                } else {
                    fumador = "NO";
                }
                System.out.println(codigo + "   " + codigoVuelo + "   " + plaza + "   " + fumador);
            }
            System.out.println("****************************************************");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    
    public static void mostrarPasajerosVuelo(ConnectionDB bbdd, String codigo) {
        ResultSet rs = cargaDatos(bbdd, "SELECT * FROM pasajeros WHERE  codigo_vuelo='" + codigo + "'");
        try {
            if(!rs.isBeforeFirst()) {
                System.out.println("ERROR: LA CONSULTA NO HA DEVUELTO RESULTADOS...");
            }else {
                System.out.println("*****************TABLA PASAJEROS********************");
                System.out.println("PASAJERO  VUELO  PLAZA  FUMADOR");
                while (rs.next()) {
                    String numero = Integer.toString(rs.getInt("ID"));
                    String codigoVuelo = rs.getString("CODIGO_VUELO");
                    String plaza = rs.getString("TIPO_PLAZA");
                    String fumador;
                    if(rs.getBoolean("fumador")) {
                        fumador = "SI";
                    } else {
                        fumador = "NO";
                }
                    System.out.println(numero + "   " + codigoVuelo + "   " + plaza + "   " + fumador);
                }
                System.out.println("****************************************************");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Inserta un vuelo en la BBDD
     */
    public static boolean insertarVuelo(ConnectionDB bbdd, String codigo, Date hora, String destino, String procede, int pF, int pNof, int pT, int pP) {
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
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(ConnectionDB.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    /**
     * Este método borrará el vuelo del cual cuyo código se ha introducido siempre que no tenga pasajeros asociados en la base de datos.
     * @param codigo es el código único que presenta cada vuelo de la base de datos.
     */
    public static void borrarVuelo(ConnectionDB bbdd, String codigo){
        PreparedStatement stmt = null;
        try {
            stmt = bbdd.getConn().prepareStatement("DELETE FROM vuelos WHERE codigo_vuelo = ?");
            stmt.setString(1, codigo);
            stmt.executeUpdate();
            System.out.println("El vuelo se ha borrado con éxito!");
            
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            System.out.println("ERROR AL BORRAR...");
        }
    }

    /**
     * Este método muestra los vuelos creados por el usuario, es decir los vuelos sin pasajeros asignados, y posteriormente permite al usuario borrar el que desee.
     * @param input variable Scanner para introducir datos por teclado.
     */
    public static void verVuelosCreados(ConnectionDB bbdd, Scanner input) {
       int vuelosSinPasajeros = 0; //controla que exitan vuelos con pasajeros

       System.out.println("A continuación se muestran los vuelos que han sido creados mediante este sistema y por lo tanto no poseen pasajeros.\n");
       //ResultSet rs = cargaDatos(bbdd,"SELECT v.codigo_vuelo, COUNT(p.codigo_vuelo) FROM vuelos v , pasajeros p WHERE v.codigo_vuelo=p.codigo_vuelo GROUP BY p.codigo_vuelo HAVING COUNT(p.codigo_vuelo)=0");
       ResultSet rs = cargaDatos(bbdd,"SELECT * FROM VUELOS WHERE (SELECT COUNT(*) FROM PASAJEROS "
               + "WHERE PASAJEROS.CODIGO_VUELO = VUELOS.CODIGO_VUELO) = 0");
       try {
            if (rs.next()) {
                System.out.println("CODIGO      FECHA-HORA      PROCEDENCIA  DESTINO");
                do {
                    String codigoVuelo = rs.getString("CODIGO_VUELO");
                    String fecha = rs.getString("HORA_SALIDA");
                    String procedencia = rs.getString("PROCEDENCIA");
                    String destino = rs.getString("DESTINO");
                    vuelosSinPasajeros += 1;
                    System.out.println(codigoVuelo + "   " + fecha + "   " + procedencia + "    " + destino);
                } while (rs.next());
                
                if(vuelosSinPasajeros == 0){
                    System.out.println("Actualmente no hay ningún vuelo sin pasajeros, si quiere continuar cree uno.");
                }else{
                    String codigoVuelo;
                    do{
                        System.out.println("Introduzca el código del vuelo que desee borrar (X para salir): ");
                        codigoVuelo = input.nextLine();
                        if(!codigoVuelo.equalsIgnoreCase("X")) {
                            rs = cargaDatos(bbdd,"SELECT '" + codigoVuelo + "' IN (SELECT CODIGO_VUELO FROM VUELOS) AND "
                                    + "(SELECT COUNT(*) FROM PASAJEROS WHERE PASAJEROS.CODIGO_VUELO = '" 
                                    + codigoVuelo + "') = 0 AS VALIDO");
                            rs.next();
                            if(rs.getBoolean("VALIDO")) {
                                borrarVuelo(bbdd, codigoVuelo);
                                break;
                            } else {
                                System.out.println("VUELO NO VALIDO...");
                            }
                        }
                        
                    }while(!codigoVuelo.equalsIgnoreCase("X"));
                }

            } else {
                System.out.println("Actualmente no existen vuelos en nuestra base de datos.");
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        System.out.println("****************************************************");
    }

    /**
     * Cambia el valor de la columna 'Fumador' de los pasajeros en la base de datos a 'NO' en caso de ser 'SI'.
     * Además los valores de la columna 'pF' (plazas fumadores) en la tabla 'Vuelos' pasa a 0 y su anterior valor se suma a 'pNoF' (plazas no fumadores).
     */
    public static void cambiarFumadores(ConnectionDB bbdd){
        PreparedStatement stmt;
        System.out.println("Cambiando fumadores a no fumadores...");

        //pasajeros
        try{
            stmt = bbdd.getConn().prepareStatement("UPDATE pasajeros SET fumador = false WHERE fumador = true");
            stmt.executeUpdate();
            System.out.println("Los fumadores han sido cambiados");
        }catch(SQLException e){System.out.println(e);}

        //vuelos
        try{
            stmt = bbdd.getConn().prepareStatement("UPDATE vuelos SET plazas_fumadores = SUM(plazas_no_fumadores, plazas_fumadores),"
                    + " plazas_fumadores = 0 WHERE NOT plazas_fumadores = 0");
            stmt.executeUpdate();
            System.out.println("Las plazas han sido cambiadas");
        }catch(SQLException e){
            System.out.println(e);
        }

    }
}
