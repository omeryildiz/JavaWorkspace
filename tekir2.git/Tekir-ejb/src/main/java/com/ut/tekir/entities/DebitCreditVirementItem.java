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

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.validator.Length;
import org.hibernate.validator.Valid;

/**
 * Entity class DebitCreditVirementItem
 * 
 * @author haky
 */
@Entity
@Table(name="DEBIT_CREDIT_VIREMENT_ITEM")
public class DebitCreditVirementItem implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE,generator="genericSeq")
    @Column(name="ID")
    private Long id;

    @ManyToOne
    @JoinColumn(name="OWNER_ID")
    private DebitCreditVirement owner;

    @Column(name="LINE_CODE", length=10)
    @Length(max=10)
    private String lineCode;
    
    @Column(name="INFO")
    private String info;
    
    @ManyToOne
    @JoinColumn(name="FROM_CONTACT_ID")
    private Contact fromContact;
    
    @ManyToOne
    @JoinColumn(name="TO_CONTACT_ID")
    private Contact toContact;
    
    @Embedded
    @Valid
    @AttributeOverrides( {
        @AttributeOverride(name="currency", column=@Column(name="CCY")),
        @AttributeOverride(name="value",    column=@Column(name="CCYVAL")),
        @AttributeOverride(name="localAmount", column=@Column(name="LCYVAL"))
    })
    private MoneySet amount = new MoneySet();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DebitCreditVirement getOwner() {
        return owner;
    }

    public void setOwner(DebitCreditVirement owner) {
        this.owner = owner;
    }

    public String getLineCode() {
        return lineCode;
    }

    public void setLineCode(String lineCode) {
        this.lineCode = lineCode;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public Contact getFromContact() {
        return fromContact;
    }

    public void setFromContact(Contact fromContact) {
        this.fromContact = fromContact;
    }

    public Contact getToContact() {
        return toContact;
    }

    public void setToContact(Contact toContact) {
        this.toContact = toContact;
    }

    public MoneySet getAmount() {
        return amount;
    }

    public void setAmount(MoneySet amount) {
        this.amount = amount;
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
        if (!(object instanceof DebitCreditVirementItem)) {
            return false;
        }
        DebitCreditVirementItem other = (DebitCreditVirementItem)object;
        if (this.getId() != other.getId() && (this.getId() == null || !this.id.equals(other.id))) return false;
        return true;
    }

    @Override
    public String toString() {
        return "com.ut.tekir.entities.DebitCreditVirementItem[id=" + getId() + "]";
    }

}
