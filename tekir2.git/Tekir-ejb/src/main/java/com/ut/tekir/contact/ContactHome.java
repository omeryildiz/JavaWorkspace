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

import javax.ejb.Local;

import com.ut.tekir.entities.Bank;
import com.ut.tekir.entities.Contact;
import com.ut.tekir.entities.ContactAddress;
import com.ut.tekir.entities.ContactBankAccount;
import com.ut.tekir.entities.ContactNetwork;
import com.ut.tekir.entities.ContactPhone;
import com.ut.tekir.framework.IEntityBase;

/**
 *
 * @author haky
 */
@Local
public interface ContactHome<E> extends IEntityBase<E> {

    void init();

    Contact getContact();
    
    void setContact(Contact contact);

    @SuppressWarnings("unchecked")
	List getContactStatus();
    
    void popupNewDocumentFile();
    
    void deleteDocumentFile(int row);
        
    public Boolean controlSsn();
	
    public Boolean controlTaxNumber();

    void createNewBankAccountLine();
    
    void deleteBankAccountLine(int rowKey);
    
	ContactBankAccount getSelectedBankAccount();
	void setSelectedBankAccount(ContactBankAccount selectedBankAccount);
	
	void addToBankAccountList();
	
	void editBankAccountLine(int rowKey);

	public Boolean getIsEdit();
	
	public void setIsEdit(Boolean isEdit);
	
	void calculateWorkEndDate();
	
	void deleteAddressLine(int rowKey);
	
	void createNewAddressLine();
	
	void activateSelected(int rowKey);

	public ContactAddress getSelectedAddress();
	
	public void setSelectedAddress(ContactAddress selectedAddress);

	void addToAddressList();

	void editAddressLine(int rowKey);

	void deletePhoneLine(int rowKey);
	
	void createNewPhoneLine();

	void editPhoneLine(int rowKey);

	ContactPhone getSelectedPhone();
	
	void setSelectedPhone(ContactPhone selectedPhone);

	void addToPhoneList();
	
	void activateSelectedPhone(int rowKey);
	
	Boolean makePhoneValidations();
	
	Boolean makeAddressValidations();
	
	Boolean makeBankAccountValidations();
	
	public ContactNetwork getSelectedNetwork();
	
	public void setSelectedNetwork(ContactNetwork selectedNetwork);

	void createNewNetworkLine();
	
	void editNetworkLine(int rowKey);
	
	void deleteNetworkLine(int rowKey);
	
	void addToNetworkList();
	
	Boolean makeNetworkValidations();

	void endAddressEdit();
	
	void endPhoneEdit();
	
	void endBankAccountEdit();

	void activateSelectedEmail(int rowKey);
	
	void endNetworkEdit();

	public Bank getBank();
	public void setBank(Bank bank);

    public boolean getOrganizationSchemeOption();
    
    String getSystemCurrency();
}
