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

import javax.ejb.Local;

import com.ut.tekir.framework.IEntityHome;

/**
 *
 * @author haky
 */
@Local
public interface ExpenseTypeHome<ExpenseType> extends IEntityHome<ExpenseType> {
    
	ExpenseType getExpenseType();
    void setExpenseType(ExpenseType expenseType);
    
    void initExpenseTypeList();
    
}
