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
import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;
import org.hibernate.validator.Valid;

import com.ut.tekir.framework.BaseConsts;

/**
 * Teklif, sipariş, irsaliye ve fatura masterlarındaki 
 * ortak bilgileri tutar.
 * @author sinan.yumak
 *
 */
@MappedSuperclass
public class TenderBase extends AuditBase implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.TABLE,generator="genericSeq")
    @Column(name="ID")
    private Long id;

    @Column(name="SERIAL", length=10, nullable=false, unique=true)
    @NotNull
    @Length(max=10, min=1)
    private String serial;
    
    @Column(name="CODE", length=15)
    @Length(max=15)
    private String code;

    /**
     * Normal fatura açıklaması olarak kullanılacaktır.
     */
    @Column(name="INFO")
    private String info;

    /**
     * Fatura yazdırılırken kullanılacak açıklamalardan ilki.
     */
    @Column(name="INFO1")
    private String info1;

    /**
     * Fatura yazdırılırken kullanılacak açıklamalardan ikincisi.
     */
    @Column(name="INFO2")
    private String info2;
    
    @Column(name="REFERENCE", length=10)
    @Length(max=10)
    private String reference;
    
    @Column(name="ISACTIVE")
    private Boolean active = Boolean.TRUE;
    
    @Column(name="TXNDATE")
    @Temporal(value = TemporalType.DATE)
    private Date date;

    //Belgenin Düzenlenme Saati
    @Column(name="TXNTIME")
    @Temporal(value = TemporalType.TIME)
    private Date time;

    @ManyToOne
    @JoinColumn(name="CONTACT_ID")
    private Contact contact;

    @Column(name="DOCUMENT_TYPE_ID")
    @Enumerated(EnumType.ORDINAL)
    private DocumentType documentType;

    /**
     * Teslimat süresi
     */
    @Column(name="DELIVERY_DATE")
    @Temporal(value = TemporalType.TIMESTAMP)
    private Date deliveryDate;

    //DIKKAT:Buradaki teslimat bilgileri cari kartı üzerinden çoklanmıştır.
    /**
     * Teslimat adresi
     */
    @Embedded
    @Valid
    @AttributeOverrides( {
        @AttributeOverride(name="street", column=@Column(name="DELIVERY_ADDRESS_STREET")),
        @AttributeOverride(name="province",    column=@Column(name="DELIVERY_ADDRESS_PROVINCE")),
        @AttributeOverride(name="city",    column=@Column(name="DELIVERY_ADDRESS_CITY")),
        @AttributeOverride(name="country",    column=@Column(name="DELIVERY_ADDRESS_COUNTRY")),
        @AttributeOverride(name="zip",    column=@Column(name="DELIVERY_ADDRESS_ZIP"))
    })
    private Address deliveryAddress = new Address();

    /**
     * Teslimat carisinin kişi mi kurum mu olduğu bilgisini tutar.
     */
    @Column(name="DELIVERY_PERSON")
    private Boolean deliveryPerson = Boolean.FALSE;

    /**
     * Teslimat carisi şirket ise şirket unvan bilgisini tutar.
     */
    @Column(name="DELIVERY_COMPANY", length=250)
    @Length(max=250)
    private String deliveryCompany;

    /**
     * Teslimat carisi kişi ise tc kimlik bilgisini tutar.
     */
    @Column(name="DELIVERY_SSN", length=20)
    @Length(max=20)
    private String deliverySsn;

    /**
     * Teslimat carisi kişi ise tam adı tutar.
     */
    @Column(name="DELIVERY_FULLNAME", length=92)
    @Length(max=92)
    private String deliveryFullname;

    /**
     * Vergi no
     */
    @Column(name="TAX_NUMBER")
    private String deliveryTaxNumber; 
    
    /**
     * Vergi dairesi
     */
    @Column(name="TAX_OFFICE")
    private String deliveryTaxOffice;

    /*
       Toplam bilgileri
     */
        
    //(Vergi Matrahı) Hem vergilerin hem de indirimlerin
    //(Satır, irsaliye ve fatura) düşüldüğü tutardır.
    //OTV varsa dahil olacak.
    @Embedded
    @Valid
    @AttributeOverrides( {
        @AttributeOverride(name="currency", column=@Column(name="TAX_EXCLUDED_TOTAL_CCY")),
        @AttributeOverride(name="value",    column=@Column(name="TAX_EXCLUDED_TOTAL_VALUE")),
        @AttributeOverride(name="localAmount",    column=@Column(name="TAX_EXCLUDED_TOTAL_LCYVAL"))
    })
    private MoneySet taxExcludedTotal = new MoneySet();

    /*
       Buradaki toplam masraf,indirim ve harç bilgi amaçlı tutulmaktadır.	
     */
    
    @Embedded
    @Valid
    @AttributeOverrides( {
        @AttributeOverride(name="currency", column=@Column(name="TOTAL_BEFORE_TAX_CCY")),
        @AttributeOverride(name="value",    column=@Column(name="TOTAL_BEFORE_TAX_VALUE")),
        @AttributeOverride(name="localAmount",    column=@Column(name="TOTAL_BEFORE_TAX_LCYVAL"))
    })
    private MoneySet totalBeforeTax = new MoneySet();

    @Embedded
    @Valid
    @AttributeOverrides( {
    	@AttributeOverride(name="currency", column=@Column(name="TOTAL_TAX_EXCLUDED_AMOUNT_CCY")),
    	@AttributeOverride(name="value",    column=@Column(name="TOTAL_TAX_EXCLUDED_AMOUNT_VALUE")),
    	@AttributeOverride(name="localAmount",    column=@Column(name="TOTAL_TAX_EXCLUDED_AMOUNT_LCYVAL"))
    })
    private MoneySet totalTaxExcludedAmount = new MoneySet();

    //vergi toplamı
    @Embedded
    @Valid
    @AttributeOverrides( {
        @AttributeOverride(name="currency", column=@Column(name="TOTAL_TAX_CCY")),
        @AttributeOverride(name="value",    column=@Column(name="TOTAL_TAX_VALUE")),
        @AttributeOverride(name="localAmount",    column=@Column(name="TOTAL_TAX_LCYVAL"))
    })
    private MoneySet totalTax = new MoneySet();

    /**
     * harç toplamı
     */
    @Embedded
    @Valid
    @AttributeOverrides( {
    	@AttributeOverride(name="currency", column=@Column(name="TOTAL_FEE_CCY")),
    	@AttributeOverride(name="value",    column=@Column(name="TOTAL_FEE_VALUE")),
    	@AttributeOverride(name="localAmount",    column=@Column(name="TOTAL_FEE_LCYVAL"))
    })
    private MoneySet totalFee = new MoneySet();

    /**
     * Satır masraf toplamını tutar.(Satırlara yansıtılır.)
     */
    @Embedded
    @Valid
    @AttributeOverrides( {
    	@AttributeOverride(name="currency", column=@Column(name="TOTAL_EXPENSE_CCY")),
    	@AttributeOverride(name="value",    column=@Column(name="TOTAL_EXPENSE_VALUE")),
    	@AttributeOverride(name="localAmount",    column=@Column(name="TOTAL_EXPENSE_LCYVAL"))
    })
    private MoneySet totalExpense = new MoneySet();

    /**
     * Döküman masraf toplamını tutar.(Satırlara yansıtılır.)
     */
    @Embedded
    @Valid
    @AttributeOverrides( {
    	@AttributeOverride(name="currency", column=@Column(name="TOTAL_DOCUMENT_EXPENSE_CCY")),
    	@AttributeOverride(name="value",    column=@Column(name="TOTAL_DOCUMENT_EXPENSE_VALUE")),
    	@AttributeOverride(name="localAmount",    column=@Column(name="TOTAL_DOCUMENT_EXPENSE_LCYVAL"))
    })
    private MoneySet totalDocumentExpense = new MoneySet();
    
    /**
     * Satır indirimlerinin toplamını tutar.(Satırlara yansıtılır.)
     */
    @Embedded
    @Valid
    @AttributeOverrides( {
    	@AttributeOverride(name="currency", column=@Column(name="TOTAL_DISCOUNT_CCY")),
    	@AttributeOverride(name="value",    column=@Column(name="TOTAL_DISCOUNT_VALUE")),
    	@AttributeOverride(name="localAmount",    column=@Column(name="TOTAL_DISCOUNT_LCYVAL"))
    })
    private MoneySet totalDiscount = new MoneySet();

    /**
     * Döküman indirimlerinin toplamını tutar.(Satırlara yansıtılır.)
     */
    @Embedded
    @Valid
    @AttributeOverrides( {
    	@AttributeOverride(name="currency", column=@Column(name="TOTAL_DOCUMENT_DISCOUNT_CCY")),
    	@AttributeOverride(name="value",    column=@Column(name="TOTAL_DOCUMENT_DISCOUNT_VALUE")),
    	@AttributeOverride(name="localAmount",    column=@Column(name="TOTAL_DOCUMENT_DISCOUNT_LCYVAL"))
    })
    private MoneySet totalDocumentDiscount = new MoneySet();

    /**
     * Belge dip toplamından(satırlara dağıtılmadan) vergi dahil yapılan indirimlerin toplamıdır.
     */
    @Embedded
    @Valid
    @AttributeOverrides( {
        @AttributeOverride(name="currency", column=@Column(name="TOTAL_DISCOUNT_ADDITION_CCY")),
        @AttributeOverride(name="value",    column=@Column(name="TOTAL_DISCOUNT_ADDITION_VALUE")),
        @AttributeOverride(name="localAmount",    column=@Column(name="TOTAL_DISCOUNT_ADDITION_LCYVAL"))
    })
    private MoneySet totalDiscountAddition = new MoneySet();

    /**
     * Belge dip toplamından(satırlara dağıtılmadan) vergi dahil yapılan eklemlerin toplamıdır.
     */
    @Embedded
    @Valid
    @AttributeOverrides( {
    	@AttributeOverride(name="currency", column=@Column(name="TOTAL_EXPENSE_ADDITION_CCY")),
    	@AttributeOverride(name="value",    column=@Column(name="TOTAL_EXPENSE_ADDITION_VALUE")),
    	@AttributeOverride(name="localAmount",    column=@Column(name="TOTAL_EXPENSE_ADDITION_LCYVAL"))
    })
    private MoneySet totalExpenseAddition = new MoneySet();

    /**
     * Tüm işlemler bittikten sonra elde edilen tutar bilgisidir.
     */
    @Embedded
    @Valid
    @AttributeOverrides( {
        @AttributeOverride(name="currency", column=@Column(name="TOTAL_AMOUNT_CCY")),
        @AttributeOverride(name="value",    column=@Column(name="TOTAL_AMOUNT_VALUE")),
        @AttributeOverride(name="localAmount",    column=@Column(name="TOTAL_AMOUNT_LCYVAL"))
    })
    private MoneySet totalAmount = new MoneySet();

    /**
     * Satırın herşey dahil son tutarı.
     */
    @Embedded
    @Valid
    @AttributeOverrides( {
        @AttributeOverride(name="currency", column=@Column(name="GRAND_TOTAL_CCY")),
        @AttributeOverride(name="value",    column=@Column(name="GRAND_TOTAL_VALUE")),
        @AttributeOverride(name="localAmount",    column=@Column(name="GRAND_TOTAL_LCYVAL"))
    })
    private MoneySet grandTotal = new MoneySet();

    /**
     * satışı yapan tezgahtar bilgisini tutar.
     */
    @ManyToOne
    @JoinColumn(name="CLERK_ID")
    private User clerk;

    /**
     * Dökümanın iptal edilip edilmediği bilgisini tutar.
     */
    @Column(name="CANCELLED")
    private boolean cancelled;
    
    /**
     * Dökümanın iptal edilme sebebini tutar.
     */
    @Column(name="CANCEL_INFO")
    private String cancelInfo;

    /**
     * Dökümanın iptal edilme tarihini tutar.
     */
    @Column(name="CANCEL_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date cancelDate;
    
    @Column(name="DOC_CURRENCY")
    private String docCurrency = BaseConsts.SYSTEM_CURRENCY_CODE;//dökümanın döviz tipi bilgisini tutar.

    @Enumerated(EnumType.ORDINAL)
    @Column(name="RATE_TYPE")
    private CurrencyRateType rateType = CurrencyRateType.Ask;
    
    @ManyToOne
    @JoinColumn(name="WORKBUNCH_ID")
    private WorkBunch workBunch;

    public boolean isProformaDocument() {
    	return DocumentType.SaleProformaInvoice.equals(documentType);
    }
    
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public MoneySet getTotalTaxExcludedAmount() {
		if (totalTaxExcludedAmount == null) {
			totalTaxExcludedAmount = new MoneySet();
		}
		return totalTaxExcludedAmount;
	}

	public void setTotalTaxExcludedAmount(MoneySet totalTaxExcludedAmount) {
		this.totalTaxExcludedAmount = totalTaxExcludedAmount;
	}

	public String getSerial() {
		return serial;
	}

	public void setSerial(String serial) {
		this.serial = serial;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
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

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public MoneySet getTotalFee() {
		if (totalFee == null) {
			totalFee = new MoneySet();
		}
		return totalFee;
	}

	public void setTotalFee(MoneySet totalFee) {
		this.totalFee = totalFee;
	}

	public MoneySet getTotalExpense() {
		if (totalExpense == null) {
			totalExpense = new MoneySet();
		}
		return totalExpense;
	}

	public void setTotalExpense(MoneySet totalExpense) {
		this.totalExpense = totalExpense;
	}

	public MoneySet getTotalDiscount() {
		if (totalDiscount == null) {
			totalDiscount = new MoneySet();
		}
		return totalDiscount;
	}

	public void setTotalDiscount(MoneySet totalDiscount) {
		this.totalDiscount = totalDiscount;
	}

	public MoneySet getTotalTax() {
		return totalTax;
	}

	public void setTotalTax(MoneySet totalTax) {
		this.totalTax = totalTax;
	}

	public MoneySet getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(MoneySet totalAmount) {
		this.totalAmount = totalAmount;
	}

	public Contact getContact() {
		return contact;
	}

	public void setContact(Contact contact) {
		this.contact = contact;
	}

	public Address getDeliveryAddress() {
		if (deliveryAddress == null) {
			deliveryAddress = new Address();
		}
		return deliveryAddress;
	}

	public void setDeliveryAddress(Address deliveryAddress) {
		this.deliveryAddress = deliveryAddress;
	}

	public Date getDeliveryDate() {
		return deliveryDate;
	}

	public void setDeliveryDate(Date deliveryDate) {
		this.deliveryDate = deliveryDate;
	}

	public MoneySet getTaxExcludedTotal() {
		if (taxExcludedTotal == null) {
			taxExcludedTotal = new MoneySet(); 
		}
		return taxExcludedTotal;
	}

	public void setTaxExcludedTotal(MoneySet taxExcludedTotal) {
		this.taxExcludedTotal = taxExcludedTotal;
	}


	public MoneySet getTotalBeforeTax() {
		return totalBeforeTax;
	}

	public void setTotalBeforeTax(MoneySet totalBeforeTax) {
		this.totalBeforeTax = totalBeforeTax;
	}

	public DocumentType getDocumentType() {
		return documentType;
	}

	public void setDocumentType(DocumentType documentType) {
		this.documentType = documentType;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public MoneySet getTotalDocumentDiscount() {
		if (totalDocumentDiscount == null) {
			totalDocumentDiscount = new MoneySet();
		}
		return totalDocumentDiscount;
	}

	public void setTotalDocumentDiscount(MoneySet totalDocumentDiscount) {
		this.totalDocumentDiscount = totalDocumentDiscount;
	}

    public User getClerk() {
        return clerk;
    }

    public void setClerk(User clerk) {
        this.clerk = clerk;
    }

	public Boolean getDeliveryPerson() {
		return deliveryPerson;
	}

	public void setDeliveryPerson(Boolean deliveryPerson) {
		this.deliveryPerson = deliveryPerson;
	}

	public String getDeliveryCompany() {
		return deliveryCompany;
	}

	public void setDeliveryCompany(String deliveryCompany) {
		this.deliveryCompany = deliveryCompany;
	}

	public String getDeliverySsn() {
		return deliverySsn;
	}

	public void setDeliverySsn(String deliverySsn) {
		this.deliverySsn = deliverySsn;
	}

	public String getDeliveryFullname() {
		return deliveryFullname;
	}

	public void setDeliveryFullname(String deliveryFullname) {
		this.deliveryFullname = deliveryFullname;
	}

	public String getDeliveryTaxNumber() {
		return deliveryTaxNumber;
	}

	public void setDeliveryTaxNumber(String deliveryTaxNumber) {
		this.deliveryTaxNumber = deliveryTaxNumber;
	}

	public String getDeliveryTaxOffice() {
		return deliveryTaxOffice;
	}

	public void setDeliveryTaxOffice(String deliveryTaxOffice) {
		this.deliveryTaxOffice = deliveryTaxOffice;
	}

	public boolean isCancelled() {
		return cancelled;
	}

	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}

	public String getCancelInfo() {
		return cancelInfo;
	}

	public void setCancelInfo(String cancelInfo) {
		this.cancelInfo = cancelInfo;
	}

	public Date getCancelDate() {
		return cancelDate;
	}

	public void setCancelDate(Date cancelDate) {
		this.cancelDate = cancelDate;
	}

	public MoneySet getTotalDocumentExpense() {
		if (totalDocumentExpense == null) {
			totalDocumentExpense = new MoneySet();
		}

		return totalDocumentExpense;
	}

	public void setTotalDocumentExpense(MoneySet totalDocumentExpense) {
		this.totalDocumentExpense = totalDocumentExpense;
	}

	public MoneySet getTotalDiscountAddition() {
		if (totalDiscountAddition == null) {
			totalDiscountAddition = new MoneySet();
		}
		return totalDiscountAddition;
	}

	public void setTotalDiscountAddition(MoneySet totalDiscountAddition) {
		this.totalDiscountAddition = totalDiscountAddition;
	}

	public MoneySet getTotalExpenseAddition() {
		if (totalExpenseAddition == null) {
			totalExpenseAddition = new MoneySet();
		}
		return totalExpenseAddition;
	}

	public void setTotalExpenseAddition(MoneySet totalExpenseAddition) {
		this.totalExpenseAddition = totalExpenseAddition;
	}

	public MoneySet getGrandTotal() {
		if (grandTotal == null) {
			grandTotal = new MoneySet();
		}
		return grandTotal;
	}

	public void setGrandTotal(MoneySet grandTotal) {
		this.grandTotal = grandTotal;
	}

	public String getDocCurrency() {
		return docCurrency;
	}

	public void setDocCurrency(String docCurrency) {
		this.docCurrency = docCurrency;
	}

	public CurrencyRateType getRateType() {
		return rateType;
	}

	public void setRateType(CurrencyRateType rateType) {
		this.rateType = rateType;
	}

	public WorkBunch getWorkBunch() {
		return workBunch;
	}

	public void setWorkBunch(WorkBunch workBunch) {
		this.workBunch = workBunch;
	}

}
