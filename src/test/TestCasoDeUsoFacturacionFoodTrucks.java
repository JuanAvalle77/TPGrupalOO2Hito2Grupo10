package test;

import datos.FoodTruck;
import datos.UnidadVenta;
import negocio.UnidadVentaABM;
import datos.Festival;
import negocio.FestivalABM;

public class TestCasoDeUsoFacturacionFoodTrucks {

    public static void main(String[] args) {
    	
    	FestivalABM festivalABM = new FestivalABM();
    	Festival festival = festivalABM.traer(1);

        long idFestival = 1; // ajustar según el ID del festival

        UnidadVentaABM abm = new UnidadVentaABM();

        System.out.printf("Facturación de FoodTrucks del festival id=%d%n", idFestival);

        for (UnidadVenta u : abm.traerFoodTrucksConPedidosDeFestival(festival)) {

            FoodTruck ft = (FoodTruck) u;

            double facturacion = 0;

            for (var pedido : ft.getPedidosRealizados()) {
                facturacion += pedido.calcularTotal();
            }

            System.out.printf("- %s (codigo=%s, patente=%s) -> $%.2f%n",
                    ft.getNombre(),
                    ft.getCodigo(),
                    ft.getPatente(),
                    facturacion);
        }
        
        System.out.println("\nFacturación calculada mediante HQL:");

        for (Object[] resultado : abm.facturacionFoodTrucksDeFestival(festival)) {

            FoodTruck ft = (FoodTruck) resultado[0];
            Double facturacion = (Double) resultado[1];

            System.out.printf("- %s -> $%.2f%n",
                    ft.getNombre(),
                    facturacion);
        }
    }
}