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

package com.ut.tekir.finance;

import java.io.Serializable;
import java.util.Date;

import javax.ejb.Local;

import com.ut.tekir.entities.Bank;
import com.ut.tekir.entities.Foundation;
import com.ut.tekir.entities.PaymentContract;
import com.ut.tekir.entities.PaymentType;
import com.ut.tekir.entities.Pos;

/**
 * Ödeme sözleşmesi bulmak için kullanacağımız bileşendir.
 * @author sinan.yumak
 *
 */
@Local
public interface ContractFinder extends Serializable {

	/**
	 * Verilen kuruma uygun sözleşmeyi bularak döndürür.
	 * @param aFoundation, sözleşmesi aranacak kurum.
	 * @param operationDate, işlem tarihi.
	 * @return paymentContract, bulunan sözleşme.
	 */
	PaymentContract findContract(Foundation aFoundation, Date operationDate);
	
	/**
	 * Verilen posa uygun sözleşmeyi bularak döndürür.
	 * @param aPos, sözleşmesi aranacak pos.
	 * @param operationDate, işlem tarihi.
	 * @param paymentType, sözleşme ödeme tipi.
	 * @return paymentContract, bulunan sözleşme.
	 */
	PaymentContract findContract(Pos aPos,PaymentType paymentType, Date operationDate, Bank bank);
}
