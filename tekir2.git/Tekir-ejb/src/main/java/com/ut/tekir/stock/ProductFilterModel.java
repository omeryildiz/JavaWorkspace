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
import com.ut.tekir.entities.ProductGroup;
import com.ut.tekir.entities.ProductType;
import java.math.BigDecimal;

/**
 *
 * @author haky
 */
public class ProductFilterModel {

    private String code;
    private String name;
    private String barcode;
    private ProductCategory category;
    private ProductType productType;
    private BigDecimal lastPurchasePriceValue;
    private BigDecimal lastSalePriceValue;
    private ProductGroup group;

    public ProductGroup getGroup() {
        return group;
    }

    public void setGroup(ProductGroup group) {
        this.group = group;
    }

    public BigDecimal getLastPurchasePriceValue() {
        return lastPurchasePriceValue;
    }

    public void setLastPurchasePriceValue(BigDecimal lastPurchasePriceValue) {
        this.lastPurchasePriceValue = lastPurchasePriceValue;
    }

    public BigDecimal getLastSalePriceValue() {
        return lastSalePriceValue;
    }

    public void setLastSalePriceValue(BigDecimal lastSalePriceValue) {
        this.lastSalePriceValue = lastSalePriceValue;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public ProductCategory getCategory() {
        return category;
    }

    public void setCategory(ProductCategory category) {
        this.category = category;
    }

	public void setProductType(ProductType productType) {
		this.productType = productType;
	}

	public ProductType getProductType() {
		return productType;
	}

    
}
