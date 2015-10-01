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

import javax.ejb.Local;

import com.ut.tekir.entities.Contact;
import com.ut.tekir.entities.ContactAddress;
import com.ut.tekir.entities.ContactPhone;
import com.ut.tekir.entities.TradeAction;

/**
 *
 * @author sozmen
 */
@Local
public interface QuickContactAddHome {

    void init();
    void manualFlush();
    
	void closeContact();

	void saveContact();
	
	void createNew(TradeAction tradeAction);
	
	Contact getContact();
	void setContact(Contact contact);
	
	ContactAddress getContactAddress();
	void setContactAddress(ContactAddress contactAddress);
	

	ContactPhone getContactPhone();
	void setContactPhone(ContactPhone contactPhone);

	boolean facesMessagesHasErrorMessages();
	
    void destroy();
    
    void saveContactFromPopup();
}
