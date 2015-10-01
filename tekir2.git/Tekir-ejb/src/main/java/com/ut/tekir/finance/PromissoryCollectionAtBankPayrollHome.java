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

import com.ut.tekir.entities.PromissoryCollectedAtBankPayroll;
import com.ut.tekir.entities.PromissoryNote;
import com.ut.tekir.entities.PromissoryToBankPayrollDetail;
import com.ut.tekir.framework.IEntityBase;

/**
 * @author yigit
 *
 */
@Local
public interface PromissoryCollectionAtBankPayrollHome<E> extends IEntityBase<E> {

	void init();

	PromissoryCollectedAtBankPayroll getPromissoryCollectionAtBankPayroll();
	void setPromissoryCollectionAtBankPayroll(PromissoryCollectedAtBankPayroll promissoryCollectionAtBankPayroll);

	void createNewLine();

	void deleteLine(PromissoryToBankPayrollDetail detail);

	void deleteLine(Integer ix);

	void popupPromissorySelect(int clientOrFirm);

	void selectClientPromissory(PromissoryNote promissory);
	
	Boolean getIsEditable();
	void setIsEditable(Boolean isEditable);

	void manualFlush();
    void pdf(PromissoryCollectedAtBankPayroll payroll);

}