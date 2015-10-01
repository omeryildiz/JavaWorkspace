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
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.validator.Length;

import com.ut.tekir.framework.BaseConsts;

/**
 * @author sinan.yumak
 *
 */
@Embeddable
public class TaxEmbeddable implements Serializable {

	private static final long serialVersionUID = 1L;

    /*
     * Vergi bilgileri
     */
	@JoinColumn(name="TAX_ID")
	@ManyToOne
	private Tax tax;
	
	@Column(name="KIND")
    @Enumerated(EnumType.ORDINAL)
    private TaxKind kind;
    
    @Column(name="TAX_INCLUDED")
    private Boolean taxIncluded = Boolean.TRUE;

    @Column(name="RATE",precision=10,scale=2)
    private BigDecimal rate;

    @Column(name="TYPE")
    @Enumerated(EnumType.ORDINAL)
    private TaxType type;

    /* 
	 * Tutar bilgileri
	 */
	@Column(name="CCY", length=3)
    @Length(max=3)
    private String currency = BaseConsts.SYSTEM_CURRENCY_CODE;
    
    @Column(name="CCYVAL", precision=10, scale=2)
    private BigDecimal value = BigDecimal.ZERO;

    @Column(name="LCYVAL", precision=10, scale=2)
    private BigDecimal localAmount = BigDecimal.ZERO;

    /**
     * Eğer varsa tevkifat Bilgileri...
     */ 
    @Column(name="WITHHOLDING_KIND")
    @Enumerated(EnumType.ORDINAL)
    private TaxKind withholdingKind;

    //Eğer varsa tevkifat oranını tutar.
    @Column(name="WITHHOLDING_RATE", precision=10, scale=2)
    private BigDecimal withholdingRate = BigDecimal.ZERO;

    //Eğer varsa tevkifat tutarını tutar.
    @Column(name="WITHHOLDING_AMOUNT", precision=10, scale=2)
    private BigDecimal withholdingAmount = BigDecimal.ZERO;
    
    @Override
    public String toString() {
        NumberFormat f = NumberFormat.getInstance();
        f.setMaximumFractionDigits(2);
        f.setMinimumFractionDigits(2);

        return f.format(value) + " " + getCurrency();
    }

    public void moveFieldsOf(TaxEmbeddable anotherTEmbeddable) {
    	if (anotherTEmbeddable != null) {
    		this.currency = anotherTEmbeddable.getCurrency();
    		this.tax = anotherTEmbeddable.getTax();
    		this.localAmount = anotherTEmbeddable.getLocalAmount();
    		this.kind = anotherTEmbeddable.getKind();
    		this.rate = anotherTEmbeddable.getRate();
    		this.type = anotherTEmbeddable.getType();
    		this.value = anotherTEmbeddable.getValue();	
    		this.withholdingAmount = anotherTEmbeddable.getWithholdingAmount();	
    		this.withholdingRate = 	anotherTEmbeddable.getWithholdingRate();
    		this.withholdingKind = 	anotherTEmbeddable.getWithholdingKind();
    		if (anotherTEmbeddable.getTaxIncluded() != null) {
    			this.taxIncluded = anotherTEmbeddable.getTaxIncluded();
    		}
    	}
    }
    
	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}
	
	public BigDecimal getWithholdingAmount() {
		return withholdingAmount;
	}

	public void setWithholdingAmount(BigDecimal withholdingAmount) {
		this.withholdingAmount = withholdingAmount;
	}
	
	public TaxKind getWithholdingKind() {
		return withholdingKind;
	}

	public void setWithholdingKind(TaxKind withholdingKind) {
		this.withholdingKind = withholdingKind;
	}

	public BigDecimal getWithholdingRate() {
		return withholdingRate;
	}

	public void setWithholdingRate(BigDecimal withholdingRate) {
		this.withholdingRate = withholdingRate;
	}

	public Boolean getTaxIncluded() {
		return taxIncluded;
	}

	public void setTaxIncluded(Boolean taxIncluded) {
		this.taxIncluded = taxIncluded;
	}

	public BigDecimal getRate() {
		return rate;
	}

	public void setRate(BigDecimal rate) {
		this.rate = rate;
	}

	public TaxType getType() {
		return type;
	}

	public void setType(TaxType type) {
		this.type = type;
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
		this.localAmount = localAmount.setScale(2, RoundingMode.HALF_UP);
	}

	public TaxKind getKind() {
		return kind;
	}

	public void setKind(TaxKind kind) {
		this.kind = kind;
	}

	public Tax getTax() {
		return tax;
	}

	public void setTax(Tax tax) {
		this.tax = tax;
	}

}
