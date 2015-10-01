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
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;
import org.hibernate.validator.Valid;

/**
 * Entity class FundTransfer
 * 
 * @author haky
 */
@Entity
@Table(name="FUND_TRANSFER")
public class FundTransfer extends DocumentBase implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE,generator="genericSeq")
    @Column(name="ID")
    private Long id;
    
    @OneToMany(mappedBy="owner", cascade=CascadeType.ALL)
    @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    private List<FundTransferItem> items = new ArrayList<FundTransferItem>();

    @Embedded
    @Valid
    @AttributeOverrides( {
        @AttributeOverride(name="currency", column=@Column(name="TOTAL_AMOUNT_CCY")),
        @AttributeOverride(name="value",    column=@Column(name="TOTAL_AMOUNT_VALUE"))
    })
    private Money totalAmount = new Money();

    @OneToMany(mappedBy="fundTransfer", cascade=CascadeType.ALL)
    @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    private List<FundTransferCurrencyRate> currencyRates = new ArrayList<FundTransferCurrencyRate>();

    @Override
	public DocumentType getDocumentType() {
		return DocumentType.FundTransfer;
	}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<FundTransferItem> getItems() {
        return items;
    }

    public void setItems(List<FundTransferItem> items) {
        this.items = items;
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
        if (!(object instanceof FundTransfer)) {
            return false;
        }
        FundTransfer other = (FundTransfer)object;
        if (this.getId() != other.getId() && (this.getId() == null || !this.id.equals(other.id))) return false;
        return true;
    }

     @Override
    public String toString() {
        return "com.ut.tekir.entities.FundTransfer[id=" + getId() + "]";
    }

    /**
     * @return the totalAmount
     */
    public Money getTotalAmount() {
        return totalAmount;
    }

    /**
     * @param totalAmount the totalAmount to set
     */
    public void setTotalAmount(Money totalAmount) {
        this.totalAmount = totalAmount;
    }

    /**
     * @return the currencyRates
     */
    public List<FundTransferCurrencyRate> getCurrencyRates() {
        return currencyRates;
    }

    /**
     * @param currencyRates the currencyRates to set
     */
    public void setCurrencyRates(List<FundTransferCurrencyRate> currencyRates) {
        this.currencyRates = currencyRates;
    }
    
}
