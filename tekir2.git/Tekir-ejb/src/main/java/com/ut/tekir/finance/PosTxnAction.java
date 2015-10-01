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

import javax.ejb.Local;

import com.ut.tekir.entities.BankToContactTransfer;
import com.ut.tekir.entities.Payment;
import com.ut.tekir.entities.PaymentTable;

/**
 *
 * @author sinan.yumak
 */
@Local
public interface PosTxnAction extends Serializable {

    void initComponent();
    void destroy();

    void createPosTxnRecordsForCollection(Payment aCollection);
    void deletePosTxnRecordsForCollection(Payment aCollection);
 
    void deletePosTxnRecordsForContactToBankTransfer(BankToContactTransfer doc);
    void updateTradeinFields(PaymentTable paymentTable, boolean newState);
}
