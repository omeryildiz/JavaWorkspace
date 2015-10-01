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

import javax.ejb.Stateful;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.core.Conversation;
import org.jboss.seam.international.StatusMessage.Severity;

import com.ut.tekir.entities.PaymentContract;
import com.ut.tekir.entities.PaymentContractCommision;
import com.ut.tekir.entities.PaymentContractPeriod;
import com.ut.tekir.framework.BaseConsts;
import com.ut.tekir.framework.EntityBase;

/**
 *
 * @author sinan.yumak
 */
@Stateful
@Name("paymentContractHome")
@Scope(value = ScopeType.CONVERSATION)
public class PaymentContractHomeBean extends EntityBase<PaymentContract> implements PaymentContractHome<PaymentContract> {

    @Create 
    @Begin(join=true,flushMode = org.jboss.seam.annotations.FlushModeType.MANUAL)
    public void init() {
    }
  
    @Out(required = false)
    public PaymentContract getPaymentContract() {
        return getEntity();
    }

    @In(required = false)
    public void setPaymentContract(PaymentContract paymentContract) {
        setEntity(paymentContract);
    }

    @Override
    public void createNew() {
        log.debug("New payment contract...");

        entity = new PaymentContract();
        entity.setActive(true);
    }

    @Override
    @Transactional
    public String save() {
		log.info("Saving payment contract. Payment contract code :{0}", entity.getCode());
		String result = BaseConsts.FAIL;
    	
        try {
        	makeEntityValidations();
        	
        	convertPaymentDaysToString();
        	
            result = super.save();

        } catch (Exception e) {
        	facesMessages.add(Severity.ERROR,"Kaydederken hata meydana geldi. Sebebi :{0}",e.getMessage());
        	log.error("Hata :{0}", e);
            return BaseConsts.FAIL;
        }
        return result;
    }

    private void convertPaymentStringToDays() {
    	String[] dayList= null;
    	for(PaymentContractPeriod pcp : entity.getPeriodList())  {
    		dayList = pcp.getPaymentDay().split(";");
    		
    		pcp.setFirstDay(false);
    		pcp.setSecondDay(false);
    		pcp.setThirdDay(false);
    		pcp.setFourthDay(false);
    		pcp.setFifthDay(false);
    		pcp.setSixthDay(false);
    		pcp.setSeventhDay(false);

    		for (String day : dayList) {

    			if (day.equals("Mon")) {
    				pcp.setFirstDay(true);
    			} 
    			if (day.equals("Tue")){
    				pcp.setSecondDay(true);
    			}
    			if (day.equals("Wed")){
    				pcp.setThirdDay(true);
    			}
    			if (day.equals("Thu")){
    				pcp.setFourthDay(true);
    			}
    			if (day.equals("Fri")){
    				pcp.setFifthDay(true);
    			}
    			if (day.equals("Sat")){
    				pcp.setSixthDay(true);
    			}
    			if (day.equals("Sun")){
    				pcp.setSeventhDay(true);
    			}
    		}
    	}
    }

    private void convertPaymentDaysToString() {
    	StringBuilder dayList = null;
    	for(PaymentContractPeriod pcp : entity.getPeriodList())  {
    		dayList = new StringBuilder();
    		if (pcp.isFirstDay()) {
    			dayList.append("Mon;");
    		}
    		if (pcp.isSecondDay()) {
    			dayList.append("Tue;");
    		}
    		if (pcp.isThirdDay()) {
    			dayList.append("Wed;");
    		}
    		if (pcp.isFourthDay()) {
    			dayList.append("Thu;");
    		}
    		if (pcp.isFifthDay()) {
    			dayList.append("Fri;");
    		}
    		if (pcp.isSixthDay()) {
    			dayList.append("Sat;");
    		}
    		if (pcp.isSeventhDay()) {
    			dayList.append("Sun;");
    		}
    		pcp.setPaymentDay(dayList.toString());
    	}
    }

	private void makeEntityValidations() throws Exception {
		if (entity.getPeriodList().size() == 0 ) {
			throw new Exception("#{messages['beanMessages.AtLeastOneItemRequired']}");
		}
	}
	
    public void createNewLine() {
        manualFlush();
        if (entity == null) {
            return;
        }

        log.debug("EntityManager.FlushMode : #0", entityManager.getFlushMode());

        PaymentContractPeriod item = new PaymentContractPeriod();
        item.setOwner(entity);
        
        entity.getPeriodList().add(item);
        log.debug("yeni item eklendi");
        convertPaymentStringToDays();
    }

    public void createNewCommisionLine(Integer rowKey) {
    	manualFlush();
    	if (entity == null) {
    		return;
    	}
    	PaymentContractPeriod selectedPeriod = entity.getPeriodList().get(rowKey);
    	
    	log.debug("EntityManager.FlushMode : #0", entityManager.getFlushMode());
    	
    	PaymentContractCommision item = new PaymentContractCommision();
    	item.setOwner(selectedPeriod);
    	
    	selectedPeriod.getCommisionList().add(item);
    	log.debug("yeni komisyon satırı eklendi.");
    }

    public void deleteLine(Integer ix) {
        manualFlush();
        if (entity == null) {
            return;
        }
        log.debug("Kayıt Silinecek IX : {0}", ix);
        entity.getPeriodList().remove(ix.intValue());
    }

    public void deleteCommisionLine(Integer ix) {
    	manualFlush();
    	if (entity == null) {
    		return;
    	}
    	PaymentContractPeriod selectedPeriod = entity.getPeriodList().get(ix);

    	log.debug("Kayıt Silinecek IX : {0}", ix);
    	selectedPeriod.getCommisionList().remove(ix.intValue());
    }

    public void manualFlush() {
        Conversation.instance().changeFlushMode(org.jboss.seam.annotations.FlushModeType.MANUAL);
    }

	@Override
	public void setId(Long id) {
        if (entity != null) {
            return;
        }

		super.setId(id);

		convertPaymentStringToDays();
	}

}
