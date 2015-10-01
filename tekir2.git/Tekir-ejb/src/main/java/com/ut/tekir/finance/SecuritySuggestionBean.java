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

package com.ut.tekir.finance;

import java.util.List;

import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.persistence.EntityManager;

import org.hibernate.Criteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.log.Log;
import org.jboss.seam.persistence.HibernateSessionProxy;

import com.ut.tekir.entities.BondPurchaseSale;
import com.ut.tekir.entities.Contact;
import com.ut.tekir.entities.ContactCategory;
import com.ut.tekir.entities.Security;

/**
 *
 * @author huseyin
 */
@Stateful
@Name("securitySuggestion")
public class SecuritySuggestionBean implements SecuritySuggestion {

    @Logger
    private Log log;
    @In
    private EntityManager entityManager;
    
    @SuppressWarnings("unchecked")
	private List securityList;
    private String code;
    private String isin;

    @SuppressWarnings("unchecked")
	public List<Security> suggestSecurity(Object event){
        String pref = event.toString();

        log.debug("suggest security: {0}", pref );
        
        return entityManager.createQuery("select c from Security c where c.code like :code or c.isin like :isin" )
                .setParameter("code", pref + "%")
                .setParameter("isin", "%" + pref + "%")
                .setMaxResults(30)
                .setHint("org.hibernate.cacheable", true)
                .getResultList();
    }
    
    public void selectSecurityList(){
        
    	HibernateSessionProxy session = (HibernateSessionProxy) entityManager.getDelegate();
        Criteria crit = session.createCriteria( Security.class );
        
        if( getCode() != null && getCode().length() > 0 ){
            crit.add( Restrictions.like("code", getCode() + "%" ));
        }
        
        if( getIsin() != null && getIsin().length() > 0 ){
            crit.add( Restrictions.like("isin", getIsin() + "%" ));
        }
        
        crit.setProjection(Projections.projectionList()
                .add(Projections.property("code"), "code")
                .add(Projections.property("isin"), "isin"));
        
        crit.setMaxResults(30);
        crit.setCacheable(true);
        securityList= crit.list();
        
    }
    
    @Remove @Destroy
    public void destroy() {
    }

    @SuppressWarnings("unchecked")
	public List getSecurityList() {
        return securityList;
    }

    @SuppressWarnings("unchecked")
	public void setSecurityList(List securityList) {
        this.securityList = securityList;
    }

    public String getIsin() {
        return isin;
    }

    public void setIsin(String isin) {
        this.isin = isin;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
