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
import com.ut.tekir.entities.ChequeCollectedAtBankPayroll;
import com.ut.tekir.entities.ChequeCollectedAtBankPayrollDetail;
import com.ut.tekir.entities.ChequeStatus;
import com.ut.tekir.entities.ChequeToBankPayrollDetail;
import com.ut.tekir.entities.DocumentType;
import com.ut.tekir.framework.BaseConsts;
import com.ut.tekir.framework.CalendarManager;
import com.ut.tekir.framework.EntityBase;
import com.ut.tekir.framework.JasperHandlerBean;
import com.ut.tekir.framework.SequenceManager;
import com.ut.tekir.framework.SequenceType;

/**
 * Çek banka tahsil edildi bordrosu home bileşenidir.
 * 
 * Açıklama:
 *     Müşteriden alınan çeklerin topluca girilebileceği bordrodur.
 *     Alınan çekler banka tahsil edildi statüsünü alırlar.
 *     Banka hareket tablosuna banka hesabı için borç yazar.
 *     Çek tarihçesi tablosunda hareket ile ilgili kayıt atılır.
 * 
 * 
 * @author sinan.yumak
 */
@Stateful
@Name("chequeCollectedAtBankPayrollHome")
@Scope(value = ScopeType.CONVERSATION)
public class ChequeCollectionAtBankPayrollHomeBean extends EntityBase<ChequeCollectedAtBankPayroll> implements ChequeCollectionAtBankPayrollHome<ChequeCollectedAtBankPayroll>{

	@In(create = true)
    SequenceManager sequenceManager;
    @In
    CalendarManager calendarManager;
    @In
    ChequeSuggestion chequeSuggestion;
    @In
    ChequeAction chequeAction;
    @In
    ChequePopupHome chequePopupHome;
    @In
    BankTxnAction bankTxnAction;
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
	public ChequeCollectedAtBankPayroll getChequeCollectionAtBankPayroll(){
		return getEntity();
	}
	
	@In(required = false)
	public void setChequeCollectionAtBankPayroll(ChequeCollectedAtBankPayroll chequeCollectionAtBankPayroll){
		setEntity(chequeCollectionAtBankPayroll);
	}

	@Override
    public void createNew() {
        log.debug("Yeni ChequeCollectedAtBankPayroll");

        entity = new ChequeCollectedAtBankPayroll();
        entity.setSerial(sequenceManager.getNewSerialNumber(SequenceType.SERIAL_CHEQUE_COLLECTED_AT_BANK_PAYROLL));
        entity.setActive(true);
        entity.setDate(calendarManager.getCurrentDate());

        log.info("entity.setActive : {0}", entity.getActive());
        log.info("entity.setDate : {0}", entity.getDate());
    }

	@Override
	@Transactional
	public String save() {
		manualFlush();
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
	        log.info("Yeni Bordro Eklendi");
			if(res.equals(BaseConsts.SUCCESS)){
				
				for(ChequeCollectedAtBankPayrollDetail cpd : entity.getDetails()){
					
					Cheque cheque = cpd.getCheque();
	    			
					if (! chequeAction.alreadySaved(cheque, DocumentType.ChequeCollectedAtBankPayroll, entity.getId())) {

	                    ChequeParamModel cpm = new ChequeParamModel();
	                    cpm.setCheque(cheque);
	                    cpm.setDocumentId(entity.getId());
	                    cpm.setDocumentSerial(entity.getSerial());
	                    cpm.setNewStatus(ChequeStatus.BankaTahsilEdildi);
	                    cpm.setDocumentType(DocumentType.ChequeCollectedAtBankPayroll);
	                    cpm.setToBankAccount(entity.getBankAccount());
	                    cpm.setStatusDate(entity.getDate());
	                    chequeAction.changePosition(cpm);
	
	                    entityManager.merge(cheque);
					}

				}
	            bankTxnAction.saveChequeCollectedAtBankPayroll(entity);

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

    @Override
    @Transactional
    public String delete() {
    	bankTxnAction.deleteChequeCollectedAtBankPayroll(entity);

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
        ChequeCollectedAtBankPayrollDetail detail = new ChequeCollectedAtBankPayrollDetail();
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
	public void selectClientCheque(Cheque cheque) {
		manualFlush();

		if (entity == null) {
			return;
		}
	 	
	 	ChequeCollectedAtBankPayrollDetail cpd = new ChequeCollectedAtBankPayrollDetail();
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
 
    public void controllItem(){
    	
    	if(entity == null ){
    		return;
    	}
    	
		try {
    		
			if(entity.getId() != null){
        		
				if( entity.getDetails() != null && entity.getDetails().size() > 0){
        			
					for(ChequeCollectedAtBankPayrollDetail items : getEntity().getDetails()) {

						if(items.getId() == null){
							return;
						}
						Cheque cheque = items.getCheque();
						if(cheque.getLastStatus() != ChequeStatus.BankaTahsilEdildi){
						
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

    public void pdf(ChequeCollectedAtBankPayroll payroll){
        Map<Object, Object> params = new HashMap<Object, Object>();
        
        params.put("pBordroId", payroll.getId());
        try{
            jasperReport.reportToPDF("cekBankaTahsilEdildiBordrosu", "cekBankaTahsilEdildiBordrosu", params);
        }catch(JRException ex){
            log.error("Rapor Hatası", ex);
            facesMessages.add("Rapor Çalıştırılamadı");
        }
    }

    public void manualFlush() {
        Conversation.instance().changeFlushMode(org.jboss.seam.annotations.FlushModeType.MANUAL);
    }

	@Override
	public void setEntity(ChequeCollectedAtBankPayroll entity) {
		super.setEntity(entity);
		controllItem();
	}

	public Boolean getIsEditable() {
		return isEditable;
	}

	public void setIsEditable(Boolean isEditable) {
		this.isEditable = isEditable;
	}

}
