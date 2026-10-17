package com.unla.epicentrogourmet.services.implementation;

import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;

import com.unla.epicentrogourmet.dtos.LiquidacionDTO;
import com.unla.epicentrogourmet.dtos.LiquidacionUnidadDTO;
import com.unla.epicentrogourmet.entities.CostoFestival;
import com.unla.epicentrogourmet.entities.Festival;
import com.unla.epicentrogourmet.entities.Personal;
import com.unla.epicentrogourmet.entities.UnidadVenta;
import com.unla.epicentrogourmet.services.ILiquidacionService;

/**
 * Liquidacion de haberes. Los montos (sueldo base, plus por categoria, plus por antiguedad)
 * salen de CostoFestival. Cada tipo de empleado calcula su propio plus (Personal.calcularPlus):
 * la cocina cobra plus por categoria y el cajero cobra antiguedad. La antiguedad se mide hasta
 * la fecha de fin del festival.
 */
@Service("liquidacionService")
public class LiquidacionService implements ILiquidacionService {

	private static final long TOPE_PRIMER_TRAMO = 15;
	private static final long TOPE_SEGUNDO_TRAMO = 30;

	@Override
	public double calcularFactor(long diasFestival) {
		if (diasFestival <= TOPE_PRIMER_TRAMO) {
			return 0.5;
		}
		if (diasFestival <= TOPE_SEGUNDO_TRAMO) {
			return 1.0;
		}
		return (diasFestival / TOPE_PRIMER_TRAMO + 1) * 0.5;
	}

	@Override
	public double calcularSueldo(Personal personal, Festival festival) {
		return calcularLiquidacion(personal, festival).getTotal();
	}

	@Override
	public LiquidacionDTO calcularLiquidacion(Personal personal, Festival festival) {
		CostoFestival costos = festival.getCostos();
		if (costos == null) {
			throw new IllegalStateException("El festival '" + festival.getNombre() + "' no tiene costos cargados");
		}
		double sueldoBase = costos.getSueldoBase();
		double plus = personal.calcularPlus(costos, festival.getFechaFin());
		double subtotal = sueldoBase + plus;
		long dias = festival.getDuracionEnDias();
		double factor = calcularFactor(dias);
		return new LiquidacionDTO(personal.getId(), personal.getNombreCompleto(), personal.getDni(),
				personal.getRolDescripcion(), personal.isActivo(), festival.getNombre(), dias, sueldoBase,
				personal.getConceptoPlus(festival.getFechaFin()), plus, subtotal, factor, subtotal * factor);
	}

	@Override
	public LiquidacionUnidadDTO liquidarUnidad(UnidadVenta unidad) {
		Festival festival = unidad.getFestival();
		List<LiquidacionDTO> liquidaciones = unidad.getPersonal().stream()
				.map(p -> calcularLiquidacion(p, festival))
				.sorted(Comparator.comparing(LiquidacionDTO::getEmpleado))
				.toList();
		double total = liquidaciones.stream().mapToDouble(LiquidacionDTO::getTotal).sum();
		long dias = festival.getDuracionEnDias();
		return new LiquidacionUnidadDTO(unidad.getNombreComercial(), festival.getNombre(), dias,
				calcularFactor(dias), liquidaciones, total);
	}

	@Override
	public List<LiquidacionUnidadDTO> liquidarUnidades(List<UnidadVenta> unidades) {
		return unidades.stream().map(this::liquidarUnidad).toList();
	}
}
