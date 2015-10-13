package tr.gov.tubitak.course.util;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public abstract class GenericDAO<E> extends BaseBean<E> implements Serializable{
	
	private static final long serialVersionUID = -4740585103917128293L;
	@PersistenceContext
	EntityManager entityManager;

	@Transactional
	public List<E> getList() {
		return entityManager.createQuery(getQuery()).getResultList();
	}

	public String getQuery(){
		return "from " + getClassInstance().getName();
	}

	@Transactional
	public void save(E e) {
		entityManager.persist(e);
	}

	@Transactional
	public void remove(E e) {
		e = entityManager.merge(e);
		entityManager.remove(e);
		
	}

	@Transactional
	public void update(E e) {
		entityManager.merge(e);
	}
	
	public EntityManager getEntityManager() {
		return entityManager;
	}
}
