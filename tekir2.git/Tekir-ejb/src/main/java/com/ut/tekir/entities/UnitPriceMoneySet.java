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
import java.math.RoundingMode;
import java.text.NumberFormat;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * Uygulama içerisinde temel para tipi
 * @author haky
 */
@Embeddable
public class UnitPriceMoneySet extends MoneySet {

    private static final long serialVersionUID = 1L;
	    
    public UnitPriceMoneySet() {
    	setValue(BigDecimal.ZERO);
    	getValue().setScale(6, RoundingMode.HALF_UP);
    }
    
    public UnitPriceMoneySet(UnitPriceMoneySet money) {
    	super(money);
    }

    @Override
    @Column(name="CCYVAL", precision=10, scale=6)
    public BigDecimal getValue() {
        return super.getValue();
    }

    @Override
    @Column(name="LCYVAL", precision=10, scale=6)
    public BigDecimal getLocalAmount() {
        return super.getLocalAmount();
    }

    @Override
    public String toString() {

        NumberFormat f = NumberFormat.getInstance();
        f.setMaximumFractionDigits(6);
        f.setMinimumFractionDigits(2);

        return f.format( getValue() ) + " " + getCurrency();
    }

}
