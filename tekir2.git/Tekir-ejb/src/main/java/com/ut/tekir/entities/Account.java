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
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;
import org.hibernate.validator.Valid;

/**
 * Entity class Account
 * Account for saving funds.
 *
 * <b>Usage example</b>
 * code : 101
 * accountType : cashbook
 * name : Nakit Kasa
 *
 * code : 201
 * accountType : bank
 * name : Garanti Beşiktaş
 * info : 334-828282
 * 
 * @author haky
 */
@Entity
@Table(name="ACCOUNT")
public class Account extends AuditBase implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE,generator="genericSeq")
    @Column(name="ID")
    private Long id;
    
    @Column(name="ACCOUNT_TYPE")
    @Enumerated(EnumType.ORDINAL)
    private AccountType accountType;
    
    @Column(name="CODE", length=20, nullable=false, unique=true) 
    @NotNull
    @Length(max=20, min=1)
    private String code;
    
    @Column(name="NAME", length=50) 
    @Length(max=50, min=1)
    private String name;
    
    @Column(name="INFO") 
    private String info;
  
    @Column(name="SYSTEM")
    private Boolean system;

    @Column(name="ISACTIVE")
    private Boolean active = Boolean.TRUE;

    @Embedded
    private ContactPerson contactPerson;


    @ManyToOne
    @JoinColumn(name="ORGANIZATION_ID")
    private Organization organization;

    /**
     * ContactPerson icinde fax bilgisi zaten var. 2. kez eklenmis
     */
    /*
	@Embedded
	@Valid
	@AttributeOverrides( {
			@AttributeOverride(name = "countryCode", column = @Column(name = "ACCOUNT_FAX_COUNTRY_CODE")),
			@AttributeOverride(name = "areaCode", column = @Column(name = "ACCOUNT_FAX_AREA_CODE")),
			@AttributeOverride(name = "number", column = @Column(name = "ACCOUNT_FAX_NUMBER")),
			@AttributeOverride(name = "extention", column = @Column(name = "ACCOUNT_FAX_EXTENTION")) })
	private Phone fax = new Phone();
    */

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Account)) {
            return false;
        }
        Account other = (Account)object;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) return false;
        return true;
    }

    @Override
    public String toString() {
        return "com.ut.tekir.entities.Account[id=" + id + "]";
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /*
    public Phone getFax() {
		return fax;
	}

	public void setFax(Phone fax) {
		this.fax = fax;
	}
    */
	public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
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

	public ContactPerson getContactPerson() {
		if (contactPerson == null) {
			contactPerson = new ContactPerson();
		}
		return contactPerson;
	}

	public void setContactPerson(ContactPerson contactPerson) {
		this.contactPerson = contactPerson;
	}

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

}
