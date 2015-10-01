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

package com.ut.tekir.entities.inv;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.ut.tekir.entities.TenderTaxSummaryBase;


/**
 * @author sinan.yumak
 *
 */
@Entity
@Table(name="TEKIR_INVOICE_TAX_SUMMARY")
public class TekirInvoiceTaxSummary extends TenderTaxSummaryBase implements Serializable { 

    private static final long serialVersionUID = 1L;

    @ManyToOne
    @JoinColumn(name="OWNER_ID")
    private TekirInvoice owner;

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (this.getId() != null ? this.getId().hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof TekirInvoiceTaxSummary)) {
            return false;
        }
        TekirInvoiceTaxSummary other = (TekirInvoiceTaxSummary)object;
        if (this.getId() != other.getId() && (this.getId() == null || !this.getId().equals(other.getId()))) return false;
        return true;
    }

    @Override
    public String toString() {
        return "com.ut.tekir.entities.InvoiceTaxSummary[id=" + getId() + "]";
    }

    public TekirInvoiceTaxSummary clone() {
    	TekirInvoiceTaxSummary clonedts = new TekirInvoiceTaxSummary();
    	clonedts.setAmount(getAmount());
    	clonedts.setBase(getBase());
    	clonedts.setKind(getKind());
    	clonedts.setSourceAmount(getSourceAmount());
    	clonedts.setSourceRate(getSourceRate());
    	clonedts.setType(getType());
    	return clonedts;
    }
    
	public TekirInvoice getOwner() {
		return owner;
	}

	public void setOwner(TekirInvoice owner) {
		this.owner = owner;
	}

}
