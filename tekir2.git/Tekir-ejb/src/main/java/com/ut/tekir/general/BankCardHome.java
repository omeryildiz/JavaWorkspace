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

import com.ut.tekir.entities.BankCard;
import com.ut.tekir.framework.IEntityHome;

/**
 *
 * @author sinan.yumak
 */
@Local
public interface BankCardHome<E> extends IEntityHome<E>{
    
    BankCard getBankCard();
    void setBankCard(BankCard bankCard);
    
    void initBankCardList();
    
}
