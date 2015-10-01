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

import java.math.BigDecimal;
import java.util.List;

import javax.ejb.Local;

import com.ut.tekir.entities.Bank;
import com.ut.tekir.entities.BankBranch;
import com.ut.tekir.entities.ContactAddress;
import com.ut.tekir.entities.DocumentType;
import com.ut.tekir.entities.ExpenseNote;
import com.ut.tekir.entities.ExpenseNoteItem;
import com.ut.tekir.entities.inv.TekirInvoice;
import com.ut.tekir.framework.IEntityBase;
import com.ut.tekir.invoice.yeni.InvoiceSuggestionFilterModel;

/**
 *
 * @author sinan.yumak
 */
@Local
public interface ExpenseNoteHome<E> extends IEntityBase<E> {
    
    ExpenseNote getExpenseNote();

    void setExpenseNote(ExpenseNote ExpenseNote);

    void createNewLine();
    
    void deleteLine( ExpenseNoteItem item );
    
    void deleteLine( Integer ix );

    void deleteReturnLine(Integer ix);

    void init();
    
    void manualFlush();
    
    void selectLine(Integer ix);
    
    void calculateAmountsAction(ExpenseNoteItem item);
    
    Bank getBank();
    void setBank(Bank bank);
    
    BankBranch getBankBranch();
    void setBankBranch(BankBranch bankBranch);
    
    void clearBankAccount();
    BigDecimal getBankTransferCost();
    
    void setBankTransferCost(BigDecimal bankTransferCost);
    void calcTotalAmountsAction();

	DocumentType getDocumentType();
	void setDocumentType(DocumentType documentType);

	void selectInvoice(int ix);
	
	List<TekirInvoice> findInvoices();
	
	List<TekirInvoice> getInvoiceList();
	void setInvoiceList(List<TekirInvoice> invoiceList);

	InvoiceSuggestionFilterModel getFilterModel();
    void setFilterModel(InvoiceSuggestionFilterModel filterModel);

    void setAddress();
    
	ContactAddress getSelectedAddress();
	void setSelectedAddress(ContactAddress selectedAddress);

	void addLineExpenseAction();
}
