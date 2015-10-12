package tr.gov.tubitak.course.dao;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import tr.gov.tubitak.course.entity.Customer;

@Repository
public class CustomerDao implements Serializable{

	@PersistenceContext
	EntityManager entityManager;
	
	@Transactional
	public void save(Customer customer){
		entityManager.persist(customer);
	}
	
	@SuppressWarnings("unchecked")
	public List<Customer> list(){
		return entityManager.createQuery("from Customer").getResultList();
	}
}
