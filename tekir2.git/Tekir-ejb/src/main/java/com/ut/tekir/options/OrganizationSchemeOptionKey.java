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
 * Organizasyon kırılımı option bilgilerini tutar.
 * @author sinan.yumak
 *
 */
public enum OrganizationSchemeOptionKey implements OptionKey {

    USE_SCHEME("organizationSchemeOption.UseScheme","false"),
    CONTACT_LEVEL("organizationLevelOption.value.Contact","false"),	
    CUSTOMER_LEVEL("organizationLevelOption.value.Customer","false"),	
    ACCOUNT_LEVEL("organizationLevelOption.value.Account","false"),	
    WAREHOUSE_LEVEL("organizationLevelOption.value.Warehouse","false"),	
    BANKBRANCH_LEVEL("organizationLevelOption.value.BankBranch","false"),	
    PERSONNEL_LEVEL("organizationLevelOption.value.Personnel","false"),	
    PROVIDER_LEVEL("organizationLevelOption.value.Provider","false"),	
    AGENT_LEVEL("organizationLevelOption.value.Agent","false");	
	
    private String optionValue;
    private String defaultValue;

    private OrganizationSchemeOptionKey() {
	}

	private OrganizationSchemeOptionKey(String optionValue) {
		this.optionValue = optionValue;
	}

	private OrganizationSchemeOptionKey(String optionValue, String defaultValue) {
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
