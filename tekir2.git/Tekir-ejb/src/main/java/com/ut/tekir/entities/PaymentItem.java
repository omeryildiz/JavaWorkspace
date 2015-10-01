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

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.validator.Length;

/**
 * Entity PaymentItem base class for payment item types
 * Cash
 * 
 * @author haky
 */
@Entity
@Table(name="PAYMENT_ITEM")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(
    name="lineType",
    discriminatorType=DiscriminatorType.INTEGER
)
@DiscriminatorValue("0")
public class PaymentItem implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE,generator="genericSeq")
    @Column(name="ID")
    private Long id;

    @ManyToOne
    @JoinColumn(name="PAYMENT_ID")
    private Payment owner;
    
    @Column(name="LINE_TYPE")
    @Enumerated(EnumType.ORDINAL)
    private PaymentType lineType = PaymentType.Cash;
    
    @Column(name="LINE_CODE", length=10)
    @Length(max=10)
    private String lineCode;
    
    @Column(name="INFO")
    private String info;
    
    @Embedded
    @AttributeOverrides( {
        @AttributeOverride(name="currency", column=@Column(name="CCY")),
        @AttributeOverride(name="value",    column=@Column(name="CCYVAL")),
        @AttributeOverride(name="localAmount", column=@Column(name="LCYVAL"))
    })
    private MoneySet amount = new MoneySet();

	@Column(name="PAYMENT_TABLE_REFERENCE_ID")
	private Long paymentTableReferenceId;

	@Column(name="MATCHING_FINISHED")
	private Boolean matchingFinished = false;

	@ManyToOne
	@JoinColumn(name="WORKBUNCH_ID")
	@ForeignKey(name="FK_PAYMENTITEM_WORKBUNCHID")
	private WorkBunch workBunch;
	
	public boolean isPromissory() { 
		return false;
	}

	public boolean isCheque() { 
		return false;
	}

	public boolean isClientPromissory() {
		return false;
	}
	
	public boolean isClientCheque() {
		return false;
	}
	
	public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Payment getOwner() {
        return owner;
    }

    public void setOwner(Payment owner) {
        this.owner = owner;
    }

    public PaymentType getLineType() {
        return lineType;
    }

    public void setLineType(PaymentType lineType) {
        this.lineType = lineType;
    }

    public String getLineCode() {
        return lineCode;
    }

    public void setLineCode(String lineCode) {
        this.lineCode = lineCode;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

	public MoneySet getAmount() {
        return amount;
    }

    public void setAmount(MoneySet amount) {
        this.amount = amount;
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
        if (!(object instanceof PaymentItem)) {
            return false;
        }
        PaymentItem other = (PaymentItem)object;
        if (this.getId() != other.getId() && (this.getId() == null || !this.id.equals(other.id))) return false;
        return true;
    }

    @Override
    public String toString() {
        return "com.ut.tekir.entities.PaymentItem[id=" + getId() + "]";
    }

	public Long getPaymentTableReferenceId() {
		return paymentTableReferenceId;
	}

	public void setPaymentTableReferenceId(Long paymentTableReferenceId) {
		this.paymentTableReferenceId = paymentTableReferenceId;
	}

	public Boolean getMatchingFinished() {
		return matchingFinished;
	}

	public void setMatchingFinished(Boolean matchingFinished) {
		this.matchingFinished = matchingFinished;
	}

	public WorkBunch getWorkBunch() {
		return workBunch;
	}

	public void setWorkBunch(WorkBunch workBunch) {
		this.workBunch = workBunch;
	}

}
