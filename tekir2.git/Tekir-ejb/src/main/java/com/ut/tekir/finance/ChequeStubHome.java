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

import com.ut.tekir.entities.Bank;
import com.ut.tekir.entities.BankBranch;
import javax.ejb.Local;

import com.ut.tekir.entities.ChequeStub;
import com.ut.tekir.framework.IEntityBase;

/**
 *
 * @author selman
 */
@Local
public interface ChequeStubHome<E> extends IEntityBase<E> {

    void init();
    ChequeStub getChequeStub();
    void setChequeStub(ChequeStub chequeStub);
    Bank getBank();
    void setBank(Bank bank) ;
    BankBranch getBankBranch();
    void setBankBranch(BankBranch bankBranch);
	void clearBankAccount();
}
