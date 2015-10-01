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

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.ForeignKey;

/**
 * Tahsilat ve ödeme satırlarında tipi kredi kartı olan
 * satırlar için kullanacağımız modeldir.
 * 
 * @author sinan.yumak
 */
@Entity
@DiscriminatorValue("3")
public class PaymentItemCreditCard extends PaymentItem {

    private static final long serialVersionUID = 1L;

    
	public PaymentItemCreditCard() {
		super();
		setLineType(PaymentType.CreditCard);
	}

	@ManyToOne
	@JoinColumn(name="POS_ID")
	private Pos pos;

	@ManyToOne
	@JoinColumn(name="BANK_ID")
	@ForeignKey(name="FK_PAYMENTITEMCREDITCARD_BANKID")
	private Bank bank;
	
	@Column(name="PERIOD")
	private Integer period = 1;
	
	@Column(name="CREDIT_CARD_NUMBER")
	private String creditCardNumber;
	
	@Override
    public int hashCode() {
        int hash = 0;
        hash += (this.getId() != null ? this.getId().hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof PaymentItemCreditCard)) {
            return false;
        }
        PaymentItemCreditCard other = (PaymentItemCreditCard)object;
        if (this.getId() != other.getId() && (this.getId() == null || !this.getId().equals(other.getId()))) return false;
        return true;
    }

    @Override
    public String toString() {
        return "com.ut.tekir.entities.PaymentItemCreditCard[id=" + getId() + "]";
    }

	public Pos getPos() {
		return pos;
	}

	public void setPos(Pos pos) {
		this.pos = pos;
	}

	public String getCreditCardNumber() {
		return creditCardNumber;
	}

	public void setCreditCardNumber(String creditCardNumber) {
		this.creditCardNumber = creditCardNumber;
	}

	public Bank getBank() {
		return bank;
	}

	public void setBank(Bank bank) {
		this.bank = bank;
	}

	public int getPeriod() {
		return period;
	}

	public void setPeriod(int period) {
		this.period = period;
	}
	
}
