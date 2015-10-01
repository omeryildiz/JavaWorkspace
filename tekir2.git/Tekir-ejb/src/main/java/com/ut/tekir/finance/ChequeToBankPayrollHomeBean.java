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

import com.ut.tekir.entities.Cheque;
import com.ut.tekir.entities.ChequeStatus;
import com.ut.tekir.entities.ChequeToBankPayroll;
import com.ut.tekir.entities.ChequeToBankPayrollDetail;
import com.ut.tekir.entities.DocumentType;
import com.ut.tekir.framework.BaseConsts;
import com.ut.tekir.framework.CalendarManager;
import com.ut.tekir.framework.EntityBase;
import com.ut.tekir.framework.JasperHandlerBean;
import com.ut.tekir.framework.SequenceManager;
import com.ut.tekir.framework.SequenceType;

/**
 * Çek banka tahsilata gönderme bordrosu home bileşenidir.
 * 
 * Açıklama:
 *     Müşteriden alınan çeklerin topluca bankaya tahsilata gönderilebileceği bir bordrodur.
 *     Çek tarihçesi tablosunda hareket ile ilgili kayıt atılır.
 * 
 * @author yigit
 */
@Stateful
@Name("chequeToBankPayrollHome")
@Scope(value = ScopeType.CONVERSATION)
public class ChequeToBankPayrollHomeBean extends EntityBase<ChequeToBankPayroll> implements	ChequeToBankPayrollHome<ChequeToBankPayroll> {

	@In(create = true)
    SequenceManager sequenceManager;
    @In
    CalendarManager calendarManager;
    @In
    ChequeSuggestion chequeSuggestion;
    @In
    ChequeAction chequeAction;
    @In
    ChequeChangeLastStatus chequeChangeLastStatus;
    @In( create=true )
    private JasperHandlerBean jasperReport;

    private Boolean isEditable = Boolean.TRUE;
    
    @Create 
    @Begin(join=true,flushMode = org.jboss.seam.annotations.FlushModeType.MANUAL)
    public void init() {

    }
    
    @Out(required = false)
	public ChequeToBankPayroll getChequeToBankPayroll(){
		return getEntity();
	}
	
	@In(required = false)
	public void setChequeToBankPayroll(ChequeToBankPayroll chequeToBankPayroll){
		setEntity(chequeToBankPayroll);
	}
	
	@Override
    public void createNew() {
        log.debug("Yeni ChequeToBankPayroll");

        entity = new ChequeToBankPayroll();
        entity.setSerial(sequenceManager.getNewSerialNumber(SequenceType.SERIAL_CHEQUE_TO_BANK_PAYROLL));
        entity.setActive(true);
        entity.setDate(calendarManager.getCurrentDate());

        log.info("entity.setActive : {0}", entity.getActive());
        log.info("entity.setDate : {0}", entity.getDate());
        isEditable = Boolean.TRUE;
    }
	
	public void clearBankAccount(){
		entity.setBankBranch(null);
		entity.setBankAccount(null);
    }
	
	public String save() {
		isEditable = true;
		String res = null;

		try {
			if(entity.getDetails().size() <= 0){
				facesMessages.add("#{messages['chequePromissory.label.DetailListEmpty']}");
				return BaseConsts.FAIL;	
			}
	        /* Giriste zorunlu olmadigi icin banka-sube bos kalmasin */
	        entity.setBank(entity.getBankAccount().getBankBranch().getBank());
	        entity.setBankBranch(entity.getBankAccount().getBankBranch());

			res = super.save();

			if(res.equals(BaseConsts.SUCCESS)){
				
				for(ChequeToBankPayrollDetail cpd : entity.getDetails()){
					Cheque cheque = cpd.getCheque();
					if (! chequeAction.alreadySaved(cheque, DocumentType.ChequeCollectionPayroll, entity.getId())) {
						
	                    ChequeParamModel cpm = new ChequeParamModel();
	                    cpm.setCheque(cheque);
	                    cpm.setDocumentId(entity.getId());
	                    cpm.setDocumentSerial(entity.getSerial());
	                    cpm.setNewStatus(ChequeStatus.BankaTahsilatta);
	                    cpm.setDocumentType(DocumentType.ChequeCollectionPayroll);
	                    cpm.setToBankAccount(entity.getBankAccount());
	                    cpm.setStatusDate(entity.getDate());
	                    chequeAction.changePosition(cpm);
						
						entityManager.merge(cheque);
						
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
	
	public void controllItem(){
    	
    	if(getEntity() == null){
    		return;
    	}
    	
		try {
    		
			if(entity.getId() != null){
        		
				if( entity.getDetails() != null && entity.getDetails().size() > 0){
        			
					for(ChequeToBankPayrollDetail items : getEntity().getDetails()) {
						
						if(items.getId() == null){
							return;
						}
						
						Cheque cheque = items.getCheque();
						if(cheque.getLastStatus() != ChequeStatus.BankaTahsilatta){
						
							isEditable = false;
							break;
						}
					}
				}
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
    	
    	Cheque cheque= entity.getDetails().get(i).getCheque();
    	try {
			
    		cheque.getHistory().remove(cheque.getHistory().size()-1);

    		if(cheque.getClientCheque().equals(Boolean.TRUE)){
    			chequeChangeLastStatus.setNewStatusOfCheque(cheque);
    			entityManager.merge(cheque);
			} else {
    			if(cheque.getHistory().size() == 0){
    				entityManager.remove(cheque);	
    			} else {
    				chequeChangeLastStatus.setNewStatusOfCheque(cheque);
    				entityManager.merge(cheque);
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
        ChequeToBankPayrollDetail detail = new ChequeToBankPayrollDetail();
        detail.setOwner(entity);
        entity.getDetails().add(detail);
        log.debug("yeni detay eklendi");
    }
    
    public void deleteLine(ChequeToBankPayrollDetail detail) {
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
    
    public void popupChequeSelect(int clientOrFirm) {
		chequeSuggestion.setCallerObserveString("chequeToBankPayrollHome:popupNotice:client.cheque");
		chequeSuggestion.setClientCheque(Boolean.TRUE);
		chequeSuggestion.setTargetStatus(ChequeStatus.BankaTahsilatta);
		
		log.debug("Client cheque selected.");
    }
 
	 @Observer("chequeToBankPayrollHome:popupNotice:client.cheque")
	 public void selectClientCheque(Cheque cheque){
	     
	 	manualFlush();
	     
	 	if (entity == null) {
	         return;
	     }
	 	
	 	ChequeToBankPayrollDetail cpd = new ChequeToBankPayrollDetail();
	 	cpd.setOwner(entity);
	    cpd.setCheque(cheque);
	    cpd.getCheque().setMoney(cheque.getMoney());
	    cpd.getCheque().getMoney().setCurrency(cheque.getMoney().getCurrency());
	    cpd.getCheque().getMoney().setValue(cheque.getMoney().getValue());
	    cpd.getCheque().setInfo(cheque.getInfo());
	    cpd.getCheque().setReferenceNo(cheque.getReferenceNo());
	    cpd.getCheque().getMoney().setCurrency(cheque.getMoney().getCurrency());
	    
	    if(cpd.getCheque().getId() != null && entity.getDetails().contains(cpd)){
	 		facesMessages.add("Çek zaten mevcut");
	 		return ;
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
	public void setEntity(ChequeToBankPayroll entity) {
		super.setEntity(entity);
		controllItem();
	}

    public void pdf(ChequeToBankPayroll payroll){
        Map<Object, Object> params = new HashMap<Object, Object>();

        params.put("pBordroId", payroll.getId());
        try{
            jasperReport.reportToPDF("cekBankayaGonderildiBordrosu", "cekBankayaGonderildiBordrosu", params);
        }catch(JRException ex){
            log.error("Rapor Hatası", ex);
            facesMessages.add("Rapor Çalıştırılamadı");
        }
    }

}
