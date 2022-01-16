/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aeropop.aeropop;

import java.util.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author mlinares, AlexPrieto01, YudaRamos, JGilmar
 */
public class AeroPOP {

    private static final ConnectionDB bbdd = ConnectionDB.getInstance();

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        int opcion;

        // Se muestra el menú hasta que se introduzca una opción valida o se salga del programa.
        do {
            System.out.println("*************SISTEMA DE GESTIÓN AEROPOP*************");
            System.out.println("0. Mostrar y pedir información de la BBDD en general.");
            System.out.println("1. Mostrar la información de la tabla de pasajeros.");
            System.out.println("2. Ver la información de los pasajeros de un vuelo.");
            System.out.println("3. Planificar vuelo.");
            System.out.println("4. Eliminar vuelo.");
            System.out.println("5. Modificar los vuelos de fumadores a no fumadores.");
            System.out.println("6. Salir.");

            try {
                System.out.println("Elija una opcion: ");
                opcion = Integer.parseInt(input.nextLine());

                if (opcion < 0 || opcion > 6) {
                    System.out.println("ERROR: Opción Inválida...");
                    System.out.print("Pulsa Intro para continuar...");
                    input.nextLine();
                } else {
                    menuOpciones(opcion, input);
                }

            } catch (NumberFormatException nfe) {
                System.out.println("ERROR: Debe introducir solo números...");
                System.out.println("Pulsa Intro para continuar ...");
                input.nextLine();
                opcion = Integer.MIN_VALUE;
            }

        } while (opcion != 6);
        // Se cierra la conexión a la BBDD y la entrada por teclado.
        input.close();
        bbdd.closeConn();
    }

    /**
     * Ejecuta las operaciones correspondientes a la operación seleccionada por
     * el usuario.
     *
     * @param opcion Indica la opción seleccionada por el usuario.
     * @param input traspasa el Scanner asociado al teclado.
     */
    public static void menuOpciones(int opcion, Scanner input) {

        switch (opcion) {
            case 0:
                System.out.println("-----------------------------------------------------------------------------------------------------");
                System.out.println("\nMOSTRANDO VUELOS ... ");
                Queries.mostrarVuelos(bbdd);
                System.out.println("-----------------------------------------------------------------------------------------------------");
                break;
            case 1:
                System.out.println("-----------------------------------------------------------------------------------------------------");
                System.out.println("\nMOSTRANDO PASAJEROS ... ");
                Queries.mostrarPasajeros(bbdd);
                System.out.println("-----------------------------------------------------------------------------------------------------");
                break;
            case 2:
                System.out.println("-----------------------------------------------------------------------------------------------------");
                Queries.mostrarPasajerosVuelo(bbdd, setCodigoVuelo(input));
                System.out.println("-----------------------------------------------------------------------------------------------------");
                break;
            case 3:
                System.out.println("-----------------------------------------------------------------------------------------------------");
                System.out.println("\nCREANDO UN VUELO ...");
                prepararNuevoVuelo(input);
                System.out.println("-----------------------------------------------------------------------------------------------------");
                break;
            case 4:
                System.out.println("-----------------------------------------------------------------------------------------------------");
                System.out.println("\nELIMINANDO UN VUELO ...");
                Queries.verVuelosCreados(bbdd, input);
                System.out.println("-----------------------------------------------------------------------------------------------------");
                break;
            case 5:
                System.out.println("-----------------------------------------------------------------------------------------------------");
                System.out.println("CAMBIANDO SITUACIÓN DE PLAZAS FUMADORES ...");
                Queries.cambiarFumadores(bbdd, input);
                System.out.println("-----------------------------------------------------------------------------------------------------");
                break;
            case 6:
                System.out.println("-----------------------------------------------------------------------------------------------------");
                System.out.println("GRACIAS POR UTILIZAR EL PROGRAMA, BUEN DÍA. VUELVE PRONTO!!!");
                System.out.println("-----------------------------------------------------------------------------------------------------");
                break;
        }
        System.out.println("Pulsa Intro para continuar...");
        input.nextLine();
    }

    /**
     * Añade un nuevo vuelo a la BBDD
     *
     * @param input traspasa el Scanner asociado al teclado.
     */
    public static void prepararNuevoVuelo(Scanner input) {

        String codigoVuelo = setCodigoVuelo(input);
        Date fechaHora = validarFechaHora(input);

        System.out.println("Introduzca el origen del vuelo: ");
        String procedencia = setDatos(input);

        System.out.println("Introduzca destino del vuelo: ");
        String destino = setDatos(input);
        
        System.out.println("\n***Número de plazas totales por defecto(400)\n");
        int pF;
        do {            
            System.out.println("Introduzca número de plazas para FUMADOR");
            pF = setNumeros(input);
            if(pF > 400)
                System.out.println("El número de plazas de los FUMADORES no debe exceder al de plazas totales(400)");
        } while (pF >400);

        int pNof = 400 - pF;
        System.out.println("Número de plazas para NO FUMADOR:" + pNof);
        
        int pT;
        do {            
            System.out.println("Introduzca número de plazas tipo TURISTA");
            pT = setNumeros(input);
            if(pT > 400)
                System.out.println("El número de plazas de tipo TURISTA no debe exceder al de plazas totales");
        } while (pT >400);

        int pP = 400 -pT;
        System.out.println("Número de plazas tipo PRIMERA: " + pP);
        
        // Validación de entrada por parte del usuario.
        System.out.println("\n**********NUEVO VUELO***********");
        System.out.println("Código Vuelo: " + codigoVuelo);
        System.out.println("Fecha: " + fechaHora);
        System.out.println("Destino: " + destino);
        System.out.println("Procedencia: " + procedencia);
        System.out.println("Plazas Fumador: " + pF);
        System.out.println("Plazas No Fumador: " + pNof);
        System.out.println("Plazas Turista: " + pT);
        System.out.println("Plazas Primera: " + pP);
        System.out.println("*********************************\n");

        String confirma;
        do {
            System.out.println("Confirmar Carga (S/N):");
            confirma = input.nextLine();
        } while (!confirma.equalsIgnoreCase("S") && !confirma.equalsIgnoreCase("N"));

        // Si la entrada se confirma, intenta añadirla a la BBDD.
        if (confirma.equalsIgnoreCase("S")) {
            if (Queries.insertarVuelo(bbdd, codigoVuelo, fechaHora, destino, procedencia, pF, pNof, pT, pP)) {
                System.out.println("DATOS CARGADOS CORRECTAMENTE!");
            } else {
                System.out.println("ERROR DE CARGA...");
            }
        } else {
            System.out.println("Operación Cancelada...");
        }
    }

    /**
     * Verifica que el usuario introduzca un código de vuelo
     *
     * @param input traspasa el Scanner asociado al teclado.
     * @return codigo de vuelo con el formato adecuado
     */
    public static String setCodigoVuelo(Scanner input) {
        String codigoVuelo;
        String regexformato = "^[A-Z]{2}-[A-Z0-9]{2,3}-[0-9]{3,4}$";
        Matcher matcher;

        do {
            System.out.println("\nIntroduzca un codigo de vuelo con el siguiente formato "
                    + "=> 2*LETRAS + (2-3)*(LETRAS-NÚMEROS) + (3-4)*(NUMEROS): ");
            codigoVuelo = input.nextLine();
            Pattern pattern = Pattern.compile(regexformato);
            matcher = pattern.matcher(codigoVuelo);
            if (!matcher.matches()) {
                System.out.println("Formato incorrecto");
            }
        } while (!matcher.matches());
        return codigoVuelo;
    }
    
    /**
     * Solicita al usuario introducir una fecha y valida que esta este en el
     * formato deseado.
     * 
     * @param input traspasa el Scanner asociado al teclado.
     * @return Date devuelve un objeto Date válido
     */
    public static Date validarFechaHora(Scanner input) {

        Date thisDate = null;
        boolean formatoCorrecto = true;
        // Utilizamos la clase DateFormat para establecer un formato para la hora y fecha
        DateFormat dateFormat = new SimpleDateFormat("YYYY-MM-DD HH:MM");
        
        do {
            formatoCorrecto = true;
            System.out.println("\nEscriba la fecha y hora de salida del vuelo en formato "
                    + "=>YYYY-MM-DD HH:MM :");
            String fechaHora = input.nextLine();

            try {
                // Intentamos parsear el string que introduce el usuario y si falla es que esta mal, asi que repetimos
                thisDate = dateFormat.parse(fechaHora);
                formatoCorrecto = entrarFecha(fechaHora);
            } catch (ParseException ex) {
                formatoCorrecto = false;
            }
            if (!formatoCorrecto) {
                System.out.println("ERROR: Formato de fecha/hora incorrecto...");
            }
        } while (!formatoCorrecto);

        return thisDate;
    }

    /**
     * Verifica si una fecha con el formato deseada es correcta.
     * 
     * @param fechaHora Recibe una fecha que ya tiene el formato adecuado.
     * @return boolean true/OK false/error.
     */
    public static boolean entrarFecha(String fechaHora) {
        if (fechaHora == null) {
            return false;
        }
        try {
            // Separamos los datos del String introducido.
            int hora = Integer.valueOf(fechaHora.substring(11, 13));
            int min = Integer.valueOf(fechaHora.substring(14, 16));
            int dia = Integer.valueOf(fechaHora.substring(8, 10));
            int mes = Integer.valueOf(fechaHora.substring(5, 7));
            int anio = Integer.valueOf(fechaHora.substring(0, 4));
            boolean anioBisciesto = false;
            
            // Validación Bisiesto.
            if ((anio % 4 == 0 && anio % 400 == 0 && anio % 100 == 0)
                    || (anio % 4 == 0 && anio % 100 != 0)) {
                anioBisciesto = true;
            }
            
            // Validación Hora.
            if (hora < 0 || hora > 23) {
                return false;
            }
            // Validación Minutos.
            if (min < 0 || min > 59) {
                return false;
            }
            // Validación Día.
            if (dia < 1 || dia > 31) {
                return false;
            }
            // Validación Mes de febrero.
            if (mes == 2 && dia > 28 && !anioBisciesto) {
                return false;
            }
            // Validación Día 31.
            if ((mes == 2 || mes == 4 || mes == 6 || mes == 9 || mes == 11) && (dia == 31)) {
                return false;
            }
            // Validación Mes.
            if (mes < 1 || mes > 12) {
                return false;
            }
            /*Para saber si un año es bisiesto se puede aplicar una simple formula,
            la cual dice que un año es bisiesto si es divisible por cuatro,
            excepto los principios de año (los divisibles por 100),
            que para ser bisiestos deben de ser divisibles también por 400.*/
            if (mes == 2 && dia == 29 && !anioBisciesto) {
                return false;
            }

        } catch (NumberFormatException | StringIndexOutOfBoundsException e) {
            return false;
        }

        // Fecha válida
        return true;
    }

    /**
     * Veririca que el usuario introduzca una cadena válida.
     * @param input traspasa el Scanner asociado al teclado.
     * @return String validado
     */
    public static String setDatos(Scanner input) {
        String dato;
        Matcher matcher;
        String formato = "[A-Z]{2,254}";
        do {
            //System.out.print("Introduza el dato:");
            dato = input.nextLine().toUpperCase();
            Pattern pattern = Pattern.compile(formato);
            matcher = pattern.matcher(dato);

        } while (!matcher.matches());

        return dato;
    }
    /**
     * Veririca que el usuario introduzca un valor numérico válido.
     * @param input traspasa el Scanner asociado al teclado.
     * @return Int validado
     */
    public static int setNumeros(Scanner input) {
        int dato;
        boolean correcto;
        do {
            correcto = true;
            //System.out.println("Introduzca un número:");
            try {
                dato = Integer.parseInt(input.nextLine());
            } catch (NumberFormatException nfe) {
                correcto = false;
                dato = 0;
            }
        } while (!correcto);

        return dato;
    }
}
