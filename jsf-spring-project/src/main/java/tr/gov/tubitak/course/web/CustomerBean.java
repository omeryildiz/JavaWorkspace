package tr.gov.tubitak.course.web;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.faces.bean.RequestScoped;
import javax.inject.Named;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import tr.gov.tubitak.course.entity.Customer;
import tr.gov.tubitak.course.service.CustomerService;

//@Controller("customerBean")
@RequestScoped
@Named
public class CustomerBean implements Serializable{
	
	private Customer customer = new Customer();
	private List<Customer> list;
	
	@Autowired CustomerService customerService;
	
	
	@PostConstruct
	public void init(){
		this.list = customerService.list();
	}
	
	public void save(){
		customerService.save(customer);
		customer = new Customer();
		init();
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public List<Customer> getList() {
		return list;
	}

	public void setList(List<Customer> list) {
		this.list = list;
	}
}
