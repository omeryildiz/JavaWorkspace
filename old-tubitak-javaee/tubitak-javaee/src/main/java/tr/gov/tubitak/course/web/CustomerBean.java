package tr.gov.tubitak.course.web;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import tr.gov.tubitak.course.entity.Customer;
import tr.gov.tubitak.course.service.CustomerService;
import tr.gov.tubitak.course.util.GenericService;

@Named
@SessionScoped
public class CustomerBean extends GenericBean<Customer> implements Serializable{
	
	@Inject CustomerService customerService;
	
	@Override
	public GenericService<Customer> getService() {
		// TODO Auto-generated method stub
		return customerService;
	}
	
	@PostConstruct
	public void init(){
		super.init();
	}

	
}
