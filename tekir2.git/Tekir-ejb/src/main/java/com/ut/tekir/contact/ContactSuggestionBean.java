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
import javax.persistence.EntityManager;

import org.hibernate.Criteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.log.Log;
import org.jboss.seam.persistence.HibernateSessionProxy;

import com.ut.tekir.entities.Contact;
import com.ut.tekir.entities.ContactCategory;
import com.ut.tekir.entities.ContactType;

/**
 *
 * @author haky
 */
@Stateful
@Name("contactSuggestion")
@Scope(value=ScopeType.SESSION)
public class ContactSuggestionBean implements ContactSuggestion {

    @Logger
    private Log log;
    @In
    private EntityManager entityManager;
    
    @SuppressWarnings("unchecked")
	private List contactList;
    private String code;
    private String fullname;
    private ContactType type;
    private ContactCategory category;
    private String ssn;
    private String taxNumber;
    private String company;
    private String exCode1;
    private String exCode2;

    @SuppressWarnings("unchecked")
	public List<ContactCategory> suggestContactCategory(Object event){
        String pref = event.toString();

        log.debug("suggest category  : {0}", pref );
        
        return entityManager.createQuery("select c from ContactCategory c where ( c.code like :code ) and active = 1" )
                .setParameter("code", pref + "%")
                .setMaxResults(30)
                .setHint("org.hibernate.cacheable", true)
                .getResultList();
    }

    @SuppressWarnings("unchecked")
    public Contact findContact(Long conId){
    	log.debug("Finding contact with id: #0", conId);
    	Contact con = null;
    	try{
	    	List<Contact> contactList = entityManager.createQuery("select c from Contact c where c.id =:conId")
	    	.setParameter("conId", conId)
	    	.getResultList();
    	
	    	if(contactList != null && contactList.size()>0){
	    		con=contactList.get(0);
	    	}
    	}
    	catch (Exception e) {
    		log.error("Hata oluştu. Sebebi :#0", e);
		}
    	return con;
    }
    
    @SuppressWarnings("unchecked")
	public List<Contact> suggestContact(Object event){
        String pref = event.toString();

        log.debug("suggest contact  : {0}", pref );
        //tum muhatablar
        return entityManager.createQuery("select c from Contact c " +
        		" where ( LOWER(c.code) like :code or LOWER(c.fullname) like :fullname or LOWER(c.company) like :comp ) and active = 1" )
                .setParameter("code", pref.toLowerCase() + "%")
                .setParameter("fullname", "%" + pref.toLowerCase() + "%")
                .setParameter("comp","%" + pref.toLowerCase() + "%")
                .setMaxResults(30)
                .setHint("org.hibernate.cacheable", true)
                .getResultList();
    }
    
    @SuppressWarnings("unchecked")
	public List<Contact> suggestProvider(Object event){
        String pref = event.toString();

        log.debug("suggest provider  : {0}", pref );
        //satıcı (genelde alış fişlerinde kullanılır)
        return entityManager.createQuery("select c from Contact c where c.providerType = 1 " +
        		" and  ( LOWER(c.code) like :code or LOWER(c.fullname) like :fullname or LOWER(c.company) like :comp ) and active = 1" )
                .setParameter("code", pref.toLowerCase() + "%")
                .setParameter("fullname", "%" + pref.toLowerCase() + "%")
                .setParameter("comp","%" + pref.toLowerCase() + "%")
                .setMaxResults(30)
                .setHint("org.hibernate.cacheable", true)
                .getResultList();
    }

    @SuppressWarnings("unchecked")
	public List<Contact> suggestPersonnel(Object event){
        String pref = event.toString();

        log.debug("suggest personnel  : {0}", pref );
        //personel
        return entityManager.createQuery("select c from Contact c where c.personnelType = 1 " +
        		" and ( LOWER(c.code) like :code or LOWER(c.fullname) like :fullname ) and active = 1" )
                .setParameter("code", pref.toLowerCase() + "%")
                .setParameter("fullname", "%" + pref.toLowerCase() + "%")
                .setMaxResults(30)
                .setHint("org.hibernate.cacheable", true)
                .getResultList();
    }
    
    @SuppressWarnings("unchecked")
	public List<Contact> suggestCustomer(Object event){
        String pref = event.toString();

        log.debug("suggest customer : {0}", pref );
        //musteri (genelde satış fişlerinde kullanılır)
        return entityManager.createQuery("select c from Contact c where c.customerType = 1 " +
        		" and ( LOWER(c.code) like :code or LOWER(c.fullname) like :fullname or LOWER(c.company) like :comp) and active = 1" )
                .setParameter("code", pref.toLowerCase() + "%")
                .setParameter("fullname", "%" + pref.toLowerCase() + "%")
                .setParameter("comp","%" + pref.toLowerCase() + "%")
                .setMaxResults(30)
                .setHint("org.hibernate.cacheable", true)
                .getResultList();
    }
    
    @SuppressWarnings("unchecked")
	public List<ContactCategory> getContactCategoryList(){
        
        return entityManager.createQuery("select c from ContactCategory c where active = 1 order by weight,code" )
                .setHint("org.hibernate.cacheable", true)
                .getResultList();
    }
    
    public void selectCustomerList(){
        
    	HibernateSessionProxy session = (HibernateSessionProxy) entityManager.getDelegate();

    	
    	Criteria crit = session.createCriteria( Contact.class );
        
        if( getCode() != null && getCode().length() > 0 ){
            crit.add( Restrictions.ilike("this.code", getCode(), MatchMode.START));
        }

        if( getCompany() != null && !getCompany().isEmpty() ){
            crit.add( Restrictions.ilike("this.company", getCompany(), MatchMode.START));
        }

        if( getSsn() != null && !getSsn().isEmpty() ){
            crit.add( Restrictions.ilike("this.ssn", getSsn(), MatchMode.START));
        }

        if( getTaxNumber() != null & !getTaxNumber().isEmpty() ){
            crit.add( Restrictions.ilike("this.taxNumber", getTaxNumber(), MatchMode.START));
        }
        
        if( getFullname() != null && getFullname().length() > 0 ){
            crit.add( Restrictions.ilike("this.fullname", getFullname(), MatchMode.ANYWHERE ));
        }
        if( getType() != null && getType() != ContactType.All){
        	crit.add( Restrictions.eq("this."+getType().toString().toLowerCase() + "Type", Boolean.TRUE));
        }
        if (getCategory() != null) {
            crit.add(Restrictions.eq("this.category", getCategory()));
        }
        if (getExCode1() != null & !getExCode1().isEmpty()) {
            crit.add(Restrictions.ilike("this.exCode1", getExCode1(), MatchMode.START));
        }
        if (getExCode2() != null & !getExCode2().isEmpty()) {
        	crit.add(Restrictions.ilike("this.exCode2", getExCode2(), MatchMode.START));
        }
        
        crit.setProjection(Projections.projectionList()
                .add(Projections.property("code"), "code")
                .add(Projections.property("fullname"), "fullname")
                .add(Projections.property("customerType"), "customerType")
                .add(Projections.property("personnelType"), "personnelType")
                .add(Projections.property("agentType"), "agentType")
                .add(Projections.property("branchType"), "branchType")
                .add(Projections.property("providerType"), "providerType")
                .add(Projections.property("contactType"), "contactType")
                .add(Projections.property("allType"), "allType")
                .add(Projections.property("id"), "id")
                .add(Projections.property("category"), "category")
                .add(Projections.property("taxNumber"), "taxNumber")
                .add(Projections.property("ssn"), "ssn")
                .add(Projections.property("company"), "company")
                .add(Projections.property("exCode2"), "exCode2")
                .add(Projections.property("exCode1"), "exCode1")
                );
        
        crit.add(Restrictions.eq("active", true));
        
        crit.setMaxResults(30);
        crit.setCacheable(true);
        //TODO: Map niye çalışmıyor kine?
        //crit.setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP);
        contactList = crit.list();
        
    }
    
    @Remove @Destroy
    public void destroy() {
    }

    @SuppressWarnings("unchecked")
	public List getContactList() {
        return contactList;
    }

    @SuppressWarnings("unchecked")
	public void setContactList(List contactList) {
        this.contactList = contactList;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public ContactType getType() {
    	return type;
    }
    
    public void setType(ContactType type) {
    	this.type = type;
    }

    public ContactCategory getCategory() {
        return category;
    }

    public void setCategory(ContactCategory category) {
        this.category = category;
    }

    public String getSsn() {
        return ssn;
    }

    public void setSsn(String ssn) {
        this.ssn = ssn;
    }

    public String getTaxNumber() {
        return taxNumber;
    }

    public void setTaxNumber(String taxNumber) {
        this.taxNumber = taxNumber;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getExCode2() {
        return exCode2;
    }

    public void setExCode2(String exCode2) {
        this.exCode2 = exCode2;
    }

	public String getExCode1() {
		return exCode1;
	}

	public void setExCode1(String exCode1) {
		this.exCode1 = exCode1;
	}

}
