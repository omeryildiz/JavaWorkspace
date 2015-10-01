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

import com.ut.tekir.entities.BankAccount;
import com.ut.tekir.framework.DefaultDocumentFilterModel;

/**
 *
 * @author huseyin
 */
public class DepositAccountFilterModel extends DefaultDocumentFilterModel{
	
	BankAccount bankAccount;
	BankAccount depositBankAccount;

	public BankAccount getBankAccount() {
		return bankAccount;
	}

	public void setBankAccount(BankAccount bankAccount) {
		this.bankAccount = bankAccount;
	}

	public BankAccount getDepositBankAccount() {
		return depositBankAccount;
	}

	public void setDepositBankAccount(BankAccount depositBankAccount) {
		this.depositBankAccount = depositBankAccount;
	}
    
   
}