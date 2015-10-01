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
import com.ut.tekir.entities.ChequeToAccountPaymentPayroll;
import com.ut.tekir.entities.ChequeToAccountPaymentPayrollDetail;
import com.ut.tekir.framework.IEntityBase;

/**
 * @author yigit
 *
 */
@Local
public interface ChequeToAccountPaymentPayrollHome<E> extends IEntityBase<E> {

	void init();
	ChequeToAccountPaymentPayroll getChequeToAccountPaymentPayroll();
	void setChequeToAccountPaymentPayroll(ChequeToAccountPaymentPayroll chequeToAccountPaymentPayroll);

	void createNewLine();

	void deleteLine(ChequeToAccountPaymentPayrollDetail detail);

	void deleteLine(Integer ix);

	void popupChequeSelect(int clientOrFirm);

	void selectClientCheque(Cheque cheque);
	
	public Boolean getIsEditable();
	
	public void setIsEditable(Boolean isEditable);
	
	void manualFlush();
}