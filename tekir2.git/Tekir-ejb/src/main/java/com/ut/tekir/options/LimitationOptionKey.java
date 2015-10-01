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
 * Kısıtlama option bilgilerini tutar.
 * @author sinan.yumak
 *
 */
public enum LimitationOptionKey implements OptionKey {

	STOCK_LEVEL("systemSettings.control.StockLevel","NoControl"),
	ACCOUNT_LEVEL("systemSettings.control.AccountLevel","NoControl"),
	CONTACT_RISK_LIMIT("systemSettings.control.ContactRiskLimit","NoControl"),
	CONTACT_CREDIT_LIMIT("systemSettings.control.ContactCreditLimit","NoControl"),
	STOCK_SHELF_LIFE("systemSettings.control.StockShelfLife","NoControl"),
	PRICE_CHANGE_CONTROL("systemSettings.control.PriceChangeControl","NoControl"),	
	SALEINVOICE_ZERO_LINEAMOUNT("systemSettings.control.SaleInvoiceZeroLineAmount","NoControl"),	
	PURCHASEINVOICE_ZERO_LINEAMOUNT("systemSettings.control.PurchaseInvoiceZeroLineAmount","NoControl"),	
	SALESHIPMENT_ZERO_LINEAMOUNT("systemSettings.control.SaleShipmentZeroLineAmount","NoControl"),	
	PURCHASESHIPMENT_ZERO_LINEAMOUNT("systemSettings.control.PurchaseShipmentZeroLineAmount","NoControl"),	
	SALEORDER_ZERO_LINEAMOUNT("systemSettings.control.SaleOrderZeroLineAmount","NoControl"),	
	PURCHASEORDER_ZERO_LINEAMOUNT("systemSettings.control.PurchaseOrderZeroLineAmount","NoControl"),	
	SALETENDER_ZERO_LINEAMOUNT("systemSettings.control.SaleTenderZeroLineAmount","NoControl"),
	PURCHASETENDER_ZERO_LINEAMOUNT("systemSettings.control.PurchaseTenderZeroLineAmount","NoControl");	
	
    private String optionValue;
    private String defaultValue;

    private LimitationOptionKey() {
	}

	private LimitationOptionKey(String optionValue) {
		this.optionValue = optionValue;
	}

	private LimitationOptionKey(String optionValue, String defaultValue) {
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
