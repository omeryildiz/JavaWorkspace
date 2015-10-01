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

import com.ut.tekir.contact.ContactModel;
import com.ut.tekir.entities.BankAccount;
import com.ut.tekir.framework.IEntityHome;

/**
 *
 * @author haky
 */
public interface BankAccountHome<E> extends IEntityHome<E>{
    
    BankAccount getBankAccount();

    void setBankAccount(BankAccount bankAccount);
    
    void initBankAccountList();

    void calculateWorkEndDate();
    
    ContactModel getAccountOwner(BankAccount bankAccount);
}
