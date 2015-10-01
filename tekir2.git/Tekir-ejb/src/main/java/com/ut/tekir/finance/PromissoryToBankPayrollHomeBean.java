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

import java.util.HashMap;
import java.util.Map;

import javax.ejb.Stateful;

import net.sf.jasperreports.engine.JRException;

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
import com.ut.tekir.entities.PromissoryToBankPayroll;
import com.ut.tekir.entities.PromissoryToBankPayrollDetail;
import com.ut.tekir.framework.BaseConsts;
import com.ut.tekir.framework.CalendarManager;
import com.ut.tekir.framework.EntityBase;
import com.ut.tekir.framework.JasperHandlerBean;
import com.ut.tekir.framework.SequenceManager;
import com.ut.tekir.framework.SequenceType;

/**
 * Bankaya senet tahsilatı home bileşenidir.
 * 
 * Açıklama:
 *     Müşteriden alınan senetlerin topluca bankaya tahsilata gönderilebileceği bir bordrodur.
 *     Senet tarihçesi tablosunda hareket ile ilgili kayıt atılır.
 * 
 * @author yigit
 */
@Stateful
@Name("promissoryToBankPayrollHome")
@Scope(value = ScopeType.CONVERSATION)
public class PromissoryToBankPayrollHomeBean extends EntityBase<PromissoryToBankPayroll> implements	PromissoryToBankPayrollHome<PromissoryToBankPayroll> {

	@In(create = true)
    SequenceManager sequenceManager;
    @In
    CalendarManager calendarManager;
    @In
    PromissoryAction promissoryAction;
    @In
    PromissoryPopupHome promissoryPopupHome;
    @In
    PromissorySuggestion promissorySuggestion;
    @In
    PromissoryChangeLastStatus promissoryChangeLastStatus;
    @In( create=true )
    private JasperHandlerBean jasperReport;
    
    private Boolean isEditable = Boolean.TRUE;
    
    @Create 
    @Begin(join=true,flushMode = org.jboss.seam.annotations.FlushModeType.MANUAL)
    public void init() {

    }
    
    @Out(required = false)
	public PromissoryToBankPayroll getPromissoryToBankPayroll(){
		return getEntity();
	}
	
	@In(required = false)
	public void setPromissoryToBankPayroll(PromissoryToBankPayroll promissoryToBankPayroll){
		setEntity(promissoryToBankPayroll);
	}
	
	@Override
    public void createNew() {
        log.debug("Yeni PromissoryToBankPayroll");
    
        entity = new PromissoryToBankPayroll();
        entity.setSerial(sequenceManager.getNewSerialNumber(SequenceType.SERIAL_PROMISSORY_TO_BANK_PAYROLL));
        entity.setActive(true);
        entity.setDate(calendarManager.getCurrentDate());
    }
	
	public void clearBankAccount(){
		entity.setBankBranch(null);
		entity.setBankAccount(null);
    }
	
	@Override
	@Transactional
	public String save() {
		String res = null;

		try {
			if(entity.getDetails().size() <= 0){
				facesMessages.add("#{messages['chequePromissory.label.DetailListEmpty']}");
				return BaseConsts.FAIL;
			}

			res = super.save();

			if(res.equals(BaseConsts.SUCCESS)){
				
				for(PromissoryToBankPayrollDetail ppd : entity.getDetails()){
					PromissoryNote promissory = ppd.getPromissory();
					if (! promissoryAction.alreadySaved(promissory, DocumentType.PromissoryCollectionPayroll, entity.getId())) {

						PromissoryParamModel ppm = new PromissoryParamModel();
						ppm.setPromissory(ppd.getPromissory());
						ppm.setDocumentId(entity.getId());
						ppm.setNewStatus(ChequeStatus.BankaTahsilatta);
						ppm.setDocumentType(DocumentType.PromissoryCollectionPayroll);
						promissoryAction.changePosition(ppm);
						
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
					for (PromissoryToBankPayrollDetail items : getEntity().getDetails()) {

						if(items.getId() == null){
							return;
						}
							PromissoryNote promissoryNote=  items.getPromissory();

							if (promissoryNote.getLastStatus() != ChequeStatus.BankaTahsilatta) {

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
        PromissoryToBankPayrollDetail detail = new PromissoryToBankPayrollDetail();
        detail.setOwner(entity);
        entity.getDetails().add(detail);
        log.debug("yeni detay eklendi");
    }
    
    public void deleteLine(PromissoryToBankPayrollDetail detail) {
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
    	
		promissorySuggestion.setCallerObserveString("promissoryToBankPayrollHome:popupNotice:client.promissory");
		promissorySuggestion.setClientPromissory(Boolean.TRUE);
		promissorySuggestion.setTargetStatus(ChequeStatus.BankaTahsilatta);
		
		log.debug("Client promissory selected.");
    	
    }
 
	@Observer("promissoryToBankPayrollHome:popupNotice:client.promissory")
	public void selectClientPromissory(PromissoryNote promissory) {

		if (entity == null) {
			return;
		}

		PromissoryToBankPayrollDetail pdetail = new PromissoryToBankPayrollDetail();

		pdetail.setOwner(entity);
		pdetail.setPromissory(promissory);
		pdetail.getPromissory().setPromissorynoteOwner(promissory.getPromissorynoteOwner());
		pdetail.getPromissory().getMoney().setCurrency(promissory.getMoney().getCurrency());
		pdetail.getPromissory().setContact(promissory.getContact());
		pdetail.getPromissory().setReferenceNo(promissory.getReferenceNo());
		pdetail.getPromissory().setMoney(promissory.getMoney());
		pdetail.getPromissory().getMoney().setValue(promissory.getMoney().getValue());
		pdetail.getPromissory().setInfo(promissory.getInfo());
		pdetail.getPromissory().setLastStatus(promissory.getLastStatus());

		if(pdetail.getPromissory().getId() != null && entity.getDetails().contains(pdetail)){
	 		facesMessages.add("Senet zaten mevcut");
	 		return ;
	 	}
		entity.getDetails().add(pdetail);

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
	public void setEntity(PromissoryToBankPayroll entity) {
		super.setEntity(entity);
		controllItem();
	}

    public void pdf(PromissoryToBankPayroll payroll){
        Map<Object, Object> params = new HashMap<Object, Object>();

        params.put("pBordroId", payroll.getId());
        try{
            jasperReport.reportToPDF("senetBankayaGonderildiBordrosu", "senetBankayaGonderildiBordrosu", params);
        }catch(JRException ex){
            log.error("Rapor Hatası", ex);
            facesMessages.add("Rapor Çalıştırılamadı");
        }
    }

}
