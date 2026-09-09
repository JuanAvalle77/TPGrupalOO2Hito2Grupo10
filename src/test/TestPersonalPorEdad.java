package test;

import datos.*;

import negocio.*;
import java.util.*;


public class TestPersonalPorEdad {

    public static void main(String[] args) {
        
        PersonalABM abmPersonal = new PersonalABM();
        int edadMin = 25;
        int edadMax = 40;

        System.out.println("=== PERSONAL CON EDAD ENTRE " + edadMin + " Y " + edadMax + " AÑOS ===");
        List<Personal> lista = abmPersonal.traerPersonalPorRangoEdad(edadMin, edadMax);

        for (Personal p : lista) {
            String funcion = p.getClass().getSimpleName();
            double sueldo = p.calcularSueldo();

            System.out.println("-> [" + funcion + "] " + p.getApellido() + ", " + p.getNombre()
                               + " | F.Nac: " + p.getFechaNacimiento()
                               + " | Sueldo Calculado: $" + sueldo);
        }
    }
}