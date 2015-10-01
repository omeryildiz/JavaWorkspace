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

import com.ut.tekir.entities.ChequeStatus;
import com.ut.tekir.entities.DocumentType;
import com.ut.tekir.entities.PromissoryNote;

@Local
public interface PromissoryAction {

	public Boolean changePosition(PromissoryParamModel cpm);
	public List<Boolean> changePosition(List<PromissoryParamModel> ppmList);
	
	public boolean isValidAction(PromissoryNote promissory, ChequeStatus newStatus);
	
	boolean alreadySaved(PromissoryNote promissory, DocumentType documentType, Long documentId);
	
	@Remove @Destroy
    public void destroy();

}