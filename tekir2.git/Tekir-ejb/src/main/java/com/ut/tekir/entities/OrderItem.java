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

@Entity
@Table(name="ORDER_ITEM")
public class OrderItem implements Serializable{

	private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE,generator="genericSeq")
    @Column(name="ID")
    private Long id;
    
    @ManyToOne
    @JoinColumn(name="OWNER_ID")
    private OrderNote owner;
    
    @ManyToOne
    @JoinColumn(name="PRODUCT_ID")
    private Product product;
    
    @Column(name="LINE_CODE", length=10)
    @Length(max=10)
    private String lineCode;
    
    @Column(name="INFO")
    private String info;
    
    @Embedded
    @Valid
    private Quantity quantity = new Quantity();
    
    @Column(name="APPROVED_QUANTITY", precision=5, scale=2)
    private Double approvedQuantity = 0d;
    
    @Column(name="CLOSED_QUANTITY", precision=5, scale=2)
    private Double closedQuantity = 0d;
    
    @Embedded
    @Valid
    @AttributeOverrides( {
        @AttributeOverride(name="currency", column=@Column(name="PRICECCY")),
        @AttributeOverride(name="value", column=@Column(name="PRICEVALUE"))
    })
    private Money unitPrice = new Money(); 
    
    @Embedded
    @AttributeOverrides( {
        @AttributeOverride(name="currency", column=@Column(name="CCY")),
        @AttributeOverride(name="value",    column=@Column(name="CCYVAL")),
        @AttributeOverride(name="localAmount", column=@Column(name="LCYVAL"))
    })
    private MoneySet amount = new MoneySet(); 
    
    @Column(name="TAX_RATE", precision=10, scale=2)
    private BigDecimal taxRate = BigDecimal.ZERO;
    
    @Embedded
    @Valid
    @AttributeOverrides( {
        @AttributeOverride(name="currency", column=@Column(name="TAXCCY")),
        @AttributeOverride(name="value", column=@Column(name="TAXVALUE")),
        @AttributeOverride(name="localAmount", column=@Column(name="TAXLCYVAL"))
    })
    private MoneySet taxAmount = new MoneySet(); 
    
    @Embedded
    @Valid
    @AttributeOverrides( {
        @AttributeOverride(name="currency", column=@Column(name="TOTALCCY")),
        @AttributeOverride(name="value", column=@Column(name="TOTALVALUE")),
        @AttributeOverride(name="localAmount", column=@Column(name="TOTALLCYVAL"))
    })
    private Money totalAmaount = new Money();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public OrderNote getOwner() {
		return owner;
	}

	public void setOwner(OrderNote owner) {
		this.owner = owner;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
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

	public Quantity getQuantity() {
		return quantity;
	}

	public void setQuantity(Quantity quantity) {
		this.quantity = quantity;
	}

	public Double getApprovedQuantity() {
		return approvedQuantity;
	}

	public void setApprovedQuantity(Double approvedQuantity) {
		this.approvedQuantity = approvedQuantity;
	}

	public Double getClosedQuantity() {
		return closedQuantity;
	}

	public void setClosedQuantity(Double closedQuantity) {
		this.closedQuantity = closedQuantity;
	}

	public Money getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(Money unitPrice) {
		this.unitPrice = unitPrice;
	}

	public MoneySet getAmount() {
		return amount;
	}

	public void setAmount(MoneySet amount) {
		this.amount = amount;
	}

	public BigDecimal getTaxRate() {
		return taxRate;
	}

	public void setTaxRate(BigDecimal taxRate) {
		this.taxRate = taxRate;
	}

	public MoneySet getTaxAmount() {
		return taxAmount;
	}

	public void setTaxAmount(MoneySet taxAmount) {
		this.taxAmount = taxAmount;
	}

	public Money getTotalAmaount() {
		return totalAmaount;
	}

	public void setTotalAmaount(Money totalAmaount) {
		this.totalAmaount = totalAmaount;
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
        if (!(object instanceof OrderItem)) {
            return false;
        }
        OrderItem other = (OrderItem)object;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) return false;
        return true;
    }

    @Override
    public String toString() {
        return "com.ut.tekir.entities.OrderItem[id=" + id + "]";
    }

}
