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

import java.util.HashMap;
import java.util.Map;

import javax.ejb.Stateful;

import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Scope;

import com.ut.tekir.entities.Contact;
import com.ut.tekir.entities.ContactType;
import com.ut.tekir.entities.User;
import com.ut.tekir.entities.UserRole;
import com.ut.tekir.framework.BrowserBase;

/**
 *
 * @author haky , sinan, volkan
 */
@Stateful
@Name("contactBrowse")
@Scope(value = ScopeType.SESSION)
public class ContactBrowseBean extends BrowserBase<ContactModel, ContactFilterModel> implements ContactBrowse<ContactModel, ContactFilterModel> {
		
	private Integer exid;
	private ContactNetworkHelper networkHelper;
	private ContactPhoneHelper phoneHelper;
	private ContactAddressHelper addressHelper;

	@Override
	public void init() {
		super.init();
		initHelpers();
	}
	
	private void initHelpers() {
		networkHelper = ContactNetworkHelper.instance();
		phoneHelper = ContactPhoneHelper.instance();
		addressHelper = ContactAddressHelper.instance();
	}

	@In
	User activeUser;
	
    @Override
    public ContactFilterModel newFilterModel() {
        return new ContactFilterModel();
    }
    
    @Override
    public DetachedCriteria buildCriteria() {
    	DetachedCriteria crit = DetachedCriteria.forClass(Contact.class);
        
        crit.setProjection(Projections.distinct(Projections.projectionList()
                .add(Projections.property("id"), "id")
                .add(Projections.property("code"), "code")
                .add(Projections.property("name"), "name")
                .add(Projections.property("fullname"), "fullname")
                .add(Projections.property("ssn"), "ssn")
                .add(Projections.property("company"), "company")
                .add(Projections.property("taxNumber"), "taxNumber")
                .add(Projections.property("taxOffice"), "taxOffice")
                .add(Projections.property("title"), "title")
                .add(Projections.property("representative"), "representative")
                .add(Projections.property("info"), "info")
                .add(Projections.property("exCode1"), "exCode1")
                .add(Projections.property("exCode2"), "exCode2")
                .add(Projections.property("allType"), "allType")
                .add(Projections.property("customerType"), "customerType")
                .add(Projections.property("providerType"), "providerType")
                .add(Projections.property("agentType"), "agentType")
                .add(Projections.property("personnelType"), "personnelType")
                .add(Projections.property("branchType"), "branchType")
                .add(Projections.property("contactType"), "contactType")
                .add(Projections.property("bankType"), "bankType")
                .add(Projections.property("relatedType"), "relatedType")
                .add(Projections.property("foundationType"), "foundationType")
                ));

        crit.setResultTransformer(Transformers.aliasToBean(ContactModel.class));
       
        if (filterModel.getCode() != null && filterModel.getCode().length() > 0) {
            crit.add(Restrictions.ilike("this.code", filterModel.getCode()  ,MatchMode.START));
        }

        if (filterModel.getFullname() != null && filterModel.getFullname().length() > 0) {
            crit.add(Restrictions.ilike("this.fullname", filterModel.getFullname() ,MatchMode.ANYWHERE));
        }

        if (filterModel.getCompany() != null && filterModel.getCompany().length() > 0) {
            crit.add(Restrictions.ilike("this.company", filterModel.getCompany() ,MatchMode.START));
        }

        if (filterModel.getSsn() != null && filterModel.getSsn().length() > 0) {
            crit.add(Restrictions.ilike("this.ssn", filterModel.getSsn() ,MatchMode.START));
        }

        if (filterModel.getTaxNumber() != null && filterModel.getTaxNumber().length() > 0) {
            crit.add(Restrictions.ilike("this.taxNumber", filterModel.getTaxNumber() ,MatchMode.START));
        }

        if (filterModel.getRepresentative() != null && filterModel.getRepresentative().length() > 0) {
            crit.add(Restrictions.ilike("this.representative", filterModel.getRepresentative()  ,MatchMode.START));
        }

        if (filterModel.getExCode1() != null && filterModel.getExCode1().length() > 0) {
            crit.add(Restrictions.ilike("this.exCode1", filterModel.getExCode1()  ,MatchMode.START));
        }

        if (filterModel.getExCode2() != null && filterModel.getExCode2().length() > 0) {
            crit.add(Restrictions.ilike("this.exCode2", filterModel.getExCode2()  ,MatchMode.START));
        }

        if (filterModel.getCategory() != null) {
            crit.add(Restrictions.eq("this.category", filterModel.getCategory()));
        }

        if (filterModel.getOrganization() != null) {
        	crit.add(Restrictions.eq("this.organization", filterModel.getOrganization()));
        }

        if (filterModel.getCompanyType() !=null && !filterModel.getCompanyType().equals("All")){
            if(filterModel.getCompanyType().equals("Person")){
                crit.add(Restrictions.eq("this.person", Boolean.TRUE));
            }else
                crit.add(Restrictions.eq("this.person", Boolean.FALSE));
        }
        
        if (filterModel.getType() != null && filterModel.getType() != ContactType.All) {
    		crit.add(Restrictions.eq("this." + filterModel.getType().toString().toLowerCase() + "Type", Boolean.TRUE));
        }

        if (filterModel.getCountry() != null) {
        	crit.createAlias("this.addressList", "addressList", CriteriaSpecification.INNER_JOIN);
        	crit.add(Restrictions.eq("addressList.address.country", filterModel.getCountry() ));
        
	        if (filterModel.getCity1() != null) {            
	            crit.add(Restrictions.eq("addressList.city", filterModel.getCity1() ));
	        } 
        }

        //FIXME: bu kontrol nasıl olmalı?
        if (hasRegionRestriction()) {
        	if (activeUser.getContact() != null && activeUser.getContact().getOrganization() != null 
        			&& activeUser.getContact().getOrganization() != null) {
        		
        		crit.add(Restrictions.or(
        				 Restrictions.eq("this.organization.id", activeUser.getContact().getOrganization().getId()),
        				 Restrictions.eq("this.isPublic", Boolean.TRUE) ));
        	}
        }
        crit.addOrder(Order.desc("this.code"));

        return crit;
    }

    @Observer("refreshBrowser:com.ut.tekir.entities.Contact")
    public void refreshResults() {
        log.debug("Uyarı geldi resultSet tazeleniyor");
        if (getEntityList() == null || getEntityList().isEmpty()) {
            return;
        }
        search();
    }

    @SuppressWarnings("unchecked")
	public void pdf() {
        Map params = new HashMap();

        if (filterModel.getCode() != null && filterModel.getCode().length() > 0) {
            params.put("pCode", filterModel.getCode());
        }
        
        if (filterModel.getFullname() != null && filterModel.getFullname().length() > 0) {
            params.put("pName", filterModel.getFullname());
        }
        
        if (isNotEmpty(filterModel.getSsn())){
        	params.put("pSSN", filterModel.getSsn());
        }
        
        if(filterModel.getCompanyType() != null && !filterModel.getCompanyType().equals("All")){
        	if(filterModel.getCompanyType().equals("Person")){
        		params.put("pComType", Boolean.TRUE);
        	}
        	else{
        		params.put("pComType", Boolean.FALSE);
        	}
        }
        
        if (filterModel.getType() != null && filterModel.getType() != ContactType.All) {
        	params.put("pType",filterModel.getType().toString().toLowerCase() + "Type"); 
        }
        
        if (filterModel.getCompany() != null && filterModel.getCompany().length() > 0) {
            params.put("pComp", filterModel.getCompany() );
        } 
        
        if (filterModel.getRepresentative() != null && filterModel.getRepresentative().length() > 0) {
            params.put("pRep", filterModel.getRepresentative() );
        } 
        
        if(isNotEmpty(filterModel.getTaxNumber())){
        	params.put("pTaxNo", filterModel.getTaxNumber());
        }
        
        if(filterModel.getOrganization() != null){
        	params.put("pOrg", filterModel.getOrganization().getId());
        }
        
        if(filterModel.getCity1()!= null){
        	params.put("pCity1", filterModel.getCity1().getId());
        }
        
        if (filterModel.getCategory() != null) {
            params.put("pCate", filterModel.getCategory().getId());
        }
        
        if (filterModel.getExCode1() != null && filterModel.getExCode1().length() > 0) {
            params.put("pEx1", filterModel.getExCode1());
        }
        
        if (filterModel.getExCode2() != null && filterModel.getExCode2().length() > 0) {
            params.put("pEx2", filterModel.getExCode2());
        }

        
        execPdf("cari_listesi", "Cari_Listesi", params);

    }

    private Boolean hasRegionRestriction() {
    	Boolean result = false;
    	Map<String, Boolean> roleMap = new HashMap<String, Boolean>();

    	for (UserRole role : activeUser.getRoles()) {
            roleMap.put(role.getRole().getName(), true);
        }
        if (!( roleMap.containsKey("ADMIN") || roleMap.containsKey("SYSOP") )) {
			result = true;
		}
    	return result;
    }
    
    @SuppressWarnings("unchecked")
	public void detailedPdf() {
        Map params = new HashMap();

        if (filterModel.getCode() != null && filterModel.getCode().length() > 0) {
            params.put("pCode", filterModel.getCode() + "%");
            params.put("pNullCode", 0);
        } else{
        	params.put("pNullCode", 1);
        }
        
        if (filterModel.getFullname() != null && filterModel.getFullname().length() > 0) {
            params.put("pName", filterModel.getFullname() + "%");
            params.put("pNullName", 0);
        } else{
        	params.put("pNullName", 1);       
        }
       
        if (filterModel.getCompany() != null && filterModel.getCompany().length() > 0) {
            params.put("pComp", filterModel.getCompany() + "%");
            params.put("pNullComp", 0);
        } else {
        	params.put("pNullComp", 1);
        }
        
        if (filterModel.getRepresentative() != null && filterModel.getRepresentative().length() > 0) {
            params.put("pRep", filterModel.getRepresentative() + "%");
            params.put("pNullRep", 0);
        } else {
        	params.put("pNullRep", 1);
        }
        
        if (filterModel.getExCode1() != null && filterModel.getExCode1().length() > 0) {
            params.put("pEx1", filterModel.getExCode1() + "%");
            params.put("pNullEx1", 0);
        } else{
        	params.put("pNullEx1", 1);
        }
        
        if (filterModel.getExCode2() != null && filterModel.getExCode2().length() > 0) {
            params.put("pEx2", filterModel.getExCode2() + "%");
            params.put("pNullEx2", 0);
        } else {
        	params.put("pNullEx2", 1);
        }
        
        if(filterModel.getOrganization() != null){
        	params.put("pOrg", filterModel.getOrganization().getId());
        }

        if (filterModel.getCategory() != null) {
            params.put("pCate", filterModel.getCategory().getId());
        }
        
        if (filterModel.getType() != null && filterModel.getType() != ContactType.All) {
        	params.put("pType", filterModel.getType().ordinal());
        }

        execPdf("cari_detay", "Cari_Listesi_Detayli", params);
    }

    @SuppressWarnings("unchecked")
	public void xlsToPdf() {
        Map params = new HashMap();
        
        if (filterModel.getCode() != null && filterModel.getCode().length() > 0) {
            params.put("pCode", filterModel.getCode());
        }
        
        if (filterModel.getFullname() != null && filterModel.getFullname().length() > 0) {
            params.put("pName", filterModel.getFullname());
        }
        
        if (isNotEmpty(filterModel.getSsn())){
        	params.put("pSSN", filterModel.getSsn());
        }
        
        if(filterModel.getCompanyType() != null && !filterModel.getCompanyType().equals("All")){
        	if(filterModel.getCompanyType().equals("Person")){
        		params.put("pComType", Boolean.TRUE);
        	}
        	else{
        		params.put("pComType", Boolean.FALSE);
        	}
        }
        
        if (filterModel.getType() != null && filterModel.getType() != ContactType.All) {
        	params.put("pType",filterModel.getType().toString().toLowerCase() + "Type"); 
        }
        
        if (filterModel.getCompany() != null && filterModel.getCompany().length() > 0) {
            params.put("pComp", filterModel.getCompany() );
        } 
        
        if (filterModel.getRepresentative() != null && filterModel.getRepresentative().length() > 0) {
            params.put("pRep", filterModel.getRepresentative() );
        } 
        
        if(isNotEmpty(filterModel.getTaxNumber())){
        	params.put("pTaxNo", filterModel.getTaxNumber());
        }
        
        if(filterModel.getOrganization() != null){
        	params.put("pOrg", filterModel.getOrganization().getId());
        }
        
        if(filterModel.getCity1()!= null){
        	params.put("pCity1", filterModel.getCity1().getId());
        }
        
        if (filterModel.getCategory() != null) {
            params.put("pCate", filterModel.getCategory().getId());
        }
        
        if (filterModel.getExCode1() != null && filterModel.getExCode1().length() > 0) {
            params.put("pEx1", filterModel.getExCode1());
        }
        
        if (filterModel.getExCode2() != null && filterModel.getExCode2().length() > 0) {
            params.put("pEx2", filterModel.getExCode2());
        }

        execPdf("cari_listesi_excel", "Cari_Listesi", params);

    }

    
    @SuppressWarnings("unchecked")
	public void xls() {
        Map params = new HashMap();
        
        if (filterModel.getCode() != null && filterModel.getCode().length() > 0) {
            params.put("pCode", filterModel.getCode());
        }
        
        if (filterModel.getFullname() != null && filterModel.getFullname().length() > 0) {
            params.put("pName", filterModel.getFullname());
        }
        
        if (isNotEmpty(filterModel.getSsn())){
        	params.put("pSSN", filterModel.getSsn());
        }
        
        if(filterModel.getCompanyType() != null && !filterModel.getCompanyType().equals("All")){
        	if(filterModel.getCompanyType().equals("Person")){
        		params.put("pComType", Boolean.TRUE);
        	}
        	else{
        		params.put("pComType", Boolean.FALSE);
        	}
        }
        
        if (filterModel.getType() != null && filterModel.getType() != ContactType.All) {
        	params.put("pType",filterModel.getType().toString().toLowerCase() + "Type"); 
        }
        
        if (filterModel.getCompany() != null && filterModel.getCompany().length() > 0) {
            params.put("pComp", filterModel.getCompany() );
        } 
        
        if (filterModel.getRepresentative() != null && filterModel.getRepresentative().length() > 0) {
            params.put("pRep", filterModel.getRepresentative() );
        } 
        
        if(isNotEmpty(filterModel.getTaxNumber())){
        	params.put("pTaxNo", filterModel.getTaxNumber());
        }
        
        if(filterModel.getOrganization() != null){
        	params.put("pOrg", filterModel.getOrganization().getId());
        }
        
        if(filterModel.getCity1()!= null){
        	params.put("pCity1", filterModel.getCity1().getId());
        }
        
        if (filterModel.getCategory() != null) {
            params.put("pCate", filterModel.getCategory().getId());
        }
        
        if (filterModel.getExCode1() != null && filterModel.getExCode1().length() > 0) {
            params.put("pEx1", filterModel.getExCode1());
        }
        
        if (filterModel.getExCode2() != null && filterModel.getExCode2().length() > 0) {
            params.put("pEx2", filterModel.getExCode2());
        }

        execXls("cari_listesi_excel", "Cari_Listesi", params);

    }

    public void initDefaults(int rowKey) {
    	if (getEntityList().get(rowKey).isInitialized()) return; 
    	initDefaultNetworks(rowKey);
    	initDefaultPhones(rowKey);
    	initDefaultAddress(rowKey);
    	initCaption(rowKey);
    }
    
    private void initDefaultNetworks(int rowKey) {
    	ContactModel model = getEntityList().get(rowKey);
    	networkHelper.initDefaultNetworks(model);
    }

    private void initDefaultPhones(int rowKey) {
    	ContactModel model = getEntityList().get(rowKey);
    	phoneHelper.initDefaultPhones(model);
    }

    private void initDefaultAddress(int rowKey) {
    	ContactModel model = getEntityList().get(rowKey);
    	addressHelper.initDefaultAddress(model);
    }

    public void initCaption(int rowKey) {
    	ContactModel model = getEntityList().get(rowKey);
		ContactCaption.initCaption(model);
    }
    
	public Integer getExid() {
		return exid;
	}

	public void setExid(Integer exid) {
		this.exid = exid;
	}
    
}
