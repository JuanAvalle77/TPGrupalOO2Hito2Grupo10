package test;

/**
 * Corre, en el orden correcto, todas las clases de carga de datos de prueba.
 * Util para levantar toda la base de una sola vez en lugar de correr cada
 * clase por separado. Correr una sola vez, sobre una base recien creada
 * (DROP DATABASE + CREATE DATABASE).
 */
public class TestCargarTodosLosDatosPrueba {

	public static void main(String[] args) {
		TestCargarDatosPrueba.main(args);
		TestCargarMasFoodTrucksPrueba.main(args);
		TestCargarStaffPrueba.main(args);
		TestCargarMasDatosPrueba.main(args);
		TestCargarMuchosFoodTrucksPrueba.main(args);
		TestCargarPuestoDesarmablePrueba.main(args);
		System.out.println("=== Toda la carga de datos de prueba se completo con exito ===");
	}
}
