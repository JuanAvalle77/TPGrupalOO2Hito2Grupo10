package negocio;

import java.time.LocalDate;
import java.util.List;

import dao.FestivalDao;
import datos.Festival;

public class FestivalABM {

	FestivalDao dao = new FestivalDao();

	public int agregar(String nombre, String temporada, LocalDate fechaInicio, LocalDate fechaFin) {
		Festival f = new Festival(nombre, temporada, fechaInicio, fechaFin);
		return dao.agregar(f);
	}

	public Festival traer(long idFestival) {
		return dao.traer(idFestival);
	}

	public List<Festival> traer() {
		return dao.traer();
	}
}
