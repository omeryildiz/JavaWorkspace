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

package com.ut.tekir.general;

import java.util.Date;
import java.util.List;

import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.persistence.EntityManager;

import org.hibernate.Criteria;
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
import com.ut.tekir.entities.WorkBunch;
import com.ut.tekir.entities.WorkBunchStatus;

/**
 * @author cenk.canarslan
 *
 */
@Stateful
@Name("workBunchSuggestion")
@Scope(ScopeType.SESSION)
public class WorkBunchSuggestionBean implements WorkBunchSuggestion {

    @Logger
    private Log log;
    @In
    private EntityManager entityManager;

    private List<WorkBunch> workBunchList;

	private Contact contact;
    private String code;
    private String name;
    private WorkBunchStatus workBunchStatus;
    private Date beginDate;
    private Date endDate;
    
	@SuppressWarnings("unchecked")
	public List<WorkBunch> suggestWorkBunch(Object event) {
        String pref = event.toString();

        log.debug("suggestWorkBunch Req : {0}", pref);

        return entityManager.createQuery("select fu from WorkBunch fu where ( fu.code like :code or fu.name like :name ) and active = 1")
                .setParameter("code", pref + "%")
                .setParameter("name", "%" + pref + "%")
                .setMaxResults(30)
                .setHint("org.hibernate.cacheable", true)
                .getResultList();
    }

    @SuppressWarnings("unchecked")
	public List<WorkBunch> getActiveWorkBunchs(){
    	return entityManager.createQuery("select f from WorkBunch f where f.active = :isActive")
    				.setParameter("isActive", Boolean.TRUE)
    				.getResultList();
    }
    
	@SuppressWarnings("unchecked")
	public void selectWorkBunchList(){
		
    	HibernateSessionProxy session = (HibernateSessionProxy) entityManager.getDelegate();
    	Criteria crit = session.createCriteria(WorkBunch.class);
    	
        if( getCode() != null && getCode().length() > 0 ){
            crit.add( Restrictions.like("this.code", getCode() + "%" ));
        }
        if( getName() != null && getName().length() > 0 ){
            crit.add( Restrictions.like("this.name", getName() + "%" ));
        }
        if (getContact() != null){
        	crit.add(Restrictions.eq("this.contact", getContact()));
        }
       	if(getWorkBunchStatus() != null && getWorkBunchStatus() != WorkBunchStatus.All){
        	crit.add(Restrictions.eq("this.WorkBunchStatus", getWorkBunchStatus()));
        }
        if (getEndDate() != null){
        	crit.add(Restrictions.le("this.endDate", getEndDate()));
        }
    	if(getBeginDate() != null){
    		crit.add(Restrictions.ge("this.beginDate", getBeginDate()));
    	}
    	crit.add(Restrictions.eq("this.active", true));
    	
    	crit.setProjection(Projections.projectionList()
    			.add(Projections.property("code"), "code")
    			.add(Projections.property("name"), "name")
    			.add(Projections.property("contact"), "contact")
    			.add(Projections.property("workBunchStatus"), "workBunchStatus")
    			.add(Projections.property("beginDate"), "beginDate")
    			.add(Projections.property("endDate"), "endDate")
    			);
    	
    	crit.setMaxResults(30);
    	
    	setWorkBunchList(crit.list());
    }
    
    
    
    
    @Remove @Destroy
    public void destroy() {
    }

    public List<WorkBunch> getWorkBunchList() {
		return workBunchList;
	}

	public void setWorkBunchList(List<WorkBunch> workBunchList) {
		this.workBunchList = workBunchList;
	}

	public Contact getContact() {
		return contact;
	}

	public void setContact(Contact contact) {
		this.contact = contact;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public WorkBunchStatus getWorkBunchStatus() {
		return workBunchStatus;
	}

	public void setWorkBunchStatus(WorkBunchStatus workBunchStatus) {
		this.workBunchStatus = workBunchStatus;
	}

	public Date getBeginDate() {
		return beginDate;
	}

	public void setBeginDate(Date beginDate) {
		this.beginDate = beginDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
    
    
    
    
    
/*    
    
    public void selectProductList(){
        
    	HibernateSessionProxy session = (HibernateSessionProxy) entityManager.getDelegate();

    	Criteria crit = session.createCriteria( Product.class );
        
        if( getCode() != null && getCode().length() > 0 ){
            crit.add( Restrictions.like("this.code", getCode() + "%" ));
        }
        
        if( getName() != null && getName().length() > 0 ){
            crit.add( Restrictions.like("this.name", getName() + "%" ));
        }

        if( getBarcode() != null && getBarcode().length() > 0 ){
        	SimpleExpression barcodeCrit1 = Restrictions.like("this.barcode1", getBarcode() + "%" );
        	SimpleExpression barcodeCrit2 = Restrictions.like("this.barcode2", getBarcode() + "%" );
        	SimpleExpression barcodeCrit3 = Restrictions.like("this.barcode3", getBarcode() + "%" );
        	
        	crit.add(Restrictions.or(Restrictions.or(barcodeCrit1, barcodeCrit2), barcodeCrit3));
        }
        
        if( getProductType() != ProductType.Unknown ){
        	crit.add( Restrictions.eq("this.productType", getProductType() ));
        }

        if(getCategory() != null){
            crit.add(Restrictions.eq("this.category", getCategory()));
        }

        if(getGroup() != null){
            crit.add(Restrictions.eq("this.group", getGroup()));
        }

        crit.setProjection(Projections.projectionList()
                .add(Projections.property("code"), "code")
                .add(Projections.property("name"), "name")
                .add(Projections.property("productType"), "productType")
                .add(Projections.property("barcode1"), "barcode1")
                .add(Projections.property("category"),"category")
                .add(Projections.property("group"),"group"));
        
        crit.add(Restrictions.eq("this.active", true));
        
        crit.setMaxResults(30);
//        crit.setCacheable(true);
        //TODO: Map niye çalışmıyor kine?
        //crit.setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP);
        productList = crit.list();
        
    }
    
    public void selectExpenseAndDiscountList(){
    	
    	HibernateSessionProxy session = (HibernateSessionProxy) entityManager.getDelegate();
    	Criteria crit = session.createCriteria( Product.class );
    	
    	if( getCode() != null && getCode().length() > 0 ){
    		crit.add( Restrictions.like("this.code", getCode() + "%" ));
    	}
    	
    	if( getName() != null && getName().length() > 0 ){
    		crit.add( Restrictions.like("this.name", getName() + "%" ));
    	}
    	
    	if( getProductType() != null && getProductType() == ProductType.Unknown){
    		crit.add( Restrictions.or( Restrictions.eq("this.productType", ProductType.Expense ), Restrictions.eq("this.productType", ProductType.Discount) ));
    	}else{
    		crit.add(Restrictions.eq("this.productType", getProductType()));
    	}
    	
    	crit.setProjection(Projections.projectionList()
    			.add(Projections.property("code"), "code")
    			.add(Projections.property("name"), "name")
    			.add(Projections.property("productType"), "productType"));
    	
    	crit.add(Restrictions.eq("active", true));
    	
    	crit.setMaxResults(30);
    	crit.setCacheable(true);
    	//TODO: Map niye çalışmıyor kine?
    	//crit.setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP);
    	expenseAndDiscountList = crit.list();
    	
    }
    
 */
}
