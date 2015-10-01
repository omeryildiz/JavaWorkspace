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

import java.io.Serializable;
import java.util.List;

import javax.persistence.EntityManager;

import org.jboss.seam.Component;
import org.jboss.seam.log.Log;
import org.jboss.seam.log.Logging;

import com.ut.tekir.entities.Address;
import com.ut.tekir.entities.Contact;
import com.ut.tekir.entities.ContactAddress;

/**
 * 
 * @author sinan.yumak
 *
 */
public class ContactAddressHelper implements Serializable {
	private static final long serialVersionUID = 1L;
	private Log log = Logging.getLog(ContactAddressHelper.class);
	
	private EntityManager entityManager;
	
    public ContactAddress getDefaultAddress(List<ContactAddress> addresses) {
    	for (ContactAddress item : addresses) {
    		if (item.getDefaultAddress()) return item;
    	}
    	return null;
    }

	public ContactAddress getDefaultAddress(Contact contact) {
    	return getDefaultAddress(contact.getId());
    }

    @SuppressWarnings("unchecked")
    public ContactAddress getDefaultAddress(Long contactId) {
    	try {
    		List<ContactAddress> addresses =  getEntityManager().createQuery("select ca from ContactAddress ca where " +
														    				 "ca.owner.id=:ownerId and " +
														    				 "ca.defaultAddress=true ")
														    				 .setParameter("ownerId", contactId)
														    				 .getResultList();
														    		
    		if (addresses != null && addresses.size() == 1) {
    			return addresses.get(0);
    		}
    	} catch (Exception e) {
    		log.error("Ön tanımlı adres sorgulanırken hata meydana geldi. #0", e);
    	}
    	return null;
    }

    @SuppressWarnings("unchecked")
    public void initDefaultAddress(ContactModel model) {
    	try {
    		List<Address> addresses =  getEntityManager().createQuery("select new com.ut.tekir.entities.Address(ca.address.street,ca.address.province," +
    																		 "ca.address.city,ca.address.country,ca.address.zip) from ContactAddress ca where " +
														    				 "ca.owner.id=:ownerId and " +
													    					 "ca.defaultAddress=true ")
																    		.setParameter("ownerId", model.getId())
																    		.getResultList();
    		
    		if (addresses != null && addresses.size() == 1) {
    			model.setDefaultAddress(addresses.get(0).description());
    		} else {
    			model.setDefaultAddress("");
    		}
    	} catch (Exception e) {
    		log.error("Ön tanımlı adres sorgulanırken hata meydana geldi. #0", e);
    	}
    }

	public void activateSelected(Contact contact, int index) {
		for (int i = 0; i < contact.getAddressList().size(); i++) {
			if (i != index) {
				contact.getAddressList().get(i).setDefaultAddress(Boolean.FALSE);
			} else {
				contact.getAddressList().get(i).setDefaultAddress(Boolean.TRUE);
			}
		}
	}
	
	public static ContactAddressHelper instance() {
		return new ContactAddressHelper();
	}

	public EntityManager getEntityManager() {
		if (entityManager == null || !entityManager.isOpen()) {
			entityManager = (EntityManager)Component.getInstance("entityManager");
		}
		return entityManager;
	}

}
