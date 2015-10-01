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

import org.hibernate.annotations.ForeignKey;
import org.hibernate.validator.Length;
import org.hibernate.validator.Valid;

/**
 * Sayım fişi detay satır bilgilerini tutan sınıftır.
 * @author sinan.yumak
 *
 */
@Entity
@Table(name="COUNT_NOTE_ITEM")
public class CountNoteItem implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE,generator="genericSeq")
    @Column(name="ID")
    private Long id;

    @ManyToOne
    @JoinColumn(name="OWNER_ID")
    @ForeignKey(name="FK_COUNTNOTEITEM_OWNERID")
    private CountNote owner;

    /**
     * Var olan stok miktarını tutar.
     */
    @Column(name="EXIST")
    private Integer exist;

    /**
     * Miktar bilgisini tutar.
     */
    @Column(name="QUANTITY")
    private Integer quantity = 1;

    /**
     * Düzeltme miktarı bilgisini tutar.
     */
    @Column(name="CORR_QUANTITY")
    private Integer corrQuantity = 0;

    /**
     * Birim bilgisini tutar.
     */
    @Column(name="UNIT", length=10)
    @Length(max=10)
    private String unit;

    /**
     * Birim fiyatı tutar.
     */
    @Embedded
    @Valid
    @AttributeOverrides( {
        @AttributeOverride(name="currency", column=@Column(name="PRICE_CCY")),
        @AttributeOverride(name="value",    column=@Column(name="PRICE_VALUE")),
        @AttributeOverride(name="localAmount", column=@Column(name="PRICE_LCYVAL"))
    })
    private MoneySet price = new MoneySet();

    /**
     * Ürün bilgisini tutar.
     */
    @ManyToOne
    @JoinColumn(name="PRODUCT_ID")
    private Product product;

    /**
     * Sayılan barkod bilgisini tutar
     */
    @Column(name="BARCODE", length=80)
    @Length(max=80)
    private String barcode;
    
    /**
     * Sayım görevlisi (sayan) kullanıcının id tutar
     */
    @ManyToOne
    @JoinColumn(name="USER_ID")
    @ForeignKey(name="FK_COUNTNOTEITEM_USERID")
    private User user;
    
    /**
     * Sayılan barkod bilgisini tutar
     */
    @Column(name="INFO", length=250)
    @Length(max=250)
    private String info;
    
    /**
     * Sayılan ürünün yer bilgisini tutar.
     */
    @Column(name="PLACE")
    private String place;

    public CountNoteItem() {
    	super();
    }

    public CountNoteItem(Product product, int quantity, MoneySet price, String barcode, User enumerator, String info ) {
    	this.product = product;
    	this.unit = product.getUnit();
    	this.quantity = quantity;
    	this.price = price;
    	this.barcode = barcode;
    	this.user = enumerator;
    	this.info = info;
    }

    public CountNoteItem(Product product, CountNote owner) {
    	this.product = product;
    	this.unit = product.getUnit();
    	this.quantity = 0;
    	this.owner = owner;
    }
    
    public boolean matches(ProductCategory cat) {
    	if (cat == null) return true;
    	return cat.equals(product.getCategory());
    }
    
    public boolean matches(ProductGroup group) {
    	if (group == null) return true;
    	return group.equals(product.getGroup());
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
        if (!(object instanceof CountNoteItem)) {
            return false;
        }
        CountNoteItem other = (CountNoteItem)object;
        if (this.getId() != other.getId() && (this.getId() == null || !this.id.equals(other.id))) return false;
        return true;
    }

    @Override
    public String toString() {
        return "com.ut.tekir.entities.CountNoteItem[id=" + getId() + "]";
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public CountNote getOwner() {
		return owner;
	}

	public void setOwner(CountNote owner) {
		this.owner = owner;
	}

	public MoneySet getPrice() {
		return price;
	}

	public void setPrice(MoneySet price) {
		this.price = price;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getBarcode() {
		return barcode;
	}

	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public String getPlace() {
		return place;
	}

	public void setPlace(String place) {
		this.place = place;
	}

	public Integer getCorrQuantity() {
		return corrQuantity;
	}

	public void setCorrQuantity(Integer corrQuantity) {
		this.corrQuantity = corrQuantity;
	}

	public Integer getExist() {
		return exist;
	}

	public void setExist(Integer exist) {
		this.exist = exist;
	}

}
