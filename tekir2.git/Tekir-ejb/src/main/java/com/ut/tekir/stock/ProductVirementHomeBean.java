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

import com.ut.tekir.entities.ProductType;
import com.ut.tekir.entities.ProductVirement;
import com.ut.tekir.entities.ProductVirementItem;
import com.ut.tekir.framework.BaseConsts;
import com.ut.tekir.framework.CalendarManager;
import com.ut.tekir.framework.EntityBase;
import com.ut.tekir.framework.SequenceManager;
import com.ut.tekir.framework.SequenceType;
import javax.ejb.Stateful;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.Conversation;

/**
 *
 * @author nexus
 */
@Stateful
@Name("productVirementHome")
@Scope(value = ScopeType.CONVERSATION)
public class ProductVirementHomeBean extends EntityBase<ProductVirement> implements ProductVirementHome<ProductVirement> {

    @In(create = true)
    SequenceManager sequenceManager;
    @In
    CalendarManager calendarManager;
    @In
    ProductTxnAction productTxnAction;
    @In(create=true)
    StockSuggestion stockSuggestion;

    @Create
    @Begin(join=true, flushMode = org.jboss.seam.annotations.FlushModeType.MANUAL)
    public void init() {

    }

    @Out(required = false)
    public ProductVirement getProductVirement() {
        return getEntity();
    }

    @Override
    public void createNew() {
        log.debug("Yeni ProductVirement");
        entity = new ProductVirement();
        entity.setActive(true);
        entity.setSerial(sequenceManager.getNewSerialNumber(SequenceType.SERIAL_PRODUCT_VIREMENT));
        //entity.setReference(sequenceManager.getNewSerialNumber(SequenceType.REFERENCE_PRODUCT_VIREMENT));
        entity.setDate(calendarManager.getCurrentDate());
        stockSuggestion.setProductType(ProductType.Product);
        stockSuggestion.setDisableProductCombo(true);
    }

    @Override
    public String save() {

        try {
            if (entity.getItems().size() == 0) {
                facesMessages.add("En az bir detay girmelisiniz!");
                throw new RuntimeException("Hatalar Var! Kayıt gerçekleşmedi!");
            }

            for (ProductVirementItem it : entity.getItems()) {

                if (it.getFromProduct() == null && it.getToProduct() == null) {
                    facesMessages.add("En az bir stok seçmelisiniz!");
                    throw new RuntimeException("Hatalar Var! Kayıt gerçekleşmedi!");
                }
                
            }
        } catch (Exception e) {
            log.error("Hata :", e);
            return BaseConsts.FAIL;
        }

		String res = super.save();
		
	    if (BaseConsts.SUCCESS.equals(res)) {
	        productTxnAction.saveProductVirement(entity);
	    }
	    return res;

    }

    @Override
    public String delete() {

    	productTxnAction.deleteProductVirement(entity);

        return super.delete();
    }

    public void createNewLine() {
        manualFlush();
        if (entity == null) {
            return;
        }

        log.debug("EntityManager.FluashMode : #0", entityManager.getFlushMode());

        ProductVirementItem it = new ProductVirementItem();
        it.setOwner(entity);
        //it.getAmount().setCurrency(BaseConsts.SYSTEM_CURRENCY_CODE);
        entity.getItems().add(it);
        log.debug("yeni item eklendi");
    }

    public void deleteLine(ProductVirementItem item) {
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
    
    public void selectFromProduct(Integer ix) {
        manualFlush();
        if (entity == null) {
            return;
        }
        log.debug("İşlenecek IX : {0}", ix);

        ProductVirementItem o = entity.getItems().get(ix.intValue());
        if (o != null && o.getFromProduct() != null) {
            o.getQuantity().setUnit(o.getFromProduct().getUnit());
        }
    }
    
    public void selectToProduct(Integer ix) {
        manualFlush();
        if (entity == null) {
            return;
        }
        log.debug("İşlenecek IX : {0}", ix);

        ProductVirementItem o = entity.getItems().get(ix.intValue());
        if (o != null && o.getToProduct() != null) {
            o.getQuantity().setUnit(o.getToProduct().getUnit());
        }
    }
    
    public void manualFlush() {
        Conversation.instance().changeFlushMode(org.jboss.seam.annotations.FlushModeType.MANUAL);
    }

	public void selectProduct(Integer ix) {
		// TODO Auto-generated method stub
		
	}

	public void setProductVirement(ProductVirement productVirement) {
		// TODO Auto-generated method stub
		
	}

}
