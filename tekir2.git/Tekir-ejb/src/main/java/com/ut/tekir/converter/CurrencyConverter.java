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

package com.ut.tekir.converter;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.faces.Converter;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

/**
 * @author sozmen
 *
 * Döviz kurları sayfasında bigdecimalConverter virgulden sonra
 * 2 hane gosteriyordu. 4 hane seklinde gostermek icin bigdecimalConverter
 * coklandi.
 * @see BigDecimalConverter
 */
@Name("currencyConverter")
@BypassInterceptors
@Converter
public class CurrencyConverter extends DecimalConverterBase {

	public String getFormatString() {
		return "0.0000";
	}

	public int formatLength() {
		return 4;
	}
    
}