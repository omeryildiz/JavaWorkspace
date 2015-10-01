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
import com.ut.tekir.entities.PromissoryToBankPayroll;
import com.ut.tekir.entities.PromissoryToBankPayrollDetail;
import com.ut.tekir.framework.IEntityBase;

/**
*
* @author yigit
*/
@Local
public interface PromissoryToBankPayrollHome<E> extends IEntityBase<E> {
	
	void init();
    void manualFlush();
    
    PromissoryToBankPayroll getPromissoryToBankPayroll();
    void setPromissoryToBankPayroll(PromissoryToBankPayroll promissoryToBankPayroll);
    
    void popupPromissorySelect(int clientOrFirm);
    void selectClientPromissory(PromissoryNote promissory);

    void clearBankAccount(); 
    
    void createNewLine();
    void deleteLine(PromissoryToBankPayrollDetail detail);
    void deleteLine(Integer ix);
    
    Boolean getIsEditable();
	void setIsEditable(Boolean isEditable);
    void pdf(PromissoryToBankPayroll payroll);
}
