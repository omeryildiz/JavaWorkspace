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
import java.util.Date;

import com.ut.tekir.entities.ChequeStatus;
import com.ut.tekir.entities.Contact;

/**
 *
 * @author selman
 */
public class ChequeFilterModel{
	
	private String referenceNo;
	private String bankName;
	private String bankBranch;
    private String bankAccountName;
	private Contact contact;
	private String chequeOwner;
	private Date beginDate;
	private Date endDate;
	private Date issueDate;
	private Date issueBeginDate;
	private Date issueEndDate;
	private ChequeStatus lastStatus;
	private ChequeStatus previousStatus;
    private Bank bank;
    private BankAccount bankAccount;
	

	/**
	 * @return the referenceNo
	 */
	public String getReferenceNo() {
		return referenceNo;
	}
	/**
	 * @param referenceNo the referenceNo to set
	 */
	public void setReferenceNo(String referenceNo) {
		this.referenceNo = referenceNo;
	}
	/**
	 * @return the bankName
	 */
	public String getBankName() {
		return bankName;
	}
	/**
	 * @param bankName the bankName to set
	 */
	public void setBankName(String bankName) {
		this.bankName = bankName;
	}
	/**
	 * @return the bankBranch
	 */
	public String getBankBranch() {
		return bankBranch;
	}
	/**
	 * @param bankBranch the bankBranch to set
	 */
	public void setBankBranch(String bankBranch) {
		this.bankBranch = bankBranch;
	}
	/**
	 * @return the contact
	 */
	public Contact getContact() {
		return contact;
	}
	/**
	 * @param contact the contact to set
	 */
	public void setContact(Contact contact) {
		this.contact = contact;
	}
	/**
	 * @return the chequeOwner
	 */
	public String getChequeOwner() {
		return chequeOwner;
	}
	/**
	 * @param chequeOwner the chequeOwner to set
	 */
	public void setChequeOwner(String chequeOwner) {
		this.chequeOwner = chequeOwner;
	}
	/**
	 * @return the beginDate
	 */
	public Date getBeginDate() {
		return beginDate;
	}
	/**
	 * @param beginDate the beginDate to set
	 */
	public void setBeginDate(Date beginDate) {
		this.beginDate = beginDate;
	}
	/**
	 * @return the endDate
	 */
	public Date getEndDate() {
		return endDate;
	}
	/**
	 * @param endDate the endDate to set
	 */
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	
	/**
	 * @return düzenleme tarihi
	 */
	public Date getIssueBeginDate() {
		return issueBeginDate;
	}
	/**
	 * @param düzenleme tarihi set
	 */
	public void setIssueBeginDate(Date issueBeginDate) {
		this.issueBeginDate = issueBeginDate;
	}
	/**
	 * @return the issueEndDate
	 */
	public Date getIssueEndDate() {
		return issueEndDate;
	}
	/**
	 * @param issueEndDate the issueEndDate to set
	 */
	public void setIssueEndDate(Date issueEndDate) {
		this.issueEndDate = issueEndDate;
	}
	/**
	 * @return the lastStatus
	 */
	public ChequeStatus getLastStatus() {
		return lastStatus;
	}
	/**
	 * @param lastStatus the lastStatus to set
	 */
	public void setLastStatus(ChequeStatus lastStatus) {
		this.lastStatus = lastStatus;
	}
	public ChequeStatus getPreviousStatus() {
		return previousStatus;
	}
	public void setPreviousStatus(ChequeStatus previousStatus) {
		this.previousStatus = previousStatus;
	}
	public Date getIssueDate() {
		return issueDate;
	}
	public void setIssueDate(Date issueDate) {
		this.issueDate = issueDate;
	}

    public String getBankAccountName() {
        return bankAccountName;
    }

    public void setBankAccountName(String bankAccountName) {
        this.bankAccountName = bankAccountName;
    }

    /**
     * @return the bank
     */
    public Bank getBank() {
        return bank;
    }

    /**
     * @param bank the bank to set
     */
    public void setBank(Bank bank) {
        this.bank = bank;
    }

    /**
     * @return the bankAccount
     */
    public BankAccount getBankAccount() {
        return bankAccount;
    }

    /**
     * @param bankAccount the bankAccount to set
     */
    public void setBankAccount(BankAccount bankAccount) {
        this.bankAccount = bankAccount;
    }
	
	
}