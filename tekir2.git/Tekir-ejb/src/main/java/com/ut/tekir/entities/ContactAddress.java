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

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.validator.Valid;

/**
 * @author sinan.yumak
 *
 */
@Entity
@Table(name="CONTACT_ADDRESS")
public class ContactAddress extends AuditBase implements Serializable {

	private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE,generator="genericSeq")
    @Column(name="ID")
    private Long id;

    //master bağlantısı
    @ManyToOne
    @JoinColumn(name="OWNER_ID")
    private Contact owner;
    
    @Column(name="INFO")
    private String info;
    
    @Embedded
    @Valid
    private Address address = new Address();

    @Column(name="ACTIVE_ADDRESS")
    private Boolean activeAddress = Boolean.TRUE;

    @Column(name="DEFAULT_ADDRESS")
    private Boolean defaultAddress = Boolean.FALSE;
    
    @Column(name="INVOICE_ADDRESS")
    private Boolean invoiceAddress = Boolean.FALSE; 

    @Column(name="SHIPPING_ADDRESS")
    private Boolean shippingAddress = Boolean.FALSE; 

    @Column(name="DELIVERY_ADDRESS")
    private Boolean deliveryAddress = Boolean.FALSE; 

    @Column(name="HOME_ADDRESS")
    private Boolean homeAddress = Boolean.FALSE; 

    @Column(name="WORK_ADDRESS")
    private Boolean workAddress = Boolean.FALSE; 

    @Column(name="HOMEOFFICE_ADDRESS")
    private Boolean homeOfficeAddress = Boolean.FALSE; 

    @Column(name="OTHER_ADDRESS")
    private Boolean otherAddress = Boolean.FALSE; 

    //doğrudan bir adres mi değil mi? (Ör. eşinin iş adresi)
    @Column(name="RELATED")
    private Boolean related = Boolean.FALSE;
    
    @ManyToOne
    @JoinColumn(name="CITY_ID")
    private City city;
    
    @ManyToOne
    @JoinColumn(name="PROVINCE_ID")
    private Province province;
    
    /**
     * Holds if this address used in a document.
     */
    @Column(name="USED")
    private Boolean used = Boolean.FALSE;

    /**
     * Bu alanlar adresle ilişkili olarak alıcı bilgilerini tutar.
     */
    @Column(name="RECIPIENT_NAME",length=30)
    private String recipientName;

    @Column(name="RECIPIENT_SURNAME",length=30)
    private String recipientSurName;
    
    public String getIconName() {
    	if (workAddress) return "workAddressIcon";
    	if (homeAddress) return "homeAddressIcon";
    	if (homeOfficeAddress) return "homeOfficeAddressIcon";
    	if (otherAddress) return "otherAddressIcon";
    	if (related) return "relatedAddressIcon";
    	return null;
    }
    
    public String getUsageType() {
    	if (homeAddress) return "homeAddress";
    	if (workAddress) return "workAddress";
    	if (otherAddress) return "otherAddress";
    	if (homeOfficeAddress) return "homeOfficeAddress";
    	if (related) return "relatedAddress";
    	return null;
    }

    public void setUsageType(String usageType) {
    	homeAddress = Boolean.FALSE;
    	homeOfficeAddress = Boolean.FALSE;
    	workAddress = Boolean.FALSE;
    	otherAddress = Boolean.FALSE;
    	related = Boolean.FALSE;

    	if (usageType.equals("homeAddress")) {
    		homeAddress = Boolean.TRUE;
    	} else if (usageType.equals("homeOfficeAddress")) {
    		homeOfficeAddress = Boolean.TRUE;
    	} else if (usageType.equals("workAddress")) {
    		workAddress = Boolean.TRUE;
    	} else if (usageType.equals("otherAddress")) {
    		otherAddress = Boolean.TRUE;
    	} else if (usageType.equals("relatedAddress")) {
    		related = Boolean.TRUE;
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
        if (!(object instanceof ContactAddress)) {
            return false;
        }
        ContactAddress other = (ContactAddress)object;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) return false;
        return true;
    }

    @Override
    public String toString() {
        return "com.ut.tekir.entities.ContactAddress[id=" + id + "]";
    }
    
    public String description() {
    	StringBuffer sb = new StringBuffer();
    	if (getRecipientName() != null) sb.append(getRecipientName()).append(" ");
    	if (getRecipientSurName() != null) sb.append(getRecipientSurName());
    	if (address != null) {
    		if (address.getStreet() != null) {
    			sb.append("\n");
    			sb.append(address.getStreet());
    		}
    		if (address.getZip() != null) {
    			sb.append("\n");
    			sb.append(address.getZip());
    		}
    		if (address.getProvince() != null) {
    			sb.append("\n");
    			sb.append(address.getProvince());
    			sb.append("/");
    			sb.append(address.getCity());
    		}
    		if (address.getCountry() != null) {
    			sb.append("\n");
    			sb.append(address.getCountry());
    		}
    	}
    	return sb.toString();
    }
    
    public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public Boolean getActiveAddress() {
		return activeAddress;
	}

	public void setActiveAddress(Boolean activeAddress) {
		this.activeAddress = activeAddress;
	}

	public Boolean getDefaultAddress() {
		return defaultAddress;
	}

	public void setDefaultAddress(Boolean defaultAddress) {
		this.defaultAddress = defaultAddress;
	}

	public Boolean getInvoiceAddress() {
		return invoiceAddress;
	}

	public void setInvoiceAddress(Boolean invoiceAddress) {
		this.invoiceAddress = invoiceAddress;
	}

	public Boolean getShippingAddress() {
		return shippingAddress;
	}

	public void setShippingAddress(Boolean shippingAddress) {
		this.shippingAddress = shippingAddress;
	}

	public Boolean getDeliveryAddress() {
		return deliveryAddress;
	}

	public void setDeliveryAddress(Boolean deliveryAddress) {
		this.deliveryAddress = deliveryAddress;
	}

	public Boolean getHomeAddress() {
		return homeAddress;
	}

	public void setHomeAddress(Boolean homeAddress) {
		this.homeAddress = homeAddress;
	}

	public Boolean getWorkAddress() {
		return workAddress;
	}

	public void setWorkAddress(Boolean workAddress) {
		this.workAddress = workAddress;
	}

	public Boolean getOtherAddress() {
		return otherAddress;
	}

	public void setOtherAddress(Boolean otherAddress) {
		this.otherAddress = otherAddress;
	}

	public Boolean getRelated() {
		return related;
	}

	public void setRelated(Boolean related) {
		this.related = related;
	}

	public Address getAddress() {
		if (address == null) {
			address = new Address();
		}
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}
	
	public Boolean getUsed() {
		return used;
	}

	public void setUsed(Boolean used) {
		this.used = used;
	}

	public Contact getOwner() {
		return owner;
	}

	public void setOwner(Contact owner) {
		this.owner = owner;
	}

	public City getCity() {
		return city;
	}

	public void setCity(City city) {
		this.city = city;
		if (city != null) getAddress().setCity(city.getName());
	}

	public Province getProvince() {
		return province;
	}

	public void setProvince(Province province) {
		this.province = province;
		if (province != null) getAddress().setProvince(province.getName());
	}

	public String getRecipientName() {
		return recipientName;
	}

	public void setRecipientName(String recipientName) {
		this.recipientName = recipientName;
	}

	public String getRecipientSurName() {
		return recipientSurName;
	}

	public void setRecipientSurName(String recipientSurName) {
		this.recipientSurName = recipientSurName;
	}

	public Boolean getHomeOfficeAddress() {
		return homeOfficeAddress;
	}

	public void setHomeOfficeAddress(Boolean homeOfficeAddress) {
		this.homeOfficeAddress = homeOfficeAddress;
	}
	
}
