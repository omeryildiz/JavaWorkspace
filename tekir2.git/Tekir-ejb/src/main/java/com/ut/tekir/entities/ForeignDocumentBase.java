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

import java.util.List;

/**
 * Kur listesi bulunduran dökümanların ortak bilgilerini tutar.
 * @author sinan.yumak
 *
 */
public abstract class ForeignDocumentBase extends DocumentBase {

	public boolean currencyRateListContains(String currency) {
		boolean result = false;
		for (DocumentCurrencyRateBase item : getDocumentCurrencyRateList()) {
			if(item.getCurrencyPair().getHardCurrency().getCode().equals(currency)) {
				result = true;
				break;
			}
		}
		return result;
	}

	public boolean itemListContains(String currency) {
		boolean result = false;
		for (DocumentItemBase item : getDocumentItemList()) {
			if(item.getAmount().getCurrency().equals(currency)) {
				result = true;
				break;
			}
		}
		return result;
	}

	public abstract List<DocumentCurrencyRateBase> getDocumentCurrencyRateList();
	public abstract void addToCurrencyRateList(DocumentCurrencyRateBase currencyRateBase);
	public abstract void clearCurrencyRateList();
	public abstract void removeFromCurrencyRateList(int index);

	public abstract List<DocumentItemBase> getDocumentItemList();
	
	public abstract void setTotalAmount(Money totalAmount);
	
}
