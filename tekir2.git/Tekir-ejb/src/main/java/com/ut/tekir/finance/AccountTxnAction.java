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

import com.ut.tekir.entities.Account;
import com.ut.tekir.entities.AccountTxn;
import com.ut.tekir.entities.BankToAccountTransfer;
import com.ut.tekir.entities.Cheque;
import com.ut.tekir.entities.ChequeToAccountCollectionPayroll;
import com.ut.tekir.entities.ChequeToAccountPaymentPayroll;
import com.ut.tekir.entities.DocumentBase;
import com.ut.tekir.entities.DocumentType;
import com.ut.tekir.entities.FinanceAction;
import com.ut.tekir.entities.ForeignDocumentBase;
import com.ut.tekir.entities.FundTransfer;
import com.ut.tekir.entities.Payment;
import com.ut.tekir.entities.PaymentTable;
import com.ut.tekir.entities.PromissoryNote;
import com.ut.tekir.entities.PromissoryToAccountCollectionPayroll;
import com.ut.tekir.entities.PromissoryToAccountPaymentPayroll;

/**
 *
 * @author haky
 */
@Local
public interface AccountTxnAction {

    void initComponent();
    void destroy();

    Boolean saveFundTransfer(FundTransfer doc);
    Boolean deleteFundTransfer(FundTransfer doc);

    Boolean saveBankToAccountTransfer (BankToAccountTransfer doc);
    Boolean deleteBankToAccountTransfer (BankToAccountTransfer doc);

    Boolean saveAccountToBankTransfer (BankToAccountTransfer doc);
    Boolean deleteAccountToBankTransfer (BankToAccountTransfer doc);
    
	Boolean deleteCheque(Cheque cheque, DocumentType documentType);
	AccountTxn saveCheque(Cheque cheque, Account account, FinanceAction action, DocumentType documentType);
	
	Boolean deletePromissory(PromissoryNote promissory, DocumentType documentType);
	AccountTxn savePromissory(PromissoryNote promissory, Account account, FinanceAction action, DocumentType documentType);
    
	void saveChequeToAccountCollectionPayroll(ChequeToAccountCollectionPayroll doc);
	void deleteChequeToAccountCollectionPayroll(ChequeToAccountCollectionPayroll doc);

	void saveChequeToAccountPaymentPayroll(ChequeToAccountPaymentPayroll doc);
	void deleteChequeToAccountPaymentPayroll(ChequeToAccountPaymentPayroll doc);

	void savePromissoryToAccountCollectionPayroll(PromissoryToAccountCollectionPayroll doc);
	void deletePromissoryToAccountCollectionPayroll(PromissoryToAccountCollectionPayroll doc);
	
	void savePromissoryToAccountPaymentPayroll(PromissoryToAccountPaymentPayroll doc);
	void deletePromissoryToAccountPaymentPayroll(PromissoryToAccountPaymentPayroll doc);
	
    void savePayment(Payment doc);
    void deletePayment(Payment doc);
    void updateTradeinFields(PaymentTable paymentTable, boolean newState);
    
    void saveDocument(ForeignDocumentBase doc);
    void deleteDocument(DocumentBase doc);
}
