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

package com.ut.tekir.entities.shp;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;
import org.hibernate.validator.Length;

import com.ut.tekir.entities.ITender;
import com.ut.tekir.entities.OrderShipmentLink;
import com.ut.tekir.entities.ProductType;
import com.ut.tekir.entities.TenderBase;
import com.ut.tekir.entities.TenderDetailBase;
import com.ut.tekir.entities.TradeAction;
import com.ut.tekir.entities.Warehouse;
import com.ut.tekir.entities.inv.TekirInvoice;
import com.ut.tekir.entities.inv.TekirInvoiceDetail;

/**
 * Tekir 2.0 yapısına uygun irsaliye modelimiz.
 * @author sinan.yumak
 *
 */
@Entity
@Table(name="TEKIR_SHIPMENT_NOTE")
public class TekirShipmentNote extends TenderBase implements ITender {

	private static final long serialVersionUID = 1L;

	/**
	 * İrsaliyenin bağlı olduğu faturayı tutar.
	 */
	@ManyToOne
	@JoinColumn(name="INVOICE_ID")
	private TekirInvoice invoice;

    @ManyToOne
    @JoinColumn(name="WAREHOUSE_ID")
    private Warehouse warehouse;

	/**
	 * İrsaliyenin tipini tutar.
	 */
	@Column(name="TRADE_ACTION")
    @Enumerated(EnumType.ORDINAL)
    private TradeAction tradeAction;
	
    @OneToMany(mappedBy="owner", cascade=CascadeType.ALL)
    @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    private List<TekirShipmentNoteCurrencyRate> currencyRateList = new ArrayList<TekirShipmentNoteCurrencyRate>();

    @OneToMany(mappedBy="owner", cascade=CascadeType.ALL)
    @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    private List<TekirShipmentNoteTaxSummary> taxSummaryList = new ArrayList<TekirShipmentNoteTaxSummary>();

    @OneToMany(mappedBy="owner", cascade=CascadeType.ALL)
    @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    private List<TekirShipmentNoteCurrencySummary> currencySummaryList = new ArrayList<TekirShipmentNoteCurrencySummary>();

    @OneToMany(mappedBy="owner", cascade=CascadeType.ALL)
    @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    @OrderBy("id")
    private List<TekirShipmentNoteDetail> items = new ArrayList<TekirShipmentNoteDetail>();
	
    /**
     * irsaliyenin bağlı olduğu siparişler listesidir.
     */
    @OneToMany(mappedBy="shipmentNote", cascade=CascadeType.ALL)
    @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    private List<OrderShipmentLink> orderLinks = new ArrayList<OrderShipmentLink>();

    /**
     * Belgeyi teslim alan kisiyi tutar
     */
    @Column(name="RECEPIENT", length=92)
    @Length(max=92)
    private String recepient;
    
    /**
     * Belgeyi teslim eden kisiyi tutar
     */
    @Column(name="DELIVERER", length=92)
    @Length(max=92)
    private String deliverer;

    public TekirShipmentNote() {
    }
    
    public TekirShipmentNote(Long anId, String aSerial) {
    	setId(anId);
    	setSerial(aSerial);
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
        if (!(object instanceof TekirShipmentNote)) {
            return false;
        }
        TekirShipmentNote other = (TekirShipmentNote)object;
        if (this.getId() != other.getId() && (this.getId() == null || !this.getId().equals(other.getId()))) return false;
        return true;
    }

    @Override
    public String toString() {
        return "com.ut.tekir.entities.TekirShipmentNote[id=" + getId() + "]";
    }


	@Override
	public List<TekirShipmentNoteCurrencyRate> getCurrencyRateList() {
		return currencyRateList;
	}

	@Override
	public List<TekirShipmentNoteCurrencySummary> getCurrencySummaryList() {
		return currencySummaryList;
	}

	@Override
	public List<? extends TenderDetailBase> getDocumentDiscountItems() {
		List<TekirShipmentNoteDetail> result = new ArrayList<TekirShipmentNoteDetail>();
		for (TenderDetailBase item : getItems()) {
			if (item.getProductType().equals(ProductType.DocumentDiscount) || 
					item.getProductType().equals(ProductType.ContactDiscount)) {
				result.add((TekirShipmentNoteDetail) item);
			}
		}
		return result;
	}

	@Override
	public List<? extends TenderDetailBase> getDocumentExpenseItems() {
		List<TekirShipmentNoteDetail> result = new ArrayList<TekirShipmentNoteDetail>();
		for (TenderDetailBase item : getItems()) {
			if (item.getProductType().equals(ProductType.DocumentExpense)) {
				result.add((TekirShipmentNoteDetail) item);
			}
		}
		return result;
	}

	@Override
	public List<? extends TenderDetailBase> getDocumentFeeItems() {
		List<TekirShipmentNoteDetail> result = new ArrayList<TekirShipmentNoteDetail>();
		for (TenderDetailBase item : getItems()) {
			if (item.getProductType().equals(ProductType.DocumentFee)) {
				result.add((TekirShipmentNoteDetail) item);
			}
		}
		return result;
	}

	@Override
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

	@Override
	public List<TekirShipmentNoteDetail> getItems() {
		return items;
	}

	@Override
	public List<TekirShipmentNoteDetail> getProductItems() {
		List<TekirShipmentNoteDetail> result = new ArrayList<TekirShipmentNoteDetail>();
		for (TenderDetailBase item : getItems()) {
			if (item.getProductType().equals(ProductType.Product) || 
					item.getProductType().equals(ProductType.Service)) {
				result.add((TekirShipmentNoteDetail) item);
			}
		}
		return result;
	}

	@Override
	public List<TekirShipmentNoteTaxSummary> getTaxSummaryList() {
		return taxSummaryList;
	}

	public void setCurrencyRateList(
			List<TekirShipmentNoteCurrencyRate> currencyRateList) {
		this.currencyRateList = currencyRateList;
	}

	public void setTaxSummaryList(List<TekirShipmentNoteTaxSummary> taxSummaryList) {
		this.taxSummaryList = taxSummaryList;
	}

	public void setCurrencySummaryList(
			List<TekirShipmentNoteCurrencySummary> currencySummaryList) {
		this.currencySummaryList = currencySummaryList;
	}

	public void setItems(List<TekirShipmentNoteDetail> items) {
		this.items = items;
	}

	public TekirInvoice getInvoice() {
		return invoice;
	}

	public void setInvoice(TekirInvoice invoice) {
		this.invoice = invoice;
	}

	public TradeAction getTradeAction() {
		return tradeAction;
	}

	public void setTradeAction(TradeAction tradeAction) {
		this.tradeAction = tradeAction;
	}

	public Warehouse getWarehouse() {
		return warehouse;
	}

	public void setWarehouse(Warehouse warehouse) {
		this.warehouse = warehouse;
	}

	public String getRecepient() {
		return recepient;
	}

	public void setRecepient(String recepient) {
		this.recepient = recepient;
	}

	public String getDeliverer() {
		return deliverer;
	}

	public void setDeliverer(String deliverer) {
		this.deliverer = deliverer;
	}

	public List<OrderShipmentLink> getOrderLinks() {
		return orderLinks;
	}

	public void setOrderLinks(List<OrderShipmentLink> orderLinks) {
		this.orderLinks = orderLinks;
	}
	
}
