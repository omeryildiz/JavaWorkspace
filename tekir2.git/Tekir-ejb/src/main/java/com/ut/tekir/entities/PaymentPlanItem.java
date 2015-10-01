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

@Entity
@Table(name="PAYMENT_PLAN_ITEM")
public class PaymentPlanItem implements Serializable {

	private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE,generator="genericSeq")
    @Column(name="ID")
	private Long id;
	
    @ManyToOne
    @JoinColumn(name="PAYMENT_PLAN_ID")
	private PaymentPlan paymentPlan;

    @Column(name="PAYMENT_DAY")
    private Integer day;
    
    /**
     * Hesaplama tipi
     */
    @Column(name="CALC_TYPE")
    @Enumerated(EnumType.ORDINAL)
	private PaymentPlanCalcType calcType = PaymentPlanCalcType.Rate;
    
    /**
     * Hedef (Tutar üzerinden, Vergi üzerinden)
     */
    @Column(name="DEST_TYPE")
    @Enumerated(EnumType.ORDINAL)
    private PaymentPlanDestType destType = PaymentPlanDestType.Total;
    
    @Column(name="RATE", precision=17, scale=2)
    private BigDecimal rate = BigDecimal.ZERO;

    public PaymentPlanItem clone() {
    	PaymentPlanItem clonedppi = new PaymentPlanItem();
    	clonedppi.setCalcType(calcType);
    	clonedppi.setDay(day);
    	clonedppi.setDestType(destType);
    	clonedppi.setRate(rate);
    	return clonedppi;
    }
    
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public PaymentPlan getPaymentPlan() {
		return paymentPlan;
	}

	public void setPaymentPlan(PaymentPlan paymentPlan) {
		this.paymentPlan = paymentPlan;
	}

	public Integer getDay() {
		return day;
	}

	public void setDay(Integer day) {
		this.day = day;
	}

	public PaymentPlanCalcType getCalcType() {
		return calcType;
	}

	public void setCalcType(PaymentPlanCalcType calcType) {
		this.calcType = calcType;
	}

	public PaymentPlanDestType getDestType() {
		return destType;
	}

	public void setDestType(PaymentPlanDestType destType) {
		this.destType = destType;
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
		hash += (this.id != null ? this.id.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		// TODO: Warning - this method won't work in the case the id fields are
		// not set
		if (!(object instanceof PaymentPlanItem)) {
			return false;
		}
		PaymentPlanItem other = (PaymentPlanItem) object;
		if (this.id != other.id
				&& (this.id == null || !this.id.equals(other.id)))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "com.ut.tekir.entities.PaymentPlanItem[id=" + id + "]";
	}
	
}
