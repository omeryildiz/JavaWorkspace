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
 * Şirket option bilgilerini tutar.
 * @author sinan.yumak
 *
 */
public enum DailyRateOptionKey implements OptionKey {

	ALERTPOPUP_VISIBLE("currencyAlertPopup.isVisible","true");	
	
    private String optionValue;
    private String defaultValue;

    private DailyRateOptionKey() {
	}

	private DailyRateOptionKey(String optionValue) {
		this.optionValue = optionValue;
	}

	private DailyRateOptionKey(String optionValue, String defaultValue) {
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
