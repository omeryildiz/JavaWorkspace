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

package com.ut.tekir.stock.yeni;

import java.util.List;

import javax.ejb.Local;

import com.ut.tekir.entities.ContactAddress;
import com.ut.tekir.entities.ShipmentItem;
import com.ut.tekir.entities.shp.TekirShipmentNote;
import com.ut.tekir.framework.IEntityBase;

/**
 *
 * @author sinan.yumak
 */
@Local
public interface PurchaseShipmentHome<E> extends IEntityBase<E> {

    void createNewLine();
    
    void deleteLine(ShipmentItem item);

    void deleteLine(Integer ix);

    TekirShipmentNote getPurchaseShipment();
    
    void setPurchaseShipment(TekirShipmentNote shipmentNote);

    void init();

    void manualFlush();

    void selectProduct(Integer ix);
        
    void calculateEverything();
    
    void selectProductWithBarcode(Integer ix);

    void setAddress();

    ContactAddress getSelectedAddress();
	void setSelectedAddress(ContactAddress selectedAddress);
	
	void print();

	List<String> getZeroLineAmountWarnings();
	boolean hasZeroLineAmountWarning();
	void detailValidations();

	void updateDetailCurrencies();
}
