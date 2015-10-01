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

import com.ut.tekir.entities.Product;
import com.ut.tekir.entities.ProductType;
import com.ut.tekir.framework.IEntityBase;
import javax.ejb.Local;

/**
 *
 * @author bilga
 */
@Local
public interface ExpenseAndDiscountHome<E> extends IEntityBase<E> {

    void init();
    Product getProduct();
    void setProduct(Product product);
    void manualFlush();
    void setType(ProductType type);
    ProductType getType();
    
}
