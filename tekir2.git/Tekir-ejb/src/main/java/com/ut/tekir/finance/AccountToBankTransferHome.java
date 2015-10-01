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

import com.ut.tekir.entities.BankToAccountTransfer;
import com.ut.tekir.framework.IEntityBase;
import javax.ejb.Local;

/**
 *
 * @author haky
 */
@Local
public interface AccountToBankTransferHome<E> extends IEntityBase<E> {

	BankToAccountTransfer getAccountToBankTransfer();

    void init();

    void manualFlush();

    void setAccountToBankTransfer(BankToAccountTransfer accountToBankTransfer);
    void clearBankAccount();
}
