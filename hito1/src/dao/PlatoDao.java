package dao;

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import datos.Plato;

public class PlatoDao {

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

	public int agregar(Plato objeto) {
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

	public Plato traer(long idPlato) {
		Plato objeto = null;
		try {
			iniciaOperacion();
			objeto = session.get(Plato.class, idPlato);
		} finally {
			session.close();
		}
		return objeto;
	}

	public List<Plato> traer() {
		List<Plato> lista = null;
		try {
			iniciaOperacion();
			Query<Plato> query = session.createQuery("from Plato p order by p.nombre asc", Plato.class);
			lista = query.getResultList();
		} finally {
			session.close();
		}
		return lista;
	}
}
