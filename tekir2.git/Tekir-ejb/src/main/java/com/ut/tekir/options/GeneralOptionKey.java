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
 * Genel option bilgilerini tutar.
 * @author sinan.yumak
 *
 */
public enum GeneralOptionKey implements OptionKey {

    SERIALNUMBER_ISEDITABLE("SerialNumber.isEditable","true"),
    FOREIGN_INVOICING_ENABLED("systemSettings.foreignInvoicingEnabled","true");	
	
    private String optionValue;
    private String defaultValue;

    private GeneralOptionKey() {
	}

	private GeneralOptionKey(String optionValue) {
		this.optionValue = optionValue;
	}

	private GeneralOptionKey(String optionValue, String defaultValue) {
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
