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
import com.ut.tekir.entities.PromissoryNote;

/**
 *
 * @author selman
 */
@Local
public interface PromissoryPopupHome {

	void init();

	PromissoryNote getPromissory();

	void setPromissory(PromissoryNote promissory);

	ChequeStatus[] getChequeStatus();

	String close();

	void destroy();

	void createNew();

	Boolean getIsClient();

	void setIsClient(Boolean isClient);

	void setCallerObserveString(String callerObserveString);
	
	void cancel();

}
