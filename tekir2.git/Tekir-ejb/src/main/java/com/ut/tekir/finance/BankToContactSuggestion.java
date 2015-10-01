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

import com.ut.tekir.entities.BankToContactTransfer;

/**
 * @author sinan.yumak
 *
 */
@Local
public interface BankToContactSuggestion {

	List<BankToContactTransfer> suggest(BankToContactSuggestionFilterModel model);
	
	void setFilterModel(BankToContactSuggestionFilterModel filterModel);
	
	void destroy();
}
