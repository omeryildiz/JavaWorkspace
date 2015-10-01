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
import com.ut.tekir.entities.ChequeToContactPayroll;
import com.ut.tekir.entities.ChequeToContactPayrollDetail;
import com.ut.tekir.framework.IEntityBase;

/**
*
* @author yigit
*/
@Local
public interface ChequeToContactPayrollHome<E> extends IEntityBase<E> {
	
	void init();
    void manualFlush();
    
    ChequeToContactPayroll getChequeToContactPayroll();
    void setChequeToContactPayroll(ChequeToContactPayroll chequeToContactPayroll);
    
    void popupChequeSelect(int clientOrFirm);
    void selectClientCheque(Cheque cheque);
    void popupChequeCreate();

    void createNewLine();
    void deleteLine(ChequeToContactPayrollDetail detail);
    void deleteLine(Integer ix);
    
    Boolean getIsEditable();
    void findCheque(int rowKey);
    void setIsEditable(Boolean isEditable);
    void pdf(ChequeToContactPayroll payroll);
}
