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

import com.ut.tekir.entities.Cheque;
import com.ut.tekir.entities.ChequeToAccountCollectionPayroll;
import com.ut.tekir.entities.ChequeToAccountCollectionPayrollDetail;
import com.ut.tekir.framework.IEntityBase;

/**
 * @author yigit
 *
 */
public interface ChequeToAccountCollectionPayrollHome<E> extends IEntityBase<E> {

	void init();
	ChequeToAccountCollectionPayroll getChequeToAccountCollectionPayroll();
	void setChequeToAccountCollectionPayroll(ChequeToAccountCollectionPayroll ChequeToAccountCollectionPayroll);

	void createNewLine();
	void deleteLine(ChequeToAccountCollectionPayrollDetail detail);
	void deleteLine(Integer ix);

	void popupChequeSelect(int clientOrFirm);
	void selectClientCheque(Cheque cheque);

	public Boolean getIsEditable();
	
	public void setIsEditable(Boolean isEditable);
	
	void manualFlush();

}