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

import javax.ejb.Local;

import com.ut.tekir.framework.IBrowserBase;

/**
 *
 * @author sinan.yumak
 */
@Local
public interface SaleInvoiceBrowse<E, F> extends IBrowserBase<E, F> {

    void refreshResults();
    void pdf();
    void detailedPdf();
    void xls();
 
    Integer getExid();
	void setExid(Integer exid);

	boolean isOnlyReturned();
	void setOnlyReturned(boolean onlyReturned);	
}
