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

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cascade;

/**
 * 
 * @author sinan.yumak
 *
 */
@Entity
@Table(name="PROMISSORY_TO_CONTACT_PAYROLL")
public class PromissoryToContactPayroll extends DocumentBase implements Serializable {

	private static final long serialVersionUID = 1L; 

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE,generator="genericSeq")
    @Column(name="ID")
	private Long id;
    
    @ManyToOne
    @JoinColumn(name="CONTACT_ID")
    private Contact contact;
	
    @OneToMany(mappedBy="owner", cascade=CascadeType.ALL)
    @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    private List<PromissoryToContactPayrollDetail> details = new ArrayList<PromissoryToContactPayrollDetail>();
    
    @Override
	public DocumentType getDocumentType() {
		//FIXME: döküman tipini bulamadım.
    	return null;
	}

    public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Contact getContact() {
		return contact;
	}

	public void setContact(Contact contact) {
		this.contact = contact;
	}

	public List<PromissoryToContactPayrollDetail> getDetails() {
		return details;
	}

	public void setDetails(List<PromissoryToContactPayrollDetail> details) {
		this.details = details;
	}

	@Transient
    public DocumentTotal getTotals(){
        DocumentTotal tot = new DocumentTotal();

        for( PromissoryToContactPayrollDetail item : getDetails() ){
            tot.add( new MoneySet(item.getPromissory().getMoney()));
        }

        return tot;
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
        if (!(object instanceof PromissoryToContactPayroll)) {
            return false;
        }
        PromissoryToContactPayroll other = (PromissoryToContactPayroll)object;
        if (this.getId() != other.getId() && (this.getId() == null || !this.getId().equals(other.getId()))) return false;
        return true;
    }

    @Override
    public String toString() {
        return "com.ut.tekir.entities.PromissoryToContactPayroll[id=" + getId() + "]";
    }
    
}
