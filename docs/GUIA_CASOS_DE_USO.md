# Guía para implementar los casos de uso (Hito 2)

Esta guía está pensada para **pasársela a tu IA** (ChatGPT, Claude, Copilot, etc.) junto con el repositorio. No hace falta saber programación web: la base ya está armada y cada caso de uso sigue siempre la misma receta.

> **Cómo usarla con una IA:** abrí el repo en tu IDE o pegale a la IA (1) la sección *1. Contexto técnico*, (2) la receta de la sección *2*, y (3) **solo** la ficha de tu caso de uso de la sección *4*. Pedile que genere el código siguiendo los archivos que ya existen como ejemplo (`HomeController`, `FestivalService`, `templates/home/index.html`).

---

## 1. Contexto técnico

Aplicación **Spring Boot 3 (Java 17+) + Thymeleaf + Spring Data JPA + MySQL + Spring Security**, en arquitectura de capas:

| Capa | Carpeta (`src/main/java/com/unla/epicentrogourmet/`) | Qué hace |
|---|---|---|
| Entidades | `entities/` | Clases JPA (ya creadas, no cambiarlas sin avisar) |
| Repositorios | `repositories/` | Interfaces `JpaRepository`: acá van las consultas (`@Query` en JPQL o métodos derivados) |
| Servicios | `services/` (interfaz) y `services/implementation/` | Lógica de negocio y validaciones |
| Controladores | `controllers/` | Reciben la petición web y devuelven una vista (`ModelAndView`) |
| DTOs | `dtos/` | Objetos de reportes que **no se persisten** |
| Vistas | `src/main/resources/templates/` | HTML con Thymeleaf; todas usan `layout.html` |
| Rutas/vistas | `helpers/ViewRouteHelper.java` | Constantes con las rutas y nombres de vista |

**Entidades ya disponibles:** `Festival`, `CostoFestival`, `UnidadVenta` (abstracta) → `FoodTruck`, `PuestoDesarmable`; `Personal` (abstracta) → `Cajero`, `Cocinero`; `Plato`, `Pedido`, `DetallesPedido`, `CierreCaja`, `Usuario`. Ver el diagrama en el README.

**Seguridad (ya hecha):** tres zonas con prefijo de ruta. Si tu ruta empieza con el prefijo del rol, **queda protegida sola**:
- `/admin/**` → solo `ADMIN` · `/responsable/**` → solo `RESPONSABLE` · `/empleado/**` → solo `EMPLEADO`.
- Para saber quién está logueado: inyectar `SesionService` (`getUsuarioActual()`, `getPersonalActual()`, y para responsables `getUnidadesDelResponsable()`).

**Datos y valores de negocio:** nada hardcodeado. Sueldo base, canon, plus, etc. salen de `Festival.getCostos()` (entidad `CostoFestival`).

**Usuarios de prueba** (cargar `sql/datos_prueba.sql`, ver README): `admin/admin123`, `juan.perez@epicentro.com/resp123` (responsable de *El Fueguito*), `lucia.gomez@epicentro.com/emp123` (cajera), `marcos.diaz@epicentro.com/emp123` (cocina), `valentina.ortiz@epicentro.com/emp123` (trabajó en dos festivales).

**Datos de prueba clave:** Festival 1 *Festival del Choripan* (en curso, 13 días), Festival 2 *Feria del Chocolate* (finalizado, 38 días), Festival 3 *Festival Cervecero* (futuro). Unidades: 1 *El Fueguito* (FoodTruck, resp. Juan Perez), 2 *La Empanada Andante* (FoodTruck), 3 *La Carpa de Pedro* (Puesto) — las tres en el festival 1; 4 *Sushi On Wheels* y 5 *Dulces del Sur* en el festival 2; 6 en el festival 3.

---

## 2. Receta para agregar un caso de uso (siempre igual)

Ejemplo de referencia ya hecho: la página de inicio (`HomeController` + `FestivalService` + `templates/home/index.html`).

1. **Repositorio** (`repositories/IXxxRepository.java`): agregar el método de consulta que necesites.
2. **Servicio**: crear `services/IXxxService.java` (interfaz) y `services/implementation/XxxService.java` (`@Service`) con la lógica. Las validaciones lanzan `IllegalArgumentException` con un mensaje claro.
3. **Controlador**: crear **un controlador propio por caso de uso** (ej. `CanonController` con `@Controller @RequestMapping("/responsable/canon")`) para no pisarse con los demás en el mismo archivo. Agregar los métodos `@GetMapping` / `@PostMapping`. Devolver `ModelAndView` con la vista y los datos (`mAV.addObject("clave", valor)`). Las rutas van bajo el prefijo del rol (ej. `/responsable/empleados`).
4. **Vista**: crear `templates/<rol>/<pagina>.html` copiando la estructura de `templates/home/index.html` (los `th:replace` del layout al principio y al final). Los formularios usan `th:action="@{/ruta}" method="post"`: Spring agrega solo el token CSRF.
5. **Navegación**: en `templates/<rol>/index.html` reemplazar la tarjeta *Pendiente* de tu caso de uso (cada caso tiene la suya) por `<a class="cu-card" th:href="@{/ruta}">…</a>`.
6. **Constantes**: sumar tus rutas/vistas a `ViewRouteHelper`.
7. **Probar**: levantar la app, entrar con el usuario del rol y recorrer el flujo. Idealmente agregar un test en `src/test/java`.
8. **Git** (obligatorio): rama `CU<Funcionalidad><Nombre>`, commits claros, `gh pr create --base main` (o desde la web), descripción del PR con lo hecho (entidades/consultas, servicios, controladores, vistas) y cómo probarlo. Pegar el link del PR en la tabla del README.

**Reglas generales:** no tocar archivos de otros casos de uso salvo para agregar tu tarjeta/link; no escribir contraseñas ni valores de negocio en el código; si falta una entidad o campo, **avisar en el grupo** antes de modificarla.

---

## 3. Reparto

| Integrante | Casos de uso (PRs) |
|---|---|
| **Juan Avalle** | ABM de empleados + mail · Filtros de empleados · Cálculo de canon · Liquidación de haberes · Unidades con mayor canon · Rentabilidad neta (y entre fechas) |
| **Santiago Agarzúa** | Registro de unidad con responsable · Alta de platos · Plato estrella |
| **Franco Luzardo** | Ver festivales y unidades · Ranking de unidades · Reporte de recaudación · Ver mi información (empleado) |
| **Federico Acosta** | Recibo de sueldo en PDF · Dónde trabajo hoy · Cerrar caja |

**Orden recomendado:** Juan mergea primero *canon* y *liquidación de haberes* (los usan otros). Mientras tanto todos pueden avanzar con lo que no depende de eso.

**Mínimos de la consigna que se cumplen:** 1 mail (ABM de empleados) · DTOs de reporte (mayor canon, recaudación, plato estrella) · 1 PDF (recibo) · cada uno tiene 2 o más casos de uso y 2 validaciones de carga.

---

## 4. Fichas de los casos de uso

### JUAN AVALLE

**CU ABM de empleados + mail** · rama `CUABMEmpleadosJuanAvalle` · rol Responsable · *(grupo: envío de mail)*
- **Qué hace:** el responsable da de alta, modifica y da de baja empleados de **su** unidad; ve la lista de empleados activos.
- **Rutas:** `/responsable/empleados` (lista), `/responsable/empleados/nuevo` (formulario), `/responsable/empleados/{id}/editar`, `POST .../baja/{id}`, `POST .../reactivar/{id}`.
- **Alta:** formulario con nombre, apellido, DNI, fecha de nacimiento, fecha de ingreso, email, contacto y tipo (Cajero con turno, o Cocina con rol). Al guardar: crear el `Personal` (Cajero o Cocinero), asignarlo a la unidad del responsable (`unidad.getPersonal().add(...)`), crear un `Usuario` con rol `EMPLEADO`, `username` = email y una **contraseña aleatoria** guardada con BCrypt (`PasswordEncoder`), y **enviar un mail** con `MailService.enviar(email, asunto, texto)` con el usuario y la contraseña generada.
- **Baja lógica:** no se borra: `personal.setActivo(false)` y `usuario.setEnabled(false)`. La lista y los filtros solo muestran `activo = true`.
- **Reactivar:** método específico que vuelve a `activo = true` / `enabled = true` (si se intenta dar de alta un DNI/email eliminado, usar este método en vez de crear otro).
- **Validaciones de carga (Juan):** persona **mayor de edad** (`Personal.esMayorDeEdad()`); **email con formato válido y único**.
- **Listo cuando:** el responsable `juan.perez@…` da de alta un empleado, el mail aparece en la consola (o llega si hay `MAIL_*`), el nuevo empleado puede iniciar sesión, y al darlo de baja desaparece de la lista y no puede loguearse.

**CU Filtros de empleados** · rama `CUFiltrosEmpleadosJuanAvalle` · Responsable
- **Qué hace:** en la lista de empleados, filtrar por uno o varios atributos (nombre/apellido, DNI, tipo, turno o rol de cocina) **solo entre los activos**.
- **Cómo:** `GET /responsable/empleados?apellido=&dni=&tipo=` — armar la consulta con `@Query` con parámetros opcionales (`(:apellido is null or lower(p.apellido) like lower(concat('%', :apellido, '%')))`) y `activo = true`, siempre acotada a la unidad del responsable.
- **Listo cuando:** combinando filtros devuelve el subconjunto correcto y nunca muestra dados de baja.

**CU Cálculo de canon** · rama `CUCalculoCanonJuanAvalle` · Responsable
- **Qué hace:** el responsable ve el monto de canon de su unidad en su festival.
- **Regla:** ya está en `UnidadVenta.calcularCanon(CostoFestival)`: FoodTruck = `superficie × costoPorM2` + plus eléctrico si lo requiere; Puesto = `superficie × costoPorM2 − tiempoMontaje × costoPorMinutoMontaje`. Crear `CanonService.calcularCanon(UnidadVenta)` que use `unidad.getFestival().getCostos()` y una vista `/responsable/canon`.
- **Verificación:** *El Fueguito* = 9750 · *La Empanada Andante* = 6000 · *La Carpa de Pedro* = 3950.

**CU Liquidación de haberes** · rama `CULiquidacionHaberesJuanAvalle` · Responsable
- **Qué hace:** calcular el sueldo de cada empleado de la unidad por festival; vista `/responsable/haberes`.
- **Reglas** (valores desde `CostoFestival`: `sueldoBase`, `plusAntiguedadAnual`, `getPlusCategoria(rol)`):
  - Cocina: `sueldoBase + plusCategoria(rol)`. Cajero: `sueldoBase + antigüedad × plusAntiguedadAnual` (antigüedad en años completos **a la fecha de fin del festival**: `Personal.getAntiguedadEnAnios(fecha)`).
  - Factor según los días del festival (`Festival.getDuracionEnDias()`): hasta 15 días → **0,5**; de 16 a 30 → **1,0**; más de 30 → `(días/15 + 1) × 0,5` (división entera).
  - Total = factor × (base del rol).
- **Crear:** `LiquidacionService.calcularSueldo(Personal, Festival)` — **lo usa Federico** para el recibo en PDF, así que mantener esa firma.
- **Verificación:** *Valentina Ortiz* (lavaplatos) en el festival 2 (38 días) = **225.000** · *Ana Torres* (cajera, ingreso 2024-02-01) en el festival 2 = **165.000**.

**CU Unidades con mayor canon** · rama `CUMayorCanonJuanAvalle` · Admin · *(DTO de reporte)*
- **Qué hace:** el admin elige un festival y un número `x`; ve las `x` unidades que más canon pagan.
- **Ruta:** `/admin/mayor-canon?festivalId=1&x=3`. Crear `dtos/ReporteMayoresCanonDTO` (nombreComercial, codigo, tipo, canon) — **no es entidad**. Ordenar por canon descendente y cortar en `x`.
- **Verificación** (festival 1, x=3): El Fueguito 9750, La Empanada Andante 6000, La Carpa de Pedro 3950.

**CU Rentabilidad neta (y entre dos fechas)** · rama `CURentabilidadNetaJuanAvalle` · Responsable
- **Qué hace:** ganancia de **una unidad en un festival** = `total de pedidos − costo de producción de los platos − sueldos del personal − canon`. Segunda parte: lo mismo **limitado a pedidos entre dos fechas**.
- **Cómo:** usa `Pedido.calcularTotal()`, `Pedido.calcularCostoPlatos()`, `LiquidacionService` y `CanonService`. Rutas `/responsable/rentabilidad` y `/responsable/rentabilidad?desde=&hasta=`.
- **Decisión a documentar:** entre fechas, prorratear sueldos y canon por `días del rango / días del festival` (anotarlo en el PR).

### SANTIAGO AGARZÚA

**CU Registro de Unidad de Venta con su Responsable** · rama `CURegistroUnidadSantiagoAgarzua` · Público
- **Qué hace:** desde la página de inicio, cualquier persona registra una unidad de venta y a su responsable; se crea el usuario del responsable.
- **Rutas:** `GET /registro` (formulario, ya es pública) y `POST /registro`. Agregar el botón "Registrar unidad" en `templates/home/index.html` (hay un `TODO`).
- **Formulario:** festival (lista de festivales), tipo de unidad (FoodTruck: patente y si requiere electricidad; Puesto: carpas y minutos de montaje), nombre comercial, superficie, código; datos del responsable (nombre, apellido, DNI, fecha de nacimiento, fecha de ingreso, email, tipo de personal) y **usuario y contraseña obligatorios**.
- **Al guardar:** crear el responsable (`Cajero` o `Cocinero`), la unidad con `setResponsable(...)`, agregarlo a `unidad.getPersonal()`, y un `Usuario` con rol `RESPONSABLE` y contraseña con BCrypt (`PasswordEncoder`).
- **Validaciones (Santiago):** **código** de 10 caracteres con formato `2 letras mayúsculas + 8 dígitos` y **único** (`existsByCodigo`); `username` único.
- **Listo cuando:** se registra una unidad nueva, aparece en la base y el responsable puede iniciar sesión y entra a `/responsable`.

**CU Alta de platos** · rama `CUAltaPlatosSantiagoAgarzua` · Responsable
- **Qué hace:** el responsable agrega platos al menú de **su** unidad y ve la lista.
- **Rutas:** `/responsable/platos` (lista) y `/responsable/platos/nuevo` (formulario: nombre, precio de venta, costo de producción).
- **Validación:** **precio de venta mayor al costo de producción** y valores positivos.
- **Listo cuando:** el plato nuevo aparece solo en la unidad del responsable logueado.

**CU Plato estrella** · rama `CUPlatoEstrellaSantiagoAgarzua` · Responsable · *(DTO de reporte)*
- **Qué hace:** dada una unidad y un festival, devuelve el plato que más se pidió. Crear `dtos/PlatoDTO` (nombre, precio de venta, cantidad de pedidos en los que aparece, unidades vendidas).
- **Criterio (documentarlo en el PR):** el plato con más **unidades vendidas** (suma de `DetallesPedido.cantidad`); la cantidad de pedidos queda como dato y como desempate.
- **Cómo:** consulta agrupando `DetallesPedido` por plato para esa unidad y festival, ordenando descendente y tomando el primero (`LIMIT 1` o `findFirst`). Ruta `/responsable/plato-estrella`.
- **Verificación:** *El Fueguito*, festival 1 → **Choripan** (8 unidades en 2 pedidos; Papas Fritas: 6 unidades en 2 pedidos).

### FRANCO LUZARDO

**CU Ver festivales y unidades de un festival** · rama `CUVerFestivalesUnidadesFrancoLuzardo` · Admin
- **Qué hace:** el admin ve la lista de festivales y, al elegir uno, las unidades de venta de ese festival (nombre, código, tipo, superficie, responsable).
- **Rutas:** `/admin/festivales` y `/admin/festivales/{id}/unidades`. Agregar `findByFestivalId` en `IUnidadVentaRepository`.
- **Validaciones (Franco):** **período del festival válido** (`fechaFin` no anterior a `fechaInicio`; implementarlo en `FestivalService.validarPeriodo`) y **superficie mayor a 0** (anotación `@Positive` en `UnidadVenta.superficie`).
- **Listo cuando:** el festival 1 muestra sus 3 unidades.

**CU Ranking de unidades por recaudación** · rama `CURankingUnidadesFrancoLuzardo` · Admin
- **Qué hace:** para un festival, la lista de unidades ordenada de mayor a menor recaudación (suma de `Pedido.calcularTotal()` de cada unidad).
- **Ruta:** `/admin/ranking?festivalId=1`. Crear un DTO simple (`RankingUnidadDTO`: nombre, código, recaudación).
- **Verificación** (festival 1): El Fueguito **51.400** · La Carpa de Pedro **18.000** · La Empanada Andante **9.500**.

**CU Reporte de recaudación** · rama `CUReporteRecaudacionFrancoLuzardo` · Responsable · *(DTO de reporte)*
- **Qué hace:** dado un festival, la recaudación total. Crear `dtos/ReporteVentaDTO` (festival, cantidad de pedidos, recaudación total).
- **Ruta:** `/responsable/reporte-recaudacion?festivalId=1`.
- **Verificación** (festival 1, todo el festival): **78.900**.

**CU Ver mi información (empleado)** · rama `CUMiInformacionFrancoLuzardo` · Empleado
- **Qué hace:** el empleado logueado ve los festivales y unidades en las que trabajó.
- **Ruta:** `/empleado/mi-informacion`. Usar `SesionService.getPersonalActual().getUnidades()` y de cada unidad su `getFestival()`.
- **Verificación:** `valentina.ortiz@…` ve dos filas (festival 1 y festival 2).

### FEDERICO ACOSTA

**CU Recibo de sueldo en PDF** · rama `CUReciboSueldoFedericoAcosta` · Empleado · *(grupo: generación de PDF)*
- **Qué hace:** el empleado ve el sueldo que recibió en cada festival y descarga el recibo en PDF.
- **Rutas:** `/empleado/sueldos` (lista de festivales con el monto) y `/empleado/sueldos/{festivalId}/recibo` (descarga).
- **Cómo:** el monto viene de `LiquidacionService.calcularSueldo(personal, festival)` (lo implementa Juan; hasta que se mergee, usá un valor fijo de prueba y después lo conectás). El PDF con **OpenPDF** (ya está en el `pom.xml`: clases `com.lowagie.text.Document`, `PdfWriter`, `Paragraph`). Devolver `ResponseEntity<byte[]>` con `Content-Type: application/pdf` y `Content-Disposition: attachment; filename="recibo.pdf"`.
- **Contenido del recibo:** datos del empleado, festival y fechas, rol, sueldo base, plus/antigüedad, factor aplicado y total.
- **Listo cuando:** `valentina.ortiz@…` descarga un PDF por cada festival con el total correcto (225.000 en el festival 2).

**CU Dónde trabajo hoy** · rama `CUDondeTrabajoHoyFedericoAcosta` · Empleado
- **Qué hace:** muestra si el empleado tiene que trabajar hoy en un festival activo y en qué unidad.
- **Ruta:** `/empleado/hoy`. Buscar entre `personal.getUnidades()` la que tenga `festival.estaActivo(LocalDate.now())`. Si no hay, mostrar "Hoy no tenés festival asignado".
- **Verificación:** `lucia.gomez@…` → *El Fueguito* (festival 1 en curso); `valentina.ortiz@…` → *La Carpa de Pedro*.

**CU Cerrar caja (cajero)** · rama `CUCerrarCajaFedericoAcosta` · Empleado
- **Qué hace:** el **cajero** cierra la caja del día: se guarda un `CierreCaja` con el total recaudado por su unidad ese día (suma de `Pedido.calcularTotal()` con `fecha = hoy`).
- **Ruta:** `/empleado/cerrar-caja` (GET muestra el total del día, POST confirma). Solo cajeros: si el usuario es `Cocinero`, mostrar error.
- **Validaciones (Federico):** **un cierre por cajero por día** (hay restricción única en la tabla) y **los pedidos solo se cargan con la fecha del día**: crear `PedidoService.crear(...)` que rechace fechas distintas de hoy y cantidades ≤ 0 en los items.
- **Listo cuando:** `lucia.gomez@…` cierra la caja y un segundo intento el mismo día muestra un mensaje de error.
