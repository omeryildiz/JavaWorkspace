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
import java.util.Date;

import com.ut.tekir.entities.Contact;
import com.ut.tekir.entities.DocumentType;
import com.ut.tekir.entities.Organization;
import com.ut.tekir.entities.WorkBunch;
import com.ut.tekir.entities.MoneySet;
import com.ut.tekir.entities.TradeAction;
import com.ut.tekir.entities.Warehouse;

/**
 * @author sinan.yumak
 * 
 */
public class InvoiceFilterModel extends DefaultDocumentFilterModel {

	private String contactCode;
	private String contactName;
	private String contactCompany;
	private boolean contactPerson;
	private Contact contact;
	private Warehouse warehouse;
	private String serial;
	private String reference;
	private String code;
	private Long id;
	private DocumentType documentType;
	private TradeAction tradeAction;
	private Date date;
	private String info;
	private MoneySet totalBeforeTax = new MoneySet();
	private MoneySet totalTax = new MoneySet();
	private MoneySet grandTotal = new MoneySet();
	private Boolean returned;
	private Boolean proformaInvoice;
	private Boolean active;
	private WorkBunch workBunch;
	private Organization organization;
	
	public String getContactCaption() {
		return contactPerson ? "[" + getContactCode() + "] " + getContactName() : getContactCompany();
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

	public String getContactCompany() {
		return contactCompany;
	}

	public void setContactCompany(String contactCompany) {
		this.contactCompany = contactCompany;
	}

	public boolean isContactPerson() {
		return contactPerson;
	}

	public void setContactPerson(boolean contactPerson) {
		this.contactPerson = contactPerson;
	}

	public Contact getContact() {
		return contact;
	}

	public void setContact(Contact contact) {
		this.contact = contact;
	}

	public Warehouse getWarehouse() {
		return warehouse;
	}

	public void setWarehouse(Warehouse warehouse) {
		this.warehouse = warehouse;
	}

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

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public DocumentType getDocumentType() {
		return documentType;
	}

	public void setDocumentType(DocumentType documentType) {
		this.documentType = documentType;
	}

	public TradeAction getTradeAction() {
		return tradeAction;
	}

	public void setTradeAction(TradeAction tradeAction) {
		this.tradeAction = tradeAction;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public MoneySet getTotalBeforeTax() {
		return totalBeforeTax;
	}

	public void setTotalBeforeTax(MoneySet totalBeforeTax) {
		this.totalBeforeTax = totalBeforeTax;
	}
	
	public void setTotalBeforeTaxValue(BigDecimal value) {
		this.totalBeforeTax.setValue(value);
	}

	public void setTotalBeforeTaxLocalAmount(BigDecimal localAmount) {
		this.totalBeforeTax.setLocalAmount(localAmount);
	}

	public void setTotalTaxValue(BigDecimal value) {
		this.totalTax.setValue(value);
	}

	public void setTotalTaxLocalAmount(BigDecimal localAmount) {
		this.totalTax.setLocalAmount(localAmount);
	}
	
	public void setGrandTotalValue(BigDecimal value) {
		this.grandTotal.setValue(value);
	}

	public void setTotalBeforeTaxCurrency(String currency) {
		this.totalBeforeTax.setCurrency(currency);
	}
	
	public void setTotalTaxCurrency(String currency) {
		this.totalTax.setCurrency(currency);
	}
	
	public void setGrandTotalCurrency(String currency) {
		this.grandTotal.setCurrency(currency);
	}
	
	public void setGrandTotalLocalAmount(BigDecimal localAmount){
		this.grandTotal.setLocalAmount(localAmount);
	}

	public MoneySet getTotalTax() {
		return totalTax;
	}

	public void setTotalTax(MoneySet totalTax) {
		this.totalTax = totalTax;
	}

	public MoneySet getGrandTotal() {
		return grandTotal;
	}

	public void setGrandTotal(MoneySet grandTotal) {
		this.grandTotal = grandTotal;
	}

	public Boolean getReturned() {
		return returned;
	}

	public void setReturned(Boolean returned) {
		this.returned = returned;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public Boolean getProformaInvoice() {
		return proformaInvoice;
	}

	public void setProformaInvoice(Boolean proformaInvoice) {
		this.proformaInvoice = proformaInvoice;
	}

	public WorkBunch getWorkBunch() {
		return workBunch;
	}

	public void setWorkBunch(WorkBunch workBunch) {
		this.workBunch = workBunch;
	}

	public void setOrganization(Organization organization) {
		this.organization = organization;
	}

	public Organization getOrganization() {
		return organization;
	}

}
