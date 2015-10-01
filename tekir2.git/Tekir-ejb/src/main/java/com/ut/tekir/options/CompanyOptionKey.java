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
public enum CompanyOptionKey implements OptionKey {

	COMPANY_NAME("company.Name","Tekir Ticari Otomasyon"),
	COMPANY_TITLE("company.Title","Şirket Unvanı"),
	COMPANY_TAXOFFICE("company.TaxOffice","Vergi Dairesi"),
    COMPANY_TAX_NUMBER("company.TaxNumber","Sicil No"),
    COMPANY_ADDRESS("company.Address","Resmi Adres"),	
	COMPANY_PHONE("company.Phone","Telefon"),
	COMPANY_FAX("company.Fax","Faks"),	
	COMPANY_EMAIL("company.Email","E-Posta"),	
	COMPANY_WEB("company.Web","Web");	
	
    private String optionValue;
    private String defaultValue;

    private CompanyOptionKey() {
	}

	private CompanyOptionKey(String optionValue) {
		this.optionValue = optionValue;
	}

	private CompanyOptionKey(String optionValue, String defaultValue) {
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
