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

import java.util.List;

import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.persistence.EntityManager;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.log.Log;
import org.jboss.seam.persistence.HibernateSessionProxy;

import com.ut.tekir.entities.PaymentActionType;
import com.ut.tekir.entities.PaymentCommission;

/**
 * Ödeme komisyon suggestionı yapan home sınıfıdır.
 * @author sinan.yumak
 */
@Stateful
@Name("paymentCommisionSuggestion")
@Scope(ScopeType.CONVERSATION)
@AutoCreate
public class PaymentCommisionSuggestionBean implements PaymentCommisionSuggestion {

    @Logger
    Log log;
    @In
    EntityManager entityManager;
 
	private List<PaymentCommission> paymentCommissionList;
    private String code;
    private String name;
    private PaymentActionType paymentActionType;

    @SuppressWarnings("unchecked")
	public List<PaymentCommission> suggestPaymentCommission(Object event){
        String pref = event.toString();

        log.debug("suggest commission: {0}", pref );
        
        return entityManager.createQuery("select pc from PaymentCommission pc where pc.code like :code or pc.name like :name" )
                .setParameter("code", pref + "%")
                .setParameter("name", "%" + pref + "%")
                .setMaxResults(30)
                .setHint("org.hibernate.cacheable", true)
                .getResultList();
    }
    
    @SuppressWarnings("unchecked")
	public void selectPaymentCommissionList(){
        
    	HibernateSessionProxy session = (HibernateSessionProxy) entityManager.getDelegate();
        Criteria crit = session.createCriteria( PaymentCommission.class );
        
        if( getCode() != null && getCode().length() > 0 ){
            crit.add( Restrictions.like("code", getCode() + "%" ));
        }
        
        if( getName() != null && getName().length() > 0 ){
            crit.add( Restrictions.like("name", getName() + "%" ));
        }

        if( getPaymentActionType() != null ){
        	crit.add( Restrictions.eq("paymentActionType", getPaymentActionType()));
        }

        //FIXME: projection eklemeli.
        crit.setMaxResults(30);
        crit.setCacheable(true);
        paymentCommissionList = crit.list();
    }
    
    @Remove @Destroy
    public void destroy() {
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

	public List<PaymentCommission> getPaymentCommissionList() {
		return paymentCommissionList;
	}

	public void setPaymentCommissionList(
			List<PaymentCommission> paymentCommissionList) {
		this.paymentCommissionList = paymentCommissionList;
	}

	public PaymentActionType getPaymentActionType() {
		return paymentActionType;
	}

	public void setPaymentActionType(PaymentActionType paymentActionType) {
		this.paymentActionType = paymentActionType;
	}
	
}
