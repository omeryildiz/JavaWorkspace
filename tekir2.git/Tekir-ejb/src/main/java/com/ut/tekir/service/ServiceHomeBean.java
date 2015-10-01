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

package com.ut.tekir.service;

import java.lang.reflect.Method;
import java.math.BigDecimal;

import com.ut.tekir.entities.Product;
import com.ut.tekir.entities.ProductType;
import com.ut.tekir.entities.ProductUnit;
import com.ut.tekir.entities.Tax;
import com.ut.tekir.entities.TaxType;
import com.ut.tekir.framework.BaseConsts;
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
 * @author haky
 */
@Stateful
@Name("serviceHome")
@Scope(value=ScopeType.CONVERSATION)
public class ServiceHomeBean extends EntityBase<Product> implements ServiceHome<Product>{

	@In(create = true)
    SequenceManager sequenceManager;
	
    @Create @Begin(join=true,flushMode = org.jboss.seam.annotations.FlushModeType.MANUAL)
    public void init() {
        log.debug("ServiceHome Init");
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
        log.debug("Yeni Servis");
        entity = new Product();
        entity.setCode(sequenceManager.getNewSerialNumber(SequenceType.SERIAL_CARDS_SERVICE));
        entity.setActive(true);
        entity.setProductType(ProductType.Service);
    }
    
    @Override
    public String save() {
    	
    	entity.setBuyTax(entity.getSellTax());
    	entity.setBuyTax2(entity.getSellTax2());
    	entity.setBuyTax3(entity.getSellTax3());
    	entity.setBuyTax4(entity.getSellTax4());
    	entity.setBuyTax5(entity.getSellTax5());
    	
    	Boolean hata = false;
    	try{

    		Method sellTaxGetter = null;
    		Method taxIncludedGetter = null;
    		
    		Tax sellTax = null;
    		Boolean taxIncluded = null;
    		
    		for (int k = 1; k <= 5; k++) {
				sellTaxGetter = entity.getClass().getMethod("getSellTax"+k);
				taxIncludedGetter = entity.getClass().getMethod("getTax"+k+"Included");
				
				sellTax = (Tax)sellTaxGetter.invoke(entity);
				taxIncluded = (Boolean)taxIncludedGetter.invoke(entity);
				
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
    		
    		if (hata) {
                throw new RuntimeException("Hata!");
    		}
    	}catch (Exception e) {
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

        ProductUnit it = new ProductUnit();
        it.setProduct(entity);
        entity.getProductUnitList().add(it);
        log.debug("yeni productUnit eklendi");
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
    
}
