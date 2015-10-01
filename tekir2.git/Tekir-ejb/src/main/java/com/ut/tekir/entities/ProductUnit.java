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

package com.ut.tekir.entities;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * @author bilga
 *
 */
@Entity
@Table(name="PRODUCT_UNIT")
public class ProductUnit implements Serializable {
	
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE,generator="genericSeq")
    @Column(name="ID")
    private Long id;
    
    @ManyToOne
    @JoinColumn(name="PRODUCT_ID")
    private Product product;
 
    @Column(name="CHANGE_UNIT")
    private String changeUnit;
 
    @Column(name="CHANGE_UNIT_VALUE")
    private BigDecimal changeUnitValue;
    
    @Column(name="MAIN_UNIT")
    private String mainUnit;
    
    @Column(name="MAIN_UNIT_VALUE")
    private BigDecimal mainUnitValue = BigDecimal.ONE;
    
    @Column(name="BARCODE1")
    private String barcode1;
    
    @Column(name="BARCODE2")
    private String barcode2;
    
    @Column(name="BARCODE3")
    private String barcode3;
    
    @Column(name="DEFAULT_UNIT")
    private Boolean defaultUnit = false;
    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (this.getId() != null ? this.getId().hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ProductUnit)) {
            return false;
        }
        ProductUnit other = (ProductUnit)object;
        if (this.getId() != other.getId() && (this.getId() == null || !this.id.equals(other.id))) return false;
        return true;
    }

    @Override
    public String toString() {
        return getChangeUnit();
    }

	public void setId(Long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public Product getProduct() {
		return product;
	}

	public void setMainUnit(String mainUnit) {
		this.mainUnit = mainUnit;
	}

	public String getMainUnit() {
		return mainUnit;
	}

	public void setBarcode1(String barcode1) {
		this.barcode1 = barcode1;
	}

	public String getBarcode1() {
		return barcode1;
	}

	public void setBarcode2(String barcode2) {
		this.barcode2 = barcode2;
	}

	public String getBarcode2() {
		return barcode2;
	}

	public void setBarcode3(String barcode3) {
		this.barcode3 = barcode3;
	}

	public String getBarcode3() {
		return barcode3;
	}

	public void setChangeUnit(String changeUnit) {
		this.changeUnit = changeUnit;
	}

	public String getChangeUnit() {
		return changeUnit;
	}

	public void setChangeUnitValue(BigDecimal changeUnitValue) {
		this.changeUnitValue = changeUnitValue;
	}

	public BigDecimal getChangeUnitValue() {
		return changeUnitValue;
	}

	public void setMainUnitValue(BigDecimal mainUnitValue) {
		this.mainUnitValue = mainUnitValue;
	}

	public BigDecimal getMainUnitValue() {
		return mainUnitValue;
	}

	public void setDefaultUnit(Boolean defaultUnit) {
		this.defaultUnit = defaultUnit;
	}

	public Boolean getDefaultUnit() {
		return defaultUnit;
	}

}
