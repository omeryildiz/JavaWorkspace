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
import com.ut.tekir.entities.PromissoryToContactPayroll;
import com.ut.tekir.entities.PromissoryToContactPayrollDetail;
import com.ut.tekir.framework.IEntityBase;

/**
*
* @author yigit
*/
@Local
public interface PromissoryToContactPayrollHome<E> extends IEntityBase<E> {
	
	void init();
    void manualFlush();
    
    PromissoryToContactPayroll getPromissoryToContactPayroll();
    void setPromissoryToContactPayroll(PromissoryToContactPayroll promissoryToContactPayroll);
    
    void popupPromissorySelect(int clientOrFirm);
    void selectClientPromissory(PromissoryNote promissory);
    void popupPromissoryCreate();

    void findPromissory(int rowKey);
    void createNewLine();
    void deleteLine(PromissoryToContactPayrollDetail detail);
    void deleteLine(Integer ix);
    
    Boolean getIsEditable();
	void setIsEditable(Boolean isEditable);
    void pdf(PromissoryToContactPayroll payroll);
}
