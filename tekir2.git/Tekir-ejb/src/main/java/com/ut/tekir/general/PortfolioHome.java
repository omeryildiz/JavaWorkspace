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

import com.ut.tekir.entities.Portfolio;
import com.ut.tekir.framework.IEntityHome;
import java.util.List;

/**
 *
 * @author haky
 */
public interface PortfolioHome<E> extends IEntityHome<E> {

   
    Portfolio getPortfolio();
    void setPortfolio(Portfolio portfolio);
    
    List<Portfolio> getEntityList();
    void setEntityList(List<Portfolio> entityList);
    
    void initPortfolioList();
    
    void clearBankAccount(); 
    
}
