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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.ut.tekir.entities.Contact;
import com.ut.tekir.entities.Payment;

/**
 * 
 * @author sinan.yumak
 * 
 */
public class MapperPaymentModel {
	private Payment payment;
	private List<MapperPaymentItemModel> items;

	public MapperPaymentModel() {
	}
	
	public MapperPaymentModel(Payment py) {
		this.payment = py;
	}

	public boolean hasRemainingAmount() {
		return BigDecimal.ZERO.compareTo(getRemainingAmount()) < 0;
	}

	public Payment getPayment() {
		return payment;
	}

	public Contact getContact() {
		return payment != null ? payment.getContact() : null;
	}
	
	public void setPayment(Payment payment) {
		this.payment = payment;
	}

	public BigDecimal getRemainingAmount() {
		BigDecimal remainingTotal = BigDecimal.ZERO;
		for (MapperPaymentItemModel item : items) {
			remainingTotal = remainingTotal.add(item.getRemainingAmount().getLocalAmount());
		}
		return remainingTotal;
	}

	public List<MapperPaymentItemModel> getItems() {
		if (items == null) {
			items = new ArrayList<MapperPaymentItemModel>();
		}
		return items;
	}

	public void setItems(List<MapperPaymentItemModel> items) {
		this.items = items;
	}

}
