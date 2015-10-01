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

import java.util.Date;

import com.ut.tekir.entities.Account;
import com.ut.tekir.entities.AccountCreditNoteDetail;
import com.ut.tekir.entities.Contact;
import com.ut.tekir.framework.DefaultDocumentFilterModel;

/**
 * 
 * @author sinan.yumak
 */
public class ChequeFromContactPayrollFilterModel extends DefaultDocumentFilterModel {

	private Long id;
	private Date date;
	private String info;
	private String contactName;
	private String contactCode;
	private String contactCaption;
	private String accountName;
	private String accountCode;
	private String accountCaption;
	private Boolean person;
	private String company;
	private Account account;
	
	//TODO:bu alanı silip cari adı ve koda göre mi aramalı?
	private Contact contact;
	
	public Contact getContact() {
		return contact;
	}

	public void setContact(Contact contact) {
		this.contact = contact;
	}

	public String getContactName() {
		return contactName;
	}

	public void setContactName(String contactName) {
		this.contactName = contactName;
	}

	public String getContactCode() {
		return contactCode;
	}

	public void setContactCode(String contactCode) {
		this.contactCode = contactCode;
	}

	public String getContactCaption() {
        return "[" + getContactCode() + "] " + getContactName();
	}

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

	public Boolean getPerson() {
		return person;
	}

	public void setPerson(Boolean person) {
		this.person = person;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public String getAccountCode() {
		return accountCode;
	}

	public void setAccountCode(String accountCode) {
		this.accountCode = accountCode;
	}
	
	

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public String getAccountCaption() {
        return "[" + getAccountCode() + "] " + getAccountName();
	}

}
