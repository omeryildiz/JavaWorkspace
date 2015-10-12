package tr.gov.tubitak.course.service;

import java.io.Serializable;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tr.gov.tubitak.course.dao.EmployeeDao;
import tr.gov.tubitak.course.entity.Employee;

public class EmployeeService implements Serializable {

	EmployeeDao employeeDao;
	
	public void setEmployeeDao(EmployeeDao employeeDao) {
		this.employeeDao = employeeDao;
	}

	@Transactional
	public void save(Employee employee) {
		employeeDao.save(employee);
	}

	@Transactional(readOnly = true)
	public List<Employee> list() {
		return employeeDao.list();
	}
}
