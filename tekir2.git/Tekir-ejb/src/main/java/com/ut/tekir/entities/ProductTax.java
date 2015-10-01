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
 * Stok/hizmetin vergi bilgilerini tutmak için kullanılacak ara model.
 * 
 * @author sinan.yumak
 * 
 */
public class ProductTax {

	private Tax tax;
	private Boolean taxIncluded = Boolean.TRUE;
	private TaxRate rate;
	
	public ProductTax() {
	}
	
	public ProductTax(Tax tax, Boolean taxIncluded) {
		super();
		this.tax = tax;
		this.taxIncluded = taxIncluded;
	}

	public Tax getTax() {
		return tax;
	}

	public void setTax(Tax tax) {
		this.tax = tax;
	}

	public Boolean getTaxIncluded() {
		return taxIncluded;
	}

	public void setTaxIncluded(Boolean taxIncluded) {
		this.taxIncluded = taxIncluded;
	}

	public TaxRate getRate() {
		return rate;
	}

	public void setRate(TaxRate rate) {
		this.rate = rate;
	}

}
