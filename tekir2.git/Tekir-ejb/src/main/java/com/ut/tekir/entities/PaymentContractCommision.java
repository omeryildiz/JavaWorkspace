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

import org.hibernate.annotations.ForeignKey;

/**
 * Ödeme sözleşmesinin komisyon bilgilerini tutar.
 * @author sinan.yumak
 *
 */
@Entity
@Table(name="PAYMENT_CONTRACT_COMMISION")
public class PaymentContractCommision implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.TABLE,generator="genericSeq")
    @Column(name="ID")
	private Long id;

	@ManyToOne
	@JoinColumn(name="OWNER_ID")
	@ForeignKey(name="FK_PAYMENTCONTRACTCOMMISION_OWNERID")
	private PaymentContractPeriod owner;

	@ManyToOne
	@JoinColumn(name="COMMISSION_ID")
	@ForeignKey(name="FK_PAYMENTCONTRACTCOMMISION_COMMISSIONID")
	private PaymentCommission commission;

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
    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (this.getId() != null ? this.getId().hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof PaymentContractCommision)) {
            return false;
        }
        PaymentContractCommision other = (PaymentContractCommision)object;
        if (this.getId() != other.getId() && (this.getId() == null || !this.getId().equals(other.getId()))) return false;
        return true;
    }

    @Override
    public String toString() {
        return "com.ut.tekir.entities.PaymentContractCommision[id=" + getId() + "]";
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public PaymentContractPeriod getOwner() {
		return owner;
	}

	public void setOwner(PaymentContractPeriod owner) {
		this.owner = owner;
	}

	public PaymentCommission getCommission() {
		return commission;
	}

	public void setCommission(PaymentCommission commission) {
		this.commission = commission;
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

}
