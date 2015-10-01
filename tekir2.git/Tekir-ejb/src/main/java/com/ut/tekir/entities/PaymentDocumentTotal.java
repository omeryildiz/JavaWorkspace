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

package com.ut.tekir.entities;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Tahsilat-tediye fişlerinin satırlarını döviz ve iş takibine göre 
 * gruplar.
 * 
 * @author sinan.yumak
 *
 */
public class PaymentDocumentTotal {
	private Map<WorkBunchCurrency, MoneySet> data = new HashMap<WorkBunchCurrency, MoneySet>();
	private BigDecimal localTotal = BigDecimal.ZERO;

	public PaymentDocumentTotal() {
	}
	
	public PaymentDocumentTotal(Payment payment) {
		initData(payment);
	}
	
	public static PaymentDocumentTotal instance(Payment payment) {
		return new PaymentDocumentTotal(payment);
	}
	
	private void initData(Payment payment) {
		for (PaymentItem item : payment.getItems()) {
			if (item.getLineType().equals(PaymentType.Cash)) add(item);
		}
	}
	
	private void add(PaymentItem pi) {
		add(pi.getWorkBunch(), pi.getAmount());
	}
	
	public void add(WorkBunch bunch, MoneySet amount) {
		WorkBunchCurrency wbc = new WorkBunchCurrency(bunch, amount.getCurrency());
		MoneySet item = data.get(wbc);

		if (item == null) {
			item = new MoneySet(amount);
			data.put(wbc, item);
		} else {
			item.add(amount);
		}
		localTotal = localTotal.add(amount.getLocalAmount());
	}

	public BigDecimal getLocalTotal() {
		return localTotal;
	}

	public Map<WorkBunchCurrency, MoneySet> getTotals() {
		return data;
	}

}
