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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jboss.seam.international.Messages;

import com.google.common.base.Joiner;


/**
 * @author sinan.yumak
 *
 */
public class ContactCaption {
    private static Map<String,String> messages = Messages.instance();
    
    public static String initCaption(ContactModel model) {
		StringBuilder sb = new StringBuilder();

		Joiner joiner = Joiner.on(" - ");
		joiner.appendTo(sb, typesAsList(model));
		
		model.setTypeCaption(sb.toString());

		return model.getTypeCaption();
	}
	
    private static List<String> typesAsList(ContactModel model) {
		List<String> types = new ArrayList<String>();
    	if (model.getCustomerType() != null && model.getCustomerType()) types.add(messages.get("contact.type.Customer"));
    	if (model.getProviderType() != null && model.getProviderType()) types.add(messages.get("contact.type.Provider"));
    	if (model.getAgentType() != null && model.getAgentType()) types.add(messages.get("contact.type.Agent"));
    	if (model.getPersonnelType() != null && model.getPersonnelType()) types.add(messages.get("contact.type.Personnel"));
    	if (model.getBranchType() != null && model.getBranchType()) types.add(messages.get("contact.type.Branch"));
    	if (model.getContactType() != null && model.getContactType()) types.add(messages.get("contact.type.Contact"));
    	if (model.getBankType() != null && model.getBankType()) types.add(messages.get("contact.type.Bank"));
    	if (model.getFoundationType() != null && model.getFoundationType()) types.add(messages.get("contact.type.Foundation"));
    	if (model.getRelatedType() != null && model.getRelatedType()) types.add(messages.get("contact.type.Related"));
    	return types;
    }
    
}
