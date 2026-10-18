package dao;

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import datos.Festival;
import datos.UnidadVenta;
import datos.PuestoDesarmable;

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
	
	public List<UnidadVenta> traerFoodTrucksConPedidosDeFestival(Festival festival) {
	    List<UnidadVenta> lista = null;
	    try {
	        iniciaOperacion();

	        String hql = "select distinct ft from FoodTruck ft "
	                + "left join fetch ft.pedidosRealizados p "
	                + "left join fetch p.detalles d "
	                + "left join fetch d.plato "
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
	
	// Segunda consulta: obtiene la facturación total de cada FoodTruck de un Festival mediante HQL.
	public List<Object[]> facturacionFoodTrucksDeFestival(Festival festival) {
	    
	    List<Object[]> lista = null;

	    try {
	        iniciaOperacion();
	        String hql = "select ft, sum(d.cantidad * d.plato.precioVenta) "
	                + "from FoodTruck ft "
	                + "join ft.pedidosRealizados p "
	                + "join p.detalles d "
	                + "where ft.festival = :festival "
	                + "group by ft "
	                + "order by ft.nombre asc";
	        Query<Object[]> query = session.createQuery(hql, Object[].class);
	        query.setParameter("festival", festival);
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
	
	public List<UnidadVenta> traerPuestosDesarmablesDeFestival(Festival f) {

	    List<UnidadVenta> lista = null;

	    try {

	        iniciaOperacion();

	        String hql = "select distinct pd from PuestoDesarmable pd "
	                   + "left join fetch pd.platos "
	                   + "where pd.festival = :festival "
	                   + "order by pd.nombre asc";

	        Query<UnidadVenta> query = session.createQuery(hql, UnidadVenta.class);

	        query.setParameter("festival", f);

	        lista = query.getResultList();

	    } finally {

	        session.close();

	    }

	    return lista;
	}
	
	public double traerSuperficieTotal(Festival f) {
			double resultado = 0;
			try {
				iniciaOperacion();
				String hql = "select sum(u.superficie) "
				+ "from UnidadVenta u "
				+ "where u.festival = :festival";

		        Query<Double> query = session.createQuery(hql, Double.class);
		        query.setParameter("festival", f);

		        resultado = query.getSingleResult();
				
			} finally {
				session.close();
			}
			return resultado;
	}
	
	public double traerSuperficieTotalPuestosDesarmables(Festival f) {

	    double resultado = 0;
	    try {
	        iniciaOperacion();
	        String hql = "select sum(pd.superficie) "
	                   + "from PuestoDesarmable pd "
	                   + "where pd.festival = :festival";
	        Query<Double> query = session.createQuery(hql, Double.class);
	        query.setParameter("festival", f);
	        resultado = query.getSingleResult();
	    } finally {
	        session.close();
	    }

	    return resultado;
	}
	///Santiago Agarzúa
	public UnidadVenta traerUnidadVentaConStaff(long idUnidadVenta) throws HibernateException {
	       UnidadVenta objeto = null;
	       try {
	           iniciaOperacion();
	           // HQL: u.personal es el Set<Personal> mapeado en UnidadVenta.hbm.xml
	           // left join fetch trae la unidad aunque todavia no tenga personal asignado
	           // (con inner join, una unidad sin staff no aparece en el resultado)
	           String hql = "from UnidadVenta u left join fetch u.personal p where u.idUnidadVenta = :id";

	           objeto = (UnidadVenta) session.createQuery(hql)
	                                          .setParameter("id", idUnidadVenta)
	                                          .uniqueResult();
	       } finally {
	            if (session != null && session.isOpen()) {
	                session.close();
	            }
	        }
	        return objeto;
	    }
}
