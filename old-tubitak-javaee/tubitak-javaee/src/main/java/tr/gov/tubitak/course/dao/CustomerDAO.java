package tr.gov.tubitak.course.dao;

import java.io.Serializable;
import java.util.List;

import javax.ejb.Stateful;

import tr.gov.tubitak.course.entity.Customer;
import tr.gov.tubitak.course.util.GenericDAO;

@Stateful
public class CustomerDAO extends GenericDAO<Customer> implements Serializable{

	public List<Customer> findByName(String name){
		return getEntityManager()
			.createQuery("from Customer where name=:name")
			.setParameter("name", name)
			.getResultList();
	}
	
}
