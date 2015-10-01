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

import com.ut.tekir.entities.Contact;
import com.ut.tekir.entities.Product;
import com.ut.tekir.entities.User;
import com.ut.tekir.entities.WorkBunch;

/**
 * @author rustem
 *
 */
public class ContactServiceAcquiredFilterModel {
	
	private Contact contact;
	private Date beginDate;
	private Date endDate;
	private String cashier;
	private User clerk;
	private String barcode1;
	private Product product;
	private WorkBunch workBunch;
	
	
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
	public String getCashier() {
		return cashier;
	}
	public void setCashier(String cashier) {
		this.cashier = cashier;
	}
	public User getClerk() {
		return clerk;
	}
	public void setClerk(User clerk) {
		this.clerk = clerk;
	}
	/**
	 * @return the barcode
	 */
	public String getBarcode1() {
		return barcode1;
	}
	/**
	 * @param barcode the barcode to set
	 */
	public void setBarcode1(String barcode1) {
		this.barcode1 = barcode1;
	}
	/**
	 * @return the product
	 */
	public Product getProduct() {
		return product;
	}
	/**
	 * @param product the product to set
	 */
	public void setProduct(Product product) {
		this.product = product;
	}
	public WorkBunch getWorkBunch() {
		return workBunch;
	}
	public void setWorkBunch(WorkBunch workBunch) {
		this.workBunch = workBunch;
	}
	
}
