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
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;
import org.hibernate.validator.Valid;

/**
 * Entity class Contact
 * 
 * @author haky
 */
@Entity
@Table(name="CONTACT")
public class Contact extends AuditBase implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE,generator="genericSeq")
    @Column(name="ID")
    private Long id;
	
    @Column(name="CODE", nullable=false, unique=true, length=20)
    @NotNull
    @Length(max=20, min=1)
    private String code;
    
    @Column(name="NAME", length=30)
    @Length(max=30)
    private String name;

    @Column(name="MIDNAME", length=30)
    @Length(max=30)
    private String midname;

    @Column(name="SURNAME", length=30)
    @Length(max=30)
    private String surname;

    @Column(name="FULLNAME", length=92)
    @Length(max=92)
    private String fullname;

    @Column(name="INFO")
    private String info;
    
    @Column(name="OPEN_DATE")
    @Temporal(TemporalType.DATE)
    private Date openDate = new Date();
    
    @Column(name="ISACTIVE")
    private Boolean active = Boolean.TRUE;
  
    @Column(name="ALL_TYPE")
    private Boolean allType = Boolean.FALSE;

    @Column(name="CUSTOMER_TYPE")
    private Boolean customerType = Boolean.FALSE;
    
    @Column(name="PROVIDER_TYPE")
    private Boolean providerType = Boolean.FALSE;

    @Column(name="AGENT_TYPE")
    private Boolean agentType = Boolean.FALSE;

    @Column(name="PERSONNEL_TYPE")
    private Boolean personnelType = Boolean.FALSE;
    
    @Column(name="BRANCH_TYPE")
    private Boolean branchType = Boolean.FALSE;
    
    @Column(name="CONTACT_TYPE")
    private Boolean contactType = Boolean.FALSE;

    @Column(name="BANK_TYPE")
    private Boolean bankType = Boolean.FALSE;

    @Column(name="FOUNDATION_TYPE")
    private Boolean foundationType = Boolean.FALSE;

    @Column(name="RELATED_TYPE")
    private Boolean relatedType = Boolean.FALSE;
    
    @ManyToOne
    @JoinColumn(name="CONTACT_CATEGORY_ID")
    private ContactCategory category;
    
    @Column(name="SYSTEM")
    private Boolean system;
    
    /**
     * Kaydın kişi mi kurum mu olduğu
     */
    @Column(name="PERSON")
    private Boolean person = Boolean.FALSE;
    
    @Column(name="REPRESENTATIVE", length=30)
    @Length(max=30)
    private String representative;
    
    @Column(name="COMPANY", length=250)
    @Length(max=250)
    private String company;
    
    @Column(name="TITLE", length=30)
    @Length(max=30)
    private String title;
                
    @Column(name="TAX_NUMBER", length=20)
    @Length(max=20)
    private String taxNumber; 
    
    @Column(name="TAX_OFFICE", length=20)
    @Length(max=20)
    private String taxOffice;
    
    @Column(name="SSN", length=20)
    @Length(max=20)
    private String ssn; /*Türkçesi TC Kimlik No*/
    
    @Column(name="EXCODE1", length=10)
    @Length(max=10)
    private String exCode1;
    
    @Column(name="EXCODE2", length=10)
    @Length(max=10)
    private String exCode2;

    @Column(name="DEBIT_LIMIT", precision=10, scale=2)
    private BigDecimal debitLimit = BigDecimal.ZERO;
    
    @Column(name="RISK_LIMIT", precision=10, scale=2)
    private BigDecimal riskLimit = BigDecimal.ZERO;

    @ManyToOne
    @JoinColumn(name="ORGANIZATION_ID")
    private Organization organization;
    
	/**
	 * Kişisel bilgiler
	 */
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="OWN_COMPANY_ID")
    private Contact ownCompany; /* Bağlı olduğu kurumun cari kodu, id */

    @Column(name="START_WORKING_DATE")
    @Temporal(TemporalType.DATE)
    private Date startWorkingDate;
	
    @Column(name="END_WORKING_DATE")
    @Temporal(TemporalType.DATE)
    private Date endWorkingDate;

    @Column(name="ISACTIVE_PERSONEL") /* Aktif çalışan mı */
    private Boolean activePersonel = Boolean.TRUE;
	
    @Column(name="SGK_NUMBER", length=20)
    @Length(max=20)
	private String sgkNumber; /* SSK, Bag, Emekli sandık no */
	
    @Column(name="BIRTH_DATE")
    @Temporal(TemporalType.DATE)
    private Date birthdate;

    @Column(name="BLOOD_GROUP", length=6)
    @Length(max=6)
	private String bloodGroup; /* 0-, 0+ A- A+ vb */
	
    @Column(name="FATHER_NAME", length=30)
    @Length(max=30)
	private String fatherName; 
	
    @Column(name="gender")
    @Enumerated(EnumType.ORDINAL)
    private GenderType gender;

    @Column(name="education")
    @Enumerated(EnumType.ORDINAL)
    private EducationType education;

	/**
	 * Finansal bilgiler
	 */
    @Column(name="PASSPORT_NUMBER")
    private String passportNo;

    @Column(name="MARITAL_STATUS")
    @Enumerated(EnumType.ORDINAL)
    private MaritalStatus maritalStatus;

    @Column(name="SPOUSE_INCOME_STAT")
    @Enumerated(EnumType.ORDINAL)
    private SpouseIncomeStatus spouseIncomeStatus;

    @Column(name="CHILD_NUMBER")
    private Integer childNumber;

    // Bakmakla yukumlu oldugu kisi sayisi
    @Column(name="DEPENDENTS")
    private Integer dependents;

    // Brut maas
    @Embedded
	@Valid
	@AttributeOverrides({
		@AttributeOverride(name="localAmount", column=@Column(name="LCYVAL")),
		@AttributeOverride(name="currency", column=@Column(name="CCY")),
		@AttributeOverride(name="value", column=@Column(name="CCYVAL"))
	})
	private MoneySet grossSalary = new MoneySet();

    //tevkifat(stopaj) yapılacak mı?
    @Column(name="HAS_WITHHOLDING")
    private Boolean hasWithholding = Boolean.FALSE;
    
    // butun bolgeler tarafindan goruntulenecek mi bilgisi.
    @Column(name="IS_PUBLIC")
    private Boolean isPublic = Boolean.FALSE;
    
    //bu contact icin upload edilen dosya listesi
    @OneToMany(mappedBy="contact",cascade=CascadeType.ALL )
    @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    private List<DocumentFile> documentFiles = new ArrayList<DocumentFile>();

    @OneToMany(mappedBy="owner",cascade=CascadeType.ALL )
    @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    private List<ContactBankAccount> bankAccountList= new ArrayList<ContactBankAccount>();

    @OneToMany(mappedBy="owner",cascade=CascadeType.ALL )
    @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    @OrderBy("defaultAddress DESC,activeAddress DESC")
    private List<ContactAddress> addressList= new ArrayList<ContactAddress>();

    @OneToMany(mappedBy="owner",cascade=CascadeType.ALL )
    @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    @OrderBy("defaultPhone DESC,activePhone DESC")
    private List<ContactPhone> phoneList= new ArrayList<ContactPhone>();

    @OneToMany(mappedBy="ownCompany",cascade=CascadeType.ALL,fetch=FetchType.LAZY )
    @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    private List<Contact> personelList= new ArrayList<Contact>();

    @OneToMany(mappedBy="owner",cascade=CascadeType.ALL)
    @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    @OrderBy("defaultNetwork DESC,activeNetwork DESC")
    private List<ContactNetwork> networkList= new ArrayList<ContactNetwork>();

    /**
     * Cariye has indirim bilgilerini tutar.
     */
    @Embedded
    @Valid
    @AttributeOverrides( {
    	@AttributeOverride(name="percentage", column=@Column(name="DISCOUNT_PERCENTAGE")),
    	@AttributeOverride(name="rate", column=@Column(name="DISCOUNT_RATE")),
        @AttributeOverride(name="currency", column=@Column(name="DISCOUNT_CCY")),
        @AttributeOverride(name="value",    column=@Column(name="DISCOUNT_VALUE")),
        @AttributeOverride(name="localAmount",    column=@Column(name="DISCOUNT_LCYVAL"))
    })
	private DiscountOrExpense discount = new DiscountOrExpense();

    /**
     * cariye has indirim olup olmayacağı bilgisini tutar.
     */
    @Column(name="HAS_DISCOUNT")
    private Boolean hasDiscount = Boolean.FALSE;

    /**
     * Faturada kullanılan ödeme planı bilgisini tutar.
     */
    @ManyToOne
    @JoinColumn(name="PAYMENT_PLAN_ID")
    @ForeignKey(name="FK_CONTACT_PAYMENTPLANID")
    private PaymentPlan paymentPlan;

    @Enumerated(EnumType.ORDINAL)
    @Column(name="CURRENCY_RATE_TYPE")
    private CurrencyRateType currencyRateType = CurrencyRateType.Ask;
    
    //TODO:Carinin açılış tarihini raporlama amacıyla eklemek gerek!
	@Override
    public int hashCode() {
        int hash = 0;
        hash += (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Contact)) {
            return false;
        }
        Contact other = (Contact)object;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) return false;
        return true;
    }

    @Override
    public String toString() {
        return "com.ut.tekir.entities.Contact[id=" + id + "]";
    }

    //FIXME: bu metotlar yerine Contact helper sınıflarını kullanmalı.
    public ContactAddress getDefaultAddress() {
    	for (ContactAddress item : addressList) {
    		if (item.getDefaultAddress()) return item;
    	}
    	return null;
    }

    public ContactPhone getDefaultPhone() {
    	for (ContactPhone item : phoneList) {
    		if (item.getDefaultPhone()) return item;
    	}
    	return null;
    }
    
	public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    public Boolean getIsPublic() {
		return isPublic;
	}

	public void setIsPublic(Boolean isPublic) {
		this.isPublic = isPublic;
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

	public Organization getOrganization() {
		return organization;
	}

	public void setOrganization(Organization organization) {
		this.organization = organization;
	}

	public List<ContactNetwork> getNetworkList() {
		return networkList;
	}

	public void setNetworkList(List<ContactNetwork> networkList) {
		this.networkList = networkList;
	}

	public List<Contact> getPersonelList() {
		return personelList;
	}

	public void setPersonelList(List<Contact> personelList) {
		this.personelList = personelList;
	}

	public List<ContactPhone> getPhoneList() {
		return phoneList;
	}

	public void setPhoneList(List<ContactPhone> phoneList) {
		this.phoneList = phoneList;
	}

    public Boolean getHasWithholding() {
		return hasWithholding;
	}

	public void setHasWithholding(Boolean hasWithholding) {
		this.hasWithholding = hasWithholding;
	}

	public EducationType getEducation() {
		return education;
	}

	public void setEducation(EducationType education) {
		this.education = education;
	}

	public List<DocumentFile> getDocumentFiles() {
		return documentFiles;
	}

	public void setDocumentFiles(List<DocumentFile> documentFiles) {
		this.documentFiles = documentFiles;
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

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public ContactCategory getCategory() {
        return category;
    }

    public void setCategory(ContactCategory category) {
        this.category = category;
    }

    public Boolean getSystem() {
        return system;
    }

    public void setSystem(Boolean system) {
        this.system = system;
    }

    public Boolean getPerson() {
        return person;
    }

    public void setPerson(Boolean person) {
        this.person = person;
    }

    public String getRepresentative() {
        return representative;
    }

    public void setRepresentative(String representative) {
        this.representative = representative;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTaxNumber() {
        return taxNumber;
    }

    public void setTaxNumber(String taxNumber) {
        this.taxNumber = taxNumber;
    }

    public String getTaxOffice() {
        return taxOffice;
    }

    public void setTaxOffice(String taxOffice) {
        this.taxOffice = taxOffice;
    }

    public String getSsn() {
        return ssn;
    }

    public void setSsn(String ssn) {
        this.ssn = ssn;
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

    public String getCaption(){
    	if ( getPerson() ) {
    		return  "[" + getCode() + "] " + getFullname();
    	} else return  "[" + getCode() + "] " + getCompany();        
    }

    public BigDecimal getDebitLimit() {
		return debitLimit;
	}

	public void setDebitLimit(BigDecimal debitLimit) {
		this.debitLimit = debitLimit;
	}

	public BigDecimal getRiskLimit() {
		return riskLimit;
	}

	public void setRiskLimit(BigDecimal riskLimit) {
		this.riskLimit = riskLimit;
	}

	public Contact getOwnCompany() {
		return ownCompany;
	}

	public void setOwnCompany(Contact ownCompany) {
		this.ownCompany = ownCompany;
	}

	public Date getStartWorkingDate() {
		return startWorkingDate;
	}

	public void setStartWorkingDate(Date startWorkingDate) {
		this.startWorkingDate = startWorkingDate;
	}

	public Date getEndWorkingDate() {
		return endWorkingDate;
	}

	public void setEndWorkingDate(Date endWorkingDate) {
		this.endWorkingDate = endWorkingDate;
	}

	public Boolean getActivePersonel() {
		return activePersonel;
	}

	public void setActivePersonel(Boolean activePersonel) {
		this.activePersonel = activePersonel;
	}

	public String getSgkNumber() {
		return sgkNumber;
	}

	public void setSgkNumber(String sgkNumber) {
		this.sgkNumber = sgkNumber;
	}

	public Date getBirthdate() {
		return birthdate;
	}

	public void setBirthdate(Date birthdate) {
		this.birthdate = birthdate;
	}

	public String getBloodGroup() {
		return bloodGroup;
	}

	public void setBloodGroup(String bloodGroup) {
		this.bloodGroup = bloodGroup;
	}

	public String getFatherName() {
		return fatherName;
	}

	public void setFatherName(String fatherName) {
		this.fatherName = fatherName;
	}

	public GenderType getGender() {
		return gender;
	}

	public void setGender(GenderType gender) {
		this.gender = gender;
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

    /**
     * @return the passportNo
     */
    public String getPassportNo() {
        return passportNo;
    }

    /**
     * @param passportNo the passportNo to set
     */
    public void setPassportNo(String passportNo) {
        this.passportNo = passportNo;
    }

    /**
     * @return the maritalStatus
     */
    public MaritalStatus getMaritalStatus() {
        return maritalStatus;
    }

    /**
     * @param maritalStatus the maritalStatus to set
     */
    public void setMaritalStatus(MaritalStatus maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    /**
     * @return the spouseIncomeStatus
     */
    public SpouseIncomeStatus getSpouseIncomeStatus() {
        return spouseIncomeStatus;
    }

    /**
     * @param spouseIncomeStatus the spouseIncomeStatus to set
     */
    public void setSpouseIncomeStatus(SpouseIncomeStatus spouseIncomeStatus) {
        this.spouseIncomeStatus = spouseIncomeStatus;
    }

    /**
     * @return the childNumber
     */
    public Integer getChildNumber() {
        return childNumber;
    }

    /**
     * @param childNumber the childNumber to set
     */
    public void setChildNumber(Integer childNumber) {
        this.childNumber = childNumber;
    }

    /**
     * @return the dependents
     */
    public Integer getDependents() {
        return dependents;
    }

    /**
     * @param dependents the dependents to set
     */
    public void setDependents(Integer dependents) {
        this.dependents = dependents;
    }

    /**
     * @return the grossSalary
     */
    public MoneySet getGrossSalary() {
    	if ( grossSalary == null ){
    		grossSalary = new MoneySet(); 
    	}
        return grossSalary;
    }

    /**
     * @param grossSalary the grossSalary to set
     */
    public void setGrossSalary(MoneySet grossSalary) {
        this.grossSalary = grossSalary;
    }

	public List<ContactAddress> getAddressList() {
		return addressList;
	}

	public List<ContactAddress> getActiveAddressList() {
		List<ContactAddress> actAddrList = new ArrayList<ContactAddress>();
		for(ContactAddress ca : addressList ){
			if(ca.getActiveAddress()) actAddrList.add(ca);
		}
		return actAddrList;
	}
	
	public void setAddressList(List<ContactAddress> addressList) {
		this.addressList = addressList;
	}

	public void setOpenDate(Date openDate) {
		this.openDate = openDate;
	}

	public Date getOpenDate() {
		return openDate;
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

	public Boolean getHasDiscount() {
		return hasDiscount;
	}

	public void setHasDiscount(Boolean hasDiscount) {
		this.hasDiscount = hasDiscount;
	}

	public List<ContactBankAccount> getBankAccountList() {
		return bankAccountList;
	}

	public void setBankAccountList(List<ContactBankAccount> bankAccountList) {
		this.bankAccountList = bankAccountList;
	}

	public PaymentPlan getPaymentPlan() {
		return paymentPlan;
	}

	public void setPaymentPlan(PaymentPlan paymentPlan) {
		this.paymentPlan = paymentPlan;
	}

	public CurrencyRateType getCurrencyRateType() {
		return currencyRateType;
	}

	public void setCurrencyRateType(CurrencyRateType currencyRateType) {
		this.currencyRateType = currencyRateType;
	}

	public Boolean getBankType() {
		return bankType;
	}

	public void setBankType(Boolean bankType) {
		this.bankType = bankType;
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
	
}
