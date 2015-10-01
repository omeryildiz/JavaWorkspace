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
import javax.persistence.Transient;

import org.hibernate.annotations.Cascade;
import org.hibernate.validator.Valid;

import com.ut.tekir.entities.inv.TekirInvoice;
import com.ut.tekir.entities.ord.TekirOrderNote;



/**
 * Entity class Payment
 * 
 * @author haky
 */
@Entity
@Table(name="PAYMENT")
public class Payment extends DocumentBase implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE,generator="genericSeq")
    @Column(name="ID")
    private Long id;
    
    @Column(name="FINANCE_ACTION")
    @Enumerated(EnumType.ORDINAL)
    private FinanceAction action;
    
    @ManyToOne
    @JoinColumn(name="CONTACT_ID")
    private Contact contact;
    
    @ManyToOne
    @JoinColumn(name="ACCOUNT_ID")
    private Account account;
    
    @OneToMany(mappedBy="owner", cascade=CascadeType.ALL)
    @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    private List<PaymentItem> items = new ArrayList<PaymentItem>();
    
    @OneToMany(mappedBy="payment", cascade=CascadeType.ALL)
    @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    private List<PaymentCurrencyRate> currencyRates = new ArrayList<PaymentCurrencyRate>();

    @Embedded
    @Valid
    @AttributeOverrides( {
        @AttributeOverride(name="currency", column=@Column(name="TOTAL_AMOUNT_CCY")),
        @AttributeOverride(name="value",    column=@Column(name="TOTAL_AMOUNT_VALUE"))
    })
    private Money totalAmount = new Money();
    
    /**
     * eğer bir fatura aracılığı ile oluşturulmuşsa, faturanın
     * bilgilerini tutar.
     */
    @ManyToOne
    @JoinColumn(name="INVOICE_ID")
    private TekirInvoice invoice;

    /**
     * eğer bir sipariş aracılığı ile oluşturulmuşsa, siparişin 
     * bilgilerini tutar.
     */
    @ManyToOne
    @JoinColumn(name="ORDER_NOTE_ID")
    private TekirOrderNote orderNote;
    
    /**
     * İade gider pusulası bilgisini tutar.
     */
    @ManyToOne
    @JoinColumn(name="EXPENSE_NOTE_ID")
    private ExpenseNote expenseNote;

    @Enumerated(EnumType.ORDINAL)
    @Column(name="RATE_TYPE")
    private CurrencyRateType rateType = CurrencyRateType.Ask;

    @Enumerated(EnumType.ORDINAL)
    @Column(name="PROCESS_TYPE")
    private AdvanceProcessType processType = AdvanceProcessType.Normal;

    public Payment() {
    }
    
    public Payment(Long anId, String aSerial) {
    	setId(anId);
    	setSerial(aSerial);
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TekirInvoice getInvoice() {
		return invoice;
	}

	public void setInvoice(TekirInvoice invoice) {
		this.invoice = invoice;
	}

	public FinanceAction getAction() {
        return action;
    }

    public void setAction(FinanceAction action) {
        this.action = action;
    }

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public List<PaymentItem> getItems() {
        return items;
    }

    public void setItems(List<PaymentItem> items) {
        this.items = items;
    }

	public Money getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(Money totalAmount) {
		this.totalAmount = totalAmount;
	}
    
    @Transient
    public DocumentTotal getTotals(){
        DocumentTotal tot = new DocumentTotal();

        for( PaymentItem item : getItems() ){
        	if( item.getLineType() == PaymentType.Cash ){
        		tot.add( item.getAmount());
        	}
        }

        return tot;
    }

	@Override
	public DocumentType getDocumentType() {
		return action.equals(FinanceAction.Debit) ? DocumentType.Payment : DocumentType.Collection;
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
        if (!(object instanceof Payment)) {
            return false;
        }
        Payment other = (Payment)object;
        if (this.getId() != other.getId() && (this.getId() == null || !this.id.equals(other.id))) return false;
        return true;
    }

    @Override
    public String toString() {
        return "com.ut.tekir.entities.Payment[id=" + getId() + "]";
    }

	public List<PaymentCurrencyRate> getCurrencyRates() {
		return currencyRates;
	}

	public void setCurrencyRates(List<PaymentCurrencyRate> currencyRates) {
		this.currencyRates = currencyRates;
	}

	public TekirOrderNote getOrderNote() {
		return orderNote;
	}

	public void setOrderNote(TekirOrderNote orderNote) {
		this.orderNote = orderNote;
	}

	public ExpenseNote getExpenseNote() {
		return expenseNote;
	}

	public void setExpenseNote(ExpenseNote expenseNote) {
		this.expenseNote = expenseNote;
	}

	public CurrencyRateType getRateType() {
		return rateType;
	}

	public void setRateType(CurrencyRateType rateType) {
		this.rateType = rateType;
	}

	public AdvanceProcessType getProcessType() {
		return processType;
	}

	public void setProcessType(AdvanceProcessType processType) {
		this.processType = processType;
	}

}
