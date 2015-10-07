package tr.gov.tubitak.course.util;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

public abstract class GenericDAO<E> extends BaseBean<E> implements Serializable{
	
	@PersistenceContext(type = PersistenceContextType.EXTENDED, unitName="primary")
	EntityManager entityManager;

	public List<E> getList() {
		return entityManager.createQuery(getQuery()).getResultList();
	}

	public String getQuery(){
		return "from " + getClassInstance().getName();
	}

	public void save(E e) {
		entityManager.persist(e);
	}

	public void remove(E e) {
		e = entityManager.merge(e);
		entityManager.remove(e);
		
	}

	public void update(E e) {
		entityManager.merge(e);
	}
	
	public EntityManager getEntityManager() {
		return entityManager;
	}
}
