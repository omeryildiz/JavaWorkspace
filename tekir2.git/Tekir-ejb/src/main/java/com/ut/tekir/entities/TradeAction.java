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
 *
 * @author haky
 */
public enum TradeAction {
    Purchase, //0- Alış
    Sale, //1- Satış
    PurchseReturn, //2- Alış İade
    SaleReturn, //3- Satış İade
    Transport, //4- Nakliye
	Spending, //5- Sarf
    Reserved, //6- sipariş rezervasyonu
    Delivered; //7- sipariş rezervasyonundan teslim edildi.
    
	public static TradeAction fromInteger(int anOrdinal) {
		for (TradeAction type : values()) {
			if ( type.ordinal() == anOrdinal ) {
				return type;
			}
		}
		return TradeAction.Purchase;
	}

}
