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
import com.ut.tekir.entities.OrderItem;
import com.ut.tekir.entities.PosCommisionDetail;
import com.ut.tekir.entities.PriceItem;
import com.ut.tekir.entities.Product;
import com.ut.tekir.entities.User;
import com.ut.tekir.entities.ord.TekirOrderNote;
import com.ut.tekir.entities.ord.TekirOrderNoteDetail;
import com.ut.tekir.framework.IEntityBase;
import com.ut.tekir.invoice.yeni.ContactOperation;

/**
 *
 * @author sinan.yumak
 */
@Local
public interface SaleOrderHome<E> extends IEntityBase<E> {

    void createNewLine();
    void deleteLine(OrderItem item);
    void deleteLine(Integer ix);

    TekirOrderNote getSaleOrder();
    void setSaleOrder(TekirOrderNote orderNote);

    void init();
    void manualFlush();

    void selectProduct(Integer ix);
    void print();

    void setAddress();
    
	ContactAddress getSelectedAddress();
	void setSelectedAddress(ContactAddress selectedAddress);
	
	void calculateEverything();
	
	List<PosCommisionDetail> prepareCommisionPeriodForLine(Integer ix);
	
	void deletePaymentTableDetail(Integer ix);
	void createNewPaymentTableDetail();

	void addDocumentDiscountToItems();
	
	void createDocumentDiscountLine();
	
	TekirOrderNoteDetail getSelectedDetail();
	void setSelectedDetail(TekirOrderNoteDetail selectedDetail);
	
	Product getSelectedDiscountExpense();
	void setSelectedDiscountExpense(Product selectedDiscountExpense);
	
	void createDiscountLine(Integer ix);
	
	void addDiscountToItem();
	
	void addContactDocumentDiscount();

	void toggleMiniTable();
	Boolean getShowMiniTable();
	
	void selectProductWithBarcode(Integer ix);
	
	PriceItem getSelectedPriceItem();
	void setSelectedPriceItem(PriceItem selectedPriceItem);

	List<PriceItem> getPriceItemList();

	void setPriceItemList(List<PriceItem> priceItemList);
	
	void refreshInvoiceWithSelectedPriceItem();
	
	void updatePriceItemList();
	
	User getAuthorizedUser();
	
	void setAuthorizedUser(User authorizedUser);
	
	boolean isRequestDiscountPermission();

	void setRequestDiscountPermission(boolean requestDiscountPermission);
	
	void createDiscountAdditionLine();
	void createExpenseAdditionLine();	
	void createDocumentExpenseLine();
	void createExpenseLine(Integer ix);
	void addDocumentExpenseToItems();
	void addExpenseToItem();
	void addDiscountExpenseAdditionToItem();

	ContactOperation getContactOperation();
	void setContactOperation(ContactOperation contactOperation);

	List<String> getZeroLineAmountWarnings();
	boolean hasZeroLineAmountWarning();
	void detailValidations();
	void updateDetailCurrencies();

}
