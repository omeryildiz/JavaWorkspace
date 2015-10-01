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

package com.ut.tekir.framework;

import java.math.BigDecimal;

import com.ut.tekir.entities.AdvanceProcessType;
import com.ut.tekir.entities.DocumentType;
import com.ut.tekir.entities.Organization;

/**
*
*	İrsaliye Listesini doldurmak için kullanılan alanlardan oluşan model.
*   Borclu raporunda kullanilir.
*
* @author bilga
*/

public class ContactReportModel {

	private String contactCode;
	private String contactName;
	private Boolean person;
	private String company;
	private String ccy;
	private BigDecimal ccyval;
	private BigDecimal lcyval;
	private BigDecimal totalval;
	private BigDecimal avgrate;
	private Boolean active;
	private Organization organization;
	private String docCode;
	private DocumentType documentType;
	private AdvanceProcessType processType;
	
	
	public ContactReportModel(){}
	
	public ContactReportModel(Object[] obj) {
		this.contactCode = (String) obj[0];
		this.contactName = (String) obj[1];
		this.company = (String) obj[2];
		this.person = (Boolean) obj [3];
		this.ccy = (String) obj[4];
		this.lcyval = (BigDecimal) obj[5];
		this.totalval = (BigDecimal) obj[6];
		this.avgrate = (BigDecimal) obj[7];
	}

	
	
	public Boolean getPerson() {
		return person;
	}

	public void setPerson(Boolean person) {
		this.person = person;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getContactCode() {
		return contactCode;
	}

	public void setContactCode(String contactCode) {
		this.contactCode = contactCode;
	}

	public String getContactName() {
		return contactName;
	}

	public void setContactName(String contactName) {
		this.contactName = contactName;
	}

	public String getCcy() {
		return ccy;
	}

	public void setCcy(String ccy) {
		this.ccy = ccy;
	}

	public BigDecimal getLcyval() {
		return lcyval;
	}

	public void setLcyval(BigDecimal lcyval) {
		this.lcyval = lcyval;
	}

	public BigDecimal getTotalval() {
		return totalval;
	}

	public void setTotalval(BigDecimal totalval) {
		this.totalval = totalval;
	}

	public BigDecimal getCcyval() {
		return ccyval;
	}

	public void setCcyval(BigDecimal ccyval) {
		this.ccyval = ccyval;
	}

	public BigDecimal getAvgrate() {
		return avgrate;
	}

	public void setAvgrate(BigDecimal avgrate) {
		this.avgrate = avgrate;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
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

	public AdvanceProcessType getProcessType() {
		return processType;
	}

	public void setProcessType(AdvanceProcessType processType) {
		this.processType = processType;
	}

	
}
