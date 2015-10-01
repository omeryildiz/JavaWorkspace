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

import com.ut.tekir.entities.Contact;
import com.ut.tekir.entities.Payment;
import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @author nexus
 */
public class PaymentPrintMasterModel {

    private Payment payment;

    public PaymentPrintMasterModel( Payment payment ){
        this.payment = payment;
    }

    public String getSerial(){
        return getPayment().getSerial();
    }

    public String getReference(){
        return getPayment().getReference();
    }

    public String getCode(){
        return getPayment().getCode();
    }

    public String getInfo(){
        return getPayment().getInfo();
    }

    public Date getDate(){
        return getPayment().getDate();
    }

    public Date getTime(){
        return getPayment().getTime();
    }

    public Contact getContact() {
        return getPayment().getContact();
    }

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    public BigDecimal getPaymentTotal(){
        return getPayment().getTotalAmount().getValue();
        //getPayment().getTotals().getLocalTotal();
    }

}
