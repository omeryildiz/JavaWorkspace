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

import com.ut.tekir.entities.ForeignDocumentBase;

/**
 * Dökümanların ortak hesaplamalarının(kurlar, toplamlar vb.) yapıldığı home bileşenidir.
 * @author sinan.yumak
 *
 */
public interface DocumentCalculationHome {

	void fillCurrencyRates(ForeignDocumentBase document);
	
	void calculate();
	
	void calculateDocumentTotal(ForeignDocumentBase document);
}
