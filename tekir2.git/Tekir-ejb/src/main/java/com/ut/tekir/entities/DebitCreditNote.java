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
import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;
import org.hibernate.validator.Valid;

/**
 * 
 * DebitCreditNote entity
 * 
 * @author haky
 *
 */
@Entity
@Table(name="DEBIT_CREDIT_NOTE")
public class DebitCreditNote extends DocumentBase implements Serializable {

	private static final long serialVersionUID = 1L;
	
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE,generator="genericSeq")
    @Column(name="ID")
    private Long id;
    
    @ManyToOne
    @JoinColumn(name="CONTACT_ID")
    private Contact contact;
    
    @Column(name="ACTION")
    @Enumerated(EnumType.ORDINAL)
    private FinanceAction action;
    
    @OneToMany(mappedBy = "owner", cascade=CascadeType.ALL )
    @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    private List<DebitCreditNoteItem> items = new ArrayList<DebitCreditNoteItem>();

    @Embedded
    @Valid
    @AttributeOverrides( {
        @AttributeOverride(name="currency", column=@Column(name="TOTAL_AMOUNT_CCY")),
        @AttributeOverride(name="value",    column=@Column(name="TOTAL_AMOUNT_VALUE"))
    })
    private Money totalAmount = new Money();
    
	@Override
	public DocumentType getDocumentType() {
		return action.equals(FinanceAction.Credit) ? DocumentType.DebitCreditNotePayment : DocumentType.DebitCreditNoteCollection;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public List<DebitCreditNoteItem> getItems() {
		return items;
	}

	public void setItems(List<DebitCreditNoteItem> items) {
		this.items = items;
	}

	public FinanceAction getAction() {
		return action;
	}

	public void setAction(FinanceAction action) {
		this.action = action;
	}

	public Contact getContact() {
		return contact;
	}

	public void setContact(Contact contact) {
		this.contact = contact;
	}

	public Money getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(Money totalAmount) {
		this.totalAmount = totalAmount;
	}

	public DocumentTotal getTotals() {
        DocumentTotal tot = new DocumentTotal();

        for( DebitCreditNoteItem item : items ){
            tot.add( item.getAmount());
        }

        return tot;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof DebitCreditNote))
			return false;
		final DebitCreditNote other = (DebitCreditNote) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

    @Override
    public String toString() {
        return "com.ut.tekir.entities.DebitCreditNote[id=" + getId() + "]";
    }

}
