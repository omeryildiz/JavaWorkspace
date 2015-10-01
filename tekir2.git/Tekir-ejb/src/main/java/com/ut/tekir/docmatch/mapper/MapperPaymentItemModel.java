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

import com.ut.tekir.entities.MoneySet;
import com.ut.tekir.entities.PaymentItem;

/**
 * 
 * @author sinan.yumak
 * 
 */
public class MapperPaymentItemModel {
	private PaymentItem paymentItem;
	private MoneySet remainingAmount = new MoneySet();
	
	public MapperPaymentItemModel() {
	}
	
	public MapperPaymentItemModel(PaymentItem pi, BigDecimal usedAmount) {
		this.paymentItem = pi;
		this.remainingAmount.setCurrency(pi.getAmount().getCurrency());
		this.remainingAmount.setLocalAmount(pi.getAmount().getLocalAmount().subtract(usedAmount));
	}

	public boolean hasRemainingAmount() {
		return BigDecimal.ZERO.compareTo(remainingAmount.getValue()) < 0;
	}
	
	public boolean isMatchingFinished() {
		return paymentItem.getMatchingFinished();
	}
	
	public PaymentItem getPaymentItem() {
		return paymentItem;
	}

	public void setPaymentItem(PaymentItem paymentItem) {
		this.paymentItem = paymentItem;
	}

	public MoneySet getRemainingAmount() {
		return remainingAmount;
	}

	public void setRemainingAmount(MoneySet remainingAmount) {
		this.remainingAmount = remainingAmount;
	}

}
