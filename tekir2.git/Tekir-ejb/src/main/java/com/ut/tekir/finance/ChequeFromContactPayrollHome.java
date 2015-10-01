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
import com.ut.tekir.entities.ChequeFromContactPayroll;
import com.ut.tekir.entities.ChequeFromContactPayrollDetail;
import com.ut.tekir.framework.IEntityBase;

/**
 * @author yigit
 *
 */
@Local
public interface ChequeFromContactPayrollHome <E> extends IEntityBase<E>{

	void init();
	ChequeFromContactPayroll getChequeFromContactPayroll();
	void setChequeFromContactPayroll(ChequeFromContactPayroll chequeFromContactPayroll);

	void createNewLine();
	void findCheque(int rowKey);
	void deleteLine(ChequeFromContactPayrollDetail detail);
	void deleteLine(Integer ix);
	void popupChequeCreate();
	void selectClientCheque(Cheque cheque);
	
	public Boolean getIsEditable();
	
	public void setIsEditable(Boolean isEditable);

	void manualFlush();
    void pdf(ChequeFromContactPayroll payroll);
}