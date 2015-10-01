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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cascade;
import org.hibernate.validator.Valid;

import com.ut.tekir.entities.ITenderDetail;
import com.ut.tekir.entities.MoneySet;
import com.ut.tekir.entities.ProductType;
import com.ut.tekir.entities.TenderDetailBase;
import com.ut.tekir.entities.User;

/**
 * @author sinan.yumak
 *
 */
@Entity
@Table(name="TEKIR_INVOICE_DETAIL")
public class TekirInvoiceDetail extends TenderDetailBase implements ITenderDetail {

	private static final long serialVersionUID = 1L;

    @ManyToOne
    @JoinColumn(name="OWNER_ID")
    private TekirInvoice owner;

    @ManyToOne
    @JoinColumn(name="PARENT_ID")
    private TekirInvoiceDetail parent;

    //Fatura üzerindeki döküman indirimlerinden gelen indirim tutarını tutar. 
    @Embedded
    @Valid
    @AttributeOverrides( {
        @AttributeOverride(name="currency", column=@Column(name="INVOICE_DISCOUNT_CCY")),
        @AttributeOverride(name="value",    column=@Column(name="INVOICE_DISCOUNT_VALUE")),
        @AttributeOverride(name="localAmount", column=@Column(name="INVOICE_DISCOUNT_LCYVAL"))
    })
    private MoneySet invoiceDiscount;
    
    //Fatura üzerindeki döküman masraflarından gelen masraf tutarını tutar.
    @Embedded
    @Valid
    @AttributeOverrides( {
    	@AttributeOverride(name="currency", column=@Column(name="INVOICE_EXPENSE_CCY")),
    	@AttributeOverride(name="value",    column=@Column(name="INVOICE_EXPENSE_VALUE")),
    	@AttributeOverride(name="localAmount", column=@Column(name="INVOICE_EXPENSE_LCYVAL"))
    })
    private MoneySet invoiceExpense;

    //Bu detaya bağlı masraf ve indirim satırlarını tutar.
    @OneToMany(mappedBy="parent",cascade=CascadeType.ALL)
    @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    private List<TekirInvoiceDetail> childList = new ArrayList<TekirInvoiceDetail>();
    
    //Fatura ekranlarında barkodla yapılan işlemleri kolaylaştırmak adına 
    //eklendi.
    @Transient
    private String barcode;
    
    /**
     * satışı yapan tezgahtar bilgisini tutar.
     */
    @ManyToOne
    @JoinColumn(name="CLERK_ID")
    private User clerk;

    //TODO:Acaba tüm bir satırı iade etmektense, siparişte olduğu gibi
    //satırdan istenilen miktarda iade edilebilmesi sağlanmalı mı? 
    /**
     * Eğer fatura iade edilmişse, satırın iade edilip edilmediği
     * bilgisini tutar.
     */
    @Column(name="RETURNED")
    private boolean returned = false;

    public String lineDiscountString() {
    	StringBuffer result = new StringBuffer();
    	BigDecimal rate = BigDecimal.ZERO;
    	BigDecimal value = BigDecimal.ZERO;
    	boolean percentage = true;
    	if (getProductType().equals(ProductType.Product)) {
    		rate = getDiscount().getRate();
    		value = getDiscount().getValue();
    		percentage = getDiscount().getPercentage();

    	} else if (getProductType().equals(ProductType.Discount)){
    		rate = getDiscount().getRate();
    		value = getAmount().getValue();
    		percentage = getDiscount().getPercentage();
    	
    	} else if (getProductType().equals(ProductType.DocumentDiscount) ||
    			   		getProductType().equals(ProductType.ContactDiscount) || 
    			   		getProductType().equals(ProductType.DiscountAddition)) {
    		rate = getDiscount().getRate();
    		value = getDiscount().getValue();
    		percentage = getDiscount().getPercentage();

    	}
		if (percentage && !rate.equals(BigDecimal.ZERO)) {
			result.append(getNumberFormat().format(value))
				  .append("(%")
				  .append(getNumberFormat().format(rate))
				  .append(")");
		} else {
			result.append(getNumberFormat().format(value));
		}
    	return result.toString();
    }
    
    public String lineExpenseString() {
    	StringBuffer result = new StringBuffer();
    	BigDecimal rate = BigDecimal.ZERO;
    	BigDecimal value = BigDecimal.ZERO;
    	boolean percentage = true;
    	if (getProductType().equals(ProductType.Product)) {
    		rate = getExpense().getRate();
    		value = getExpense().getValue();
    		percentage = getExpense().getPercentage();
    		
    	} else if (getProductType().equals(ProductType.Expense)){
    		rate = getExpense().getRate();
    		value = getAmount().getValue();
    		percentage = getExpense().getPercentage();
    		
    	} else if (getProductType().equals(ProductType.DocumentExpense) ||
    			getProductType().equals(ProductType.ExpenseAddition)) {
    		rate = getExpense().getRate();
    		value = getExpense().getValue();
    		percentage = getExpense().getPercentage();
    		
    	}
    	if (percentage && !rate.equals(BigDecimal.ZERO)) {
    		result.append(getNumberFormat().format(value))
    		.append("(%")
    		.append(getNumberFormat().format(rate))
    		.append(")");
    	} else {
    		result.append(getNumberFormat().format(value));
    	}
    	return result.toString();
    }

    /**
     * Satır türüne uygun olarak toplam tutarı döndürür. Örn. eğer
     * satır bir indirim satırı ise indirim tutarını, masraf ise masraf tutarını
     * döndürür.
     */
    public BigDecimal totalAmountRelatedToProductType() {
    	BigDecimal result = BigDecimal.ZERO;
    	if (getProductType().equals(ProductType.Expense) ||
    			   getProductType().equals(ProductType.ExpenseAddition) || 
    			   getProductType().equals(ProductType.DocumentExpense) ){
    		
    		result = getExpense().getValue();
    	} else if (getProductType().equals(ProductType.Discount) ||
 			   	   getProductType().equals(ProductType.DiscountAddition) || 
 			   	   getProductType().equals(ProductType.DocumentDiscount) || 
 			   	   getProductType().equals(ProductType.ContactDiscount)){

    		result = getDiscount().getValue();
    	}
    	result = result.setScale(2, RoundingMode.HALF_UP);
    	return result;
    }
    
	private NumberFormat getNumberFormat() {
        NumberFormat f = NumberFormat.getInstance();
        f.setMaximumFractionDigits(2);
        f.setMinimumFractionDigits(2);
        return f;
	}

	/**
	 * Satır masraflarının ve dökümandan gelen masrafların toplamınının 
	 * birim başına düşen değerini döndürür.
	 */
	public MoneySet getLineExpenseTotal() {
		return new MoneySet().add( getExpense().getAsMoney() )
							 .add( getInvoiceExpense() )
							 .divide( getQuantity().asBigDecimal(), getProduct().getUnitPriceScale().getScale());
	}
	
	/**
	 * Verginin ve masrafların dahil olduğu birim fiyat bilgisini tutar.
	 */
	public MoneySet getTaxIncludedUnitPrice() {
		return new MoneySet( getGrandTotal() ).divide(getQuantity().asBigDecimal(), 
												      getProduct().getUnitPriceScale().getScale());
	}

	/**
	 * İndirimlerin dahil olduğu birim fiyat bilgisini tutar.
	 */
	public MoneySet getDiscountIncludedUnitPrice() {
		return new MoneySet( getBeforeTax() ).divide(getQuantity().asBigDecimal(), 
				getProduct().getUnitPriceScale().getScale());
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
        if (!(object instanceof TekirInvoiceDetail)) {
            return false;
        }
        TekirInvoiceDetail other = (TekirInvoiceDetail)object;
        if (this.getId() != other.getId() && (this.getId() == null || !this.getId().equals(other.getId()))) return false;
        return true;
    }

    @Override
    public String toString() {
        return "com.ut.tekir.entities.TekirInvoiceDetail[id=" + getId() + "]";
    }
	
    @Override
	public List<TekirInvoiceDetail> getChildList() {
		removeDuplicateOfParent();
    	return childList;
	}

    private void removeDuplicateOfParent() {
		TekirInvoiceDetail item = null;
		for(int i=0;i<childList.size();i++) {
			item = childList.get(i);
			if (item.getId() != null && hasParentId(item) && 
					item.getId().equals(item.getParent().getId())) {
				childList.remove(i);
			}
		}
	}

	private boolean hasParentId(TekirInvoiceDetail item) {
		if (item.getParent() != null && item.getParent().getId() != null) return true;
		return false;
	}

	@Override
	public List<? extends TenderDetailBase> getDiscountAndExpenseList() {
		List<TekirInvoiceDetail> result = new ArrayList<TekirInvoiceDetail>();
		for (TekirInvoiceDetail item : getChildList()) {
			if (item.getProductType().equals(ProductType.Expense) || 
					item.getProductType().equals(ProductType.Discount)) {
				result.add(item);
			}
		}
		return result;
	}

	@Override
	public List<? extends TenderDetailBase> getDiscountList() {
		List<TekirInvoiceDetail> result = new ArrayList<TekirInvoiceDetail>();
		for (TekirInvoiceDetail item : getChildList()) {
			if (item.getProductType().equals(ProductType.Discount)) {
				result.add(item);
			}
		}
		return result;
	}

	@Override
	public List<? extends TenderDetailBase> getExpenseList() {
		List<TekirInvoiceDetail> result = new ArrayList<TekirInvoiceDetail>();
		for (TekirInvoiceDetail item :getChildList()) {
			if (item.getProductType().equals(ProductType.Expense)) {
				result.add(item);
			}
		}
		return result;
	}

	@Override
	public List<? extends TenderDetailBase> getFeeList() {
		List<TekirInvoiceDetail> result = new ArrayList<TekirInvoiceDetail>();
		for (TekirInvoiceDetail item :getChildList()) {
			if (item.getProductType().equals(ProductType.Fee)) {
				result.add(item);
			}
		}
		return result;
	}

	@Override
	public MoneySet getInvoiceDiscount() {
		if (invoiceDiscount == null) {
			invoiceDiscount = new MoneySet();
		}
		return invoiceDiscount;
	}

	@Override
	public MoneySet getInvoiceExpense() {
		if (invoiceExpense == null) {
			invoiceExpense = new MoneySet();
		}
		return invoiceExpense;
	}

	@Override
	public MoneySet getOrderDiscount() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MoneySet getOrderExpense() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MoneySet getShipmentDiscount() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MoneySet getShipmentExpense() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MoneySet getTenderDiscount() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MoneySet getTenderExpense() {
		// TODO Auto-generated method stub
		return null;
	}

	public TekirInvoiceDetail clone() {
		TekirInvoiceDetail clonedid = new TekirInvoiceDetail();
		clonedid.setAmount(getAmount());
		clonedid.setBarcode(getBarcode());
		clonedid.setBeforeTax(getBeforeTax());
		clonedid.setClerk(getClerk());
		clonedid.setDiscount(getDiscount());
		clonedid.setDiscountStyle(getDiscountStyle());
		clonedid.setExpense(getExpense());
		clonedid.setExpenseStyle(getExpenseStyle());
		clonedid.setFee(getFee());
		clonedid.setGrandTotal(getGrandTotal());
		clonedid.setInfo(getInfo());
		clonedid.setInvoiceDiscount(getInvoiceDiscount());
		clonedid.setInvoiceExpense(getInvoiceExpense());
		clonedid.setLineCode(getLineCode());
		clonedid.setProduct(getProduct());
		clonedid.setProductType(getProductType());
		clonedid.setQuantity(getQuantity());
		clonedid.setReferenceDocumentId(getReferenceDocumentId());
		clonedid.setReferenceDocumentType(getReferenceDocumentType());
		clonedid.setReturned(isReturned());
		clonedid.setTax1(getTax1());
		clonedid.setTax2(getTax2());
		clonedid.setTax3(getTax3());
		clonedid.setTax4(getTax4());
		clonedid.setTax5(getTax5());
		clonedid.setTaxExcludedAmount(getTaxExcludedAmount());
		clonedid.setTaxExcludedTotal(getTaxExcludedTotal());
		clonedid.setTaxExcludedUnitPrice(getTaxExcludedUnitPrice());
		clonedid.setTaxTotalAmount(getTaxTotalAmount());
		clonedid.setTotalAmount(getTotalAmount());
		clonedid.setUnitPrice(getUnitPrice());
		
		clonedid.setParent(parent);
//		clonedid.setChildList(childList);

		return clonedid;
	}
	
	public TekirInvoice getOwner() {
		return owner;
	}

	public void setOwner(TekirInvoice owner) {
		this.owner = owner;
	}

	public TekirInvoiceDetail getParent() {
		return parent;
	}

	public void setParent(TekirInvoiceDetail parent) {
		this.parent = parent;
	}

	public void setInvoiceDiscount(MoneySet invoiceDiscount) {
		this.invoiceDiscount = invoiceDiscount;
	}

	public void setInvoiceExpense(MoneySet invoiceExpense) {
		this.invoiceExpense = invoiceExpense;
	}

	public void setChildList(List<TekirInvoiceDetail> childList) {
		this.childList = childList;
	}

	public String getBarcode() {
		return barcode;
	}

	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}

	public User getClerk() {
		return clerk;
	}

	public void setClerk(User clerk) {
		this.clerk = clerk;
	}

	public boolean isReturned() {
		return returned;
	}

	public void setReturned(boolean returned) {
		this.returned = returned;
	}
	
}
