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

import com.ut.tekir.entities.CurrencyPair;
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
 * Currency Pair Home Bean
 * @author haky
 */
@Stateful
@Name("currencyPairHome")
@Scope(value=ScopeType.CONVERSATION)
public class CurrencyPairHomeBean extends EntityHome<CurrencyPair> implements CurrencyPairHome<CurrencyPair>{
    
	@Create
    public void init() {
    }
    
    @Out(required=false)
    public CurrencyPair getCurrencyPair(){
        return getEntity();
    }
    
    @In(required=false)
    public void setCurrencyPair( CurrencyPair ccy ){
        setEntity(ccy);
    }
    
    @DataModel("currencyPairList")
    private List<CurrencyPair> entityList;
    
    @SuppressWarnings("unchecked")
	@Factory("currencyPairList")
    public void initCurrencyPairList() {
        log.debug("CurrencyPair Listesi hazırlanıyor...");
        
        entityList = getEntityManager().createQuery("select c from CurrencyPair c")
        //.setMaxResults(100)
        //.setHint("org.hibernate.cacheable", true)
        .getResultList();
    }
    
    
    
    @Override
    public void createNew(){
        entity = new CurrencyPair();
        entity.setId(null);
        entity.setActive(true);
        
    }
    
    @Override
    public List<CurrencyPair> getEntityList() {
        return entityList;
    }

    public void setEntityList(List<CurrencyPair> entityList) {
        this.entityList = entityList;
    }
    
}
