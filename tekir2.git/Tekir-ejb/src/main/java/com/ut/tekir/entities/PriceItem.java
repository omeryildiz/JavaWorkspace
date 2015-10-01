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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
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

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;

/**
 * Hem satış hem de alış fiyat listelerini tutacak olan model...
 * 
 * @author sinan.yumak
 *
 */
@Entity
@Table(name="PRICE_ITEM")
public class PriceItem implements Serializable {

	private static final long serialVersionUID = 1L;
	
    @Id
	@GeneratedValue(strategy = GenerationType.TABLE,generator="genericSeq")
    @Column(name="ID")
    private Long id;

    /**
     * bağlı olduğu fiyat listesi.
     */
    @ManyToOne
    @JoinColumn(name="OWNER_ID")
    private PriceList owner;
    
	@Column(name="CALC_TYPE")
	@Enumerated(EnumType.ORDINAL)
    private PriceListCalcType calcType;
    
    @Column(name="IS_ACTIVE")
    private Boolean active = Boolean.TRUE;

    @Column(name="IS_DEFAULT_ITEM")
    private Boolean defaultItem = Boolean.FALSE;

    @Column(name="INFO")
    private String info;

    @Column(name="CODE", nullable=false, length=10)
    @NotNull
    @Length(max=10, min=1)
    private String code;

    /**
     * geçerlilik başlangıç tarihi
     */
    @Column(name="BEGIN_DATE")
    @Temporal(value = TemporalType.DATE)
    private Date beginDate;

    /**
     * geçerlilik bitiş tarihi
     */
    @Column(name="END_DATE")
    @Temporal(value = TemporalType.DATE)
    private Date endDate;

    @Column(name="ACTION")
    @Enumerated(EnumType.ORDINAL)
    private TradeAction action;
    
    /**
	 * Eğer satış tipinde bir liste ise, marka bilgisini tutar.  
	 */
	@ManyToOne
	@JoinColumn(name="GROUP_ID")
	private ProductGroup group;

	/**
	 * Eğer alış tipinde bir liste ise, tedarikçi bilgisini tutar.  
	 */
	@ManyToOne
	@JoinColumn(name="CONTACT_ID")
	@ForeignKey(name="FK_PRICEITEM_CONTACTID")
	private Contact contact;

	@OneToMany(mappedBy = "owner", cascade=CascadeType.ALL )
    @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    private List<PriceItemDetail> items = new ArrayList<PriceItemDetail>();
	
	public PriceItemDetail findItemWithProduct(Product aProduct) {
		for(PriceItemDetail item : items) {
			if (item.getProduct().equals(aProduct)) {
				return item;
			}
		}
		return null;
	}
	
	public PriceItemDetail findItemWithProductId(Long aProduct) {
		for(PriceItemDetail item : items) {
			if (item.getProduct().getId().equals(aProduct)) {
				return item;
			}
		}
		return null;
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
        if (!(object instanceof PriceItem)) {
            return false;
        }
        PriceItem other = (PriceItem)object;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) return false;
        return true;
    }
	
	@Override
    public String toString() {
        return "com.ut.tekir.entities.PriceItem[id=" + id + "]";
    }

	public String info() {
		StringBuilder sb= new StringBuilder();
		sb.append("Code : ")
		  .append(getCode())
		  .append(" Begin Date: ")
		  .append(getBeginDate())
		  .append(" End Date: ")
		  .append(getEndDate());
		return sb.toString();
	}
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}

	public Date getBeginDate() {
		return beginDate;
	}

	public void setBeginDate(Date beginDate) {
		this.beginDate = beginDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public List<PriceItemDetail> getItems() {
		return items;
	}

	public void setItems(List<PriceItemDetail> items) {
		this.items = items;
	}

	public PriceList getOwner() {
		return owner;
	}

	public void setOwner(PriceList owner) {
		this.owner = owner;
	}

	public PriceListCalcType getCalcType() {
		return calcType;
	}

	public void setCalcType(PriceListCalcType calcType) {
		this.calcType = calcType;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Boolean getDefaultItem() {
		return defaultItem;
	}

	public void setDefaultItem(Boolean defaultItem) {
		this.defaultItem = defaultItem;
	}

	public ProductGroup getGroup() {
		return group;
	}

	public void setGroup(ProductGroup group) {
		this.group = group;
	}

	public Contact getContact() {
		return contact;
	}

	public void setContact(Contact contact) {
		this.contact = contact;
	}

	public TradeAction getAction() {
		return action;
	}

	public void setAction(TradeAction action) {
		this.action = action;
	}

}
