package tr.gov.tubitak.course.web;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;

import tr.gov.tubitak.course.entity.Employee;
import tr.gov.tubitak.course.service.EmployeeService;

public class EmployeeBean implements Serializable{
	
	private Employee employee = new Employee();
	private List<Employee> list;
	
	EmployeeService employeeService;
	
	public void setEmployeeService(EmployeeService employeeService) {
		this.employeeService = employeeService;
	}
	
	@PostConstruct
	public void init(){
		this.list = employeeService.list();
	}
	
	public void save(){
		employeeService.save(employee);
		employee = new Employee();
		init();
	}

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public List<Employee> getList() {
		return list;
	}

	public void setList(List<Employee> list) {
		this.list = list;
	}
}
