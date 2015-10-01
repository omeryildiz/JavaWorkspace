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
 * Tahsilat-tediye toplamlarını gruplarken kullanacağımız model sınıfıdır.
 * 
 * @author sinan.yumak
 * 
 */
public class WorkBunchCurrency {
	private String currency;
	private WorkBunch bunch;

	public WorkBunchCurrency() {
	}

	public WorkBunchCurrency(WorkBunch bunch, String currency) {
		super();
		this.currency = currency;
		this.bunch = bunch;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public WorkBunch getBunch() {
		return bunch;
	}

	public void setBunch(WorkBunch bunch) {
		this.bunch = bunch;
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof WorkBunchCurrency)) return false;
		WorkBunchCurrency other = (WorkBunchCurrency) object;
		if ( (this.getBunch() != null && this.getBunch() != other.getBunch()) || !this.getCurrency().equals(other.getCurrency())) return false;
		return true;
	}

	@Override
	public int hashCode() {
		return bunch != null ? bunch.hashCode() + currency.hashCode() : currency.hashCode();
	}

}
