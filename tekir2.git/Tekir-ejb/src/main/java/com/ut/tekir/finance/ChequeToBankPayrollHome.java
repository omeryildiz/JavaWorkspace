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
import com.ut.tekir.entities.ChequeToBankPayroll;
import com.ut.tekir.entities.ChequeToBankPayrollDetail;
import com.ut.tekir.framework.IEntityBase;

/**
*
* @author yigit
*/
@Local
public interface ChequeToBankPayrollHome<E> extends IEntityBase<E> {
	
	void init();
    void manualFlush();
    
    ChequeToBankPayroll getChequeToBankPayroll();
    void setChequeToBankPayroll(ChequeToBankPayroll chequeToBankPayroll);
    
    void popupChequeSelect(int clientOrFirm);
    void selectClientCheque(Cheque cheque);

    void clearBankAccount(); 
    
    void createNewLine();
    void deleteLine(ChequeToBankPayrollDetail detail);
    void deleteLine(Integer ix);
    
    Boolean getIsEditable();
    void setIsEditable(Boolean isEditable);
    void pdf(ChequeToBankPayroll payroll);
}
