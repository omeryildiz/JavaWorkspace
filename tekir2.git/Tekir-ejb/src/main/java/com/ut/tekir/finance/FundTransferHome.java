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

import com.ut.tekir.entities.FundTransfer;
import com.ut.tekir.entities.FundTransferItem;
import com.ut.tekir.framework.IEntityBase;
import javax.ejb.Local;

/**
 *
 * @author haky
 */
@Local
public interface FundTransferHome<E> extends IEntityBase<E> {

    void createNewLine();
    void deleteLine(FundTransferItem item);
    void deleteLine(Integer ix);

    FundTransfer getFundTransfer();
    void setFundTransfer(FundTransfer fundTransfer);
    
    void init();
    void manualFlush();

    void recalculate();
}
