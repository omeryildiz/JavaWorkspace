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

import java.util.List;

import javax.ejb.Local;

import com.ut.tekir.entities.inv.TekirInvoice;
import com.ut.tekir.invoice.yeni.InvoiceSuggestionFilterModel;

/**
 * @author sinan.yumak
 *
 */
@Local
public interface InvoiceSuggestion {

	List<TekirInvoice> suggestInvoice();
	List<TekirInvoice> suggestInvoice(InvoiceSuggestionFilterModel model);
	
	void setFilterModel(InvoiceSuggestionFilterModel filterModel);
	
	void destroy();
}
