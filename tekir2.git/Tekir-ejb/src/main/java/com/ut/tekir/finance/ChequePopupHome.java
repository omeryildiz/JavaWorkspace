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
import com.ut.tekir.entities.ChequeStub;

/**
 *
 * @author selman
 */
@Local
public interface ChequePopupHome {

	void init();

	void createNew();

	Cheque getCheque();

	void setCheque(Cheque cheque);

	ChequeStatus[] getChequeStatus();

	void setChequeFromChequeStub();

	void setChequeStub(ChequeStub chequeStub);

	ChequeStub getChequeStub();

	void destroy();

	String close();

	void cancel();
	
	Boolean getIsClient();

	void setCallerObserveString(String callerString);
	
	void setIsClient(Boolean isClient);
}
