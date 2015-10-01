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

import javax.ejb.Remove;
import javax.ejb.Stateful;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.Events;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.log.Log;

import com.ut.tekir.entities.Cheque;
import com.ut.tekir.entities.ChequeStatus;
import com.ut.tekir.entities.ChequeStub;
import com.ut.tekir.framework.BaseConsts;
import com.ut.tekir.framework.CalendarManager;
import com.ut.tekir.framework.SequenceManager;
import com.ut.tekir.framework.SequenceType;

/**
 *
 * @author selman
 */
@Stateful
@Name("chequePopupHome")
@Scope(value = ScopeType.CONVERSATION)
@AutoCreate
public class ChequePopupHomeBean implements ChequePopupHome{
    
	private Cheque cheque;
    private Boolean isClient=Boolean.FALSE;
	private ChequeStub chequeStub;
	private String callerObserveString;
	
    @Logger
    private Log log;
    @In
    protected Events events;
    @In(create = true)
    SequenceManager sequenceManager;
    @In
    CalendarManager calendarManager;
    @In 
    protected FacesMessages facesMessages;

    @Create 
    @Begin(flushMode = org.jboss.seam.annotations.FlushModeType.MANUAL, join=true)
    public void init() {

    }
    
    public void createNew() {
        cheque = new Cheque();
        cheque.setIssueDate(calendarManager.getCurrentDate());
        cheque.setSerialNo(sequenceManager.getNewSerialNumber( SequenceType.SERIAL_CHEQUE));
        cheque.setEntryDate(calendarManager.getCurrentDate());
        cheque.setClientCheque(isClient);
        cheque.getMoney().setCurrency(BaseConsts.SYSTEM_CURRENCY_CODE);
    }
    
    public ChequeStatus[] getChequeStatus(){
    	return ChequeStatus.values();
    }
    
    public void setChequeFromChequeStub(){
        if (chequeStub != null) {
            cheque.setAccountNo(chequeStub.getAccountNo());
            cheque.setBankBranch(chequeStub.getBankBranch());
            cheque.setBankName(chequeStub.getBankName());
            cheque.setBankAccountId(chequeStub.getBankAccount().getId());
            cheque.setBankBranchId(chequeStub.getBankAccount().getBankBranch().getId());
            cheque.setPaymentPlace(chequeStub.getPaymentPlace());
            cheque.setIban(chequeStub.getIban());
            cheque.setChequeOwner(chequeStub.getChequeOwner());
            cheque.getMoney().setCurrency(chequeStub.getBankAccount().getCurrency());
        }
    }
    
    public String close() {
    	
    	
    	
    	
    	
    	
    	
    	if(cheque != null ){
    		
        	if (callerObserveString != null) {
        		events.raiseTransactionSuccessEvent(callerObserveString, cheque);

        		chequeStub = null;
        		log.debug("Event raised!");
        	} else {
        		log.debug("Event did not raise!");
        	}

    	}
    	
        return BaseConsts.SUCCESS;
    }
    
    @Remove
    @Destroy
    public void destroy() {
    }

	public ChequeStub getChequeStub() {
		return chequeStub;
	}
	
	public void setChequeStub(ChequeStub chequeStub) {
		this.chequeStub = chequeStub;
	}
	
	public void cancel(){
    	cheque=null;
    }

	@Out(required = false)
	public Cheque getCheque() {
		if(cheque == null)
			createNew();
		return cheque;
	}

	@In(required = false)
	public void setCheque(Cheque cheque) {
		this.cheque = cheque;
	}

	public Boolean getIsClient() {
		return isClient;
	}

	public void setIsClient(Boolean isClient) {
		this.isClient = isClient;
	}

	public void setCallerObserveString(String callerObserveString) {
		this.callerObserveString = callerObserveString;
	}

}
