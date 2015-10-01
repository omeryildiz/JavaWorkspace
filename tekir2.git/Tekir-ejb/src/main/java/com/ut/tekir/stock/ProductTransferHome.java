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

import com.ut.tekir.entities.ProductTransfer;
import com.ut.tekir.entities.ProductTransferItem;
import com.ut.tekir.framework.IEntityBase;
import javax.ejb.Local;

/**
 *
 * @author haky
 */
@Local
public interface ProductTransferHome<E> extends IEntityBase<E>  {

    void init();
    void manualFlush();
    
    void createNewLine();
    void deleteLine(ProductTransferItem item);
    void deleteLine(Integer ix);

    void selectProduct( Integer ix );
    
    ProductTransfer getProductTransfer();
    void setProductTransfer(ProductTransfer productTransfer);
    void print();
    

}
