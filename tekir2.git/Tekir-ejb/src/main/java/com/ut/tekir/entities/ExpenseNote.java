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
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.validator.Length;
import org.hibernate.validator.Valid;

import com.ut.tekir.entities.inv.TekirInvoice;

/**
 * Gider pusulası model sınıfıdır.
 * 
 * @author haky
 */
@Entity
@Table(name="EXPENSE_NOTE")
public class ExpenseNote extends DocumentBase implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE,generator="genericSeq")
    @Column(name="ID")
    private Long id;
    
    @ManyToOne
    @JoinColumn(name="CONTACT_ID")
    private Contact contact;
    
    @ManyToOne
    @JoinColumn(name="ACCOUNT_ID")
    private Account account;

    @ManyToOne
    @JoinColumn(name="BANK_ACCOUNT_ID")
    private BankAccount bankAccount;
    
    @Column(name="EXPENSE_OWNER", length=20)
    @Length(max=20)
    private String expenseOwner;

    @Column(name="PAYMENT_FROM")
    @Enumerated(EnumType.ORDINAL)
    private PaymentFrom paymentFrom = PaymentFrom.Account;

    /**
     * Hizmetin alindigi yer
     */
    @ManyToOne
    @JoinColumn(name="WAREHOUSE_ID")
    private Warehouse warehouse;

    @Column(name="DOCUMENT_TYPE_ID")
    @Enumerated(EnumType.ORDINAL)
    private DocumentType documentType = DocumentType.ExpenseNote;

    @Embedded
    @Valid
    @AttributeOverrides( {
        @AttributeOverride(name="currency", column=@Column(name="WITHHOLD_CCY")),
        @AttributeOverride(name="value",    column=@Column(name="WITHHOLD_VALUE"))
    })
    private Money totalWithhold = new Money();

    @Embedded
    @Valid
    @AttributeOverrides( {
        @AttributeOverride(name="currency", column=@Column(name="TOTAL_CCY")),
        @AttributeOverride(name="value",    column=@Column(name="TOTAL_VALUE"))
    })
    private Money totalAmount = new Money();

    @Embedded
    @Valid
    @AttributeOverrides( {
        @AttributeOverride(name="currency", column=@Column(name="TOTAL_NET_CCY")),
        @AttributeOverride(name="value",    column=@Column(name="TOTAL_NET_VALUE"))
    })
    private Money totalNetAmount = new Money();

    @Embedded
    @Valid
    @AttributeOverrides( {
    	@AttributeOverride(name="currency", column=@Column(name="TOTAL_TAX_CCY")),
    	@AttributeOverride(name="value",    column=@Column(name="TOTAL_TAX_VALUE"))
    })
    private Money totalTaxAmount = new Money();

    /**
     * Toplam masraf bilgisini tutar.
     */
    @Embedded
    @Valid
    @AttributeOverrides({
    	@AttributeOverride(name="currency", column=@Column(name="TOTAL_INVOICE_UNIT_EXPENSE_CCY")),
    	@AttributeOverride(name="value",    column=@Column(name="TOTAL_INVOICE_UNIT_EXPENSE_VALUE")),
    	@AttributeOverride(name="localAmount",    column=@Column(name="TOTAL_INVOICE_UNIT_EXPENSE_LCYVAL"))
    })
    private MoneySet totalInvoiceUnitExpense = new MoneySet();

    /**
     * Gider pusulasının iade edildiği fatura bilgisini tutar.
     */
    @ManyToOne
    @JoinColumn(name="INVOICE_ID")
    private TekirInvoice invoice;

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

    /**
     * satışı yapan tezgahtar bilgisini tutar.
     */
    @ManyToOne
    @JoinColumn(name="CLERK_ID")
    @ForeignKey(name="FK_EXPENSENOTE_CLERKID")
    private User clerk;

    /**
     * Faturadan gelen masrafların net tutara eklenip eklenmeyeceği bilgisini tutar.
     */
    @Column(name="ADD_INVOICE_EXPENSES")
    private boolean addInvoiceExpenses = true;

    @OneToMany(mappedBy="owner", cascade=CascadeType.ALL)
    @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    private List<ExpenseNoteItem> items = new ArrayList<ExpenseNoteItem>();

    @ManyToOne
    @JoinColumn(name="WORKBUNCH_ID")
    private WorkBunch workBunch;
    
    public TradeAction getTradeAction() {
    	if ( DocumentType.ReturnExpenseNote.equals(documentType) ) return TradeAction.SaleReturn;
    	return TradeAction.Purchase;
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
        if (!(object instanceof ExpenseNote)) {
            return false;
        }
        ExpenseNote other = (ExpenseNote)object;
        if (this.getId() != other.getId() && (this.getId() == null || !this.id.equals(other.id))) return false;
        return true;
    }

    @Override
    public String toString() {
        return "com.ut.tekir.entities.ExpenceNote[id=" + getId() + "]";
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

    public List<ExpenseNoteItem> getItems() {
        return items;
    }

    public void setItems(List<ExpenseNoteItem> items) {
        this.items = items;
    }

    public String getExpenseOwner() {
        return expenseOwner;
    }

    public void setExpenseOwner(String expenseOwner) {
        this.expenseOwner = expenseOwner;
    }

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

    /**
     * @return the bankAccount
     */
    public BankAccount getBankAccount() {
        return bankAccount;
    }

    /**
     * @param bankAccount the bankAccount to set
     */
    public void setBankAccount(BankAccount bankAccount) {
        this.bankAccount = bankAccount;
    }

    /**
     * @return the paymentFrom
     */
    public PaymentFrom getPaymentFrom() {
        return paymentFrom;
    }

    /**
     * @param paymentFrom the paymentFrom to set
     */
    public void setPaymentFrom(PaymentFrom paymentFrom) {
        this.paymentFrom = paymentFrom;
    }

    /**
     * @return the totalWithhold
     */
    public Money getTotalWithhold() {
        return totalWithhold;
    }

    /**
     * @param totalWithhold the totalWithhold to set
     */
    public void setTotalWithhold(Money totalWithhold) {
        this.totalWithhold = totalWithhold;
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
     * @return the totalNetAmount
     */
    public Money getTotalNetAmount() {
        return totalNetAmount;
    }

    /**
     * @param totalNetAmount the totalNetAmount to set
     */
    public void setTotalNetAmount(Money totalNetAmount) {
        this.totalNetAmount = totalNetAmount;
    }

    /**
     * @return the warehouse
     */
    public Warehouse getWarehouse() {
        return warehouse;
    }

    /**
     * @param warehouse the warehouse to set
     */
    public void setWarehouse(Warehouse warehouse) {
        this.warehouse = warehouse;
    }

	public TekirInvoice getInvoice() {
		return invoice;
	}

	public void setInvoice(TekirInvoice invoice) {
		this.invoice = invoice;
	}

	public DocumentType getDocumentType() {
		return documentType;
	}

	public void setDocumentType(DocumentType documentType) {
		this.documentType = documentType;
	}

	public Money getTotalTaxAmount() {
		return totalTaxAmount;
	}

	public void setTotalTaxAmount(Money totalTaxAmount) {
		this.totalTaxAmount = totalTaxAmount;
	}

	public Address getDeliveryAddress() {
		return deliveryAddress;
	}

	public void setDeliveryAddress(Address deliveryAddress) {
		this.deliveryAddress = deliveryAddress;
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

	public User getClerk() {
		return clerk;
	}

	public void setClerk(User clerk) {
		this.clerk = clerk;
	}

	public MoneySet getTotalInvoiceUnitExpense() {
		return totalInvoiceUnitExpense;
	}

	public void setTotalInvoiceUnitExpense(MoneySet totalInvoiceUnitExpense) {
		this.totalInvoiceUnitExpense = totalInvoiceUnitExpense;
	}

	public boolean isAddInvoiceExpenses() {
		return addInvoiceExpenses;
	}

	public void setAddInvoiceExpenses(boolean addInvoiceExpenses) {
		this.addInvoiceExpenses = addInvoiceExpenses;
	}

	public WorkBunch getWorkBunch() {
		return workBunch;
	}

	public void setWorkBunch(WorkBunch workBunch) {
		this.workBunch = workBunch;
	}

}
