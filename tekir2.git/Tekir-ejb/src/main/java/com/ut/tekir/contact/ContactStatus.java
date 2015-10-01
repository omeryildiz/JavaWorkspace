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

package com.ut.tekir.contact;

import com.ut.tekir.entities.Contact;
import java.util.List;

/**
 *
 * @author haky
 */
public interface ContactStatus {
    
    void init();
    
    void calculateStatus();
    
    @SuppressWarnings("unchecked")
	List getResultList();
    @SuppressWarnings("unchecked")
	List getResultList(Long contactId);

    String beginStatusReport();
    
    List<StatusRow> getStatusRows();
    
    Integer getYear();
    void setYear(Integer year);

    String getCurrency();
    void setCurrency(String currency);
        
    Contact getContact();
    void setContact(Contact contact);
    
    Long getContactId();
    void setContactId(Long contactId);
    
    void destroy();
}

