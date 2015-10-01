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
import com.ut.tekir.entities.ChequeToBankPaymentPayroll;
import com.ut.tekir.entities.ChequeToBankPaymentPayrollDetail;
import com.ut.tekir.framework.IEntityBase;
import javax.ejb.Local;

/**
 *
 * @author yigit
 */
@Local
public interface ChequeToBankPaymentPayrollHome<E> extends IEntityBase<E> {

    void init();
    ChequeToBankPaymentPayroll getChequeToBankPaymentPayroll();
    void setChequeToBankPaymentPayroll(ChequeToBankPaymentPayroll chequeToBankPaymentPayroll);

    void popupChequeSelect(int clientOrFirm);
    void selectClientCheque(Cheque cheque);

    void createNewLine();
    void deleteLine(ChequeToBankPaymentPayrollDetail detail);
    void deleteLine(Integer ix);

    Boolean getIsEditable();
    void setIsEditable(Boolean isEditable);

    void manualFlush();

}
