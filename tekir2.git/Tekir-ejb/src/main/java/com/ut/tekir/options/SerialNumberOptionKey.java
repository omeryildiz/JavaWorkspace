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
 * Seri numaraları option bilgilerini tutar.
 * @author sinan.yumak
 *
 */
public enum SerialNumberOptionKey implements OptionKey {

	SERIAL_INVOICE_PURCHASE("serial.invoice.Purchase","AF"),
	SERIAL_INVOICE_SALE("serial.invoice.Sale","SF"),
	SERIAL_SHIPMENT_PURCHASE("serial.shipment.Purchase","AI"),
	SERIAL_ORDER_PURCHASE("serial.order.Purchase","SI"),
	SERIAL_ORDER_SALE("serial.order.Sale","SP"),
	SERIAL_SHIPMENT_SALE("serial.shipment.Sale","SI"),
	SERIAL_SHIPMENT_TRANSFER("serial.shipment.Transfer","DSI"),
	SERIAL_FUND_TRANSFER("serial.fund.Transfer","KS"),
	SERIAL_FUND_COLLECTION("serial.fund.Collection","TH"),
	SERIAL_FUND_PAYMENT("serial.fund.Payment","TD"),
	SERIAL_FUND_DEBITCREDIT("serial.fund.DebitCredit","CV"),
	SERIAL_PRODUCT_VIREMENT("serial.stock.ProductVirement","ST"),
	SERIAL_DEBITCREDITNOTE_COLLECTION("serial.debitcreditnote.Collection","ADF"),
	SERIAL_DEBITCREDITNOTE_PAYMENT("serial.debitcreditnote.Payment","BDF"),
	SERIAL_BANKTO_ACCOUNT_TRANSFER("serial.banktoaccount.Transfer","BKT"),
	SERIAL_ACCOUNT_TO_BANK_TRANSFER("serial.accountToBank.Transfer","KBT"),
	SERIAL_BANKTO_CONTACT_TRANSFER("serial.banktocontact.Transfer","BCT"),
	SERIAL_CONTACT_TO_BANK_TRANSFER("serial.contacttobank.Transfer","CBT"),
	SERIAL_BANKTO_BANK_TRANSFER("serial.banktobank.Transfer","BBT"),
	SERIAL_FINANCE_DEPOSITACCOUNT("serial.finance.DepositAccount","VH"),
	SERIAL_BOND_SALE("serial.bond.Sale","BS"),
	SERIAL_BOND_PURCHASE("serial.bond.Purchase","BA"),
	SERIAL_PORTFOLIOTO_PORTFOLIO("serial.finance.PortfolioToPortfolio","PT"),
	SERIAL_CHEQUE_TO_BANK_PAYROLL("serial.chequeToBankPayroll","CTH"),
	SERIAL_CHEQUE_TO_CONTACT_PAYROLL("serial.chequeToContactPayroll","CC"),
	SERIAL_CHEQUE_FROM_CONTACT_PAYROLL("serial.chequeFromContactPayroll","CG"),
	SERIAL_CHEQUE_TO_BANK_ASSURANCE_PAYROLL("serial.chequeToBankAssurancePayroll","CTM"),
	SERIAL_CHEQUE_COLLECTED_AT_BANK_PAYROLL("serial.chequeCollectedAtBankPayroll","CTE"),
	SERIAL_CHEQUE_TO_ACCOUNT_COLLECTION_PAYROLL("serial.chequeToAccountCollectionPayroll","CKT"),
	SERIAL_CHEQUE_TO_ACCOUNT_PAYMENT_PAYROLL("serial.chequeToAccountPaymentPayroll","CKO"),
	SERIAL_CHEQUE_TO_BANK_PAYMENT_PAYROLL("serial.chequeToBankPaymentPayroll","CBO"),
	SERIAL_PROMISSORY_TO_BANK_PAYROLL("serial.promissoryToBankPayroll","STH"),
	SERIAL_PROMISSORY_TO_CONTACT_PAYROLL("serial.promissoryToContactPayroll","SC"),
	SERIAL_PROMISSORY_FROM_CONTACT_PAYROLL("serial.promissoryFromContactPayroll","SG"),
	SERIAL_PROMISSORY_TO_BANK_ASSURANCE_PAYROLL("serial.promissoryToBankAssurancePayroll","STM"),
	SERIAL_PROMISSORY_COLLECTED_AT_BANK_PAYROLL("serial.promissoryCollectedAtBankPayroll","STE"),
	SERIAL_PROMISSORY_TO_ACCOUNT_COLLECTION_PAYROLL("serial.promissoryToAccountCollectionPayroll","SKT"),
	SERIAL_PROMISSORY_TO_ACCOUNT_PAYMENT_PAYROLL("serial.promissoryToAccountPaymentPayroll","SKO"),
	SERIAL_PROMISSORY_TO_BANK_PAYMENT_PAYROLL("serial.promissoryToBankPaymentPayroll","SBO"),
	SERIAL_CARDS_CONTACT("serial.cards.Contact","CK"),
	SERIAL_CARDS_STOCK("serial.cards.Stock","SK"),
	SERIAL_CARDS_SERVICE("serial.cards.Service","HK"),
	SERIAL_CARDS_EXPENSE("serial.cards.Expense","MK"),
	SERIAL_CARDS_DISCOUNT("serial.cards.Discount","IK"),
	SERIAL_EXPENSE_NOTE("serial.expenseNote","GF"),
	SERIAL_TENDER("serial.tender","TE"),
	SERIAL_STOCK_BARCODE("serial.stock.Barcode","B1"),
	SERIAL_CHEQUE("serial.Cheque","AA"),
	SERIAL_PROMISSORY("serial.Promissory","AA"),	
    SERIAL_COUNT_NOTE("serial.countNote","SF");
	
    private String optionValue;
    private String defaultValue;

    private SerialNumberOptionKey() {
	}

	private SerialNumberOptionKey(String optionValue) {
		this.optionValue = optionValue;
	}

	private SerialNumberOptionKey(String optionValue, String defaultValue) {
		this.optionValue = optionValue;
		this.defaultValue = defaultValue;
	}
	
	public String getValue() {
		return optionValue;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public static void main(String[] args) {
		for (SerialNumberOptionKey key: values()) {
			System.out.println("optionEditor." + key.getValue());
		}
	}
}
