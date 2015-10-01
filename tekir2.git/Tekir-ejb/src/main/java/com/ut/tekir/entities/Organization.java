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
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;
import org.hibernate.validator.NotNull;
import org.hibernate.validator.Valid;

/**
 * Holds hierarchy information of company.
 * 
 * @author sinan.yumak
 *
 */
@Entity
@Table(name="ORGANIZATION")
public class Organization implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE,generator="genericSeq")
    @Column(name="ID")
    private Long id;
	
    @Column(name="CODE", nullable=false, unique=true, length=20)
    @NotNull
    private String code;
    
    @Column(name="NAME", length=30)
    @NotNull
    private String name;

    @Column(name="IS_ACTIVE")
    private Boolean active = Boolean.TRUE;
    
    @Column(name="HAS_CHILD")
    private Boolean hasChild = Boolean.FALSE;

    @ManyToOne
    @JoinColumn(name="PARENT_ORGANIZATION")
    private Organization parentOrganization;
    
    @ManyToOne
    @JoinColumn(name="LEVEL_ID")
    private OrganizationLevel level;

    /**
     * Private Address Informations...
     */
    @Column(name="HAS_PRIVATE_ADDRESS")
    private Boolean hasPrivateAddress = Boolean.FALSE;

    @Column(name="INFO")
    private String info;
	
    @Column(name="FIRM_TITLE")
    private String firmTitle;
    
    @Column(name="TAX_OFFICE")
    private String taxOffice;
    
    @Column(name="TAX_NUMBER")
    private String taxNumber;

    @Embedded
    @Valid
    private Address address = new Address();
    
    @ManyToOne
    @JoinColumn(name="CITY_ID")
    private City city;
    
    @ManyToOne
    @JoinColumn(name="PROVINCE_ID")
    private Province province;
    
	@Embedded
    private ContactPerson contactPerson;
    
    @OneToMany(mappedBy="parentOrganization", cascade=CascadeType.ALL)
    @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    @OrderBy("id ASC")
    private List<Organization> childOrganizationList = new ArrayList<Organization>();

	@Override
    public int hashCode() {
        int hash = 0;
        hash += (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Organization)) {
            return false;
        }
        Organization other = (Organization)object;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) return false;
        return true;
    }

    @Override
    public String toString() {
        return "com.ut.tekir.entities.Organization[id=" + id + "]";
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public City getCity() {
		return city;
	}

	public void setCity(City city) {
		this.city = city;
	}
	
	public ContactPerson getContactPerson() {
		if (contactPerson == null) {
			contactPerson = new ContactPerson();
		}
		return contactPerson;
	}

	public void setContactPerson(ContactPerson contactPerson) {
		this.contactPerson = contactPerson;
	}

	public Province getProvince() {
		return province;
	}

	public void setProvince(Province province) {
		this.province = province;
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

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}
	
	public String getFirmTitle() {
		return firmTitle;
	}

	public void setFirmTitle(String firmTitle) {
		this.firmTitle = firmTitle;
	}

	public Boolean getHasPrivateAddress() {
		return hasPrivateAddress;
	}

	public void setHasPrivateAddress(Boolean hasPrivateAddress) {
		this.hasPrivateAddress = hasPrivateAddress;
	}

	public String getTaxOffice() {
		return taxOffice;
	}

	public void setTaxOffice(String taxOffice) {
		this.taxOffice = taxOffice;
	}

	public String getTaxNumber() {
		return taxNumber;
	}

	public void setTaxNumber(String taxNumber) {
		this.taxNumber = taxNumber;
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

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public Boolean getHasChild() {
		return hasChild;
	}

	public void setHasChild(Boolean hasChild) {
		this.hasChild = hasChild;
	}

	public Organization getParentOrganization() {
		return parentOrganization;
	}

	public void setParentOrganization(Organization parentOrganization) {
		this.parentOrganization = parentOrganization;
	}

	public List<Organization> getChildOrganizationList() {
		return childOrganizationList;
	}

	public void setChildOrganizationList(List<Organization> childOrganizationList) {
		this.childOrganizationList = childOrganizationList;
	}

	public OrganizationLevel getLevel() {
		return level;
	}

	public void setLevel(OrganizationLevel level) {
		this.level = level;
	}

}
