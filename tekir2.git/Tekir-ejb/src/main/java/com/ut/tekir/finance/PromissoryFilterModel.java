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

import com.ut.tekir.entities.ChequeStatus;
import com.ut.tekir.entities.Contact;

/**
 *
 * @author selman
 */
public class PromissoryFilterModel{
	
	private String referenceNo;
	private Contact contact;
	private String promissorynoteOwner;
	private Date beginDate;
	private Date endDate;
	private Date issueDate;
	private Date issueBeginDate;
	private Date issueEndDate;
	private ChequeStatus lastStatus;
	private ChequeStatus previousStatus;
	private Boolean clientPromissoryNote;

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
	 * @return the promissorynoteOwner
	 */
	public String getPromissorynoteOwner() {
		return promissorynoteOwner;
	}
	/**
	 * @param promissorynoteOwner the promissorynoteOwner to set
	 */
	public void setPromissorynoteOwner(String promissorynoteOwner) {
		this.promissorynoteOwner = promissorynoteOwner;
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
	/**
	 * @return the clientPromissoryNote
	 */
	public Boolean getClientPromissoryNote() {
		return clientPromissoryNote;
	}
	/**
	 * @param clientPromissoryNote the clientPromissoryNote to set
	 */
	public void setClientPromissoryNote(Boolean clientPromissoryNote) {
		this.clientPromissoryNote = clientPromissoryNote;
	}

	public Date getIssueDate() {
		return issueDate;
	}
	public void setIssueDate(Date issueDate) {
		this.issueDate = issueDate;
	}
	/**
	 * @return the issueBeginDate
	 */
	public Date getIssueBeginDate() {
		return issueBeginDate;
	}
	/**
	 * @param issueBeginDate the issueBeginDate to set
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
	
}
