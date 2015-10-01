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
import java.util.List;

import javax.persistence.AssociationOverride;
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
import javax.persistence.Transient;

import org.hibernate.validator.Length;
import org.hibernate.validator.Valid;

import com.ut.tekir.entities.Product.UnitPriceScale;

/**
 * Teklif, sipariş, irsaliye ve fatura detay satırları için 
 * ortak bilgileri tutar.
 * 
 * @author sinan.yumak
 *
 */
@MappedSuperclass
public class TenderDetailBase implements Serializable {

	private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE,generator="genericSeq")
    @Column(name="ID")
    private Long id;

    @Column(name="LINE_CODE", length=10)
    @Length(max=10)
    private String lineCode;
    
    @Column(name="INFO")
    private String info;
	
    //Hizmet veya ürün
    @ManyToOne
    @JoinColumn(name="PRODUCT_ID")
    private Product product;

    @Column(name="PRODUCT_TYPE")
    @Enumerated(EnumType.ORDINAL)
    private ProductType productType = ProductType.Unknown;
    
    //Miktar
    @Embedded
    @Valid
    private Quantity quantity = new Quantity();

    //Yabancı para ile birim fiyat
    @Embedded
    @Valid
    @AttributeOverrides( {
        @AttributeOverride(name="currency", column=@Column(name="FOREIGN_PRICE_CCY")),
        @AttributeOverride(name="value",    column=@Column(name="FOREIGN_PRICE_VALUE")),
        @AttributeOverride(name="localAmount", column=@Column(name="FOREIGN_PRICE_LCYVAL"))
    })
    private MoneySet foreignUnitPrice = new UnitPriceMoneySet();

    //Birim fiyat
    @Embedded
    @Valid
    @AttributeOverrides( {
        @AttributeOverride(name="currency", column=@Column(name="PRICE_CCY")),
        @AttributeOverride(name="value",    column=@Column(name="PRICE_VALUE")),
        @AttributeOverride(name="localAmount", column=@Column(name="PRICE_LCYVAL"))
    })
    private MoneySet unitPrice = new UnitPriceMoneySet();

    //Eğer vergiler dahil olarak işaretlenmiş ise, vergilerin
    //düşülmüş olduğu birim fiyat.
    @Embedded
    @Valid
    @AttributeOverrides( {
        @AttributeOverride(name="currency", column=@Column(name="TAX_EXCLUDED_PRICE_CCY")),
        @AttributeOverride(name="value",    column=@Column(name="TAX_EXCLUDED_PRICE_VALUE")),
        @AttributeOverride(name="localAmount", column=@Column(name="TAX_EXCLUDED_PRICE_LCYVAL"))
    })
    private MoneySet taxExcludedUnitPrice = new UnitPriceMoneySet();
    
    //Miktar x Birim Fiyat değeri
    //Bu alan vergilerin dahil olduğu birim fiyat ve miktarın çarpımını
    //tutar.
    @Embedded
    @Valid
    @AttributeOverrides( {
        @AttributeOverride(name="currency", column=@Column(name="AMOUNT_CCY")),
        @AttributeOverride(name="value",    column=@Column(name="AMOUNT_VALUE")),
        @AttributeOverride(name="localAmount",    column=@Column(name="AMOUNT_LCYVAL"))
    })
    private MoneySet amount = new MoneySet();
    
    //Eğer vergiler dahil olarak işaretlenmiş ise, vergilerin
    //düşülmüş olduğu tutar.
    @Embedded
    @Valid
    @AttributeOverrides( {
        @AttributeOverride(name="currency", column=@Column(name="TAX_EXCLUDED_AMOUNT_CCY")),
        @AttributeOverride(name="value",    column=@Column(name="TAX_EXCLUDED_AMOUNT_VALUE")),
        @AttributeOverride(name="localAmount", column=@Column(name="TAX_EXCLUDED_AMOUNT_LCYVAL"))
    })
    private MoneySet taxExcludedAmount = new MoneySet();

    //(OTV matrahı) Vergiden önce, satır indirimi uygulanmış
    @Embedded
    @Valid
    @AttributeOverrides( {
        @AttributeOverride(name="currency", column=@Column(name="BEFORE_TAX_CCY")),
        @AttributeOverride(name="value",    column=@Column(name="BEFORE_TAX_VALUE")),
        @AttributeOverride(name="localAmount",    column=@Column(name="BEFORE_TAX_LCYVAL"))
    })
    private MoneySet beforeTax = new MoneySet();

    //(Vergi Matrahı) Hem vergilerin hem de indirimlerin
    //(Satır, irsaliye ve fatura) düşüldüğü tutardır.
    //OTV varsa dahil olacak. Yoksa beforeTax ile aynı olmalı.
    @Embedded
    @Valid
    @AttributeOverrides( {
        @AttributeOverride(name="currency", column=@Column(name="TAX_EXCLUDED_TOTAL_CCY")),
        @AttributeOverride(name="value",    column=@Column(name="TAX_EXCLUDED_TOTAL_VALUE")),
        @AttributeOverride(name="localAmount",    column=@Column(name="TAX_EXCLUDED_TOTAL_LCYVAL"))
    })
    private MoneySet taxExcludedTotal = new MoneySet();
    
    //Satırın tüm vergilerinin toplamını tutar.
    @Embedded
    @Valid
    @AttributeOverrides( {
    	@AttributeOverride(name="currency", column=@Column(name="TAX_TOTAL_AMOUNT_CCY")),
    	@AttributeOverride(name="value",    column=@Column(name="TAX_TOTAL_AMOUNT_VALUE")),
    	@AttributeOverride(name="localAmount",    column=@Column(name="TAX_TOTAL_AMOUNT_LCYVAL"))
    })
    private MoneySet taxTotalAmount = new MoneySet();

    //harçların olmadığı satır toplamı.
    @Embedded
    @Valid
    @AttributeOverrides( {
        @AttributeOverride(name="currency", column=@Column(name="TOTAL_AMOUNT_CCY")),
        @AttributeOverride(name="value",    column=@Column(name="TOTAL_AMOUNT_VALUE")),
        @AttributeOverride(name="localAmount",    column=@Column(name="TOTAL_AMOUNT_LCYVAL"))
    })
    private MoneySet totalAmount = new MoneySet();

    /*
     * Satır masraf, indirim ve harçları....
     */

    /**
     * Eğer yapılan indirim dökümana yapılan bir indirimse, indirim tarzını
     * tutar...
     */
    @Column(name="DISCOUNT_STYLE")
    @Enumerated(EnumType.ORDINAL)
    private DistributionStyle discountStyle = DistributionStyle.Equally; 

    //satırlardaki masraflar için kullanılacak.
    @Embedded
    @Valid
    @AttributeOverrides( {
    	@AttributeOverride(name="percentage", column=@Column(name="EXPENSE_PERCENTAGE")),
    	@AttributeOverride(name="rate", column=@Column(name="EXPENSE_RATE")),
        @AttributeOverride(name="currency", column=@Column(name="EXPENSE_CCY")),
        @AttributeOverride(name="value",    column=@Column(name="EXPENSE_VALUE")),
        @AttributeOverride(name="localAmount",    column=@Column(name="EXPENSE_LCYVAL"))
    })
    private DiscountOrExpense expense;

    //Eğer yapılan masraf dökümana yapılan bir masrafsa, masraf tarzını
    //tutar...
    @Column(name="EXPENSE_STYLE")
    @Enumerated(EnumType.ORDINAL)
    private DistributionStyle expenseStyle = DistributionStyle.Equally; 

    //satırlardaki indirimler için kullanılacak.
    @Embedded
    @Valid
    @AttributeOverrides( {
    	@AttributeOverride(name="percentage", column=@Column(name="DISCOUNT_PERCENTAGE")),
    	@AttributeOverride(name="rate", column=@Column(name="DISCOUNT_RATE")),
        @AttributeOverride(name="currency", column=@Column(name="DISCOUNT_CCY")),
        @AttributeOverride(name="value",    column=@Column(name="DISCOUNT_VALUE")),
        @AttributeOverride(name="localAmount",    column=@Column(name="DISCOUNT_LCYVAL"))
    })
    private DiscountOrExpense discount;

    //satır üzerindeki harcı tutar. 
    @Embedded
    @Valid
    @AttributeOverrides( {
    	@AttributeOverride(name="percentage", column=@Column(name="FEE_PERCENTAGE")),
    	@AttributeOverride(name="rate", column=@Column(name="FEE_RATE")),
        @AttributeOverride(name="currency", column=@Column(name="FEE_CCY")),
        @AttributeOverride(name="value",    column=@Column(name="FEE_VALUE")),
        @AttributeOverride(name="localAmount",    column=@Column(name="FEE_LCYVAL"))
    })
    private DiscountOrExpense fee = new DiscountOrExpense();
    
    /*
      Buradaki bilgiler söz konusu belge kesildikten 
      sonra eğer tekrar hesaplama yapmak gerekirse kullanılacaklar.
     */

    //Vergi bilgileri
    @Embedded
    @Valid
    @AttributeOverrides( {
    	@AttributeOverride(name="kind",  column=@Column(name="TAX1_KIND")),
    	@AttributeOverride(name="taxIncluded", column=@Column(name="TAX1_TAX_INCLUDED")),
    	@AttributeOverride(name="rate",        column=@Column(name="TAX1_RATE")),
    	@AttributeOverride(name="type",        column=@Column(name="TAX1_TYPE")),
        @AttributeOverride(name="currency",    column=@Column(name="TAX1_CCY")),
        @AttributeOverride(name="value",       column=@Column(name="TAX1_VALUE")),
        @AttributeOverride(name="localAmount", column=@Column(name="TAX1_LCYVAL")),
        @AttributeOverride(name="withholdingKind", column=@Column(name="TAX1_WITHHOLDING_KIND")),
        @AttributeOverride(name="withholdingRate", column=@Column(name="TAX1_WITHHOLDING_RATE")),
        @AttributeOverride(name="withholdingAmount", column=@Column(name="TAX1_WITHHOLDING_AMOUNT"))
    })
	@AssociationOverride(name="tax", joinColumns=@JoinColumn(name="TAX1_ID"))
    private TaxEmbeddable tax1;

    @Embedded
    @Valid
    @AttributeOverrides( {
    	@AttributeOverride(name="kind",  column=@Column(name="TAX2_KIND")),
    	@AttributeOverride(name="taxIncluded", column=@Column(name="TAX2_TAX_INCLUDED")),
    	@AttributeOverride(name="rate",        column=@Column(name="TAX2_RATE")),
    	@AttributeOverride(name="type",        column=@Column(name="TAX2_TYPE")),
    	@AttributeOverride(name="currency",    column=@Column(name="TAX2_CCY")),
    	@AttributeOverride(name="value",       column=@Column(name="TAX2_VALUE")),
    	@AttributeOverride(name="localAmount", column=@Column(name="TAX2_LCYVAL")),
        @AttributeOverride(name="withholdingKind", column=@Column(name="TAX2_WITHHOLDING_KIND")),
        @AttributeOverride(name="withholdingRate", column=@Column(name="TAX2_WITHHOLDING_RATE")),
        @AttributeOverride(name="withholdingAmount", column=@Column(name="TAX2_WITHHOLDING_AMOUNT"))
    })
	@AssociationOverride(name="tax", joinColumns=@JoinColumn(name="TAX2_ID"))
    private TaxEmbeddable tax2;
    
    @Embedded
    @Valid
    @AttributeOverrides( {
    	@AttributeOverride(name="kind",  column=@Column(name="TAX3_KIND")),
    	@AttributeOverride(name="taxIncluded", column=@Column(name="TAX3_TAX_INCLUDED")),
    	@AttributeOverride(name="rate",        column=@Column(name="TAX3_RATE")),
    	@AttributeOverride(name="type",        column=@Column(name="TAX3_TYPE")),
    	@AttributeOverride(name="currency",    column=@Column(name="TAX3_CCY")),
    	@AttributeOverride(name="value",       column=@Column(name="TAX3_VALUE")),
    	@AttributeOverride(name="localAmount", column=@Column(name="TAX3_LCYVAL")),
        @AttributeOverride(name="withholdingKind", column=@Column(name="TAX3_WITHHOLDING_KIND")),
        @AttributeOverride(name="withholdingRate", column=@Column(name="TAX3_WITHHOLDING_RATE")),
        @AttributeOverride(name="withholdingAmount", column=@Column(name="TAX3_WITHHOLDING_AMOUNT"))
    })
	@AssociationOverride(name="tax", joinColumns=@JoinColumn(name="TAX3_ID"))
    private TaxEmbeddable tax3;
    
    @Embedded
    @Valid
    @AttributeOverrides( {
    	@AttributeOverride(name="kind",  column=@Column(name="TAX4_KIND")),
    	@AttributeOverride(name="taxIncluded", column=@Column(name="TAX4_TAX_INCLUDED")),
    	@AttributeOverride(name="rate",        column=@Column(name="TAX4_RATE")),
    	@AttributeOverride(name="type",        column=@Column(name="TAX4_TYPE")),
    	@AttributeOverride(name="currency",    column=@Column(name="TAX4_CCY")),
    	@AttributeOverride(name="value",       column=@Column(name="TAX4_VALUE")),
    	@AttributeOverride(name="localAmount", column=@Column(name="TAX4_LCYVAL")),
        @AttributeOverride(name="withholdingKind", column=@Column(name="TAX4_WITHHOLDING_KIND")),
        @AttributeOverride(name="withholdingRate", column=@Column(name="TAX4_WITHHOLDING_RATE")),
        @AttributeOverride(name="withholdingAmount", column=@Column(name="TAX4_WITHHOLDING_AMOUNT"))
    })
	@AssociationOverride(name="tax", joinColumns=@JoinColumn(name="TAX4_ID"))
    private TaxEmbeddable tax4;
    
    @Embedded
    @Valid
    @AttributeOverrides( {
    	@AttributeOverride(name="kind",  column=@Column(name="TAX5_KIND")),
    	@AttributeOverride(name="taxIncluded", column=@Column(name="TAX5_TAX_INCLUDED")),
    	@AttributeOverride(name="rate",        column=@Column(name="TAX5_RATE")),
    	@AttributeOverride(name="type",        column=@Column(name="TAX5_TYPE")),
    	@AttributeOverride(name="currency",    column=@Column(name="TAX5_CCY")),
    	@AttributeOverride(name="value",       column=@Column(name="TAX5_VALUE")),
    	@AttributeOverride(name="localAmount", column=@Column(name="TAX5_LCYVAL")),
        @AttributeOverride(name="withholdingKind", column=@Column(name="TAX5_WITHHOLDING_KIND")),
        @AttributeOverride(name="withholdingRate", column=@Column(name="TAX5_WITHHOLDING_RATE")),
        @AttributeOverride(name="withholdingAmount", column=@Column(name="TAX5_WITHHOLDING_AMOUNT"))
    })
	@AssociationOverride(name="tax", joinColumns=@JoinColumn(name="TAX5_ID"))
    private TaxEmbeddable tax5;
    
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
     * bu satırın hangi belge detayı aracılığıyla eklendiği bilgisini tutar.
     */
    @Column(name="REFERENCE_ID")
    private Long referenceDocumentId;

    @Column(name="REFERENCE_DOCUMENT_TYPE_ID")
    @Enumerated(EnumType.ORDINAL)
    private DocumentType referenceDocumentType;
    
    @Transient
    private MoneySet totalAmountTransient = new MoneySet();

    public String getCurrencyOfItem() {
    	if (getTaxExcludedAmount() != null) {
    		return getTaxExcludedAmount().getCurrency();
    	}
    	return null;
    }
    
    public String getLineCode() {
		return lineCode;
	}

	public void setLineCode(String lineCode) {
		this.lineCode = lineCode;
	}

	public List<TaxEmbeddable> getTaxList() {
		List<TaxEmbeddable> taxList = new ArrayList<TaxEmbeddable>();
		taxList.add(getTax1());
		taxList.add(getTax2());
		taxList.add(getTax3());
		taxList.add(getTax4());
		taxList.add(getTax5());
		
		return taxList;
	}

	public Tax getOTVTax() {
		if (getOTVTaxEmbeddable() != null && getOTVTaxEmbeddable().getTax() != null) return getOTVTaxEmbeddable().getTax();
		return getProduct() != null ? getProduct().getOTVTax() : null;
	}
	
	public TaxEmbeddable getOTVTaxEmbeddable() {
		Method taxGetter = null;
		TaxEmbeddable tax = null;
		try {
			for (int i=1;i<=5;i++) {
				taxGetter = this.getClass().getMethod("getTax" + i);
				tax = (TaxEmbeddable)taxGetter.invoke(this);
				
				if (tax != null && tax.getType() != null && tax.getType().equals(TaxType.OTV)) {
					return tax;
				}
			}
		} catch (Exception e) {
			System.out.println("Hata: "+ e.getMessage());
		}
		return null;
	}

	public List<TaxEmbeddable> getOtherTaxList() {
		List<TaxEmbeddable> taxList = new ArrayList<TaxEmbeddable>();
		
		Method taxGetter = null;
		TaxEmbeddable tax = null;
		try {
			for (int i=1;i<=5;i++) {
				taxGetter = this.getClass().getMethod("getTax" + i);
				tax = (TaxEmbeddable)taxGetter.invoke(this);
				
				if (tax != null && tax.getType() != null && !tax.getType().equals(TaxType.OTV)) {
					taxList.add(tax);
				}
			}
		} catch (Exception e) {
			System.out.println("Hata: "+ e.getMessage());
		}
		return taxList;
	}

	public Boolean isProductOrFeeLine() {
		return isTypeOf( ProductType.Product, ProductType.Fee);
	}

	public Boolean isProductOrServiceOrUnknown() {
		return isTypeOf( ProductType.Product, ProductType.Service, ProductType.Unknown);
	}
	
	public Boolean isProductOrExpenseOrDocExpense() {
		return isTypeOf( ProductType.Product, ProductType.DocumentExpense, ProductType.Expense);
	}
	
	public Boolean isProductOrDiscountOrDocDiscount() {
		return isTypeOf( ProductType.Product, ProductType.DocumentDiscount, ProductType.Discount);
	}
	
	public Boolean isDiscountOrDocumentDiscountOrContactDiscount() {
		return isTypeOf( ProductType.Discount, ProductType.DocumentDiscount, ProductType.ContactDiscount);
	}

	public Boolean isDocumentLine() {
		return isTypeOf( ProductType.DocumentDiscount, ProductType.DocumentExpense, ProductType.DocumentFee);
	}
	
	public int getUnitPriceScale() {
		return product != null ? product.getUnitPriceScale().getScale() : UnitPriceScale.defaultScale();
	}

	public boolean isTypeOf(ProductType... types) {
		for (ProductType aType : types) {
			if (aType.equals(productType)) return true;
		}
		return false;
	}
		
	/**
	 * Satırın amount alanını hesaplar.
	 */
	public void amountCalculation() {
		BigDecimal quantityValue = quantity.asBigDecimal();
		BigDecimal unitPrice = getUnitPrice().getValue();
		BigDecimal calculatedAmount = unitPrice.multiply(quantityValue);
	
		MoneySet amount = new MoneySet();
		amount.setCurrency( getCurrencyOfItem() );
		amount.setValue( calculatedAmount );

		setAmount(amount);
	}
	
	public TaxEmbeddable getTax1() {
		if (tax1 == null) {
			tax1 = new TaxEmbeddable();
		}
		return tax1;
	}

	public void setTax1(TaxEmbeddable tax1) {
		this.tax1 = tax1;
	}

	public TaxEmbeddable getTax2() {
		if (tax2 == null) {
			tax2 = new TaxEmbeddable();
		}
		return tax2;
	}

	public void setTax2(TaxEmbeddable tax2) {
		this.tax2 = tax2;
	}

	public TaxEmbeddable getTax3() {
		if (tax3 == null) {
			tax3 = new TaxEmbeddable();
		}
		return tax3;
	}

	public void setTax3(TaxEmbeddable tax3) {
		this.tax3 = tax3;
	}

	public TaxEmbeddable getTax4() {
		if (tax4 == null) {
			tax4 = new TaxEmbeddable();
		}
		return tax4;
	}

	public void setTax4(TaxEmbeddable tax4) {
		this.tax4 = tax4;
	}

	public TaxEmbeddable getTax5() {
		if (tax5 == null) {
			tax5 = new TaxEmbeddable();
		}
		return tax5;
	}

	public void setTax5(TaxEmbeddable tax5) {
		this.tax5 = tax5;
	}

	public Quantity getQuantity() {
		return quantity;
	}

	public void setQuantity(Quantity quantity) {
		this.quantity = quantity;
	}
	
	public MoneySet getTaxExcludedTotal() {
		return taxExcludedTotal;
	}

	public void setTaxExcludedTotal(MoneySet taxExcludedTotal) {
		this.taxExcludedTotal = taxExcludedTotal;
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

	public MoneySet getBeforeTax() {
		return beforeTax;
	}

	public void setBeforeTax(MoneySet beforeTax) {
		this.beforeTax = beforeTax;
	}

	public MoneySet getGrandTotal() {
		return grandTotal;
	}

	public void setGrandTotal(MoneySet grandTotal) {
		this.grandTotal = grandTotal;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public MoneySet getTaxTotalAmount() {
		return taxTotalAmount;
	}

	public void setTaxTotalAmount(MoneySet taxTotalAmount) {
		this.taxTotalAmount = taxTotalAmount;
	}

	public MoneySet getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(MoneySet totalAmount) {
		this.totalAmount = totalAmount;
	}

	public DiscountOrExpense getFee() {
		if (fee == null) {
			fee = new DiscountOrExpense();
		}
		return fee;
	}

	public void setFee(DiscountOrExpense fee) {
		this.fee = fee;
	}

	public MoneySet getTaxExcludedUnitPrice() {
		return taxExcludedUnitPrice;
	}
	
	public void setTaxExcludedUnitPrice(MoneySet taxExcludedUnitPrice) {
		this.taxExcludedUnitPrice = taxExcludedUnitPrice;
	}
	
	public MoneySet getTaxExcludedAmount() {
		return taxExcludedAmount;
	}

	public void setTaxExcludedAmount(MoneySet taxExcludedAmount) {
		this.taxExcludedAmount = taxExcludedAmount;
	}

	public ProductType getProductType() {
		return productType;
	}

	public void setProductType(ProductType productType) {
		this.productType = productType;
	}

	public DiscountOrExpense getExpense() {
		if (expense == null) {
			expense = new DiscountOrExpense();
		}
		return expense;
	}

	public void setExpense(DiscountOrExpense expense) {
		this.expense = expense;
	}

	public DiscountOrExpense getDiscount() {
		if (discount == null) {
			discount = new DiscountOrExpense(); 
		}
		return discount;
	}

	public void setDiscount(DiscountOrExpense discount) {
		this.discount = discount;
	}

	public DistributionStyle getDiscountStyle() {
		return discountStyle;
	}

	public void setDiscountStyle(DistributionStyle discountStyle) {
		this.discountStyle = discountStyle;
	}

	public DistributionStyle getExpenseStyle() {
		return expenseStyle;
	}

	public void setExpenseStyle(DistributionStyle expenseStyle) {
		this.expenseStyle = expenseStyle;
	}

	public MoneySet getTotalAmountTransient() {
		return totalAmountTransient;
	}

	public void setTotalAmountTransient(MoneySet totalAmountTransient) {
		this.totalAmountTransient = totalAmountTransient;
	}

	public Long getReferenceDocumentId() {
		return referenceDocumentId;
	}

	public void setReferenceDocumentId(Long referenceDocumentId) {
		this.referenceDocumentId = referenceDocumentId;
	}

	public DocumentType getReferenceDocumentType() {
		return referenceDocumentType;
	}

	public void setReferenceDocumentType(DocumentType referenceDocumentType) {
		this.referenceDocumentType = referenceDocumentType;
	}

	public MoneySet getForeignUnitPrice() {
		return foreignUnitPrice;
	}

	public void setForeignUnitPrice(MoneySet foreignUnitPrice) {
		this.foreignUnitPrice = foreignUnitPrice;
	}
	
}
