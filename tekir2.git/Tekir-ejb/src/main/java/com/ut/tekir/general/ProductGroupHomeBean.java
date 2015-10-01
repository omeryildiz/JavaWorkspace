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
import com.ut.tekir.entities.ProductGroup;
import com.ut.tekir.framework.EntityHome;

/**
 * Marka tanımlarının yapıldığı home sınıfımızdır.
 * @author sinan.yumak
 */
@Stateful
@Name("productGroupHome")
@Scope(value=ScopeType.CONVERSATION)
public class ProductGroupHomeBean extends EntityHome<ProductGroup> implements ProductGroupHome<ProductGroup> {
    
    @DataModel("productGroupList")
    private List<ProductGroup> entityList;
    
    @SuppressWarnings("unchecked")
	@Factory("productGroupList")
    public void initProductGroupList() {
        log.debug("Preparing Product Group List...");
        
        setEntityList(getEntityManager().createQuery("select pg from ProductGroup pg")
        //.setMaxResults(100)
        //.setHint("org.hibernate.cacheable", true)
        .getResultList());
    }
    
    @Out(required=false)
    public ProductGroup getProductGroup() {
        return getEntity();
    }

    @In(required=false)
    public void setProductGroup(ProductGroup productGroup) {
        setEntity( productGroup );
    }

    @Override
    public void createNew() {
        log.debug("New product group...");
        entity = new ProductGroup();
    }

    @Override
    public List<ProductGroup> getEntityList() {
        return entityList;
    }

    public void setEntityList(List<ProductGroup> entityList) {
        this.entityList = entityList;
    }
    
}
