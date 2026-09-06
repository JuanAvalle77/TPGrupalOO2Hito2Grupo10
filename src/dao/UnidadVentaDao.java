package dao;

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import datos.Festival;
import datos.UnidadVenta;

public class UnidadVentaDao {

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

	public int agregar(UnidadVenta objeto) {
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

	public UnidadVenta traer(long idUnidadVenta) {
		UnidadVenta objeto = null;
		try {
			iniciaOperacion();
			objeto = session.get(UnidadVenta.class, idUnidadVenta);
		} finally {
			session.close();
		}
		return objeto;
	}

	public List<UnidadVenta> traer() {
		List<UnidadVenta> lista = null;
		try {
			iniciaOperacion();
			Query<UnidadVenta> query = session.createQuery("from UnidadVenta u order by u.nombre asc", UnidadVenta.class);
			lista = query.getResultList();
		} finally {
			session.close();
		}
		return lista;
	}

	/**
	 * Caso de Uso: FoodTrucks de un festival junto con la cantidad de platos
	 * que ofrece cada uno. Combina Herencia (FoodTruck) + Uno a Muchos
	 * (UnidadVenta -> Plato).
	 */
	public List<UnidadVenta> traerFoodTrucksDeFestival(Festival festival) {
		List<UnidadVenta> lista = null;
		try {
			iniciaOperacion();
			String hql = "select distinct ft from FoodTruck ft "
					+ "left join fetch ft.platos "
					+ "where ft.festival = :festival "
					+ "order by ft.nombre asc";
			Query<UnidadVenta> query = session.createQuery(hql, UnidadVenta.class);
			query.setParameter("festival", festival);
			lista = query.getResultList();
		} finally {
			session.close();
		}
		return lista;
	}

	/**
	 * Caso de Uso: precio de venta promedio de los platos que ofrecen los
	 * FoodTrucks de un festival. Combina Herencia (FoodTruck) + Uno a Muchos
	 * (UnidadVenta -> Plato), esta vez con un agregado (avg) resuelto en HQL
	 * en vez de calcularlo en Java.
	 */
	public Double traerPrecioPromedioPlatosFoodTrucksDeFestival(Festival festival) {
		Double promedio = null;
		try {
			iniciaOperacion();
			String hql = "select avg(p.precioVenta) from FoodTruck ft join ft.platos p "
					+ "where ft.festival = :festival";
			Query<Double> query = session.createQuery(hql, Double.class);
			query.setParameter("festival", festival);
			promedio = query.uniqueResult();
		} finally {
			session.close();
		}
		return promedio;
	}

	//Metodo Total facturado por cada FoodTruck de un festival  
	// Por Federico Acosta Rosales 
	
	public List<UnidadVenta> traerFoodTrucksConPedidosDeFestival(long idFestival) {
	    List<UnidadVenta> lista = null;
	    try {
	        iniciaOperacion();

	        String hql = "select distinct ft from FoodTruck ft "
	                + "left join fetch ft.pedidosRealizados p "
	                + "left join fetch p.detalles d "
	                + "left join fetch d.plato "
	                + "where ft.festival.idFestival = :idFestival "
	                + "order by ft.nombre asc";

	        Query<UnidadVenta> query = session.createQuery(hql, UnidadVenta.class);
	        query.setParameter("idFestival", idFestival);

	        lista = query.getResultList();

	    } finally {
	        session.close();
	    }
	    return lista;
	}
	
	/**
	 * Caso de Uso: Puestos Desarmables de un festival junto con la cantidad de
	 * platos que ofrece cada uno. Combina Herencia (PuestoDesarmable) + Uno a
	 * Muchos (Festival -> UnidadVenta).
	 */
	public List<UnidadVenta> traerPuestosDesarmablesDeFestival(long idFestival) {
		List<UnidadVenta> lista = null;
		try {
			iniciaOperacion();
			String hql = "select distinct pd from PuestoDesarmable pd "
					+ "left join fetch pd.platos "
					+ "where pd.festival.idFestival = :idFestival "
					+ "order by pd.nombre asc";
			Query<UnidadVenta> query = session.createQuery(hql, UnidadVenta.class);
			query.setParameter("idFestival", idFestival);
			lista = query.getResultList();
		} finally {
			session.close();
		}
		return lista;
	}
}
