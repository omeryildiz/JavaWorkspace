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

import com.ut.tekir.entities.Contact;
import com.ut.tekir.entities.Invoice;
import com.ut.tekir.entities.ContactAddress;
import com.ut.tekir.entities.Money;

import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @author haky
 * 
 *  irsaliyeli satış faturası master
 *  
 */
public class InvoicePrintMasterModel {
    
    
    private Invoice invoice;
    
    public InvoicePrintMasterModel( Invoice invoice ){
        this.invoice = invoice;
    }
    
    public String getSerial(){
        return getInvoice().getSerial();
    }
    
    public String getReference(){
        return getInvoice().getReference();
    }
    
    public String getCode(){
        return getInvoice().getCode();
    }
    
    public String getInfo(){
        return getInvoice().getInfo();
    }
    
    public String getInfo1(){
        return getInvoice().getInfo1();
    }
    
    public String getInfo2(){
        return getInvoice().getInfo2();
    }
    
    public Date getIssueDate(){
        return getInvoice().getDate();
    }

    public Date getShippingDate(){
        return getInvoice().getShippingDate();
    }
    
    public Date getIssueTime(){
        return getInvoice().getTime();
    }
    
    public String getCustomerCode(){
        return getContact().getCode();
    }
    
    public String getCustomerName(){
        return getContact().getFullname();
    }
    
    public String getCustomerCompany(){
        return getContact().getCompany();
    }
    
    public Boolean getCustomerIsPerson(){
        return getContact().getPerson();
    }
    
    public String getCustomerStreet(){
    	String street = "";
    	for (ContactAddress address : getContact().getAddressList()) {
    		if (address.getDefaultAddress()) {
    			street = address.getAddress().getStreet();
    			break;
    		}
    	}
        return street;
    }
    
    public String getCustomerProvince(){
    	String province = "";
    	for (ContactAddress address : getContact().getAddressList()) {
    		if (address.getActiveAddress()) {
    			province = address.getAddress().getProvince();
    			break;
    		}
    	}
        return province;
    }
    
    public String getCustomerCity(){
    	String city = "";
    	for (ContactAddress address : getContact().getAddressList()) {
    		if (address.getActiveAddress()) {
    			city = address.getAddress().getCity();
    			break;
    		}
    	}
        return city;
    }

    public String getCustomerZip(){
    	String zip = "";
    	for (ContactAddress address : getContact().getAddressList()) {
    		if (address.getActiveAddress()) {
    			zip = address.getAddress().getZip();
    			break;
    		}
    	}
        return zip;
    }
    
    public String getCustomerAddress(){
        return getCustomerStreet() + " " + getCustomerZip() + " " + getCustomerProvince() + " " + getCustomerCity();
    }
    
    public String getCustomerTaxOffice(){
        return getContact().getTaxOffice();
    }
    
    public String getCustomerTaxNumber(){
        return getContact().getTaxNumber();
    }
    
    public String getCustomerSsn(){
        return getContact().getSsn();
    }
    
    public Contact getContact() {
        return invoice.getContact();
    }

    public Invoice getInvoice() {
        return invoice;
    }

    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
    }
    
    public Boolean hasTaxRate( int ix ){
        return ( invoice.getTaxSummaries().size() > ix && ix >= 0 );
    }
    
    public String getTaxName( int ix ){
    	if( !hasTaxRate( ix )){
    		return "";
    	}
        return invoice.getTaxSummaries().get(ix).getTax().getName();
    }
    
    public BigDecimal getTaxRate( int ix ){
    	if( !hasTaxRate( ix )){
    		return BigDecimal.ZERO;
    	}
        return invoice.getTaxSummaries().get(ix).getRate();
    }
    
    public Money getTaxAmount( int ix ){
    	if( !hasTaxRate( ix )){
    		return new Money();
    	}
        return invoice.getTaxSummaries().get(ix).getAmount();
    }
    
    public Boolean hasCcyRate( int ix ){
        return ( invoice.getCurrencyRates().size() > ix && ix >= 0 );
    }
    
    public String getCcyName( int ix ){
        return invoice.getCurrencyRates().get(ix).getCurrencyPair().getCaption();
    }
    
    public BigDecimal getCcyAskRate( int ix ){
        return invoice.getCurrencyRates().get(ix).getAsk();
    }
    
    public BigDecimal getCcyBidRate( int ix ){
        return invoice.getCurrencyRates().get(ix).getBid();
    }
    
    public BigDecimal getBeforeTax(){
        return invoice.getBeforeTax().getValue();
    }
    
    public BigDecimal getTotalTax(){
        return invoice.getTotalTax().getValue();
    }
    
    public BigDecimal getInvoiceTotal(){
        return invoice.getInvoiceTotal().getValue();
    }
    
}
