package com.unla.epicentrogourmet.services.implementation;

import java.io.ByteArrayOutputStream;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import com.unla.epicentrogourmet.dtos.LiquidacionDTO;
import com.unla.epicentrogourmet.dtos.ReciboSueldoDTO;
import com.unla.epicentrogourmet.entities.Festival;
import com.unla.epicentrogourmet.entities.Personal;
import com.unla.epicentrogourmet.services.ILiquidacionService;

/** Consulta los festivales trabajados por el empleado y genera sus recibos. */
@Service
public class ReciboSueldoService {
	private final SesionService sesionService;
	private final ILiquidacionService liquidacionService;

	public ReciboSueldoService(SesionService sesionService, ILiquidacionService liquidacionService) {
		this.sesionService = sesionService;
		this.liquidacionService = liquidacionService;
	}

	@Transactional(readOnly = true)
	public List<ReciboSueldoDTO> listarRecibos() {
		Personal personal = sesionService.getPersonalActual();
		Map<Long, Festival> festivales = new LinkedHashMap<>();
		personal.getUnidades().forEach(unidad -> festivales.putIfAbsent(unidad.getFestival().getId(), unidad.getFestival()));
		return festivales.values().stream()
				.sorted(Comparator.comparing(Festival::getFechaInicio).reversed())
				.map(festival -> new ReciboSueldoDTO(festival, liquidacionService.calcularLiquidacion(personal, festival)))
				.toList();
	}

	@Transactional(readOnly = true)
	public byte[] generarRecibo(Long festivalId) {
		ReciboSueldoDTO recibo = listarRecibos().stream()
				.filter(item -> item.getFestivalId().equals(festivalId))
				.findFirst()
				.orElseThrow(() -> new IllegalArgumentException("No tenés un recibo para ese festival"));
		try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
			Document document = new Document();
			PdfWriter.getInstance(document, output);
			document.open();
			LiquidacionDTO liquidacion = recibo.getLiquidacion();
			document.add(new Paragraph("RECIBO DE SUELDO"));
			document.add(new Paragraph("Empleado: " + liquidacion.getEmpleado()));
			document.add(new Paragraph("DNI: " + liquidacion.getDni()));
			document.add(new Paragraph("Rol: " + liquidacion.getRol()));
			document.add(new Paragraph("Festival: " + recibo.getNombreFestival()));
			document.add(new Paragraph("Período: " + recibo.getFechaInicio() + " al " + recibo.getFechaFin()));
			document.add(new Paragraph("Duración: " + liquidacion.getDiasFestival() + " días"));
			document.add(new Paragraph("Sueldo base: " + liquidacion.getSueldoBase()));
			document.add(new Paragraph(liquidacion.getConceptoPlus() + ": " + liquidacion.getPlus()));
			document.add(new Paragraph("Subtotal: " + liquidacion.getSubtotal()));
			document.add(new Paragraph("Factor aplicado: " + liquidacion.getFactor()));
			document.add(new Paragraph("Total: " + liquidacion.getTotal()));
			document.close();
			return output.toByteArray();
		} catch (DocumentException ex) {
			throw new IllegalStateException("No se pudo generar el recibo PDF", ex);
		} catch (java.io.IOException ex) {
			throw new IllegalStateException("No se pudo cerrar el PDF del recibo", ex);
		}
	}
}
