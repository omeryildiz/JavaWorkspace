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

package com.ut.tekir.invoice.yeni;

import java.util.List;

import javax.ejb.Local;

import com.ut.tekir.entities.ContactAddress;
import com.ut.tekir.entities.ControlType;
import com.ut.tekir.entities.Payment;
import com.ut.tekir.entities.PosCommisionDetail;
import com.ut.tekir.entities.PriceItem;
import com.ut.tekir.entities.Product;
import com.ut.tekir.entities.User;
import com.ut.tekir.entities.inv.TekirInvoiceDetail;
import com.ut.tekir.entities.ord.TekirOrderNote;
import com.ut.tekir.entities.shp.TekirShipmentNote;
import com.ut.tekir.framework.IEntityBase;
import com.ut.tekir.options.OptionKey;

/**
 * 
 * @author sinan.yumak
 */
@Local
public interface RetailSaleInvoiceHome<E> extends IEntityBase<E> {

	void init();
	
	void createNew();
	
	void createNewDetail();
	
	void deleteLine(Integer ix);

	void calculateEverything();

	void selectProduct(Integer ix);
	
	void selectProductWithBarcode(Integer ix);

	PriceItem getSelectedPriceItem();
	void setSelectedPriceItem(PriceItem selectedPriceItem);

	List<PriceItem> getPriceItemList();

	void setPriceItemList(List<PriceItem> priceItemList);
	
	void refreshInvoiceWithSelectedPriceItem();
	
	String getBarcodeInput();
	
	void setBarcodeInput(String barcodeInput);

	void createNewLineWithBarcode();

	void sendToPosPrinter();
	
	Double getBarcodeQuantity();
	
	void setBarcodeQuantity(Double barcodeQuantity);
	
	void updatePriceItemList();
	
	void findUserPosAndAccount();
	
	User getClerk();
	void setClerk(User clerk);	
	
	void createNewPaymentTableDetail();
	
	void deletePaymentTableDetail(Integer ix);
	
	List<PosCommisionDetail> prepareCommisionPeriodForLine(Integer ix);
	
	Boolean getShowMiniTable();
	
	void toggleMiniTable();
	
	TekirInvoiceDetail getSelectedDetail();
	void setSelectedDetail(TekirInvoiceDetail selectedDetail);

	Product getSelectedDiscountExpense();
	void setSelectedDiscountExpense(Product selectedDiscountExpense);
	
	ContactAddress getSelectedAddress();
	
	void setSelectedAddress(ContactAddress selectedAddress);
	
	User getAuthorizedUser();	
	void setAuthorizedUser(User authorizedUser);
	
	boolean isRequestDiscountPermission();
	void setRequestDiscountPermission(boolean requestDiscountPermission);
	
	void addContactDocumentDiscount();

	void setAddress();
	
	void createDocumentDiscountLine();
	
	void addDocumentDiscountToItems();
	
	void createDiscountLine(Integer ix);

	void addDiscountToItem();
	
	List<TekirOrderNote> getOrderList();
	void setOrderList(List<TekirOrderNote> orderList);
	
	List<TekirOrderNote> findOrders();
	
	void selectOrderNote(int ix);
	void removeOrderNote(int ix);

	void print();
	
	void createDiscountAdditionLine();
	void createExpenseAdditionLine();
	void addDiscountExpenseAdditionToItem();
	
	void createExpenseLine(Integer ix);
	void addDocumentExpenseToItems();
	void addExpenseToItem();
	void createDocumentExpenseLine();
	
	ContactOperation getContactOperation();
	void setContactOperation(ContactOperation contactOperation);

	List<String> getZeroLineAmountWarnings();
	LimitationChecker getLimitationChecker();
	boolean hasWarningOrRequiredLimitation();
	boolean hasRequiredLimitation();
	LimitationMessages getLimitationMessages();
	ControlType getOption(OptionKey key);

	boolean hasZeroLineAmountWarning();
	
	void detailValidations();
	
	Payment getPaymentOfInvoice();
	
	TekirShipmentNote getShipmentOfInvoice();

	void updateDetailCurrencies();	
	
	boolean isCheckingRequired();
}
