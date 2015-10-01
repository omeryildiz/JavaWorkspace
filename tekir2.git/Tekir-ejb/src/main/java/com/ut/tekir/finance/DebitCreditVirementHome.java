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

import com.ut.tekir.entities.DebitCreditVirement;
import com.ut.tekir.entities.DebitCreditVirementItem;
import com.ut.tekir.framework.IEntityBase;
import javax.ejb.Local;

/**
 *
 * @author haky
 */
@Local
public interface DebitCreditVirementHome<E> extends IEntityBase<E> {
    
    DebitCreditVirement getDebitCreditVirement();
    void setDebitCreditVirement(DebitCreditVirement debitCreditVirement);

    public void createNewLine();
    public void deleteLine( DebitCreditVirementItem item );
    public void deleteLine( Integer ix );
    
    void init();
    void manualFlush();
}
