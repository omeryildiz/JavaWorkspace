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

package com.ut.tekir.docmatch.mapper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.ut.tekir.entities.Contact;
import com.ut.tekir.entities.FinanceAction;

/**
 * 
 * @author sinan.yumak
 * 
 */
public class MapperModel {

	private List<MapperInvoiceModel> invoices;
	private List<MapperPaymentModel> payments;
	private Contact contact;
	private Date beginDate;
	private Date endDate;
	private FinanceAction action;
	
	public void clear() {
		getInvoices().clear();
		getPayments().clear();
	}

	public void add(MapperInvoiceModel model) {
		getInvoices().add(model);
	}

	public void add(MapperPaymentModel model) {
		getPayments().add(model);
	}
	
	public List<MapperInvoiceModel> getInvoices() {
		if (invoices == null) {
			invoices = new ArrayList<MapperInvoiceModel>();
		}
		return invoices;
	}

	public void setInvoices(List<MapperInvoiceModel> invoices) {
		this.invoices = invoices;
	}

	public Contact getContact() {
		return contact;
	}

	public void setContact(Contact contact) {
		this.contact = contact;
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

	public FinanceAction getAction() {
		return action;
	}

	public void setAction(FinanceAction action) {
		this.action = action;
	}

	public List<MapperPaymentModel> getPayments() {
		if (payments == null) {
			payments = new ArrayList<MapperPaymentModel>();
		}
		return payments;
	}

	public void setPayments(List<MapperPaymentModel> payments) {
		this.payments = payments;
	}
}
