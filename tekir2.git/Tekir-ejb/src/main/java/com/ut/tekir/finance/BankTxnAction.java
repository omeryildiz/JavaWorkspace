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

import com.ut.tekir.entities.BankAccount;
import com.ut.tekir.entities.BankToAccountTransfer;
import com.ut.tekir.entities.BankToBankTransfer;
import com.ut.tekir.entities.BankToContactTransfer;
import com.ut.tekir.entities.BankTxn;
import com.ut.tekir.entities.Cheque;
import com.ut.tekir.entities.ChequeCollectedAtBankPayroll;
import com.ut.tekir.entities.ChequeToBankPaymentPayroll;
import com.ut.tekir.entities.DepositAccount;
import com.ut.tekir.entities.DocumentType;
import com.ut.tekir.entities.FinanceAction;
import com.ut.tekir.entities.ForeignExchange;
import com.ut.tekir.entities.PromissoryCollectedAtBankPayroll;
import com.ut.tekir.entities.PromissoryNote;
import com.ut.tekir.entities.PromissoryToBankPaymentPayroll;

/**
 *
 * @author huseyin
 */
@Local
public interface BankTxnAction {

    void initComponent();
    void destroy();

    void saveBankToAccount(BankToAccountTransfer doc);
    Boolean deleteBankToAccount(BankToAccountTransfer doc);

    void saveAccountToBank(BankToAccountTransfer doc);
    Boolean deleteAccountToBank(BankToAccountTransfer doc);

    Boolean saveBankToBankTransfer(BankToBankTransfer doc);
    Boolean deleteBankToBankTransfer(BankToBankTransfer doc);
    
    Boolean saveForeignExchange(ForeignExchange doc);
    Boolean deleteForeignExchange(ForeignExchange doc);
    
    void saveBankToContactTransfer(BankToContactTransfer doc);
    Boolean deleteBankToContactTransfer(BankToContactTransfer doc);

    void saveContactToBankTransfer(BankToContactTransfer doc);
    Boolean deleteContactToBankTransfer(BankToContactTransfer doc);
    
    void saveDepositAccount(DepositAccount doc);
    void deleteDepositAccount(DepositAccount doc);

    Boolean deleteCheque(Cheque cheque, DocumentType documentType);
	BankTxn saveCheque(Cheque cheque, BankAccount account, FinanceAction action, DocumentType documentType);
	
	Boolean deletePromissory(PromissoryNote promissory, DocumentType documentType);
	BankTxn savePromissory(PromissoryNote Promissory, BankAccount account, FinanceAction action, DocumentType documentType);
    
	void deleteDocument(Long docId, DocumentType docType);
	
	void saveChequeCollectedAtBankPayroll(ChequeCollectedAtBankPayroll doc);
	Boolean deleteChequeCollectedAtBankPayroll(ChequeCollectedAtBankPayroll doc);

	void savePromissoryCollectedAtBankPayroll(PromissoryCollectedAtBankPayroll doc);
	Boolean deletePromissoryCollectedAtBankPayroll(PromissoryCollectedAtBankPayroll doc);

	void saveChequeToBankPaymentPayroll(ChequeToBankPaymentPayroll item);
	void deleteChequeToBankPaymentPayroll(ChequeToBankPaymentPayroll doc);
	
	void savePromissoryToBankPaymentPayroll(PromissoryToBankPaymentPayroll doc);
	void deletePromissoryToBankPaymentPayroll(PromissoryToBankPaymentPayroll doc);
}
