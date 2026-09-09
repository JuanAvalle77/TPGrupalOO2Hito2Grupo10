package dao;

import java.time.*;

import java.util.List;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import datos.Personal;

public class PersonalDao {

	private static Session session;
	private Transaction tx;

	private void iniciaOperacion() throws HibernateException {
		session = HibernateUtil.getSessionFactory().openSession();
		tx = session.beginTransaction();
	}

	private void manejaExcepcion(HibernateException he) throws HibernateException {
		tx.rollback();
		throw new HibernateException("ERROR en la capa de acceso a datos", he);
	}

	public int agregar(Personal objeto) {
		int id = 0;
		try {
			iniciaOperacion();
			id = Integer.parseInt(session.save(objeto).toString());
			tx.commit();
		} catch (HibernateException he) {
			manejaExcepcion(he);
		} finally {
			session.close();
		}
		return id;
	}

	public Personal traer(long idPersonal) {
		Personal objeto = null;
		try {
			iniciaOperacion();
			objeto = session.get(Personal.class, idPersonal);
		} finally {
			session.close();
		}
		return objeto;
	}

	public List<Personal> traer() {
		List<Personal> lista = null;
		try {
			iniciaOperacion();
			Query<Personal> query = session.createQuery("from Personal p order by p.apellido asc", Personal.class);
			lista = query.getResultList();
		} finally {
			session.close();
		}
		return lista;
	}
	
	
	///Santiago Agarzúa
	@SuppressWarnings("unchecked")
    public List<Personal> traerPersonalPorRangoEdad(int edadMin, int edadMax) throws HibernateException {
        List<Personal> lista = null;
        try {
            iniciaOperacion();
            
            // Calculamos los rangos de fechas límite a partir de las edades pasadas por parámetro
            LocalDate fechaHasta = LocalDate.now().minusYears(edadMin);
            LocalDate fechaDesde = LocalDate.now().minusYears(edadMax + 1).plusDays(1);

            // Consulta HQL filtrando por fechaNacimiento usando BETWEEN
            String hql = "from Personal p where p.fechaNacimiento between :desde and :hasta order by p.apellido asc, p.nombre asc";
            
            lista = session.createQuery(hql)
                           .setParameter("desde", fechaDesde)
                           .setParameter("hasta", fechaHasta)
                           .list();
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
        return lista;
    }
}
	

