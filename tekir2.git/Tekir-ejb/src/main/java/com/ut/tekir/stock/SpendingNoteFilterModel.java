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

package com.ut.tekir.stock;

import com.ut.tekir.entities.Employee;
import com.ut.tekir.entities.Warehouse;
import com.ut.tekir.framework.DefaultDocumentFilterModel;

/**
 *
 * @author selman
 */
public class SpendingNoteFilterModel extends DefaultDocumentFilterModel{
    
    private Employee employee;
    private Warehouse warehouse;
    
	public Employee getEmployee() {
		return employee;
	}
	public void setEmployee(Employee employee) {
		this.employee = employee;
	}
	public Warehouse getWarehouse() {
		return warehouse;
	}
	public void setWarehouse(Warehouse warehouse) {
		this.warehouse = warehouse;
	}
    
}
