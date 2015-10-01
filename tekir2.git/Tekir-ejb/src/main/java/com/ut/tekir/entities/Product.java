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
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;
import org.hibernate.validator.Valid;

/**
 * Entity class Product
 * 
 * @author haky
 */
@Entity
@Table(name="PRODUCT")
public class Product extends AuditBase implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE,generator="genericSeq")
    @Column(name="ID")
    private Long id;

    @Column(name="PRODUCT_TYPE")
    @Enumerated(EnumType.ORDINAL)
    private ProductType productType = ProductType.Product; 
    
    @Column(name="CODE", nullable=false, unique=true, length=20)
    @NotNull
    @Length(max=20, min=1)
    private String code;
    
    @Column(name="NAME", length=80)
    @Length(max=80)
    private String name;
    
    @Column(name="INFO")
    private String info;
    
    @Column(name="OPEN_DATE")
    @Temporal(TemporalType.DATE)
    private Date openDate = new Date();
    
    @ManyToOne
    @JoinColumn(name="PRODUCT_CATEGORY_ID")
    private ProductCategory category;
    
    @Column(name="SYSTEM")
    private Boolean system;
    
    @Column(name="ISACTIVE")
    private Boolean active = Boolean.TRUE;
    
    @Column(name="UNIT", length=10)
    @Length(max=10)
    private String unit;
        
    @Column(name="BARCODE1", length=80)
    @Length(max=80)
    private String barcode1;
    
    @Column(name="BARCODE2", length=80)
    @Length(max=80)
    private String barcode2;
    
    @Column(name="BARCODE3", length=80)
    @Length(max=80)
    private String barcode3;
    
    @Column(name="IMAGE")
    private String image;
    
    @ManyToOne
	@JoinColumn(name="ExpenseType_ID")
	private ExpenseType expenseType;

    // KDV - VAT
    @ManyToOne
    @JoinColumn(name="BUY_TAX_ID")
    private Tax buyTax;
    
    @ManyToOne
    @JoinColumn(name="SELL_TAX_ID")
    private Tax sellTax;
    
    @Column(name="TAX_INCLUDED")
    private Boolean taxIncluded = Boolean.TRUE;

    // OIV / OTV
    @ManyToOne
    @JoinColumn(name="BUY_TAX2_ID")
    private Tax buyTax2;

    @ManyToOne
    @JoinColumn(name="SELL_TAX2_ID")
    private Tax sellTax2;

    @Column(name="TAX2_INCLUDED")
    private Boolean tax2Included = Boolean.TRUE;
    
    @ManyToOne
    @JoinColumn(name="BUY_TAX3_ID")
    private Tax buyTax3;

    @ManyToOne
    @JoinColumn(name="SELL_TAX3_ID")
    private Tax sellTax3;

    @Column(name="TAX3_INCLUDED")
    private Boolean tax3Included = Boolean.TRUE;
    
    @ManyToOne
    @JoinColumn(name="BUY_TAX4_ID")
    private Tax buyTax4;

    @ManyToOne
    @JoinColumn(name="SELL_TAX4_ID")
    private Tax sellTax4;

    @Column(name="TAX4_INCLUDED")
    private Boolean tax4Included = Boolean.TRUE;
    
    @ManyToOne
    @JoinColumn(name="BUY_TAX5_ID")
    private Tax buyTax5;

    @ManyToOne
    @JoinColumn(name="SELL_TAX5_ID")
    private Tax sellTax5;

    @Column(name="TAX5_INCLUDED")
    private Boolean tax5Included = Boolean.TRUE;

    @OneToMany(mappedBy="product",cascade=CascadeType.ALL )
    @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    private List<ProductUnit> productUnitList= new ArrayList<ProductUnit>();

    @OneToMany(mappedBy="owner",cascade=CascadeType.ALL )
    @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    private List<ProductDetail> detailList= new ArrayList<ProductDetail>();
    
    @Column(name="SHELF_PLACE")
    private String shelfPlace;

    /**
     * Eğer ürün masraf veya indirim olarak işaretlenmişse indirim/masraf 
     * bilgileri tutar.
     */
    @Embedded
    @Valid
    @AttributeOverrides( {
    	@AttributeOverride(name="percentage", column=@Column(name="DISCOUNT_EXPENSE_PERCENTAGE")),
    	@AttributeOverride(name="rate", column=@Column(name="DISCOUNT_EXPENSE_RATE")),
        @AttributeOverride(name="currency", column=@Column(name="DISCOUNT_EXPENSE_CCY")),
        @AttributeOverride(name="value",    column=@Column(name="DISCOUNT_EXPENSE_VALUE")),
        @AttributeOverride(name="localAmount",    column=@Column(name="DISCOUNT_EXPENSE_LCYVAL"))
    })
	private DiscountOrExpense discountOrExpense;

    /**
     * Son alış fiyat bilgileridir.
     */
    @Embedded
    @Valid
    @AttributeOverrides( {
        @AttributeOverride(name="currency", column=@Column(name="LAST_PURCHASE_PRICE_CCY")),
        @AttributeOverride(name="value",    column=@Column(name="LAST_PURCHASE_PRICE_VALUE")),
        @AttributeOverride(name="localAmount",    column=@Column(name="LAST_PURCHASE_PRICE_LCYVAL"))
    })
    private MoneySet lastPurchasePrice = new UnitPriceMoneySet();

    /**
     * Son satış fiyat bilgileridir.
     */
    @Embedded
    @Valid
    @AttributeOverrides( {
    	@AttributeOverride(name="currency", column=@Column(name="LAST_SALE_PRICE_CCY")),
    	@AttributeOverride(name="value",    column=@Column(name="LAST_SALE_PRICE_VALUE")),
    	@AttributeOverride(name="localAmount",    column=@Column(name="LAST_SALE_PRICE_LCYVAL"))
    })
    private MoneySet lastSalePrice = new UnitPriceMoneySet();
    
    /**
     * Etiketin üzerine basılacak olan yazı bilgisini tutar.
     */
    @Column(name="LABEL_NAME")
    private String labelName;
    
    /**
     * Marka bilgisini tutar.
     */
    @ManyToOne
    @JoinColumn(name="GROUP_ID")
    private ProductGroup group;

    /**
     * Kurum bilgisini tutar.
     */
    @ManyToOne
    @JoinColumn(name="FOUNDATION_ID")
    @ForeignKey(name="FK_PRODUCT_FOUNDATIONID")
    private Foundation foundation;

    @ManyToOne
    @JoinColumn(name="PAYMENT_ACTION_TYPE_ID")
    @ForeignKey(name="FK_PRODUCT_PAYMENTACTIONTYPEID")
    private PaymentActionType paymentActionType;
    
    /**
     * Katkı masraf veya indirimlerinin yansıtılacağı hizmeti tutar.
     */
    @ManyToOne
    @JoinColumn(name="REFERENCE_PRODUCT_ID")
    @ForeignKey(name="FK_PRODUCT_REFERENCEPRODUCTID")
    private Product referenceProduct;
    
    public enum UnitPriceScale{ High(6), Low(2);
    	
    	private int scale;
    	UnitPriceScale(int scale) {
    		this.scale = scale;
    	}
    	
    	public int getScale() {
    		return scale;
    	}
    	
    	public static int defaultScale() {
    		return Low.scale;
    	}
    };
    
    /**
     * Ürün veya hizmetin birim fiyatının scale(virgülden sonrası) 
     * bilgisini tutar.
     */
    @Column(name="UNIT_PRICE_SCALE")
    @Enumerated(EnumType.ORDINAL)
    private UnitPriceScale unitPriceScale = UnitPriceScale.Low;

    public Tax getOTVTax() {
		try {
			Method taxGetter = null;
			Tax tax = null;

			for (int i = 1; i <= 5; i++) {
				taxGetter = getClass().getMethod("getSellTax" + i);
				tax = (Tax)taxGetter.invoke(this);

				if (tax != null && tax.getType().equals(TaxType.OTV)) {
					return tax;
				}
			}
		} catch (Exception e) {
			System.out.println("Hata" + e.getMessage());
		}
    	return null;
    }
    
	public boolean isHasLowerPriceThanCent() {
    	if (unitPriceScale.equals(UnitPriceScale.High)) return true;
    	return false;
	}

	public void setHasLowerPriceThanCent(boolean aValue) {
    	if (aValue) {
    		unitPriceScale = UnitPriceScale.High;
    	} else {
    		unitPriceScale = UnitPriceScale.Low;
    	}
	}

	/**
	 * vergileri cachelemek için kullanacak.
	 */
	@Transient
	private List<ProductTax> taxList;
	
    public List<ProductTax> taxesAsList() {
    	if (taxList == null) {
    		taxList = new ArrayList<ProductTax>();

			taxList.add(new ProductTax(getBuyTax1(),getTax1Included()));
			taxList.add(new ProductTax(getBuyTax2(),getTax2Included()));
			taxList.add(new ProductTax(getBuyTax3(),getTax3Included()));
			taxList.add(new ProductTax(getBuyTax4(),getTax4Included()));
			taxList.add(new ProductTax(getBuyTax5(),getTax5Included()));
    	}
    	return taxList;
    }

    public ProductTax getWitholdingTax() {
    	for (ProductTax productTax : taxesAsList()) {
    		if (productTax.getTax() != null && productTax.getTax().getType().equals(TaxType.STOPAJ)) return productTax;
    	}
    	return null;
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public List<ProductDetail> getDetailList() {
		return detailList;
	}

	public void setDetailList(List<ProductDetail> detailList) {
		this.detailList = detailList;
	}

	public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public ProductCategory getCategory() {
        return category;
    }

    public void setCategory(ProductCategory category) {
        this.category = category;
    }

    public Boolean getSystem() {
        return system;
    }

    public void setSystem(Boolean system) {
        this.system = system;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Tax getBuyTax() {
        return buyTax;
    }

    public Tax getBuyTax1() {
    	return buyTax;
    }
    
    public void setBuyTax(Tax buyTax) {
        this.buyTax = buyTax;
    }

    public Tax getSellTax() {
        return sellTax;
    }

    public Tax getSellTax1() {
    	return sellTax;
    }

    public void setSellTax(Tax sellTax) {
        this.sellTax = sellTax;
    }

    public String getBarcode1() {
        return barcode1;
    }

    public void setBarcode1(String barcode1) {
        this.barcode1 = barcode1;
    }

    public Boolean getTaxIncluded() {
        return taxIncluded;
    }

    public Boolean getTax1Included() {
    	return taxIncluded;
    }

    public void setTaxIncluded(Boolean taxIncluded) {
        this.taxIncluded = taxIncluded;
    }
    
    @Transient
    public String getCaption(){
        return "[" + getCode() + "] " + getName();
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
        if (!(object instanceof Product)) {
            return false;
        }
        Product other = (Product)object;
        if (this.getId() != other.getId() && (this.getId() == null || !this.id.equals(other.id))) return false;
        return true;
    }

    @Override
    public String toString() {
        return getName();
    }

	public DiscountOrExpense getDiscountOrExpense() {
		if(discountOrExpense == null){
			discountOrExpense = new DiscountOrExpense();
		}
		return discountOrExpense;
	}

	public void setDiscountOrExpense(DiscountOrExpense discountOrExpense) {
		this.discountOrExpense = discountOrExpense;
	}

	public ExpenseType getExpenseType() {
		return expenseType;
	}

	public void setExpenseType(ExpenseType expenseType) {
		this.expenseType = expenseType;
	}

    /**
     * @return the buyTax2
     */
    public Tax getBuyTax2() {
        return buyTax2;
    }

    /**
     * @param buyTax2 the buyTax2 to set
     */
    public void setBuyTax2(Tax buyTax2) {
        this.buyTax2 = buyTax2;
    }

    /**
     * @return the sellTax2
     */
    public Tax getSellTax2() {
        return sellTax2;
    }

    /**
     * @param sellTax2 the sellTax2 to set
     */
    public void setSellTax2(Tax sellTax2) {
        this.sellTax2 = sellTax2;
    }

    /**
     * @return the tax2Included
     */
    public Boolean getTax2Included() {
        return tax2Included;
    }

    /**
     * @param tax2Included the tax2Included to set
     */
    public void setTax2Included(Boolean tax2Included) {
        this.tax2Included = tax2Included;
    }

	public void setBuyTax3(Tax buyTax3) {
		this.buyTax3 = buyTax3;
	}

	public Tax getBuyTax3() {
		return buyTax3;
	}

	public void setSellTax3(Tax sellTax3) {
		this.sellTax3 = sellTax3;
	}

	public Tax getSellTax3() {
		return sellTax3;
	}

	public void setTax3Included(Boolean tax3Included) {
		this.tax3Included = tax3Included;
	}

	public Boolean getTax3Included() {
		return tax3Included;
	}

	public void setBuyTax4(Tax buyTax4) {
		this.buyTax4 = buyTax4;
	}

	public Tax getBuyTax4() {
		return buyTax4;
	}

	public void setSellTax4(Tax sellTax4) {
		this.sellTax4 = sellTax4;
	}

	public Tax getSellTax4() {
		return sellTax4;
	}

	public void setTax4Included(Boolean tax4Included) {
		this.tax4Included = tax4Included;
	}

	public Boolean getTax4Included() {
		return tax4Included;
	}

	public void setBuyTax5(Tax buyTax5) {
		this.buyTax5 = buyTax5;
	}

	public Tax getBuyTax5() {
		return buyTax5;
	}

	public void setSellTax5(Tax sellTax5) {
		this.sellTax5 = sellTax5;
	}

	public Tax getSellTax5() {
		return sellTax5;
	}

	public void setTax5Included(Boolean tax5Included) {
		this.tax5Included = tax5Included;
	}

	public Boolean getTax5Included() {
		return tax5Included;
	}

	public void setBarcode2(String barcode2) {
		this.barcode2 = barcode2;
	}

	public String getBarcode2() {
		return barcode2;
	}

	public void setBarcode3(String barcode3) {
		this.barcode3 = barcode3;
	}

	public String getBarcode3() {
		return barcode3;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getImage() {
		return image;
	}

	public void setProductUnitList(List<ProductUnit> productUnitList) {
		this.productUnitList = productUnitList;
	}

	public List<ProductUnit> getProductUnitList() {
		return productUnitList;
	}

	public void setShelfPlace(String shelfPlace) {
		this.shelfPlace = shelfPlace;
	}

	public String getShelfPlace() {
		return shelfPlace;
	}

	public ProductType getProductType() {
		return productType;
	}

	public void setProductType(ProductType productType) {
		this.productType = productType;
	}

	public void setOpenDate(Date openDate) {
		this.openDate = openDate;
	}

	public Date getOpenDate() {
		return openDate;
	}

	public MoneySet getLastPurchasePrice() {
		if (lastPurchasePrice == null) {
			lastPurchasePrice = new MoneySet();
		}
		return lastPurchasePrice;
	}

	public void setLastPurchasePrice(MoneySet lastPurchasePrice) {
		this.lastPurchasePrice = lastPurchasePrice;
	}

	public MoneySet getLastSalePrice() {
		if (lastSalePrice == null) {
			lastSalePrice = new MoneySet();
		}
		return lastSalePrice;
	}

	public void setLastSalePrice(MoneySet lastSalePrice) {
		this.lastSalePrice = lastSalePrice;
	}

	public String getLabelName() {
		return labelName;
	}

	public void setLabelName(String labelName) {
		this.labelName = labelName;
	}

	public ProductGroup getGroup() {
		return group;
	}

	public void setGroup(ProductGroup group) {
		this.group = group;
	}

    public BigDecimal getLastPurchasePriceValue(){
        return lastPurchasePrice.getValue();
    }

    public void setLastPurchasePriceValue(BigDecimal lastPurchasePriceValue){
        this.lastPurchasePrice.setValue(lastPurchasePriceValue);
    }
	public BigDecimal getLastSalePriceValue(){
        return lastSalePrice.getValue();
    }

    public void setLastSalePriceValue(BigDecimal lastSalePriceValue){
        this.lastSalePrice.setValue(lastSalePriceValue);
    }

	public Foundation getFoundation() {
		return foundation;
	}

	public void setFoundation(Foundation foundation) {
		this.foundation = foundation;
	}

	public PaymentActionType getPaymentActionType() {
		return paymentActionType;
	}

	public void setPaymentActionType(PaymentActionType paymentActionType) {
		this.paymentActionType = paymentActionType;
	}

	public Product getReferenceProduct() {
		return referenceProduct;
	}

	public void setReferenceProduct(Product referenceProduct) {
		this.referenceProduct = referenceProduct;
	}

	public UnitPriceScale getUnitPriceScale() {
		return unitPriceScale;
	}

	public void setUnitPriceScale(UnitPriceScale unitPriceScale) {
		this.unitPriceScale = unitPriceScale;
	}

}
