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

/**
 * @author sinan.yumak
 *
 */
public enum ProductType {
	Unknown, //0
	Product, //1
	Service, //2
	Expense, //3
	Discount, //4
	DocumentExpense, //5
	DocumentDiscount, //6
	Fee, //7
	DocumentFee, //8
	ContactDiscount, //9 cari karttan gelen indirim satırlarını takip etmek için kullanacağız. 
	DiscountAddition, //10 vergi dahil indirim 
	ExpenseAddition; //11 vergi dahil masraf

	public static ProductType fromInteger(int anOrdinal) {
		for (ProductType type : values()) {
			if ( type.ordinal() == anOrdinal ) {
				return type;
			}
		}
		return ProductType.Unknown;
	}

}
