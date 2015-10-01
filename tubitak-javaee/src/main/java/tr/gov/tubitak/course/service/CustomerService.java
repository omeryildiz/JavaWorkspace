package tr.gov.tubitak.course.service;

import java.io.Serializable;
import java.util.List;

import javax.ejb.Stateful;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

import tr.gov.tubitak.course.dao.CustomerDAO;
import tr.gov.tubitak.course.entity.Customer;
import tr.gov.tubitak.course.util.GenericDAO;
import tr.gov.tubitak.course.util.GenericService;

@Stateful
public class CustomerService extends GenericService<Customer> implements Serializable{

	@Inject CustomerDAO customerDao;
	
	@Override
	public GenericDAO getDao() {
		return customerDao;
	}

	@Override
	public void save(Customer e) {
		if(e.getName().startsWith("A"))
			return;
		super.save(e);
	}
	

}
