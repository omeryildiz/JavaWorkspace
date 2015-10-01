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

package com.ut.tekir.invoice.yeni;

import java.util.Date;
import java.util.List;

import com.ut.tekir.entities.Contact;
import com.ut.tekir.entities.Product;
import com.ut.tekir.entities.ReturnInvoiceStatus;
import com.ut.tekir.entities.TradeAction;

/**
 * @author sinan.yumak
 *
 */
public class InvoiceSuggestionFilterModel {

	private Contact contact;
	private TradeAction tradeAction;
	private List<ReturnInvoiceStatus> retInvoiceStatus;
	private Product product;
	private Date beginDate;
	private Date endDate;
	private String serial;
	private String reference;
	private Boolean matchingFinished = false;

	public Contact getContact() {
		return contact;
	}

	public void setContact(Contact contact) {
		this.contact = contact;
	}

	public TradeAction getTradeAction() {
		return tradeAction;
	}

	public void setTradeAction(TradeAction tradeAction) {
		this.tradeAction = tradeAction;
	}

	public List<ReturnInvoiceStatus> getRetInvoiceStatus() {
		return retInvoiceStatus;
	}

	public void setRetInvoiceStatus(List<ReturnInvoiceStatus> retInvoiceStatus) {
		this.retInvoiceStatus = retInvoiceStatus;
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

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public Boolean getMatchingFinished() {
		return matchingFinished;
	}

	public void setMatchingFinished(Boolean matchingFinished) {
		this.matchingFinished = matchingFinished;
	}

}
