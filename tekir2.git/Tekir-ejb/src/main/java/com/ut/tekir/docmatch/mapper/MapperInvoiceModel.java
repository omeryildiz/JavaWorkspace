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

import com.ut.tekir.entities.Contact;
import com.ut.tekir.entities.inv.TekirInvoice;


/**
 * 
 * @author sinan.yumak
 *
 */
public class MapperInvoiceModel {
	private boolean selected = true;
	private TekirInvoice invoice;
	private BigDecimal remainingAmount = BigDecimal.ZERO;
	
	public MapperInvoiceModel() {
	}

	public MapperInvoiceModel(TekirInvoice inv) {
		this.invoice = inv;
	}

	public MapperInvoiceModel(TekirInvoice inv, BigDecimal usedAmount) {
		this.invoice = inv;
		this.remainingAmount = inv.getGrandTotal().getValue().subtract(usedAmount);
	}

	public Contact getContact() {
		return invoice != null ? invoice.getContact() : null;
	}
	
	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public TekirInvoice getInvoice() {
		return invoice;
	}

	public void setInvoice(TekirInvoice invoice) {
		this.invoice = invoice;
	}

	public BigDecimal getRemainingAmount() {
		
		return remainingAmount;
	}

	public void setRemainingAmount(BigDecimal remainingAmount) {
		this.remainingAmount = remainingAmount;
	}

	public boolean hasRemainingAmount() {
		return BigDecimal.ZERO.compareTo(remainingAmount) < 0;
	}

}
