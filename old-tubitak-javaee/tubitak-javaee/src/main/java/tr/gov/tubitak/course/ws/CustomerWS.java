package tr.gov.tubitak.course.ws;

import java.io.Serializable;

import javax.inject.Inject;
import javax.jws.WebService;

import tr.gov.tubitak.course.entity.Customer;
import tr.gov.tubitak.course.service.CustomerService;

@WebService
public class CustomerWS implements Serializable{

	@Inject CustomerService customerService;
	
	public void save(Customer customer){
		customerService.save(customer);
	}
}
