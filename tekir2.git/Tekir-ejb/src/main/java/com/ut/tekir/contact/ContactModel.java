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

package com.ut.tekir.contact;

import com.ut.tekir.entities.ContactType;


/**
 *
 * @author haky
 */
public class ContactModel {
    private Long id;
    private String code;
    private String name;
    private String midname;
    private String surname;
    private String fullname;
    private String ssn;
    private String company;
    private String taxNumber;
    private String categoryCode;
    private String title;
    private String representative;
    private String info;
    private String phone1;
    private String phone2;
    private String phone3;
    private String phoneMobile;
    private String phoneFax;
    private String email1;
    private String email2;
    private String exCode1;
    private String exCode2;
    private String web;
    private ContactType type;

    private Boolean allType;
    private Boolean customerType;
    private Boolean providerType;
    private Boolean agentType;
    private Boolean personnelType;
    private Boolean branchType;
    private Boolean contactType;
    private Boolean bankType;
    private Boolean foundationType;
    private Boolean relatedType;
    private String typeCaption;
    private String taxOffice;
    private String street;
    
    private String defaultNetwork;
    private String defaultEmail;
    private String defaultWeb;
    private String defaultGsm;
    private String defaultFax;
    private String defaultImmobile;
    private String defaultAddress;
    
    public ContactModel() {
    }

    public ContactModel(Long id, String fullname, String company) {
		super();
		this.id = id;
		this.fullname = fullname;
		this.company = company;
	}

    public boolean isInitialized() {
    	return defaultAddress != null;
    }
    
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
    
    public Boolean getAllType() {
		return allType;
	}

	public void setAllType(Boolean allType) {
		this.allType = allType;
	}

	public Boolean getCustomerType() {
		return customerType;
	}

	public void setCustomerType(Boolean customerType) {
		this.customerType = customerType;
	}

	public Boolean getProviderType() {
		return providerType;
	}

	public void setProviderType(Boolean providerType) {
		this.providerType = providerType;
	}

	public Boolean getAgentType() {
		return agentType;
	}

	public void setAgentType(Boolean agentType) {
		this.agentType = agentType;
	}

	public Boolean getPersonnelType() {
		return personnelType;
	}

	public void setPersonnelType(Boolean personnelType) {
		this.personnelType = personnelType;
	}

	public Boolean getBranchType() {
		return branchType;
	}

	public void setBranchType(Boolean branchType) {
		this.branchType = branchType;
	}

	public Boolean getContactType() {
		return contactType;
	}

	public void setContactType(Boolean contactType) {
		this.contactType = contactType;
	}

	public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCategoryCode() {
        return categoryCode;
    }

    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRepresentative() {
        return representative;
    }

    public void setRepresentative(String representative) {
        this.representative = representative;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getPhone1() {
        return phone1;
    }

    public void setPhone1(String phone1) {
        this.phone1 = phone1;
    }

    public String getPhone2() {
        return phone2;
    }

    public void setPhone2(String phone2) {
        this.phone2 = phone2;
    }

    public String getPhone3() {
        return phone3;
    }

    public void setPhone3(String phone3) {
        this.phone3 = phone3;
    }

    public String getPhoneMobile() {
        return phoneMobile;
    }

    public void setPhoneMobile(String phoneMobile) {
        this.phoneMobile = phoneMobile;
    }

    public String getPhoneFax() {
        return phoneFax;
    }

    public void setPhoneFax(String phoneFax) {
        this.phoneFax = phoneFax;
    }

    public String getEmail1() {
        return email1;
    }

    public void setEmail1(String email1) {
        this.email1 = email1;
    }

    public String getEmail2() {
        return email2;
    }

    public void setEmail2(String email2) {
        this.email2 = email2;
    }

    public String getWeb() {
        return web;
    }

    public void setWeb(String web) {
        this.web = web;
    }

	public ContactType getType() {
		return type;
	}

	public void setType(ContactType type) {
		this.type = type;
	}

	public String getMidname() {
		return midname;
	}

	public void setMidname(String midname) {
		this.midname = midname;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public String getFullname() {
		return fullname;
	}

	public void setFullname(String fullname) {
		this.fullname = fullname;
	}

	public String getSsn() {
		return ssn;
	}

	public void setSsn(String ssn) {
		this.ssn = ssn;
	}

	public String getTaxNumber() {
		return taxNumber;
	}

	public void setTaxNumber(String taxNumber) {
		this.taxNumber = taxNumber;
	}

	public String getExCode1() {
		return exCode1;
	}

	public void setExCode1(String exCode1) {
		this.exCode1 = exCode1;
	}

	public String getExCode2() {
		return exCode2;
	}

	public void setExCode2(String exCode2) {
		this.exCode2 = exCode2;
	}

	public String getTypeCaption() {
		return typeCaption;
	}
	
    /**
     * @param typeCaption the typeCaption to set
     */
    public void setTypeCaption(String typeCaption) {
        this.typeCaption = typeCaption;
    }

    /**
     * @return the taxOffice
     */
    public String getTaxOffice() {
        return taxOffice;
    }

    /**
     * @param taxOffice the taxOffice to set
     */
    public void setTaxOffice(String taxOffice) {
        this.taxOffice = taxOffice;
    }

    /**
     * @return the street
     */
    public String getStreet() {
        return street;
    }

    /**
     * @param street the street to set
     */
    public void setStreet(String street) {
        this.street = street;
    }

	public Boolean getFoundationType() {
		return foundationType;
	}

	public void setFoundationType(Boolean foundationType) {
		this.foundationType = foundationType;
	}

	public Boolean getRelatedType() {
		return relatedType;
	}

	public void setRelatedType(Boolean relatedType) {
		this.relatedType = relatedType;
	}

	public Boolean getBankType() {
		return bankType;
	}

	public void setBankType(Boolean bankType) {
		this.bankType = bankType;
	}

	public String getDefaultNetwork() {
		return defaultNetwork;
	}

	public void setDefaultNetwork(String defaultNetwork) {
		this.defaultNetwork = defaultNetwork;
	}

	public String getDefaultEmail() {
		return defaultEmail;
	}

	public void setDefaultEmail(String defaultEmail) {
		this.defaultEmail = defaultEmail;
	}

	public String getDefaultWeb() {
		return defaultWeb;
	}

	public void setDefaultWeb(String defaultWeb) {
		this.defaultWeb = defaultWeb;
	}

	public String getDefaultGsm() {
		return defaultGsm;
	}

	public void setDefaultGsm(String defaultGsm) {
		this.defaultGsm = defaultGsm;
	}

	public String getDefaultFax() {
		return defaultFax;
	}

	public void setDefaultFax(String defaultFax) {
		this.defaultFax = defaultFax;
	}

	public String getDefaultImmobile() {
		return defaultImmobile;
	}

	public void setDefaultImmobile(String defaultImmobile) {
		this.defaultImmobile = defaultImmobile;
	}

	public String getDefaultAddress() {
		return defaultAddress;
	}

	public void setDefaultAddress(String defaultAddress) {
		this.defaultAddress = defaultAddress;
	}

}
