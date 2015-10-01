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

import org.hibernate.criterion.DetachedCriteria;

import com.ut.tekir.framework.IBrowserBase;

/**
 * @author yigit
 *
 */
@Local
public interface PromissoryCollectionAtBankPayrollBrowse<E, F> extends IBrowserBase<E, F> {

	void clearBankAccount();

	DetachedCriteria buildCriteria();
	void refreshResults();
	void pdf();
	void detailedPdf();

}