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
import com.ut.tekir.entities.PromissoryToBankPaymentPayroll;
import com.ut.tekir.entities.PromissoryToBankPaymentPayrollDetail;
import com.ut.tekir.framework.IEntityBase;

/**
 *
 * @author sinan.yumak
 */
@Local
public interface PromissoryToBankPaymentPayrollHome<E> extends IEntityBase<E> {

    void init();
    PromissoryToBankPaymentPayroll getPromissoryToBankPaymentPayroll();
    void setPromissoryToBankPaymentPayroll(PromissoryToBankPaymentPayroll promissoryToBankPaymentPayroll);

    void popupPromissorySelect(int clientOrFirm);
    void selectClientPromissory(PromissoryNote promissory);

    void createNewLine();
    void deleteLine(PromissoryToBankPaymentPayrollDetail detail);
    void deleteLine(Integer ix);

    Boolean getIsEditable();
    void setIsEditable(Boolean isEditable);

    void manualFlush();
}
