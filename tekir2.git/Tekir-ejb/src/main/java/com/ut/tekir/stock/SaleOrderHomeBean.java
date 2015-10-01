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

import java.io.FileNotFoundException;
import java.util.Map;
import java.util.HashMap;
import net.sf.jasperreports.engine.JRException;


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
import com.ut.tekir.framework.CurrencyManager;
import com.ut.tekir.framework.JasperHandlerBean;
import com.ut.tekir.framework.EntityBase;
import com.ut.tekir.framework.SequenceManager;
import com.ut.tekir.framework.SequenceType;

/**
 *
 * @author haky
 */
//@Stateful
//@Name("saleOrderHome")
@Scope(value = ScopeType.CONVERSATION)
public class SaleOrderHomeBean extends EntityBase<OrderNote> implements SaleOrderHome<OrderNote> {
    
    @In(create = true)
    SequenceManager sequenceManager;
    @In
    CalendarManager calendarManager;
    @In
    CurrencyManager currencyManager;
    @In
    ProductTxnAction productTxnAction;
    @In
	JasperHandlerBean jasperReport;


    @Create 
    @Begin(join=true,flushMode = org.jboss.seam.annotations.FlushModeType.MANUAL)
    public void init() {

    }

    @Out(required = false)
    public OrderNote getSaleOrder() {
        return getEntity();
    }

    @In(required = false)
    public void setSaleOrder(OrderNote orderNote) {
        setEntity(orderNote);
    }

    @Override
    public void createNew() {
        log.debug("Yeni SaleOrderNote");

        entity = new OrderNote();
        entity.setAction(TradeAction.Sale);
        entity.setActive(true);
        //TODO: Burada sequencetype a yeni bir enum eklenip entity nin serialine atanmalı!
        entity.setSerial(sequenceManager.getNewSerialNumber(SequenceType.SERIAL_SHIPMENT_PURCHASE));
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
        
        for (OrderItem it : entity.getItems()) {

            it.getAmount().setLocalAmount(currencyManager.convertToLocal(it.getAmount().getValue(), it.getAmount().getCurrency(), entity.getDate()));
            
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

    @SuppressWarnings("unchecked")
    public void print() {
		try {

			log.info("Product Transfer Print");

			Map params = new HashMap();
			params.put("invoice", entity.getId());
			jasperReport.printObjectToPDF( "Satis_Siparis_Fisi", "satis_siparis_fisi", params, entity.getItems());

		} catch (JRException ex) {
			log.error("Invoice print error", ex);
			facesMessages.add("İrsaliye yazdırılamadı!");
		} catch (FileNotFoundException e) {
			log.error("Invoice template not found", e);
			facesMessages.add("İrsaliye yazdırma şablonu bulunamadı!");
		}
	}

    public void manualFlush() {
        Conversation.instance().changeFlushMode(org.jboss.seam.annotations.FlushModeType.MANUAL);
    }

}
