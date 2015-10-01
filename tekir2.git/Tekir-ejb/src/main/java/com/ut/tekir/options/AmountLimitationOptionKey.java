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

package com.ut.tekir.options;


/**
 * Yuvarlama option bilgilerini tutar.
 * @author sinan.yumak
 *
 */
public enum AmountLimitationOptionKey implements OptionKey {

    MAX_ROUNDING_AMOUNT("systemSettings.control.MaxRoundingAmount","0.09"),	
    MAX_PAYMENT_AMOUNT("systemSettings.control.MaxPaymentAmount","8000.00");	
	
    private String optionValue;
    private String defaultValue;

    private AmountLimitationOptionKey() {
	}

	private AmountLimitationOptionKey(String optionValue) {
		this.optionValue = optionValue;
	}

	private AmountLimitationOptionKey(String optionValue, String defaultValue) {
		this.optionValue = optionValue;
		this.defaultValue = defaultValue;
	}
	
	public String getValue() {
		return optionValue;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

}
