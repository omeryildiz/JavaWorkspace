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

import com.ut.tekir.entities.BankAccount;
import java.util.Date;
import java.util.List;

import javax.ejb.Local;

import com.ut.tekir.entities.Cheque;
import com.ut.tekir.entities.ChequeStatus;
import com.ut.tekir.entities.Contact;

/**
 *
 * @author huseyin
 */
@Local
public interface ChequeSuggestion {

    List<Cheque> getChequeList();

	void setChequeList(List<Cheque> chequeList);
    
    List<Cheque> selectChequeList();
    
    String getReferenceNo();
    
    void setReferenceNo(String referenceNo);
    
    String getSerialNo();
    
    void setSerialNo(String serialNo);
        
    Date getEndDate();
    
    void setEndDate(Date endDate);
    
    String getBankName();
    
    void setBankName(String bankName);
    
    String getBankBranch();
    
    void setBankBranch(String bankBranch);
    
    String getBankAccount();
    
    void setBankAccount(String bankAccount);
    
    ChequeStatus getLastStatus();
    
    void setLastStatus(ChequeStatus lastStatus);
    
	ChequeStatus getTargetStatus();

	void setTargetStatus(ChequeStatus targetStatus);
    
    void selectedCheque( int rowKey);
    
    Cheque getCheque();

    void setCheque(Cheque cheque);
    
    void setCallerObserveString(String callerString);
    
    void setClientCheque(Boolean clientCheque);
    
    Boolean getClientCheque();
    
	Contact getContact();

	void setContact(Contact contact);
	
	public Date getBeginDate();

	public void setBeginDate(Date beginDate);
    
	public String getChequeOwner();

	public void setChequeOwner(String chequeOwner);

    void destroy();
    
	public Date getIssueDate();

	public void setIssueDate(Date issueDate);
	
	public ChequeStatus getPreviousStatus();

	public void setPreviousStatus(ChequeStatus previousStatus);
	
	public List<ChequeStatus> possibleStatusForCustomer(ChequeStatus requestStatus);
	
	public List<ChequeStatus> possibleStatusForFirm(ChequeStatus requestStatus);
	
	public Date getHistoryBeginDate();

	public void setHistoryBeginDate(Date historyBeginDate);

	public Date getHistoryEndDate();

	public void setHistoryEndDate(Date historyEndDate);

    public BankAccount getBankAccountId();

    public void setBankAccountId(BankAccount bankAccountId);

}
