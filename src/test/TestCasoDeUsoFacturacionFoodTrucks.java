package test;

import datos.FoodTruck;
import datos.UnidadVenta;
import negocio.UnidadVentaABM;

public class TestCasoDeUsoFacturacionFoodTrucks {

    public static void main(String[] args) {

        long idFestival = 1; // ajustar según el ID del festival

        UnidadVentaABM abm = new UnidadVentaABM();

        System.out.printf("Facturación de FoodTrucks del festival id=%d%n", idFestival);

        for (UnidadVenta u : abm.traerFoodTrucksConPedidosDeFestival(idFestival)) {

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
    }
}