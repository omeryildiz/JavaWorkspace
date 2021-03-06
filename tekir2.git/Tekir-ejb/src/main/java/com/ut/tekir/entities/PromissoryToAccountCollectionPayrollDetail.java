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

/**
 * Çek kasa ödeme Bordrosu detayları
 * 
 * @author burak Nebioglu
 */
@Entity
@Table(name="PROM_TO_ACC_COL_DET")
public class PromissoryToAccountCollectionPayrollDetail implements Serializable {
	    
	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.TABLE,generator="genericSeq")
    @Column(name="ID")
	private Long id;

	@ManyToOne
	@JoinColumn(name="OWNER_ID")
	private PromissoryToAccountCollectionPayroll owner;
	
	@ManyToOne
	@JoinColumn(name="PROMISSORY_ID")
	private PromissoryNote promissory;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public PromissoryToAccountCollectionPayroll getOwner() {
		return owner;
	}

	public void setOwner(PromissoryToAccountCollectionPayroll owner) {
		this.owner = owner;
	}

	public PromissoryNote getPromissory() {
		return promissory;
	}

	public void setPromissory(PromissoryNote promissory) {
		this.promissory = promissory;
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
        if (!(object instanceof PromissoryToAccountCollectionPayrollDetail)) {
            return false;
        }
        PromissoryToAccountCollectionPayrollDetail other = (PromissoryToAccountCollectionPayrollDetail)object;
        if (this.getId() != other.getId() && (this.getId() == null || !this.getId().equals(other.getId()))) return false;
        return true;
    }

    @Override
    public String toString() {
        return "com.ut.tekir.entities.PromissoryToAccountCollectionPayrollDetail[id=" + getId() + "]";
    }

}
