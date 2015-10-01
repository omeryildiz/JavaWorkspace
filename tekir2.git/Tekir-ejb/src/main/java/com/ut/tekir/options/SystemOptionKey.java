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
 * @author sinan.yumak
 *
 */
public enum SystemOptionKey implements OptionKey {

	CURRENCY_CODE("systemOption.CurrencyCode","TL"),
	CURRENCYDEC_CODE("systemOption.CurrencydecCode","KR"),
	COUNTRY_NAME("systemOption.CountryName","TÜRKİYE"),
	COUNTRY_SHORTNAME("systemOption.CountryShortName","TR"),
	LOCALE("systemOption.Locale","tr_TR"),
	COUNTRY_CODE("systemOption.CountryCode","90");
	
    private String optionValue;
    private String defaultValue;

    private SystemOptionKey() {
    }

	private SystemOptionKey(String optionValue) {
		this.optionValue = optionValue;
	}

	private SystemOptionKey(String optionValue, String defaultValue) {
		this.optionValue = optionValue;
		this.defaultValue = defaultValue;
	}
    
	@Override
	public String getDefaultValue() {
		return defaultValue;
	}

	@Override
	public String getValue() {
		return optionValue;
	}

}
