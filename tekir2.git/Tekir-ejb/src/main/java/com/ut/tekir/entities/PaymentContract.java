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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Cascade;
import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;

/**
 * Ödeme sözleşme bilgilerini tutar.
 * @author sinan.yumak
 *
 */
@Entity
@Table(name="PAYMENT_CONTRACT")
public class PaymentContract extends AuditBase implements Serializable {
    
	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.TABLE,generator="genericSeq")
    @Column(name="ID")
	private Long id;

	@Column(name="IS_ACTIVE")
	private Boolean active = Boolean.TRUE;

	@Column(name="CODE", length=20, unique=true, nullable=false)
    @Length(max=20, min=1)
    @NotNull
	private String code;

    @Column(name="NAME", length=50)
    @Length(max=50)
    private String name;

    @Column(name="INFO")
	private String info;

    /**
     * sözleşme geçerlilik başlangıç tarihi
     */
    @Column(name="BEGIN_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date beginDate;
    
    /**
     * sözleşme geçerlilik bitiş tarihi
     */
    @Column(name="END_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date endDate;
    
    @Column(name="OWNER_ID")
    private Long ownerId;

    @Column(name="OWNER_TYPE")
    @Enumerated(EnumType.ORDINAL)
    private PaymentOwnerType ownerType;
    
    @Column(name="PAYMENT_TYPE")
    @Enumerated(EnumType.ORDINAL)
    private PaymentType paymentType;

    @Column(name="CARD_OWNER_TYPE")
    @Enumerated(EnumType.ORDINAL)
    private CardOwnerType cardOwnerType;

	@OneToMany(mappedBy="owner", cascade=CascadeType.ALL)
    @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    private List<PaymentContractPeriod> periodList = new ArrayList<PaymentContractPeriod>();

	public PaymentContractPeriod getPeriod(int aPeriod) {
		for (PaymentContractPeriod period : periodList) {
			if (period.getPeriod() == aPeriod) return period;
		}
		return null;
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
        if (!(object instanceof PaymentContract)) {
            return false;
        }
        PaymentContract other = (PaymentContract)object;
        if (this.getId() != other.getId() && (this.getId() == null || !this.getId().equals(other.getId()))) return false;
        return true;
    }

    @Override
    public String toString() {
        return "com.ut.tekir.entities.PaymentContract[id=" + getId() + "]";
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
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

	public Long getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(Long ownerId) {
		this.ownerId = ownerId;
	}

	public PaymentOwnerType getOwnerType() {
		return ownerType;
	}

	public void setOwnerType(PaymentOwnerType ownerType) {
		this.ownerType = ownerType;
	}

	public List<PaymentContractPeriod> getPeriodList() {
		return periodList;
	}

	public void setPeriodList(List<PaymentContractPeriod> periodList) {
		this.periodList = periodList;
	}

	public PaymentType getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(PaymentType paymentType) {
		this.paymentType = paymentType;
	}

	public CardOwnerType getCardOwnerType() {
		return cardOwnerType;
	}

	public void setCardOwnerType(CardOwnerType cardOwnerType) {
		this.cardOwnerType = cardOwnerType;
	}

}
