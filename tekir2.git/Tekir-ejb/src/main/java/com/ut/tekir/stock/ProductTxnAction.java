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

import com.ut.tekir.entities.CountNote;
import com.ut.tekir.entities.ExpenseNote;
import com.ut.tekir.entities.ProductTransfer;
import com.ut.tekir.entities.ProductVirement;
import com.ut.tekir.entities.ShipmentNote;
import com.ut.tekir.entities.SpendingNote;
import com.ut.tekir.entities.ord.TekirOrderNote;
import com.ut.tekir.entities.shp.TekirShipmentNote;

/**
 *
 * @author haky
 */
public interface ProductTxnAction {

    void saveShipmentNote(ShipmentNote doc);
    void deleteShipmentNote(ShipmentNote doc);
    
    void saveProductTransfer(ProductTransfer doc);
    Boolean deleteProductTransfer(ProductTransfer doc);
    
    Boolean saveProductVirement(ProductVirement doc);
    Boolean deleteProductVirement(ProductVirement doc);
    
    void saveSpendingNote(SpendingNote doc);
    void deleteSpendingNote(SpendingNote doc);
    
    void saveExpenseNote(ExpenseNote expenseNote);
    void deleteExpenseNote(ExpenseNote expenseNote);
    
    void destroy();
    void initComponent();

    void createProductTxnRecordsForShipment(TekirShipmentNote doc);
    void deleteProductTxnRecordsForShipmentNote(TekirShipmentNote doc);

    void createProductTxnRecordsForOrderNote(TekirOrderNote doc);
    void deleteProductTxnRecordsForOrderNote(TekirOrderNote doc);
    
    void createProductTxnRecordsForCountNote(CountNote doc, List<CountNoteCompareModel> compareList);
    void deleteCountNote(CountNote countNote);
}
