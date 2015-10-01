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

import java.util.List;

import com.ut.tekir.entities.Contact;
import com.ut.tekir.entities.ContactCategory;
import com.ut.tekir.entities.ContactType;

/**
 *
 * @author haky
 */
@SuppressWarnings("unchecked")
public interface ContactSuggestion {

	List<ContactCategory> suggestContactCategory(Object event);
    
	List<Contact> suggestContact(Object event);
    
	List<Contact> suggestProvider(Object event);
    
	List<Contact> suggestCustomer(Object event);

    void selectCustomerList();
    
    List<ContactCategory> getContactCategoryList();
    
    Contact findContact(Long conId);
    
	List getContactList();

	void setContactList(List contactList);
    
    String getFullname();
    void setFullname(String fullname);

    String getCode();
    void setCode(String code);

    ContactCategory getCategory();
    void setCategory(ContactCategory category);
    
    ContactType getType();
    void setType(ContactType type);
    
    String getSsn();
    void setSsn(String ssn);

    String getTaxNumber();
    void setTaxNumber(String taxNumber);

    String getCompany();
    void setCompany(String company);

    String getExCode2();
    void setExCode2(String exCode2);

    void destroy();

    List<Contact> suggestPersonnel(Object event);
    
	String getExCode1();
	void setExCode1(String exCode1);
}
