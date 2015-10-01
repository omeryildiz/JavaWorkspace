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

package com.ut.tekir.report;

import java.util.Date;

import com.ut.tekir.entities.AdvanceProcessType;
import com.ut.tekir.entities.Contact;
import com.ut.tekir.entities.ContactCategory;
import com.ut.tekir.entities.ContactType;
import com.ut.tekir.entities.DocumentType;
import com.ut.tekir.entities.Organization;
import com.ut.tekir.entities.WorkBunch;

/**
 * @author rustem
 * 
 */
public class ContactStatusFilterModel {

	private String code;
	private String name;
	private ContactCategory category;
	private Date beginDate;
	private Date endDate;
	private Boolean active;
	private String companyType;
	private ContactType type;
	private String exCode1;
	private String exCode2;
	private Organization organization;
	private String docCode;
	private DocumentType documentType;
	private AdvanceProcessType processType;
	private WorkBunch workBunch;
	private Contact contact;

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

	public ContactCategory getCategory() {
		return category;
	}

	public void setCategory(ContactCategory category) {
		this.category = category;
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

	public String getDocCode() {
		return docCode;
	}

	public void setDocCode(String docCode) {
		this.docCode = docCode;
	}

	public DocumentType getDocumentType() {
		return documentType;
	}

	public void setDocumentType(DocumentType documentType) {
		this.documentType = documentType;
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

	public Contact getContact() {
		return contact;
	}

	public void setContact(Contact contact) {
		this.contact = contact;
	}

}
