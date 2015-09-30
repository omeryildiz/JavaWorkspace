package tr.gov.tubitak.course.web;


import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.UserTransaction;

import tr.gov.tubitak.course.entity.Customer;

@Named
@SessionScoped
public class CustomerBean implements Serializable {
	private Customer customer = new Customer();
	private List<Customer> customerList;
	
	@PersistenceContext
	EntityManager entityManager;

	@Inject
	UserTransaction utx;
	
	
	@PostConstruct
	public void updateList() {
		this.setCustomerList(entityManager.createQuery("from Customer").getResultList());
	}
	
	public void save() throws Exception {
		utx.begin();
		entityManager.persist(customer);
		utx.commit();
		updateList();
		this.customer = new Customer();
		System.out.println("Kayit yapildi");

	}

	public void remove(Customer selectedCustomer) throws Exception {
		utx.begin();
		selectedCustomer = entityManager.merge(selectedCustomer);
		entityManager.remove(selectedCustomer);
		utx.commit();
		updateList();

	}

	public void select(Customer selectedProduct) throws Exception {
		this.customer = selectedProduct;

	}

	public void update() throws Exception {
		utx.begin();
		entityManager.merge(this.customer);
		utx.commit();
		updateList();
		customer = new Customer();

	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public List<Customer> getCustomerList() {
		return customerList;
	}

	public void setCustomerList(List<Customer> customerList) {
		this.customerList = customerList;
	}

}
