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
import java.util.Date;

/**
 *
 * @author yigit
 */
public class ChequeToBankPaymentPayrollFilterModel extends DefaultDocumentFilterModel {

    private Bank bank;
	private BankBranch bankBranch;
	private BankAccount bankAccount;
	private Long id;
	private Date date;
	private String info;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
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

	public BankAccount getBankAccount() {
		return bankAccount;
	}

	public void setBankAccount(BankAccount bankAccount) {
		this.bankAccount = bankAccount;
	}

}
