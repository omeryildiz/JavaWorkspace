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

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * @author uygun
 *
 */
@Entity
@Table(name="CHEQUE_TO_BANK_ASS_PAY_DET")
public class ChequeToBankAssurancePayrollDetail implements Serializable {
	    
	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.TABLE,generator="genericSeq")
    @Column(name="ID")
	private Long id;

	@ManyToOne
	@JoinColumn(name="OWNER_ID")
	private ChequeToBankAssurancePayroll owner;
	
	@ManyToOne( cascade = {CascadeType.PERSIST,CascadeType.MERGE} )
	@JoinColumn(name="CHEQUE_ID")
	private Cheque cheque;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public ChequeToBankAssurancePayroll getOwner() {
		return owner;
	}

	public void setOwner(ChequeToBankAssurancePayroll owner) {
		this.owner = owner;
	}

	public Cheque getCheque() {
		return cheque;
	}

	public void setCheque(Cheque cheque) {
		this.cheque = cheque;
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
        if (!(object instanceof ChequeToBankAssurancePayrollDetail)) {
            return false;
        }
        ChequeToBankAssurancePayrollDetail other = (ChequeToBankAssurancePayrollDetail)object;
        if (this.getId() != other.getId() && (this.getId() == null || !this.getId().equals(other.getId()))) return false;
        if((this.getCheque() != null && other.getCheque() != null) &&  
            	this.getCheque().getReferenceNo() != other.getCheque().getReferenceNo() ) return false;
        return true;
    }

    @Override
    public String toString() {
        return "com.ut.tekir.entities.ChequeToBankAssurancePayrollDetail[id=" + getId() + "]";
    }
    
}
