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
import java.util.List;

import com.ut.tekir.entities.BarcodeTxn;

/**
 * Ürünlerden(Product) barkod çıktısı almak için kullanacağımız home 
 * bileşenimizdir.
 * @author sinan.yumak
 *
 */
public interface BarcodePrinterHome extends Serializable {

	/**
	 * Listede verilen ürünlerin PPLA olarak barkod çıktısını hazırlayıp barkod yazıcısına gönderir.
	 * @param barkod listesi
	 */
	void sendToBarcodePrinter(List<BarcodeTxn> barcodeList) throws Exception;
}
