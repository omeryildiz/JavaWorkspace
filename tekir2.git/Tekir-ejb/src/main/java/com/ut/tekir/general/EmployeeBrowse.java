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

import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Destroy;

import com.ut.tekir.entities.Employee;

public interface EmployeeBrowse {

	@SuppressWarnings("unchecked")
	void searchEmployee();

	String getFirstName();

	void setFirstName(String firstName);

	String getLastName();

	void setLastName(String lastName);

	List<Employee> getEmployeeList();

	void setEmployeeList(List<Employee> employeeList);
	@Remove
    @Destroy
    void destroy();
	@Create
    void init();
}