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

import com.ut.tekir.entities.BondPurchaseSale;
import com.ut.tekir.entities.PortfolioToPortfolioTransfer;


/**
 *
 * @author bilge
 */
public interface BondTxnAction {
    
    Boolean savePortfolioToPortfolioTransfer(PortfolioToPortfolioTransfer doc);
    Boolean deletePortfolioToPortfolioTransfer(PortfolioToPortfolioTransfer doc);

    Boolean saveBondSale(BondPurchaseSale doc);
    Boolean deleteBondSale(BondPurchaseSale doc);
    
    Boolean saveBondPurchase(BondPurchaseSale doc);
    Boolean deleteBondPurchase(BondPurchaseSale doc);
    
    void destroy();
    void initComponent();

}
