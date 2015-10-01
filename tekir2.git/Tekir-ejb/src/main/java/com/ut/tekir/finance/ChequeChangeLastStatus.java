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

import com.ut.tekir.entities.Cheque;
import com.ut.tekir.entities.ChequeStatus;
import com.ut.tekir.framework.IBrowserBase;

@Local
public interface ChequeChangeLastStatus<E, F> extends IBrowserBase<E, F>{

	void refreshResults();
    
	ChequeStatus getNewStatus();

	void setNewStatus(ChequeStatus newStatus);

	Boolean getIsClientCheque();

	void setIsClientCheque(Boolean isClientCheque);
	
	void setNewStatusOfCheque(Cheque cheque);
	
	String changeLastStatus();
	
	String close();
}