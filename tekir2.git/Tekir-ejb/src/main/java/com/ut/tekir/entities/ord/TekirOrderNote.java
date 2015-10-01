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

package com.ut.tekir.entities.ord;

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

import com.ut.tekir.entities.Account;
import com.ut.tekir.entities.ITender;
import com.ut.tekir.entities.InvoiceOrderLink;
import com.ut.tekir.entities.OrderShipmentLink;
import com.ut.tekir.entities.OrderStatus;
import com.ut.tekir.entities.PaymentTable;
import com.ut.tekir.entities.ProductType;
import com.ut.tekir.entities.TenderBase;
import com.ut.tekir.entities.TenderDetailBase;
import com.ut.tekir.entities.TradeAction;
import com.ut.tekir.entities.Warehouse;

/**
 * Tekir 2.0 yapısına uygun sipariş modelidir.
 * @author sinan.yumak
 *
 */
@Entity
@Table(name="TEKIR_ORDER_NOTE")
public class TekirOrderNote extends TenderBase implements ITender {

	private static final long serialVersionUID = 1L;

    @OneToMany(mappedBy="owner", cascade=CascadeType.ALL)
    @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    private List<TekirOrderNoteCurrencyRate> currencyRateList = new ArrayList<TekirOrderNoteCurrencyRate>();

    @OneToMany(mappedBy="owner", cascade=CascadeType.ALL)
    @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    private List<TekirOrderNoteTaxSummary> taxSummaryList = new ArrayList<TekirOrderNoteTaxSummary>();

    @OneToMany(mappedBy="owner", cascade=CascadeType.ALL)
    @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    private List<TekirOrderNoteCurrencySummary> currencySummaryList = new ArrayList<TekirOrderNoteCurrencySummary>();

    @OneToMany(mappedBy="owner", cascade=CascadeType.ALL)
    @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    @OrderBy("id")
    private List<TekirOrderNoteDetail> items = new ArrayList<TekirOrderNoteDetail>();

    /**
     * siparişin bağlı olduğu faturalar listesidir.
     */
    @OneToMany(mappedBy="orderNote", cascade=CascadeType.ALL)
    @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    private List<InvoiceOrderLink> invoiceLinks = new ArrayList<InvoiceOrderLink>();

    /**
     * siparişin bağlı olduğu irsaliyeler listesidir.
     */
    @OneToMany(mappedBy="orderNote", cascade=CascadeType.ALL)
    @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    private List<OrderShipmentLink> shipmentLinks = new ArrayList<OrderShipmentLink>();

    @Column(name="TRADE_ACTION")
    @Enumerated(EnumType.ORDINAL)
    private TradeAction tradeAction;

    @Column(name="STATUS")
    @Enumerated(EnumType.ORDINAL)
    private OrderStatus status;

    @ManyToOne
    @JoinColumn(name="WAREHOUSE_ID")
    private Warehouse warehouse;

    /**
     * siparişin ödeme bilgilerini tutar.
     */
    @ManyToOne(cascade = {CascadeType.ALL} )
    @JoinColumn(name="PAYMENT_TABLE_ID")
    private PaymentTable paymentTable;
    
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
    
    /**
     * siparişin ödemesinin gireceği kasa bilgisidir.
     */
    @ManyToOne
    @JoinColumn(name="ACCOUNT_ID")
    private Account account;

    @Column(name="CONTAINS_SERVICE")
    private boolean containsService = false;
    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (this.getId() != null ? this.getId().hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof TekirOrderNote)) {
            return false;
        }
        TekirOrderNote other = (TekirOrderNote)object;
        if (this.getId() != other.getId() && (this.getId() == null || !this.getId().equals(other.getId()))) return false;
        return true;
    }

    @Override
    public String toString() {
        return "com.ut.tekir.entities.TekirOrderNote[id=" + getId() + "]";
    }

	@Override
	public List<TekirOrderNoteCurrencyRate> getCurrencyRateList() {
		return currencyRateList;
	}

	@Override
	public List<TekirOrderNoteCurrencySummary> getCurrencySummaryList() {
		return currencySummaryList;
	}

	@Override
	public List<? extends TenderDetailBase> getDocumentDiscountItems() {
		List<TekirOrderNoteDetail> result = new ArrayList<TekirOrderNoteDetail>();
		for (TenderDetailBase item : getItems()) {
			if (item.getProductType().equals(ProductType.DocumentDiscount) ||
					item.getProductType().equals(ProductType.ContactDiscount)) {
				result.add((TekirOrderNoteDetail) item);
			}
		}
		return result;
	}

	@Override
	public List<? extends TenderDetailBase> getDocumentExpenseItems() {
		List<TekirOrderNoteDetail> result = new ArrayList<TekirOrderNoteDetail>();
		for (TenderDetailBase item : getItems()) {
			if (item.getProductType().equals(ProductType.DocumentExpense)) {
				result.add((TekirOrderNoteDetail) item);
			}
		}
		return result;
	}

	@Override
	public List<? extends TenderDetailBase> getDiscountAdditionItems() {
		List<TekirOrderNoteDetail> result = new ArrayList<TekirOrderNoteDetail>();
		for (TenderDetailBase item : getItems()) {
			if (item.getProductType().equals(ProductType.DiscountAddition)) {
				result.add((TekirOrderNoteDetail) item);
			}
		}
		return result;
	}

	@Override
	public List<? extends TenderDetailBase> getExpenseAdditionItems() {
		List<TekirOrderNoteDetail> result = new ArrayList<TekirOrderNoteDetail>();
		for (TenderDetailBase item : getItems()) {
			if (item.getProductType().equals(ProductType.ExpenseAddition)) {
				result.add((TekirOrderNoteDetail) item);
			}
		}
		return result;
	}

	@Override
	public List<? extends TenderDetailBase> getDocumentFeeItems() {
		List<TekirOrderNoteDetail> result = new ArrayList<TekirOrderNoteDetail>();
		for (TenderDetailBase item : getItems()) {
			if (item.getProductType().equals(ProductType.DocumentFee)) {
				result.add((TekirOrderNoteDetail) item);
			}
		}
		return result;
	}

	@Override
	public List<TekirOrderNoteDetail> getItems() {
		return items;
	}

	@Override
	public List<TekirOrderNoteDetail> getProductItems() {
		List<TekirOrderNoteDetail> result = new ArrayList<TekirOrderNoteDetail>();
		for (TenderDetailBase item : getItems()) {
			if (item.getProductType().equals(ProductType.Product) || 
					item.getProductType().equals(ProductType.Service)) {
				result.add((TekirOrderNoteDetail) item);
			}
		}
		return result;
	}

	@Override
	public List<TekirOrderNoteTaxSummary> getTaxSummaryList() {
		return taxSummaryList;
	}

	public void setCurrencyRateList(
			List<TekirOrderNoteCurrencyRate> currencyRateList) {
		this.currencyRateList = currencyRateList;
	}

	public void setTaxSummaryList(List<TekirOrderNoteTaxSummary> taxSummaryList) {
		this.taxSummaryList = taxSummaryList;
	}

	public void setCurrencySummaryList(
			List<TekirOrderNoteCurrencySummary> currencySummaryList) {
		this.currencySummaryList = currencySummaryList;
	}

	public void setItems(List<TekirOrderNoteDetail> items) {
		this.items = items;
	}

	public TradeAction getTradeAction() {
		return tradeAction;
	}

	public void setTradeAction(TradeAction tradeAction) {
		this.tradeAction = tradeAction;
	}

	public OrderStatus getStatus() {
		return status;
	}

	public void setStatus(OrderStatus status) {
		this.status = status;
	}

	public Warehouse getWarehouse() {
		return warehouse;
	}

	public void setWarehouse(Warehouse warehouse) {
		this.warehouse = warehouse;
	}

	public PaymentTable getPaymentTable() {
		return paymentTable;
	}

	public void setPaymentTable(PaymentTable paymentTable) {
		this.paymentTable = paymentTable;
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

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public List<InvoiceOrderLink> getInvoiceLinks() {
		return invoiceLinks;
	}

	public void setInvoiceLinks(List<InvoiceOrderLink> invoiceLinks) {
		this.invoiceLinks = invoiceLinks;
	}

	public List<OrderShipmentLink> getShipmentLinks() {
		return shipmentLinks;
	}

	public void setShipmentLinks(List<OrderShipmentLink> shipmentLinks) {
		this.shipmentLinks = shipmentLinks;
	}

	public boolean isContainsService() {
		return containsService;
	}

	public void setContainsService(boolean containsService) {
		this.containsService = containsService;
	}
	
}
