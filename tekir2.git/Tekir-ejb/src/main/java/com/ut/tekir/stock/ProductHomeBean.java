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

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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
import org.jboss.seam.international.StatusMessage.Severity;

import com.ut.tekir.entities.BarcodeTxn;
import com.ut.tekir.entities.MetricalConvert;
import com.ut.tekir.entities.PriceItem;
import com.ut.tekir.entities.PriceItemDetail;
import com.ut.tekir.entities.Product;
import com.ut.tekir.entities.ProductDetail;
import com.ut.tekir.entities.ProductGroup;
import com.ut.tekir.entities.ProductType;
import com.ut.tekir.entities.ProductUnit;
import com.ut.tekir.entities.Tax;
import com.ut.tekir.entities.TaxType;
import com.ut.tekir.entities.TradeAction;
import com.ut.tekir.framework.BaseConsts;
import com.ut.tekir.framework.CurrencyManager;
import com.ut.tekir.framework.EntityBase;
import com.ut.tekir.framework.SequenceManager;
import com.ut.tekir.framework.SequenceType;
import com.ut.tekir.general.GeneralSuggestion;
import com.ut.tekir.util.GenerateBarcode;

/**
 * NOT: Ötv hariç ise, diğer tüm vergiler hariç olmalı, eğer
 * ötv dahil ise diğer vergilerin dahil veya hariç olup olmaması
 * seçimlik olmalı!.
 */

/**
 *
 * @author haky
 */
@Stateful
@Name("productHome")
@Scope(value=ScopeType.CONVERSATION)
public class ProductHomeBean extends EntityBase<Product> implements ProductHome<Product> {

	@In(create = true)
    SequenceManager sequenceManager;
	@In
    CurrencyManager currencyManager;
	@In
	GeneralSuggestion generalSuggestion;
    @In(create = true)
    GenerateBarcode generateBarcode;
	
	/**
	 * barkodun kaç tane yazdırılacağı bilgisidir.
	 */
	private int count = 1;
	
    @Create @Begin(join=true,flushMode = org.jboss.seam.annotations.FlushModeType.MANUAL)
    public void init() {
        log.debug("ProductHome Init");
    }
    
    @Out(required=false) 
    public Product getProduct() {
        return getEntity();
    }

    @In(required=false)
    public void setProduct(Product product) {
        setEntity( product );
    }
    
    @Override
    public void createNew() {
        log.debug("Yeni Product");
        entity = new Product();
        entity.setCode(sequenceManager.getNewSerialNumber(SequenceType.SERIAL_CARDS_STOCK));
        entity.setActive(true);
        entity.setProductType(ProductType.Product);
        entity.setUnit("Adet");
        
        createDefaultUnitLine();
    }

    public void generateBarcode() {
    	log.info("Creating barcode for product :{0}", entity.getCaption());
    	entity.setBarcode1(generateBarcode.create("EAN13"));
    	
    	createDefaultUnitLine();
    }

    @Override
    @Transactional
    public String saveAndNew() {
        return super.saveAndNew();
    }

    @Override
    @Transactional
    public String save(){
    	String res = BaseConsts.FAIL;
    	Boolean hata = false;
    	try{
    		
    		Method buyTaxGetter = null;
    		Method sellTaxGetter = null;
    		Method taxIncludedGetter = null;
    		
    		Tax buyTax = null;
    		Tax sellTax = null;
    		Boolean taxIncluded = null;
    		
    		for (int k = 1; k <= 5; k++) {
				buyTaxGetter = entity.getClass().getMethod("getBuyTax"+k);
				sellTaxGetter = entity.getClass().getMethod("getSellTax"+k);
				taxIncludedGetter = entity.getClass().getMethod("getTax"+k+"Included");
				
				buyTax = (Tax)buyTaxGetter.invoke(entity);
				sellTax = (Tax)sellTaxGetter.invoke(entity);
				taxIncluded = (Boolean)taxIncludedGetter.invoke(entity);
				
				if(buyTax != null){
					if(buyTax.getType().equals(TaxType.OTV) && taxIncluded.booleanValue() == false){
						for (int j= 1 ; j <= 5; j++) {
							buyTaxGetter = entity.getClass().getMethod("getBuyTax"+j);
		    				taxIncludedGetter = entity.getClass().getMethod("getTax"+j+"Included");
		    				
		    				buyTax = (Tax)buyTaxGetter.invoke(entity);
		    				taxIncluded = (Boolean)taxIncludedGetter.invoke(entity);
		    				
		    				if(taxIncluded.booleanValue() == true){
		    					facesMessages.add("#{messages['product.message.CheckTaxIncluded']}");
		    					hata = true;
		    				}
						}
					}
				}
				
				if(sellTax != null){
					if(sellTax.getType().equals(TaxType.OTV) && taxIncluded.booleanValue() == false){
						for (int j= 1 ; j <= 5; j++) {
		    				sellTaxGetter = entity.getClass().getMethod("getSellTax"+j);
		    				taxIncludedGetter = entity.getClass().getMethod("getTax"+j+"Included");
		    				
		    				sellTax = (Tax)sellTaxGetter.invoke(entity);
		    				taxIncluded = (Boolean)taxIncludedGetter.invoke(entity);
		    				
		    				if(taxIncluded.booleanValue() == true){
		    					facesMessages.add("#{messages['product.message.CheckTaxIncluded']}");
		    					hata = true;
		    				}
						}
					}
				}
			}
    		
    		for (int i = 0; i < entity.getProductUnitList().size(); i++) {
    			for (int j = i+1; j < entity.getProductUnitList().size(); j++) {
    				if ( entity.getProductUnitList().get(i).getMainUnit().equals(entity.getProductUnitList().get(j).getMainUnit()) ){
    					if (entity.getProductUnitList().get(i).getChangeUnit().equals(entity.getProductUnitList().get(j).getChangeUnit())) {
							facesMessages.add("#{messages['product.message.DuplicateUnit']} {0} #{messages['product.message.Line']} {1} #{messages['product.message.Line']}",
											i + 1, j + 1);
							hata = true;
						}
    				}
    				
    				if ( entity.getProductUnitList().get(i).getMainUnit().equals(entity.getProductUnitList().get(j).getChangeUnit()) ){
    					if ( entity.getProductUnitList().get(i).getChangeUnit().equals(entity.getProductUnitList().get(j).getMainUnit()) ){
    		    			facesMessages.add("#{messages['product.message.DuplicateUnit']} {0} #{messages['product.message.Line']} {1} #{messages['product.message.Line']}", 
									i+1,j+1);
		                    hata = true;
    					}
    				}
				}
			}
    		
    		for (ProductDetail item : entity.getDetailList()) {
    			if(item.getProduct().getDiscountOrExpense().getValue().compareTo(BigDecimal.ZERO) < 0){
    				facesMessages.add("Sıfırdan büyük bir değer girmelisiniz");
					throw new RuntimeException("Hatalar Var! Kayıt gerçekleşmedi!");
    			}
    			
    			if(item.getProduct().getDiscountOrExpense().getCurrency().equals(BaseConsts.SYSTEM_CURRENCY_CODE)) {
                	item.getProduct().getDiscountOrExpense().setLocalAmount(item.getProduct().getDiscountOrExpense().getValue()); 
                } else{
                	if(!generalSuggestion.kurKontrol(entity.getOpenDate())){
                		facesMessages.add("#{messages['general.message.DailyRatesUndefined']}");
                		return BaseConsts.FAIL;
                	}
                	item.getProduct().getDiscountOrExpense().setLocalAmount(currencyManager.convertToLocal(item.getProduct().getDiscountOrExpense().getValue(), 
                																   item.getProduct().getDiscountOrExpense().getCurrency(), 
                																   entity.getOpenDate())); 

                	item.getProduct().getDiscountOrExpense().setLocalAmount(item.getProduct().getDiscountOrExpense().getLocalAmount().setScale(2, RoundingMode.HALF_UP));
                	
                }
    		}
    		
    		if (entity.getBarcode1() != null && entity.getBarcode1().length() > 0) {
    			barcodeExist(entity.getBarcode1());
    		}
    		
    		if (entity.getBarcode2() != null && entity.getBarcode2().length() > 0) {
    			barcodeExist(entity.getBarcode2());
    		}
    		
    		if (entity.getBarcode3() != null && entity.getBarcode3().length() > 0) {
    			barcodeExist(entity.getBarcode3());
    		}
    		
    		if (hata) {
                throw new RuntimeException("Hata!");
    		}

    		setLastSalePurchasePrices();

    		
    		boolean createPriceItem = false;
    		if (entityId() == null) {
    			createPriceItem = true;
    		}
    		
    		res = super.save();
    		if (res.equals(BaseConsts.SUCCESS) && createPriceItem ) {
    			createPriceItem();
    		}
    	} catch (Exception e) {
    		log.error("Hata :", e);
    		facesMessages.add(Severity.ERROR,e.getMessage());
            return BaseConsts.FAIL;    		
    	}
    	
    	return res;
    }
    
    @SuppressWarnings("unchecked")
    @Transactional
    private void createPriceItem() {
    	log.info("Creating price item for product. Product caption :{0}", entity.getCaption());
    	manualFlush();
    	
    	String groupQuery = "";
    	if(entity.getGroup() != null) {
    		groupQuery = "pi.group.id=" + entity.getGroup().getId() + " and ";
    	} else {
    		groupQuery = "pi.group.id is null and ";
    	}
    	
    	List<PriceItem> resultList = entityManager.createQuery("select pi from PriceItem pi where " +
							    							   groupQuery +
							    							   "pi.beginDate <= :requestedDate " +
							    							   "order by beginDate desc")
							    							   .setParameter("requestedDate", entity.getCreateDate())
							    							   .getResultList();
    	
    	PriceItem pi = null;
    	String priceItemCode = "DEFAULT";
    	if (resultList != null && resultList.size() != 0) {
    		pi = resultList.get(0);
    	} else if (resultList == null || resultList.size() == 0){
    		pi = new PriceItem();
    		pi.setAction(TradeAction.Sale);
    		
    		ProductGroup pg = entity.getGroup();
    		if (pg != null) {
    			
    			if (pg.getCode().length()>3) {
    				priceItemCode = pg.getCode().substring(0,3);
    			} else {
    				priceItemCode = pg.getCode();
    			}
    			pi.setGroup(pg);
    		} 
    		pi.setCode(priceItemCode);

    		pi.setBeginDate(new Date());

    		
        	Calendar c = Calendar.getInstance();
        	c.setTime(pi.getBeginDate());
        	c.add(Calendar.MONTH, 3 );
    		pi.setEndDate(c.getTime());
    	}

    	PriceItemDetail detail = new PriceItemDetail();

		detail.setProduct(entity);
		detail.setGrossPrice(entity.getLastSalePrice());

		detail.setOwner(pi);
		pi.getItems().add(detail);
    	
//		if (pi.getId() == null) {
//			entityManager.persist(pi);
//		} else {
//			entityManager.merge(pi);
//		}
		entityManager.flush();
	}
    
    private void setLastSalePurchasePrices() {
		BigDecimal convertedPurchaseAmount = currencyManager.convertToLocal(entity.getLastPurchasePrice().getValue(), 
																			entity.getLastPurchasePrice().getCurrency());
		entity.getLastPurchasePrice().setLocalAmount(convertedPurchaseAmount);

		
		BigDecimal convertedSaleAmount = currencyManager.convertToLocal(entity.getLastSalePrice().getValue(), 
																		entity.getLastSalePrice().getCurrency());
		entity.getLastSalePrice().setLocalAmount(convertedSaleAmount);
    }
    
    private boolean barcodeExist(String aBarcode) throws Exception {
		Long resultSize = null;
    	if (entityId() == null) {
    		
			resultSize = (Long)entityManager.createQuery("select count(id) from Product p where " +
														    "(p.barcode1= :barcode or " +
														    "p.barcode2= :barcode or " +
														    "p.barcode3= :barcode )")
														    .setParameter("barcode", aBarcode)
														    .getSingleResult();
    	} else {
			resultSize = (Long)entityManager.createQuery("select count(id) from Product p where " +
												    "(p.barcode1= :barcode or " +
												    "p.barcode2= :barcode or " +
												    "p.barcode3= :barcode) and " + 
												    "p.id != :id")
													.setParameter("id", entity.getId())
												    .setParameter("barcode", aBarcode)
												    .getSingleResult();

    	}
		if (resultSize.compareTo(Long.parseLong("0")) != 0) {
			throw new Exception(aBarcode + " barkodu daha önce kullanılmış!");
		}
    	return false;
    }
    
	@Override
	public void sendToBarcodeSpool() {
		log.info("Sending to barcode pool, product caption :{0}", entity.getCaption());
		try {
			BarcodeTxn barcodeTxn = new BarcodeTxn();

			barcodeTxn.setProduct(getEntity());
			barcodeTxn.setUnit(getCount());

			entityManager.persist(barcodeTxn);
			entityManager.flush();
			
			log.info("Barkod havuzuna başarılı bir şekilde gönderildi. Ürün kodu : {0}", getEntity().getCaption());
			facesMessages.add("Barkod havuzuna başarılı bir şekilde gönderildi.");
		} catch (Exception e) {
			log.error("Hata :{0}", e);
			facesMessages.add(Severity.ERROR,"Barkod havuzuna gönderirken hata meydana geldi.Hata sebebi:\n{0}",e.getMessage());
		}
	}

	public void copyNameToLabelName() {
		log.debug("Copying code to label.");
		entity.setLabelName(entity.getCode());
	}

	public void copyBuyTaxesToSellTaxes() {
		log.debug("Copying buy taxes to sell taxes.");
		entity.setSellTax(entity.getBuyTax());
		entity.setSellTax2(entity.getBuyTax2());
		entity.setSellTax3(entity.getBuyTax3());
		entity.setSellTax4(entity.getBuyTax4());
		entity.setSellTax5(entity.getBuyTax5());
	}
	
	public void createNewLine() {
        manualFlush();
        if (entity == null) {
            return;
        }

        log.debug("EntityManager.FlushMode : #0", entityManager.getFlushMode());

        ProductUnit it = new ProductUnit();
        it.setProduct(entity);
        entity.getProductUnitList().add(it);
        log.debug("yeni productUnit eklendi");
    }
    
	public void createNewDetailLine() {
		manualFlush();
		if (entity == null) {
			return;
		}
		
		log.debug("EntityManager.FlushMode : #0", entityManager.getFlushMode());
		
		ProductDetail it = new ProductDetail();
		it.setOwner(entity);
		entity.getDetailList().add(it);
		log.debug("yeni productDetail eklendi");
	}
	
    public void manualFlush() {
        Conversation.instance().changeFlushMode(org.jboss.seam.annotations.FlushModeType.MANUAL);
    } 

    public void deleteLine(Integer ix) {
        manualFlush();
        if (entity == null) {
            return;
        }
        log.debug("Kayıt Silinecek IX : {0}", ix);
        log.debug("Toplam Kayıt : {0}", entity.getProductUnitList().size());
        Object o = entity.getProductUnitList().remove(ix.intValue());
        log.debug("Silinen : {0}", o);
        log.debug("Toplam Kayıt : {0}", entity.getProductUnitList().size());
    }
    
    public void deleteDetailLine(Integer ix) {
    	manualFlush();
    	if (entity == null) {
    		return;
    	}
    	log.debug("Kayıt Silinecek IX : {0}", ix);
    	log.debug("Toplam Kayıt : {0}", entity.getDetailList().size());
    	Object o = entity.getDetailList().remove(ix.intValue());
    	log.debug("Silinen : {0}", o);
    	log.debug("Toplam Kayıt : {0}", entity.getDetailList().size());
    }
    
    public void createDefaultUnitLine(){
    	ProductUnit productUnit = null;
    	if(entity.getProductUnitList().size() != 0){
    		productUnit = entity.getProductUnitList().get(0);
    	}
    	
    	if (productUnit == null || productUnit.getDefaultUnit().equals(Boolean.FALSE)) {
    		productUnit = new ProductUnit();
    		productUnit.setDefaultUnit(true);

    		productUnit.setProduct(entity);
    		entity.getProductUnitList().add(0,productUnit);
    	}
		productUnit.setMainUnit(entity.getUnit());
		productUnit.setBarcode1(entity.getBarcode1());
		productUnit.setBarcode2(entity.getBarcode2());
		productUnit.setBarcode3(entity.getBarcode3());
		productUnit.setMainUnitValue(BigDecimal.ONE);
		productUnit.setChangeUnitValue(BigDecimal.ONE);
		productUnit.setChangeUnit(entity.getUnit());
    	
    }
        
    public Boolean isDefault(){
    	
    	if(entity.getProductUnitList().size() != 0){
    		if(entity.getProductUnitList().size() <= 1 && entity.getProductUnitList().get(0).getDefaultUnit().equals(Boolean.TRUE)){
        		return false;
        	}else{
        		return true;
        	}
    	}
    	return false;
    }
    
    public void takeMetricalConvert() {

		List<MetricalConvert> mc = null;
		mc = generalSuggestion.findMetricalConvert(entity.getUnit());
		ProductUnit pUnit = null;
		
		for(MetricalConvert metricItem : mc) {
			pUnit = new ProductUnit();
			
			pUnit.setMainUnit(metricItem.getMainUnit());
			pUnit.setChangeUnit(metricItem.getChangeUnit());
			pUnit.setMainUnitValue(metricItem.getMainUnitValue());
			pUnit.setChangeUnitValue(metricItem.getChangeUnitValue());
			
			pUnit.setProduct(entity);
			entity.getProductUnitList().add(pUnit);
		}

	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}
    
}
