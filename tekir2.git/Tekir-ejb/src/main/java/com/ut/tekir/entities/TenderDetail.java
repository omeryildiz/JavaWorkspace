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

import org.hibernate.annotations.Cascade;
import org.hibernate.validator.Valid;

/**
 * @author sinan.yumak
 *
 */
@Entity
@Table(name="TENDER_DETAIL")
public class TenderDetail extends TenderDetailBase implements ITenderDetail {

	private static final long serialVersionUID = 1L;

    @ManyToOne
    @JoinColumn(name="OWNER_ID")
    private Tender owner;

    @ManyToOne
    @JoinColumn(name="PARENT_ID")
    private TenderDetail parent;
    
    //Teklif üzerindeki döküman indirimlerinden gelen indirim tutarını tutar. 
    @Embedded
    @Valid
    @AttributeOverrides( {
        @AttributeOverride(name="currency", column=@Column(name="TENDER_DISCOUNT_CCY")),
        @AttributeOverride(name="value",    column=@Column(name="TENDER_DISCOUNT_VALUE")),
        @AttributeOverride(name="localAmount", column=@Column(name="TENDER_DISCOUNT_LCYVAL"))
    })
    private MoneySet tenderDiscount;

    //Teklif üzerindeki döküman indirimlerinden gelen masraf tutarını tutar.
    @Embedded
    @Valid
    @AttributeOverrides( {
    	@AttributeOverride(name="currency", column=@Column(name="TENDER_EXPENSE_CCY")),
    	@AttributeOverride(name="value",    column=@Column(name="TENDER_EXPENSE_VALUE")),
    	@AttributeOverride(name="localAmount", column=@Column(name="TENDER_EXPENSE_LCYVAL"))
    })
    private MoneySet tenderExpense;

    //Bu detaya bağlı masraf ve indirim satırlarını tutar.
    @OneToMany(mappedBy="parent",cascade=CascadeType.ALL)
    @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    private List<TenderDetail> childList = new ArrayList<TenderDetail>();
    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (this.getId() != null ? this.getId().hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof TenderDetail)) {
            return false;
        }
        TenderDetail other = (TenderDetail)object;
        if (this.getId() != other.getId() && (this.getId() == null || !this.getId().equals(other.getId()))) return false;
        return true;
    }

    @Override
    public String toString() {
        return "com.ut.tekir.entities.TenderDetail[id=" + getId() + "]";
    }
	
    public boolean hasChildren() {
    	if (childList != null && childList.size() != 0) return true;
    	return false;
    }

    public boolean hasParent() {
    	if (parent != null) return true;
    	return false;
    }

    public void clearChildList() {
    	if (hasChildren()) childList.clear();
    }

    public Tender getOwner() {
		return owner;
	}

	public void setOwner(Tender owner) {
		this.owner = owner;
	}

	public void setTenderDiscount(MoneySet tenderDiscount) {
		this.tenderDiscount = tenderDiscount;
	}

	public void setTenderExpense(MoneySet tenderExpense) {
		this.tenderExpense = tenderExpense;
	}
	
	public TenderDetail getParent() {
		return parent;
	}

	public void setParent(TenderDetail parent) {
		this.parent = parent;
	}
	
	@Override
	public List<TenderDetail> getChildList() {
		removeDuplicateOfParent();
		return childList;
	}

	public void setChildList(List<TenderDetail> childList) {
		this.childList = childList;
	}

	private void removeDuplicateOfParent() {
		TenderDetail item = null;
		for(int i=0;i<childList.size();i++) {
			item = childList.get(i);
			if (item.getId() != null && hasParentId(item) && 
					item.getId().equals(item.getParent().getId())) {
				childList.remove(i);
			}
		}
	}

	private boolean hasParentId(TenderDetail item) {
		if (item.getParent() != null && item.getParent().getId() != null) return true;
		return false;
	}
	
	public List<TenderDetail> getExpenseList() {
		List<TenderDetail> result = new ArrayList<TenderDetail>();
		for (TenderDetail item :getChildList()) {
			if (item.getProductType().equals(ProductType.Expense)) {
				result.add(item);
			}
		}
		return result;
	}

	public List<TenderDetail> getFeeList() {
		List<TenderDetail> result = new ArrayList<TenderDetail>();
		for (TenderDetail item :getChildList()) {
			if (item.getProductType().equals(ProductType.Fee)) {
				result.add(item);
			}
		}
		return result;
	}

	public List<TenderDetail> getDiscountAndExpenseList() {
		List<TenderDetail> result = new ArrayList<TenderDetail>();
		for (TenderDetail item : getChildList()) {
			if (item.getProductType().equals(ProductType.Expense) || 
					item.getProductType().equals(ProductType.Discount)) {
				result.add(item);
			}
		}
		return result;
	}
	
	public List<TenderDetail> getDiscountList() {
		List<TenderDetail> result = new ArrayList<TenderDetail>();
		for (TenderDetail item : getChildList()) {
			if (item.getProductType().equals(ProductType.Discount)) {
				result.add(item);
			}
		}
		return result;
	}
	
	@Override
	public MoneySet getTenderExpense() {
		if (tenderExpense == null) {
			tenderExpense = new MoneySet();
		}
		return tenderExpense;
	}
	
	@Override
	public MoneySet getTenderDiscount() {
		if (tenderDiscount == null) {
			tenderDiscount = new MoneySet();
		}
		return tenderDiscount;
	}

	@Override
	public MoneySet getOrderDiscount() {
		return null;
	}

	@Override
	public MoneySet getOrderExpense() {
		return null;
	}

	@Override
	public MoneySet getShipmentDiscount() {
		return null;
	}

	@Override
	public MoneySet getShipmentExpense() {
		return null;
	}

	@Override
	public MoneySet getInvoiceDiscount() {
		return null;
	}
	
	@Override
	public MoneySet getInvoiceExpense() {
		return null;
	}

}
