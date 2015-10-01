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

import com.ut.tekir.entities.BankToContactTransfer;
import com.ut.tekir.framework.IEntityBase;
import javax.ejb.Local;

/**
 *
 * @author burak	
 */
@Local
public interface BankToContactTransferHome<E> extends IEntityBase<E> {

    BankToContactTransfer getBankToContactTransfer();

    void setBankToContactTransfer(BankToContactTransfer bankToContactTransfer);
    //void selectBankAccount(Integer ix);  
    void clearBankAccount();
    void init();
    void manualFlush();
    void createNewInvoiceMatch(InvoiceMatchFilterModel invoiceModel);
    void openMatchPopup();
}
