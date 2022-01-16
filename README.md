<h1 align="center">
    <img src="https://raw.githubusercontent.com/JGuilmar/fp/main/AeroPOP.jpg" alt="AeroPOP"/>
  <br/>
  AeroPOP
</h1>

El repositorio ha sido para poder desarrollar un trabajo de la asignatura Acceso a Datos.
Se puede clonar y examinar el código. Si los cambios se consideran oportunos, se hará merge con la rama principal.

Integrantes:
  - [Miguel Jaimes](https://github.com/mlinares1998)
  - [Yudaisy Ramos](https://github.com/YudaRamos)
  - [Miguel Pita](https://github.com/AlexHub1801)
  - [José Bayona](https://github.com/jguilmar)

------------

### Problema a resolver.
Hemos creado una aplicación para una aerolìnea que está conectada a una base de datos en [PostgreSQL](https://www.postgresql.org).

La base de datos tendrá 2 tablas:
1. Tabla Vuelos:
    - Código de velo.
    - Fecha y hora de salida.
    - Destino.
    - Procedencia.
    - Plaza de fumadores.
    - Plaza de no fumadores.
    - plaza turista.
    - Plaza primera 

 ![tabla_vuelos](https://raw.githubusercontent.com/JGuilmar/fp/main/vuelos.jpeg)

2. Tabla PASAJEROS:
    - ID.
    - Código de vuelo.
    - Tipo de plaza
    - Fumador(o no fumador)

![tabla_pasajeros](https://raw.githubusercontent.com/JGuilmar/fp/main/pasajeros.jpeg)

------------

### Explicación de la aplicación:
La aplicación es para una aerolínea e interactúa con la base de datos a través de un menú mostrado por consola.
Consta de los siguientes opciones de menú :
- 0 Mostrar y pedir información de la BBDD en general.
- 1 Mostrar la información de la tabla de pasajeros.
- 2 Ver la información de los pasajeros de un vuelo.
- 3 Planificar vuelo.
- 4 Eliminar vuelo.
- 5 Modificar los vuelos de fumadores a no fumadores.
- 6 Salir

#### Opción 0.
Muestra los datos que existe en la BBDD.
![opcion_0](https://raw.githubusercontent.com/JGuilmar/fp/main/0.PNG)

#### Opción 1.
Muestra los pasajeros que existe en la BBDD.

![opcion_1](https://raw.githubusercontent.com/JGuilmar/fp/main/1.PNG)


#### Opción 2.
Muestra los pasajeros de un vuelo concreto, pasamos por teclado el código del vuelo(comprobando si existe el vuelo).

![opcion_2](https://raw.githubusercontent.com/JGuilmar/fp/main/2.PNG)


#### Opción 3.
Creamos un nuevo vuelo, pasamos por teclado los respectivos datos.
- Código de vuelo (2*LETRAS + (2-3)*(LETRAS-NÚMEROS) + (3-4)*(NUMEROS)).
- Fecha y hora de salida (YYYY-MM-DD HH:MM)
- Origen.
- Destino.
- Número de plazas fumador.
- Número de plazas turista.

![opcion_3](https://raw.githubusercontent.com/JGuilmar/fp/main/3.PNG)


#### Opción 4.
Borramos un vuelo pasandole por teclado su respectivo código.

![opcion_4](https://raw.githubusercontent.com/JGuilmar/fp/main/4.PNG)


#### Opción 5.
Convertimos vuelos de fumadores en “no fumadores”.

![opcion_5](https://raw.githubusercontent.com/JGuilmar/fp/main/5.PNG)


#### Opción 6.
Salimos de la aplicación, mostrando un mensaje de despedida.

![opcion_6](https://raw.githubusercontent.com/JGuilmar/fp/main/6.PNG)
