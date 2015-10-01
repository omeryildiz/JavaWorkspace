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

import java.util.List;

import javax.ejb.Local;

import com.ut.tekir.entities.BankToContactTransfer;
import com.ut.tekir.framework.IEntityBase;
import com.ut.tekir.report.CreditCardPosCommisionViewModel;

/**
 *
 * @author selman	
 */
@Local
public interface ContactToBankTransferHome<E> extends IEntityBase<E> {

	BankToContactTransfer getContactToBankTransfer();

    void setContactToBankTransfer(BankToContactTransfer contactToBankTransfer);
    void clearBankAccount();
    void init();
    void manualFlush();
    void createNewInvoiceMatch(InvoiceMatchFilterModel invoiceModel);
    void openMatchPopup();
    
	List<CreditCardPosCommisionViewModel> getPosCommissionViewList();
	void setPosCommissionViewList(List<CreditCardPosCommisionViewModel> posCommissionViewList);

}
