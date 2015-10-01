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

package com.ut.tekir.finance;

import java.io.Serializable;

import com.ut.tekir.entities.ExpenseNote;
import com.ut.tekir.entities.inv.TekirInvoice;

import javax.ejb.Local;

/**
 *
 * @author yigit
 */
@Local
public interface TaxTxnAction extends Serializable {

    void init();

    void destroy();
    
    void saveExpenseNote(ExpenseNote expenseNote);
    
    Boolean deleteExpenseNote(ExpenseNote expenseNote);

    void createTaxTxnRecordsForInvoice(TekirInvoice doc);
    
    void deleteTaxTxnRecordsForInvoice(TekirInvoice doc);

}
