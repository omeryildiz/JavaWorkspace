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
import com.ut.tekir.entities.Product;
import com.ut.tekir.entities.ShipmentItem;
import com.ut.tekir.entities.User;
import com.ut.tekir.entities.ord.TekirOrderNote;
import com.ut.tekir.entities.shp.TekirShipmentNote;
import com.ut.tekir.entities.shp.TekirShipmentNoteDetail;
import com.ut.tekir.framework.IEntityBase;

/**
 *
 * @author sinan.yumak
 */
@Local
public interface SaleShipmentHome<E> extends IEntityBase<E> {

    void createNewLine();

    void deleteLine(ShipmentItem item);

    void deleteLine(Integer ix);

    TekirShipmentNote getSaleShipment();

    void init();

    void manualFlush();

    void selectProduct(Integer ix);

    void setSaleShipment(TekirShipmentNote shipmentNote);
    
    void print();
    
    void calculateEverything();
    
    void selectProductWithBarcode(Integer ix);
    
    ContactAddress getSelectedAddress();
    void setSelectedAddress(ContactAddress selectedAddress);
    
    void setAddress();
    
	Boolean getShowMiniTable();
	
	void createDocumentDiscountLine();
	
	User getAuthorizedUser();
	void setAuthorizedUser(User authorizedUser);

	Product getSelectedDiscountExpense();
	void setSelectedDiscountExpense(Product selectedDiscountExpense);

	void createDiscountLine(Integer ix);
	
	List<TekirOrderNote> findOrders();
	
	void selectOrderNote(int ix);
	
	void removeOrderNote(int ix);
	
	TekirShipmentNoteDetail getSelectedDetail();
	void setSelectedDetail(TekirShipmentNoteDetail selectedDetail);
	
	void addDiscountToItem();
	
	void addDocumentDiscountToItems();
	
	void toggleMiniTable();

	List<TekirOrderNote> getOrderList();
	void setOrderList(List<TekirOrderNote> orderList);

	List<String> getZeroLineAmountWarnings();
	boolean hasZeroLineAmountWarning();
	void detailValidations();

	void updateDetailCurrencies();
}
