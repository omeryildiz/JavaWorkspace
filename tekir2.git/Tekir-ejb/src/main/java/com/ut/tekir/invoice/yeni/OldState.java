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

package com.ut.tekir.invoice.yeni;

import java.math.BigDecimal;

/**
 * Fatura vb. belgenin edit edilmeden önceki tutar bilgilerini tutar.
 * 
 * @author sinan.yumak
 *
 */
public class OldState {
	private BigDecimal contactLimit = BigDecimal.ZERO;//fatura tutarı - ödeme tablosu tutarı bilgisidir.

	public BigDecimal getContactLimit() {
		return contactLimit;
	}

	public void setContactLimit(BigDecimal contactLimit) {
		this.contactLimit = contactLimit;
	}
	
	
}
