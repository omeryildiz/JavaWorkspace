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

import com.ut.tekir.entities.Currency;
import com.ut.tekir.framework.EntityHome;
import java.util.List;
import javax.ejb.Stateful;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.datamodel.DataModel;

/**
 * Currency Home Bean
 * @author haky
 */
@Stateful
@Name("currencyHome")
@Scope(value=ScopeType.CONVERSATION)
public class CurrencyHomeBean extends EntityHome<Currency> implements CurrencyHome<Currency> {

	@Create
	public void init() {
	}
	
    @Out(required=false)
    public Currency getCurrency(){
        return getEntity();
    }
    
    @In(required=false)
    public void setCurrency( Currency ccy ){
        setEntity(ccy);
    }
    
    @DataModel("currencyList")
    private List<Currency> entityList;
    
    @SuppressWarnings("unchecked")
	@Factory("currencyList")
    public void initCurrencyList() {
        log.debug("Currency Listesi hazırlanıyor...");
        
        entityList = getEntityManager().createQuery("select c from Currency c")
        //.setMaxResults(100)
        //.setHint("org.hibernate.cacheable", true)
        .getResultList();
    }
    
    
    @Override
    public void createNew(){
        entity = new Currency();
        //entity.setId(null);
        entity.setActive(true);
        //entity.setPip(1);
    }
    
    @Override
    public List<Currency> getEntityList() {
        return entityList;
    }

    public void setEntityList(List<Currency> entityList) {
        this.entityList = entityList;
    }
    
}
