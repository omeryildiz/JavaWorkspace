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
import java.math.RoundingMode;

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
import javax.persistence.Transient;

import org.hibernate.validator.Valid;

/**
 * @author sinan.yumak
 *
 */
@Entity
@Table(name="PRICE_ITEM_DETAIL")
public class PriceItemDetail implements Serializable {

	private static final long serialVersionUID = 1L;
	
    @Id
	@GeneratedValue(strategy = GenerationType.TABLE,generator="genericSeq")
    @Column(name="ID")
    private Long id;

    @ManyToOne
    @JoinColumn(name="OWNER_ID")
    private PriceItem owner;

    @ManyToOne
    @JoinColumn(name="PRODUCT_ID")
    private Product product;
    
    /**
     * Brüt fiyat bilgilerini tutar.
     */
    @Embedded
    @Valid
    @AttributeOverrides( {
    	@AttributeOverride(name="currency", column=@Column(name="GROSS_PRICE_CCY")),
    	@AttributeOverride(name="value",    column=@Column(name="GROSS_PRICE_VALUE")),
    	@AttributeOverride(name="localAmount", column=@Column(name="GROSS_PRICE_LCYVAL"))
    })
    private MoneySet grossPrice = new UnitPriceMoneySet();

    /**
     * Net fiyat bilgilerini tutar.
     */
    @Embedded
    @Valid
    @AttributeOverrides( {
    	@AttributeOverride(name="currency", column=@Column(name="NET_PRICE_CCY")),
    	@AttributeOverride(name="value",    column=@Column(name="NET_PRICE_VALUE")),
    	@AttributeOverride(name="localAmount", column=@Column(name="NET_PRICE_LCYVAL"))
    })
    private MoneySet netPrice = new UnitPriceMoneySet();

    /**
     * fiyatta indirim olup olmayacağı bilgisini tutar.
     */
    @Column(name="HAS_DISCOUNT")
    private Boolean hasDiscount = Boolean.FALSE;
    
    /**
     * True ise oran, değilse tutar kabul edilecek.
     */
    @Column(name="DISCOUNT_TYPE")
    private Boolean discountType = Boolean.TRUE;
    
    /**
     * ürüne yapılan indirim oranını tutar.
     */
    @Column(name="DISCOUNT_RATE", precision=10, scale=2)
    private BigDecimal discountRate = BigDecimal.ZERO;

    /**
     * Ürüne yapılan indirim tutarını tutar.
     */
    @Embedded
    @Valid
    @AttributeOverrides( {
    	@AttributeOverride(name="currency", column=@Column(name="DISCOUNT_PRICE_CCY")),
    	@AttributeOverride(name="value",    column=@Column(name="DISCOUNT_PRICE_VALUE")),
    	@AttributeOverride(name="localAmount", column=@Column(name="DISCOUNT_PRICE_LCYVAL"))
    })
    private MoneySet discountPrice = new UnitPriceMoneySet();

    /**
     * Tedarikçi ürün kod bilgisini tutar.
     */
    @Column(name="PROVIDER_PRODUCT_CODE")
    private String providerProductCode;

    /**
     * Tedarikçi ürün ad bilgisini tutar.
     */
    @Column(name="PROVIDER_PRODUCT_NAME")
    private String providerProductName;
    
    /**
	 * seçili fiyat satırının barkod kuyruğuna gönderilip gönderilmeyeceği 
	 * bilgisini tutar.
	 */
	@Transient
	private boolean sendToSpool = false;
	
	public BigDecimal getMinimumPrice() {
		BigDecimal tolerance = BigDecimal.ZERO;
		BigDecimal netPrice = getNetPrice().getValue();

		if (getDiscountType()) {
			BigDecimal hundred = BigDecimal.valueOf(100);
			tolerance = netPrice.multiply(discountRate)
								.divide(hundred, product.getUnitPriceScale().getScale(), RoundingMode.HALF_UP);
		} else {
			tolerance = discountPrice.getValue();
		}
		return netPrice.subtract(tolerance);
	}

	public BigDecimal getMaxPrice() {
		BigDecimal tolerance = BigDecimal.ZERO;
		BigDecimal netPrice = getNetPrice().getValue();

		if (getDiscountType()) {
			BigDecimal hundred = BigDecimal.valueOf(100);
			tolerance = netPrice.multiply(discountRate)
								.divide(hundred, product.getUnitPriceScale().getScale(), RoundingMode.HALF_UP);
		} else {
			tolerance = discountPrice.getValue();
		}
		return netPrice.add(tolerance);
	}
	
	public void checkTolerance(Product aProduct, BigDecimal aValue) {
    	StringBuilder errorString = new StringBuilder();

    	BigDecimal minimumPrice = getMinimumPrice();
    	BigDecimal maxPrice = getMaxPrice();

    	if (aValue.compareTo(minimumPrice) < 0 || aValue.compareTo(maxPrice) > 0) {
			errorString.append(aProduct.getCaption())
					   .append(" ürünü için fiyat limiti aşıldı. Beklenen ürün fiyatı ")
					   .append(minimumPrice)
					   .append(" ile ")
					   .append(maxPrice)
					   .append(" aralığında olmalıdır.\n");
			
			throw new RuntimeException(errorString.toString());
		}
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
        if (!(object instanceof PriceItemDetail)) {
            return false;
        }
        PriceItemDetail other = (PriceItemDetail)object;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) return false;
        return true;
    }
	
	@Override
    public String toString() {
        return "com.ut.tekir.entities.PriceItemDetail[id=" + id + "]";
    }

	public MoneySet getDiscountPrice() {
		return discountPrice;
	}

	public void setDiscountPrice(MoneySet discountPrice) {
		this.discountPrice = discountPrice;
	}

	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}

	public PriceItem getOwner() {
		return owner;
	}

	public void setOwner(PriceItem owner) {
		this.owner = owner;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public MoneySet getGrossPrice() {
		return grossPrice;
	}

	public void setGrossPrice(MoneySet grossPrice) {
		this.grossPrice = grossPrice;
	}

	public MoneySet getNetPrice() {
		return netPrice;
	}

	public void setNetPrice(MoneySet netPrice) {
		this.netPrice = netPrice;
	}

	public BigDecimal getDiscountRate() {
		return discountRate;
	}

	public void setDiscountRate(BigDecimal discountRate) {
		this.discountRate = discountRate;
	}

	public Boolean getDiscountType() {
		return discountType;
	}

	public void setDiscountType(Boolean discountType) {
		this.discountType = discountType;
	}

	public Boolean getHasDiscount() {
		return hasDiscount;
	}

	public void setHasDiscount(Boolean hasDiscount) {
		this.hasDiscount = hasDiscount;
	}

	public boolean isSendToSpool() {
		return sendToSpool;
	}

	public void setSendToSpool(boolean sendToSpool) {
		this.sendToSpool = sendToSpool;
	}

	public String getProviderProductCode() {
		return providerProductCode;
	}

	public void setProviderProductCode(String providerProductCode) {
		this.providerProductCode = providerProductCode;
	}

	public String getProviderProductName() {
		return providerProductName;
	}

	public void setProviderProductName(String providerProductName) {
		this.providerProductName = providerProductName;
	}

}
