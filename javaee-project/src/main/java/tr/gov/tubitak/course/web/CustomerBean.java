package tr.gov.tubitak.course.web;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.Stateful;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.UserTransaction;

import tr.gov.tubitak.course.entity.Country;
import tr.gov.tubitak.course.entity.Customer;
import tr.gov.tubitak.course.entity.Product;

@Named
@SessionScoped
@Stateful
public class CustomerBean implements Serializable {
	private Customer customer = new Customer();

	@PersistenceContext
	EntityManager entityManager;
	private List<Customer> customerList;
	private List<Country> countryList;
	private List<Product> productList;
	// @Inject
	// UserTransaction utx;

	@PostConstruct
	public void init() {
		loadCustomerList();
		this.setCountryList(entityManager.createQuery("from Country").getResultList());
		this.setProductList(entityManager.createQuery("from Product").getResultList());
	}

	private void loadCustomerList() {
		this.setCountryList(entityManager.createQuery("from Customer").getResultList());
	}

	// @PostConstruct
	// public void updateList() {
	// this.setCustomerList(entityManager.createQuery("from
	// Customer").getResultList());
	// }

	public void save() throws Exception {
		// utx.begin();
		entityManager.persist(customer);
		// utx.commit();
		// updateList();
		loadCustomerList();
		this.customer = new Customer();
		System.out.println("Kayit yapildi");

	}

	public void remove(Customer selectedCustomer) throws Exception {
		// utx.begin();
		selectedCustomer = entityManager.merge(selectedCustomer);
		entityManager.remove(selectedCustomer);
		loadCustomerList();
		// utx.commit();
		// updateList();

	}

	public void select(Customer selectedProduct) throws Exception {
		this.customer = selectedProduct;

	}

	public void update() throws Exception {
		// utx.begin();
		entityManager.merge(this.customer);
		// utx.commit();
		loadCustomerList();
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

	public List<Country> getCountryList() {
		return countryList;
	}

	public void setCountryList(List<Country> countryList) {
		this.countryList = countryList;
	}

	public List<Product> getProductList() {
		return productList;
	}

	public void setProductList(List<Product> productList) {
		this.productList = productList;
	}

}
