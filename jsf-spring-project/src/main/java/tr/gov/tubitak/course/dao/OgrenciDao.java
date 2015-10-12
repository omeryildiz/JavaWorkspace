package tr.gov.tubitak.course.dao;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import tr.gov.tubitak.course.entity.Ogrenci;

@Repository
public class OgrenciDao implements Serializable{

	private static final long serialVersionUID = 1507378660020296636L;
	@PersistenceContext
	EntityManager entityManager;
	
	@Transactional
	public void save(Ogrenci ogrenci){
		entityManager.persist(ogrenci);
	}
	
	@Transactional
	public void remove(Ogrenci ogrenci) {
		ogrenci = entityManager.merge(ogrenci);
		entityManager.remove(ogrenci);
		
	}
	
	@Transactional
	public void update(Ogrenci ogrenci) {
		entityManager.merge(ogrenci);
	}
	
	@SuppressWarnings("unchecked")
	public List<Ogrenci> list(){
		return entityManager.createQuery("from Ogrenci").getResultList();
	}
}
