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

import org.hibernate.criterion.DetachedCriteria;
import org.jboss.seam.annotations.Observer;

import com.ut.tekir.entities.Account;
import com.ut.tekir.entities.Cheque;
import com.ut.tekir.framework.IBrowserBase;

@Local
public interface ChequePaymentToAccount<E, F> extends IBrowserBase<E, F> {

	String savePosition();

	DetachedCriteria buildCriteria();

	@Observer("refreshBrowser:com.ut.tekir.entities.ChequeFilterModel")
	void refreshResults();

	Account getAccount();

	void setAccount(Account account);
	
	String close();
	
	List<Cheque> getChequeList();

	void setChequeList(List<Cheque> chequeList);

}