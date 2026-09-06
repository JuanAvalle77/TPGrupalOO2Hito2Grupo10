# TP Grupal OO2 2026 - Sistema de Gestión "Epicentro Gourmet"

Trabajo práctico grupal de la materia Orientación a Objetos II (UNLa) - Hito 1: Hibernate.

## Grupo

**Grupo 10**

| Nombre y Apellido | Usuario GitHub |
|---|---|
| Juan Ignacio Avalle | JuanAvalle77 |
| Santiago Agarzúa | SantiagoCode06 |
| Franco Joaquín Luzardo | FJLuzardo |
| Federico Acosta Rosales | FederAcosta |

## Diagrama de Clases

Fuente editable: [`diagrama/oo2UML.dia`](diagrama/oo2UML.dia) (abrir con [Dia](https://sourceforge.net/projects/dia-installer/)).

```mermaid
classDiagram
    class Festival {
        -nombre: string
        -temporada: string
        -fechaInicio: date
        -fechaFin: date
        -unidadesVenta: Set~UnidadVenta~
        -platos: Set~Plato~
    }
    class UnidadVenta {
        <<abstracta>>
        -nombre: string
        -superficie: float
        -codigo: string
        -festival: Festival
        -responsable: Personal
        -platos: Set~Plato~
        -pedidosRealizados: Set~Pedido~
        -personal: Set~Personal~
        +calcularCostoTotal() double
    }
    class FoodTruck {
        -requiereElectricidad: boolean
        -patente: String
        +calcularCostoTotal() double
    }
    class PuestoDesarmable {
        -cantidadCarpas: int
        -tiempoMontaje: int
        +calcularCostoTotal() double
    }
    class Personal {
        <<abstracta>>
        -nombre: String
        -apellido: String
        -dni: int
        -fechaNacimiento: Date
        -fechaIngreso: Date
        -sueldoBase: double
        -unidadVenta: UnidadVenta
        -contacto: String
        +calcularSueldo() double
    }
    class Cajero {
        -turno: String
        -plus: int
        +calcularSueldo() double
    }
    class Cocinero {
        -especialidad: String
        -plusEspecialidad: double
        +calcularSueldo() double
    }
    class Plato {
        -nombre: String
        -precioVenta: double
        -costoProduccion: double
        -unidadVenta: UnidadVenta
        +calcularGanancia() double
    }
    class Pedido {
        -fecha: Date
        -festival: Festival
        -unidadVenta: UnidadVenta
        -detalles: Set~DetallesPedido~
        +calcularTotal() double
    }
    class DetallesPedido {
        -cantidad: int
        -plato: Plato
        -pedido: Pedido
    }

    UnidadVenta <|-- FoodTruck
    UnidadVenta <|-- PuestoDesarmable
    Personal <|-- Cajero
    Personal <|-- Cocinero

    Festival "1" --> "0..*" UnidadVenta
    UnidadVenta "1" --> "1..*" Plato
    UnidadVenta "1" --> "0..*" Pedido
    UnidadVenta "1" --> "1..*" Personal : personal
    Pedido "1" --> "1..*" DetallesPedido
    DetallesPedido "0..*" --> "1" Plato
```

## Casos de Uso (Hito 1)

| Caso de Uso | Responsable | Estado |
|---|---|---|
| FoodTrucks de un festival con la cantidad de platos que ofrece cada uno (Herencia: FoodTruck · Uno a Muchos: UnidadVenta→Plato) | Juan Avalle | ✅ |
| Staff (Cocineros/Cajeros) asignado a una Unidad de Venta, con sueldo calculado (Herencia: Cocinero/Cajero · Uno a Muchos: UnidadVenta→Personal) | Santiago Agarzúa | pendiente |
| Puestos Desarmables de un festival, con cantidad de carpas y superficie total (Herencia: PuestoDesarmable · Uno a Muchos: Festival→UnidadVenta) | Franco Luzardo | ✅ |
| Total facturado por cada FoodTruck de un festival (Herencia: FoodTruck · Uno a Muchos: Pedido→DetallesPedido) | Federico Acosta Rosales | ✅ |

## Stack

- Java 21 (JDK)
- Hibernate 5.4.11 (mapeo por XML `.hbm.xml`, sin JPA)
- MySQL 8

## Cómo levantar el proyecto (paso a paso)

Guía pensada para alguien que nunca tocó este repo. Seguí los pasos en orden.

### 0) Requisitos previos

- **Eclipse IDE** (cualquier edición para Java, ej. "Eclipse IDE for Java Developers"). Trae un JDK propio incluido, no hace falta instalar Java aparte.
- **MySQL Server** instalado y corriendo, + **MySQL Workbench** (o cualquier cliente para ejecutar SQL).
- **Git** instalado (o descargar el repo como ZIP desde GitHub si no usás Git por consola).

### 1) Clonar el repositorio

Abrí una terminal (Git Bash, símbolo del sistema o PowerShell), ubicate con `cd` en la carpeta donde querés que quede el proyecto (por ejemplo `cd Documents`), y ahí corré:

```bash
git clone https://github.com/JuanAvalle77/TPGrupalOO2Grupo10.git
```

Esto crea una carpeta nueva `TPGrupalOO2Grupo10` con el proyecto adentro.

O desde GitHub, sin usar Git por consola: botón verde **Code → Download ZIP** y descomprimirlo en alguna carpeta.

### 2) Crear la base de datos

Abrí **MySQL Workbench**, conectate a tu instancia local (doble clic en la conexión, te va a pedir la contraseña de `root`), abrí una pestaña de **Query** y ejecutá (ícono del rayo ⚡ o `Ctrl+Enter`):

```sql
CREATE DATABASE epicentro_gourmet
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;
```

No hace falta crear tablas a mano — Hibernate las crea solo la primera vez que corras el proyecto.

### 3) Importar el proyecto en Eclipse

1. Abrí Eclipse (elegí cualquier carpeta como workspace si te lo pide).
2. Menú **File → Open Projects from File System...**
3. En "Import source", clic en **Directory...** y seleccioná la carpeta donde clonaste/descomprimiste el repo (la que tiene adentro `src`, `lib`, `.project`, etc.).
4. Debería aparecer el proyecto `TpGrupo10` tildado en la lista → **Finish**.
5. Esperá que Eclipse termine de indexar/compilar (mirá que no queden procesos corriendo en la barra de estado, abajo a la derecha).

### 4) Configurar la conexión a la base (`hibernate.cfg.xml`)

Este archivo tiene tu contraseña de MySQL, por eso **no está en el repo** (cada uno tiene la suya). Hay que crearlo a partir de una plantilla:

1. En el Package Explorer, buscá `src/hibernate.cfg.xml.template`.
2. Copialo y pegalo en la misma carpeta (`Ctrl+C`, `Ctrl+V` dentro de Eclipse) — te va a preguntar el nombre de la copia, o si prefieren, hacé la copia desde el explorador de Windows.
3. Renombrá la copia a `hibernate.cfg.xml` (sin `.template`), en la misma carpeta `src`.
4. Abrilo y reemplazá el texto `TU_PASSWORD_ACA` (línea `<property name="connection.password">...</property>`) por tu contraseña real de MySQL.
5. Guardá con `Ctrl+S`.

### 5) Chequear las librerías (Classpath vs Modulepath) — importante

Este es el paso que más rompe las bolas si se salta. Java moderno (9+) separa las dependencias en dos "carriles": **Modulepath** y **Classpath**. Los jars de Hibernate que usamos son viejos y tienen que ir sí o sí en **Classpath**, o vas a tener un error `NoClassDefFoundError` relacionado a `byte-buddy` apenas arranque Hibernate.

Como el archivo `.classpath` del proyecto ya viene configurado bien en el repo, en teoría no tenés que tocar nada. Pero conviene verificarlo:

1. Clic derecho en el proyecto → **Properties**.
2. **Java Build Path** → pestaña **Libraries**.
3. Deberías ver dos nodos: **Modulepath** (con solo el `JRE System Library`) y **Classpath** (con los 19 jars de `lib/`).
4. Si ves los 19 jars bajo **Modulepath** en lugar de Classpath: seleccionalos todos (clic en el primero, `Shift+clic` en el último), **Remove**, hacer clic en el nodo **Classpath** para seleccionarlo, y **Add JARs...** → navegar a `TpGrupo10/lib` → seleccionar los 19 archivos `.jar` → OK.
5. **Apply and Close**.

### 6) Primera corrida: crear tablas y cargar datos de prueba

1. En el Package Explorer, abrí `src/test/TestCargarDatosPrueba.java`.
2. Clic derecho → **Run As → Java Application**.
3. Mirá la consola: vas a ver mucho texto en rojo (es normal, es el logging de Hibernate, no un error) y al final líneas tipo `Festival creado, id=1`, `Cocinero creado, id=1`, etc. sin ninguna `Exception`.
4. Confirmá en MySQL Workbench (Schemas → `epicentro_gourmet` → clic derecho → Refresh All) que aparecieron las tablas.

**Corré esta clase UNA SOLA VEZ.** Si la corrés de nuevo va a fallar por datos duplicados (DNI/código únicos) — es esperado, no arreglar nada, simplemente no la vuelvas a correr. Si necesitás datos frescos, mirá "Reiniciar la base" más abajo.

### 7) Correr un caso de uso

Cualquier otra clase dentro de `src/test/` (por ejemplo `TestCasoDeUsoFoodTrucks.java`) se corre igual: clic derecho → **Run As → Java Application**. Esas sí las podés correr las veces que quieras, son solo lectura.

## Cómo agregar tu propio Caso de Uso

1. Elegí de la tabla de arriba cuál te toca (o coordinen entre el grupo).
2. En el `Dao` correspondiente (`src/dao/`), agregá un método nuevo con una consulta HQL, siguiendo el mismo patrón que `UnidadVentaDao.traerFoodTrucksDeFestival(...)` (abrir sesión, `try/finally` con `session.close()`).
3. Agregá el método "espejo" en el `ABM` correspondiente (`src/negocio/`) que solo delega al Dao.
4. Creá una clase de prueba en `src/test/` con un `main()` que llame a tu ABM e imprima el resultado.
5. Actualizá la tabla de Casos de Uso en este README con tu nombre y el estado.
6. Commiteá y pusheá **tu propio commit** (no lo mezcles con cambios de otra parte del proyecto), para que se vea claramente tu aporte en el historial.

## Reiniciar la base desde cero

Si en algún momento querés borrar todo y volver a probar desde el principio, en MySQL Workbench:

```sql
DROP DATABASE epicentro_gourmet;
CREATE DATABASE epicentro_gourmet CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

Y después volvé a correr `TestCargarDatosPrueba.java` una sola vez.

## Estructura

- `datos/` - POJOs del modelo
- `mapeos/` - mapeos Hibernate `.hbm.xml`
- `dao/` - acceso a datos (`HibernateUtil` + un Dao por entidad)
- `negocio/` - capa de negocio (ABM por entidad)
- `test/` - clases de prueba con `main()`
