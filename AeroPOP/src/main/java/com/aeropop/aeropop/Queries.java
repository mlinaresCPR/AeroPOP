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

    /**
     * Realiza una Query a la BBDD y guarda los datos en un ResultSet
     *
     * @param bbdd Objeto ConnectionDB con la conexión a la BBDD.
     * @param query Query de PostgreSQL.
     * @return ResultSet con el resultado de la consulta.
     */
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
     * Muestra los datos de la tabla Vuelos.
     *
     * @param bbdd Objeto ConnectionDB con la conexión a la BBDD.
     */
    public static void mostrarVuelos(ConnectionDB bbdd) {

        ResultSet rs = cargaDatos(bbdd, "SELECT * FROM vuelos");

        try {
            // Verifica que la Query tenga contenido.
            if (rs.isBeforeFirst()) {
                System.out.println("*******************TABLA VUELOS********************");
                System.out.println("CODIGO      FECHA-HORA      PROCEDENCIA  DESTINO");
                // Carga los datos de cada entrada.
                while (rs.next()) {
                    String codigoVuelo = rs.getString("CODIGO_VUELO");
                    String fecha = rs.getString("HORA_SALIDA");
                    String procedencia = rs.getString("PROCEDENCIA");
                    String destino = rs.getString("DESTINO");
                    System.out.println(codigoVuelo + "   " + fecha + "   " + procedencia + "    " + destino);
                }
            } else {
                System.out.println("ERROR: LA CONSULTA NO HA DEVUELTO RESULTADOS...");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        System.out.println("****************************************************");
    }

    /**
     * Muestra los datos de la tabla pasajeros.
     * 
     * @param bbdd Objeto ConnectionDB con la conexión a la BBDD.
     */
    public static void mostrarPasajeros(ConnectionDB bbdd) {
        ResultSet rs = cargaDatos(bbdd, "SELECT * FROM pasajeros");

        try {
            // Verifica que la Query tenga contenido.
            if (rs.isBeforeFirst()) {
                System.out.println("******************TABLA PASAJEROS**********************");
                System.out.println("PASAJERO  VUELO  PLAZA  FUMADOR");
                // Carga los datos de cada entrada.
                while (rs.next()) {
                    String codigo = Integer.toString(rs.getInt("ID"));
                    String codigoVuelo = rs.getString("CODIGO_VUELO");
                    String plaza = rs.getString("TIPO_PLAZA");
                    String fumador;
                    if (rs.getBoolean("fumador")) {
                        fumador = "SI";
                    } else {
                        fumador = "NO";
                    }
                    System.out.println(codigo + "   " + codigoVuelo + "   " + plaza + "   " + fumador);
                }
                System.out.println("****************************************************");
            } else {
                System.out.println("ERROR: LA CONSULTA NO HA DEVUELTO RESULTADOS...");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    /**
     * Muestra los pasajeros de un vuelo, indicandio el código de vuelo.
     * 
     * @param bbdd Objeto ConnectionDB con la conexión a la BBDD.
     * @param codigo Código de vuelo.
     */
    public static void mostrarPasajerosVuelo(ConnectionDB bbdd, String codigo) {
        ResultSet rs = cargaDatos(bbdd, "SELECT * FROM pasajeros WHERE  codigo_vuelo='" + codigo + "'");
        try {
            if (rs.isBeforeFirst()) {
                System.out.println("**************PASAJEROS VUELO " + codigo + "************");
                System.out.println("PASAJERO  VUELO  PLAZA  FUMADOR");
                while (rs.next()) {
                    String numero = Integer.toString(rs.getInt("ID"));
                    String codigoVuelo = rs.getString("CODIGO_VUELO");
                    String plaza = rs.getString("TIPO_PLAZA");
                    String fumador;
                    if (rs.getBoolean("fumador")) {
                        fumador = "SI";
                    } else {
                        fumador = "NO";
                    }
                    System.out.println(numero + "   " + codigoVuelo + "   " + plaza + "   " + fumador);
                }
                System.out.println("****************************************************");
            } else {
                System.out.println("ERROR: LA CONSULTA NO HA DEVUELTO RESULTADOS...");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Inserta un nuevo vuelo en la BBDD. 
     * @param bbdd Objeto ConnectionDB con la conexión a la BBDD.
     * @param codigo Código de vuelo.
     * @param hora Hora de Salida.
     * @param destino Destino del vuelo.
     * @param procede Origen del vuelo.
     * @param pF Plazas fumadores.
     * @param pNof Plazas no fumadores.
     * @param pT Plazas Turista.
     * @param pP Plazas Primera Clase.
     * @return true/Carga OK false/Error de carga
     */
    public static boolean insertarVuelo(ConnectionDB bbdd, String codigo, Date hora, String destino, String procede, int pF, int pNof, int pT, int pP) {
        Timestamp timestamp = new Timestamp(hora.getTime());
        String insertDatosQL = "INSERT INTO vuelos VALUES "
                + "(?,?,?,?,?,?,?,?)";
        try {
            PreparedStatement stmt = bbdd.getConn().prepareStatement(insertDatosQL);
            stmt.setString(1, codigo);
            stmt.setTimestamp(2, timestamp);
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
     * Este método borrará el vuelo del cual cuyo código se ha introducido
     * siempre que no tenga pasajeros asociados en la base de datos.
     *
     * @param bbdd Objeto ConnectionDB con la conexión a la BBDD.
     * @param codigo es el código único que presenta cada vuelo de la base de
     * datos.
     */
    public static void borrarVuelo(ConnectionDB bbdd, String codigo) {
        PreparedStatement stmt;
        try {
            stmt = bbdd.getConn().prepareStatement("DELETE FROM vuelos WHERE codigo_vuelo = ?");
            stmt.setString(1, codigo);
            stmt.executeUpdate();
            System.out.println("VUELO ELIMINADO CON ÉXITO!");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            System.out.println("ERROR AL BORRAR EL VUELO...");
        }
    }

    /**
     * Este método muestra los vuelos creados por el usuario, es decir los
     * vuelos sin pasajeros asignados, y posteriormente permite al usuario
     * borrar el que desee.
     *
     * @param bbdd Objeto ConnectionDB con la conexión a la BBDD.
     * @param input variable Scanner para introducir datos por teclado.
     */
    public static void verVuelosCreados(ConnectionDB bbdd, Scanner input) {
        ResultSet rs = cargaDatos(bbdd, "SELECT * FROM VUELOS WHERE (SELECT COUNT(*) FROM PASAJEROS "
                + "WHERE PASAJEROS.CODIGO_VUELO = VUELOS.CODIGO_VUELO) = 0");

        System.out.println("**********Lista de vuelos sin pasajeros asignados.*********");
        try {
            // Verificamos si existen vuelos sin pasajeros.
            if (rs.isBeforeFirst()) {
                System.out.println("CODIGO      FECHA-HORA      PROCEDENCIA  DESTINO");
                // Mostramos los vuelos.
                while (rs.next()) {
                    String codigoVuelo = rs.getString("CODIGO_VUELO");
                    String fecha = rs.getString("HORA_SALIDA");
                    String procedencia = rs.getString("PROCEDENCIA");
                    String destino = rs.getString("DESTINO");
                    System.out.println(codigoVuelo + "   " + fecha + "   " + procedencia + "    " + destino);
                }
                // Preguntamos que vuelo se quiere borrar
                String codigoVuelo;
                do {
                    System.out.println("Introduzca el código del vuelo que desee borrar (X para salir): ");
                    codigoVuelo = input.nextLine();
                    if (!codigoVuelo.equalsIgnoreCase("X")) {
                        rs = cargaDatos(bbdd, "SELECT '" + codigoVuelo + "' IN (SELECT CODIGO_VUELO FROM VUELOS) AND "
                                + "(SELECT COUNT(*) FROM PASAJEROS WHERE PASAJEROS.CODIGO_VUELO = '"
                                + codigoVuelo + "') = 0 AS VALIDO");
                        rs.next();
                        if (rs.getBoolean("VALIDO")) {
                            borrarVuelo(bbdd, codigoVuelo);
                            break;
                        } else {
                            System.out.println("VUELO NO VALIDO...");
                        }
                    }
                } while (!codigoVuelo.equalsIgnoreCase("X"));
            } else {
                System.out.println("Actualmente no hay ningún vuelo sin pasajeros, si quiere continuar cree uno.");
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        System.out.println("****************************************************");
    }

    /**
     * Cambia el valor de la columna 'Fumador' de los pasajeros en la base de
     * datos a 'NO' en caso de ser 'SI'. Además los valores de la columna 'pF'
     * (plazas fumadores) en la tabla 'Vuelos' pasa a 0 y su anterior valor se
     * suma a 'pNoF' (plazas no fumadores).
     * 
     * @param bbdd Objeto ConnectionDB con la conexión a la BBDD.
     */
    public static void cambiarFumadores(ConnectionDB bbdd, Scanner input) {
        PreparedStatement stmt;
        String confirma;
        do {
            System.out.println("Confirmar Cambio de Fumadores a No Fumadores (S/N):");
            confirma = input.nextLine();
        } while (!confirma.equalsIgnoreCase("S") && !confirma.equalsIgnoreCase("N"));

        // Si la entrada se confirma, intenta efectuar los cambios en la BBDD.
        if (confirma.equalsIgnoreCase("S")) {
            System.out.println("Cambiando fumadores a no fumadores...");

            //pasajeros
            try {
                stmt = bbdd.getConn().prepareStatement("UPDATE pasajeros SET fumador = false WHERE fumador = true");
                stmt.executeUpdate();
                System.out.println("Los fumadores han sido cambiados!");
            } catch (SQLException e) {
                System.out.println(e);
            }

            //vuelos
            try {
                stmt = bbdd.getConn().prepareStatement("UPDATE VUELOS SET PLAZAS_NO_FUMADORES = "
                        + "(PLAZAS_FUMADORES + PLAZAS_NO_FUMADORES), PLAZAS_FUMADORES = 0");
                stmt.executeUpdate();
                System.out.println("Las plazas han sido cambiadas!");
            } catch (SQLException e) {
                System.out.println(e);
            }

        } else {
            System.out.println("Operación Cancelada...");
        }
        
    }
}
