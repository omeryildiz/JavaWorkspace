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

import java.util.Date;
import java.util.List;

/**
 * @author sinan.yumak
 *
 */
public interface ITender {

	Date getDate();

	List<? extends TenderDetailBase> getItems();
	
	List<? extends TenderDetailBase> getProductItems();

	List<? extends TenderDetailBase> getDocumentDiscountItems();

	List<? extends TenderDetailBase> getDocumentExpenseItems();

	List<? extends TenderDetailBase> getDiscountAdditionItems();

	List<? extends TenderDetailBase> getExpenseAdditionItems();
	
	List<? extends TenderDetailBase> getDocumentFeeItems();
	
	List<? extends TenderTaxSummaryBase> getTaxSummaryList();

	List<? extends TenderCurrencySummaryBase> getCurrencySummaryList();

	List<? extends TenderCurrencyRateBase> getCurrencyRateList();

	MoneySet getTotalAmount();

	MoneySet getTotalBeforeTax();

	CurrencyRateType getRateType();
	
	String getDocCurrency();
}
