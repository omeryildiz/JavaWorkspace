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
import com.ut.tekir.entities.BankTransferType;
import com.ut.tekir.entities.WorkBunch;
import com.ut.tekir.framework.DefaultDocumentFilterModel;

/**
 *
 * @author haky
 */
public class BankToBankTransferFilterModel extends DefaultDocumentFilterModel{

	private Bank fromBank;
	private Bank toBank;
	private BankBranch fromBankBranch;
	private BankBranch toBankBranch;
	private BankAccount fromBankAccount;
	private BankAccount toBankAccount;
	private BankTransferType transferType;
	private WorkBunch workBunch;
	
	public BankAccount getFromBankAccount(){
		return fromBankAccount;
	}
	
	public void setfromBankAccount(BankAccount fromBankAccount){
		this.fromBankAccount=fromBankAccount;
	}

	public BankAccount getToBankAccount(){
		return toBankAccount;
	}
	
	public void setToBankAccount(BankAccount toBankAccount){
		this.toBankAccount=toBankAccount;
	}

	public BankTransferType getTransferType() {
		return transferType;
	}

	public void setTransferType(BankTransferType transferType) {
		this.transferType = transferType;
	}

	public Bank getFromBank() {
		return fromBank;
	}

	public void setFromBank(Bank fromBank) {
		this.fromBank = fromBank;
	}

	public Bank getToBank() {
		return toBank;
	}

	public void setToBank(Bank toBank) {
		this.toBank = toBank;
	}

	public BankBranch getFromBankBranch() {
		return fromBankBranch;
	}

	public void setFromBankBranch(BankBranch fromBankBranch) {
		this.fromBankBranch = fromBankBranch;
	}

	public BankBranch getToBankBranch() {
		return toBankBranch;
	}

	public void setToBankBranch(BankBranch toBankBranch) {
		this.toBankBranch = toBankBranch;
	}

	public WorkBunch getWorkBunch() {
		return workBunch;
	}

	public void setWorkBunch(WorkBunch workBunch) {
		this.workBunch = workBunch;
	}
	
}
