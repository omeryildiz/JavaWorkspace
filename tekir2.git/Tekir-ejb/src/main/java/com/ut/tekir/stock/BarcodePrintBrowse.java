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

import com.ut.tekir.framework.IBrowserBase;

/**
 * @author sinan.yumak
 *
 */
public interface BarcodePrintBrowse <E, F> extends IBrowserBase<E, F>{

	String close();
	
	void sendToBarcodePrinter();

	void deleteLine(int rowKey);
	
	void refreshResults();
}
