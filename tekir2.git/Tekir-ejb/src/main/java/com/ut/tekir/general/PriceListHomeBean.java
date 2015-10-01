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

import java.util.List;

import javax.ejb.Stateful;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.datamodel.DataModel;

import com.ut.tekir.entities.PriceList;
import com.ut.tekir.framework.EntityHome;

/**
 *
 * @author sinan.yumak
 */
@Stateful
@Name("priceListHome")
@Scope(value=ScopeType.CONVERSATION)
public class PriceListHomeBean extends EntityHome<PriceList> implements PriceListHome<PriceList> {
    
    @DataModel("priceListList")
    private List<PriceList> entityList;
    
    @SuppressWarnings("unchecked")
	@Factory("priceListList")
    public void initPriceListList() {
        log.debug("Preparing Price List...");
        
        setEntityList(getEntityManager().createQuery("select c from PriceList c")
        //.setMaxResults(100)
        //.setHint("org.hibernate.cacheable", true)
        .getResultList());
    }
    
    @Out(required=false)
    public PriceList getPriceList() {
        return getEntity();
    }

    @In(required=false)
    public void setPriceList(PriceList priceList) {
        setEntity( priceList );
    }

    @Override
    public void createNew() {
        log.debug("New price...");
        entity = new PriceList();
    }

    @Override
    public List<PriceList> getEntityList() {
        return entityList;
    }

    public void setEntityList(List<PriceList> entityList) {
        this.entityList = entityList;
    }
    
}
