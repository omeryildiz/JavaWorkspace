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

package com.ut.tekir.entities.shp;

import java.math.BigDecimal;
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
@Table(name="TEKIR_SHIPMENT_NOTE_DETAIL")
public class TekirShipmentNoteDetail extends TenderDetailBase implements ITenderDetail {

	private static final long serialVersionUID = 1L;

    @ManyToOne
    @JoinColumn(name="OWNER_ID")
    private TekirShipmentNote owner;

    @ManyToOne
    @JoinColumn(name="PARENT_ID")
    private TekirShipmentNoteDetail parent;

    //FIXME:irsaliyede indirim söz konusu olmayabilir. shipmentDiscount ve shipmentExpense
    //alanlarını silmemiz gerekebilir.

    //İrsaliye üzerindeki döküman indirimlerinden gelen indirim tutarını tutar. 
    @Embedded
    @Valid
    @AttributeOverrides( {
        @AttributeOverride(name="currency", column=@Column(name="SHIPMENT_DISCOUNT_CCY")),
        @AttributeOverride(name="value",    column=@Column(name="SHIPMENT_DISCOUNT_VALUE")),
        @AttributeOverride(name="localAmount", column=@Column(name="SHIPMENT_DISCOUNT_LCYVAL"))
    })
    private MoneySet shipmentDiscount;
    
    //İrsaliye üzerindeki döküman indirimlerinden gelen masraf tutarını tutar.
    @Embedded
    @Valid
    @AttributeOverrides( {
    	@AttributeOverride(name="currency", column=@Column(name="SHIPMENT_EXPENSE_CCY")),
    	@AttributeOverride(name="value",    column=@Column(name="SHIPMENT_EXPENSE_VALUE")),
    	@AttributeOverride(name="localAmount", column=@Column(name="SHIPMENT_EXPENSE_LCYVAL"))
    })
    private MoneySet shipmentExpense;

    //Bu detaya bağlı masraf ve indirim satırlarını tutar.
    @OneToMany(mappedBy="parent",cascade=CascadeType.ALL)
    @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    private List<TekirShipmentNoteDetail> childList = new ArrayList<TekirShipmentNoteDetail>();

    //Fatura ekranlarında barkodla yapılan işlemleri kolaylaştırmak adına 
    //eklendi.
    @Transient
    private String barcode;

    /**
     * Tezgahtar bilgisini tutar.
     */
    @ManyToOne
    @JoinColumn(name="CLERK_ID")
    private User clerk;

    @Override
	public List<TekirShipmentNoteDetail> getChildList() {
		removeDuplicateOfParent();
		return childList;
	}

    private void removeDuplicateOfParent() {
		TekirShipmentNoteDetail item = null;
		for(int i=0;i<childList.size();i++) {
			item = childList.get(i);
			if (item.getId() != null && hasParentId(item) && 
					item.getId().equals(item.getParent().getId())) {
				childList.remove(i);
			}
		}
	}

    private boolean hasParentId(TekirShipmentNoteDetail item) {
		if (item.getParent() != null && item.getParent().getId() != null) return true;
		return false;
	}

	@Override
	public List<? extends TenderDetailBase> getDiscountAndExpenseList() {
		List<TekirShipmentNoteDetail> result = new ArrayList<TekirShipmentNoteDetail>();
		for (TekirShipmentNoteDetail item : getChildList()) {
			if (item.getProductType().equals(ProductType.Expense) || 
					item.getProductType().equals(ProductType.Discount)) {
				result.add(item);
			}
		}
		return result;
	}

	@Override
	public List<? extends TenderDetailBase> getDiscountList() {
		List<TekirShipmentNoteDetail> result = new ArrayList<TekirShipmentNoteDetail>();
		for (TekirShipmentNoteDetail item : getChildList()) {
			if (item.getProductType().equals(ProductType.Discount)) {
				result.add(item);
			}
		}
		return result;
	}

	@Override
	public List<? extends TenderDetailBase> getExpenseList() {
		List<TekirShipmentNoteDetail> result = new ArrayList<TekirShipmentNoteDetail>();
		for (TekirShipmentNoteDetail item :getChildList()) {
			if (item.getProductType().equals(ProductType.Expense)) {
				result.add(item);
			}
		}
		return result;
	}

	@Override
	public List<? extends TenderDetailBase> getFeeList() {
		List<TekirShipmentNoteDetail> result = new ArrayList<TekirShipmentNoteDetail>();
		for (TekirShipmentNoteDetail item :getChildList()) {
			if (item.getProductType().equals(ProductType.Fee)) {
				result.add(item);
			}
		}
		return result;
	}

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

	private NumberFormat getNumberFormat() {
        NumberFormat f = NumberFormat.getInstance();
        f.setMaximumFractionDigits(2);
        f.setMinimumFractionDigits(2);
        return f;
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
		if(shipmentDiscount == null) {
			shipmentDiscount = new MoneySet();
		}
		return shipmentDiscount;
	}

	@Override
	public MoneySet getShipmentExpense() {
		if (shipmentExpense == null) {
			shipmentExpense = new MoneySet();
		}
		return shipmentExpense;
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

	public TekirShipmentNote getOwner() {
		return owner;
	}

	public void setOwner(TekirShipmentNote owner) {
		this.owner = owner;
	}

	public TekirShipmentNoteDetail getParent() {
		return parent;
	}

	public void setParent(TekirShipmentNoteDetail parent) {
		this.parent = parent;
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

}
