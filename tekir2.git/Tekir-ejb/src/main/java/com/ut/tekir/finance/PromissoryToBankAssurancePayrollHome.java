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

import com.ut.tekir.entities.PromissoryNote;
import com.ut.tekir.entities.PromissoryToBankAssurancePayroll;
import com.ut.tekir.entities.PromissoryToBankAssurancePayrollDetail;
import com.ut.tekir.framework.IEntityBase;

/**
 * @author yigit
 *
 */
@Local
public interface PromissoryToBankAssurancePayrollHome<E> extends IEntityBase<E> {

	void init();
	PromissoryToBankAssurancePayroll getPromissoryToBankAssurancePayroll();

	void setPromissoryToBankAssurancePayroll(PromissoryToBankAssurancePayroll promissoryToBankAssurancePayroll);

	void clearBankAccount();

	void createNewLine();
	void deleteLine(PromissoryToBankAssurancePayrollDetail detail);
	void deleteLine(Integer ix);

	void popupPromissorySelect(int clientOrFirm);

	void popupPromissoryCreate();

	void selectClientPromissory(PromissoryNote promissory);
	
	Boolean getIsEditable();
	void setIsEditable(Boolean isEditable);

	void manualFlush();

}