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

import com.ut.tekir.entities.Security;

/**
 *
 * @author huseyin
 */
public interface SecuritySuggestion {

    List<Security> suggestSecurity(Object event);
    
    @SuppressWarnings("unchecked")
	List getSecurityList();
    @SuppressWarnings("unchecked")
	void setSecurityList(List securityList);
    
    void selectSecurityList();
    
    String getIsin();
    void setIsin(String isin);
    String getCode();
    void setCode(String code);
    
    void destroy();

}
