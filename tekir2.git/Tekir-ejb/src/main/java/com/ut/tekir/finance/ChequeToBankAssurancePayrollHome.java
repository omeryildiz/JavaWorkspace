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

import com.ut.tekir.entities.Cheque;
import com.ut.tekir.entities.ChequeToBankAssurancePayroll;
import com.ut.tekir.entities.ChequeToBankAssurancePayrollDetail;
import com.ut.tekir.framework.IEntityBase;

/**
 * @author yigit
 *
 */
@Local
public interface ChequeToBankAssurancePayrollHome<E> extends IEntityBase<E> {

	void init();
	ChequeToBankAssurancePayroll getChequeToBankAssurancePayroll();
	void setChequeToBankAssurancePayroll(ChequeToBankAssurancePayroll chequeToBankAssurancePayroll);

	void clearBankAccount();

	void createNewLine();

	void deleteLine(ChequeToBankAssurancePayrollDetail detail);

	void deleteLine(Integer ix);

	void popupChequeSelect(int clientOrFirm);

	void popupChequeCreate();

	void selectClientCheque(Cheque cheque);
	
    Boolean getIsEditable();
    void setIsEditable(Boolean isEditable);

    void findCheque(int rowKey);
	
	void manualFlush();
}