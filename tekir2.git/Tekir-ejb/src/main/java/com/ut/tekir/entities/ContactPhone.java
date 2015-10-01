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
@Table(name="CONTACT_PHONE")
public class ContactPhone extends AuditBase implements Serializable {

	private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE,generator="genericSeq")
    @Column(name="ID")
    private Long id;
    
    //master bağlantısı
    @ManyToOne
    @JoinColumn(name="OWNER_ID")
    private Contact owner;

    @Embedded
	@Valid
    private Phone phone;
    
    @Column(name="INFO")
    private String info;

    @Column(name="ACTIVE_PHONE")
    private Boolean activePhone = Boolean.TRUE;
    
    @Column(name="DEFAULT_PHONE")
    private Boolean defaultPhone = Boolean.FALSE;

    @Column(name="HOME_PHONE")
    private Boolean homePhone = Boolean.FALSE;

    @Column(name="WORK_PHONE")
    private Boolean workPhone = Boolean.FALSE;
    
    @Column(name="OTHER_PHONE")
    private Boolean otherPhone = Boolean.FALSE;

    @Column(name="FAX_PHONE")
    private Boolean faxPhone = Boolean.FALSE;

    @Column(name="GSM_PHONE")
    private Boolean gsmPhone = Boolean.FALSE;

    @Column(name="VEHICLE_PHONE")
    private Boolean vehiclePhone = Boolean.FALSE;

    @Column(name="CALLER_PHONE")
    private Boolean callerPhone = Boolean.FALSE;

    @Column(name="IMMOBILE_PHONE")
    private Boolean immobilePhone = Boolean.FALSE;

    @Column(name="RELATED")
    private Boolean related = Boolean.FALSE;
    
    public String getIconName() {
    	if (immobilePhone) return "immobilePhoneIcon";
    	if (gsmPhone) return "gsmPhoneIcon";
    	if (vehiclePhone) return "vehiclePhoneIcon";
    	if (callerPhone) return "callerPhoneIcon";
    	if (faxPhone) return "faxPhoneIcon";
    	return null;
    }

    public void setUsageType(String usageType) {
    	immobilePhone = Boolean.FALSE;
    	gsmPhone = Boolean.FALSE;
    	vehiclePhone = Boolean.FALSE;
    	callerPhone = Boolean.FALSE;
    	faxPhone = Boolean.FALSE;

    	if (usageType.equals("immobilePhone")) {
    		immobilePhone = Boolean.TRUE;
    	} else if (usageType.equals("gsmPhone")) {
    		gsmPhone = Boolean.TRUE;
    	} else if (usageType.equals("vehiclePhone")) {
    		vehiclePhone = Boolean.TRUE;
    	} else if (usageType.equals("callerPhone")) {
    		callerPhone = Boolean.TRUE;
    	} else if (usageType.equals("faxPhone")) {
    		faxPhone = Boolean.TRUE;
    	}
    }

    public String getUsageType() {
    	if (immobilePhone) return "immobilePhone";
    	if (gsmPhone) return "gsmPhone";
    	if (vehiclePhone) return "vehiclePhone";
    	if (callerPhone) return "callerPhone";
    	if (faxPhone) return "faxPhone";
    	return null;
    }

    public void setPhoneType(String phoneType) {
    	homePhone = Boolean.FALSE;
    	workPhone = Boolean.FALSE;
    	otherPhone = Boolean.FALSE;
    	related = Boolean.FALSE;

    	if (phoneType.equals("homePhone")) {
    		homePhone = Boolean.TRUE;
    	} else if (phoneType.equals("workPhone")) {
    		workPhone = Boolean.TRUE;
    	} else if (phoneType.equals("otherPhone")) {
    		otherPhone = Boolean.TRUE;
    	} else if (phoneType.equals("relatedPhone")) {
    		related = Boolean.TRUE;
    	}
    }

    public String getPhoneType() {
    	if (homePhone) return "homePhone";
    	if (workPhone) return "workPhone";
    	if (otherPhone) return "otherPhone";
    	if (related) return "relatedPhone";
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
        if (!(object instanceof ContactPhone)) {
            return false;
        }
        ContactPhone other = (ContactPhone)object;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) return false;
        return true;
    }

    @Override
    public String toString() {
        return "com.ut.tekir.entities.ContactPhone[id=" + id + "]";
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Phone getPhone() {
		if (phone == null) {
			phone = new Phone();
		}
		return phone;
	}

	public void setPhone(Phone phone) {
		this.phone = phone;
	}

	public Contact getOwner() {
		return owner;
	}

	public void setOwner(Contact owner) {
		this.owner = owner;
	}

	public Boolean getImmobilePhone() {
		return immobilePhone;
	}

	public void setImmobilePhone(Boolean immobilePhone) {
		this.immobilePhone = immobilePhone;
	}

	public Boolean getActivePhone() {
		return activePhone;
	}

	public void setActivePhone(Boolean activePhone) {
		this.activePhone = activePhone;
	}

	public Boolean getDefaultPhone() {
		return defaultPhone;
	}

	public void setDefaultPhone(Boolean defaultPhone) {
		this.defaultPhone = defaultPhone;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public Boolean getHomePhone() {
		return homePhone;
	}

	public void setHomePhone(Boolean homePhone) {
		this.homePhone = homePhone;
	}

	public Boolean getWorkPhone() {
		return workPhone;
	}

	public void setWorkPhone(Boolean workPhone) {
		this.workPhone = workPhone;
	}

	public Boolean getOtherPhone() {
		return otherPhone;
	}

	public void setOtherPhone(Boolean otherPhone) {
		this.otherPhone = otherPhone;
	}

	public Boolean getFaxPhone() {
		return faxPhone;
	}

	public void setFaxPhone(Boolean faxPhone) {
		this.faxPhone = faxPhone;
	}

	public Boolean getGsmPhone() {
		return gsmPhone;
	}

	public void setGsmPhone(Boolean gsmPhone) {
		this.gsmPhone = gsmPhone;
	}

	public Boolean getVehiclePhone() {
		return vehiclePhone;
	}

	public void setVehiclePhone(Boolean vehiclePhone) {
		this.vehiclePhone = vehiclePhone;
	}

	public Boolean getCallerPhone() {
		return callerPhone;
	}

	public void setCallerPhone(Boolean callerPhone) {
		this.callerPhone = callerPhone;
	}

	public Boolean getRelated() {
		return related;
	}

	public void setRelated(Boolean related) {
		this.related = related;
	}

}
