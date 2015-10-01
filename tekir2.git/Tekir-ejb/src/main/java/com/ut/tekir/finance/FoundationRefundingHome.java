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

import javax.ejb.Local;

import com.ut.tekir.framework.IBrowserBase;

/**
 * Kurum geri ödeme işlemlerinin yapıldığı home sınıfıdır.
 * @author sinan.yumak
 *
 */
@Local
public interface FoundationRefundingHome<E, F> extends IBrowserBase<E, F> {

	void init();
	void manualFlush();
	void xls();
	void pdf();

	String createInvoiceFromSelectedItems();

}
