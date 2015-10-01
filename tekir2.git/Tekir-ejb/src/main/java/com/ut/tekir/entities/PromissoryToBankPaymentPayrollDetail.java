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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;

/**
 *
 * Senet Bankadan Ödeme Bordrosu detayları
 *
 * @author sinan.yumak
 */
@Entity
@Table(name="PROMISSORY_TO_BANK_PAY_PAYROLL_DET")
public class PromissoryToBankPaymentPayrollDetail implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE,generator="genericSeq")
    @Column(name="ID")
	private Long id;

	@ManyToOne
	@JoinColumn(name="OWNER_ID")
	@ForeignKey(name="FK_PROMISSORYTOBANKPAYPAYROLLDET_OWNERID")
	private PromissoryToBankPaymentPayroll owner;

	@ManyToOne
	@JoinColumn(name="PROMISSORY_ID")
	@ForeignKey(name="FK_PROMISSORYTOBANKPAYPAYROLLDET_CHEQUEID")
	private PromissoryNote promissory;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Override
    public int hashCode() {
        int hash = 0;
        hash += (this.getId() != null ? this.getId().hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof PromissoryToBankPaymentPayrollDetail)) {
            return false;
        }
        PromissoryToBankPaymentPayrollDetail other = (PromissoryToBankPaymentPayrollDetail)object;
        if (this.getId() != other.getId() && (this.getId() == null || !this.getId().equals(other.getId()))) return false;

        if((this.getPromissory() != null && other.getPromissory() != null) &&
            	this.getPromissory().getReferenceNo() != other.getPromissory().getReferenceNo() ) return false;
        return true;
    }

    @Override
    public String toString() {
        return "com.ut.tekir.entities.PromissoryToBankPaymentPayrollDetail[id=" + getId() + "]";
    }

	public PromissoryToBankPaymentPayroll getOwner() {
		return owner;
	}

	public void setOwner(PromissoryToBankPaymentPayroll owner) {
		this.owner = owner;
	}

	public PromissoryNote getPromissory() {
		return promissory;
	}

	public void setPromissory(PromissoryNote promissory) {
		this.promissory = promissory;
	}

}
