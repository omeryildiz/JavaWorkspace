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

package com.ut.tekir.configuration;

/**
 * Sistem içerisinde kullanılan temel ayarlar vb. tutar.
 * 
 * @author sinan.yumak
 * 
 */
public class SystemConfiguration {

	public static String CURRENCY_CODE = "TL";
	public static String CURRENCYDEC_CODE = "KR";
	public static String COUNTRY_NAME = "TÜRKİYE";
	public static String COUNTRY_SHORTNAME = "TR";
	public static String COUNTRY_CODE = "90";
	public static String LOCALE = "tr_TR";
	public static final String PRODUCTLABEL = "Tekir Ticari Otomasyon";
	
	public static void setCurrencyCode(String currencyCode) {
		CURRENCY_CODE = currencyCode;
	}

	public static void setCurrencydecCode(String currencydecCode) {
		CURRENCYDEC_CODE = currencydecCode;
	}

	public static void setCountryName(String countryName) {
		COUNTRY_NAME = countryName;
	}

	public static void setCountryCode(String countryCode) {
		COUNTRY_CODE = countryCode;
	}

	public static void setCountryShortName(String countryShortName) {
		COUNTRY_SHORTNAME = countryShortName;
	}

	public static void setLocale(String locale) {
		LOCALE = locale;
	}

	public static String getProductlabel() {
		return PRODUCTLABEL;
	}

}
