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
import com.ut.tekir.entities.FoundationTxn;
import com.ut.tekir.entities.PosCommisionDetail;
import com.ut.tekir.entities.Product;
import com.ut.tekir.entities.User;
import com.ut.tekir.entities.inv.TekirInvoice;
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
public interface SaleInvoiceHome<E> extends IEntityBase<E>  {
    void init();
    void manualFlush();

    void print(String templateName);
    
    TekirInvoice getSaleInvoice();
    void setSaleInvoice(TekirInvoice invoice);

	Integer getInvoiceType();
	void setInvoiceType(Integer invoiceType);

	Integer getActionType();
	void setActionType(Integer actionType);
	
	void createDocumentDiscountLine();
	void createDiscountLine(Integer ix);
	void addDocumentDiscountToItems();
	void addDiscountToItem();
	
    void createNewDetail();
	void deleteLine(Integer ix);
	void deleteReturnLine(Integer ix);
	
	TekirInvoiceDetail getSelectedDetail();
	void setSelectedDetail(TekirInvoiceDetail selectedDetail);
	
	Product getSelectedDiscountExpense();
	void setSelectedDiscountExpense(Product selectedDiscountExpense);

	void calculateEverything();

    void selectProduct(Integer ix);
    void selectProductWithBarcode(Integer ix);
    
	void setAddress();
	
	ContactAddress getSelectedAddress();
	void setSelectedAddress(ContactAddress selectedAddress);
	
	void toggleMiniTable();
	
	Boolean getShowMiniTable();

	User getAuthorizedUser();
	void setAuthorizedUser(User authorizedUser);
	
	boolean isRequestDiscountPermission();
	void setRequestDiscountPermission(boolean requestDiscountPermission);
	
	void addContactDocumentDiscount();
	
	List<TekirOrderNote> findOrders();
	void selectOrderNote(int ix);
	void removeOrderNote(int ix);
	
	List<TekirOrderNote> getOrderList();
	void setOrderList(List<TekirOrderNote> orderList);

	List<TekirShipmentNote> getShipmentList();
	void setShipmentList(List<TekirShipmentNote> shipmentList);

	List<TekirShipmentNote> findShipments();
	void selectShipmentNote(int ix);
	void removeShipmentNote(int ix);
	

	List<TekirInvoice> getInvoiceList();
	void setInvoiceList(List<TekirInvoice> invoiceList);

	List<TekirInvoice> findInvoices();
	void removeInvoice(int ix);
	void selectInvoice(int ix);
	
	void sendToPosPrinter();
	
	void createNewPaymentTableDetail();
	List<PosCommisionDetail> prepareCommisionPeriodForLine(Integer ix);
	void deletePaymentTableDetail(Integer ix);
	
	boolean hasPaymentTableExceededInvoiceTotal();
	
	void setDefaultAccount() throws Exception;
	void setDefaultWarehouse() throws Exception;

	void prepareAllProductAndServiceLines();
	
	List<FoundationTxn> getTxnList();
	void setTxnList(List<FoundationTxn> txnList);

	void createExpenseLine(Integer ix);
	void createDocumentExpenseLine();
	void createDiscountAdditionLine();
	void createExpenseAdditionLine();
	void addDocumentExpenseToItems();
	void addDiscountExpenseAdditionToItem();
	void addExpenseToItem();
	
	void preparePaymentPlanItems();

	InvoiceSuggestionFilterModel getFilterModel();
	void setFilterModel(InvoiceSuggestionFilterModel filterModel);

	void updateDocumentCurrency();
	void updateDetailCurrencies();
	
	String createInvoice();
	
	void calculateUnitPrice(int rowKey);
	void calculateForeignUnitPrice(int rowKey);

    void makeEntityValidations();
    boolean hasWarningOrRequiredLimitation();
    boolean hasRequiredLimitation();
    LimitationMessages getLimitationMessages();
    
    ControlType getOption(OptionKey key);
    boolean isCheckingRequired();
	
}
