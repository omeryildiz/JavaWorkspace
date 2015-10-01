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

package com.ut.tekir.entities.inv;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.validator.Length;

import com.ut.tekir.entities.Account;
import com.ut.tekir.entities.ITender;
import com.ut.tekir.entities.InvoiceOrderLink;
import com.ut.tekir.entities.InvoicePaymentPlanItem;
import com.ut.tekir.entities.MoneySet;
import com.ut.tekir.entities.PaymentPlan;
import com.ut.tekir.entities.PaymentTable;
import com.ut.tekir.entities.ProductType;
import com.ut.tekir.entities.ReturnInvoiceStatus;
import com.ut.tekir.entities.TenderBase;
import com.ut.tekir.entities.TenderDetailBase;
import com.ut.tekir.entities.TradeAction;
import com.ut.tekir.entities.Warehouse;
import com.ut.tekir.entities.shp.TekirShipmentNote;
import com.ut.tekir.invoice.yeni.PaymentPlanHolder;

/**
 * Tekir 2.0 yapısına uygun fatura modelimiz.
 * @author sinan.yumak
 *
 */
@Entity
@Table(name="TEKIR_INVOICE")
public class TekirInvoice extends TenderBase implements ITender, PaymentPlanHolder {

	private static final long serialVersionUID = 1L;

    @OneToMany(mappedBy="owner", cascade=CascadeType.ALL)
    @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    private List<TekirInvoiceCurrencyRate> currencyRateList = new ArrayList<TekirInvoiceCurrencyRate>();

    @OneToMany(mappedBy="owner", cascade=CascadeType.ALL)
    @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    private List<TekirInvoiceTaxSummary> taxSummaryList = new ArrayList<TekirInvoiceTaxSummary>();

    @OneToMany(mappedBy="owner", cascade=CascadeType.ALL)
    @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    private List<TekirInvoiceCurrencySummary> currencySummaryList = new ArrayList<TekirInvoiceCurrencySummary>();

    @OneToMany(mappedBy="owner", cascade=CascadeType.ALL)
    @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    @OrderBy("id")
    private List<TekirInvoiceDetail> items = new ArrayList<TekirInvoiceDetail>();
    
    @OneToMany(mappedBy="owner", cascade=CascadeType.ALL)
    @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    private List<InvoicePaymentPlanItem> paymentPlanItems = new ArrayList<InvoicePaymentPlanItem>();

    /**
     * Faturada kullanılan ödeme planı bilgisini tutar.
     */
    @ManyToOne
    @JoinColumn(name="PAYMENT_PLAN_ID")
    @ForeignKey(name="FK_INVOICE_PAYMENTPLANID")
    private PaymentPlan paymentPlan;
    
    /**
     * faturanın bağlı olduğu irsaliyeler listesi
     */
    @OneToMany(mappedBy="invoice")
//    @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    private List<TekirShipmentNote> shipmentList = new ArrayList<TekirShipmentNote>();

    /**
     * faturanın bağlı olduğu siparişler listesi
     */
    @OneToMany(mappedBy="invoice", cascade=CascadeType.ALL)
    @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    private List<InvoiceOrderLink> orderLinks = new ArrayList<InvoiceOrderLink>();

    //FIXME:iade fatura listesinin modelinin düzenlenmesi gerekiyor.
    /**
     * faturanın iade faturaları listesidir.
     */
    @OneToMany(mappedBy="returnInvoice")
    private List<TekirInvoice> returnInvoiceList = new ArrayList<TekirInvoice>();

    @Column(name="TRADE_ACTION")
    @Enumerated(EnumType.ORDINAL)
    private TradeAction tradeAction;
    
    @ManyToOne
    @JoinColumn(name="WAREHOUSE_ID")
    private Warehouse warehouse;

    /**
     * faturanın ödemesinin gireceği kasa bilgisidir.
     */
    @ManyToOne
    @JoinColumn(name="ACCOUNT_ID")
    private Account account;
        
    /**
     * Sevk Tarihi
     */
    @Column(name="SHIPPING_DATE")
    @Temporal(value = TemporalType.DATE)
    private Date shippingDate;

    /**
     * faturanın ödeme bilgilerini tutar.
     */
    @ManyToOne(cascade = {CascadeType.ALL} )
    @JoinColumn(name="PAYMENT_TABLE_ID")
    private PaymentTable paymentTable;


    //Belgeyi teslim alan kisiyi tutar
    @Column(name="RECEPIENT", length=92)
    @Length(max=92)
    private String recepient;

    //Belgeyi teslim eden kisiyi tutar
    @Column(name="DELIVERER", length=92)
    @Length(max=92)
    private String deliverer;

    /**
     * Bu faturanın hangi faturanın iadesi olduğu bilgisini tutar.
     */
    @ManyToOne
    @JoinColumn(name="RETURN_INVOICE_ID")
    private TekirInvoice returnInvoice;
    
    /**
     * Eğer fatura iade edilmiş ise, iade statüsünü tutar.
     */
    @Column(name="RETURN_INVOICE_STATUS_ID")
    @Enumerated(EnumType.ORDINAL)
    private ReturnInvoiceStatus returnInvoiceStatus = ReturnInvoiceStatus.Open;

    /**
     * Eğer fatura proforma ise, faturanın statüsünü tutar.
     */
    @Column(name="PROFORMA_STATUS_ID")
    @Enumerated(EnumType.ORDINAL)
    private ProformaStatus proformaStatus = ProformaStatus.Open;
    
	@Column(name="MATCHING_FINISHED")
	private Boolean matchingFinished = Boolean.FALSE;

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (this.getId() != null ? this.getId().hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof TekirInvoice)) {
            return false;
        }
        TekirInvoice other = (TekirInvoice)object;
        if (this.getId() != other.getId() && (this.getId() == null || !this.getId().equals(other.getId()))) return false;
        return true;
    }

    @Override
    public String toString() {
        return "com.ut.tekir.entities.TekirInvoice[id=" + getId() + "]";
    }

	public TekirInvoice clone() {
		TekirInvoice clonedInv = new TekirInvoice();
		clonedInv.setAccount(account);
		clonedInv.setActive(getActive());
		clonedInv.setCancelDate(getCancelDate());
		clonedInv.setCancelInfo(getCancelInfo());
		clonedInv.setCancelled(isCancelled());
		clonedInv.setClerk(getClerk());
		clonedInv.setCode(getCode());
		clonedInv.setContact(getContact());
		clonedInv.setCreateDate(getCreateDate());
		clonedInv.setCreateUser(getCreateUser());
		clonedInv.setDate(getDate());
		clonedInv.setDeliverer(getDeliverer());
		clonedInv.setDeliveryAddress(getDeliveryAddress());
		clonedInv.setDeliveryCompany(getDeliveryCompany());
		clonedInv.setDeliveryDate(getDeliveryDate());
		clonedInv.setDeliveryFullname(getDeliveryFullname());
		clonedInv.setDeliveryPerson(getDeliveryPerson());
		clonedInv.setDeliverySsn(getDeliverySsn());
		clonedInv.setDeliveryTaxNumber(getDeliveryTaxNumber());
		clonedInv.setDeliveryTaxOffice(getDeliveryTaxOffice());
		clonedInv.setDocCurrency(getDocCurrency());
		clonedInv.setDocumentType(getDocumentType());
		clonedInv.setGrandTotal(getGrandTotal());
//		clonedInv.setInfo(getInfo());
		clonedInv.setInfo1(getInfo1());
		clonedInv.setInfo2(getInfo2());
		clonedInv.setPaymentPlan(getPaymentPlan());

		if (getPaymentTable() != null) clonedInv.setPaymentTable(getPaymentTable().clone());

		clonedInv.setRateType(getRateType());
		clonedInv.setRecepient(getRecepient());
		clonedInv.setReference(getReference());
		clonedInv.setReturnInvoice(getReturnInvoice());
		clonedInv.setReturnInvoiceStatus(getReturnInvoiceStatus());
		clonedInv.setSerial(getSerial());
		clonedInv.setShippingDate(getShippingDate());
		clonedInv.setTaxExcludedTotal(new MoneySet(getTaxExcludedTotal()));

		clonedInv.setTime(getTime());
		clonedInv.setTotalAmount(getTotalAmount());
		clonedInv.setTotalBeforeTax(getTotalBeforeTax());
		clonedInv.setTotalDiscount(getTotalDiscount());
		clonedInv.setTotalDiscountAddition(getTotalDiscountAddition());
		clonedInv.setTotalDocumentDiscount(getTotalDocumentDiscount());
		clonedInv.setTotalDocumentExpense(getTotalDocumentExpense());
		clonedInv.setTotalExpense(getTotalExpense());
		clonedInv.setTotalExpenseAddition(getTotalExpenseAddition());
		clonedInv.setTotalFee(getTotalFee());
		clonedInv.setTotalTax(getTotalTax());
		clonedInv.setTotalTaxExcludedAmount(getTotalTaxExcludedAmount());
		clonedInv.setTradeAction(getTradeAction());
		clonedInv.setUpdateDate(getUpdateDate());
		clonedInv.setUpdateUser(getUpdateUser());
		clonedInv.setWarehouse(getWarehouse());
		
//		clonedInv.getShipmentList()
//		clonedInv.getReturnInvoiceList()
		
		for (TekirInvoiceCurrencyRate cr : getCurrencyRateList()) {
			TekirInvoiceCurrencyRate clonedcr = cr.clone();

			clonedcr.setOwner(clonedInv);
			clonedInv.getCurrencyRateList().add(clonedcr);
		}

		for (TekirInvoiceCurrencySummary cs : getCurrencySummaryList()) {
			TekirInvoiceCurrencySummary clonedcs = cs.clone();
			
			clonedcs.setOwner(clonedInv);
			clonedInv.getCurrencySummaryList().add(clonedcs);
		}
		
		for(InvoiceOrderLink ol : getOrderLinks()) {
			InvoiceOrderLink clonedol = ol.clone();

			clonedol.setInvoice(clonedInv);
			clonedInv.getOrderLinks().add(clonedol);
		}

		for (InvoicePaymentPlanItem ppi : getPaymentPlanItems()) {
			InvoicePaymentPlanItem clonedppi = ppi.clone();
			
			clonedppi.setOwner(clonedInv);
			clonedInv.getPaymentPlanItems().add(clonedppi);
		}

		for (TekirInvoiceTaxSummary ts : getTaxSummaryList()) {
			TekirInvoiceTaxSummary clonedts = ts.clone();
			
			clonedts.setOwner(clonedInv);
			clonedInv.getTaxSummaryList().add(clonedts);
		}
		
		for (int i=0;i<getItems().size();i++) {
			TekirInvoiceDetail tid = getItems().get(i);
			TekirInvoiceDetail clonedpid = tid.clone();
			clonedpid.setOwner(clonedInv);
			clonedInv.getItems().add(clonedpid);

			if ( tid.getChildList() != null && tid.getChildList().size() >0 ) {
				for (int j=0;j<tid.getChildList().size();j++) {
					TekirInvoiceDetail clonedcid = tid.getChildList().get(j).clone();
					
					clonedcid.setParent(clonedpid);
					clonedpid.getChildList().add(clonedcid);

					clonedcid.setOwner(clonedInv);
					clonedInv.getItems().add(clonedcid);

					i++;
				}
			}
		}
		return clonedInv;
	}
	
	@Override
	public List<TekirInvoiceCurrencyRate> getCurrencyRateList() {
		return currencyRateList;
	}

	@Override
	public List<TekirInvoiceCurrencySummary> getCurrencySummaryList() {
		return currencySummaryList;
	}

	@Override
	public List<? extends TenderDetailBase> getDocumentDiscountItems() {
		List<TekirInvoiceDetail> result = new ArrayList<TekirInvoiceDetail>();
		for (TenderDetailBase item : getItems()) {
			if (item.getProductType().equals(ProductType.DocumentDiscount) ||
					item.getProductType().equals(ProductType.ContactDiscount)) {
				result.add((TekirInvoiceDetail) item);
			}
		}
		return result;
	}

	@Override
	public List<? extends TenderDetailBase> getDocumentExpenseItems() {
		List<TekirInvoiceDetail> result = new ArrayList<TekirInvoiceDetail>();
		for (TenderDetailBase item : getItems()) {
			if (item.getProductType().equals(ProductType.DocumentExpense)) {
				result.add((TekirInvoiceDetail) item);
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
	public List<? extends TenderDetailBase> getDocumentFeeItems() {
		return getItems( new ProductType[]{ProductType.DocumentFee} );
	}

	private List<TenderDetailBase> getItems(ProductType[] typeList) {
		List<TenderDetailBase> result = new ArrayList<TenderDetailBase>();
		for (TenderDetailBase item : getItems()) {
			if ( Arrays.asList(typeList).contains(item.getProductType()) ) {
				result.add(item);
			}
		}
		return result;
	}
	
	@Override
	public List<TekirInvoiceDetail> getItems() {
		return items;
	}

	@Override
	public List<TekirInvoiceDetail> getProductItems() {
		List<TekirInvoiceDetail> result = new ArrayList<TekirInvoiceDetail>();
		for (TenderDetailBase item : getItems()) {
			if (item.getProductType().equals(ProductType.Product) || 
					item.getProductType().equals(ProductType.Service)) {
				result.add((TekirInvoiceDetail) item);
			}
		}
		return result;
	}

	@Override
	public List<TekirInvoiceTaxSummary> getTaxSummaryList() {
		return taxSummaryList;
	}

	public void setCurrencyRateList(List<TekirInvoiceCurrencyRate> currencyRateList) {
		this.currencyRateList = currencyRateList;
	}

	public void setTaxSummaryList(List<TekirInvoiceTaxSummary> taxSummaryList) {
		this.taxSummaryList = taxSummaryList;
	}

	public void setCurrencySummaryList(
			List<TekirInvoiceCurrencySummary> currencySummaryList) {
		this.currencySummaryList = currencySummaryList;
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

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public Date getShippingDate() {
		return shippingDate;
	}

	public void setShippingDate(Date shippingDate) {
		this.shippingDate = shippingDate;
	}

	public PaymentTable getPaymentTable() {
		return paymentTable;
	}

	public void setPaymentTable(PaymentTable paymentTable) {
		this.paymentTable = paymentTable;
	}

	public List<TekirShipmentNote> getShipmentList() {
		return shipmentList;
	}

	public void setShipmentList(List<TekirShipmentNote> shipmentList) {
		this.shipmentList = shipmentList;
	}

	public List<InvoiceOrderLink> getOrderLinks() {
		return orderLinks;
	}

	public void setOrderLinks(List<InvoiceOrderLink> orderLinks) {
		this.orderLinks = orderLinks;
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

	public List<TekirInvoice> getReturnInvoiceList() {
		return returnInvoiceList;
	}

	public void setReturnInvoiceList(List<TekirInvoice> returnInvoiceList) {
		this.returnInvoiceList = returnInvoiceList;
	}

	public TekirInvoice getReturnInvoice() {
		return returnInvoice;
	}

	public void setReturnInvoice(TekirInvoice returnInvoice) {
		this.returnInvoice = returnInvoice;
	}

	public ReturnInvoiceStatus getReturnInvoiceStatus() {
		return returnInvoiceStatus;
	}

	public void setReturnInvoiceStatus(ReturnInvoiceStatus returnInvoiceStatus) {
		this.returnInvoiceStatus = returnInvoiceStatus;
	}

	public List<InvoicePaymentPlanItem> getPaymentPlanItems() {
		return paymentPlanItems;
	}

	public void setPaymentPlanItems(List<InvoicePaymentPlanItem> paymentPlanItems) {
		this.paymentPlanItems = paymentPlanItems;
	}

	public PaymentPlan getPaymentPlan() {
		if (paymentPlan != null) {
			paymentPlan.convertPaymentStringToDays();
			paymentPlan.convertPaymentStringToWeeks();
		}
		return paymentPlan;
	}

	public void setPaymentPlan(PaymentPlan paymentPlan) {
		this.paymentPlan = paymentPlan;
	}

	public ProformaStatus getProformaStatus() {
		return proformaStatus;
	}

	public void setProformaStatus(ProformaStatus proformaStatus) {
		this.proformaStatus = proformaStatus;
	}

	public Boolean getMatchingFinished() {
		return matchingFinished;
	}

	public void setMatchingFinished(Boolean matchingFinished) {
		this.matchingFinished = matchingFinished;
	}

}
