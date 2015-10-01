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

import com.ut.tekir.entities.Employee;
import com.ut.tekir.framework.IEntityHome;

/**
 * 
 * @author sozmen
 *
 */
public interface EmployeeHome<E> extends IEntityHome<E>{
	Employee getEmployee();
    void setEmployee(Employee employee);
    
    void initEmployeeList();
	
}
