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

import com.ut.tekir.entities.Account;
import com.ut.tekir.entities.AdvanceProcessType;
import com.ut.tekir.entities.Bank;
import com.ut.tekir.entities.BankAccount;
import com.ut.tekir.entities.BankBranch;
import com.ut.tekir.entities.FinanceAction;
import com.ut.tekir.entities.WorkBunch;
import com.ut.tekir.framework.DefaultDocumentFilterModel;

/**
 *
 * @author huseyin
 */
public class BankToAccountTransferFilterModel extends DefaultDocumentFilterModel{
    
    private Account account;
    private Bank bank;
    private BankBranch bankBranch;
    private BankAccount bankAccount;
    private FinanceAction action;
    private AdvanceProcessType processType;
    private WorkBunch WorkBunch;

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

	public BankAccount getBankAccount() {
		return bankAccount;
	}

	public void setBankAccount(BankAccount bankAccount) {
		this.bankAccount = bankAccount;
	}

	public FinanceAction getAction() {
		return action;
	}

	public void setAction(FinanceAction action) {
		this.action = action;
	}

	public Bank getBank() {
		return bank;
	}

	public void setBank(Bank bank) {
		this.bank = bank;
	}

	public BankBranch getBankBranch() {
		return bankBranch;
	}

	public void setBankBranch(BankBranch bankBranch) {
		this.bankBranch = bankBranch;
	}

	public AdvanceProcessType getProcessType() {
		return processType;
	}

	public void setProcessType(AdvanceProcessType processType) {
		this.processType = processType;
	}

	public WorkBunch getWorkBunch() {
		return WorkBunch;
	}

	public void setWorkBunch(WorkBunch workBunch) {
		WorkBunch = workBunch;
	}

}