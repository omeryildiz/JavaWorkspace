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

package com.ut.tekir.stock;

import java.io.Serializable;

import javax.ejb.Local;

import com.ut.tekir.entities.Product;
import com.ut.tekir.entities.Warehouse;

/**
 * @author sinan.yumak
 *
 */
@Local
public interface StockLevelFinder extends Serializable {

	/**
	 * ProductTxn tablosunu süzer ve ürünün aktif seviyesini döndürür.
	 * @param aProduct
	 */
	double findActiveLevel(Product aProduct, Warehouse warehouse);
	
	/**
	 * ProductTxn tablosunu süzer ve ürünün rezerve edilmiş seviyesini döndürür.
	 * @param aProduct
	 */
	double findReservedLevel(Product aProduct, Warehouse warehouse);
	
	double findStockLevel(Product aProduct, Warehouse warehouse);
	
}
