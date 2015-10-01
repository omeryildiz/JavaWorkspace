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

package com.ut.tekir.report;

import java.util.Date;

import com.ut.tekir.entities.Contact;
import com.ut.tekir.entities.WorkBunch;

/**
 * @author rustem
 * 
 */
public class ExtractAccountFilterModel {
	private Contact contact;
	private Date beginDate;
	private Date endDate;
	private String currency;
	private String financeTxnCode;
	private WorkBunch workBunch;

	public Contact getContact() {
		return contact;
	}

	public void setContact(Contact contact) {
		this.contact = contact;
	}

	public Date getBeginDate() {
		return beginDate;
	}

	public void setBeginDate(Date beginDate) {
		this.beginDate = beginDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getFinanceTxnCode() {
		return financeTxnCode;
	}

	public void setFinanceTxnCode(String financeTxnCode) {
		this.financeTxnCode = financeTxnCode;
	}

	public WorkBunch getWorkBunch() {
		return workBunch;
	}

	public void setWorkBunch(WorkBunch workBunch) {
		this.workBunch = workBunch;
	}

}
