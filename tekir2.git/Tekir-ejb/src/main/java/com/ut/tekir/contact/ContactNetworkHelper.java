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
import com.ut.tekir.entities.ContactNetwork;

/**
 * 
 * @author sinan.yumak
 *
 */
public class ContactNetworkHelper implements Serializable {
	private static final long serialVersionUID = 1L;
	private Log log = Logging.getLog(ContactNetworkHelper.class);
	
	private EntityManager entityManager;
	
    public ContactNetwork getDefaultNetwork(List<ContactNetwork> networks, String usageType) {
    	for (ContactNetwork item : networks) {
    		if (usageType.equals(item.getNetwork()) && item.getDefaultNetwork()) return item;
    	}
    	return null;
    }

    @SuppressWarnings("unchecked")
	public ContactNetwork getDefaultNetwork(Long contactId, String usageType) {
    	try {
    		List<ContactNetwork> networks =  getEntityManager().createQuery("select cp from ContactNetwork cp where " +
								    									     "cp.owner.id=:ownerId and " +
								    									     "cp."+ usageType + "=true and " +
								    									     "cp.defaultNetwork=true ")
								    									     .setParameter("ownerId", contactId)
								    									     .getResultList();
    		
    		if (networks != null && networks.size() == 1) {
    			return networks.get(0);
    		}
		} catch (Exception e) {
			log.error("Ön tanımlı ağ bilgisi sorgulanırken hata meydana geldi. #0", e);
		}
    	return null;
    }

    @SuppressWarnings("unchecked")
    public void initDefaultNetworks(ContactModel model) {
    	try {
    		List<Object[]> networks =  getEntityManager().createQuery("select cp.value,cp.email,cp.web from ContactNetwork cp where " +
												    				"cp.owner.id=:ownerId and " +
														    		"cp.defaultNetwork=true ")
														    		.setParameter("ownerId", model.getId())
														    		.getResultList();
    		
    		for (Object[] network : networks) {
    			if ((Boolean)network[1]) {
    				model.setDefaultEmail((String)network[0]);continue;
    			}
    			if ((Boolean)network[2]) {
    				model.setDefaultWeb((String)network[0]);continue;
    			}
    		}
    	} catch (Exception e) {
    		log.error("Ön tanımlı ağ bilgisi sorgulanırken hata meydana geldi. #0", e);
    	}
    }

    public ContactNetwork getDefaultNetwork(Contact contact, String usageType) {
    	return getDefaultNetwork(contact.getId(), usageType);
    }
    
    private String getUsageType(Contact contact, int index) {
    	return contact.getNetworkList().get(index).getNetwork();
    }
    
	public void activateSelected(Contact contact, int index) {
		String usageType = getUsageType(contact, index);
		ContactNetwork network;
		for (int i = 0; i < contact.getNetworkList().size(); i++) {
			network = contact.getNetworkList().get(i);
			if (usageType.equals(network.getNetwork())) {
				if (i != index) {
					network.setDefaultNetwork(Boolean.FALSE);
				} else {
					network.setDefaultNetwork(Boolean.TRUE);
				}
			}
		}
	}
	
	public static ContactNetworkHelper instance() {
		return new ContactNetworkHelper();
	}

	public EntityManager getEntityManager() {
		if (entityManager == null || !entityManager.isOpen()) {
			entityManager = (EntityManager)Component.getInstance("entityManager");
		}
		return entityManager;
	}

}
