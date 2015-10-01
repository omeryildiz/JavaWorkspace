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

import com.ut.tekir.framework.BaseConsts;

/**
 * Money sınıfına bir de local tutarı ekler...
 * @author haky
 *
 */
@Embeddable
public class MoneySet extends Money implements Serializable {

    private static final long serialVersionUID = 1L;

    private BigDecimal localAmount = BigDecimal.ZERO;

    public MoneySet(){
        super();
        localAmount = BigDecimal.ZERO;
        localAmount.setScale(2, RoundingMode.HALF_UP);
    }

    public MoneySet(String currency){
    	super(currency);
    }

    public MoneySet( BigDecimal ccyAmt, BigDecimal localAmt, String currency ){
        super( ccyAmt, currency );
        this.localAmount = localAmt;
    }

    public MoneySet(BigDecimal ccyAmt, BigDecimal localAmt) {
        super( ccyAmt );
        this.localAmount = localAmt;
	}

    public MoneySet(BigDecimal ccyAmt, String currency) {
    	super( ccyAmt );
    	setCurrency(currency);
    }

    public MoneySet(BigDecimal ccyAmt) {
    	super( ccyAmt );
    }

    public MoneySet(MoneySet amount) {
        super( amount.getValue(), amount.getCurrency());
        localAmount = amount.getLocalAmount();
    }
    
    public MoneySet(Money amount) {
    	super( amount.getValue(), amount.getCurrency());
    	localAmount = amount.getValue();
    }


	@Column(name="LCYVAL", precision=19, scale=2)
    public BigDecimal getLocalAmount() {
        return localAmount;
    }
    
    public void setLocalAmount(BigDecimal localAmount) {
        this.localAmount = localAmount;
    }

    public String localAmountString(){
        NumberFormat f = NumberFormat.getInstance();
        f.setMaximumFractionDigits(2);
        f.setMinimumFractionDigits(2);

        return f.format(getLocalAmount()) + " " + getCurrency();
    }

    public String valueString() {
    	NumberFormat f = NumberFormat.getInstance();
    	f.setMaximumFractionDigits(2);
    	f.setMinimumFractionDigits(2);
    	
    	return f.format(getValue()) + " " + getCurrency();
    }

	private NumberFormat getNumberFormat() {
        NumberFormat f = NumberFormat.getInstance();
        f.setMaximumFractionDigits(2);
        f.setMinimumFractionDigits(2);
        return f;
	}

	public String valueWithLocalAmountString() {
		StringBuffer result = new StringBuffer();

    	result.append(getNumberFormat().format(getValue()));
    	
    	if (!getCurrency().equals(BaseConsts.SYSTEM_CURRENCY_CODE)) {
    		result.append("(")
    			  .append(getNumberFormat().format(getLocalAmount()))
    			  .append(")");
    	} 
    	return result.toString();
    }


    public void clearMoney() {
    	setCurrency(BaseConsts.SYSTEM_CURRENCY_CODE);
    	setLocalAmount(BigDecimal.ZERO);
    	setValue(BigDecimal.ZERO);
    }
    
    public void moveFieldsOf(MoneySet anotherMoneySet) {
    	setCurrency(anotherMoneySet.getCurrency());
    	setLocalAmount(anotherMoneySet.getLocalAmount());
    	setValue(anotherMoneySet.getValue());
    }

    public MoneySet add(MoneySet moneySet) {
        super.add(moneySet);
        localAmount = localAmount.add(moneySet.getLocalAmount());
        return this;
    }

    public MoneySet substract(MoneySet moneySet) {
    	super.substract(moneySet);
    	localAmount = localAmount.subtract(moneySet.getLocalAmount());
    	return this;
    }

    public void divide(MoneySet moneySet) {
    	super.divide(moneySet);
    	localAmount = localAmount.divide(moneySet.getLocalAmount(), 2, RoundingMode.HALF_UP);
    }
    
    public MoneySet divide(BigDecimal divisor, int scale) {
    	setValue( getValue().divide(divisor, scale, RoundingMode.HALF_UP) );
    	localAmount = localAmount.divide(divisor, scale, RoundingMode.HALF_UP);
    	return this;
    }

    public MoneySet multiply(BigDecimal multiplicand) {
    	setValue( getValue().multiply(multiplicand) );
    	setLocalAmount( getLocalAmount().multiply(multiplicand) );
    	return this;
    }

}
