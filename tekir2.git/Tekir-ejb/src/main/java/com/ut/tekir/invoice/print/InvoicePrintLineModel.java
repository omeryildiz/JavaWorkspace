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

package com.ut.tekir.invoice.print;

import java.math.BigDecimal;
import java.util.Date;

import com.ut.tekir.entities.Contact;
import com.ut.tekir.entities.Invoice;
import com.ut.tekir.entities.Money;
import com.ut.tekir.entities.Quantity;
import com.ut.tekir.invoice.InvoiceItemModel;
import com.ut.tekir.invoice.InvoiceItemModel.ItemType;
import com.ut.tekir.util.NumberToText;
import com.ut.tekir.util.NumberToTextTR;

/**
 * This class is a helper model class for Invoice Printing
 * @author haky
 * 
 * irsaliyeli satış faturası detay
 * 
 */
public class InvoicePrintLineModel {

    private InvoicePrintMasterModel master;
    private InvoiceItemModel line;
    
  
    public InvoicePrintLineModel( InvoicePrintMasterModel master, InvoiceItemModel line ){
        this.master = master;
        this.line = line;
    }
    
    public String getLineInfo() {
        return line.getInfo();
    }

    public String getLineCode() {
        return line.getLineCode();
    }

    public String getLineName() {
        if (line.getType() == ItemType.ServiceItem) {
            return line.getService().getCaption();
        } else {
        	return line.getProduct().getCaption();
        }
    }

    public String getLineProductCode() {
        if (line.getType() == ItemType.ServiceItem) {
            return line.getService().getCode();
        } else {
        	return line.getProduct().getCode();
        }
    }
    
    public String getLineProductName() {
        if (line.getType() == ItemType.ServiceItem) {
            return line.getService().getName();
        } else {
        	return line.getProduct().getName();
        }
    }
    
    public Double getQuantity() {
        return line.getQuantity().getValue();
    }
    
    public Quantity getQuantityType() {
        return line.getQuantity();
    }
    
    public Money getUnitPrice() {
        return line.getUnitPrice();
    }

    public Money getLineAmount() {
        return line.getAmount();
    }

    
    public BigDecimal getLineTaxRate() {
        return line.getTaxRate();        
    }

    public Money getLineTaxAmount() {
        return line.getTaxAmount();
    }

    
    public Money getLineTotalAmaount() {
        return line.getTotalAmaount();        
    }
    
//    public Money getLineTaxExcludedCurrencyAmount() {
//        return line.getTaxExcludedTotalAmount();        
//    }

    public Double getLineTaxExcludedTotalAmount() {
    	Double d;
    	if (line.getQuantity().getValue() > 0 ) {
    		d= line.getTaxExcludedTotalAmount().getLocalAmount().doubleValue();
    		return d;
    	} else return 0.0;      
    }
    
    public Double getTaxExcludedUnitPrice() {
    	Double d; 
    	if (line.getQuantity().getValue() > 0 ) {
    		d = line.getTaxExcludedTotalAmount().getLocalAmount().doubleValue() / line.getQuantity().getValue();
    		return d;
    	} else return 0.0;
    }
    
    /*
     * İndirim ve masraflar şuan işlemiyorlar.
    public Double getLineDiscount() {
        return line.getLineDiscount();
    }

    public Double getShipmentDiscount() {
        return line.getShipmentDiscount();
    }

    public Double getInvoiceDiscount() {
        return line.getInvoiceDiscount();
    }

    
    public Double getLineExpense() {
        return line.getLineExpense();
    }

    
    public Double getShipmentExpense() {
        return line.getShipmentExpense();
    }

    public Double getInvoiceExpense() {
        return line.getInvoiceExpense();
        
    }
    */
    
    
    public String getSerial(){
        return master.getSerial();
    }
    
    public String getReference(){
        return master.getReference();
    }
    
    public String getCode(){
        return master.getCode();
    }
    
    public String getInfo(){
        return master.getInfo();
    }
    public String getInfo1(){
        return master.getInfo1();
    }
    public String getInfo2(){
        return master.getInfo2();
    }
    
    public Date getIssueDate(){
        return master.getIssueDate();
    }

    public Date getShippingDate(){
        return master.getShippingDate();
    }
    
    public Date getIssueTime(){
        return master.getIssueTime();
    }
    
    public String getCustomerCode(){
        return master.getCustomerCode();
    }
    
    public String getCustomerName(){
        return master.getCustomerName();
    }
    
    public String getCustomerCompany(){
        return master.getCustomerCompany();
    }
    
    public String getCustomerStreet(){
        return master.getCustomerStreet();
    }
    
    public String getCustomerProvince(){
        return master.getCustomerProvince();
    }
    
    public String getCustomerCity(){
        return master.getCustomerCity();
    }

    public String getCustomerZip(){
        return master.getCustomerZip();
    }
    
    public String getCustomerAddress(){
        return master.getCustomerAddress();
    }
    
    public String getCustomerTaxOffice(){
        return master.getCustomerTaxOffice();
    }
    
    public String getCustomerTaxNumber(){
        return master.getCustomerTaxNumber();
    }

    public String getCustomerSsn(){
        return master.getCustomerSsn();
    }
    
    public Boolean getCustomerIsPerson(){
        return master.getCustomerIsPerson();
    }
    
    public Contact getContact() {
        return master.getContact();
    }

    public Invoice getInvoice() {
        return master.getInvoice();
    }

    public void setInvoice(Invoice invoice) {
        master.setInvoice(invoice);
    }
    
    public Boolean hasTaxRate( int ix ){
        return master.hasTaxRate(ix);
    }
    
    public String getTaxName( int ix ){
        return master.getTaxName(ix);
    }
    
    public BigDecimal getTaxRate( int ix ){
        return master.getTaxRate(ix);
    }
    
    public Money getTaxAmount( int ix ){
        return master.getTaxAmount(ix);
    }
    
    public Boolean hasCcyRate( int ix ){
        return master.hasCcyRate(ix);
    }
    
    public String getCcyName( int ix ){
        return master.getCcyName(ix);
    }
    
    public BigDecimal getCcyAskRate( int ix ){
        return master.getCcyAskRate(ix);
    }
    
    public BigDecimal getCcyBidRate( int ix ){
        return master.getCcyBidRate(ix);
    }

    public BigDecimal getBeforeTax(){
        return master.getBeforeTax();
    }
    
    public BigDecimal getTotalTax(){
        return master.getTotalTax();
    }
    
    public BigDecimal getInvoiceTotal(){
        return master.getInvoiceTotal();
    }
    
    public String getTotalAsText(){
    	NumberToText nbt = new NumberToTextTR();
    	return nbt.convert(getInvoiceTotal().doubleValue(), "TL", "KR");
    }
    
    public Boolean getHasTax0Rate(){
        return master.hasTaxRate(0);
    }
    
    public String getTax0Name(){
        return master.getTaxName(0);
    }
    
    public BigDecimal getTax0Rate(){
        return master.getTaxRate(0);
    }
    
    public Money getTax0Amount(){
        return master.getTaxAmount(0);
    }
    
    public Boolean getHasTax1Rate(){
        return master.hasTaxRate(1);
    }
    
    public String getTax1Name(){
        return master.getTaxName(1);
    }
    
    public BigDecimal getTax1Rate(){
        return master.getTaxRate(1);
    }
    
    public Money getTax1Amount(){
        return master.getTaxAmount(1);
    }
    
    public Boolean getHasTax2Rate(){
        return master.hasTaxRate(2);
    }
    
    public String getTax2Name(){
        return master.getTaxName(2);
    }
    
    public BigDecimal getTax2Rate(){
        return master.getTaxRate(2);
    }
    
    public Money getTax2Amount(){
        return master.getTaxAmount(2);
    }
    
    public Boolean getHasTax3Rate(){
        return master.hasTaxRate(3);
    }
    
    public String getTax3Name(){
        return master.getTaxName(3);
    }
    
    public BigDecimal getTax3Rate(){
        return master.getTaxRate(3);
    }
    
    public Money getTax3Amount(){
        return master.getTaxAmount(3);
    }
    
    public Boolean getHasTax4Rate(){
        return master.hasTaxRate(4);
    }
    
    public String getTax4Name(){
        return master.getTaxName(4);
    }
    
    public BigDecimal getTax4Rate(){
        return master.getTaxRate(4);
    }
    
    public Money getTax4Amount(){
        return master.getTaxAmount(4);
    }
}
