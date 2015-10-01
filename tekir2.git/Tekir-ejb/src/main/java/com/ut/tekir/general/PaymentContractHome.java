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

import com.ut.tekir.entities.PaymentContract;
import com.ut.tekir.framework.IEntityBase;

/**
 *
 * @author sinan.yumak
 */
@Local
public interface PaymentContractHome<E> extends IEntityBase<E> {

    void createNewLine();

    PaymentContract getPaymentContract();
    void setPaymentContract(PaymentContract paymentContract);

    void deleteCommisionLine(Integer ix);
    void createNewCommisionLine(Integer ix);

    void deleteLine(Integer ix);

    void init();

    void manualFlush();
}
