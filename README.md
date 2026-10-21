# TP Grupal OO2 2026 - Sistema de Gestión "Epicentro Gourmet"

Trabajo práctico grupal de Orientación a Objetos II (UNLa).

- **Hito 1 (Hibernate):** repositorio aparte, [TPGrupalOO2Grupo10](https://github.com/JuanAvalle77/TPGrupalOO2Grupo10) (tag `hito1`, tal cual se entregó).
- **Hito 2 (Spring Boot):** este repositorio (proyecto Maven en la raíz).

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

### 0) Clonar el repositorio
```bash
git clone https://github.com/JuanAvalle77/TPGrupalOO2Hito2Grupo10.git
cd TPGrupalOO2Hito2Grupo10
```
(Tienen que haber aceptado la invitación como colaboradores. Para empezar un caso de uso, ver "Cómo trabajar" más abajo.)

### 1) Requisitos
- JDK 17 o superior (sirve el JDK 21 que trae Eclipse).
- MySQL 8 corriendo.
- No hace falta instalar Maven: el proyecto trae el wrapper (`mvnw`).

### 2) Crear la base de datos
```sql
CREATE DATABASE epicentro_gourmet_h2 CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 3) Variables de entorno
Las credenciales **no** se escriben en el código (`application.yml` las lee del entorno):

| Variable | Obligatoria | Descripción |
|---|---|---|
| `DB_USERNAME` | sí | usuario de MySQL (ej: `root`) |
| `DB_PASSWORD` | sí | contraseña de MySQL |
| `DB_URL` | no | por defecto `jdbc:mysql://localhost:3306/epicentro_gourmet_h2?serverTimezone=America/Argentina/Buenos_Aires` |
| `MAIL_USERNAME` / `MAIL_PASSWORD` | no | cuenta SMTP (Gmail con contraseña de aplicación). Si no se definen, los mails solo se muestran en la consola. |

> No usar el nombre `USERNAME`: en Windows ya existe y es el usuario del sistema.

PowerShell (para la sesión actual):
```powershell
$env:DB_USERNAME="root"; $env:DB_PASSWORD="tu_password"
```
En Eclipse/STS: *Run Configurations → Environment*. En IntelliJ: *Edit Configurations → Environment variables*.

### 4) Primera ejecución (crea las tablas)
```bash
./mvnw spring-boot:run          # en Windows PowerShell: .\mvnw.cmd spring-boot:run
```
Si `JAVA_HOME` no está definida, apuntarla al JDK. Cuando termine de arrancar (`Started EpicentrogourmetApplication`), detener la app con `Ctrl+C`.

### 5) Cargar los datos de prueba
Con MySQL Workbench (abrir y ejecutar `sql/datos_prueba.sql` sobre `epicentro_gourmet_h2`) o por consola:
```bash
mysql -u root -p --default-character-set=utf8mb4 epicentro_gourmet_h2 < sql/datos_prueba.sql
```
El script se puede volver a ejecutar cuando quieran: borra los datos y los recarga. Las fechas son relativas a hoy (siempre hay un festival activo).

### 6) Usar la aplicación
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

> **Lombok en Eclipse:** si ven errores de `getX()/setX()` inexistentes, hay que instalar Lombok en el IDE (`java -jar lombok.jar`, que está en `~/.m2/repository/org/projectlombok/lombok/`). Alternativa: correr siempre con `mvnw` desde la terminal.

## Estructura del proyecto

```
src/main/java/com/unla/epicentrogourmet/
  entities/       entidades JPA (modelo del diagrama)
  enums/          Rol, RolCocina, Turno
  repositories/   interfaces JpaRepository
  services/       interfaz + implementation/ (logica de negocio)
  controllers/    controladores MVC (devuelven vistas Thymeleaf)
  dtos/           DTOs de reportes (no persisten)
  helpers/        ViewRouteHelper (rutas y vistas)
  configuration/  seguridad, ModelMapper
src/main/resources/templates/   vistas (layout.html con navbar por rol)
sql/datos_prueba.sql            datos de prueba
```

La seguridad separa tres zonas: `/admin/**` (ADMIN), `/responsable/**` (RESPONSABLE) y `/empleado/**` (EMPLEADO). Las contraseñas se guardan con BCrypt. El login redirige al panel del rol.

## Diagrama de clases

**[Ver el diagrama de clases del Hito 2](diagrama/diagrama-clases-hito2.md)** (se dibuja directo en GitHub, con la lista de cambios respecto al Hito 1). El del Hito 1 sigue en [`diagrama/oo2UML.dia`](diagrama/oo2UML.dia) (fuente editable).

## Casos de uso implementados y responsables

El estado y el link al Pull Request de cada caso de uso se completan acá a medida que se mergean.

| # | Caso de uso | Rol | Responsable | Rama | PR | Estado |
|---|---|---|---|---|---|---|
| Base | Proyecto Maven, entidades, login por roles, inicio con festivales, script SQL | todos | Juan Avalle | `CUBaseProyectoJuanAvalle` | [PR #1 (repo del Hito 1)](https://github.com/JuanAvalle77/TPGrupalOO2Grupo10/pull/1) | en revisión |
| 1 | Ver festivales y unidades de venta de un festival | Admin | Franco Luzardo | `CUVerFestivalesUnidadesFrancoLuzardo` | | pendiente |
| 2 | Ranking de unidades por recaudación | Admin | Franco Luzardo | `CURankingUnidadesFrancoLuzardo` | | pendiente |
| 3 | Unidades con mayor canon (`ReporteMayoresCanonDTO`) | Admin | Federico Acosta | `CUMayorCanonFedericoAcosta` | | pendiente |
| 4 | Registro de Unidad de Venta con su Responsable (crea usuario) | Público | Santiago Agarzúa | `CURegistroUnidadSantiagoAgarzua` | | pendiente |
| 5 | Alta, baja lógica, modificación y reactivación de empleados + **mail** | Responsable | Juan Avalle | `CUABMEmpleadosJuanAvalle` | [PR #3](https://github.com/JuanAvalle77/TPGrupalOO2Hito2Grupo10/pull/3) | en revisión |
| 6 | Filtros de empleados por uno o varios atributos (solo activos) | Responsable | Santiago Agarzúa | `CUFiltrosEmpleadosSantiagoAgarzua` | | pendiente |
| 7 | Alta de platos | Responsable | Santiago Agarzúa | `CUAltaPlatosSantiagoAgarzua` | | pendiente |
| 8 | Cálculo de canon de una unidad | Responsable | Juan Avalle | `CUCalculoCanonJuanAvalle` | [PR #1](https://github.com/JuanAvalle77/TPGrupalOO2Hito2Grupo10/pull/1) | en revisión |
| 9 | Liquidación de haberes | Responsable | Juan Avalle | `CULiquidacionHaberesJuanAvalle` | [PR #2](https://github.com/JuanAvalle77/TPGrupalOO2Hito2Grupo10/pull/2) | en revisión |
| 10 | Reporte de recaudación de un festival (`ReporteVentaDTO`) | Responsable | Franco Luzardo | `CUReporteRecaudacionFrancoLuzardo` | | pendiente |
| 11 | Rentabilidad neta de una unidad (y entre dos fechas) | Responsable | Juan Avalle | `CURentabilidadNetaJuanAvalle` | | pendiente |
| 12 | Plato estrella (`PlatoDTO`) | Responsable | Santiago Agarzúa | `CUPlatoEstrellaSantiagoAgarzua` | | pendiente |
| 13 | Ver mi información (festivales y unidades en las que trabajé) | Empleado | Franco Luzardo | `CUMiInformacionFrancoLuzardo` | | pendiente |
| 14 | Recibo de sueldo en **PDF** | Empleado | Federico Acosta | `CUReciboSueldoFedericoAcosta` | | pendiente |
| 15 | Ver en qué festival activo y unidad trabajo hoy | Empleado | Federico Acosta | `CUDondeTrabajoHoyFedericoAcosta` | | pendiente |
| 16 | Cerrar caja (cajero) | Empleado | Federico Acosta | `CUCerrarCajaFedericoAcosta` | | pendiente |

Guía detallada de cada caso de uso (qué construir, rutas, reglas y cómo verificarlo): [`docs/GUIA_CASOS_DE_USO.md`](docs/GUIA_CASOS_DE_USO.md).

**Mínimos por grupo:** 1 envío de mail (CU 5) · DTOs de reporte (CU 3, 10, 12) · 1 PDF (CU 14).

**Validaciones de carga (2 por integrante):**

| Integrante | Validaciones |
|---|---|
| Juan Avalle | personal mayor de edad · email con formato válido y único |
| Santiago Agarzúa | código de unidad único y con formato válido · precio de venta mayor al costo de producción del plato |
| Franco Luzardo | período del festival válido (fin no anterior al inicio) · superficie de la unidad mayor a 0 |
| Federico Acosta | un pedido solo puede cargarse con la fecha del día y cantidades mayores a 0 · un único cierre de caja por cajero y por día |

**Dependencias entre casos de uso:** los CU 8 (canon) y 9 (haberes) los usan los CU 3, 11 y 14. Se mergean primero; mientras tanto el resto puede avanzar con los datos de prueba.

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
Si hay conflicto en un archivo compartido (`layout.html`, el `index.html` de un panel), conservar **ambos** cambios.

### 4) Revisión y merge
- Un compañero **distinto del autor** revisa el PR (ver abajo). El autor lo mergea con el botón **Merge pull request** cuando está aprobado, y borra la rama.
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
   - [ ] La descripción del PR explica qué se hizo y cómo probarlo; no toca archivos de otros casos de uso salvo su tarjeta/link.
4. **Resolver la revisión:** botón **Review changes** → *Approve* (está bien), *Request changes* (hay que corregir algo) o *Comment* (dudas). Dejá comentarios concretos ("en la línea X falta validar Y").

### Reglas generales
1. Cada caso de uso agrega su link en el `navbar` de `layout.html` (dentro del bloque de su rol) y reemplaza su tarjeta *Pendiente* en el panel del rol.
2. Nada hardcodeado: los valores de negocio (sueldo base, canon, plus) salen de `CostoFestival`.
3. Credenciales solo por variables de entorno; nunca commitear contraseñas.
4. Si falta una entidad o un campo, avisar en el grupo antes de modificarla.
