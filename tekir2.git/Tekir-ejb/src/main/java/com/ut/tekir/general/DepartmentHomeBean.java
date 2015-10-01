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

import com.ut.tekir.entities.Department;
import com.ut.tekir.framework.EntityHome;

@Stateful
@Name("departmentHome")
@Scope(value = ScopeType.CONVERSATION)
public class DepartmentHomeBean extends EntityHome<Department> implements DepartmentHome<Department> {

	@DataModel("departmentList")
	private List<Department> entityList;

	@SuppressWarnings("unchecked")
	@Factory("departmentList")
	public void initDepartmentList() {
		log.debug("Department Listesi hazırlanıyor...");

		entityList = getEntityManager().createQuery(
				"select c from Department c").setMaxResults(100)
		// .setHint("org.hibernate.cacheable", true)
				.getResultList();
	}

	@Override
	public void createNew() {
		entity = new Department();
		entity.setActive(true);
	}

	@Out(required = false)
	public Department getDepartment() {
		return getEntity();
	}

	@In(required = false)
	public void setDepartment(Department department) {
		setEntity(department);
	}

	public List<Department> getEntityList() {
		return entityList;
	}

	public void setEntityList(List<Department> entityList) {
		this.entityList = entityList;
	}

}
