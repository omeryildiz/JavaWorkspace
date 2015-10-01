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
import java.util.List;

import javax.ejb.Local;

import com.ut.tekir.entities.ChequeStatus;
import com.ut.tekir.entities.Contact;
import com.ut.tekir.entities.PromissoryNote;

@Local
public interface PromissorySuggestion {

	List<PromissoryNote> selectPromissoryList();

	List<ChequeStatus> possibleStatusForCustomer(ChequeStatus requestStatus);

	List<ChequeStatus> possibleStatusForFirm(ChequeStatus requestStatus);

	List<PromissoryNote> getPromissoryList();

	void setPromissoryList(List<PromissoryNote> promissoryList);

	void selectedPromissory(int rowKey);

	void setCallerObserveString(String callerString);

	String getSerialNo();

	void setSerialNo(String serialNo);

	String getReferenceNo();

	void setReferenceNo(String referenceNo);

	Date getEndDate();

	void setEndDate(Date endDate);

	ChequeStatus getLastStatus();

	void setLastStatus(ChequeStatus lastStatus);

	ChequeStatus getPreviousStatus();

	void setPreviousStatus(ChequeStatus previousStatus);

	ChequeStatus getTargetStatus();

	void setTargetStatus(ChequeStatus targetStatus);

	String getPromissorynoteOwner();

	void setPromissorynoteOwner(String promissorynoteOwner);

	String getPaymentPlace();

	void setPaymentPlace(String paymentPlace);

	PromissoryNote getPromissory();

	void setPromissory(PromissoryNote promissory);

	Boolean getClientPromissory();

	void setClientPromissory(Boolean clientPromissory);

	Contact getContact();

	void setContact(Contact contact);

	Date getBeginDate();

	void setBeginDate(Date beginDate);

	void destroy();
	
	Date getHistoryBeginDate();

	void setHistoryBeginDate(Date historyBeginDate);

	Date getHistoryEndDate();

	void setHistoryEndDate(Date historyEndDate);

}