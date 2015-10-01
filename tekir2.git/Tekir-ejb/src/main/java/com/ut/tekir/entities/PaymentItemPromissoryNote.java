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

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * PromissoryNote
 * 
 * @author dumlupinar
 */
@Entity
@DiscriminatorValue("2")
public class PaymentItemPromissoryNote extends PaymentItem {

    private static final long serialVersionUID = 1L;

    
	public PaymentItemPromissoryNote() {
		super();
		setLineType(PaymentType.PromissoryNote);
	}

	@ManyToOne(cascade = {CascadeType.PERSIST,CascadeType.MERGE})
	@JoinColumn(name="PROMISSORYNOTE_ID")
	private PromissoryNote promissoryNote;

	@Override
	public boolean isClientPromissory() {
		return promissoryNote != null ? promissoryNote.getClientPromissoryNote() : false;
	}

	@Override
	public boolean isPromissory() {
		return true;
	}

    public PromissoryNote getPromissoryNote() {
		return promissoryNote;
	}

	public void setPromissoryNote(PromissoryNote promissoryNote) {
		this.promissoryNote = promissoryNote;
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
        if (!(object instanceof PaymentItemPromissoryNote)) {
            return false;
        }
        PaymentItemPromissoryNote other = (PaymentItemPromissoryNote)object;
        if (this.getId() != other.getId() && (this.getId() == null || !this.getId().equals(other.getId()))) return false;
        return true;
    }

    @Override
    public String toString() {
        return "com.ut.tekir.entities.PaymentItemPromissoryNote[id=" + getId() + "]";
    }

}
