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
 * Kasa çıkış fişi kur oranlarını tutar.
 * @author sinan.yumak
 *
 */
@Entity
@Table(name="ACCOUNT_CREDIT_NOTE_CURRENCY_RATE")
public class AccountCreditNoteCurrencyRate extends DocumentCurrencyRateBase implements Serializable {

	private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE,generator="genericSeq")
    @Column(name="ID")
    private Long id;

    @ManyToOne
    @JoinColumn(name="OWNER_ID")
    @ForeignKey(name="FK_ACCOUNTCREDITNOTECURRATE_OWNERID")
    private AccountCreditNote owner;

	public AccountCreditNoteCurrencyRate(AccountCreditNote owner) {
		super();
		this.owner = owner;
	}

	public AccountCreditNoteCurrencyRate(AccountCreditNote owner, DocumentCurrencyRateBase currencyRateBase) {
		super();
		this.owner = owner;
		this.setCurrencyPair(currencyRateBase.getCurrencyPair());
		this.setAsk(currencyRateBase.getAsk());
		this.setBid(currencyRateBase.getBid());
	}

	public AccountCreditNoteCurrencyRate() {
		super();
	}
	
	@Override
    public int hashCode() {
        int hash = 0;
        hash += (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }
	
	@Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof AccountCreditNoteCurrencyRate)) {
            return false;
        }
        AccountCreditNoteCurrencyRate other = (AccountCreditNoteCurrencyRate)object;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) return false;
        return true;
    }
	
	@Override
    public String toString() {
        return "com.ut.tekir.entities.AccountCreditNoteCurrencyRate[id=" + id + "]";
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public AccountCreditNote getOwner() {
		return owner;
	}

	public void setOwner(AccountCreditNote owner) {
		this.owner = owner;
	}

}
