package tr.gov.tubitak.course.sample;

import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import tr.gov.tubitak.course.entity.Customer;
import tr.gov.tubitak.course.service.CustomerService;
import tr.gov.tubitak.course.service.SampleService;

public class SpringStandalone {
	public static void main(String[] args) {
		ApplicationContext context = new AnnotationConfigApplicationContext(SampleConfig.class);

		CustomerService cust = context.getBean("customerService", CustomerService.class);
		List<Customer> list = cust.list();
		for (Customer customer : list) {
			System.out.println(customer.getName());
		}
		
		System.out.println("*********");
		
		SampleService sampleService = context.getBean("sampleService", SampleService.class);
		System.out.println(sampleService.getInfo());
		
		
	}
}