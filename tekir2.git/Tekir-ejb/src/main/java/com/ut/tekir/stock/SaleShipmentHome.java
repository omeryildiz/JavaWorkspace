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

import java.util.List;

import com.ut.tekir.entities.OrderNote;
import com.ut.tekir.entities.ShipmentItem;
import com.ut.tekir.entities.ShipmentNote;
import com.ut.tekir.framework.IEntityBase;
import javax.ejb.Local;

/**
 *
 * @author haky
 */
@Local
public interface SaleShipmentHome<E> extends IEntityBase<E> {

    void createNewLine();

    void deleteLine(ShipmentItem item);

    void deleteLine(Integer ix);

    ShipmentNote getSaleShipment();

    void init();

    void manualFlush();

    void selectProduct(Integer ix);

    void setSaleShipment(ShipmentNote shipmentNote);
    
    List<OrderNote> getOrderNotes();
    
    void setOrderNotes(List<OrderNote> orderNotes);
    
    void buildOrderNotes();
    
    void removeOrderNote(int ix);
    
    void selectOrderNote (int rowKey);
    
    void print();
}
