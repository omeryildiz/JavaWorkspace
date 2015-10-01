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

import java.util.List;

import javax.ejb.Local;

import com.ut.tekir.entities.PaymentActionType;
import com.ut.tekir.entities.PaymentCommission;

/**
 *
 * @author sinan.yumak
 */
@Local
public interface PaymentCommisionSuggestion {

    List<PaymentCommission> suggestPaymentCommission(Object event);
    
	List<PaymentCommission> getPaymentCommissionList();

	void setPaymentCommissionList(List<PaymentCommission> paymentCommission);
    
    void selectPaymentCommissionList();
    
    String getCode();
    void setCode(String code);

    String getName();
	void setName(String name);
	
	PaymentActionType getPaymentActionType();
	void setPaymentActionType(PaymentActionType paymentActionType);
    
	void destroy();
}
