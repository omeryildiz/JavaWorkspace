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

import com.ut.tekir.entities.PortfolioToPortfolioTransfer;
import com.ut.tekir.entities.PortfolioToPortfolioTransferItem;
import com.ut.tekir.framework.IEntityBase;
import javax.ejb.Local;

/**
 *
 * @author bilge
 */
@Local
public interface PortfolioToPortfolioTransferHome<E> extends IEntityBase<E>  {

    void init();
    void manualFlush();
    
    void createNewLine();
    void deleteLine(PortfolioToPortfolioTransferItem item);
    void deleteLine(Integer ix);

    void selectSecurity( Integer ix );
    
    PortfolioToPortfolioTransfer getPortfolioToPortfolioTransfer();
    void setPortfolioToPortfolioTransfer(PortfolioToPortfolioTransfer portfolioToPortfolioTransfer);
    

}
