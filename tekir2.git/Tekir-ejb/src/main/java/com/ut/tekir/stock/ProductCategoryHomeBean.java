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

import com.ut.tekir.entities.ProductCategory;
import com.ut.tekir.framework.EntityHome;
import java.util.List;
import javax.ejb.Stateful;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.datamodel.DataModel;

/**
 *
 * @author haky
 */
@Stateful
@Name("productCategoryHome")
@Scope(value=ScopeType.CONVERSATION)
public class ProductCategoryHomeBean extends EntityHome<ProductCategory> implements ProductCategoryHome<ProductCategory>{

    @DataModel("productCategoryList")
    private List<ProductCategory> entityList;
    
    @SuppressWarnings("unchecked")
	@Factory("productCategoryList")
    public void initProductCategoryList() {
        log.debug("Product Category Listesi hazırlanıyor...");
        
        setEntityList(getEntityManager().createQuery("select c from ProductCategory c order by weight,code")
        //.setMaxResults(100)
        //.setHint("org.hibernate.cacheable", true)
        .getResultList());
    }
    
    
    @Out(required=false)
    public ProductCategory getProductCategory() {
        return getEntity();
    }

    @In(required=false)
    public void setProductCategory(ProductCategory productCategory) {
        setEntity( productCategory );
    }
    
        
    @Override
    public void createNew() {
        log.debug("Yeni Product Kategorisi");
        entity = new ProductCategory();
        entity.setActive(true);
        entity.setWeight(10);
    }

    @Override
    public List<ProductCategory> getEntityList() {
        return entityList;
    }

    public void setEntityList(List<ProductCategory> entityList) {
        this.entityList = entityList;
    }
    
}
