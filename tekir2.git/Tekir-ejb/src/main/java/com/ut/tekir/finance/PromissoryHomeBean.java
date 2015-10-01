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

import javax.ejb.Stateful;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;

import com.ut.tekir.entities.ChequeStatus;
import com.ut.tekir.entities.PromissoryNote;
import com.ut.tekir.framework.BaseConsts;
import com.ut.tekir.framework.CalendarManager;
import com.ut.tekir.framework.EntityBase;
import com.ut.tekir.framework.SequenceManager;
import com.ut.tekir.framework.SequenceType;

/**
 *
 * @author selman
 */
@Stateful
@Name("promissoryHome")
@Scope(value = ScopeType.CONVERSATION)
public class PromissoryHomeBean extends EntityBase<PromissoryNote> implements PromissoryHome<PromissoryNote> {
	
    @In(create = true)
    SequenceManager sequenceManager;
    @In
    CalendarManager calendarManager;

    @Create 
    @Begin(flushMode = org.jboss.seam.annotations.FlushModeType.MANUAL, join=true)
    public void init() {

    }
    
    @Out(required = false) 
    public PromissoryNote getPromissoryNote() {
        return getEntity();
    }

    @In(required = false,create=true)
    public void setPromissoryNote(PromissoryNote promissoryNote) {
        setEntity(promissoryNote);
    }
    
    public ChequeStatus[] getChequeStatus(){
    	return ChequeStatus.values();
    }
    
      @Override
    public void createNew() {
        log.debug("Yeni Senet");
        entity = new PromissoryNote();
        entity.setReferenceNo(sequenceManager.getNewSerialNumber( SequenceType.SERIAL_PROMISSORY));
        entity.getMoney().setCurrency(BaseConsts.SYSTEM_CURRENCY_CODE);
        entity.setEntryDate(calendarManager.getCurrentDate());

    }
      
}
