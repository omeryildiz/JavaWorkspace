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
import java.io.FileNotFoundException;
import java.util.Map;
import java.util.HashMap;
import net.sf.jasperreports.engine.JRException;


import javax.ejb.Stateful;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.Conversation;

import com.ut.tekir.entities.ProductTransfer;
import com.ut.tekir.entities.ProductTransferItem;
import com.ut.tekir.entities.ProductType;
import com.ut.tekir.framework.BaseConsts;
import com.ut.tekir.framework.CalendarManager;
import com.ut.tekir.framework.EntityBase;
import com.ut.tekir.framework.JasperHandlerBean;
import com.ut.tekir.framework.SequenceManager;
import com.ut.tekir.framework.SequenceType;

/**
 *
 * @author haky
 */
@Stateful
@Name("productTransferHome")
@Scope(value = ScopeType.CONVERSATION)
public class ProductTransferHomeBean extends EntityBase<ProductTransfer> implements ProductTransferHome<ProductTransfer> {

    @In(create = true)
    SequenceManager sequenceManager;
    @In
    CalendarManager calendarManager;
    @In
    ProductTxnAction productTxnAction;
    @In(create=true)
    StockSuggestion stockSuggestion;

    @In
	JasperHandlerBean jasperReport;

    @Create 
    @Begin(join=true, flushMode = org.jboss.seam.annotations.FlushModeType.MANUAL)
    public void init() {

    }

    @Out(required = false)
    public ProductTransfer getProductTransfer() {
        return getEntity();
    }

    @In(required = false)
    public void setProductTransfer(ProductTransfer productTransfer) {
        setEntity(productTransfer);
    }

    @Override
    public void createNew() {
        log.debug("Yeni FundTransfer");

        entity = new ProductTransfer();
        entity.setActive(true);
        entity.setSerial(sequenceManager.getNewSerialNumber(SequenceType.SERIAL_SHIPMENT_TRANSFER));
        //entity.setReference(sequenceManager.getNewSerialNumber(SequenceType.REFERENCE_SHIPMENT_TRANSFER));
        entity.setDate(calendarManager.getCurrentDate());
        entity.setTime(new java.util.Date());
        stockSuggestion.setProductType(ProductType.Product);
        stockSuggestion.setDisableProductCombo(true);
    }

    @Override
    public String save() {
        //TODO: Hatalara dil desteği eklenecek
    	String res = null;
    	Boolean hata = false;

        try {
            if (entity.getFromWarehouse() != null && entity.getFromWarehouse().equals(entity.getToWarehouse())) {
                facesMessages.add("Giriş ve Çıkış deposu aynı olamaz!");
                hata = true;
            }

            if (entity.getItems().size() == 0) {
                facesMessages.add("En az bir detay girmelisiniz!");
                hata = true;
            }

            for (ProductTransferItem it : entity.getItems()) {

                if (it.getProduct() == null) {
                    facesMessages.add("Srok seçmelisiniz!");
                    hata = true;
                }

                if (it.getQuantity().getValue() <= 0) {
                    facesMessages.add("Sıfırdan büyük bir değer girmelisiniz");
                    hata = true;
                }

            }

            if (hata) {
                throw new RuntimeException("Hata!");
            }

            res = super.save();
            
            if (BaseConsts.SUCCESS.equals(res)) {
            	productTxnAction.saveProductTransfer(getEntity());
            }
        } catch (Exception e) {
            log.error("Hata :", e);
            return BaseConsts.FAIL;
        }
        return res;
    }

    @Override
    public String delete() {

        productTxnAction.deleteProductTransfer(entity);

        return super.delete();
    }

    public void createNewLine() {
        manualFlush();
        if (entity == null) {
            return;
        }

        log.debug("EntityManager.FlushMode : #0", entityManager.getFlushMode());

        ProductTransferItem it = new ProductTransferItem();
        it.setOwner(entity);
        entity.getItems().add(it);
        log.debug("yeni item eklendi");
    }

    public void deleteLine(ProductTransferItem item) {
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

        ProductTransferItem o = entity.getItems().get(ix.intValue());
        if (o != null && o.getProduct() != null) {
            o.getQuantity().setUnit(o.getProduct().getUnit());
        }
    }

    public void manualFlush() {
        Conversation.instance().changeFlushMode(org.jboss.seam.annotations.FlushModeType.MANUAL);
    }
    
	@SuppressWarnings("unchecked")
	public void print() {
		try {

			log.info("Product Transfer Print");

			Map params = new HashMap();
			params.put("invoice", entity.getId());
			jasperReport.printObjectToPDF( "Depo_Sevk_Irsaliyesi_" + entity.getReference(), "depo_sevk_irsaliyesi", params, entity.getItems());

		} catch (JRException ex) {
			log.error("Invoice print error", ex);
			facesMessages.add("İrsaliye yazdırılamadı!");
		} catch (FileNotFoundException e) {
			log.error("Invoice template not found", e);
			facesMessages.add("İrsaliye yazdırma şablonu bulunamadı!");
		}
	}

}
