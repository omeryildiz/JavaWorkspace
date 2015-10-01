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

import com.ut.tekir.entities.Contact;
import com.ut.tekir.entities.ContactPhone;
import com.ut.tekir.entities.Phone;

/**
 * 
 * @author sinan.yumak
 *
 */
public class ContactPhoneHelper implements Serializable {
	private static final long serialVersionUID = 1L;
	private Log log = Logging.getLog(ContactPhoneHelper.class);
	
	private EntityManager entityManager;
	
    public ContactPhone getDefaultPhone(List<ContactPhone> phones, String usageType) {
    	for (ContactPhone item : phones) {
    		if (usageType.equals(item.getUsageType()) && item.getDefaultPhone()) return item;
    	}
    	return null;
    }

	public ContactPhone getDefaultPhone(Contact contact, String usageType) {
    	return getDefaultPhone(contact.getId(), usageType);
    }
    
    @SuppressWarnings("unchecked")
    public ContactPhone getDefaultPhone(Long contactId, String usageType) {
    	try {
    		List<ContactPhone> phones =  getEntityManager().createQuery("select cp from ContactPhone cp where " +
													    				"cp.owner.id=:ownerId and " +
													    				"cp."+ usageType + "=true and " +
													    				"cp.defaultPhone=true ")
													    				.setParameter("ownerId", contactId)
													    				.getResultList();
    		
    		if (phones != null && phones.size() == 1) {
    			return phones.get(0);
    		}
    	} catch (Exception e) {
    		log.error("Ön tanımlı telefon sorgulanırken hata meydana geldi. #0", e);
    	}
    	return null;
    }

    @SuppressWarnings("unchecked")
    public String initDefaultPhones(ContactModel model) {
    	try {
    		List<Object[]> phones =  getEntityManager().createQuery("select cp.phone.countryCode,cp.phone.areaCode,cp.phone.number,cp.phone.extention," +
    																	"gsmPhone,faxPhone,immobilePhone from ContactPhone cp where " +
													    				"cp.owner.id=:ownerId and " +
													    				"cp.defaultPhone=true ")
													    				.setParameter("ownerId", model.getId())
													    				.getResultList();
    		
    		for (Object[] item : phones) { 
    			Phone phone = new Phone((String)item[0], (String)item[1], (String)item[2], (String)item[3]);
    			if ((Boolean)item[4]) model.setDefaultGsm(phone.getFullNumberWithAreaCode());
    			if ((Boolean)item[5]) model.setDefaultFax(phone.getFullNumberWithAreaCode());
    			if ((Boolean)item[6]) model.setDefaultImmobile(phone.getFullNumberWithAreaCode());
    		}
    	} catch (Exception e) {
    		log.error("Ön tanımlı telefon sorgulanırken hata meydana geldi. #0", e);
    	}
    	return "";
    }

    private String getUsageType(Contact contact, int index) {
    	return contact.getPhoneList().get(index).getUsageType();
    }
    
	public void activateSelected(Contact contact, int index) {
		String usageType = getUsageType(contact, index);
		ContactPhone phone;
		for (int i = 0; i < contact.getPhoneList().size(); i++) {
			phone = contact.getPhoneList().get(i);
			if (usageType.equals(phone.getUsageType())) {
				if (i != index) {
					phone.setDefaultPhone(Boolean.FALSE);
				} else {
					phone.setDefaultPhone(Boolean.TRUE);
				}
			}
		}
	}
	
	public static ContactPhoneHelper instance() {
		return new ContactPhoneHelper();
	}

	public EntityManager getEntityManager() {
		if (entityManager == null || !entityManager.isOpen()) {
			entityManager = (EntityManager)Component.getInstance("entityManager");
		}
		return entityManager;
	}

}
