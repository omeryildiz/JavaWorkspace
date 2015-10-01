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

import javax.ejb.Stateful;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.Conversation;

import com.ut.tekir.entities.OrderItem;
import com.ut.tekir.entities.OrderNote;
import com.ut.tekir.entities.TradeAction;
import com.ut.tekir.framework.BaseConsts;
import com.ut.tekir.framework.CalendarManager;
import com.ut.tekir.framework.EntityBase;
import com.ut.tekir.framework.SequenceManager;
import com.ut.tekir.framework.SequenceType;

/**
 *
 * @author haky
 */
@Stateful
@Name("purchaseOrderHome")
@Scope(value = ScopeType.CONVERSATION)
public class PurchaseOrderHomeBean extends EntityBase<OrderNote> implements PurchaseOrderHome<OrderNote>{
    @In(create = true)
    SequenceManager sequenceManager;
    @In
    CalendarManager calendarManager;
    @In
    ProductTxnAction productTxnAction;

    @Create 
    @Begin(join=true,flushMode = org.jboss.seam.annotations.FlushModeType.MANUAL)
    public void init() {

    }

    @Out(required = false)
    public OrderNote getPurchaseOrder() {
        return getEntity();
    }

    @In(required = false)
    public void setPurchaseOrder(OrderNote orderNote) {
        setEntity(orderNote);
    }

    @Override
    public void createNew() {
        log.debug("Yeni OrderNote");

        entity = new OrderNote();
        entity.setAction(TradeAction.Purchase);
        entity.setActive(true);
        //TODO: Burada sequencetype a yeni bir enum eklenip entity nin serialine atanmalı!
        entity.setSerial(sequenceManager.getNewSerialNumber(SequenceType.SERIAL_ORDER_PURCHASE));
        //entity.setReference(sequenceManager.getNewSerialNumber(SequenceType.REFERENCE_SHIPMENT_PURCHASE));
        entity.setDate(calendarManager.getCurrentDate());

    }

    @Override
    public String save() {

        //TODO: Hatalara dil desteği eklenecek
        Boolean hata = false;

        try {
            
            if (entity.getItems().size() == 0) {
                facesMessages.add("En az bir detay girmelisiniz!");
                hata = true;
            }

            //TODO: Metinlere dildesteği eklenecek
            for (OrderItem it : entity.getItems()) {

                if (it.getProduct() == null) {
                    facesMessages.add("Stok seçmelisiniz!");
                    hata = true;
                }

                if (it.getQuantity().getValue() <= 0) {
                    facesMessages.add("Sıfırdan büyük bir değer girmelisiniz");
                    hata = true;
                }

            }

            if (hata) {
                throw new RuntimeException("Hata!");
            } else if (entity.getId() == null ){
            	
	         	   for (OrderItem it : entity.getItems()) {
	         		   
	         		   it.setApprovedQuantity( it.getQuantity().getValue() );
	         	   }
	         	   
            }

        } catch (Exception e) {
            log.error("Hata :", e);
            return BaseConsts.FAIL;
        }

        
        String res = super.save();

        return res;

    }

public void createNewLine() {
        manualFlush();
        if (entity == null) {
            return;
        }

        log.debug("EntityManager.FlushMode : #0", entityManager.getFlushMode());

        OrderItem it = new OrderItem();
        it.setOwner(entity);
        entity.getItems().add(it);
        log.debug("yeni item eklendi");
    }

    public void deleteLine(OrderItem item) {
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

    public void selectProduct(Integer ix) {
        manualFlush();
        if (entity == null) {
            return;
        }
        log.debug("İşlenecek IX : {0}", ix);

        OrderItem o = entity.getItems().get(ix.intValue());
        if (o != null && o.getProduct() != null) {
            o.getQuantity().setUnit(o.getProduct().getUnit());
        }
    }

    public void manualFlush() {
        Conversation.instance().changeFlushMode(org.jboss.seam.annotations.FlushModeType.MANUAL);
    }

}
