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

import com.ut.tekir.entities.Bank;
import com.ut.tekir.entities.BankAccount;
import com.ut.tekir.entities.BankBranch;
import com.ut.tekir.framework.DefaultDocumentFilterModel;

/**
 * 
 * @author selman
 */
public class ForeignExchangeFilterModel extends DefaultDocumentFilterModel {

	private Bank bank;
	private BankBranch bankBranch;
	private BankAccount fromBankAccount;
	private BankAccount toBankAccount;

	public BankAccount getFromBankAccount() {
		return fromBankAccount;
	}

	public void setfromBankAccount(BankAccount fromBankAccount) {
		this.fromBankAccount = fromBankAccount;
	}

	public BankAccount getToBankAccount() {
		return toBankAccount;
	}

	public void setToBankAccount(BankAccount toBankAccount) {
		this.toBankAccount = toBankAccount;
	}

	public Bank getBank() {
		return bank;
	}

	public void setBank(Bank bank) {
		this.bank = bank;
	}

	public void setBankBranch(BankBranch bankBranch) {
		this.bankBranch = bankBranch;
	}

	public BankBranch getBankBranch() {
		return bankBranch;
	}

}
