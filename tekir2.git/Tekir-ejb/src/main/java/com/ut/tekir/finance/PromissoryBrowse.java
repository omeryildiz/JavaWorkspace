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

import com.ut.tekir.entities.ChequeStatus;
import com.ut.tekir.framework.IBrowserBase;

/**
 *
 * @author selman
 */
@Local
public interface PromissoryBrowse<E, F> extends IBrowserBase<E, F> {

	void refreshResults();

	void pdf();

	void detailedPdf();

	ChequeStatus[] getLastStatus();

	String close();

	void selectedPromissory(Long id);

	void setCallerObserveString(String callerString);
}
