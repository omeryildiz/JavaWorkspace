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

import com.ut.tekir.entities.BankToBankTransfer;
import com.ut.tekir.framework.IEntityBase;
import javax.ejb.Local;

/**
 *
 * @author haky
 */
@Local
public interface BankToBankTransferHome<E> extends IEntityBase<E> {

    BankToBankTransfer getBankToBankTransfer();
    void setBankToBankTransfer(BankToBankTransfer bankToBankTransfer);
    
    void selectBankAccount(Integer ix);
    
    void init();
    void manualFlush();
    
    void initToAmountCurrency();
    void initFromAmountCurrency();
    void clearFromBankAccount();   
    void clearToBankAccount();   

}
