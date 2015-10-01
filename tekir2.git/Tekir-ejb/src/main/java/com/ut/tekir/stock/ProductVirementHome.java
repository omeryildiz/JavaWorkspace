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

package com.ut.tekir.stock;

import com.ut.tekir.entities.ProductVirement;
import com.ut.tekir.entities.ProductVirementItem;
import com.ut.tekir.framework.IEntityBase;
import javax.ejb.Local;

/**
 *
 * @author nexus
 */
@Local
public interface ProductVirementHome<E> extends IEntityBase<E> {
    
    ProductVirement getProductVirement();
    void setProductVirement(ProductVirement productVirement);

    public void createNewLine();
    public void deleteLine( ProductVirementItem item );
    public void deleteLine( Integer ix );
    
    void selectFromProduct( Integer ix );
    void selectToProduct( Integer ix );
    
    void init();
    void manualFlush();
}
