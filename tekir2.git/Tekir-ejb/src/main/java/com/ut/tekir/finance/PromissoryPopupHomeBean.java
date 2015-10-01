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
import org.jboss.seam.log.Log;

import com.ut.tekir.entities.ChequeStatus;
import com.ut.tekir.entities.PromissoryNote;
import com.ut.tekir.framework.BaseConsts;
import com.ut.tekir.framework.CalendarManager;
import com.ut.tekir.framework.SequenceManager;
import com.ut.tekir.framework.SequenceType;

/**
 *
 * @author selman
 */
@Stateful
@Name("promissoryPopupHome")
@Scope(value = ScopeType.CONVERSATION)
@AutoCreate
public class PromissoryPopupHomeBean implements PromissoryPopupHome {
	
	private PromissoryNote promissory;
	private Boolean isClient = Boolean.FALSE;
	private String callerObserveString;
	
	@In(create = true)
	SequenceManager sequenceManager;
	@In
	CalendarManager calendarManager;
    @In
    protected Events events;
    
    @Logger
    private Log log;
    
	@Create
	@Begin(flushMode = org.jboss.seam.annotations.FlushModeType.MANUAL, join=true)
	public void init() {

	}

	public void createNew() {
		//    log.debug("Yeni Senet");
		promissory = new PromissoryNote();

		promissory.setSerialNo(sequenceManager
				.getNewSerialNumber(SequenceType.SERIAL_PROMISSORY));
		promissory.setEntryDate(calendarManager.getCurrentDate());
		promissory.setClientPromissoryNote(isClient);
		promissory.getMoney().setCurrency(BaseConsts.SYSTEM_CURRENCY_CODE);
	}
	
	public ChequeStatus[] getChequeStatus() {
		return ChequeStatus.values();
	}

	@Remove
	@Destroy
	public void destroy() {
	}
	
    public void cancel(){
    	promissory=null;
    }
	
	public String close() {

		
		
		
    	if(promissory != null ){
    		
        	if (callerObserveString != null) {
        		events.raiseTransactionSuccessEvent(callerObserveString, promissory);
        		log.debug("Event raised!");
        	} else {
        		log.debug("Event did not raise!");
        	}

    	}
    	
        return BaseConsts.SUCCESS;
	}

	@Out(required=false)
	public PromissoryNote getPromissory() {
		if (promissory == null)
			createNew();
		return promissory;
	}

	@In(required=false)
	public void setPromissory(PromissoryNote promissory) {
		this.promissory = promissory;
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
