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

import javax.ejb.Stateful;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.datamodel.DataModel;

import com.ut.tekir.entities.Employee;
import com.ut.tekir.framework.BaseConsts;
import com.ut.tekir.framework.EntityHome;

@Stateful
@Name("employeeHome")
@Scope(value=ScopeType.CONVERSATION)
public class EmployeeHomeBean extends EntityHome<Employee> implements EmployeeHome<Employee> {
	
	@DataModel("employeeList")
    private List<Employee> entityList;
	
    @SuppressWarnings("unchecked")
	@Factory("employeeList")
    public void initEmployeeList() {
        log.debug("Employee Listesi hazırlanıyor...");
        
        entityList = getEntityManager().createQuery("select c from Employee c")
        .setMaxResults(100)
        //.setHint("org.hibernate.cacheable", true)
        .getResultList();
    }
	
    @Override
    public void createNew(){
        entity = new Employee();
        entity.setActive(true);
    }
    
    @Out(required=false)
    public Employee getEmployee() {
        return getEntity();
    }

    @In(required=false)
    public void setEmployee(Employee employee) {
    	setEntity( employee );
    }

	public List<Employee> getEntityList() {
		return entityList;
	}

	public void setEntityList(List<Employee> entityList) {
		this.entityList = entityList;
	}
	
	@Override
	public String save() {
		
		if (entity.getSsn().equals("") && entity.getPassportNo().equals("")) {
			facesMessages.add("Pasaport no veya TC Kimlik no girmelisiniz!");
			return BaseConsts.FAIL;
		}
		
		return super.save();
	}
	
}
