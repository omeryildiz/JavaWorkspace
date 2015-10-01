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

import com.ut.tekir.entities.ExpenseType;
import com.ut.tekir.framework.EntityHome;
/**
 * 
 * @author Nebıoglu
 *
 */
@Stateful
@Name("expenseTypeHome")
@Scope(value = ScopeType.CONVERSATION)
public class ExpenseTypeHomeBean extends EntityHome<ExpenseType> implements ExpenseTypeHome<ExpenseType> {

	@DataModel("expenseTypeList")
	private List<ExpenseType> entityList;

	@SuppressWarnings("unchecked")
	@Factory("expenseTypeList")
	public void initExpenseTypeList() {
		log.debug("ExpenseType Listesi hazırlanıyor...");

		entityList = getEntityManager().createQuery(
				"select c from ExpenseType c").setMaxResults(100)
		// .setHint("org.hibernate.cacheable", true)
				.getResultList();
	}

	@Override
	public void createNew() {
		entity = new ExpenseType();
		entity.setActive(true);
	}

	@Out(required = false)
	public ExpenseType getExpenseType() {
		return getEntity();
	}

	@In(required = false)
	public void setExpenseType(ExpenseType expenseType) {
		setEntity(expenseType);
	}

	public List<ExpenseType> getEntityList() {
		return entityList;
	}

	public void setEntityList(List<ExpenseType> entityList) {
		this.entityList = entityList;
	}

}
