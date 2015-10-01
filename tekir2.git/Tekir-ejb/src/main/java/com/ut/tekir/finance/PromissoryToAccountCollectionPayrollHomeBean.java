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
import com.ut.tekir.entities.PromissoryToAccountCollectionPayroll;
import com.ut.tekir.entities.PromissoryToAccountCollectionPayrollDetail;
import com.ut.tekir.framework.BaseConsts;
import com.ut.tekir.framework.CalendarManager;
import com.ut.tekir.framework.EntityBase;
import com.ut.tekir.framework.SequenceManager;
import com.ut.tekir.framework.SequenceType;

/**
 * Kasaya senet tahsilatı home bileşenidir.
 * 
 * Açıklama:
 *     Müşteriden alınan senetlerin topluca girilebileceği bordrodur.
 *     Alınan senetler kasa tahsilat statüsüne girerler.
 *     Kasa hareket tablosuna kasa için borç yazar.
 *     Senet tarihçesi tablosunda hareket ile ilgili kayıt atılır.
 * 
 * @author bNebioglu
 */
@Stateful
@Name("promissoryToAccountCollectionPayrollHome")
@Scope(value = ScopeType.CONVERSATION)
public class PromissoryToAccountCollectionPayrollHomeBean extends EntityBase<PromissoryToAccountCollectionPayroll> implements PromissoryToAccountCollectionPayrollHome<PromissoryToAccountCollectionPayroll> {

	@In(create = true)
    SequenceManager sequenceManager;
    @In
    CalendarManager calendarManager;
    @In
    FinanceTxnAction financeTxnAction;
    @In
    PromissorySuggestion promissorySuggestion;
    @In
    PromissoryAction promissoryAction;
    @In
    PromissoryPopupHome promissoryPopupHome;
    @In
    AccountTxnAction accountTxnAction;
    @In
    PromissoryChangeLastStatus promissoryChangeLastStatus;
    
    private Boolean isEditable = Boolean.TRUE;
    
    @Create 
    @Begin(join=true,flushMode = org.jboss.seam.annotations.FlushModeType.MANUAL)
    public void init() {

    }
    
    @Out(required = false)
	public PromissoryToAccountCollectionPayroll getPromissoryToAccountCollectionPayroll(){
		return getEntity();
	}
	
	@In(required = false)
	public void setPromissoryToAccountCollectionPayroll(PromissoryToAccountCollectionPayroll PromissoryToAccountCollectionPayroll){
		setEntity(PromissoryToAccountCollectionPayroll);
	}
	
	@Override
    public void createNew() {
        log.debug("Yeni PromissoryToAccountCollectionPayroll");
        
        entity = new PromissoryToAccountCollectionPayroll();
        entity.setSerial(sequenceManager.getNewSerialNumber(SequenceType.SERIAL_PROMISSORY_TO_ACCOUNT_COLLECTION_PAYROLL));
        entity.setActive(true);
        entity.setDate(calendarManager.getCurrentDate());
        
        log.info("entity.setActive : {0}", entity.getActive());
        log.info("entity.setDate : {0}", entity.getDate());
    }
		
	@Override
	@Transactional
	public String save() {
		String res = null;
		
		try {
			if(entity.getDetails().size() <= 0){
				facesMessages.add("Senet Bilgisi Girilmedi!");
				return BaseConsts.FAIL;	
			}

			res = super.save();

			if(res.equals(BaseConsts.SUCCESS)){
				
				for(PromissoryToAccountCollectionPayrollDetail cpd : entity.getDetails()){
					PromissoryNote promissory = cpd.getPromissory();
					if (! promissoryAction.alreadySaved(promissory, DocumentType.PromissoryAccountCollectionPayroll, entity.getId())) {

						PromissoryParamModel cpm = new PromissoryParamModel();
						cpm.setPromissory(promissory);
						cpm.setToAccount(entity.getAccount());
						cpm.setDocumentId(entity.getId());
						cpm.setNewStatus(ChequeStatus.KasaTahsilat);
						cpm.setDocumentType(DocumentType.PromissoryAccountCollectionPayroll);
						promissoryAction.changePosition(cpm);
						
	    				entityManager.merge(promissory);
					}
					
				}
				accountTxnAction.savePromissoryToAccountCollectionPayroll(entity);

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
					for (PromissoryToAccountCollectionPayrollDetail items : getEntity().getDetails()) {


						if(items.getId() == null){
							return;
						}
							PromissoryNote promissoryNote=  items.getPromissory();

							if (promissoryNote.getLastStatus() != ChequeStatus.KasaTahsilat) {

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

    /* (non-Javadoc)
	 * @see com.ut.tekir.finance.PromissoryToAccountCollectionPayrollHome#createNewLine()
	 */
    public void createNewLine() {
        manualFlush();
        if (entity == null) {
            return;
        }
        PromissoryToAccountCollectionPayrollDetail detail = new PromissoryToAccountCollectionPayrollDetail();
        detail.setOwner(entity);
        entity.getDetails().add(detail);
        log.debug("yeni detay eklendi");
    }
    
    /* (non-Javadoc)
	 * @see com.ut.tekir.finance.PromissoryToAccountCollectionPayrollHome#deleteLine(com.ut.tekir.entities.PromissoryToAccountCollectionPayrollDetail)
	 */
    public void deleteLine(PromissoryToAccountCollectionPayrollDetail detail) {
        manualFlush();
        if (entity == null) {
            return;
        }
        log.debug("Kayıt Silinecek : {0}", detail);
        entity.getDetails().remove(detail);
    }

    /* (non-Javadoc)
	 * @see com.ut.tekir.finance.PromissoryToAccountCollectionPayrollHome#deleteLine(java.lang.Integer)
	 */
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
    
    /* (non-Javadoc)
	 * @see com.ut.tekir.finance.PromissoryToAccountCollectionPayrollHome#popupPromissorySelect(int)
	 */
    public void popupPromissorySelect(int clientOrFirm) {
		promissorySuggestion.setCallerObserveString("PromissoryToAccountCollectionPayrollHomeBean:popupNotice:client.promissory");
		promissorySuggestion.setClientPromissory(Boolean.TRUE);
		promissorySuggestion.setTargetStatus(ChequeStatus.KasaTahsilat);
		
    }
  
	/* (non-Javadoc)
	 * @see com.ut.tekir.finance.PromissoryToAccountCollectionPayrollHome#selectClientPromissory(com.ut.tekir.entities.PromissoryNote)
	 */
	@Observer("PromissoryToAccountCollectionPayrollHomeBean:popupNotice:client.promissory")
	public void selectClientPromissory(PromissoryNote promissory) {

		manualFlush();

		if (entity == null) {
			return;
		}
	 	
	 	PromissoryToAccountCollectionPayrollDetail cpd = new PromissoryToAccountCollectionPayrollDetail();
	 	cpd.setOwner(entity);
	    cpd.setPromissory(promissory);
	    cpd.getPromissory().setMoney(promissory.getMoney());
	    cpd.getPromissory().getMoney().setCurrency(promissory.getMoney().getCurrency());
	    cpd.getPromissory().getMoney().setValue(promissory.getMoney().getValue());
	    cpd.getPromissory().setInfo(promissory.getInfo());
	    cpd.getPromissory().setReferenceNo(promissory.getReferenceNo());
	    cpd.getPromissory().getMoney().setCurrency(promissory.getMoney().getCurrency());

	    if(cpd.getPromissory().getId() != null && entity.getDetails().contains(cpd)){
	 		facesMessages.add("Senet zaten mevcut");
	 		return ;
	 	}
	    
	    entity.getDetails().add(cpd);

		log.debug("yeni item eklendi");
	}
 
    /* (non-Javadoc)
	 * @see com.ut.tekir.finance.PromissoryToAccountCollectionPayrollHome#manualFlush()
	 */
    public void manualFlush() {
		Conversation.instance().changeFlushMode(org.jboss.seam.annotations.FlushModeType.MANUAL);
	}
    
    @Override
    @Transactional
    public String delete() {
        accountTxnAction.deletePromissoryToAccountCollectionPayroll(entity);
        
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

	public Boolean getIsEditable() {
		return isEditable;
	}

	public void setIsEditable(Boolean isEditable) {
		this.isEditable = isEditable;
	}
	
	@Override
	public void setEntity(PromissoryToAccountCollectionPayroll entity) {
		super.setEntity(entity);
		controllItem();
	}
}
