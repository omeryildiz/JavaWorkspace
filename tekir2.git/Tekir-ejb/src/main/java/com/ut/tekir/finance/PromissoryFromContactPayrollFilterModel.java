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

import com.ut.tekir.entities.Contact;
import com.ut.tekir.framework.DefaultDocumentFilterModel;

/**
 * 
 * @author burak nebioglu
 */
public class PromissoryFromContactPayrollFilterModel extends
		DefaultDocumentFilterModel {

	private Long id;
	private Date date;
	private String info;
	private String contactName;
	private String contactCode;
	private String contactCaption;
	private String company;
	private Boolean person;

	private Contact contact;

	public Contact getContact() {
		return contact;
	}

	public void setContact(Contact contact) {
		this.contact = contact;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public String getContactName() {
		return contactName;
	}

	public void setContactName(String contactName) {
		this.contactName = contactName;
	}

	public String getContactCode() {
		return contactCode;
	}

	public void setContactCode(String contactCode) {
		this.contactCode = contactCode;
	}

	public String getContactCaption() {
        return "[" + getContactCode() + "] " + getContactName();
	}

	public void setContactCaption(String contactCaption) {
		this.contactCaption = contactCaption;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public Boolean getPerson() {
		return person;
	}

	public void setPerson(Boolean person) {
		this.person = person;
	}
}
