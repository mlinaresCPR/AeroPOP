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
 * @author TODO
 */
public class AeroPOP {
    private static final ConnectionDB bbdd = ConnectionDB.getInstance();

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        int opcion;
        do {
            System.out.println("**********SISTEMA DE GESTIÓN AEROPOP*************");
            System.out.println("0.Mostrar y pedir información de la BBDD en general");
            System.out.println("1.Mostrar la información de la tabla de pasajeros");
            System.out.println("2.Ver la información de los pasajeros de un vuelo");
            System.out.println("3.Planificar vuelo");
            System.out.println("4.Eliminar vuelo");
            System.out.println("5.Modificar los vuelos de fumadores a no fumadores");
            System.out.println("6.Salir");
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
                System.out.println("Pulsa Intro para continuar...");
                input.nextLine();
                opcion = Integer.MIN_VALUE;
            }

        } while (opcion != 6);
        input.close();
        bbdd.closeConn();
    }

    public static void menuOpciones(int opcion, Scanner input) {
        
        switch (opcion) {
            case 0:
                System.out.println("Has seleccionado la opción 0");
                Queries.mostrarVuelos(bbdd);
                break;
            case 1:
                System.out.println("Has seleccionado la opción 1");
                Queries.mostrarPasajeros(bbdd);
                break;
            case 2:
                System.out.println("Has seleccionado la opción 2");
                String codigo = setCodigoVuelo(input);
                Queries.mostrarPasajerosVuelo(bbdd, codigo);
                break;
            case 3:
                System.out.println("Has seleccionado la opción 3");
                String codigoVuelo = setCodigoVuelo(input);
                Date fechaHora = validarFechaHora(input);
                System.out.println("Introduzca el origen del vuelo: ");
                String procedencia = setDatos(input);
                System.out.println("Introduzca destino del vuelo: ");
                String destino = setDatos(input);
                System.out.println("Introduzaca numero de plazas para fumador");
                int pF = setNumeros(input);
                System.out.println("Introduzaca numero de plazas para NO fumador");
                int pNof = setNumeros(input);
                System.out.println("Introduzaca numero de plazas tipo TURISTA");
                int pT = setNumeros(input);
                System.out.println("Introduzaca numero de plazas tipo PRIMERA");
                int pP = setNumeros(input);
                System.out.println("**********NUEVO VUELO***********");
                System.out.println("Código Vuelo: " + codigoVuelo);
                System.out.println("Fecha: " + fechaHora);
                System.out.println("Destino: " + destino);
                System.out.println("Procedencia: " + procedencia);
                System.out.println("Plazas Fumador: " + pF);
                System.out.println("Plazas No Fumador: " + pNof);
                System.out.println("Plazas Turista: " + pT);
                System.out.println("Plazas Primera: " + pP);
                System.out.println("*********************************");
                String confirma;
                do {
                    System.out.println("Confirmar Carga (S/N):");
                    confirma = input.nextLine();
                }while(!confirma.equalsIgnoreCase("S") && !confirma.equalsIgnoreCase("N"));
                if(confirma.equalsIgnoreCase("S")) {
                    if(Queries.insertarVuelo(bbdd, codigoVuelo, fechaHora, destino, procedencia, pF, pNof, pT, pP)) {
                        System.out.println("DATOS CARGADOS CORRECTAMENTE!");
                    }else {
                        System.out.println("ERROR DE CARGA...");
                    }
                } else {
                    System.out.println("Operación Cancelada...");
                }
                break;
            case 4:
                System.out.println("Has seleccionado la opción 4");
                Queries.verVuelosCreados(bbdd, input);
                break;
            case 5:
                System.out.println("Has seleccionado la opción 5");
                Queries.cambiarFumadores(bbdd);
                break;
            case 6:
                System.out.println("Gracias por utilizar el programa que tenga un buen dia");
                break;
        }
        System.out.println("Pulsa Intro para continuar...");
        input.nextLine();
    }


    public static String setCodigoVuelo(Scanner input) {
        String codigoVuelo;
        String regexformato = "^[A-Z]{2}-[A-Z0-9]{2,3}-[0-9]{3,4}$";
        Matcher matcher;
        do {
            System.out.println("Introduzca un codigo de vuelo con el siguiente formato "
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

    /*Este metodo valida que la cadena de fecha y hora introducida tenga valores correctos
     *recibe el String fechaHora que ya tiene un formato correcto
     */
    public static boolean entrarFecha(String fechaHora) {
        if (fechaHora == null) {
                return false;
        }
        try {
        int hora = Integer.valueOf(fechaHora.substring(11, 13));
        int min = Integer.valueOf(fechaHora.substring(14, 16));
        int dia = Integer.valueOf(fechaHora.substring(8, 10));
        int mes = Integer.valueOf(fechaHora.substring(5, 7));
        int anio = Integer.valueOf(fechaHora.substring(0, 4));
        boolean anioBisciesto = false;
        //Validación Bisciesto
        if ((anio % 4 == 0 && anio % 400 == 0 && anio % 100 == 0) || 
                (anio % 4 == 0 && anio % 100 != 0)) {
            anioBisciesto = true;
        }
        
        if (hora < 0 || hora > 23) {
            return false;
        }
        if (min < 0 || min > 59) {
            return false;
        }
        if (dia < 1 || dia > 31) {
            return false;
        }
        if (mes == 2 && dia > 28 && !anioBisciesto) {
            return false;
        }
        if ((mes == 2 || mes == 4 || mes == 6 || mes == 9 || mes == 11) && (dia == 31)) {
            return false;
        }

        if (mes < 1 || mes > 12) {
            return false;
        }
        /*Para saber si un año es bisiesto se puede aplicar una simple formula,
        la cual dice que un año es bisiesto si es divisible por cuatro
        , excepto los principios de año (los divisibles por 100),
        que para ser bisiestos deben de ser divisibles también por 400.*/

        if (mes == 2 && dia == 29 && !anioBisciesto) {
            return false;
        }
        
        }catch(NumberFormatException | StringIndexOutOfBoundsException e) {
            return false;
        }
        
        // Fecha válida
        return true;
    }

    public static Date validarFechaHora(Scanner input) {
        Date thisDate = null;
        boolean formatoCorrecto = true;
        //utilizamos la clase DateFormat para establecer un formato para la hora y fecha
        DateFormat dateFormat = new SimpleDateFormat("YYYY-MM-DD HH:MM");
        //DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        do {
            formatoCorrecto = true;
            System.out.println("Escriba la fecha y hora de salida del vuelo en formato "
                    + "=>YYYY-MM-DD HH:MM :");
            String fechaHora = input.nextLine();

            try {
                //intentamos parsear el string que introduce el usuario y si falla es que esta mal, asi que repetimos
                thisDate = dateFormat.parse(fechaHora);
                //LocalDateTime dateTime = LocalDateTime.parse(fechaHora, formatter);
                formatoCorrecto = entrarFecha(fechaHora);
            } catch (ParseException ex) {
                formatoCorrecto = false;
            }
            if(!formatoCorrecto) {
                System.out.println("ERROR: Formato de fecha/hora incorrecto...");
            }
        } while (!formatoCorrecto);

        return thisDate;
    }

    public static String setDatos(Scanner input) {
        String dato;
        Matcher matcher;
        String formato = "[A-Z]{2,254}";
        do {
            System.out.print("Introduza el dato:");
            dato = input.nextLine().toUpperCase();
            Pattern pattern = Pattern.compile(formato);
            matcher = pattern.matcher(dato);

        } while (!matcher.matches());

        return dato;
    }

    public static int setNumeros(Scanner input) {
        int dato;
        boolean correcto;
        do {
            correcto = true;
            System.out.println("Introduzca un número:");
            try {
                dato = Integer.parseInt(input.nextLine());
            } catch (NumberFormatException nfe) {
                correcto = false;
                return 0;
            }
        } while (!correcto);

        return dato;
    }
}