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
import com.ut.tekir.entities.PosCommisionDetail;
import com.ut.tekir.entities.Product;
import com.ut.tekir.entities.User;
import com.ut.tekir.entities.inv.TekirInvoice;
import com.ut.tekir.entities.inv.TekirInvoiceDetail;
import com.ut.tekir.entities.shp.TekirShipmentNote;
import com.ut.tekir.framework.IEntityBase;
import com.ut.tekir.options.OptionKey;

/**
 *
 * @author sinan.yumak
 */
@Local
public interface PurchaseInvoiceHome<E> extends IEntityBase<E> {

    void createNewDetail();
    void deleteLine(Integer ix);
    void deleteReturnLine(Integer ix);

    TekirInvoice getPurchaseInvoice();
    void setPurchaseInvoice(TekirInvoice invoice);

    void init();
    void manualFlush();

    void selectProduct(Integer ix);
    void print(String fileName);

	void calculateEverything();
		
	void addDocumentDiscountToItems();
	
	void createDocumentDiscountLine();
	
	TekirInvoiceDetail getSelectedDetail();
	void setSelectedDetail(TekirInvoiceDetail selectedDetail);
	
	Product getSelectedDiscountExpense();
	void setSelectedDiscountExpense(Product selectedDiscountExpense);
	
	void createDiscountLine(Integer ix);
	
	void addDiscountToItem();
	
	void addContactDocumentDiscount();

	void setAddress();
	
	ContactAddress getSelectedAddress();
	void setSelectedAddress(ContactAddress selectedAddress);
	
	void toggleMiniTable();
	
	Boolean getShowMiniTable();
	
	void selectProductWithBarcode(Integer ix);

	User getAuthorizedUser();
	void setAuthorizedUser(User authorizedUser);
	
	boolean isRequestDiscountPermission();
	void setRequestDiscountPermission(boolean requestDiscountPermission);
	
	Integer getInvoiceType();
	void setInvoiceType(Integer invoiceType);
	
	
	Integer getActionType();
	void setActionType(Integer actionType);


	List<TekirShipmentNote> getShipmentList();
	void setShipmentList(List<TekirShipmentNote> shipmentList);
	
	List<TekirShipmentNote> findShipments();
	void removeShipmentNote(int ix);
	void selectShipmentNote(int ix);


	List<TekirInvoice> getInvoiceList();
	void setInvoiceList(List<TekirInvoice> invoiceList);

	List<TekirInvoice> findInvoices();
	void removeInvoice(int ix);
	void selectInvoice(int ix);
	
	void createNewPaymentTableDetail();
	List<PosCommisionDetail> prepareCommisionPeriodForLine(Integer ix);
	void deletePaymentTableDetail(Integer ix);
	
	void sendToPosPrinter();
	
	void preparePaymentPlanItems();

	void checkPurchasePrices();
	
	void createDocumentExpenseLine();
	void createDiscountAdditionLine();
	void createExpenseAdditionLine();
	void createExpenseLine(Integer ix);
	
	void addDocumentExpenseToItems();
	void addDiscountExpenseAdditionToItem();

	void detailValidations();

	InvoiceSuggestionFilterModel getFilterModel();
	void setFilterModel(InvoiceSuggestionFilterModel filterModel);

	void calculateUnitPrice(int rowKey);
	void calculateForeignUnitPrice(int rowKey);

	void updateDocumentCurrency();
	void updateDetailCurrencies();
	void addExpenseToItem();
	
    void makeEntityValidations();
    boolean hasWarningOrRequiredLimitation();
    boolean hasRequiredLimitation();
    LimitationMessages getLimitationMessages();

    void sendToBarcodeSpool();
    
    ControlType getOption(OptionKey key);
    
    boolean isCheckingRequired();
}
