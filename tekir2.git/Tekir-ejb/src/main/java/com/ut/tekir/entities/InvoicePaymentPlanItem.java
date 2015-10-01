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

import com.ut.tekir.entities.inv.TekirInvoice;

/**
 * Faturanın ödeme planı bilgilerini tutar.
 * 
 * @author sinan.yumak
 *
 */
@Entity
@Table(name="INVOICE_PAYMENTPLAN_ITEM")
public class InvoicePaymentPlanItem implements Serializable {

	private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE,generator="genericSeq")
    @Column(name="ID")
	private Long id;
	
    @ManyToOne
    @JoinColumn(name="OWNER_ID")
    @ForeignKey(name="FK_INVOICEPAYMENTPLAN_INVOICEID")
    private TekirInvoice owner;
    
    @Column(name="PAYMENT_DATE")
    private Date date;
    
    /**
     * Hesaplama tipi
     */
    @Column(name="CALC_TYPE")
    @Enumerated(EnumType.ORDINAL)
	private PaymentPlanCalcType calcType;
    
    /**
     * Hedef (Tutar üzerinden, Vergi üzerinden)
     */
    @Column(name="DEST_TYPE")
    @Enumerated(EnumType.ORDINAL)
    private PaymentPlanDestType destType;
    
    @Column(name="RATE", precision=10, scale=2)
    private BigDecimal rate = BigDecimal.ZERO;
    
    /**
     * Satır için hesaplanan tutar bilgisidir.
     */
    @Column(name="TOTAL", precision=10, scale=2)
    private BigDecimal total = BigDecimal.ZERO;

    @Column(name="PAID_TOTAL", precision=10, scale=2)
    private BigDecimal paidTotal = BigDecimal.ZERO;

    /**
     * Vadenin tamamının ödenip ödenmediği bilgisini tutar.
     */
    @Column(name="CLOSED")
    private boolean closed = false;
    
    @Column(name="LINE_NUMBER")
    private int lineNumber = 0;
    
    public InvoicePaymentPlanItem clone() {
    	InvoicePaymentPlanItem clonedppi = new InvoicePaymentPlanItem();
    	clonedppi.setCalcType(calcType);
    	clonedppi.setClosed(closed);
    	clonedppi.setDate(date);
    	clonedppi.setDestType(destType);
    	clonedppi.setLineNumber(lineNumber);
    	clonedppi.setPaidTotal(paidTotal);
    	clonedppi.setRate(rate);
    	clonedppi.setTotal(paidTotal);
    	return clonedppi;
    }
    
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public BigDecimal getTotal() {
		return total;
	}

	public void setTotal(BigDecimal total) {
		this.total = total;
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
		if (!(object instanceof InvoicePaymentPlanItem)) {
			return false;
		}
		InvoicePaymentPlanItem other = (InvoicePaymentPlanItem) object;
		if (this.id != other.id
				&& (this.id == null || !this.id.equals(other.id)))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "com.ut.tekir.entities.InvoicePaymentPlanItem[id=" + id + "]";
	}

	public int getLineNumber() {
		return lineNumber;
	}

	public void setLineNumber(int lineNumber) {
		this.lineNumber = lineNumber;
	}

	public BigDecimal getPaidTotal() {
		return paidTotal;
	}

	public void setPaidTotal(BigDecimal paidTotal) {
		this.paidTotal = paidTotal;
	}

	public boolean isClosed() {
		return closed;
	}

	public void setClosed(boolean closed) {
		this.closed = closed;
	}

	public TekirInvoice getOwner() {
		return owner;
	}

	public void setOwner(TekirInvoice owner) {
		this.owner = owner;
	}
	
}
