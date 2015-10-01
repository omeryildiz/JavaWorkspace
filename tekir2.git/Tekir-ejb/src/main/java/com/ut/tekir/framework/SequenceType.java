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

package com.ut.tekir.framework;


/**
 *
 * @author haky
 */
public class SequenceType {

    public static final String SERIAL_INVOICE_PURCHASE                         = "serial.invoice.Purchase";
    public static final String SERIAL_INVOICE_SALE                             = "serial.invoice.Sale";
    public static final String SERIAL_PROFORMAINVOICE_SALE                     = "serial.proformaInvoice.Sale";
    public static final String SERIAL_SHIPMENT_PURCHASE                        = "serial.shipment.Purchase";
    public static final String SERIAL_ORDER_PURCHASE                           = "serial.order.Purchase";
    public static final String SERIAL_ORDER_SALE                               = "serial.order.Sale";
    public static final String SERIAL_SHIPMENT_SALE                            = "serial.shipment.Sale";
    public static final String SERIAL_SHIPMENT_TRANSFER                        = "serial.shipment.Transfer";
    public static final String SERIAL_FUND_TRANSFER                            = "serial.fund.Transfer";
    public static final String SERIAL_FUND_COLLECTION                          = "serial.fund.Collection";
    public static final String SERIAL_FUND_PAYMENT                             = "serial.fund.Payment";
    public static final String SERIAL_FUND_DEBITCREDIT                         = "serial.fund.DebitCredit";
    public static final String SERIAL_PRODUCT_VIREMENT                         = "serial.stock.ProductVirement";
    public static final String SERIAL_DEBITCREDITNOTE_COLLECTION               = "serial.debitcreditnote.Collection";
    public static final String SERIAL_DEBITCREDITNOTE_PAYMENT                  = "serial.debitcreditnote.Payment";
    public static final String SERIAL_BANKTO_ACCOUNT_TRANSFER                  = "serial.banktoaccount.Transfer";
    public static final String SERIAL_ACCOUNT_TO_BANK_TRANSFER                 = "serial.accountToBank.Transfer";
    public static final String SERIAL_BANKTO_CONTACT_TRANSFER                  = "serial.banktocontact.Transfer";
    public static final String SERIAL_CONTACT_TO_BANK_TRANSFER                 = "serial.contacttobank.Transfer";
    public static final String SERIAL_BANKTO_BANK_TRANSFER                     = "serial.banktobank.Transfer";
    public static final String SERIAL_FINANCE_DEPOSITACCOUNT                   = "serial.finance.DepositAccount";
    public static final String SERIAL_BOND_SALE                                = "serial.bond.Sale";
    public static final String SERIAL_BOND_PURCHASE                            = "serial.bond.Purchase";
    public static final String SERIAL_PORTFOLIOTO_PORTFOLIO                    = "serial.finance.PortfolioToPortfolio";

    public static final String SERIAL_CHEQUE_TO_BANK_PAYROLL                   = "serial.chequeToBankPayroll";
    public static final String SERIAL_CHEQUE_TO_CONTACT_PAYROLL                = "serial.chequeToContactPayroll";
    public static final String SERIAL_CHEQUE_FROM_CONTACT_PAYROLL              = "serial.chequeFromContactPayroll";
    public static final String SERIAL_CHEQUE_TO_BANK_ASSURANCE_PAYROLL         = "serial.chequeToBankAssurancePayroll";
    public static final String SERIAL_CHEQUE_COLLECTED_AT_BANK_PAYROLL         = "serial.chequeCollectedAtBankPayroll";
    public static final String SERIAL_CHEQUE_TO_ACCOUNT_COLLECTION_PAYROLL     = "serial.chequeToAccountCollectionPayroll";
    public static final String SERIAL_CHEQUE_TO_ACCOUNT_PAYMENT_PAYROLL        = "serial.chequeToAccountPaymentPayroll";
    public static final String SERIAL_CHEQUE_TO_BANK_PAYMENT_PAYROLL           = "serial.chequeToBankPaymentPayroll";

    public static final String SERIAL_PROMISSORY_TO_BANK_PAYROLL               = "serial.promissoryToBankPayroll";
    public static final String SERIAL_PROMISSORY_TO_CONTACT_PAYROLL            = "serial.promissoryToContactPayroll";
    public static final String SERIAL_PROMISSORY_FROM_CONTACT_PAYROLL          = "serial.promissoryFromContactPayroll";
    public static final String SERIAL_PROMISSORY_TO_BANK_ASSURANCE_PAYROLL     = "serial.promissoryToBankAssurancePayroll";
    public static final String SERIAL_PROMISSORY_COLLECTED_AT_BANK_PAYROLL     = "serial.promissoryCollectedAtBankPayroll";
    public static final String SERIAL_PROMISSORY_TO_ACCOUNT_COLLECTION_PAYROLL = "serial.promissoryToAccountCollectionPayroll";
    public static final String SERIAL_PROMISSORY_TO_ACCOUNT_PAYMENT_PAYROLL    = "serial.promissoryToAccountPaymentPayroll";
    public static final String SERIAL_PROMISSORY_TO_BANK_PAYMENT_PAYROLL       = "serial.promissoryToBankPaymentPayroll";
    
    public static final String SERIAL_CARDS_CONTACT                            = "serial.cards.Contact";
    public static final String SERIAL_CARDS_STOCK                              = "serial.cards.Stock";
    public static final String SERIAL_CARDS_SERVICE                            = "serial.cards.Service";
    public static final String SERIAL_CARDS_EXPENSE                            = "serial.cards.Expense";
    public static final String SERIAL_CARDS_DISCOUNT                           = "serial.cards.Discount";
    public static final String SERIAL_EXPENSE_NOTE                             = "serial.expenseNote";
    public static final String SERIAL_TENDER                                   = "serial.tender";
    public static final String SERIAL_STOCK_BARCODE                            = "serial.stock.Barcode";
    public static final String SERIAL_CHEQUE 								   = "serial.Cheque";
    public static final String SERIAL_PROMISSORY 							   = "serial.Promissory";
    public static final String SERIAL_COUNT_NOTE                               = "serial.countNote";
    
    public static final String REFERENCE_INVOICE_PURCHASE = "reference.invoice.Purchase";
    public static final String REFERENCE_INVOICE_SALE = "reference.invoice.Sale";
    public static final String REFERENCE_SHIPMENT_PURCHASE = "reference.shipment.Purchase";
    public static final String REFERENCE_SHIPMENT_SALE = "reference.shipment.Sale";
    public static final String REFERENCE_ORDER_PURCHASE = "reference.order.Purchase";
    public static final String REFERENCE_ORDER_SALE = "reference.order.Sale";
    public static final String REFERENCE_SHIPMENT_TRANSFER = "reference.shipment.Transfer";
    public static final String REFERENCE_FUND_TRANSFER = "reference.fund.Transfer";
    public static final String REFERENCE_FUND_COLLECTION = "reference.fund.Collection";
    public static final String REFERENCE_FUND_PAYMENT = "reference.fund.Payment";
    public static final String REFERENCE_FUND_DEBITCREDIT = "reference.fund.DebitCredit";
    public static final String REFERENCE_PRODUCT_VIREMENT = "reference.stock.ProductVirement";
    public static final String REFERENCE_DEBITCREDITNOTE_COLLECTION = "reference.debitcreditnote.Collection";
    public static final String REFERENCE_DEBITCREDITNOTE_PAYMENT = "reference.debitcreditnote.Payment";
    public static final String REFERENCE_BANKTO_ACCOUNT_TRANSFER = "reference.banktoaccount.Transfer";
    public static final String REFERENCE_BANKTO_CONTACT_TRANSFER = "reference.banktocontact.Transfer";
    public static final String REFERENCE_BANKTO_BANK_TRANSFER = "reference.banktobank.Transfer";
    public static final String REFERENCE_FINANCE_DEPOSITACCOUNT = "reference.finance.DepositAccount";
    public static final String REFERENCE_BOND_SALE = "reference.bond.Sale";
    public static final String REFERENCE_BOND_PURCHASE = "reference.bond.Purchase";
    
}
