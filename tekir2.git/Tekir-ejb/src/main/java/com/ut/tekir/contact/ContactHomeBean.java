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

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.Remove;
import javax.ejb.Stateful;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.core.Conversation;

import com.ut.tekir.configuration.SystemConfiguration;
import com.ut.tekir.documents.DocumentHome;
import com.ut.tekir.entities.AccountDepositType;
import com.ut.tekir.entities.AccountOwnerType;
import com.ut.tekir.entities.Bank;
import com.ut.tekir.entities.BankAccount;
import com.ut.tekir.entities.Contact;
import com.ut.tekir.entities.ContactAddress;
import com.ut.tekir.entities.ContactBankAccount;
import com.ut.tekir.entities.ContactNetwork;
import com.ut.tekir.entities.ContactPhone;
import com.ut.tekir.entities.GenderType;
import com.ut.tekir.entities.User;
import com.ut.tekir.framework.BaseConsts;
import com.ut.tekir.framework.EntityBase;
import com.ut.tekir.framework.OptionManager;
import com.ut.tekir.framework.SequenceManager;
import com.ut.tekir.framework.SequenceType;
import com.ut.tekir.options.OrganizationSchemeOptionKey;

//TODO: ağ bilgilerini varsayılan telefona göre sıralamalı.

/**
 *
 * @author haky
 */
@Stateful
@Name("contactHome")
@Scope(value=ScopeType.CONVERSATION)
public class ContactHomeBean extends EntityBase<Contact> implements ContactHome<Contact>{
    @In(create = true)
    SequenceManager sequenceManager;
    @In(required=false) @Out(required=false)
    ContactStatus contactStatus;
    @In
    DocumentHome documentHome;
    @In
    OptionManager optionManager;
    @In
    User activeUser;

    private ContactBankAccount selectedBankAccount = new ContactBankAccount();
    private ContactAddress selectedAddress = new ContactAddress();
    private ContactPhone selectedPhone = new ContactPhone();
    private ContactNetwork selectedNetwork = new ContactNetwork();
    private Bank bank; 
    private int selectedIndex;
    private Boolean isEdit = Boolean.TRUE;

    private ContactAddressHelper addressHelper;
    private ContactPhoneHelper phoneHelper;
    private ContactNetworkHelper networkHelper;
    
	@Create @Begin(join=true,flushMode = org.jboss.seam.annotations.FlushModeType.MANUAL)
    public void init() {
        log.debug("ContactHome Init");
        
        initHelpers();
    }
    
    private void initHelpers() {
		addressHelper = ContactAddressHelper.instance();
		phoneHelper = ContactPhoneHelper.instance();
		networkHelper = ContactNetworkHelper.instance();
	}

	@Out(required=false) 
    public Contact getContact() {
        return getEntity();
    }

    @In(required=false)
    public void setContact(Contact contact) {
        setEntity( contact );
    }
    
    @Override
    public void createNew() {
        log.debug("Yeni Contact");
        entity = new Contact();
        entity.setCode(sequenceManager.getNewSerialNumber(SequenceType.SERIAL_CARDS_CONTACT));
        entity.setActive(true);
        entity.setGender(GenderType.Unknown);
        
    }

    @SuppressWarnings("unchecked")
	public List getContactStatus(){
        log.debug("Cari için durum listesi alınacak : #0", entity );
        if ( entity == null ) return null;
        
        if( contactStatus == null ){
            contactStatus = new ContactStatusBean();
        }
        log.debug("Cari için durum listesi alınacak : #0", entity );
        return contactStatus.getResultList( entity.getId());
    }

    @SuppressWarnings("unchecked")
	@Override
	public String save(){
    	Boolean hata = false;

    	try {
    		
			if(entity.getPerson() == false && entity.getCompany().isEmpty()){
				facesMessages.add("Cari Tipi Şirket İse Şirket Alanına Değer Girilmelidir !");
				hata = true;
			}
			if(entity.getPerson() == false && entity.getTaxOffice().isEmpty()){
				facesMessages.add("Cari Tipi Şirket İse Vergi Dairesi Alanına Değer Girilmelidir !");
				hata = true;
			}
			if(entity.getPerson() == false && entity.getTaxNumber().isEmpty()){
				facesMessages.add("Cari Tipi Şirket İse Vergi No Alanına Değer Girilmelidir !");
				hata = true;
			}
			//FIXME: Zorunlu giris kontrolu opsiyonlardan ayaralanacak hale getirilecek
			/* 
			  if(entity.getPerson() == true && entity.getSsn().isEmpty()){
			 
				facesMessages.add("Cari Tipi Kişi İse TC Kimlik No Alanına Değer Girilmelidir !");
				hata = true;
				}
			*/

			if(entity.getPerson() == true && !(entity.getTaxNumber().isEmpty()) ){

				if(controlSsn()){
					facesMessages.add("TC Kimlik Numarası Aynı Olan Kayıt Var !");
					hata = true;
				}
			}
			if(entity.getPerson() == false){
				
				if(controlTaxNumber()){
					facesMessages.add("Vergi Numarası Aynı Olan Kayıt Var !");
				}
			}
			
			if (entity.getName() != null && entity.getMidname() != null && entity.getSurname() != null) {
                entity.setFullname(entity.getName().trim() + ' ' + entity.getMidname().trim() + ' ' + entity.getSurname().trim());
                
                StringBuilder stBuild = new StringBuilder();
                String[] fName = entity.getFullname().split(" ");
                for (int i = 0; i < fName.length; i++) {
					if(!fName[i].equals("")){
						stBuild.append(fName[i]).append(" ");
					}
				}        
                
                entity.setFullname(stBuild.toString().trim());
             }
			
			
//            if (entity.getName() != null && entity.getMidname() != null && entity.getMidname().trim() != "" && entity.getSurname() != null) {
//                entity.setFullname(entity.getName().trim() + ' ' + entity.getMidname().trim() + ' ' + entity.getSurname().trim());
//            }
//				
//
//            if(entity.getName() != null && entity.getMidname() != null && entity.getMidname().trim() == "" && entity.getSurname() != null) {
//				entity.setFullname(entity.getName().trim() + ' ' + entity.getSurname().trim());
//            }
//            
//            if(entity.getName() != null && entity.getMidname() == null && entity.getMidname().trim() != "" && entity.getSurname() != null) {
//				entity.setFullname(entity.getName().trim() + ' ' + entity.getSurname().trim());
//            }
//
//            if(entity.getName() != null && (entity.getMidname() == null || entity.getMidname().trim() == "") && (entity.getSurname() == null || entity.getSurname().trim() == "") ) {
//				entity.setFullname(entity.getName().trim());
//            }
            
            

            if (entity.getId() == null) {
                List<Contact> conList = entityManager.createQuery("select c from Contact c where c.code = :code")
                                        .setParameter("code", entity.getCode())
                                        .getResultList();
                if(conList.size() > 0){
                    hata = true;
                    facesMessages.add("Aynı koda sahip başka bir cari var !");
                }
            }

			if (hata) {
                throw new RuntimeException("Hata!");
            }
			
		} catch (Exception e) {
			log.error("Hata :{0}", e);
            return BaseConsts.FAIL;
		}
		String res = super.save();
		return res;
	}

	@Override
    @Transactional
    public String saveAndNew() {
        return super.saveAndNew();
    }

    public void popupNewDocumentFile(){
    	documentHome.init(getEntity());
    }

    public void deleteDocumentFile(int row) {
        log.info("dosya silinecek: " +  row);
        Long docId = entity.getDocumentFiles().get(row).getId();
        entity.getDocumentFiles().remove(row);
        documentHome.deleteDocument(docId);
    }

    public void createNewBankAccountLine() {
		manualFlush();
		if (entity == null) {
			return;
		}
		selectedBankAccount = new ContactBankAccount();
		
		selectedBankAccount.setActive(true);

		selectedBankAccount.setBankAccount(new BankAccount());
		selectedBankAccount.getBankAccount().setCurrency(BaseConsts.SYSTEM_CURRENCY_CODE);
		selectedBankAccount.getBankAccount().setAccountOwnerType(AccountOwnerType.Customer);
		selectedBankAccount.getBankAccount().setAccountDepositType(AccountDepositType.DemandDeposit);
		selectedBankAccount.getBankAccount().setCreateDate(new Date());
		selectedBankAccount.getBankAccount().setCreateUser(activeUser.getUserName());
		selectedBankAccount.getBankAccount().setUpdateDate(new Date());
		selectedBankAccount.getBankAccount().setUpdateUser(activeUser.getUserName());		
		selectedBankAccount.setOwner(entity);
                
        isEdit = Boolean.FALSE;
    }

    public void createNewAddressLine() {
		manualFlush();
		if (entity == null) {
			return;
		}
    	
    	selectedAddress = new ContactAddress();
    	
    	selectedAddress.setCreateDate(new Date());
    	selectedAddress.setCreateUser(activeUser.getUserName());
    	selectedAddress.setUpdateDate(new Date());
    	selectedAddress.setUpdateUser(activeUser.getUserName());
    	selectedAddress.setOwner(entity);

    	isEdit = Boolean.FALSE;
    }

    public void createNewPhoneLine() {
    	manualFlush();
    	if (entity == null) {
    		return;
    	}
    	
    	selectedPhone = new ContactPhone();

    	selectedPhone.setCreateDate(new Date());
    	selectedPhone.setCreateUser(activeUser.getUserName());
    	selectedPhone.setUpdateDate(new Date());
    	selectedPhone.setUpdateUser(activeUser.getUserName());
    	selectedPhone.getPhone().setCountryCode("90");
    	
    	selectedPhone.setOwner(entity);
    	
    	isEdit = Boolean.FALSE;
    }
    
    public void createNewNetworkLine() {
    	manualFlush();
    	if (entity == null) {
    		return;
    	}
    	
    	selectedNetwork = new ContactNetwork();
    	
    	selectedNetwork.setCreateDate(new Date());
    	selectedNetwork.setCreateUser(activeUser.getUserName());
    	selectedNetwork.setUpdateDate(new Date());
    	selectedNetwork.setUpdateUser(activeUser.getUserName());
    	selectedNetwork.setOwner(entity);
    	
    	isEdit = Boolean.FALSE;
    }

    public void editBankAccountLine(int rowKey) {
		manualFlush();
		if (entity == null) {
			return;
		}
    	selectedIndex = rowKey;
		
    	selectedBankAccount = new ContactBankAccount();

    	moveBankAccountFieldsFromModel(entity.getBankAccountList().get(rowKey), selectedBankAccount);
    	
    	isEdit = Boolean.TRUE;
    }

    private void moveBankAccountFieldsFromModel(ContactBankAccount fromBankAccount, ContactBankAccount toBankAccount) {
    	BankAccount fromAccount = fromBankAccount.getBankAccount();
    	BankAccount toAccount = toBankAccount.getBankAccount();
    	
    	toAccount.setAccountDepositType(fromAccount.getAccountDepositType());
    	toAccount.setAccountNo(fromAccount.getAccountNo());
    	toAccount.setActive(fromAccount.getActive());
    	toAccount.setBankBranch(fromAccount.getBankBranch());
    	toAccount.setCloseDate(fromAccount.getCloseDate());
    	toAccount.setCreateDate(fromAccount.getCreateDate());
    	toAccount.setCreateUser(fromAccount.getCreateUser());
    	toAccount.setCurrency(fromAccount.getCurrency());
    	toAccount.setExcode1(fromAccount.getExcode1());
    	toAccount.setExcode2(fromAccount.getExcode2());
    	toAccount.setIban(fromAccount.getIban());
    	toAccount.setName(fromAccount.getName());
    	toAccount.setOpenDate(fromAccount.getOpenDate());
    	toAccount.setRate(fromAccount.getRate());
    	toAccount.setTerm(fromAccount.getTerm());
    	toAccount.setUpdateDate(fromAccount.getUpdateDate());
    	toAccount.setUpdateUser(fromAccount.getUpdateUser());

    	toBankAccount.setOwner(fromBankAccount.getOwner());
    }
    
    public void editAddressLine(int rowKey) {
    	manualFlush();
    	if (entity == null) {
    		return;
    	}
    	selectedIndex = rowKey;
    	
    	selectedAddress = new ContactAddress();
    	
    	moveAddressFieldsFromModel(entity.getAddressList().get(rowKey), selectedAddress);
    	
    	isEdit = Boolean.TRUE;
    }

    private void moveAddressFieldsFromModel(ContactAddress fromAddress, ContactAddress toAddress) {
    	
    	toAddress.setActiveAddress(fromAddress.getActiveAddress());
    	toAddress.setAddress(fromAddress.getAddress());
    	toAddress.setCity(fromAddress.getCity());
    	toAddress.setDefaultAddress(fromAddress.getDefaultAddress());
    	toAddress.setDeliveryAddress(fromAddress.getDeliveryAddress());
    	toAddress.setHomeAddress(fromAddress.getHomeAddress());
    	toAddress.setInfo(fromAddress.getInfo());
    	toAddress.setInvoiceAddress(fromAddress.getInvoiceAddress());
    	toAddress.setOtherAddress(fromAddress.getOtherAddress());
    	toAddress.setOwner(fromAddress.getOwner());
    	toAddress.setProvince(fromAddress.getProvince());
    	toAddress.setRelated(fromAddress.getRelated());
    	toAddress.setShippingAddress(fromAddress.getShippingAddress());
    	toAddress.setUsed(fromAddress.getUsed());
    	toAddress.setWorkAddress(fromAddress.getWorkAddress());
    	
    }
    
    public void editPhoneLine(int rowKey) {
    	manualFlush();
    	if (entity == null) {
    		return;
    	}
    	selectedIndex = rowKey;
    	
    	selectedPhone = new ContactPhone();
    	
    	movePhoneFieldsFromModel(entity.getPhoneList().get(rowKey), selectedPhone);
    	
    	isEdit = Boolean.TRUE;
    }

    private void movePhoneFieldsFromModel(ContactPhone fromPhone, ContactPhone toPhone) {

    	toPhone.setActivePhone(fromPhone.getActivePhone());
    	toPhone.setCallerPhone(fromPhone.getCallerPhone());
    	toPhone.setDefaultPhone(fromPhone.getDefaultPhone());
    	toPhone.setFaxPhone(fromPhone.getFaxPhone());
    	toPhone.setGsmPhone(fromPhone.getGsmPhone());
    	toPhone.setHomePhone(fromPhone.getHomePhone());
    	toPhone.setImmobilePhone(fromPhone.getImmobilePhone());
    	toPhone.setInfo(fromPhone.getInfo());
    	toPhone.setOtherPhone(fromPhone.getOtherPhone());
    	toPhone.setOwner(fromPhone.getOwner());
    	toPhone.setPhone(fromPhone.getPhone());
    	toPhone.setRelated(fromPhone.getRelated());
    	toPhone.setVehiclePhone(fromPhone.getVehiclePhone());
    	toPhone.setWorkPhone(fromPhone.getWorkPhone());
    	toPhone.setPhoneType(fromPhone.getPhoneType());
    	toPhone.setUsageType(fromPhone.getUsageType());
    	
    }
    
    public void editNetworkLine(int rowKey) {
    	manualFlush();
    	if (entity == null) {
    		return;
    	}
    	selectedIndex = rowKey;
    	
    	selectedNetwork = new ContactNetwork();
    	
    	moveNetworkFieldsFromModel(entity.getNetworkList().get(rowKey), selectedNetwork);
    	
    	isEdit = Boolean.TRUE;
    }
    
    private void moveNetworkFieldsFromModel(ContactNetwork fromNetwork, ContactNetwork toNetwork) {
    	
    	toNetwork.setActiveNetwork(fromNetwork.getActiveNetwork());
    	toNetwork.setDefaultNetwork(fromNetwork.getDefaultNetwork());
    	toNetwork.setEmail(fromNetwork.getEmail());
    	toNetwork.setFacebookNetwork(fromNetwork.getFacebookNetwork());
    	toNetwork.setInfo(fromNetwork.getInfo());
    	toNetwork.setMircNetwork(fromNetwork.getMircNetwork());
    	toNetwork.setOwner(fromNetwork.getOwner());
    	toNetwork.setSkypeNetwork(fromNetwork.getSkypeNetwork());
    	toNetwork.setTwitterNetwork(fromNetwork.getTwitterNetwork());
    	toNetwork.setValue(fromNetwork.getValue());
    	toNetwork.setWeb(fromNetwork.getWeb());
    	
    }
    
    public void endNetworkEdit() {
    	boolean error = makeNetworkValidations();
    	
    	if (!error) {
    		ContactNetwork realNetwork = entity.getNetworkList().get(selectedIndex);
    		
    		moveNetworkFieldsFromModel(selectedNetwork, realNetwork);

    		realNetwork.setUpdateDate(new Date());
    		realNetwork.setUpdateUser(activeUser.getUserName());
    	}
    }
    
    public void calculateWorkEndDate() {
    	if (selectedBankAccount != null && selectedBankAccount.getBankAccount() != null) {
    		
	    	int term = selectedBankAccount.getBankAccount().getTerm();
	    	
	    	Calendar cal = Calendar.getInstance();
	
	    	cal.setTime(selectedBankAccount.getBankAccount().getOpenDate());
	    	
	    	cal.add(Calendar.DAY_OF_YEAR, term);
	    	
	    	selectedBankAccount.getBankAccount().setCloseDate(cal.getTime());
    	}

    }
    
    public void addToBankAccountList() {
    	boolean error = makeBankAccountValidations();
    	if (!error) {
    		entity.getBankAccountList().add(selectedBankAccount);
    	}
    }

    public Boolean makeBankAccountValidations() {
    	boolean error = false;
    	
    	if (selectedBankAccount.getBankAccount().getBankBranch() == null) {
    		error = true;
    	} 
    	
    	if (selectedBankAccount.getBankAccount().getAccountNo() == null || 
    			selectedBankAccount.getBankAccount().getAccountNo().length() == 0) {
    		error = true;
    	}
    	return error;
    }
    
    public void addToAddressList() {
    	
    	boolean error = makeAddressValidations();
    	
    	log.info("Hata durumu #0", error);
    	
    	if (!error) {
    		if (entity.getAddressList().size() == 0) {
    			selectedAddress.setDefaultAddress(Boolean.TRUE);
    		}
    		entity.getAddressList().add(selectedAddress);
    		log.info("Adres listesine eklendi");
    	}
    }

    public Boolean makeAddressValidations() {
    	boolean error = false;

    	if (selectedAddress.getCity() == null) {
    		facesMessages.add("#{messages['contactAddress.label.CityNotSelected']}");
    		error = true;
    	}

    	
    	//FIXME: ilce secimi System Ayarlarinda kullanici tarafaindan zorunlu olup olmadigi secildikten sonra acilabilir
    	/*
    	if (selectedAddress.getAddress().getCountry().equals("TÜRKİYE") && selectedAddress.getProvince() == null) {
    		facesMessages.add("#{messages['contactAddress.label.ProvinceNotSelected']}");
    		error = true;
    	}
    	 */
    	if (selectedAddress.getDeliveryAddress().equals(Boolean.FALSE) 
    	 && selectedAddress.getInvoiceAddress().equals(Boolean.FALSE)
    	 && selectedAddress.getShippingAddress().equals(Boolean.FALSE)) {
    	
    		facesMessages.add("#{messages['contactAddress.label.UsageTypeHasNotSelected']}");
    		error = true;
    	}

    	if (selectedAddress.getUsageType() == null || selectedAddress.getUsageType().length() == 0) {
    		facesMessages.add("#{messages['contactAddress.label.AddressTypeHasNotSelected']}");
    		error = true;
    	}

    	return error;
    }
    
    public void endAddressEdit() {
    	boolean error = makeAddressValidations();
    	
    	if (!error) {
    		ContactAddress realAddress = entity.getAddressList().get(selectedIndex);
    		
    		moveAddressFieldsFromModel(selectedAddress, realAddress);
    		
    		realAddress.setUpdateDate(new Date());
    		realAddress.setUpdateUser(activeUser.getUserName());
    	}
    
    }

    public void endPhoneEdit() {
    	boolean error = makePhoneValidations();
    	
    	if (!error) {
    		ContactPhone realPhone = entity.getPhoneList().get(selectedIndex);
    		
    		movePhoneFieldsFromModel(selectedPhone, realPhone);
    		
    		realPhone.setUpdateDate(new Date());
    		realPhone.setUpdateUser(activeUser.getUserName());
    	}    	
    }

    public void endBankAccountEdit() {
    	boolean error = makeBankAccountValidations();
    	
    	if (!error) {
    		ContactBankAccount realAccount = entity.getBankAccountList().get(selectedIndex);
    		
    		moveBankAccountFieldsFromModel(selectedBankAccount, realAccount);

    		realAccount.setUpdateDate(new Date());
    		realAccount.setUpdateUser(activeUser.getUserName());
    	}
    	
    }
    
    public void addToPhoneList() {
    	boolean error = makePhoneValidations();
    	
    	if (!error) {
    		if (entity.getPhoneList().size() == 0) {
    			selectedPhone.setDefaultPhone(Boolean.TRUE);
    		}
    		entity.getPhoneList().add(selectedPhone);
    	}
    }

    public void addToNetworkList() {
    	boolean error = makeNetworkValidations();

    	if (!error) {
    		if (entity.getNetworkList().size() == 0) {
    			selectedNetwork.setDefaultNetwork(Boolean.TRUE);
    		}
    		entity.getNetworkList().add(selectedNetwork);
    	}
    }
    
    public Boolean makeNetworkValidations() {
    	boolean error = false;
    	if (selectedNetwork.getNetwork() == null || selectedNetwork.getNetwork().length() == 0 ) {
    		facesMessages.add("#{messages['contactNetwork.label.UsageTypeHasNotSelected']}");
    		error = true;
    	}
    	return error;
    }
    
    public Boolean makePhoneValidations() {
    	boolean error = false;

    	if (selectedPhone.getGsmPhone().equals(Boolean.FALSE) 
    	    	 && selectedPhone.getVehiclePhone().equals(Boolean.FALSE)
    	    	 && selectedPhone.getFaxPhone().equals(Boolean.FALSE)
    	    	 && selectedPhone.getImmobilePhone().equals(Boolean.FALSE)
    	    	 && selectedPhone.getCallerPhone().equals(Boolean.FALSE)) {
    	    	
    	    		facesMessages.add("#{messages['contactPhone.label.UsageTypeHasNotSelected']}");
    	    		error = true;
    	    	}

    	if (selectedPhone.getHomePhone().equals(Boolean.FALSE) 
    			&& selectedPhone.getWorkPhone().equals(Boolean.FALSE)
    			&& selectedPhone.getRelated().equals(Boolean.FALSE)
    			&& selectedPhone.getOtherPhone().equals(Boolean.FALSE)) {
    		
    		facesMessages.add("#{messages['contactPhone.label.PhoneTypeHasNotSelected']}");
    		error = true;
    	}

    	if (selectedPhone.getPhone().getFullNumber() != null && 
    			selectedPhone.getPhone().getFullNumber().length() < 15) {

    		facesMessages.add("#{messages['contactPhone.label.BadPhoneNumber']}");
    		error = true;
    	}

    	return error;
    }
    
    public void deleteBankAccountLine(int rowKey) {
		manualFlush();
		if (entity == null) {
			return;
		}

		entity.getBankAccountList().remove(rowKey);
    }

	public void deleteAddressLine(int rowKey) {
		manualFlush();
		if (entity == null) {
			return;
		}

		entity.getAddressList().remove(rowKey);
    }

	public void deletePhoneLine(int rowKey) {
		manualFlush();
		if (entity == null) {
			return;
		}
		
		entity.getPhoneList().remove(rowKey);
	}

	public void deleteNetworkLine(int rowKey) {
		manualFlush();
		if (entity == null) {
			return;
		}
		
		entity.getNetworkList().remove(rowKey);
	}

	public void activateSelected(int rowKey) {
		addressHelper.activateSelected(entity, rowKey);
	}

	public void activateSelectedPhone(int rowKey) {
		phoneHelper.activateSelected(entity, rowKey);
	}

	public void activateSelectedEmail(int rowKey) {
		networkHelper.activateSelected(entity, rowKey);
	}

    @SuppressWarnings("unchecked")
	public Boolean controlSsn(){
		List<Contact> conList;
		if(entity.getId() != null){
			
			conList = entityManager.createQuery("select c from Contact c where c.ssn = :ssn and c.id != :id")
									.setParameter("ssn", entity.getSsn())
									.setParameter("id", entity.getId())
									.getResultList();
			
		}
		else
			conList = entityManager.createQuery("select c from Contact c where c.ssn = :ssn")
									.setParameter("ssn", entity.getSsn())
									.getResultList();
		
		if(conList.size() > 0){
			return true;
		}
		else return false;
	}
	
	@SuppressWarnings("unchecked")
	public Boolean controlTaxNumber(){
		List<Contact> conList;
		if(entity.getId() != null){
			
			conList = entityManager.createQuery("select c from Contact c where c.taxNumber = :taxNumber and c.id != :id")
									.setParameter("taxNumber", entity.getTaxNumber())
									.setParameter("id", entity.getId())
									.getResultList();
			
		}
		else
			conList = entityManager.createQuery("select c from Contact c where c.taxNumber = :taxNumber")
									.setParameter("taxNumber", entity.getTaxNumber())
									.getResultList();
		if(conList.size() > 0){
			return true;
		}
		else return false;
	}

	public String getSystemCurrency() {
		return SystemConfiguration.CURRENCY_CODE;
	}
	
	public ContactAddress getSelectedAddress() {
		return selectedAddress;
	}

	public void setSelectedAddress(ContactAddress selectedAddress) {
		this.selectedAddress = selectedAddress;
	}

	public ContactPhone getSelectedPhone() {
		return selectedPhone;
	}

	public void setSelectedPhone(ContactPhone selectedPhone) {
		this.selectedPhone = selectedPhone;
	}

	public ContactNetwork getSelectedNetwork() {
		return selectedNetwork;
	}

	public void setSelectedNetwork(ContactNetwork selectedNetwork) {
		this.selectedNetwork = selectedNetwork;
	}

	public Bank getBank() {
		return bank;
	}

	public void setBank(Bank bank) {
		this.bank = bank;
	}

	public Boolean getIsEdit() {
		return isEdit;
	}

	public void setIsEdit(Boolean isEdit) {
		this.isEdit = isEdit;
	}

    public void manualFlush() {
        Conversation.instance().changeFlushMode(org.jboss.seam.annotations.FlushModeType.MANUAL);
    }
    
	@Remove
    @Destroy
    public void destroy() {
    }

	public ContactBankAccount getSelectedBankAccount() {
		return selectedBankAccount;
	}

	public void setSelectedBankAccount(ContactBankAccount selectedBankAccount) {
		this.selectedBankAccount = selectedBankAccount;
	}

    public boolean getOrganizationSchemeOption(){
		return optionManager.getOption(OrganizationSchemeOptionKey.USE_SCHEME, true).getAsBoolean();
    }

}
