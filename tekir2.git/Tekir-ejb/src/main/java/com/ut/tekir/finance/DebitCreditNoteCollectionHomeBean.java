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

import java.math.BigDecimal;
import java.math.RoundingMode;

import javax.ejb.Stateful;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.core.Conversation;
import org.jboss.seam.faces.FacesMessages;

import com.ut.tekir.entities.DebitCreditNote;
import com.ut.tekir.entities.DebitCreditNoteItem;
import com.ut.tekir.entities.FinanceAction;
import com.ut.tekir.framework.BaseConsts;
import com.ut.tekir.framework.CalendarManager;
import com.ut.tekir.framework.CurrencyManager;
import com.ut.tekir.framework.EntityBase;
import com.ut.tekir.framework.SequenceManager;
import com.ut.tekir.framework.SequenceType;
import com.ut.tekir.general.GeneralSuggestion;

/**
 *
 * @author haky
 */
@Stateful
@Name("debitCreditNoteCollectionHome")
@Scope(value = ScopeType.CONVERSATION)
public class DebitCreditNoteCollectionHomeBean extends EntityBase<DebitCreditNote> implements DebitCreditNoteCollectionHome<DebitCreditNote> {
    
    @In(create = true)
    SequenceManager sequenceManager;
    @In
    CalendarManager calendarManager;
    @In
    CurrencyManager currencyManager;
    @In
    AccountTxnAction accountTxnAction;
    @In
    FinanceTxnAction financeTxnAction;
    @In
    GeneralSuggestion generalSuggestion;
    
    @Create 
    @Begin(join=true,flushMode = org.jboss.seam.annotations.FlushModeType.MANUAL)
    public void init() {

    }
    
    @Out(required = false) 
    public DebitCreditNote getDebitCreditNoteCollection() {
        return getEntity();
    }

    @In(required = false)
    public void setDebitCreditNoteCollection(DebitCreditNote debitCreditNote) {
        setEntity(debitCreditNote);
    }

    @Override
    public void createNew() {
        log.debug("Yeni Payment");
        entity = new DebitCreditNote();
        entity.setActive(true);
        entity.setAction(FinanceAction.Credit);
        entity.setSerial(sequenceManager.getNewSerialNumber( SequenceType.SERIAL_DEBITCREDITNOTE_COLLECTION));
        //entity.setReference(sequenceManager.getNewSerialNumber( SequenceType.REFERENCE_FUND_COLLECTION ));
        entity.setDate(calendarManager.getCurrentDate());
        //TODO: Default Kasa eklenecek.
    }

    @Override
    @Transactional
    public String save() {
    	String res = null;
    	boolean kurKontrol;
    	
        try {
            if (entity.getItems().size() == 0) {
                facesMessages.add("En az bir detay girmelisiniz!");
                throw new RuntimeException("Hatalar Var! Kayıt gerçekleşmedi!");
            }

	    	//günün kurları alınmış mı kontrolü
	        //TODO: işlemin yapıldığı tarih(bugün) mi yoksa entity nin tarihi mi baz alınmalı ?
	    	kurKontrol = generalSuggestion.kurKontrol(entity.getDate());

	    	BigDecimal totalAmount = BigDecimal.ZERO;
            for (DebitCreditNoteItem item : entity.getItems()) {

                if (item.getAmount().getValue().compareTo(BigDecimal.ZERO) < 0) {
                    facesMessages.add("Sıfırdan büyük bir değer girmelisiniz");
                    throw new RuntimeException("Hatalar Var! Kayıt gerçekleşmedi!");
                }

                if(item.getAmount().getCurrency().equals(BaseConsts.SYSTEM_CURRENCY_CODE)) {
                	item.getAmount().setLocalAmount(item.getAmount().getValue()); 
                } else{
                	if(!kurKontrol){
                		facesMessages.add("#{messages['general.message.DailyRatesUndefined']}");
                		return BaseConsts.FAIL;
                	}
                	item.getAmount().setLocalAmount(currencyManager.convertToLocal(item.getAmount().getValue(), 
                																   item.getAmount().getCurrency(), 
                																   entity.getDate())); 

                	item.getAmount().setLocalAmount(item.getAmount().getLocalAmount().setScale(2, RoundingMode.HALF_UP));
                	
                }
                totalAmount = totalAmount.add(item.getAmount().getLocalAmount());
            }
            entity.getTotalAmount().setValue(totalAmount);
            
            res = super.save();
            
            if( BaseConsts.SUCCESS.equals(res) ){
            	financeTxnAction.saveDebitCreditNote(entity);
            }
        } catch (Exception e) {
			FacesMessages.afterPhase();
			facesMessages.clear();
			facesMessages.add(e.getMessage());
			log.error("Hata :", e);
			res = BaseConsts.FAIL;
        }
        return res;
    }

    @Override
    @Transactional
    public String delete() {

        financeTxnAction.deleteDebitCreditNote(entity);

        return super.delete();
    }
    
    public void createNewLine() {
        manualFlush();
        if (entity == null) {
            return;
        }
        DebitCreditNoteItem it = new DebitCreditNoteItem();
        it.setOwner(entity);
        it.getAmount().setCurrency(BaseConsts.SYSTEM_CURRENCY_CODE);
        entity.getItems().add(it);
        log.debug("yeni item eklendi");
    }

    
    public void deleteLine(DebitCreditNote item) {
        manualFlush();
        if (entity == null) {
            return;
        }
        log.debug("Kayıt Silinecek : {0}", item);
        entity.getItems().remove(item);
    }

    public void deleteLine(Integer ix) {
        manualFlush();
        if (entity == null) {
            return;
        }
        log.debug("Kayıt Silinecek IX : {0}", ix);
        log.debug("Toplam Kayıt : {0}", entity.getItems().size());
        Object o = entity.getItems().remove(ix.intValue());
        log.debug("Silinen : {0}", o);
        log.debug("Toplam Kayıt : {0}", entity.getItems().size());
    }
    
    public void manualFlush() {
        Conversation.instance().changeFlushMode(org.jboss.seam.annotations.FlushModeType.MANUAL);
    }



}
