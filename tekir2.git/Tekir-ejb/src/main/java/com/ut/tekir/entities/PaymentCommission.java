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
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;

/**
 * Ödeme Komisyon Tanımları
 * 
 * @author volkan
 *
 */
@Entity
@Table(name="PAYMENT_COMMISSION")
public class PaymentCommission extends AuditBase implements Serializable {

	private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE,generator="genericSeq")
    @Column(name="ID")
	private Long id;
	
    @Column(name="CODE", length=20, unique=true, nullable=false)
    @Length(max=20, min=1)
    @NotNull
	private String code;

    @Column(name="NAME", length=50)
    @Length(max=50)
    private String name;

    @Column(name="INFO")
	private String info;

    @Column(name="ISACTIVE")
	private Boolean active = Boolean.TRUE;

    @Column(name="WEIGHT")
    private Integer weight ;

    /**
     * Oran , Tutar, Kesir
     */
    @Column(name="KIND")
	@Enumerated(EnumType.ORDINAL)
    private TaxKind kind = TaxKind.Rate;

    @Column(name="RATE", precision=10, scale=4)
    private BigDecimal rate = BigDecimal.ZERO;

    @Column(name="AMOUNT", precision=10, scale=4)
    private BigDecimal amount = BigDecimal.ZERO;

    /**
     * false : matrahı - olarak etkiler
     * true : matrahı + olarak etkiler
     */
    @Column(name="EFFECT")
	private Boolean effect = Boolean.FALSE;

    /**
     * false : sadece ilk taksitte alınacak.
     * true : tüm vadelerde alınacak. 
     */
	@Column(name="COLLECTION_TIME")
    private Boolean collectionTime = false;

    @ManyToOne
    @JoinColumn(name="PAYMENT_ACTION_ID")
    private PaymentActionType paymentActionType;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

  public TaxKind getKind() {
        return kind;
    }

    public void setKind(TaxKind kind) {
        this.kind = kind;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Boolean getEffect() {
        return effect;
    }

    public void setEffect(Boolean effect) {
        this.effect = effect;
    }

    public PaymentActionType getPaymentActionType() {
        return paymentActionType;
    }

    public void setPaymentActionType(PaymentActionType paymentActionType) {
        this.paymentActionType = paymentActionType;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }
	
	@Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof PaymentCommission)) {
            return false;
        }
        PaymentCommission other = (PaymentCommission)object;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) return false;
        return true;
    }
	
	@Override
    public String toString() {
        return "com.ut.tekir.entities.PaymentCommission[id=" + id + "]";
    }

    public String getCaption(){
        return "[" + getCode() + "] " + getName();
    }

	public Boolean getCollectionTime() {
		return collectionTime;
	}

	public void setCollectionTime(Boolean collectionTime) {
		this.collectionTime = collectionTime;
	}

   	
}
