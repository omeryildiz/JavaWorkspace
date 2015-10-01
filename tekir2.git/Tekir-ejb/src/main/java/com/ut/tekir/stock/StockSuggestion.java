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
import com.ut.tekir.entities.ProductCategory;
import com.ut.tekir.entities.ProductGroup;
import com.ut.tekir.entities.ProductType;

import java.util.List;
import javax.ejb.Remove;
import org.jboss.seam.annotations.Destroy;

/**
 *
 * @author haky
 */
@SuppressWarnings("unchecked")
public interface StockSuggestion {

    @Remove
    @Destroy
    void destroy();

    List<Product> suggestProduct(Object event);
    
    List<Product> suggestIsProduct(Object event);
    
    List<Product> suggestIsProductWithBarcode(Object event);
    
    List<Product> suggestExpenseAndDiscount(Object event);

    List<ProductCategory> suggestProductCategory(Object event);
    
    void selectProductList();
    void selectExpenseAndDiscountList();
    List<ProductCategory> getProductCategoryList();
    

	List getProductList();
	void setProductList(List productList);
	
	List getExpenseAndDiscountList();
	void setExpenseAndDiscountList(List expenseAndDiscountList);

	String getCode();
    void setCode(String code);
    
    String getName();
    void setName(String name);
    
    String getBarcode();
    void setBarcode(String barcode);
    
    ProductType getProductType();
    void setProductType(ProductType productType);
    
    Boolean getDisableProductCombo();
    void setDisableProductCombo(Boolean disableProductCombo);
    
    List<Product> suggestIsService(Object event);
    
    List<Product> suggestWithBarcode(Object event);

    ProductCategory getCategory() ;

    void setCategory(ProductCategory category);

    ProductGroup getGroup() ;

    void setGroup(ProductGroup group);

}
