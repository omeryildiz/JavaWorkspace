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
import com.ut.tekir.framework.IEntityHome;
import java.util.List;

/**
 *
 * @author haky
 */
public interface ProductCategoryHome<E> extends IEntityHome<E> {

    
    List<ProductCategory> getEntityList();
    
    ProductCategory getProductCategory();
    
    void initProductCategoryList();

    void setEntityList(List<ProductCategory> entityList);
    
    void setProductCategory(ProductCategory productCategory);

}
