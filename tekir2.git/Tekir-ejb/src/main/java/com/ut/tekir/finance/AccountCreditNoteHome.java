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

import javax.ejb.Local;

import com.ut.tekir.entities.AccountCreditNote;
import com.ut.tekir.framework.IEntityBase;

/**
 *
 * @author sinan.yumak
 */
@Local
public interface AccountCreditNoteHome<E> extends IEntityBase<E> {

	void init();
	void manualFlush();
    void createNewLine();
    void deleteLine(Integer ix);

    AccountCreditNote getAccountCreditNote();
    void setAccountCreditNote(AccountCreditNote accountCreditNote);
    
    void recalculate();
}
