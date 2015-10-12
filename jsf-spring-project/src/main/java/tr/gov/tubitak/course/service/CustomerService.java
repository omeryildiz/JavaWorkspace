package tr.gov.tubitak.course.service;

import java.io.Serializable;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tr.gov.tubitak.course.dao.CustomerDao;
import tr.gov.tubitak.course.entity.Customer;

@Service
@Scope(scopeName = "prototype")
public class CustomerService implements Serializable {

	@Autowired
	CustomerDao customerDao;

	@Transactional
	public void save(Customer customer) {
		customerDao.save(customer);
	}

	@Transactional(readOnly = true)
	public List<Customer> list() {
		return customerDao.list();
	}
}
