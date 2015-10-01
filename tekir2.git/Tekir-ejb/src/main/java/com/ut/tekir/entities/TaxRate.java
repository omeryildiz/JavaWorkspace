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
import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * Entity class TaxRate
 * 
 * @author haky
 */
@Entity
@Table(name="TAX_RATE")
public class TaxRate implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE,generator="genericSeq")
    @Column(name="ID")
    private Long id;
    
    @ManyToOne
    @JoinColumn(name="TAX_ID")
    private Tax tax;
    
    @Column(name="BEGIN_DATE")
    @Temporal(value=TemporalType.DATE)
    private Date beginDate;
    
    @Column(name="END_DATE")
    @Temporal(value=TemporalType.DATE)
    private Date endDate;

    /**
     * Vergi ile ilgili alanlar...
     */
    //Verginin yüzde mi değer mi kesir mi olduğu...
    @Column(name="KIND")
	@Enumerated(EnumType.ORDINAL)
    private TaxKind kind = TaxKind.Rate;

    //Eğer vergi tipi yüzde seçildi ise yüzdeyi
    //kesir seçildi ise paydayı tutar.
    @Column(name="RATE", precision=10, scale=2)
    private BigDecimal rate = BigDecimal.ZERO;

    // vergi tutar olarak tutulacaksa kullanılacak alan..
    @Embedded
    @AttributeOverrides( {
        @AttributeOverride(name="currency", column=@Column(name="CCY")),
        @AttributeOverride(name="value",    column=@Column(name="CCYVAL")),
        @AttributeOverride(name="localAmount", column=@Column(name="LCYVAL"))
    })
    private MoneySet amount = new MoneySet();

    /**
     * Tevkifat vergisi ile ilgili alanlar...
     */
    @Column(name="WITHHOLDING_KIND")
    @Enumerated(EnumType.ORDINAL)
    private TaxKind withholdingKind;
    	
    //Eğer tevkifat türü tutarsa kullanılacak alandır. 
    //Satır tutarı ile aynı döviz cinsinde olacak.
    @Column(name="WITHHOLDING_AMOUNT", precision=10, scale=2)
    private BigDecimal withholdingAmount = BigDecimal.ZERO;

    @Column(name="WITHHOLDING_RATE", precision=10, scale=2)
    private BigDecimal withholdingRate = BigDecimal.ZERO;
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    public BigDecimal getWithholdingRate() {
		return withholdingRate;
	}

	public void setWithholdingRate(BigDecimal withholdingRate) {
		this.withholdingRate = withholdingRate;
	}

	public Tax getTax() {
        return tax;
    }

    public void setTax(Tax tax) {
        this.tax = tax;
    }

    public Date getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(Date beginDate) {
        this.beginDate = beginDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }
    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (this.getId() != null ? this.getId().hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof TaxRate)) {
            return false;
        }
        TaxRate other = (TaxRate)object;
        if (this.getId() != other.getId() && (this.getId() == null || !this.id.equals(other.id))) return false;
        return true;
    }

    @Override
    public String toString() {
        return "com.ut.tekir.entities.TaxRate[id=" + getId() + "]";
    }

    public MoneySet getAmount() {
    	if (amount == null) {
    		amount = new MoneySet();
    	}
    	return amount;
    }

    public void setAmount(MoneySet amount) {
        this.amount = amount;
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

	public TaxKind getKind() {
		return kind;
	}

	public void setKind(TaxKind kind) {
		this.kind = kind;
	}
    
}
