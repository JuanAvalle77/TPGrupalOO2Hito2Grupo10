package test;
import datos.*;
import negocio.*;

public class TestConsultarPersonalUnidadVenta {

    public static void main(String[] args) {
        UnidadVentaABM abmUnidad = new UnidadVentaABM();
        long idUnidad = 1; // Reemplazar por el ID de la Unidad de Venta a consultar

        UnidadVenta unidad = abmUnidad.traerUnidadVentaConStaff(idUnidad);

        if (unidad != null) {
            System.out.println("=======================================================");
            System.out.println("UNIDAD DE VENTA: " + unidad.getNombre() + " (Código: " + unidad.getCodigo() + ")");
            System.out.println("=======================================================");
            System.out.println("PERSONAL ASIGNADO:");

            // Recorremos la colección 'staff' traída por Hibernate
            for (Personal p : unidad.getPersonal()) {
                
                String rol = "";
                String detallePlus = "";

                // Verificamos el tipo de instancia concreta creada por Hibernate
                if (p instanceof Cocinero) {
                    Cocinero c = (Cocinero) p;
                    rol = "Cocinero";
                    detallePlus = "Plus Esp: $" + c.getPlusEspecialidad();
                } else if (p instanceof Cajero) {
                    Cajero c = (Cajero) p;
                    rol = "Cajero";
                    detallePlus = "Plus Turno: $" + c.getPlus();
                }

                // Invocación polimórfica del sueldo
                double sueldoFinal = p.calcularSueldo();

                System.out.println(" -> [" + rol + "] " + p.getApellido() + ", " + p.getNombre() 
                                   + " | DNI: " + p.getDni() 
                                   + " | Sueldo Base: $" + p.getSueldoBase() 
                                   + " | " + detallePlus 
                                   + " => SUELDO CALCULADO: $" + sueldoFinal);
            }
            System.out.println("=======================================================");
        } else {
            System.out.println("No se encontró la Unidad de Venta con ID: " + idUnidad);
        }
    }
}