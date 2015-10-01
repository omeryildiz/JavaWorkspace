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
 * Carinin banka bilgilerini tutar.
 * @author sinan.yumak
 *
 */
@Entity
@Table(name="CONTACT_BANK_ACCOUNT")
public class ContactBankAccount extends AuditBase implements Serializable {
	private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE,generator="genericSeq")
    @Column(name="ID")
    private Long id;

    @ManyToOne
    @JoinColumn(name="OWNER_ID")
    @ForeignKey(name="FK_CONTACTBANKACCOUNT_OWNERID")
    private Contact owner;

    @ManyToOne(cascade={javax.persistence.CascadeType.ALL})
    @JoinColumn(name="BANK_ACCOUNT_ID")
    @ForeignKey(name="FK_CONTACTBANKACCOUNT_BANKACCOUNTID")
    private BankAccount BankAccount = new BankAccount();
    
    @Column(name="ISACTIVE")
	private Boolean active = Boolean.TRUE;

	@Override
    public int hashCode() {
        int hash = 0;
        hash += (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ContactBankAccount)) {
            return false;
        }
        ContactBankAccount other = (ContactBankAccount)object;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) return false;
        return true;
    }

    @Override
    public String toString() {
        return "com.ut.tekir.entities.ContactBankAccount[id=" + id + "]";
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public BankAccount getBankAccount() {
		return BankAccount;
	}

	public void setBankAccount(BankAccount bankAccount) {
		BankAccount = bankAccount;
	}

	public Contact getOwner() {
		return owner;
	}

	public void setOwner(Contact owner) {
		this.owner = owner;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}
	
}