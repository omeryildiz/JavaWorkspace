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
import java.util.List;

import javax.ejb.Stateful;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.Conversation;
import org.jboss.seam.international.StatusMessage.Severity;

import com.ut.tekir.entities.BarcodeTxn;
import com.ut.tekir.entities.MoneySet;
import com.ut.tekir.entities.PriceItem;
import com.ut.tekir.entities.PriceItemDetail;
import com.ut.tekir.entities.Product;
import com.ut.tekir.entities.ProductType;
import com.ut.tekir.entities.TradeAction;
import com.ut.tekir.framework.BaseConsts;
import com.ut.tekir.framework.EntityBase;
import com.ut.tekir.general.GeneralSuggestion;

/**
 * Fiyat listesi home bileşenidir.
 * 
 * @author sinan.yumak
 */
@Stateful
@Name("priceItemHome")
@Scope(value = ScopeType.CONVERSATION)
public class PriceItemHomeBean extends EntityBase<PriceItem> implements PriceItemHome<PriceItem> {

	@In(create=true)
    StockSuggestion stockSuggestion;
    @In
    GeneralSuggestion generalSuggestion;

    private TradeAction actionType;
    private BigDecimal price;
    private String code;
    private Double discountRate;

    private boolean increase = false;
    private boolean sendToSpool = false;
    private boolean discountType = true;

    @Create 
    @Begin(join=true,flushMode = org.jboss.seam.annotations.FlushModeType.MANUAL)
    public void init() {
    }
  
    @Out(required = false)
    public PriceItem getPriceItem() {
        return getEntity();
    }

    @In(required = false)
    public void setPriceItem(PriceItem priceItem) {
        setEntity(priceItem);
    }

    @Override
    public void createNew() {
        log.debug("New Price Item");

        entity = new PriceItem();
        entity.setActive(true);

        entity.setAction(actionType);
        log.debug("ActionType : #0, action: #1", actionType, entity.getAction() );
        stockSuggestion.setProductType(ProductType.Product);
        stockSuggestion.setDisableProductCombo(true);
    }

    @Override
    public void bringAllProducts(){
        List<Product> productList;
        if (entity.getGroup() != null){
            productList = generalSuggestion.getProductList(entity.getGroup().getId());

            Boolean varmi;
            for(Product p : productList){
                varmi = false;
                for(int i=0; i<entity.getItems().size(); i++){
                    if(entity.getItems().get(i).getProduct().equals(p)){
                        varmi=true;
                        break;
                    }
                    else if(!p.getGroup().equals(entity.getGroup())){
                        varmi=true;
                        break;
                    }
                }
                if(varmi == false){
                    PriceItemDetail detail = new PriceItemDetail();
                    detail.setProduct(p);
                    detail.setOwner(entity);
                    entity.getItems().add(detail);

                }
            }
        }
        else{
            productList = generalSuggestion.getProductList();

            Boolean varmi;
            for(Product p : productList){
                varmi = false;
                for(int i=0; i<entity.getItems().size(); i++){
                    if(entity.getItems().get(i).getProduct().equals(p)){
                        varmi=true;
                        break;
                    }
                   
                }
                if(varmi == false){
                    PriceItemDetail detail = new PriceItemDetail();
                    detail.setProduct(p);
                    detail.setOwner(entity);
                    entity.getItems().add(detail);

                }
            }
        }

       
    }

    @Override
    public String copyProducts(){

        String res = BaseConsts.SUCCESS;

        if (entity != null) {
            PriceItem newPriceItem = new PriceItem();

            newPriceItem = new PriceItem();
            newPriceItem.setActive(true);

            newPriceItem.setBeginDate(entity.getBeginDate());
            newPriceItem.setEndDate(entity.getEndDate());
            newPriceItem.setCalcType(entity.getCalcType());
            newPriceItem.setDefaultItem(entity.getDefaultItem());
            newPriceItem.setGroup(entity.getGroup());
            newPriceItem.setInfo(entity.getInfo());
            newPriceItem.setOwner(entity.getOwner());
            newPriceItem.setCode(code);

            PriceItemDetail det = null;
                for( PriceItemDetail item : entity.getItems()){
                    det = new PriceItemDetail();
                    det.setDiscountPrice(item.getDiscountPrice());
                    det.setDiscountRate(item.getDiscountRate());
                    det.setDiscountType(item.getDiscountType());
                    calculateNewPrice(det.getGrossPrice(),item.getGrossPrice());
                    det.setHasDiscount(item.getHasDiscount());
                    det.setNetPrice(item.getNetPrice());
                    det.setOwner(newPriceItem);
                    det.setProduct(item.getProduct());
                    det.setSendToSpool(item.isSendToSpool());
                    newPriceItem.getItems().add(det);
                }

            setEntity(newPriceItem);
        }
        return res;
    }

    private void calculateNewPrice(MoneySet aGrossPrice , MoneySet bGrossPrice) {

        if (increase) {

            if (discountType) {
                BigDecimal increaseMultiplier = BigDecimal.valueOf(discountRate/100);
                aGrossPrice.setValue(bGrossPrice.getValue().add(bGrossPrice.getValue().multiply(increaseMultiplier)));
            } else {
                aGrossPrice.setValue(bGrossPrice.getValue().add(price));
            }
        } else {
            if (discountType) {
                BigDecimal increaseDivide = BigDecimal.valueOf(discountRate/100).negate();
                aGrossPrice.setValue(bGrossPrice.getValue().add(bGrossPrice.getValue().multiply(increaseDivide)));
            } else {
                aGrossPrice.setValue(bGrossPrice.getValue().add(price.negate()));
            }
        }
    }

    
    @Override
    public String save() {
    	String res = BaseConsts.SUCCESS;
    	
        Boolean hata = false;
        try {
            
            if (entity.getItems().size() == 0) {
                facesMessages.add("En az bir detay girmelisiniz!");
                hata = true;
            }

            for (PriceItemDetail it : entity.getItems()) {

                if (it.getProduct() == null) {
                    facesMessages.add("Stok seçmelisiniz!");
                    hata = true;
                }
            }

            if (hata) {
                throw new RuntimeException("Hata!");
            } 
        
            res = super.save();

            if (entity.getDefaultItem()) {
            	entityManager.createQuery("update PriceItem set defaultItem = false where " +
            							  "defaultItem = true and " +
            							  "id != :id")
            							  .setParameter("id", entity.getId())
            							  .executeUpdate();
            }

        } catch (Exception e) {
            log.error("Hata :", e);
            return BaseConsts.FAIL;
        }
        return res;
    }

    @Override
    public String delete() {
        return super.delete();
    }

    public void createNewLine() {
        manualFlush();
        if (entity == null) {
            return;
        }

        log.debug("EntityManager.FlushMode : #0", entityManager.getFlushMode());

        PriceItemDetail it = new PriceItemDetail();
        it.setOwner(entity);
        entity.getItems().add(it);
        log.debug("yeni item eklendi");
    }

    public void deleteLine(PriceItemDetail item) {
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
        entity.getItems().remove(ix.intValue());
    }

    public void toggleSendToSpool() {
    	for (PriceItemDetail item : entity.getItems()) {
    		item.setSendToSpool(sendToSpool);
    	}
    }

	public void sendToBarcodeSpool() {
		log.info("Sending to barcode pool");
		try {

			BarcodeTxn barcodeTxn = null;
			for (PriceItemDetail item : entity.getItems()) {
				if (item.isSendToSpool()) {
					barcodeTxn = new BarcodeTxn();
					
					barcodeTxn.setProduct(item.getProduct());
					//Bunu sabit olarak 1 almamalı.
					barcodeTxn.setUnit(1);
					
					entityManager.persist(barcodeTxn);
				}
			}
			entityManager.flush();
			
			log.info("Barkod havuzuna başarılı bir şekilde gönderildi.");
			facesMessages.add("Barkod havuzuna başarılı bir şekilde gönderildi.");
		} catch (Exception e) {
			log.error("Hata :{0}", e);
			facesMessages.add(Severity.ERROR,"Barkod havuzuna gönderirken hata meydana geldi.Hata sebebi:\n{0}",e.getMessage());
		}
	}

    
    public void manualFlush() {
        Conversation.instance().changeFlushMode(org.jboss.seam.annotations.FlushModeType.MANUAL);
    }

	public boolean isSendToSpool() {
		return sendToSpool;
	}

	public void setSendToSpool(boolean sendToSpool) {
		this.sendToSpool = sendToSpool;
	}

    public Double getDiscountRate() {
        return discountRate;
    }

    public void setDiscountRate(Double discountRate) {
        this.discountRate = discountRate;
    }

    public boolean getIncrease() {
        return increase;
    }

    public void setIncrease(boolean increase) {
        this.increase = increase;
    }

    public boolean getDiscountType() {
        return discountType;
    }

    public void setDiscountType(boolean discountType) {
        this.discountType = discountType;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

	public TradeAction getActionType() {
		return actionType;
	}

	public void setActionType(TradeAction actionType) {
		this.actionType = actionType;
	}
	
}
