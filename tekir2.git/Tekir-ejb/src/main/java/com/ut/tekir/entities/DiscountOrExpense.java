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
import java.text.NumberFormat;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.hibernate.validator.Length;

import com.ut.tekir.framework.BaseConsts;
//FIXME: nesnenin ismi Factor olarak değiştirilse iyi olur.


/**
 * Teklif, sipariş, irsaliye ve fatura detay satırlarındaki indirim, bindirim ve 
 * harç bilgilerini tutar.
 * @author sinan.yumak
 *
 */
@Embeddable
public class DiscountOrExpense implements Serializable {

	private static final long serialVersionUID = 1L;

	@Column(name="PERCENTAGE")
	private Boolean percentage = Boolean.TRUE;
	
	@Column(name="RATE")
	private BigDecimal rate = BigDecimal.ZERO;

	@Column(name="CCY", length=3)
    @Length(max=3)
    private String currency = BaseConsts.SYSTEM_CURRENCY_CODE;
    
    @Column(name="CCYVAL", precision=10, scale=2)
    private BigDecimal value = BigDecimal.ZERO;

    @Column(name="LCYVAL", precision=10, scale=2)
    private BigDecimal localAmount = BigDecimal.ZERO;

	@Override
    public String toString() {
        NumberFormat f = NumberFormat.getInstance();
        f.setMaximumFractionDigits(2);
        f.setMinimumFractionDigits(2);

        return f.format(value) + " " + getCurrency();
    }

	public String getAsCaption() {
		StringBuilder result = new StringBuilder();
		if (percentage && !rate.equals(BigDecimal.ZERO)) {
			result.append("% ")
				  .append(getNumberFormat().format(rate))
				  .append(" ( ")
				  .append(getNumberFormat().format(value))
				  .append(" )");
		} else {
			result.append(getNumberFormat().format(value));
		}
		return result.toString();
	}
	
	private NumberFormat getNumberFormat() {
        NumberFormat f = NumberFormat.getInstance();
        f.setMaximumFractionDigits(2);
        f.setMinimumFractionDigits(2);
        return f;
	}
	
	public MoneySet getAsMoney() {
    	return new MoneySet(value, localAmount, currency);
    }
    
	public void moveFieldsOf(DiscountOrExpense anotherDOrExpense) {
		if (anotherDOrExpense != null) {
			this.currency = anotherDOrExpense.getCurrency();
			this.localAmount = anotherDOrExpense.getLocalAmount();
			this.rate = anotherDOrExpense.getRate();
			this.value = anotherDOrExpense.getValue();
			if (anotherDOrExpense.getPercentage() != null) {
				this.percentage = anotherDOrExpense.getPercentage();
			}
		}
	}
	
	public void clear() {
		this.percentage = Boolean.TRUE;
		this.rate = BigDecimal.ZERO;
		this.currency = BaseConsts.SYSTEM_CURRENCY_CODE;
		this.value = BigDecimal.ZERO;
		this.localAmount = BigDecimal.ZERO;
	}
	
	public Boolean getPercentage() {
		if (percentage == null) {
			percentage = Boolean.TRUE;
		}
		return percentage;
	}

	public void setPercentage(Boolean percentage) {
		this.percentage = percentage;
	}
	
	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public BigDecimal getValue() {
		return value;
	}

	public void setValue(BigDecimal value) {
		this.value = value;
	}

	public BigDecimal getLocalAmount() {
		return localAmount;
	}

	public void setLocalAmount(BigDecimal localAmount) {
		this.localAmount = localAmount;
	}

	public BigDecimal getRate() {
		return rate;
	}

	public void setRate(BigDecimal rate) {
		this.rate = rate;
	}
	
}
