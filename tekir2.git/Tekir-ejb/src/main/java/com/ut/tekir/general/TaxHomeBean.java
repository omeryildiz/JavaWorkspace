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

package com.ut.tekir.general;

import com.ut.tekir.entities.Tax;
import com.ut.tekir.entities.TaxKind;
import com.ut.tekir.entities.TaxRate;
import com.ut.tekir.framework.BaseConsts;
import com.ut.tekir.framework.EntityHome;

import java.math.BigDecimal;
import java.util.List;
import javax.ejb.Stateful;
import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.core.Conversation;

/**
 *
 * @author haky
 */
@Stateful
@Name("taxHome")
@Scope(value=ScopeType.CONVERSATION)
public class TaxHomeBean extends EntityHome<Tax> implements TaxHome<Tax>{
    
	@In
	EntityManager entityManager;
	
    @DataModel("taxList")
    private List<Tax> entityList;
    
    @SuppressWarnings("unchecked")
	@Factory("taxList")
    public void initTaxList() {
        log.debug("Tax Listesi hazırlanıyor...");
        
        setEntityList(entityManager.createQuery("select c from Tax c")
        //.setMaxResults(100)
        .setHint("org.hibernate.cacheable", false)
        .getResultList());
    }
    
    @Create
    public void init() {
    	manualFlush();
    }
    
    @Out(required=false)
    public Tax getTax() {
        return getEntity();
    }

    @In(required=false)
    public void setTax(Tax tax) {
        setEntity( tax );
    }
    
        
    @Override
    public void createNew() {
        log.debug("Yeni Birim");
        entity = new Tax();
        entity.setActive(true);
        
//        TaxRate rate = new TaxRate();
//        rate.setTax(entity);
//        entity.getRates().add(rate);
    }
    
    public TaxKind[] getTaxKindList() {
    	return TaxKind.values();
    }
    
	public void createNewLine() {
        manualFlush();
        if (entity == null) {
            return;
        }

        log.debug("EntityManager.FlushMode : #0", entityManager.getFlushMode());

        TaxRate it = new TaxRate();
        it.setTax(entity);
        entity.getRates().add(it);
        log.debug("yeni taxRate eklendi");
    }
	
    public void deleteLine(Integer ix) {
        manualFlush();
        if (entity == null) {
            return;
        }
        log.debug("Kayıt Silinecek IX : {0}", ix);
        log.debug("Toplam Kayıt : {0}", entity.getRates().size());
        Object o = entity.getRates().remove(ix.intValue());
        log.debug("Silinen : {0}", o);
        log.debug("Toplam Kayıt : {0}", entity.getRates().size());
    }
    
    @Override
    public String save() {
    	
    	Boolean hata = false;
    	
    	try{
    		if (entity.getRates().size() == 0) {
                facesMessages.add("En az bir detay girmelisiniz!");
                hata = true;
            }
    		
    		for (int i = 0; i < entity.getRates().size(); i++) {
    			if(entity.getRates().get(i).getBeginDate() == null || entity.getRates().get(i).getEndDate() == null){
    				facesMessages.add("#{messages['tax.message.EmptyDate']} {0} #{messages['tax.message.Line']}",i);
                    hata = true;
    			}
    			
    			for (int j = i+1; j < entity.getRates().size(); j++) {
    				
    				if(entity.getRates().get(i).getBeginDate().before(entity.getRates().get(j).getBeginDate())){
    					if(entity.getRates().get(i).getEndDate().after(entity.getRates().get(j).getBeginDate())){
    						facesMessages.add("#{messages['tax.message.BeforeBeginDate']} {0} #{messages['tax.message.Line']} {1} #{messages['tax.message.Line']}", i + 1, j + 1);
                            hata = true;
    					}
    				}
    				
    				if(entity.getRates().get(i).getBeginDate().after(entity.getRates().get(j).getEndDate())){
    					if(entity.getRates().get(i).getEndDate().before(entity.getRates().get(j).getEndDate())){
    						facesMessages.add("#{messages['tax.message.AfterEndDate']} {0} #{messages['tax.message.Line']} {1} #{messages['tax.message.Line']}", i + 1, j + 1);
                            hata = true;
    					}
    				}
    				
    				if(entity.getRates().get(i).getBeginDate().after(entity.getRates().get(j).getBeginDate())){
    					if(entity.getRates().get(i).getEndDate().before(entity.getRates().get(j).getEndDate())){
    						facesMessages.add("#{messages['tax.message.BetweenDate']} {0} #{messages['tax.message.Line']} {1} #{messages['tax.message.Line']}", i + 1, j + 1);
    						hata = true;
    					}
    				}
    			}
    			
    			if(entity.getRates().get(i).getBeginDate().after(entity.getRates().get(i).getEndDate())){
    				facesMessages.add("#{messages['tax.message.WrongDateInterval']} {0} #{messages['tax.message.Line']}",i);
                    hata = true;
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
    	initTaxList();
    	return res;
    }

    @Override
    public List<Tax> getEntityList() {
        return entityList;
    }

    public void setEntityList(List<Tax> entityList) {
        this.entityList = entityList;
    }
    
    /*
     *   Bu fonksiyon seti pansuman tedbir daha sonradan gerekli GUI yazıldığında rateler kendi başlarına çalışıyor olacaklar...
     */
    public BigDecimal getRate(){
        return entity.getRates().get(0).getRate();
    }
    
    public void setRate(BigDecimal rate){
        entity.getRates().get(0).setRate( rate );
    }
	
    private void manualFlush() {
        Conversation.instance().changeFlushMode(org.jboss.seam.annotations.FlushModeType.MANUAL);
    }
}
