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

package com.ut.tekir.finance;

import javax.ejb.Local;

import com.ut.tekir.entities.PromissoryFromContactPayroll;
import com.ut.tekir.entities.PromissoryFromContactPayrollDetail;
import com.ut.tekir.entities.PromissoryNote;
import com.ut.tekir.framework.IEntityBase;

/**
 * @author yigit
 *
 */
@Local
public interface PromissoryFromContactPayrollHome<E> extends IEntityBase<E> {

	void init();
	PromissoryFromContactPayroll getPromissoryFromContactPayroll();
	void setPromissoryFromContactPayroll(PromissoryFromContactPayroll promissoryFromContactPayroll);

	void createNewLine();
	void deleteLine(PromissoryFromContactPayrollDetail detail);
	void deleteLine(Integer ix);
	void findPromissory(int rowKey);
	void popupPromissoryCreate();

	void selectClientPromissory(PromissoryNote promissory);
	
	Boolean getIsEditable();
	void setIsEditable(Boolean isEditable);

	void manualFlush();
    void pdf(PromissoryFromContactPayroll payroll);

}