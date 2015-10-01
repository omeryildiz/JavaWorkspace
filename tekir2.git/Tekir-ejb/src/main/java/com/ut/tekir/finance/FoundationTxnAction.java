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

import javax.ejb.Local;

import com.ut.tekir.entities.inv.TekirInvoice;
import com.ut.tekir.entities.ord.TekirOrderNote;

/**
 *
 * @author sinan.yumak
 */
@Local
public interface FoundationTxnAction extends Serializable {

    void init();

    void destroy();

    void createFoundationTxnRecordsForInvoice(TekirInvoice doc);
    void deleteFoundationTxnRecordsForInvoice(TekirInvoice doc);

    void createFoundationTxnRecordsForOrderNote(TekirOrderNote doc);
    void deleteFoundationTxnRecordsForOrderNote(TekirOrderNote doc);
    
}
