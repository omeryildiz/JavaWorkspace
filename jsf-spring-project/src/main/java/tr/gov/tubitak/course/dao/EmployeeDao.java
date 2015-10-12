package tr.gov.tubitak.course.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.transaction.annotation.Transactional;

import tr.gov.tubitak.course.entity.Employee;

public class EmployeeDao {

	@PersistenceContext
	EntityManager entityManager;
	
	@Transactional
	public void save(Employee employee){
		entityManager.persist(employee);
	}
	
	@SuppressWarnings("unchecked")
	public List<Employee> list(){
		return entityManager.createQuery("from Employee").getResultList();
	}
}
