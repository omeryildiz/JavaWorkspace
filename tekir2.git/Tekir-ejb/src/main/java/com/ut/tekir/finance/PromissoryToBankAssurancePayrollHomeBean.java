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

import javax.ejb.Stateful;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.core.Conversation;
import org.jboss.seam.faces.FacesMessages;

import com.ut.tekir.entities.ChequeStatus;
import com.ut.tekir.entities.DocumentType;
import com.ut.tekir.entities.PromissoryNote;
import com.ut.tekir.entities.PromissoryToBankAssurancePayroll;
import com.ut.tekir.entities.PromissoryToBankAssurancePayrollDetail;
import com.ut.tekir.framework.BaseConsts;
import com.ut.tekir.framework.CalendarManager;
import com.ut.tekir.framework.EntityBase;
import com.ut.tekir.framework.SequenceManager;
import com.ut.tekir.framework.SequenceType;

/**
 * Bankaya senet teminat bordrosu browse bileşenidir.
 * 
 * Açıklama:
 *     Portföydeki senetlerin(firma senetlerinin, cirolanan) topluca girilebileceği bordrodur.
 *     Senet tarihçesi tablosunda hareket ile ilgili kayıt atılır.
 *     
 * @author sinan.yumak
 */
@Stateful
@Name("promissoryToBankAssurancePayrollHome")
@Scope(value = ScopeType.CONVERSATION)
public class PromissoryToBankAssurancePayrollHomeBean extends EntityBase<PromissoryToBankAssurancePayroll> implements PromissoryToBankAssurancePayrollHome<PromissoryToBankAssurancePayroll> {

	@In(create = true)
    SequenceManager sequenceManager;
    @In
    CalendarManager calendarManager;
    @In
    PromissorySuggestion promissorySuggestion;
    @In
    PromissoryAction promissoryAction;
    @In
    PromissoryPopupHome promissoryPopupHome;
    @In
    PromissoryChangeLastStatus promissoryChangeLastStatus;
    
    private Boolean isEditable = Boolean.TRUE;
	
    @Create 
    @Begin(join=true,flushMode = org.jboss.seam.annotations.FlushModeType.MANUAL)
    public void init() {
    }
    
    @Out(required = false)
	public PromissoryToBankAssurancePayroll getPromissoryToBankAssurancePayroll(){
		return getEntity();
	}

	@In(required = false)
	public void setPromissoryToBankAssurancePayroll(PromissoryToBankAssurancePayroll promissoryToBankAssurancePayroll){
		setEntity(promissoryToBankAssurancePayroll);
	}

	@Override
    public void createNew() {
        log.debug("Yeni PromissoryToBankAssurancePayroll");

        entity = new PromissoryToBankAssurancePayroll();
        entity.setSerial(sequenceManager.getNewSerialNumber(SequenceType.SERIAL_PROMISSORY_TO_BANK_ASSURANCE_PAYROLL));
        entity.setActive(true);
        entity.setDate(calendarManager.getCurrentDate());

        log.info("entity.setActive : {0}", entity.getActive());
        log.info("entity.setDate : {0}", entity.getDate());
    }
	
	public void clearBankAccount(){
		entity.setBankBranch(null);
		entity.setBankAccount(null);
    }
	
	@Transactional
	public String save() {
		manualFlush();
		String res = null;

		try {
			if(entity.getDetails().size() <= 0){
				facesMessages.add("#{messages['chequePromissory.label.DetailListEmpty']}");
				return BaseConsts.FAIL;	
			}

			res = super.save();

			if(res.equals(BaseConsts.SUCCESS)){
				
				for(PromissoryToBankAssurancePayrollDetail cpd : entity.getDetails()){
					PromissoryNote promissory = cpd.getPromissory();
					if (! promissoryAction.alreadySaved(promissory, DocumentType.PromissoryToBankAssurancePayroll, entity.getId())) {
	
						PromissoryParamModel cpm = new PromissoryParamModel();
						cpm.setPromissory(promissory);
						cpm.setDocumentId(entity.getId());
						cpm.setNewStatus(ChequeStatus.BankaTeminat);
						cpm.setDocumentType(DocumentType.PromissoryToBankAssurancePayroll);
						promissoryAction.changePosition(cpm);
						
	    				entityManager.merge(promissory);
					}
					
				}
				entityManager.flush();
			}

		} catch (Exception e) {
			FacesMessages.afterPhase();
			facesMessages.clear();
			facesMessages.add(e.getMessage());
			log.error("Hata :", e);
			res = BaseConsts.FAIL;
		}
        return res;
    }
	
	public void controllItem() {
		if (getEntity() == null)
			return;

		try {
			if (getEntity().getId() != null) {
				if (getEntity().getDetails() != null
						&& getEntity().getDetails().size() != 0) {
					for (PromissoryToBankAssurancePayrollDetail items : getEntity().getDetails()) {

						if(items.getId() == null){
							return;
						}
							PromissoryNote promissoryNote=  items.getPromissory();

							if (promissoryNote.getLastStatus() != ChequeStatus.BankaTeminat) {

								isEditable = false;
								break;
							}
							
						}					
					}
				} else {
					isEditable = true;
				}

		} catch (Exception e) {
			log.error("Hata :", e);
		}
	}
	
	@Override
	@Transactional
    public String delete() {

		for(int i=0; i < entity.getDetails().size(); i++){
    		
			deleteItem(i);
    	}

        return super.delete();
    }
	
	public void deleteItem(int i){
    	
    	PromissoryNote promissoryNote= entity.getDetails().get(i).getPromissory();
    	try {
			
    		promissoryNote.getHistory().remove(promissoryNote.getHistory().size()-1);

    		if(promissoryNote.getClientPromissoryNote().equals(Boolean.TRUE)){
    			promissoryChangeLastStatus.setNewStatusOfPromissory(promissoryNote);
    			entityManager.merge(promissoryNote);
			} else {
    			if(promissoryNote.getHistory().size() == 0){
    				entityManager.remove(promissoryNote);	
    			} else {
    				promissoryChangeLastStatus.setNewStatusOfPromissory(promissoryNote);
    				entityManager.merge(promissoryNote);
    			}
			}
		} catch (Exception e) {
			// TODO: handle exception
			facesMessages.add("Silmede hata!");
		}
    }
  
    public void createNewLine() {
        manualFlush();
        if (entity == null) {
            return;
        }
        PromissoryToBankAssurancePayrollDetail detail = new PromissoryToBankAssurancePayrollDetail();
        detail.setOwner(entity);
        entity.getDetails().add(detail);
        log.debug("yeni detay eklendi");
    }

    public void deleteLine(PromissoryToBankAssurancePayrollDetail detail) {
        manualFlush();
        if (entity == null) {
            return;
        }
        log.debug("Kayıt Silinecek : {0}", detail);
        entity.getDetails().remove(detail);
    }

    public void deleteLine(Integer ix) {
        manualFlush();
        if (entity == null) {
            return;
        }
        
        if(entity.getId() != null){
			
			deleteItem(ix);
		}

        log.debug("Kayıt Silinecek IX : {0}", ix);
        log.debug("Toplam Kayıt : {0}", entity.getDetails().size());
        Object o = entity.getDetails().remove(ix.intValue());
        log.debug("Silinen : {0}", o);
        log.debug("Toplam Kayıt : {0}", entity.getDetails().size());
    }
    
    public void popupPromissorySelect(int clientOrFirm) {
    	manualFlush();
    	
    	promissorySuggestion.setCallerObserveString("promissoryToBankAssurancePayrollHome:popupNotice:client.promissory");
		promissorySuggestion.setClientPromissory(Boolean.TRUE);
		promissorySuggestion.setTargetStatus(ChequeStatus.BankaTeminat);
		
    }

    public void popupPromissoryCreate() {
    	manualFlush();
    	
		promissoryPopupHome.setCallerObserveString("promissoryToBankAssurancePayrollHome:popupNotice:client.promissory");
		promissoryPopupHome.createNew();

    }
    
    @Observer("promissoryToBankAssurancePayrollHome:popupNotice:client.promissory")
	public void selectClientPromissory(PromissoryNote promissory) {

		manualFlush();

		if (entity == null) {
			return;
		}

		PromissoryToBankAssurancePayrollDetail cpd = new PromissoryToBankAssurancePayrollDetail();
		cpd.setOwner(entity);
		cpd.setPromissory(promissory);
		cpd.getPromissory().setMoney(promissory.getMoney());
		cpd.getPromissory().getMoney().setCurrency(promissory.getMoney().getCurrency());
		cpd.getPromissory().getMoney().setValue(promissory.getMoney().getValue());
		cpd.getPromissory().setInfo(promissory.getInfo());
		cpd.getPromissory().setReferenceNo(promissory.getReferenceNo());
		cpd.getPromissory().getMoney().setCurrency(promissory.getMoney().getCurrency());
		
		if (cpd.getPromissory().getId() != null && entity.getDetails().contains(cpd)) {
			facesMessages.add("Senet zaten mevcut");
			return;
		}

		entity.getDetails().add(cpd);

		log.debug("yeni item eklendi");
    }
 
    public void manualFlush() {
        Conversation.instance().changeFlushMode(org.jboss.seam.annotations.FlushModeType.MANUAL);
    }
    
    public Boolean getIsEditable() {
    	return isEditable;
    }
    
    public void setIsEditable(Boolean isEditable) {
    	this.isEditable = isEditable;
    }
    
    @Override
	public void setEntity(PromissoryToBankAssurancePayroll entity) {
		super.setEntity(entity);
		controllItem();
	}

}
