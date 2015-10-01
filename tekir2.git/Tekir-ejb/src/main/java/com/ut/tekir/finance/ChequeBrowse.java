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

import com.ut.tekir.entities.ChequeStatus;
import com.ut.tekir.framework.IBrowserBase;
import javax.ejb.Local;

/**
 *
 * @author selman
 */
@Local
public interface ChequeBrowse<E, F> extends IBrowserBase<E, F> {

    void refreshResults();
    void pdf();
    void detailedPdf();
    ChequeStatus[] getLastStatus();
    
	Boolean getIsClientCheque();
	void setIsClientCheque(Boolean isClientCheque);

}
