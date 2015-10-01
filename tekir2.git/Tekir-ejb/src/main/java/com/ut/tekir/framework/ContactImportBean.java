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

package com.ut.tekir.framework;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.core.Conversation;
import org.jboss.seam.core.Interpolator;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.log.Log;
import org.richfaces.event.UploadEvent;
import org.richfaces.model.UploadItem;

//import au.com.bytecode.opencsv.CSVReader;

import com.ut.tekir.contact.ContactHome;
import com.ut.tekir.entities.City;
import com.ut.tekir.entities.Contact;
import com.ut.tekir.entities.Province;

/**
 * imports contacts from cvs file.
 * 
 * @author sinan.yumak
 * 
 */
@Stateful
@Name("contactImportBean")
@Scope(ScopeType.CONVERSATION)
public class ContactImportBean implements ContactImport {
    @Logger
    Log log;
    @In
    FacesMessages facesMessages;
    @In
    EntityManager entityManager;
    @In(required=false,create=true)
    ContactHome<Contact> contactHome;
    
    private List<String> messageList = new ArrayList<String>();
    private List<Contact> contactList = new ArrayList<Contact>();
    
    @Create 
    @Begin(join=true,flushMode = org.jboss.seam.annotations.FlushModeType.MANUAL)
    public void init() {
    }
    
	@SuppressWarnings({ "unchecked", "deprecation" })
	private void importContacts(File uploadedFile) {
		/*
		
		FIXME: CSVReader bağımlılığı düzeltildikten sonra açılacak.
		
		boolean isfirstLine = true;
		try {

		
			CSVReader reader = new CSVReader(new FileReader(uploadedFile));
			List<String[]> myEntries = reader.readAll();

			Contact contact = null;
			int rowIndex = 1;

			for(int i=1;i<myEntries.size();i++) {
				int index = 0;
				
				contactHome.createNew();
				contact = contactHome.getContact();
				
				
				for (String cellValue : myEntries.get(i)) {
					
					assignCellValuesToContact(contact, cellValue, index, rowIndex);

					index++;
					
				}
				
				contactList.add(contact);
				rowIndex++;

			}

		} catch (IOException e) {
			log.error("Hata : #0", e);
			facesMessages.add(e.getMessage());
		}
		*/

	}

	/**
	 * Assigns cell values to contact with index.
	 * @param contact
	 * @param cellValue
	 * @param index
	 */
	@SuppressWarnings("deprecation")
	private void assignCellValuesToContact(Contact contact, String cellValue, int columnIndex, int rowIndex) {
		switch (columnIndex) {
			case 0:
				if (cellValue == null || cellValue.length() == 0 ) {
					addMessage("#0 #{messages['contactImporter.label.ProblemInContactName']}", rowIndex);
				} else {
					contact.setName(cellValue);
				}
				break;
			case 1:
				contact.setMidname(cellValue);
				break;
			case 2:
				if (cellValue == null || cellValue.length() == 0 ) {
					addMessage("#0 #{messages['contactImporter.label.ProblemInContactSurName']}", rowIndex);
				} else {
					contact.setSurname(cellValue);
				}
				break;
			case 3:
				contact.setInfo(cellValue);
				break;
			case 4:
				if (cellValue.equals("KISI")) {
					contact.setPerson(Boolean.TRUE);
				} else if (cellValue.equals("KURUM")){
					contact.setPerson(Boolean.FALSE);
				} else {
					//nolu satırda kişi veya kurum bilgisi hatalı!
					addMessage("#0 #{messages['contactImporter.label.ProblemInPersonOrCompany']}", rowIndex);
				}
				break;
			case 5:
				contact.setRepresentative(cellValue);
				break;
			case 6:
				contact.setCompany(cellValue);
				break;
			case 7:
				contact.setTitle(cellValue);
				break;
			case 8:
//				contact.setPhone1(cellValue);
				break;
			case 9:
//				contact.setPhone2(cellValue);
				break;
			case 10:
//				contact.setPhone3(cellValue);
				break;
			case 11:
//				contact.setPhoneMobile(cellValue);
				break;
			case 12:
//				contact.setPhoneFax(cellValue);
				break;
			case 13:
//				contact.getAddress1().setStreet(cellValue);
				break;
			case 14:
//				contact.getAddress1().setCountry(cellValue);
				break;
			case 15:
//				contact.getAddress1().setProvince(cellValue);
				break;
			case 16:
//				contact.getAddress1().setCity(cellValue);
				break;
			case 17:
//				contact.getAddress1().setZip(cellValue);
				break;
			case 18:
				City city = findCityWithName(cellValue);
				
				//FIXME: contact modeli düzenlemelerinden sonra düzeltilecek.
//				if (city != null) {
//					contact.setCity1( city );
//				} else {
//					addMessage("#0 #{messages['contactImporter.label.ProblemInCity']}", rowIndex);
//				}
				break;
			case 19:
				Province province = findProvinceWithName(cellValue);

				//FIXME: contact modeli düzenlemelerinden sonra düzeltilecek.
//				if (province != null) {
//					contact.setProvince1( province );
//				} else {
//					addMessage("#0 #{messages['contactImporter.label.ProblemInProvince']}", rowIndex);
//				}
				break;
			case 20:
//				contact.setEmail1(cellValue);
				break;
			case 21:
//				contact.setEmail2(cellValue);
				break;
			case 22:
//				contact.setWeb(cellValue);
				break;
			case 23:
				contact.setTaxNumber(cellValue);
				break;
			case 24:
				contact.setTaxOffice(cellValue);
				break;
			case 25:
				contact.setSsn(cellValue);
				break;
			case 26:
				contact.setExCode1(cellValue);
				break;
			case 27:
				contact.setExCode2(cellValue);
				break;
			case 28:
				//FIXME: şimdilik böyle kalsın bir çözüm bulunacak.
//				contact.setBankName(cellValue);
				break;
			case 29:
//				contact.setBranchName(cellValue);
				break;
			case 30:
//				contact.setAccount(cellValue);
				break;
			case 31:
//				contact.setSwiftCode( cellValue );
				break;
			case 32:
//				contact.getAddress2().setStreet(cellValue);
				break;
			case 33:
//				contact.getAddress2().setCountry(cellValue);
				break;
			case 34:
//				contact.getAddress2().setProvince(cellValue);
				break;
			case 35:
//				contact.getAddress2().setCity(cellValue);
				break;
			case 36:
//				contact.getAddress2().setZip(cellValue);
				break;
			case 37:
				//not required
//				contact.setCity2( findCityWithName(cellValue) );
				break;
			case 38:
				//not required
//				contact.setProvince2( findProvinceWithName(cellValue) );
				break;
			case 39:
				BigDecimal debitLimit = BigDecimal.ZERO;
				try {
					debitLimit = BigDecimal.valueOf(Double.parseDouble(cellValue));

					contact.setDebitLimit( debitLimit );
				} catch (Exception e) {
					addMessage("#0 #{messages['contactImporter.label.ProblemInDebitLimit']}", rowIndex);
				}
				break;
			case 40:
				BigDecimal riskLimit = BigDecimal.ZERO;
				try {
					riskLimit = BigDecimal.valueOf(Double.parseDouble(cellValue));

					contact.setRiskLimit( riskLimit );
				} catch (Exception e) {
					addMessage("#0 #{messages['contactImporter.label.ProblemInRiskLimit']}", rowIndex);
				}
				break;
			case 41:
//				if (cellValue.equals("0")) {
//					contact.setType(ContactType.All);
//				} else if (cellValue.equals("1")) {
//					contact.setType(ContactType.Customer);
//				} else if (cellValue.equals("2")) {
//					contact.setType(ContactType.Provider);
//				} else if (cellValue.equals("3")) {
//					contact.setType(ContactType.Agent);
//				} else if (cellValue.equals("4")) {
//					contact.setType(ContactType.Personnel);
//				} else if (cellValue.equals("5")) {
//					contact.setType(ContactType.Branch);
//				} else {
//					addMessage("#0 #{messages['contactImporter.label.ProblemInContactType']}", rowIndex);
//				}
				break;
			default:
				break;
		}
	}
	
    public void fileUploadListener( UploadEvent event ) {
    	messageList.clear();
    	contactList.clear();
    	
        UploadItem item = event.getUploadItem();

        log.info("Data #0, File #1, TempFile #2", item.getData(), item.getFile(), item.isTempFile());

        File uploadedFile = null;
        if( item != null ){
            uploadedFile = item.getFile();
        }
        
        importContacts(uploadedFile);

    }
    
    /**
     * Finds province with given name.
     * @param provinceName
     */
    private Province findProvinceWithName(String provinceName) {
    	Province province = null;
    	try {
    		province = (Province)entityManager.createQuery("select p from Province p where " +
					    								   "p.name =:provinceName")
					    								   .setParameter("provinceName", provinceName)
					    								   .getSingleResult();
		} catch (Exception e) {
			log.error("Hata: #0", e);
		}
    	return province;
    }
    
    /**
     * Finds city with given name.
     * @param cityName
     */
    private City findCityWithName(String cityName) {
    	City city = null;
    	try {
    		city = (City)entityManager.createQuery("select c from City c where " +
										    	   "c.name =:provinceName")
										    	   .setParameter("provinceName", cityName)
										    	   .getSingleResult();
    	} catch (Exception e) {
    		log.error("Hata: #0", e);
    	}
    	return city;
    }
    
    @Transactional
    public String save() {
    	manualFlush();
    	String result = BaseConsts.SUCCESS;
    	try {

			if (messageList.size() > 0) {
				result = BaseConsts.FAIL;
				ContactImportException ex = new ContactImportException();
				ex.add(messageList);
				throw ex;
			}

    		for (Contact contact : contactList) {

    			contactHome.setEntity(contact);
    			
    			if ( contactHome.save().equals(BaseConsts.FAIL) ) {
    				result = BaseConsts.FAIL;
    				throw new RuntimeException("Hata");
    			}
    		}

	    } catch (ContactImportException e) {
	    	log.error("Hata: #0", e);
	    	facesMessages.add(e.getMessage());
	    } catch (Exception e) {
	    	log.error("Hata: #0", e);
	    	facesMessages.add(e.getMessage());
	    }
	    return result;
    }
    
    public void manualFlush() {
        Conversation.instance().changeFlushMode(org.jboss.seam.annotations.FlushModeType.MANUAL);
    }

	public List<Contact> getContactList() {
		return contactList;
	}

	public void setContactList(List<Contact> contactList) {
		this.contactList = contactList;
	}

	@Remove
    @Destroy
    public void destroy() {
    }

	private void addMessage( String s, Object...params){
		messageList.add( Interpolator.instance().interpolate(s, params));
	}

}
