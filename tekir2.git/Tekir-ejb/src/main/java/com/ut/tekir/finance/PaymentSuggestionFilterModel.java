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

import com.ut.tekir.entities.AdvanceProcessType;
import com.ut.tekir.entities.Contact;
import com.ut.tekir.entities.FinanceAction;
import com.ut.tekir.framework.DefaultDocumentFilterModel;

/**
 * 
 * @author sinan.yumak
 * 
 */
public class PaymentSuggestionFilterModel extends DefaultDocumentFilterModel {

	private Contact contact;
	private Date beginDate;
	private Date endDate;
	private FinanceAction action;
	private Boolean matchingFinished = false;
	private AdvanceProcessType processType;
	
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

	public FinanceAction getAction() {
		return action;
	}

	public void setAction(FinanceAction action) {
		this.action = action;
	}

	public Boolean getMatchingFinished() {
		return matchingFinished;
	}

	public void setMatchingFinished(Boolean matchingFinished) {
		this.matchingFinished = matchingFinished;
	}

	public AdvanceProcessType getProcessType() {
		return processType;
	}

	public void setProcessType(AdvanceProcessType processType) {
		this.processType = processType;
	}

}
