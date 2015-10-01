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
import javax.ejb.Remove;

import org.jboss.seam.annotations.Destroy;

import com.ut.tekir.entities.Cheque;
import com.ut.tekir.entities.ChequeStatus;
import com.ut.tekir.entities.DocumentType;

@Local
public interface ChequeAction {

	public Boolean changePosition(ChequeParamModel cpm);
	public List<Boolean> changePosition(List<ChequeParamModel> cpmList);
	
	public boolean isValidAction(Cheque cheque, ChequeStatus newStatus);
	
	boolean alreadySaved(Cheque cheque, DocumentType documentType, Long documentId);
	
	@Remove @Destroy
    public void destroy();

}