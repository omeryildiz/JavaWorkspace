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

package com.ut.tekir.invoice;

import com.ut.tekir.entities.Invoice;
import com.ut.tekir.entities.InvoiceCurrencyRate;
import com.ut.tekir.entities.Money;
import com.ut.tekir.entities.ShipmentNote;
import com.ut.tekir.framework.IEntityBase;
import java.util.List;
import java.util.Map;
import javax.ejb.Local;

/**
 *
 * @author haky
 */
@Local
public interface SaleInvoiceHome<E> extends IEntityBase<E>  {

    void recalculate();
    
    Map<String, InvoiceCurrencyRate> getCcyRates();
    void setCcyRates(Map<String, InvoiceCurrencyRate> ccyRates) ;
    
    Map<String,Money> getCcyTotals();
    void setCcyTotals(Map<String,Money> ccyTotals);
    
    Map<String, Money> getLocalTotals();
    void setLocalTotals(Map<String, Money> localTotals);
    
    List<String> getCcyList();
    void setCcyList(List<String> ccyList);
            
    List<InvoiceItemModel> getItems();
    void setItems(List<InvoiceItemModel> items);
    
    Invoice getSaleInvoice();
    void setSaleInvoice(Invoice invoice);
    
    void createNewServiceLine();
    void createNewShipmentLine();
    void createNewDiscountLine();
    void createNewExpenseLine();
    
    void deleteLine(InvoiceItemModel item);
    void deleteLine(Integer ix);

    void createNewSubDiscountLine( Integer ix );
    void createNewSubExpenseLine( Integer ix );
    
    void selectLine(Integer ix);

    
    void buildShipmentNotes();
    void selectShipmentNote(int ix);
    void removeShipmentNote(int ix);
    List<ShipmentNote> getShipmentNotes();
    void setShipmentNotes(List<ShipmentNote> shipmentNotes);
    
    Integer getInvoiceType();
    void setInvoiceType(Integer invoiceType);
    void beginShipmentInvoice();
    
    void print();
    
    void init();
    void manualFlush();
    
    Double calculateAmountValue();
}
