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

import java.math.BigDecimal;

import com.ut.tekir.entities.Tax;
import com.ut.tekir.entities.TaxKind;
import com.ut.tekir.framework.IEntityHome;

/**
 *
 * @author haky
 */
public interface TaxHome<E> extends IEntityHome<E> {
    
    Tax getTax();
    void setTax(Tax tax);

    BigDecimal getRate();
    void setRate(BigDecimal rate);
    
    void initTaxList();
    
    void createNewLine();
    void deleteLine(Integer ix);

    TaxKind[] getTaxKindList();
}
