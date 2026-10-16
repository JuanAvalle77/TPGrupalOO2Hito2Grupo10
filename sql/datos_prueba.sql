-- =====================================================================
-- Datos de prueba - TP Grupal OO2 2026 - Hito 2 (Epicentro Gourmet)
--
-- Uso: la aplicacion crea las tablas sola (spring.jpa.hibernate.ddl-auto=update).
--   1) Arrancar la aplicacion una vez (crea las tablas) y detenerla.
--   2) Ejecutar este script sobre la base (ver README).
-- El script se puede ejecutar varias veces: borra los datos y los vuelve a cargar.
-- Las fechas son relativas a HOY, asi siempre hay un festival activo.
--
-- Usuarios de prueba:
--   admin                          / admin123   (ADMIN)
--   juan.perez@epicentro.com       / resp123    (RESPONSABLE de El Fueguito, festival activo)
--   rocio.fernandez@epicentro.com  / resp123    (RESPONSABLE de La Empanada Andante)
--   pedro.gomez@epicentro.com      / resp123    (RESPONSABLE de La Carpa de Pedro)
--   lucia.gomez@epicentro.com      / emp123     (EMPLEADO - cajera)
--   marcos.diaz@epicentro.com      / emp123     (EMPLEADO - cocina)
--   diego.silva@epicentro.com      / emp123     (EMPLEADO - cajero)
--   valentina.ortiz@epicentro.com  / emp123     (EMPLEADO - lavaplatos, trabajo en 2 festivales)
-- =====================================================================

SET FOREIGN_KEY_CHECKS = 0;
DELETE FROM usuario;
DELETE FROM cierre_caja;
DELETE FROM detalles_pedido;
DELETE FROM pedido;
DELETE FROM plato;
DELETE FROM unidad_personal;
DELETE FROM food_truck;
DELETE FROM puesto_desarmable;
DELETE FROM unidad_venta;
DELETE FROM cajero;
DELETE FROM cocinero;
DELETE FROM personal;
DELETE FROM festival;
DELETE FROM costo_festival;
SET FOREIGN_KEY_CHECKS = 1;

-- ---------------------------------------------------------------------
-- Costos de cada festival (nada hardcodeado en el codigo)
-- ---------------------------------------------------------------------
INSERT INTO costo_festival (id, costo_porm2, plus_conexion_electrica, costo_por_minuto_montaje, sueldo_base,
                            plus_antiguedad_anual, plus_cocinero, plus_cocinero_ayudante, plus_lavaplatos) VALUES
(1, 500, 2000, 10, 100000, 5000, 150000, 100000, 50000),
(2, 500, 2000, 10, 100000, 5000, 150000, 100000, 50000),
(3, 500, 2000, 10, 100000, 5000, 150000, 100000, 50000);

-- ---------------------------------------------------------------------
-- Festivales: 1 activo, 1 pasado (38 dias), 1 futuro
-- ---------------------------------------------------------------------
INSERT INTO festival (id, nombre, temporada, fecha_inicio, fecha_fin, costos_id) VALUES
(1, 'Festival del Choripan', 'Otonio', DATE_SUB(CURDATE(), INTERVAL 3 DAY), DATE_ADD(CURDATE(), INTERVAL 9 DAY), 1),
(2, 'Feria del Chocolate', 'Invierno', DATE_SUB(CURDATE(), INTERVAL 120 DAY), DATE_SUB(CURDATE(), INTERVAL 83 DAY), 2),
(3, 'Festival Cervecero', 'Primavera', DATE_ADD(CURDATE(), INTERVAL 60 DAY), DATE_ADD(CURDATE(), INTERVAL 79 DAY), 3);

-- ---------------------------------------------------------------------
-- Personal (el id es compartido entre personal, cajero y cocinero)
-- ---------------------------------------------------------------------
INSERT INTO personal (id, nombre, apellido, dni, fecha_nacimiento, fecha_ingreso, email, contacto, activo) VALUES
(1, 'Juan', 'Perez', 30111222, '1990-05-10', '2023-03-01', 'juan.perez@epicentro.com', '1155550001', 1),
(2, 'Lucia', 'Gomez', 32555111, '1995-03-20', '2025-06-01', 'lucia.gomez@epicentro.com', '1155550002', 1),
(3, 'Marcos', 'Diaz', 33111222, '1992-07-12', '2025-01-10', 'marcos.diaz@epicentro.com', '1155550003', 1),
(4, 'Rocio', 'Fernandez', 34222111, '1988-04-15', '2022-01-05', 'rocio.fernandez@epicentro.com', '1155550004', 1),
(5, 'Diego', 'Silva', 30987654, '1988-11-02', '2024-08-20', 'diego.silva@epicentro.com', '1155550005', 1),
(6, 'Pedro', 'Gomez', 29224300, '1987-03-17', '2021-02-10', 'pedro.gomez@epicentro.com', '1155550006', 1),
(7, 'Valentina', 'Ortiz', 36123456, '2000-09-10', '2026-03-01', 'valentina.ortiz@epicentro.com', '1155550007', 1),
(8, 'Kenji', 'Tanaka', 28555444, '1985-01-28', '2020-05-15', 'kenji.tanaka@epicentro.com', '1155550008', 1),
(9, 'Ana', 'Torres', 37444555, '1997-06-18', '2024-02-01', 'ana.torres@epicentro.com', '1155550009', 1),
(10, 'Camila', 'Rios', 35666777, '1993-02-25', '2025-11-01', 'camila.rios@epicentro.com', '1155550010', 1),
-- empleado dado de baja (baja logica)
(11, 'Nahuel', 'Lopez', 38111000, '1999-12-01', '2026-01-15', 'nahuel.lopez@epicentro.com', '1155550011', 0);

INSERT INTO cocinero (id, rol_cocina) VALUES
(1, 'COCINERO'), (3, 'COCINERO_AYUDANTE'), (4, 'COCINERO'), (7, 'LAVAPLATOS'), (8, 'COCINERO'), (10, 'COCINERO');

INSERT INTO cajero (id, turno) VALUES
(2, 'NOCHE'), (5, 'MANANA'), (6, 'NOCHE'), (9, 'MANANA'), (11, 'NOCHE');

-- ---------------------------------------------------------------------
-- Unidades de venta (con su responsable)
-- ---------------------------------------------------------------------
INSERT INTO unidad_venta (id, nombre_comercial, superficie, codigo, festival_id, responsable_id) VALUES
(1, 'El Fueguito', 15.5, 'FT00000001', 1, 1),
(2, 'La Empanada Andante', 12, 'FT00000002', 1, 4),
(3, 'La Carpa de Pedro', 8.5, 'PD00000001', 1, 6),
(4, 'Sushi On Wheels', 18, 'FT00000003', 2, 8),
(5, 'Dulces del Sur', 10, 'PD00000002', 2, 9),
(6, 'Cerveza y Pizza', 14, 'FT00000004', 3, 10);

INSERT INTO food_truck (id, patente, requiere_electricidad) VALUES
(1, 'AA123BB', 1), (2, 'AB456CD', 0), (4, 'AC789EF', 1), (6, 'AD111GG', 1);

INSERT INTO puesto_desarmable (id, cantidad_carpas, tiempo_montaje) VALUES
(3, 2, 30), (5, 3, 45);

-- Personal asignado (staff). Valentina trabajo en dos festivales.
INSERT INTO unidad_personal (unidad_id, personal_id) VALUES
(1, 1), (1, 2), (1, 3),
(2, 4), (2, 5),
(3, 6), (3, 7),
(4, 8), (4, 11),
(5, 9), (5, 7),
(6, 10);

-- ---------------------------------------------------------------------
-- Platos
-- ---------------------------------------------------------------------
INSERT INTO plato (id, nombre, precio_venta, costo_produccion, unidad_venta_id) VALUES
(1, 'Choripan', 3500, 1500, 1),
(2, 'Papas Fritas', 2500, 800, 1),
(3, 'Hamburguesa', 4200, 1800, 1),
(4, 'Empanada de Carne', 800, 300, 2),
(5, 'Empanada de Pollo', 800, 300, 2),
(6, 'Empanada de Verdura', 750, 280, 2),
(7, 'Milanesa', 4000, 2000, 3),
(8, 'Ensalada Rusa', 3000, 400, 3),
(9, 'Combo California x10', 6000, 2500, 4),
(10, 'Combo Philadelphia x10', 6500, 2800, 4),
(11, 'Alfajor', 1200, 400, 5),
(12, 'Brownie', 1500, 500, 5),
(13, 'Pizza Muzzarella', 3000, 1200, 6);

-- ---------------------------------------------------------------------
-- Pedidos con sus items (festival activo y festival pasado)
-- ---------------------------------------------------------------------
INSERT INTO pedido (id, fecha, festival_id, unidad_venta_id) VALUES
(1, DATE_SUB(CURDATE(), INTERVAL 2 DAY), 1, 1),
(2, DATE_SUB(CURDATE(), INTERVAL 1 DAY), 1, 1),
(3, DATE_SUB(CURDATE(), INTERVAL 1 DAY), 1, 2),
(4, DATE_SUB(CURDATE(), INTERVAL 2 DAY), 1, 3),
(5, CURDATE(), 1, 1),
(6, DATE_SUB(CURDATE(), INTERVAL 100 DAY), 2, 4),
(7, DATE_SUB(CURDATE(), INTERVAL 95 DAY), 2, 5);

INSERT INTO detalles_pedido (id, cantidad, plato_id, pedido_id) VALUES
(1, 3, 1, 1), (2, 2, 2, 1),
(3, 5, 1, 2), (4, 2, 3, 2),
(5, 6, 4, 3), (6, 4, 5, 3), (7, 2, 6, 3),
(8, 3, 7, 4), (9, 2, 8, 4),
(10, 4, 2, 5),
(11, 3, 9, 6), (12, 2, 10, 6),
(13, 10, 11, 7), (14, 6, 12, 7);

-- Cierre de caja de ejemplo (Lucia, ayer, en El Fueguito: 5 x 3500 + 2 x 4200)
INSERT INTO cierre_caja (id, fecha, total_recaudado, cajero_id, unidad_venta_id) VALUES
(1, DATE_SUB(CURDATE(), INTERVAL 1 DAY), 25900, 2, 1);

-- ---------------------------------------------------------------------
-- Usuarios (contrasenias con hash BCrypt, ver arriba las claves de prueba)
-- ---------------------------------------------------------------------
INSERT INTO usuario (id, username, password, enabled, rol, personal_id) VALUES
(1, 'admin', '$2a$10$HPuF9loHq2nAiiE2yIk3ReMSYPf2JxINzr.j6UWUntYkSZCeYu/AG', 1, 'ADMIN', NULL),
(2, 'juan.perez@epicentro.com', '$2a$10$GoQ//eYtcNZrCGG7yur0XOnIMFEDUsfKYAihJIRxHBf2weF0/WuOW', 1, 'RESPONSABLE', 1),
(3, 'rocio.fernandez@epicentro.com', '$2a$10$GoQ//eYtcNZrCGG7yur0XOnIMFEDUsfKYAihJIRxHBf2weF0/WuOW', 1, 'RESPONSABLE', 4),
(4, 'pedro.gomez@epicentro.com', '$2a$10$GoQ//eYtcNZrCGG7yur0XOnIMFEDUsfKYAihJIRxHBf2weF0/WuOW', 1, 'RESPONSABLE', 6),
(5, 'kenji.tanaka@epicentro.com', '$2a$10$GoQ//eYtcNZrCGG7yur0XOnIMFEDUsfKYAihJIRxHBf2weF0/WuOW', 1, 'RESPONSABLE', 8),
(6, 'ana.torres@epicentro.com', '$2a$10$GoQ//eYtcNZrCGG7yur0XOnIMFEDUsfKYAihJIRxHBf2weF0/WuOW', 1, 'RESPONSABLE', 9),
(7, 'camila.rios@epicentro.com', '$2a$10$GoQ//eYtcNZrCGG7yur0XOnIMFEDUsfKYAihJIRxHBf2weF0/WuOW', 1, 'RESPONSABLE', 10),
(8, 'lucia.gomez@epicentro.com', '$2a$10$3QaXxk3U.kGG4hztoC/i7eFuObow5PrT4MstIVcyB.etT8OQ5Tr8e', 1, 'EMPLEADO', 2),
(9, 'marcos.diaz@epicentro.com', '$2a$10$3QaXxk3U.kGG4hztoC/i7eFuObow5PrT4MstIVcyB.etT8OQ5Tr8e', 1, 'EMPLEADO', 3),
(10, 'diego.silva@epicentro.com', '$2a$10$3QaXxk3U.kGG4hztoC/i7eFuObow5PrT4MstIVcyB.etT8OQ5Tr8e', 1, 'EMPLEADO', 5),
(11, 'valentina.ortiz@epicentro.com', '$2a$10$3QaXxk3U.kGG4hztoC/i7eFuObow5PrT4MstIVcyB.etT8OQ5Tr8e', 1, 'EMPLEADO', 7),
-- empleado dado de baja: no puede iniciar sesion
(12, 'nahuel.lopez@epicentro.com', '$2a$10$3QaXxk3U.kGG4hztoC/i7eFuObow5PrT4MstIVcyB.etT8OQ5Tr8e', 0, 'EMPLEADO', 11);
