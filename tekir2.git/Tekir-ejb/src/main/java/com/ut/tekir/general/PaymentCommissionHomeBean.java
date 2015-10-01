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

import javax.ejb.Stateful;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.annotations.datamodel.DataModel;

import com.ut.tekir.entities.AuditBase;
import com.ut.tekir.entities.PaymentCommission;
import com.ut.tekir.entities.User;
import com.ut.tekir.framework.BaseConsts;
import com.ut.tekir.framework.EntityHome;

/**
 * Ödeme sözleşmelerinde kullanılacak komisyon tip tanımlamalarının yapıldığı home sınıfı..
 * @author volkan
 *
 */
@Stateful
@Name("paymentCommissionHome")
@Scope(value=ScopeType.CONVERSATION)
public class PaymentCommissionHomeBean extends EntityHome<PaymentCommission> implements PaymentCommissionHome<PaymentCommission> {
    
    @DataModel("paymentCommissionList")
    private List<PaymentCommission> entityList;
    
    @SuppressWarnings("unchecked")
	@Factory("paymentCommissionList")
    public void initPaymentCommissionList() {
        log.debug("PaymentCommissionList Listesi hazırlanıyor...");
        
        setEntityList(getEntityManager().createQuery("select p from PaymentCommission p")
        			 .getResultList());
    }
    
    @Out(required=false)
    public PaymentCommission getPaymentCommission() {
        return getEntity();
    }

    @In(required=false)
    public void setPaymentCommission(PaymentCommission paymentCommission) {
        setEntity( paymentCommission );
    }

    @Override
    public void createNew() {
        log.debug("Yeni Komisyon");
        entity = new PaymentCommission();
        entity.setActive(true);
    }

    @Override
	@Transactional
	public String save() {
        log.info("Saving commission. Commission name :{0}", entity.getName());
		String result = BaseConsts.FAIL;

		try {
            setAuditRecords();
            super.save();
            } catch (Exception e) {
                facesMessages.add(e.getMessage());
                log.error("Hata: {0}", e);
                result = BaseConsts.FAIL;
            }
		return result;
    }

    private void setAuditRecords() {
	    User activeUser = (User)Component.getInstance("activeUser",true);

		AuditBase ab = (AuditBase)entity;
		if (entityId() == null) {
			ab.setCreateDate(new Date());
			ab.setCreateUser(activeUser.getUserName());
		}
		ab.setUpdateDate(new Date());
		ab.setUpdateUser(activeUser.getUserName());
    }

    @Override
    public List<PaymentCommission> getEntityList() {
        return entityList;
    }

    public void setEntityList(List<PaymentCommission> entityList) {
        this.entityList = entityList;
    }

}
