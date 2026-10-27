# TP Grupal OO2 2026 - Sistema de Gestión "Epicentro Gourmet"

Trabajo práctico grupal de Orientación a Objetos II (UNLa) · **Grupo 10**.

Aplicación web para gestionar los festivales del predio Epicentro Gourmet: unidades de venta, personal, pedidos, canon, sueldos y rentabilidad. Tiene tres zonas según el rol del usuario: **Administrador**, **Responsable** de una unidad de venta y **Empleado**.

- **Hito 1 (Hibernate):** repositorio aparte, [TPGrupalOO2Grupo10](https://github.com/JuanAvalle77/TPGrupalOO2Grupo10) (tag `hito1`, tal cual se entregó).
- **Hito 2 (Spring Boot):** este repositorio (proyecto Maven en la raíz).

## Índice

1. [Grupo](#grupo)
2. [Stack](#stack)
3. [Cómo correr la aplicación](#cómo-correr-la-aplicación)
4. [Qué se puede probar hoy](#qué-se-puede-probar-hoy)
5. [Estructura del proyecto](#estructura-del-proyecto)
6. [Diagrama de clases](#diagrama-de-clases)
7. [Casos de uso implementados y responsables](#casos-de-uso-implementados-y-responsables)
8. [Cómo trabajar (ramas y Pull Requests)](#cómo-trabajar-ramas-y-pull-requests)

## Grupo

**Grupo 10**

| Nombre y Apellido | Usuario GitHub |
|---|---|
| Juan Ignacio Avalle | JuanAvalle77 |
| Santiago Agarzúa | SantiagoCode06 |
| Franco Joaquín Luzardo | FJLuzardo |
| Federico Acosta Rosales | FederAcosta |

## Stack

Java 17+ · Spring Boot 3.3 (Web, Data JPA, Security, Validation, Mail) · Thymeleaf · Maven · MySQL 8 · Lombok · ModelMapper · OpenPDF

## Cómo correr la aplicación

### 1) Clonar el repositorio
```bash
git clone https://github.com/JuanAvalle77/TPGrupalOO2Hito2Grupo10.git
cd TPGrupalOO2Hito2Grupo10
```
(Tienen que haber aceptado la invitación como colaboradores.)

### 2) Requisitos
- **JDK 17 o superior** (sirve el JDK 21 que trae Eclipse).
- **MySQL 8** corriendo.
- No hace falta instalar Maven: el proyecto trae el wrapper (`mvnw`).

### 3) Crear la base de datos
```sql
CREATE DATABASE epicentro_gourmet_h2 CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 4) Variables de entorno
Las credenciales **no** se escriben en el código: `application.yml` las lee del entorno.

| Variable | Obligatoria | Descripción |
|---|---|---|
| `DB_USERNAME` | sí | usuario de MySQL (ej: `root`) |
| `DB_PASSWORD` | sí | contraseña de MySQL |
| `DB_URL` | no | por defecto `jdbc:mysql://localhost:3306/epicentro_gourmet_h2?serverTimezone=America/Argentina/Buenos_Aires` |
| `MAIL_USERNAME` / `MAIL_PASSWORD` | no | cuenta SMTP para enviar mails (ver [Envío de mails](#envío-de-mails)). Si no se definen, el mail se muestra en la consola. |
| `JAVA_HOME` | si Java no está en el PATH | carpeta del JDK (ver [Problemas frecuentes](#problemas-frecuentes)) |

> No usar el nombre `USERNAME`: en Windows ya existe y es el usuario del sistema.

PowerShell, para la sesión actual (las variables valen solo para esa ventana, así que la app se levanta desde la misma):
```powershell
$env:DB_USERNAME="root"; $env:DB_PASSWORD="tu_password"
```
En Eclipse/STS: *Run Configurations → Environment*. En IntelliJ: *Edit Configurations → Environment variables*. En VS Code, la terminal integrada es una PowerShell común.

### 5) Primera ejecución (crea las tablas)
```bash
./mvnw spring-boot:run          # en Windows PowerShell: .\mvnw spring-boot:run
```
La primera vez baja Maven y las librerías, y puede tardar unos minutos. Cuando aparezca `Started EpicentrogourmetApplication`, detener la app con `Ctrl+C`.

### 6) Cargar los datos de prueba
Por consola:
```bash
mysql -u root -p --default-character-set=utf8mb4 epicentro_gourmet_h2 < sql/datos_prueba.sql
```
(En PowerShell el `<` no funciona: usar `cmd /c "mysql ... < sql\datos_prueba.sql"` o MySQL Workbench.)

Con **MySQL Workbench**: *File → Open...* y elegir `sql/datos_prueba.sql`; agregar como **primera línea** `USE epicentro_gourmet_h2;`, seleccionar todo (`Ctrl+A`) y tocar **Run**.

El script se puede volver a ejecutar cuando quieran: borra los datos y los recarga (sirve para dejar todo limpio antes de una demo). Las fechas son relativas a hoy, así que siempre hay un festival activo.

### 7) Usar la aplicación
Volver a correr `./mvnw spring-boot:run` y entrar a http://localhost:8080

| Usuario | Contraseña | Rol |
|---|---|---|
| `admin` | `admin123` | Administrador |
| `juan.perez@epicentro.com` | `resp123` | Responsable de *El Fueguito* (festival activo) |
| `rocio.fernandez@epicentro.com` | `resp123` | Responsable de *La Empanada Andante* |
| `pedro.gomez@epicentro.com` | `resp123` | Responsable de *La Carpa de Pedro* |
| `lucia.gomez@epicentro.com` | `emp123` | Empleada (cajera) |
| `marcos.diaz@epicentro.com` | `emp123` | Empleado (cocina) |
| `valentina.ortiz@epicentro.com` | `emp123` | Empleada (lavaplatos, trabajó en 2 festivales) |

Para correr los tests: `./mvnw test`.

### Envío de mails
Cuando el responsable da de alta a un empleado, el sistema le manda un mail con su usuario (su email) y una contraseña generada al azar.

- **Sin configurar:** si no se definen `MAIL_USERNAME` y `MAIL_PASSWORD`, la app funciona igual y el mensaje completo (con usuario y contraseña) queda escrito en la consola. Sirve para desarrollar.
- **Con mail real (Gmail):**
  1. En la cuenta de Google, activar la **Verificación en 2 pasos** (Seguridad).
  2. Crear una **contraseña de aplicación** (Seguridad → Contraseñas de aplicaciones). Son 16 letras, y se usan **sin espacios**.
  3. Definir las variables antes de levantar la app (en la misma terminal):
     ```powershell
     $env:MAIL_USERNAME="tu_cuenta@gmail.com"; $env:MAIL_PASSWORD="LAS16LETRASSINESPACIOS"
     ```
- **Si el envío falla** (clave incorrecta, sin internet), el alta se deshace entera y la pantalla avisa *"No se pudo enviar el mail"*: no queda un empleado sin poder entrar.
- **Seguridad:** la contraseña de aplicación nunca va en el código ni en el repositorio. Si se filtra, se revoca desde la misma página de Google.

### Problemas frecuentes

| Qué pasa | Qué hacer |
|---|---|
| `JAVA_HOME not found` | Apuntarla al JDK. Para encontrarlo: `Get-ChildItem "C:\Program Files\Java","C:\Program Files\Eclipse Adoptium","$env:USERPROFILE\.p2" -Filter javac.exe -Recurse -ErrorAction SilentlyContinue \| Select-Object -First 5 FullName`. `JAVA_HOME` es esa ruta **sin** `\bin\javac.exe`. Ej: `$env:JAVA_HOME="C:\Users\tu_usuario\.p2\pool\plugins\org.eclipse.justj.openjdk.hotspot.jre.full.win32.x86_64_21.0.8.v20250724-1412\jre"` |
| `Access denied for user 'root'` | `DB_USERNAME` o `DB_PASSWORD` incorrectos. |
| `Unknown database 'epicentro_gourmet_h2'` | Falta el paso 3: crear la base. |
| La página de inicio no muestra festivales o no hay usuarios | Faltan los datos de prueba (paso 6). |
| `Port 8080 was already in use` | Hay otra copia de la app corriendo: cerrar esa terminal. |
| `mysql` no se reconoce como comando | Usar MySQL Workbench, o la ruta completa (`C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe`). |
| El alta de empleado dice *"No se pudo enviar el mail"* | Revisar `MAIL_USERNAME`/`MAIL_PASSWORD` (16 letras sin espacios, misma cuenta donde se generaron). |
| Errores de `getX()/setX()` inexistentes en el IDE | Instalar Lombok en el IDE (`java -jar lombok.jar`, está en `~/.m2/repository/org/projectlombok/lombok/`), o correr siempre con `mvnw` desde la terminal. |

## Qué se puede probar hoy

| Zona | Rutas | Qué hace |
|---|---|---|
| Público | `/` · `/login` | Inicio con los festivales (en curso, próximo y finalizado) · inicio de sesión |
| Responsable | `/responsable` | Panel con una tarjeta por caso de uso |
| | `/responsable/canon` | Canon que paga la unidad, con el detalle del cálculo |
| | `/responsable/haberes` | Liquidación de sueldos del personal de la unidad |
| | `/responsable/empleados` | Lista y filtros de empleados activos · alta (`/nuevo`), modificación (`/{id}/editar`), baja lógica y reactivación |
| | `/responsable/platos` | Menú y alta de platos de la unidad |
| | `/responsable/reporte-recaudacion` | Recaudación de un festival |
| | `/responsable/rentabilidad` | Rentabilidad neta del festival o de un rango de fechas |
| | `/responsable/plato-estrella` | Plato más vendido de una unidad |
| Empleado | `/empleado` | Panel del empleado |
| | `/empleado/mi-informacion` | Festivales y unidades asignados al empleado |
| | `/empleado/hoy` | En qué unidad y festival activo trabaja hoy |
| | `/empleado/sueldos` | Sus liquidaciones por festival y descarga del recibo en PDF (`/empleado/sueldos/{festivalId}/recibo`) |
| | `/empleado/cerrar-caja` | Cierre diario de caja (solo cajeros) |
| Administrador | `/admin` · `/admin/festivales` · `/admin/ranking` · `/admin/mayor-canon` | Panel, festivales y unidades, ranking por recaudación y unidades con mayor canon |
| Público | `/registro` | Registro de una unidad y su responsable con cuenta de acceso |

La seguridad separa tres zonas: `/admin/**` (ADMIN), `/responsable/**` (RESPONSABLE) y `/empleado/**` (EMPLEADO); entrar a la zona de otro rol da **403**. Las contraseñas se guardan con BCrypt, los formularios llevan token CSRF y el login redirige al panel del rol.

## Estructura del proyecto

```
src/main/java/com/unla/epicentrogourmet/
  entities/       entidades JPA (modelo del diagrama)
  enums/          Rol, RolCocina, Turno, TipoEmpleado
  repositories/   interfaces JpaRepository
  services/       interfaz + implementation/ (lógica de negocio)
  controllers/    controladores MVC (devuelven vistas Thymeleaf)
  dtos/           DTOs de reportes y formularios (no persisten)
  exceptions/     ReglaDeNegocioException (validaciones de carga)
  helpers/        ViewRouteHelper, ContraseniaAleatoria, EtiquetasHelper
  configuration/  seguridad, ModelMapper
src/main/resources/templates/   vistas (layout.html con navbar por rol)
src/test/                       tests de servicios y controladores
sql/datos_prueba.sql            datos de prueba
diagrama/                       diagramas de clases
docs/GUIA_CASOS_DE_USO.md       guía de cada caso de uso
```

## Diagrama de clases

**[Ver el diagrama de clases del Hito 2](diagrama/diagrama-clases-hito2.md)** (se dibuja directo en GitHub, con la lista de cambios respecto al Hito 1). El del Hito 1 sigue en [`diagrama/oo2UML.dia`](diagrama/oo2UML.dia) (fuente editable).

## Casos de uso implementados y responsables

Guía detallada de cada caso de uso (qué construir, rutas, reglas y cómo verificarlo): [`docs/GUIA_CASOS_DE_USO.md`](docs/GUIA_CASOS_DE_USO.md).

**Alcance:** la consigna pide **2 casos de uso individuales por integrante** (que no sean los del grupo) más los del grupo: 1 mail, 1 PDF y 1 DTO de reporte. Eso da **11 casos de uso obligatorios**. Los otros 5 casos de uso que lista el enunciado quedan como **opcionales**, para hacer solo si sobra tiempo.

| Integrante | Individuales (2) | Del grupo |
|---|---|---|
| Juan Avalle | Canon · Haberes | ABM de empleados + mail · Mayor canon (DTO de reporte) |
| Federico Acosta | Dónde trabajo hoy · Cerrar caja | Recibo de sueldo en PDF |
| Santiago Agarzúa | Registro de unidad · Alta de platos | - |
| Franco Luzardo | Ver festivales y unidades · Ranking de unidades | - |

### Implementados (mergeados a `main`)

| # | Caso de uso | Rol | Responsable | Rama | PR |
|---|---|---|---|---|---|
| Base | Proyecto Maven, entidades, login por roles, inicio con festivales, script SQL | todos | Juan Avalle | `CUBaseProyectoJuanAvalle` | [PR #1 (repo del Hito 1)](https://github.com/JuanAvalle77/TPGrupalOO2Grupo10/pull/1) |
| 5 | Alta, baja lógica, modificación y reactivación de empleados + **mail** | Responsable | Juan Avalle | `CUABMEmpleadosJuanAvalle` | [PR #3](https://github.com/JuanAvalle77/TPGrupalOO2Hito2Grupo10/pull/3) |
| 8 | Cálculo de canon de una unidad | Responsable | Juan Avalle | `CUCalculoCanonJuanAvalle` | [PR #1](https://github.com/JuanAvalle77/TPGrupalOO2Hito2Grupo10/pull/1) |
| 9 | Liquidación de haberes | Responsable | Juan Avalle | `CULiquidacionHaberesJuanAvalle` | [PR #2](https://github.com/JuanAvalle77/TPGrupalOO2Hito2Grupo10/pull/2) |
| 14 | Recibo de sueldo en **PDF** | Empleado | Federico Acosta | `CUReciboSueldoFedericoAcosta` | [PR #6](https://github.com/JuanAvalle77/TPGrupalOO2Hito2Grupo10/pull/6) |
| 15 | Ver en qué festival activo y unidad trabajo hoy | Empleado | Federico Acosta | `CUDondeTrabajoHoyFedericoAcosta` | [PR #5](https://github.com/JuanAvalle77/TPGrupalOO2Hito2Grupo10/pull/5) |
| 16 | Cerrar caja (cajero) | Empleado | Federico Acosta | `CUCerrarCajaFedericoAcosta` | [PR #4](https://github.com/JuanAvalle77/TPGrupalOO2Hito2Grupo10/pull/4) |

### Implementados en la rama local `CUsRestantesJuanAvalle` (PR pendiente)

| # | Caso de uso | Rol | Estado |
|---|---|---|---|
| 1 | Ver festivales y unidades de venta de un festival | Admin | Implementado |
| 2 | Ranking de unidades por recaudación | Admin | Implementado |
| 3 | Unidades con mayor canon (`ReporteMayoresCanonDTO`) | Admin | Implementado |
| 4 | Registro de Unidad de Venta con su Responsable | Público | Implementado |
| 7 | Alta de platos | Responsable | Implementado |

No quedan casos de uso obligatorios pendientes en la rama local. Estos cambios todavía no están en `main`.

### Opcionales implementados en la rama local

Los cinco casos opcionales también están implementados en `CUsRestantesJuanAvalle`; falta su revisión e incorporación a `main`.

### Requisitos del enunciado

**Mínimos por grupo:** 1 envío de mail (CU 5, implementado) · 1 DTO de reporte (CU 3, mayor canon, `ReporteMayoresCanonDTO`) · 1 PDF (CU 14, implementado).

**Validaciones de carga (2 por integrante):**

| Integrante | Validaciones | Estado |
|---|---|---|
| Juan Avalle | personal mayor de edad · email con formato válido y único | implementadas (CU 5) |
| Federico Acosta | un pedido solo puede cargarse con la fecha del día y cantidades mayores a 0 · un único cierre de caja por cajero y por día | implementadas (CU 16) |
| Santiago Agarzúa | código de unidad único y con formato válido · precio de venta mayor al costo de producción del plato | implementadas en registro y alta de platos |
| Franco Luzardo | período del festival válido (fin no anterior al inicio) · superficie de la unidad mayor a 0 | implementadas en el servicio, las entidades y el formulario |

**Dependencias:** el canon (CU 8) y los haberes (CU 9) ya están en `main`, así que el CU 3 (mayor canon) usa `CanonService`.

## Cómo trabajar (ramas y Pull Requests)

Cada caso de uso entra a `main` **mediante Pull Request**. No se pushea directo a `main`.

### 1) Crear la rama y desarrollar
```bash
git checkout main && git pull
git checkout -b CUAltaPlatosSantiagoAgarzua      # CU<Funcionalidad><Nombre>
# ... desarrollar y probar el caso de uso ...
git add .
git commit -m "CU Alta de platos: entidad, repositorio, servicio, controlador y vista"
git push -u origin CUAltaPlatosSantiagoAgarzua
```

### 2) Abrir el Pull Request
- **Desde la web (lo más simple):** después del `push`, entrar al repo en GitHub: aparece un cartel amarillo **"Compare & pull request"**. Si no aparece, pestaña *Pull requests* → *New pull request* → base `main`, compare tu rama.
- **Desde la terminal** (solo si tienen instalado y logueado GitHub CLI): `gh pr create --base main --fill`

En la descripción del PR completar:
1. **Qué hace** el caso de uso (una o dos líneas).
2. **Qué se hizo:** entidades/consultas, repositorios, servicios, controladores, vistas.
3. **Cómo probarlo:** qué usuario usar (ver tabla de usuarios) y qué debería verse.

### 3) Si `main` cambia mientras trabajás
Cuando se mergean otros PRs, tu rama queda atrasada. Antes de pedir la revisión (o si GitHub avisa de conflictos):
```bash
git fetch origin
git merge origin/main        # resolver conflictos si los hay, probar, y:
git push
```
Si hay conflicto en un archivo compartido (`layout.html`, el `index.html` de un panel, `ViewRouteHelper`), conservar **ambos** cambios.

### 4) Revisión y merge
- Un compañero **distinto del autor** revisa el PR (ver abajo). El autor lo mergea con **Create a merge commit** cuando está aprobado (así se conservan los commits de cada uno), y borra la rama recién cuando todo lo que dependía de ella ya se mergeó.
- **Link del PR en el README:** la tabla de casos de uso la completa **una sola persona (Juan)** para no generar conflictos. Pasale el link de tu PR por el grupo.

### Cómo revisar un Pull Request
1. **Leer los cambios:** en el PR, pestaña **Files changed**. Para comentar una línea, pasar el mouse sobre el número y tocar el **+** azul. Si el PR está apilado sobre otro (su base no es `main`), solo se ven los cambios propios.
2. **Probarlo en tu compu** (lo más importante):
   ```bash
   git fetch origin
   git checkout CUAltaPlatosSantiagoAgarzua     # la rama del PR
   ./mvnw test                                   # tests
   ./mvnw spring-boot:run                        # y probar el flujo con el usuario que indica el PR
   ```
3. **Checklist:**
   - [ ] Compila, los tests pasan y el flujo descripto funciona con los datos de prueba.
   - [ ] Sigue la estructura (entidad / repositorio / servicio / controlador / vista) y usa un controlador propio.
   - [ ] La ruta está bajo el prefijo de su rol (`/admin`, `/responsable`, `/empleado`) y otro rol recibe 403.
   - [ ] Nada hardcodeado: los valores de negocio salen de `CostoFestival`.
   - [ ] Sin contraseñas ni datos personales en el código.
   - [ ] Tiene las validaciones de carga que le tocan y mensajes de error claros.
   - [ ] Tiene su link en el navbar del rol y su tarjeta activa en el panel.
   - [ ] La descripción del PR explica qué se hizo y cómo probarlo; no toca archivos de otros casos de uso salvo su tarjeta/link.
4. **Resolver la revisión:** botón **Review changes** → *Approve* (está bien), *Request changes* (hay que corregir algo) o *Comment* (dudas). Dejá comentarios concretos ("en la línea X falta validar Y").

### Reglas generales
1. Cada caso de uso agrega su link en el `navbar` de `layout.html` (dentro del bloque de su rol) y reemplaza su tarjeta *Pendiente* en el panel del rol.
2. Nada hardcodeado: los valores de negocio (sueldo base, canon, plus) salen de `CostoFestival`.
3. Credenciales solo por variables de entorno; nunca commitear contraseñas ni claves de aplicación.
4. Si falta una entidad o un campo, avisar en el grupo antes de modificarla.
5. Si el caso de uso agrega clases o métodos, actualizar el [diagrama de clases](diagrama/diagrama-clases-hito2.md).
