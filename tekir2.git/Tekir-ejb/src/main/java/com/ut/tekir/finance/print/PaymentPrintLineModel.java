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

package com.ut.tekir.finance.print;

import java.math.BigDecimal;
import java.util.Date;

import com.ut.tekir.entities.Payment;
import com.ut.tekir.entities.PaymentItem;
import com.ut.tekir.util.NumberToText;
import com.ut.tekir.util.NumberToTextTR;

/**
 *
 * @author nexus
 */
public class PaymentPrintLineModel {

    private PaymentPrintMasterModel master;
    private PaymentItem line;


    public PaymentPrintLineModel( PaymentPrintMasterModel master, PaymentItem line ){
        this.master = master;
        this.line = line;
    }

    public String getLineInfo() {
        return line.getInfo();
    }

    public String getLineCode() {
        return line.getLineCode();
    }

    public BigDecimal getAmountLocal(){
        return line.getAmount().getLocalAmount();
    }

    public BigDecimal getAmount(){
        return line.getAmount().getValue();
    }
    public String getAmountCcy(){
        return line.getAmount().getCurrency();
    }

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

    public Date getDate(){
        return master.getDate();
    }

    public Date getTime(){
        return master.getTime();
    }

    public String getContactName() {
                return master.getContact().getFullname(); 
    }

    public String getCustomerCompany(){
        return master.getContact().getCompany();
    }
    
    public Boolean getCustomerIsPerson(){
        return master.getContact().getPerson();
    }

    public Payment getPayment() {
        return master.getPayment();
    }

    public void setPayment(Payment payment) {
        master.setPayment(payment);
    }

    public String getTotalAsText(){
        NumberToText nbt = new NumberToTextTR();
    	return nbt.convert(getPaymentTotal().doubleValue(), "TL", "KR");
        //return "false";
    }

    public BigDecimal getPaymentTotal(){
        return master.getPaymentTotal();
    }

}
