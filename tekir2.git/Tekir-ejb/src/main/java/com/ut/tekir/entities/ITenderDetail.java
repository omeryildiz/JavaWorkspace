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
 * Returns discounts if available. e.g. if we are working 
 * on a shipment, it returns shipment discount.
 * @author sinan.yumak
 *
 */
public interface ITenderDetail {

	/**
	 * Returns item's child list.
	 */
	List<? extends TenderDetailBase> getChildList();
	
	/**
	 * Returns item's expense list.
	 */
	List<? extends TenderDetailBase> getExpenseList();

	/**
	 * Returns item's discount list.
	 */
	List<? extends TenderDetailBase> getDiscountList();
	
	/**
	 * Returns item's fee list.
	 */
	List<? extends TenderDetailBase> getFeeList();

	List<? extends TenderDetailBase> getDiscountAndExpenseList();

	MoneySet getTenderDiscount();

	MoneySet getTenderExpense();
	
	MoneySet getOrderDiscount();

	MoneySet getOrderExpense();
	
	MoneySet getShipmentDiscount();
	
	MoneySet getShipmentExpense();

	MoneySet getInvoiceDiscount();
	
	MoneySet getInvoiceExpense();

}
