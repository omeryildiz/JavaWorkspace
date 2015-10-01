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

import javax.ejb.Stateful;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.datamodel.DataModel;

import com.ut.tekir.entities.PaymentActionType;
import com.ut.tekir.framework.EntityHome;

/**
 * Ödeme tip tanımlamalarının yapıldığı home sınıfı..
 * @author volkan
 *
 */
@Stateful
@Name("paymentActionTypeHome")
@Scope(value=ScopeType.CONVERSATION)
public class PaymentActionTypeHomeBean extends EntityHome<PaymentActionType> implements PaymentActionTypeHome<PaymentActionType> {
    
    @DataModel("paymentActionTypeList")
    private List<PaymentActionType> entityList;
    
    @SuppressWarnings("unchecked")
	@Factory("paymentActionTypeList")
    public void initPaymentActionTypeList() {
        log.debug("PaymentActionTypeList Listesi hazırlanıyor...");
        
        setEntityList(getEntityManager().createQuery("select p from PaymentActionType p")
        			 .getResultList());
    }
    
    @Out(required=false)
    public PaymentActionType getPaymentActionType() {
        return getEntity();
    }

    @In(required=false)
    public void setPaymentActionType(PaymentActionType paymentActionType) {
        setEntity( paymentActionType );
    }

    @Override
    public void createNew() {
        log.debug("Yeni Komisyon");
        entity = new PaymentActionType();
        entity.setActive(true);
    }

    @Override
    public List<PaymentActionType> getEntityList() {
        return entityList;
    }

    public void setEntityList(List<PaymentActionType> entityList) {
        this.entityList = entityList;
    }

}
