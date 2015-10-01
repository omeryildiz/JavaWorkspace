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
 * 
 * @author sinan.yumak
 */
@Name("bigDecimalSixScaleConverter")
@BypassInterceptors
@Converter
public class BigDecimalSixScaleConverter extends DecimalConverterBase {

	public String getFormatString() {
		return "0.000000";
	}

	public int formatLength() {
		return 6;
	}

}
