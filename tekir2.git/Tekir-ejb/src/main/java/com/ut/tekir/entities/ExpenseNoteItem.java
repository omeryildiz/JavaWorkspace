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
import java.math.BigDecimal;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.validator.Length;
import org.hibernate.validator.Valid;

/**
 * Entity class ExpenseNoteItem
 * 
 * @author haky
 */
@Entity
@Table(name="EXPENSE_NOTE_ITEM")
public class ExpenseNoteItem implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE,generator="genericSeq")
    @Column(name="ID")
    private Long id;

    @ManyToOne
    @JoinColumn(name="OWNER_ID")
    private ExpenseNote owner;

    @Column(name="LINE_CODE", length=10)
    @Length(max=10)
    private String lineCode;
    
    @Column(name="INFO")
    private String info;
    
    @ManyToOne
    @JoinColumn(name="PRODUCT_ID")
    private Product service;

    @Embedded
    @Valid
    private Quantity quantity = new Quantity();
    
    @Embedded
    @Valid
    @AttributeOverrides({
        @AttributeOverride(name="currency", column=@Column(name="PRICE_CCY")),
        @AttributeOverride(name="value",    column=@Column(name="PRICE_VALUE")),
        @AttributeOverride(name="localAmount", column=@Column(name="PRICE_LCYVAL"))
    })
    private MoneySet unitPrice = new MoneySet();

    /**
     *  Brüt tutar olmalı
     */
    @Embedded
    @Valid
    @AttributeOverrides({
        @AttributeOverride(name="currency", column=@Column(name="AMOUNT_CCY")),
        @AttributeOverride(name="value",    column=@Column(name="AMOUNT_VALUE")),
        @AttributeOverride(name="localAmount", column=@Column(name="AMOUNT_LCYVAL"))
    })
    private MoneySet amount = new MoneySet();

    /**
     * Stopaj oran bilgisini tutar.
     */
    @Column(name="WITHHOLD_RATE", precision=10, scale=2)
    private BigDecimal withholdRate = BigDecimal.ZERO;

    /**
     * Stopaj tutar bilgisini tutar.
     */
    @Embedded
    @AttributeOverrides( {
        @AttributeOverride(name="currency",    column=@Column(name="WITHHOLD_CCY", length=3)),
        @AttributeOverride(name="value",       column=@Column(name="WITHHOLD_VALUE", precision=10, scale=2)),
        @AttributeOverride(name="localAmount", column=@Column(name="WITHHOLD_LCYVAL", precision=10, scale=2))
    })
    @Valid
    private MoneySet withholdAmount = new MoneySet();

    @Embedded
    @Valid
    @AttributeOverrides({
        @AttributeOverride(name="currency", column=@Column(name="NET_AMT_CCY")),
        @AttributeOverride(name="value",    column=@Column(name="NET_AMT_VALUE")),
        @AttributeOverride(name="localAmount",    column=@Column(name="NET_AMT_LCYVAL"))
    })
    private MoneySet netAmount = new MoneySet();

    @Embedded
    @Valid
    @AttributeOverrides({
    	@AttributeOverride(name="currency", column=@Column(name="TAX_AMOUNT_CCY")),
    	@AttributeOverride(name="value",    column=@Column(name="TAX_AMOUNT_VALUE")),
    	@AttributeOverride(name="localAmount",    column=@Column(name="TAX_AMOUNT_LCYVAL"))
    })
    private MoneySet taxAmount = new MoneySet();

    /**
     * Ürünün faturadaki birim satış maliyeti bilgisini tutar. Vergi dahil,
     * masraflar hariçtir.
     */
    @Embedded
    @Valid
    @AttributeOverrides({
    	@AttributeOverride(name="currency", column=@Column(name="INVOICE_UNIT_PRICE_CCY")),
    	@AttributeOverride(name="value",    column=@Column(name="INVOICE_UNIT_PRICE_VALUE")),
    	@AttributeOverride(name="localAmount",    column=@Column(name="INVOICE_UNIT_PRICE_LCYVAL"))
    })
    private MoneySet invoiceUnitPrice = new MoneySet();

    /**
     * Ürünün faturadaki birim başına düşen masraf bilgisini tutar.
     */
    @Embedded
    @Valid
    @AttributeOverrides({
    	@AttributeOverride(name="currency", column=@Column(name="INVOICE_UNIT_EXPENSE_CCY")),
    	@AttributeOverride(name="value",    column=@Column(name="INVOICE_UNIT_EXPENSE_VALUE")),
    	@AttributeOverride(name="localAmount",    column=@Column(name="INVOICE_UNIT_EXPENSE_LCYVAL"))
    })
    private MoneySet invoiceUnitExpense = new MoneySet();

    /**
     * bu satırın hangi belge detayı aracılığıyla eklendiği bilgisini tutar.
     */
    @Column(name="REFERENCE_ID")
    private Long referenceDocumentId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ExpenseNote getOwner() {
        return owner;
    }

    public void setOwner(ExpenseNote owner) {
        this.owner = owner;
    }

    public String getLineCode() {
        return lineCode;
    }

    public void setLineCode(String lineCode) {
        this.lineCode = lineCode;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public Product getService() {
        return service;
    }

    public void setService(Product service) {
        this.service = service;
    }

    public Quantity getQuantity() {
        return quantity;
    }

    public void setQuantity(Quantity quantity) {
        this.quantity = quantity;
    }

    public MoneySet getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(MoneySet unitPrice) {
        this.unitPrice = unitPrice;
    }

    public MoneySet getAmount() {
        return amount;
    }

    public void setAmount(MoneySet amount) {
        this.amount = amount;
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
        if (!(object instanceof ExpenseNoteItem)) {
            return false;
        }
        ExpenseNoteItem other = (ExpenseNoteItem)object;
        if (this.getId() != other.getId() && (this.getId() == null || !this.id.equals(other.id))) return false;
        return true;
    }

    @Override
    public String toString() {
        return "com.ut.tekir.entities.ExpenceNoteItem[id=" + getId() + "]";
    }

    /**
     * @return the withholdAmount
     */
    public MoneySet getWithholdAmount() {
        return withholdAmount;
    }

    /**
     * @param withholdAmount the withholdAmount to set
     */
    public void setWithholdAmount(MoneySet withholdAmount) {
        this.withholdAmount = withholdAmount;
    }

    /**
     * @return the withholdRate
     */
    public BigDecimal getWithholdRate() {
        return withholdRate;
    }

    /**
     * @param withholdRate the withholdRate to set
     */
    public void setWithholdRate(BigDecimal withholdRate) {
        this.withholdRate = withholdRate;
    }

    /**
     * @return the netAmount
     */
    public MoneySet getNetAmount() {
        return netAmount;
    }

    /**
     * @param netAmount the netAmount to set
     */
    public void setNetAmount(MoneySet netAmount) {
        this.netAmount = netAmount;
    }

	public Long getReferenceDocumentId() {
		return referenceDocumentId;
	}

	public void setReferenceDocumentId(Long referenceDocumentId) {
		this.referenceDocumentId = referenceDocumentId;
	}

	public MoneySet getTaxAmount() {
		return taxAmount;
	}

	public void setTaxAmount(MoneySet taxAmount) {
		this.taxAmount = taxAmount;
	}

	public MoneySet getInvoiceUnitPrice() {
		return invoiceUnitPrice;
	}

	public void setInvoiceUnitPrice(MoneySet invoiceUnitPrice) {
		this.invoiceUnitPrice = invoiceUnitPrice;
	}

	public MoneySet getInvoiceUnitExpense() {
		return invoiceUnitExpense;
	}

	public void setInvoiceUnitExpense(MoneySet invoiceUnitExpense) {
		this.invoiceUnitExpense = invoiceUnitExpense;
	}

}
