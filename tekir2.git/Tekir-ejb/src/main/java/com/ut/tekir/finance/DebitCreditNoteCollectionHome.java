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

import com.ut.tekir.entities.DebitCreditNote;
import com.ut.tekir.entities.DebitCreditNoteItem;
import com.ut.tekir.framework.IEntityBase;
import javax.ejb.Local;

/**
 *
 * @author haky
 */
@Local
public interface DebitCreditNoteCollectionHome<E> extends IEntityBase<E> {

    void createNewLine();

    void deleteLine(DebitCreditNote item);
    void deleteLine(Integer ix);
    
    DebitCreditNote getDebitCreditNoteCollection();

    void init();

    void manualFlush();

    void setDebitCreditNoteCollection(DebitCreditNote payment);

}
