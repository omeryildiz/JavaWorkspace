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
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Fiş dip toplamlarını sağlamak için generic bir araç.
 * 
 * Money setle birlikte yapısı kolaylaştı.
 * 
 * @author haky
 */
public class DocumentTotal {

	private Map<String, MoneySet> data = new HashMap<String, MoneySet>();
	private BigDecimal localTotal = BigDecimal.ZERO;

	public void add(MoneySet amount) {
		MoneySet item = data.get(amount.getCurrency());

		if (item == null) {
			item = new MoneySet(amount);
			data.put(item.getCurrency(), item);
		} else {
			item.setValue(item.getValue().add(amount.getValue()));
			item.setLocalAmount(item.getLocalAmount().add(
					amount.getLocalAmount()));
		}

		localTotal = localTotal.add(amount.getLocalAmount());
	}

	public BigDecimal getLocalTotal() {
		return localTotal;
	}

	public Map<String, MoneySet> getTotalMap() {
		return data;
	}

	public Collection<MoneySet> getTotals() {
		return data.values();
	}
}