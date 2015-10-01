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
import java.math.RoundingMode;
import java.text.NumberFormat;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.MappedSuperclass;

import org.hibernate.validator.Length;

import com.ut.tekir.framework.BaseConsts;

/**
 * Uygulama içerisinde temel para tipi
 * @author haky
 */
@Embeddable
@MappedSuperclass
public class Money implements Serializable {

   private static final long serialVersionUID = 1L;
	
   /**
     * 3Char ISO code
     */
    @Column(name="CCY", length=3)
    @Length(max=3)
    private String currency = BaseConsts.SYSTEM_CURRENCY_CODE;
    
    private BigDecimal value;

    /** Creates a new instance of Money */
    public Money() {
    	value = BigDecimal.ZERO;
    	value.setScale(2, RoundingMode.HALF_UP);
    }
    
    public Money(Money money) {
        this.currency = money.getCurrency();
        this.value = money.getValue();
    }

    public Money( String currency) {
        this.value = BigDecimal.ZERO;
        this.currency = currency;
    }
    
    public Money(BigDecimal value, String currency) {
        this.value = value;
        this.currency = currency;
    }

    public Money(BigDecimal value) {
    	this.value = value;
    }

    public void clearMoney() {
    	this.currency = BaseConsts.SYSTEM_CURRENCY_CODE;
    	this.value = BigDecimal.ZERO;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    @Column(name="CCYVAL", precision=19, scale=2)
    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    @Override
    public String toString() {

        NumberFormat f = NumberFormat.getInstance();
        f.setMaximumFractionDigits(2);
        f.setMinimumFractionDigits(2);

        return f.format(value) + " " + getCurrency();
    }

    public void add(Money money) {
    	currencyValidation(money);
        value = value.add( money.getValue());
    }
    
    public void substract(Money money) {
    	currencyValidation(money);
        value = value.subtract( money.getValue());
    }

    public void multiply(Money money) {
    	currencyValidation(money);
    	value = value.multiply(money.getValue());
    }

    public void divide(Money money) {
    	currencyValidation(money);
    	value = value.divide(money.getValue(), 2, RoundingMode.HALF_UP);
    }

    public void currencyValidation(Money money) {
    	if (!currency.equals(money.getCurrency())) {
    		throw new RuntimeException("Döviz tipleri tutmuyor...");
    	}
    }
}
