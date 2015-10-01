/*
 * Copyleft 2007-2011 Ozgur Yazilim A.S.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 * http://www.gnu.org/licenses/lgpl.html
 *
 * www.tekir.com.tr
 * www.ozguryazilim.com.tr
 *
 */

package com.ut.tekir.general;

import java.util.List;

import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import com.ut.tekir.entities.Employee;
import com.ut.tekir.framework.BrowserBase;

/**
 *
 * @author sozmen
 */
@Stateful
@Name("employeeBrowse")
@Scope(ScopeType.CONVERSATION)
public class EmployeeBrowseBean implements EmployeeBrowse{

	@In
	private EntityManager entityManager;
	
	private String firstName;
	
	private String lastName;
	
	private List<Employee> employeeList;
	
	@SuppressWarnings("unchecked")
	public void searchEmployee(){
		
		setEmployeeList(entityManager
				.createQuery(
						"select e from Employee e where e.firstName like :fname and e.lastName like :lname  and active = 1")
				.setParameter("fname", "%" + getFirstName() + "%")
				.setParameter("lname", "%" + getLastName() + "%")
				.setMaxResults(30)
				.setHint("org.hibernate.cacheable", true).getResultList());
	}
	
	
	
	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public List<Employee> getEmployeeList() {
		return employeeList;
	}

	public void setEmployeeList(List<Employee> employeeList) {
		this.employeeList = employeeList;
	}
	@Remove
	public void destroy() {
		// TODO Auto-generated method stub
	}
	@Create
	public void init() {
		// TODO Auto-generated method stub
	}
	
}
