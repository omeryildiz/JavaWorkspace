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

package com.ut.tekir.report;

import javax.ejb.Local;

import com.ut.tekir.framework.IBrowserBase;

/**
 * 
 * @author rustem.topcu
 */
@Local
public interface CreditCardPosCommissionReport <E,F> extends IBrowserBase <E, F> {
	
	void init();
    void xls();
    void pdf();
    void commissionPdf();
    String createTransferDocumentFromSelectedItems();
}
