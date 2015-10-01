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
import java.util.Date;
import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Cascade;
import org.hibernate.validator.Valid;

/**
 * Entity class Invoice
 * 
 *
 * Eğer Fatura irsaliyeli ise otomatik bir irsaliye oluşturacağız. 
 * Aynı şekilde fatura kapalı ise otomatik bir tahsilat fişi mi oluşturmalı?!
 *
 * @author haky
 */
@Entity
@Table(name="INVOICE")
public class Invoice extends DocumentBase implements Serializable {

	// fatura ekranlarına teslim alan ve teslim eden alanları eklencek..
	
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE,generator="genericSeq")
    @Column(name="ID")
    private Long id;
    
    @ManyToOne
    @JoinColumn(name="CONTACT_ID")
    private Contact contact;
    
    @Column(name="INFO_1")
    private String info1;
    
    @Column(name="INFO_2")
    private String info2;
    
    @Column(name="TRADE_ACTION")
    @Enumerated(EnumType.ORDINAL)
    private TradeAction action;

    @OneToMany(mappedBy="invoice", cascade=CascadeType.MERGE)
    @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    private List<ShipmentNote> shipments = new ArrayList<ShipmentNote>();
    
    @OneToMany(mappedBy="invoice", cascade=CascadeType.ALL)
    @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    private List<InvoiceServiceItem> serviceItems = new ArrayList<InvoiceServiceItem>();

//    @OneToMany(mappedBy="invoice", cascade=CascadeType.ALL)
//    @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
//    private List<InvoicePaymentPlanItem> paymentPlanItems = new ArrayList<InvoicePaymentPlanItem>();
    
    @OneToMany(mappedBy="invoice", cascade=CascadeType.ALL)
    @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    private List<InvoiceCurrencyRate> currencyRates = new ArrayList<InvoiceCurrencyRate>();
    
    @OneToMany(mappedBy="invoice", cascade=CascadeType.ALL)
    @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    private List<InvoiceTaxSummary> taxSummaries = new ArrayList<InvoiceTaxSummary>();
    
    @OneToMany(mappedBy="invoice", cascade=CascadeType.ALL)
    @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    private List<InvoiceCurrencySummary> currencySummaries = new ArrayList<InvoiceCurrencySummary>();
    
    @Embedded
    @Valid
    @AttributeOverrides( {
        @AttributeOverride(name="currency", column=@Column(name="BEFORETAXCCY")),
        @AttributeOverride(name="value",    column=@Column(name="BEFORETAXVALUE"))
    })
    private Money beforeTax = new Money();
    
    @Embedded
    @Valid
    @AttributeOverrides( {
        @AttributeOverride(name="currency", column=@Column(name="TAXCCY")),
        @AttributeOverride(name="value",    column=@Column(name="TAXVALUE"))
    })
    private Money totalTax = new Money();
    
    @Embedded
    @Valid
    @AttributeOverrides( {
        @AttributeOverride(name="currency", column=@Column(name="TOTALCCY")),
        @AttributeOverride(name="value",    column=@Column(name="TOTALVALUE"))
    })
    private Money invoiceTotal;
    
    @ManyToOne
    @JoinColumn(name="WAREHOUSE_ID")
    private Warehouse warehouse;
    
    @ManyToOne
    @JoinColumn(name="ACCOUNT_ID")
    private Account account;
    
    @Column(name="CLOSED")
    private Boolean closed;
    
    @Column(name="SHIPMENT_INVOICE")
    private Boolean shipmentInvoice;
    
    //Sevk Tarihi
    @Column(name="SHIPPING_DATE")
    @Temporal(value = TemporalType.DATE)
    private Date shippingDate;

    // fatura eslemesi tamamlandıktan sonra, esleme icin fatura listesinde gorunmemesi icin tutulan alan
    @Column(name="MATCHING_FINISHED")
    private Boolean matchingFinished = Boolean.FALSE;

    @Override
	public DocumentType getDocumentType() {
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

    public TradeAction getAction() {
        return action;
    }

    public void setAction(TradeAction action) {
        this.action = action;
    }

    public List<ShipmentNote> getShipments() {
        return shipments;
    }

    public void setShipments(List<ShipmentNote> shipments) {
        this.shipments = shipments;
    }

    public List<InvoiceServiceItem> getServiceItems() {
        return serviceItems;
    }

    public void setServiceItems(List<InvoiceServiceItem> serviceItems) {
        this.serviceItems = serviceItems;
    }

//    public List<InvoicePaymentPlanItem> getPaymentPlanItems() {
//		return paymentPlanItems;
//	}
//
//	public void setPaymentPlanItems(List<InvoicePaymentPlanItem> paymentPlanItems) {
//		this.paymentPlanItems = paymentPlanItems;
//	}

	public List<InvoiceCurrencyRate> getCurrencyRates() {
        return currencyRates;
    }

    public void setCurrencyRates(List<InvoiceCurrencyRate> currencyRates) {
        this.currencyRates = currencyRates;
    }

    public List<InvoiceTaxSummary> getTaxSummaries() {
        return taxSummaries;
    }

    public void setTaxSummaries(List<InvoiceTaxSummary> taxSummaries) {
        this.taxSummaries = taxSummaries;
    }

    public List<InvoiceCurrencySummary> getCurrencySummaries() {
        return currencySummaries;
    }

    public void setCurrencySummaries(List<InvoiceCurrencySummary> currencySummaries) {
        this.currencySummaries = currencySummaries;
    }

    public Money getBeforeTax() {
        return beforeTax;
    }

    public void setBeforeTax(Money beforeTax) {
        this.beforeTax = beforeTax;
    }

    public Money getTotalTax() {
        return totalTax;
    }

    public void setTotalTax(Money totalTax) {
        this.totalTax = totalTax;
    }

    public Money getInvoiceTotal() {
        return invoiceTotal;
    }

    public void setInvoiceTotal(Money invoiceTotal) {
        this.invoiceTotal = invoiceTotal;
    }

    public Warehouse getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(Warehouse warehouse) {
        this.warehouse = warehouse;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Boolean getClosed() {
        return closed;
    }

    public void setClosed(Boolean closed) {
        this.closed = closed;
    }

    public Boolean getShipmentInvoice() {
        return shipmentInvoice;
    }

    public void setShipmentInvoice(Boolean shipmentInvoice) {
        this.shipmentInvoice = shipmentInvoice;
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
        if (!(object instanceof Invoice)) {
            return false;
        }
        Invoice other = (Invoice)object;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) return false;
        return true;
    }

    @Override
    public String toString() {
        return "com.ut.tekir.entities.Invoice[id=" + id + "]";
    }

	public Date getShippingDate() {
		return shippingDate;
	}

	public void setShippingDate(Date shippingDate) {
		this.shippingDate = shippingDate;
	}

	public String getInfo1() {
		return info1;
	}

	public void setInfo1(String info1) {
		this.info1 = info1;
	}

	public String getInfo2() {
		return info2;
	}

	public void setInfo2(String info2) {
		this.info2 = info2;
	}

    /**
     * @return the matchingFinished
     */
    public Boolean getMatchingFinished() {
        return matchingFinished;
    }

    /**
     * @param matchingFinished the matchingFinished to set
     */
    public void setMatchingFinished(Boolean matchingFinished) {
        this.matchingFinished = matchingFinished;
    }
    
}
