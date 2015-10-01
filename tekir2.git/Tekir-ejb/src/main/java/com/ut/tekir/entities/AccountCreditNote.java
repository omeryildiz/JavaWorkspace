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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.validator.Valid;

import com.ut.tekir.util.Utils;


/**
 * Kasa çıkış föyü modelimizdir.
 * @author sinan.yumak
 *
 */
@Entity
@Table(name="ACCOUNT_CREDIT_NOTE")
public class AccountCreditNote extends ForeignDocumentBase implements Serializable {

	private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE,generator="genericSeq")
    @Column(name="ID")
	private Long id;

    @ManyToOne
    @JoinColumn(name="ACCOUNT_ID")
    @ForeignKey(name="FK_ACCOUNTCREDITNOTE_ACCOUNTID")
    private Account account;

    @OneToMany(mappedBy="owner", cascade=CascadeType.ALL)
    @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    private List<AccountCreditNoteDetail> items = new ArrayList<AccountCreditNoteDetail>();

    @OneToMany(mappedBy="owner", cascade=CascadeType.ALL)
    @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    private List<AccountCreditNoteCurrencyRate> currencyRateList = new ArrayList<AccountCreditNoteCurrencyRate>();

    @Embedded
    @Valid
    @AttributeOverrides( {
        @AttributeOverride(name="currency", column=@Column(name="TOTAL_AMOUNT_CCY")),
        @AttributeOverride(name="value",    column=@Column(name="TOTAL_AMOUNT_VALUE"))
    })
    private Money totalAmount = new Money();

    @Override
    public List<DocumentCurrencyRateBase> getDocumentCurrencyRateList() {
    	return Utils.changeListType(currencyRateList, DocumentCurrencyRateBase.class);
    }

    @Override
    public List<DocumentItemBase> getDocumentItemList() {
    	return Utils.changeListType(items, DocumentItemBase.class);
    }
    
    @Override
    public void addToCurrencyRateList(DocumentCurrencyRateBase currencyRateBase) {
    	AccountCreditNoteCurrencyRate newItem = new AccountCreditNoteCurrencyRate(this, currencyRateBase);
    	currencyRateList.add(newItem);
    }
    
    @Override
    public void clearCurrencyRateList() {
    	if (currencyRateList != null) currencyRateList.clear();
    }
    
    @Override
    public void removeFromCurrencyRateList(int index) {
    	currencyRateList.remove(index);
    }

	@Override
	public DocumentType getDocumentType() {
		return DocumentType.AccountCreditNote;
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
        if (!(object instanceof AccountCreditNote)) {
            return false;
        }
        AccountCreditNote other = (AccountCreditNote)object;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) return false;
        return true;
    }
	
	@Override
    public String toString() {
        return "com.ut.tekir.entities.AccountCreditNote[id=" + id + "]";
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public List<AccountCreditNoteDetail> getItems() {
		return items;
	}

	public void setItems(List<AccountCreditNoteDetail> items) {
		this.items = items;
	}

	public Money getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(Money totalAmount) {
		this.totalAmount = totalAmount;
	}

	public List<AccountCreditNoteCurrencyRate> getCurrencyRateList() {
		return currencyRateList;
	}

	public void setCurrencyRateList(List<AccountCreditNoteCurrencyRate> currencyRateList) {
		this.currencyRateList = currencyRateList;
	}
	
}
