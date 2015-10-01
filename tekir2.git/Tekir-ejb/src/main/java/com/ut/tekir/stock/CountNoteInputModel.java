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

package com.ut.tekir.stock;

import java.io.Serializable;

import com.ut.tekir.entities.MoneySet;
import com.ut.tekir.entities.Product;
import com.ut.tekir.entities.User;

/**
 * Sayım fişi girdi bilgilerini tutan model sınıfıdır.
 * @author sinan.yumak
 */
public class CountNoteInputModel implements Serializable {
	private static final long serialVersionUID = 1L;

	private String barcode;
	private int quantity = 1;
	private Product product;
	private boolean addition = true;
	private MoneySet price;
	private User user;
	private String info;
	private String place;

	public void clearBarcode(){
		this.barcode = "";
	}
	
    public void clearInputFields() {
    	this.product = null;
    	this.quantity = 1;
    	this.barcode = "";
    	this.addition = true;
    	this.price = new MoneySet();
    	this.user = null;
    	this.info = "";
    }

	public String getBarcode() {
		return barcode;
	}

	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		if (product != null) {
			this.barcode = product.getBarcode1();
		}
		this.product = product;
	}

	public boolean isAddition() {
		return addition;
	}

	public void setAddition(boolean addition) {
		this.addition = addition;
	}

	public MoneySet getPrice() {
		if (price == null) {
			price = new MoneySet();
		}
		return price;
	}

	public void setPrice(MoneySet price) {
		this.price = price;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public String getPlace() {
		return place;
	}

	public void setPlace(String place) {
		this.place = place;
	}
	


}
