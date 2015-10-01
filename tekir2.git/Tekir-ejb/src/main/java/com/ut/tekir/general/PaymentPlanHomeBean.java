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

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.FlushModeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.core.Conversation;
import org.jboss.seam.international.StatusMessage.Severity;

import com.ut.tekir.entities.PaymentPlan;
import com.ut.tekir.entities.PaymentPlanItem;
import com.ut.tekir.framework.BaseConsts;
import com.ut.tekir.framework.EntityHome;

/**
 *
 * @author selman
 */
@Stateful
@Name("paymentPlanHome")
@Scope(value=ScopeType.CONVERSATION)
public class PaymentPlanHomeBean extends EntityHome<PaymentPlan> implements PaymentPlanHome<PaymentPlan>{
    
    @In
    EntityManager entityManager;

    @DataModel("paymentPlanList")
    private List<PaymentPlan> entityList;
    
    @SuppressWarnings("unchecked")
	@Factory("paymentPlanList")
    public void initPaymentPlanList() {
        log.debug("#{messages['logMessages.PreparingEntityList']}");
        
        setEntityList(entityManager.createQuery("select pp from PaymentPlan pp")
        							    .getResultList());
    }
    
    @Create
	@Begin(join=true,flushMode= FlushModeType.MANUAL)
    public void init() {
    }
    
    @Out(required=false)
    public PaymentPlan getPaymentPlan() {
        return getEntity();
    }

    @In(required=false)
    public void setPaymentPlan(PaymentPlan paymentPlan) {
        setEntity( paymentPlan );
    }
    
    @Override
    @Transactional
    public String save() {
		log.info("Saving payment plan. code :{0}", entity.getCode());
		String result = BaseConsts.FAIL;
    	
        try {

        	entity.convertPaymentDaysToString();

        	entity.convertPaymentWeeksToString();
        	
            result = super.save();

        } catch (Exception e) {
        	facesMessages.add(Severity.ERROR,"Kaydederken hata meydana geldi. Sebebi :{0}",e.getMessage());
        	log.error("Hata :{0}", e);
            return BaseConsts.FAIL;
        }
        return result;
    }

    @Override
    public void createNew() {
        log.debug("#{messages['logMessages.CreatedEntity']}");
        entity = new PaymentPlan();
        entity.setActive(true);

        entity.convertPaymentStringToDays();
    }

	public void createNewLine() {
        manualFlush();
        if (entity == null) {
            return;
        }
        
        PaymentPlanItem item = new PaymentPlanItem();
        item.setPaymentPlan(entity);
        
        entity.getItems().add(item);
        log.debug("#{messages['logMessages.NewItemAdded']}");
    }

    public void deleteLine(Integer ix) {
        manualFlush();
        if (entity == null) {
            return;
        }

        entity.getItems().remove(ix.intValue());
        log.debug("#{messages['logMessages.ItemDeleted']} : {0}", ix.intValue());

    }
    
    @Override
    public List<PaymentPlan> getEntityList() {
        return entityList;
    }

    public void setEntityList(List<PaymentPlan> entityList) {
        this.entityList = entityList;
    }
    
    public void manualFlush() {
        Conversation.instance().changeFlushMode(org.jboss.seam.annotations.FlushModeType.MANUAL);
    }
    
    @Remove 
    @Destroy
    public void destroy() {
    }

	@Override
	public void edit(PaymentPlan e) {
		super.edit(e);

		entity.convertPaymentStringToDays();

		entity.convertPaymentStringToWeeks();
	}

}
