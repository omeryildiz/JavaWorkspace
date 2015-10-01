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

import java.math.BigDecimal;
import java.text.NumberFormat;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.hibernate.validator.Length;

/**
 *
 * @author haky
 */
@Embeddable
public class Quantity implements java.io.Serializable {
    
    private static final long serialVersionUID = 1L;

    @Column(name="UNIT", length=10)
    @Length(max=10)
    private String unit;
    
    @Column(name="QUANTITY", precision=5, scale=2)
    private Double value = 0d;
    
    public Quantity() {
        this.unit = "";
        this.value = 0d;
    }

    public Quantity(Quantity quantity) {
        this.unit = quantity.getUnit();
        this.value = new Double( quantity.getValue());
    }

    public Quantity(double value, String unit) {
        this.value = value;
        this.unit = unit;
    }

    public void moveFieldsOf(Quantity anotherQuantity) {
    	if (anotherQuantity != null) {
    		this.unit = anotherQuantity.getUnit();
    		this.value = anotherQuantity.getValue();
    	}
    }
        
    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Double getValue() {
        return value;
    }

    public BigDecimal asBigDecimal() {
    	return BigDecimal.valueOf(value);
    }

    public void setValue(Double value) {
        this.value = value;
    }
    
    @Override
    public String toString() {

        NumberFormat f = NumberFormat.getInstance();
        f.setMaximumFractionDigits(2);
        f.setMinimumFractionDigits(2);

        return f.format(getValue()) + " " + getUnit();
    }

    public String toStringInNarrowFormat() {
        NumberFormat f = NumberFormat.getInstance();
        f.setMaximumFractionDigits(2);
        f.setMinimumFractionDigits(2);

        String result = f.format(getValue()) + " " + getUnit();
        if (result.length() > 7) {
        	result = result.substring(0, 7);
        }
        return result;
    }
    
}
