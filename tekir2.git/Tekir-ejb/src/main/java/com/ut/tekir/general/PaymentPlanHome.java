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

package com.ut.tekir.general;

import javax.ejb.Local;

import com.ut.tekir.framework.IEntityHome;

/**
 *
 * @author selman
 */
@Local
public interface PaymentPlanHome<PaymentPlan> extends IEntityHome<PaymentPlan> {
    
	PaymentPlan getPaymentPlan();
	
    void setPaymentPlan(PaymentPlan paymentPlan);
    
    void initPaymentPlanList();
	
    void createNewLine();
    
    void deleteLine(Integer ix);
    
    void manualFlush();
    
    void destroy();
    
}
