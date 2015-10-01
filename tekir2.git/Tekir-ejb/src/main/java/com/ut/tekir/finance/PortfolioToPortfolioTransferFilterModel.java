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

import com.ut.tekir.entities.Portfolio;
import com.ut.tekir.framework.DefaultDocumentFilterModel;

/**
 *
 * @author haky
 */
public class PortfolioToPortfolioTransferFilterModel extends DefaultDocumentFilterModel{

    private Portfolio fromPortfolio;
    private Portfolio toPortfolio;

    public Portfolio getFromPortfolio() {
        return fromPortfolio;
    }

    public void setFromPortfolio(Portfolio fromPortfolio) {
        this.fromPortfolio = fromPortfolio;
    }

    public Portfolio getToPortfolio() {
        return toPortfolio;
    }

    public void setToPortfolio(Portfolio toPortfolio) {
        this.toPortfolio = toPortfolio;
    }
}
