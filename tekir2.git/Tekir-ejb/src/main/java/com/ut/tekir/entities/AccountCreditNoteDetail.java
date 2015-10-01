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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;


/**
 * Kasa çıkış föyü detay satırları...
 * @author sinan.yumak
 *
 */
@Entity
@Table(name="ACCOUNT_CREDIT_NOTE_DETAIL")
public class AccountCreditNoteDetail extends DocumentItemBase implements Serializable {

	private static final long serialVersionUID = 1L;

    @ManyToOne
    @JoinColumn(name="OWNER_ID")
    @ForeignKey(name="FK_ACCOUNTCREDITNOTEDETAIL_OWNERID")
    private AccountCreditNote owner;

    @ManyToOne
    @JoinColumn(name="PRODUCT_ID")
    @ForeignKey(name="FK_ACCOUNTCREDITNOTEDETAIL_PRODUCTID")
    private Product product;
    
    @Column(name="INFO")
    private String info;
    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (getId() != null ? getId().hashCode() : 0);
        return hash;
    }
	
	@Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof AccountCreditNoteDetail)) {
            return false;
        }
        AccountCreditNoteDetail other = (AccountCreditNoteDetail)object;
        if (getId() != other.getId() && (getId() == null || !getId().equals(other.getId()))) return false;
        return true;
    }
	
	@Override
    public String toString() {
        return "com.ut.tekir.entities.AccountCreditNoteDetail[id=" + getId() + "]";
    }

	public AccountCreditNote getOwner() {
		return owner;
	}

	public void setOwner(AccountCreditNote owner) {
		this.owner = owner;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

}
