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

package com.ut.tekir.service;

import com.ut.tekir.entities.Product;
import com.ut.tekir.framework.IEntityBase;
import javax.ejb.Local;

/**
 *
 * @author haky
 */
@Local
public interface ServiceHome<E> extends IEntityBase<E> {

    void init();

    Product getProduct();

    void setProduct(Product product);
    
    void createNewLine();
    void manualFlush();
    void deleteLine(Integer ix);
    void createDefaultUnitLine();
    
    Boolean isDefault();
}
