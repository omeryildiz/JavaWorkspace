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

package com.ut.tekir.finance;

import java.util.Date;

import com.ut.tekir.entities.AdvanceProcessType;
import com.ut.tekir.entities.Contact;
import com.ut.tekir.entities.ContactCategory;
import com.ut.tekir.entities.ContactType;
import com.ut.tekir.entities.DocumentType;
import com.ut.tekir.entities.Organization;
import com.ut.tekir.entities.WorkBunch;

/**
 *
 * @author haky
 */
public class FinanceTxnFilterModel {

    
    private String  serial;
    private String  reference;
    private String  code;
    private Date    beginDate;
    private Date    endDate;
    
	private String contactName;
	private String contactCode;
	private Boolean active;
    
    private Boolean localCurrencyOnly;
    private DocumentType documentType;
    
    private String companyType;
    private ContactType type;
    private ContactCategory category;
    private String exCode1;
    private String exCode2;
    private Organization organization;
    private AdvanceProcessType processType;
    private Contact contact;
    private WorkBunch workBunch;
    private String currency;

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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

    public Boolean getLocalCurrencyOnly() {
        return localCurrencyOnly;
    }

    public void setLocalCurrencyOnly(Boolean localCurrencyOnly) {
        this.localCurrencyOnly = localCurrencyOnly;
    }

    public DocumentType getDocumentType() {
        return documentType;
    }

    public void setDocumentType(DocumentType documentType) {
        this.documentType = documentType;
    }

	public void setContactName(String contactName) {
		this.contactName = contactName;
	}

	public String getContactName() {
		return contactName;
	}

	public void setContactCode(String contactCode) {
		this.contactCode = contactCode;
	}

	public String getContactCode() {
		return contactCode;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public String getCompanyType() {
		return companyType;
	}

	public void setCompanyType(String companyType) {
		this.companyType = companyType;
	}

	public ContactType getType() {
		return type;
	}

	public void setType(ContactType type) {
		this.type = type;
	}

	public ContactCategory getCategory() {
		return category;
	}

	public void setCategory(ContactCategory category) {
		this.category = category;
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

	public Organization getOrganization() {
		return organization;
	}

	public void setOrganization(Organization organization) {
		this.organization = organization;
	}

	public Contact getContact() {
		return contact;
	}

	public void setContact(Contact contact) {
		this.contact = contact;
	}

	public WorkBunch getWorkBunch() {
		return workBunch;
	}

	public void setWorkBunch(WorkBunch workBunch) {
		this.workBunch = workBunch;
	}

	public AdvanceProcessType getProcessType() {
		return processType;
	}

	public void setProcessType(AdvanceProcessType processType) {
		this.processType = processType;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}	
    
}
