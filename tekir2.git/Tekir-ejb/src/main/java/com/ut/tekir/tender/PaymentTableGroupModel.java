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

package com.ut.tekir.tender;

import com.ut.tekir.entities.Bank;
import com.ut.tekir.entities.PaymentTableDetail;
import com.ut.tekir.entities.PaymentType;

/**
 * Ödeme tablosu bilgilerini gruplamak için kullanacağımız modeldir.
 * @author sinan.yumak
 *
 */
public class PaymentTableGroupModel {
	private PaymentType type;
	private Bank bank;
	private String creditCardNumber;

	public PaymentTableGroupModel() {
	}

	public PaymentTableGroupModel(PaymentTableDetail item) {
		this.type = item.getPaymentType();
		if (item.getPaymentType().equals(PaymentType.CreditCard)) {
			this.bank = item.getPos().getBank();
			this.creditCardNumber = item.getCreditCardNumber();
		}
	}

	@Override
	public boolean equals(Object obj) {
        if (!(obj instanceof PaymentTableGroupModel)) {
            return false;
        }
        PaymentTableGroupModel model = (PaymentTableGroupModel)obj;

        if (model.getType().equals(PaymentType.Cash) && this.type.equals(model.getType())) return true;
        if (model.getType().equals(PaymentType.CreditCard)) {
        	if (this.type.equals(model.getType()) && 
        			model.getBank().equals(model.getBank()) && 
        			model.getCreditCardNumber().equals(model.getCreditCardNumber())) return true;
        }
        return false;
	}

	@Override
	public int hashCode() {
		int result =1;
		
		if (type != null) {
			result *= type.hashCode();
		}
		if (bank != null && bank.getName() != null) {
			result *= bank.getName().hashCode();
		}
		if (creditCardNumber != null) {
			result *= creditCardNumber.hashCode();
		}
		return result;
	}

	public PaymentType getType() {
		return type;
	}

	public void setType(PaymentType type) {
		this.type = type;
	}

	public String getCreditCardNumber() {
		return creditCardNumber;
	}

	public void setCreditCardNumber(String creditCardNumber) {
		this.creditCardNumber = creditCardNumber;
	}

	public Bank getBank() {
		return bank;
	}

	public void setBank(Bank bank) {
		this.bank = bank;
	}
	
}
