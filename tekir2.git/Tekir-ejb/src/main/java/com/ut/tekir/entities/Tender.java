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

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;

import com.ut.tekir.entities.inv.TekirInvoiceDetail;

/**
 * @author sinan.yumak
 *
 */
@Entity
@Table(name="TENDER")
public class Tender extends TenderBase implements ITender {

	private static final long serialVersionUID = 1L;

    @Column(name="TENDER_STATUS")
	@Enumerated(EnumType.ORDINAL)
    private TenderStatus status;
    
    @Column(name="REFERENCE_SUFFIX")
	private Integer referenceSuffix;
    
    @OneToMany(mappedBy="owner", cascade=CascadeType.ALL)
    @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    private List<TenderCurrencyRate> currencyRateList = new ArrayList<TenderCurrencyRate>();

    @OneToMany(mappedBy="owner", cascade=CascadeType.ALL)
    @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    private List<TenderTaxSummary> taxSummaryList = new ArrayList<TenderTaxSummary>();

    @OneToMany(mappedBy="owner", cascade=CascadeType.ALL)
    @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    private List<TenderCurrencySummary> currencySummaryList = new ArrayList<TenderCurrencySummary>();

    @OneToMany(mappedBy="owner", cascade=CascadeType.ALL)
    @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    @OrderBy("parent,id")
    private List<TenderDetail> items = new ArrayList<TenderDetail>();

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (this.getId() != null ? this.getId().hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Tender)) {
            return false;
        }
        Tender other = (Tender)object;
        if (this.getId() != other.getId() && (this.getId() == null || !this.getId().equals(other.getId()))) return false;
        return true;
    }

    @Override
    public String toString() {
        return "com.ut.tekir.entities.Tender[id=" + getId() + "]";
    }

    public Integer getReferenceSuffix() {
		return referenceSuffix;
	}

	public void setReferenceSuffix(Integer referenceSuffix) {
		this.referenceSuffix = referenceSuffix;
	}

	public List<TenderDetail> getItems() {
		return items;
	}

	public void setItems(List<TenderDetail> items) {
		this.items = items;
	}

	public List<TenderDetail> getProductItems() {
		List<TenderDetail> result = new ArrayList<TenderDetail>();
		for (TenderDetailBase item : getItems()) {
			if (item.getProductType().equals(ProductType.Product) || 
					item.getProductType().equals(ProductType.Service)) {
				result.add((TenderDetail) item);
			}
		}
		return result;
	}
	
	public List<TenderDetail> getDocumentDiscountItems() {
		List<TenderDetail> result = new ArrayList<TenderDetail>();
		for (TenderDetailBase item : getItems()) {
			if (item.getProductType().equals(ProductType.DocumentDiscount) || 
					item.getProductType().equals(ProductType.ContactDiscount)) {
				result.add((TenderDetail) item);
			}
		}
		return result;
	}

	public List<TenderDetail> getDocumentExpenseItems() {
		List<TenderDetail> result = new ArrayList<TenderDetail>();
		for (TenderDetailBase item : getItems()) {
			if (item.getProductType().equals(ProductType.DocumentExpense)) {
				result.add((TenderDetail) item);
			}
		}
		return result;
	}

	public List<? extends TenderDetailBase> getDiscountAdditionItems() {
		List<TekirInvoiceDetail> result = new ArrayList<TekirInvoiceDetail>();
		for (TenderDetailBase item : getItems()) {
			if (item.getProductType().equals(ProductType.DiscountAddition)) {
				result.add((TekirInvoiceDetail) item);
			}
		}
		return result;
	}

	@Override
	public List<? extends TenderDetailBase> getExpenseAdditionItems() {
		List<TekirInvoiceDetail> result = new ArrayList<TekirInvoiceDetail>();
		for (TenderDetailBase item : getItems()) {
			if (item.getProductType().equals(ProductType.ExpenseAddition)) {
				result.add((TekirInvoiceDetail) item);
			}
		}
		return result;
	}

	public List<TenderDetail> getDocumentFeeItems() {
		List<TenderDetail> result = new ArrayList<TenderDetail>();
		for (TenderDetailBase item : getItems()) {
			if (item.getProductType().equals(ProductType.DocumentFee)) {
				result.add((TenderDetail) item);
			}
		}
		return result;
	}

	public List<TenderTaxSummary> getTaxSummaryList() {
		return taxSummaryList;
	}
	
	public void setTaxSummaryList(List<TenderTaxSummary> taxSummaryList) {
		this.taxSummaryList = taxSummaryList;
	}

	public List<TenderCurrencySummary> getCurrencySummaryList() {
		return currencySummaryList;
	}

	public void setCurrencySummaryList(
			List<TenderCurrencySummary> currencySummaryList) {
		this.currencySummaryList = currencySummaryList;
	}

	public List<TenderCurrencyRate> getCurrencyRateList() {
		return currencyRateList;
	}

	public void setCurrencyRateList(List<TenderCurrencyRate> currencyRateList) {
		this.currencyRateList = currencyRateList;
	}

	public TenderStatus getStatus() {
		return status;
	}

	public void setStatus(TenderStatus status) {
		this.status = status;
	}

}
