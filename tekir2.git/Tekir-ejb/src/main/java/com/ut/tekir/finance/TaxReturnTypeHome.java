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

import com.ut.tekir.entities.DocumentType;
import com.ut.tekir.framework.IEntityHome;
import com.ut.tekir.general.GeneralSuggestion;

/**
 * @author rustem
 *
 */
@Local
public interface TaxReturnTypeHome<TaxReturnType> extends IEntityHome<TaxReturnType> {
	void init();
	
	void initTaxReturnTypeList();
    TaxReturnType getTaxReturnType();
    void setTaxReturnType(TaxReturnType taxReturnType);
    GeneralSuggestion getGeneralSuggestion();
	void setGeneralSuggestion(GeneralSuggestion generalSuggestion);

	DocumentType[] getDocumentTypeArray();
	void setDocumentTypeArray(DocumentType[] documentTypeArray);

}
