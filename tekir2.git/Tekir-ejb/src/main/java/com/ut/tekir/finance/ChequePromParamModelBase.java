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
import com.ut.tekir.entities.BankAccount;
import com.ut.tekir.entities.ChequeStatus;
import com.ut.tekir.entities.Contact;
import com.ut.tekir.entities.DocumentType;
import com.ut.tekir.entities.WorkBunch;

/**
 * @author sinan.yumak
 * 
 */
public class ChequePromParamModelBase {
	private ChequeStatus newStatus;
	private DocumentType documentType;
	private Long documentId;
	private String documentSerial;
	private Contact toContact;// Hedef cari
	private Account toAccount;// Hedef kasa
	private BankAccount toBankAccount;// Hedef banka
	private WorkBunch workBunch;

	public ChequeStatus getNewStatus() {
		return newStatus;
	}

	public void setNewStatus(ChequeStatus newStatus) {
		this.newStatus = newStatus;
	}

	public DocumentType getDocumentType() {
		return documentType;
	}

	public void setDocumentType(DocumentType documentType) {
		this.documentType = documentType;
	}

	public Long getDocumentId() {
		return documentId;
	}

	public void setDocumentId(Long documentId) {
		this.documentId = documentId;
	}

	public String getDocumentSerial() {
		return documentSerial;
	}

	public void setDocumentSerial(String documentSerial) {
		this.documentSerial = documentSerial;
	}

	public Contact getToContact() {
		return toContact;
	}

	public void setToContact(Contact toContact) {
		this.toContact = toContact;
	}

	public Account getToAccount() {
		return toAccount;
	}

	public void setToAccount(Account toAccount) {
		this.toAccount = toAccount;
	}

	public BankAccount getToBankAccount() {
		return toBankAccount;
	}

	public void setToBankAccount(BankAccount toBankAccount) {
		this.toBankAccount = toBankAccount;
	}

	public WorkBunch getWorkBunch() {
		return workBunch;
	}

	public void setWorkBunch(WorkBunch workBunch) {
		this.workBunch = workBunch;
	}

}
