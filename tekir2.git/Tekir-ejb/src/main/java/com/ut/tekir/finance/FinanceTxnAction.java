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
import com.ut.tekir.entities.Cheque;
import com.ut.tekir.entities.ChequeFromContactPayroll;
import com.ut.tekir.entities.ChequeFromContactPayrollDetail;
import com.ut.tekir.entities.ChequeToContactPayroll;
import com.ut.tekir.entities.Contact;
import com.ut.tekir.entities.DebitCreditNote;
import com.ut.tekir.entities.DebitCreditVirement;
import com.ut.tekir.entities.DocumentType;
import com.ut.tekir.entities.ExpenseNote;
import com.ut.tekir.entities.FinanceAction;
import com.ut.tekir.entities.FinanceTxn;
import com.ut.tekir.entities.Invoice;
import com.ut.tekir.entities.Payment;
import com.ut.tekir.entities.PaymentItem;
import com.ut.tekir.entities.PromissoryFromContactPayroll;
import com.ut.tekir.entities.PromissoryNote;
import com.ut.tekir.entities.PromissoryToContactPayroll;
import com.ut.tekir.entities.inv.TekirInvoice;
import com.ut.tekir.entities.ord.TekirOrderNote;

/**
 *
 * @author haky
 */
public interface FinanceTxnAction {

    /**
     * DebitCreditVirement'un her satırını dolaşır ve ilgili cari kayıtları için borç alacak hesaplarını kaydeder.
     */
    Boolean saveDebitCreditVirement(DebitCreditVirement dcn);
    Boolean deleteDebitCreditVirement( DebitCreditVirement dcn );
    
    Boolean saveBankToContactTransfer (BankToContactTransfer doc);
    Boolean deleteBankToContactTransfer (BankToContactTransfer doc);

    void saveContactToBankTransfer (BankToContactTransfer doc);
    Boolean deleteContactToBankTransfer (BankToContactTransfer doc);
    
	Boolean deleteCheque(Cheque cheque, DocumentType documentType);
	FinanceTxn saveCheque(Cheque cheque, Contact contact, FinanceAction action, DocumentType documentType);

	Boolean deletePromissory(PromissoryNote promissory, DocumentType documentType);
	FinanceTxn savePromissory(PromissoryNote promissory, Contact contact, FinanceAction action, DocumentType documentType);
	
    void saveDebitCreditNote(DebitCreditNote doc);
    void deleteDebitCreditNote(DebitCreditNote doc);
    
    void savePayment(Payment doc);
    void deletePayment(Payment doc);

    void saveInvoice(Invoice doc);
    void deleteInvoice(Invoice doc);
    
    void saveChequeToContactPayroll(ChequeToContactPayroll doc);
    void deleteChequeToContactPayroll(ChequeToContactPayroll doc);
    
    void savePromissoryToContactPayroll(PromissoryToContactPayroll doc);
    void deletePromissoryToContactPayroll(PromissoryToContactPayroll doc);
    
    void saveChequeFromContactPayroll(ChequeFromContactPayroll doc);
	void deleteChequeFromContactPayroll(ChequeFromContactPayroll doc);

	void savePromissoryFromContactPayroll(PromissoryFromContactPayroll doc);
	void deletePromissoryFromContactPayroll(PromissoryFromContactPayroll doc);

    void initComponent();
    void destroy();
    
    void savePaymentItem(PaymentItem item);
    void saveChequeFromContactPayrollItem(ChequeFromContactPayrollDetail detail);

    void deleteExpenseNote(ExpenseNote expenseNote);
    void saveExpenseNote(ExpenseNote expenseNote);
    
    void createFinanceTxnRecordsForInvoice(TekirInvoice doc);
    void deleteFinanceTxnRecordsForInvoice(TekirInvoice anInvoice);
    
    void createFinanceTxnRecordsForOrderNote(TekirOrderNote doc);
    void deleteFinanceTxnRecordsForOrderNote(TekirOrderNote doc);
    
    void createFinanceTxnRecordsForPayment(Payment doc);
	void deleteChequeFromContactPayrollItem(ChequeFromContactPayroll doc);
}
