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

import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.faces.application.FacesMessage;
import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.core.Conversation;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.log.Log;

import com.ut.tekir.entities.Contact;
import com.ut.tekir.entities.ContactAddress;
import com.ut.tekir.entities.ContactPhone;
import com.ut.tekir.entities.TradeAction;
import com.ut.tekir.framework.SequenceManager;
import com.ut.tekir.framework.SequenceType;

/**
 *
 * @author sozmen
 */
@Stateful
@Name("quickContactAddHome")
@Scope(value = ScopeType.CONVERSATION)
@AutoCreate
public class QuickContactAddHomeBean implements QuickContactAddHome {
	
	private Contact contact;
	private ContactAddress contactAddress=new ContactAddress();
	private ContactPhone contactPhone=new ContactPhone();
	
    @In(create = true)
    SequenceManager sequenceManager;
    @In
    EntityManager entityManager;
    @In
    FacesMessages facesMessages;
    @Logger
    Log log;
    
    @Create 
    @Begin(join=true,flushMode = org.jboss.seam.annotations.FlushModeType.MANUAL)
    public void init() {
        contact = new Contact();
        contact.setPerson(true);
        //contact.setCustomerType(Boolean.TRUE);
        contact.setCode(sequenceManager.getNewSerialNumber(SequenceType.SERIAL_CARDS_CONTACT));
        contact.setActive(true);
    }

    @Out(required = false)
	public Contact getContact() {
		return contact;
	}

    @In(required = false)
	public void setContact(Contact contact) {
		this.contact = contact;
	}
    	
	public void closeContact(){
		contact = null;
	}
	
	public void createNew(TradeAction tradeAction) {
		init();
		if (tradeAction == TradeAction.Sale ) {
			contact.setCustomerType(Boolean.TRUE);
		} else if (tradeAction == TradeAction.Purchase) {
			contact.setProviderType(Boolean.TRUE);
		} else {
			contact.setContactType(Boolean.TRUE); 
		}
			
		contactAddress = new ContactAddress();
		contactPhone = new ContactPhone();
	}
	
	@SuppressWarnings("unchecked")
	@Transactional
	public void saveContact() {
		log.info("Saving quick contact...");
		manualFlush();

		List<Contact> contactList = entityManager.createQuery("select c from Contact c where c.ssn = :ssn")
													.setParameter("ssn", contact.getSsn())
													.getResultList();

		if(contactList.size() > 0 && !(contact.getSsn().isEmpty()) ){
            throw new RuntimeException("TC Kimlik numarası aynı olan kayıt var ! Kayıt gerçekleşmedi !");
		} else {
			contactAddress.setDefaultAddress(Boolean.TRUE);
			contactAddress.setActiveAddress(Boolean.TRUE);
			contactAddress.setInvoiceAddress(Boolean.TRUE);

			if (contactAddress.getCity() != null) {
				contactAddress.getAddress().setCity(contactAddress.getCity().getName());
			}

			if(contactAddress.getProvince() != null) {
				contactAddress.getAddress().setProvince(contactAddress.getProvince().getName());
			}
			
			if (contact.getName() != null && contact.getMidname() != null && contact.getSurname() != null) {
				contact.setFullname(contact.getName().trim() + ' ' + contact.getMidname().trim() + ' ' + contact.getSurname().trim());
                
                StringBuilder stBuild = new StringBuilder();
                String[] fName = contact.getFullname().split(" ");
                for (int i = 0; i < fName.length; i++) {
					if(!fName[i].equals("")){
						stBuild.append(fName[i]).append(" ");
					}
				}        
                
                contact.setFullname(stBuild.toString().trim());
             }

			/*
            if(contact.getName() != null && contact.getMidname() != null && contact.getSurname() != null)
				contact.setFullname(contact.getName() + ' ' + contact.getMidname() + ' ' + contact.getSurname());

            if(contact.getName() != null && contact.getMidname() == null && contact.getSurname() != null)
				contact.setFullname(contact.getName() + ' ' + contact.getSurname());

            if(contact.getName() != null && contact.getMidname() == null && contact.getSurname() == null)
				contact.setFullname(contact.getName());
            
            */
            contactAddress.setOwner(contact);
            contact.getAddressList().add(contactAddress);

            contactPhone.setOwner(contact);
            contactPhone.setDefaultPhone(Boolean.TRUE);
			contactPhone.setActivePhone(Boolean.TRUE);
			contactPhone.setImmobilePhone(Boolean.TRUE);
			contactPhone.setOtherPhone(Boolean.TRUE);
            contact.getPhoneList().add(contactPhone);
  
            
			entityManager.persist(contact);
			entityManager.flush();
			
			log.info("Cari kaydı başarılı...");
		}
	}

	@Transactional
	public void saveContactFromPopup() {
		try {
			saveContact();
		} catch (Exception e) {
			log.error("Hızlı cari eklerken hata oluştu. Hata sebebi :{0}", e);
			facesMessages.add(Severity.ERROR, "Hızlı cari eklerken hata oluştu. Hata sebebi: {0}",e.getMessage());
		}

	}
	
	public boolean facesMessagesHasErrorMessages() {
		for (FacesMessage message : facesMessages.getCurrentMessages()) {
			if (!(message.getSeverity() == FacesMessage.SEVERITY_INFO)) {
				return true;
			}
		}
		return false;
	}
	
    public void manualFlush() {
        Conversation.instance().changeFlushMode(org.jboss.seam.annotations.FlushModeType.MANUAL);
    }

	public ContactAddress getContactAddress() {
		return contactAddress;
	}

	public void setContactAddress(ContactAddress contactAddress) {
		this.contactAddress = contactAddress;
	}

	public ContactPhone getContactPhone() {
		return contactPhone;
	}

	public void setContactPhone(ContactPhone contactPhone) {
		this.contactPhone = contactPhone;
	}
	
    @Remove
    @Destroy
    public void destroy() {
    }

}
