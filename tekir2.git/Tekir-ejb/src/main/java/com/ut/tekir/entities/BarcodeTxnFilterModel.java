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
 * Barkod bilgilerini yazdırılacak fiyatlarla tutan listemizdir.
 * @author sinan.yumak
 *
 */
public class BarcodeTxnFilterModel {
	private static final long serialVersionUID = 1L;

	private BarcodeTxn item;
	
    /**
     * Yazdırılacak fiyatı tutar.
     */
    private MoneySet grossPrice;

    /**
     * Fiyat bulunup bulunamadağı bilgisini tutar.
     */
    private boolean hasPrice = true;

	public MoneySet getGrossPrice() {
		if (grossPrice == null) {
			grossPrice = new MoneySet();
		}
		return grossPrice;
	}

	public void setGrossPrice(MoneySet grossPrice) {
		this.grossPrice = grossPrice;
	}
	
	public boolean isHasPrice() {
		return hasPrice;
	}

	public void setHasPrice(boolean hasPrice) {
		this.hasPrice = hasPrice;
	}

	public BarcodeTxn getItem() {
		return item;
	}

	public void setItem(BarcodeTxn item) {
		this.item = item;
	}

}
