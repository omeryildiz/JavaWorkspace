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

/**
 * @author sinan.yumak
 *
 */
@Entity
@Table(name="TEKIR_ORDER_NOTE_DETAIL")
public class TekirOrderNoteDetail extends TenderDetailBase implements ITenderDetail {

	private static final long serialVersionUID = 1L;

    @ManyToOne
    @JoinColumn(name="OWNER_ID")
    private TekirOrderNote owner;

    @ManyToOne
    @JoinColumn(name="PARENT_ID")
    private TekirOrderNoteDetail parent;

    //Sipariş üzerindeki döküman indirimlerinden gelen indirim tutarını tutar. 
    @Embedded
    @Valid
    @AttributeOverrides( {
        @AttributeOverride(name="currency", column=@Column(name="ORDER_NOTE_DISCOUNT_CCY")),
        @AttributeOverride(name="value",    column=@Column(name="ORDER_NOTE_DISCOUNT_VALUE")),
        @AttributeOverride(name="localAmount", column=@Column(name="ORDER_NOTE_DISCOUNT_LCYVAL"))
    })
    private MoneySet orderDiscount;
    
    //Sipariş üzerindeki döküman indirimlerinden gelen masraf tutarını tutar.
    @Embedded
    @Valid
    @AttributeOverrides( {
    	@AttributeOverride(name="currency", column=@Column(name="ORDER_NOTE_EXPENSE_CCY")),
    	@AttributeOverride(name="value",    column=@Column(name="ORDER_NOTE_EXPENSE_VALUE")),
    	@AttributeOverride(name="localAmount", column=@Column(name="ORDER_NOTE_EXPENSE_LCYVAL"))
    })
    private MoneySet orderExpense;

    /**
     * Satırın fatura edilen miktarını tutar.
     */
    @Column(name="CLOSED_QUANTITY", precision=5, scale=2)
    private Double closedQuantity = 0d;
    
    /**
     * Bu detaya bağlı masraf ve indirim satırlarını tutar.
     */
    @OneToMany(mappedBy="parent",cascade=CascadeType.ALL)
    @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    private List<TekirOrderNoteDetail> childList = new ArrayList<TekirOrderNoteDetail>();

    /**
     * Fatura ekranlarında barkodla yapılan işlemleri kolaylaştırmak adına 
     */
    @Transient
    private String barcode;

    /**
     * Bu satırın toplam miktarının çeşitli faturalarda kullanılarak bitirilip bitirilmediği
     * bilgisini tutar.
     */
    @Column(name="CLOSED")
    private boolean closed = false;
    
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
    	
    	} else if (getProductType().equals(ProductType.DocumentDiscount)||
    			getProductType().equals(ProductType.ContactDiscount)) {
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

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (this.getId() != null ? this.getId().hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof TekirOrderNoteDetail)) {
            return false;
        }
        TekirOrderNoteDetail other = (TekirOrderNoteDetail)object;
        if (this.getId() != other.getId() && (this.getId() == null || !this.getId().equals(other.getId()))) return false;
        return true;
    }

    @Override
    public String toString() {
        return "com.ut.tekir.entities.TekirOrderNoteDetail[id=" + getId() + "]";
    }

	@Override
	public List<TekirOrderNoteDetail> getChildList() {
		removeDuplicateOfParent();
		return childList;
	}

    private void removeDuplicateOfParent() {
		TekirOrderNoteDetail item = null;
		for(int i=0;i<childList.size();i++) {
			item = childList.get(i);
			if (item.getId() != null && hasParentId(item) && 
					item.getId().equals(item.getParent().getId())) {
				childList.remove(i);
			}
		}
	}

	private boolean hasParentId(TekirOrderNoteDetail item) {
		if (item.getParent() != null && item.getParent().getId() != null) return true;
		return false;
	}

	@Override
	public List<? extends TenderDetailBase> getDiscountAndExpenseList() {
		List<TekirOrderNoteDetail> result = new ArrayList<TekirOrderNoteDetail>();
		for (TekirOrderNoteDetail item : getChildList()) {
			if (item.getProductType().equals(ProductType.Expense) || 
					item.getProductType().equals(ProductType.Discount)) {
				result.add(item);
			}
		}
		return result;
	}

	@Override
	public List<? extends TenderDetailBase> getDiscountList() {
		List<TekirOrderNoteDetail> result = new ArrayList<TekirOrderNoteDetail>();
		for (TekirOrderNoteDetail item : getChildList()) {
			if (item.getProductType().equals(ProductType.Discount)) {
				result.add(item);
			}
		}
		return result;
	}

	@Override
	public List<? extends TenderDetailBase> getExpenseList() {
		List<TekirOrderNoteDetail> result = new ArrayList<TekirOrderNoteDetail>();
		for (TekirOrderNoteDetail item :getChildList()) {
			if (item.getProductType().equals(ProductType.Expense)) {
				result.add(item);
			}
		}
		return result;
	}

	@Override
	public List<? extends TenderDetailBase> getFeeList() {
		List<TekirOrderNoteDetail> result = new ArrayList<TekirOrderNoteDetail>();
		for (TekirOrderNoteDetail item :getChildList()) {
			if (item.getProductType().equals(ProductType.Fee)) {
				result.add(item);
			}
		}
		return result;
	}

	@Override
	public MoneySet getOrderDiscount() {
		if (orderDiscount == null) {
			orderDiscount = new MoneySet();
		}
		return orderDiscount;
	}

	@Override
	public MoneySet getOrderExpense() {
		if (orderExpense == null) {
			orderExpense = new MoneySet();
		}
		return orderExpense;
	}

	@Override
	public MoneySet getInvoiceDiscount() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MoneySet getInvoiceExpense() {
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

	public TekirOrderNote getOwner() {
		return owner;
	}

	public void setOwner(TekirOrderNote owner) {
		this.owner = owner;
	}

	public TekirOrderNoteDetail getParent() {
		return parent;
	}

	public void setParent(TekirOrderNoteDetail parent) {
		this.parent = parent;
	}

	public void setOrderDiscount(MoneySet orderDiscount) {
		this.orderDiscount = orderDiscount;
	}

	public void setOrderExpense(MoneySet orderExpense) {
		this.orderExpense = orderExpense;
	}

	public void setChildList(List<TekirOrderNoteDetail> childList) {
		this.childList = childList;
	}

	public Double getClosedQuantity() {
		return closedQuantity;
	}

	public void setClosedQuantity(Double closedQuantity) {
		this.closedQuantity = closedQuantity;
	}

	public String getBarcode() {
		return barcode;
	}

	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}

	public boolean isClosed() {
		return closed;
	}

	public void setClosed(boolean closed) {
		this.closed = closed;
	}

}
