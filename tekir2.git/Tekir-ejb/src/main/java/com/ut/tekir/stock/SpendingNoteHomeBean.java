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

package com.ut.tekir.stock;

import java.math.BigDecimal;

import javax.ejb.Stateful;
import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.Conversation;

import com.ut.tekir.entities.SpendingNote;
import com.ut.tekir.entities.SpendingNoteItem;
import com.ut.tekir.entities.TradeAction;
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
@Name("spendingNoteHome")
@Scope(value = ScopeType.CONVERSATION)
public class SpendingNoteHomeBean extends EntityBase<SpendingNote> implements SpendingNoteHome<SpendingNote> {

    @In(create = true)
    SequenceManager sequenceManager;
    @In
    CalendarManager calendarManager;
    @In
    ProductTxnAction productTxnAction;
    @In
    EntityManager entityManager;

    @Create 
    @Begin(flushMode = org.jboss.seam.annotations.FlushModeType.MANUAL)
    public void init() {

    }

    @Out(required = false)
    public SpendingNote getSpendingNote() {
        return getEntity();
    }

    @In(required = false)
    public void setSpendingNote(SpendingNote SpendingNote) {
        setEntity(SpendingNote);
    }

    @Override
    public void createNew() {
        log.debug("Yeni SpendingNote");

        entity = new SpendingNote();
        entity.setActive(true);
        //TODO:gider fişi yeni seri numarası alınmalı
        entity.setSerial(sequenceManager.getNewSerialNumber(SequenceType.SERIAL_FUND_DEBITCREDIT));
        entity.setDate(calendarManager.getCurrentDate());
        entity.setAction(TradeAction.Spending);
    }

    
    @Override
    public String save() {

        Boolean hata = false;

        try {

            if (entity.getItems().size() == 0) {
                facesMessages.add("En az bir detay girmelisiniz!");
                hata = true;
            }

            for (SpendingNoteItem it : entity.getItems()) {
            	
                if (it.getProduct() == null) {
                    facesMessages.add("Stok seçmelisiniz!");
                    hata = true;
                }

                if (it.getQuantity().getValue() <= 0) {
                    facesMessages.add("Sıfırdan büyük bir değer girmelisiniz");
                    hata = true;
                }
                
                if ( ( it.getAmount().getValue().compareTo(BigDecimal.ZERO) ) < 0) {
                    facesMessages.add("Sıfırdan büyük bir değer girmelisiniz");
                    hata = true;
                }

            }

            if (hata) {
                throw new RuntimeException("Hata!");
            }


        } catch (Exception e) {
            log.error("Hata :", e);
            return BaseConsts.FAIL;
        }


        String res = super.save();

        if (BaseConsts.SUCCESS.equals(res)) {
            productTxnAction.saveSpendingNote(entity);
        }

        return res;

    }
    
    @Override
    public String delete() {

        productTxnAction.deleteSpendingNote(entity);

        return super.delete();
    }


    public void createNewLine() {
        
    	manualFlush();
        
    	if (entity == null) {
            return;
        }

        log.debug("EntityManager.FluashMode : #0", entityManager.getFlushMode());

        SpendingNoteItem it = new SpendingNoteItem();
        it.setOwner(entity);
        it.getAmount().setCurrency(BaseConsts.SYSTEM_CURRENCY_CODE);
        entity.getItems().add(it);
        log.debug("yeni item eklendi");
    }

    public void deleteLine(SpendingNoteItem item) {
 
    	manualFlush();
        
    	if (entity == null) {
            return;
        }
        
    	log.debug("Kayıt Silinecek : {0}", item);
        entity.getItems().remove(item);
    }

    public void selectLine(Integer ix) {
        
    	manualFlush();
        if (entity == null) {
            return;
        }
        log.debug("İşlenecek IX : {0}", ix);

        SpendingNoteItem noteItem = entity.getItems().get(ix);
        
        if( noteItem.getProduct() != null) {
        
        	noteItem.getQuantity().setUnit(noteItem.getProduct().getUnit());
        }

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
