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
import com.ut.tekir.entities.ChequeToContactPayroll;
import com.ut.tekir.entities.ChequeToContactPayrollDetail;
import com.ut.tekir.entities.DocumentType;
import com.ut.tekir.framework.BaseConsts;
import com.ut.tekir.framework.CalendarManager;
import com.ut.tekir.framework.CurrencyManager;
import com.ut.tekir.framework.EntityBase;
import com.ut.tekir.framework.JasperHandlerBean;
import com.ut.tekir.framework.SequenceManager;
import com.ut.tekir.framework.SequenceType;

/**
 * Çek cariye çıkış bordrosu home bileşenidir.
 * 
 * Açıklama:
 *     Müşteriye verilen firma veya cirolanan çeklerin topluca girilebileceği bordrodur.
 *     Verilen çekler müşteri çeki ise  cirolanır, firma çeki ise çıkış statüsüne çekilir.
 *     Finance hareket tablosuna müşteri için borç yazar.
 *     Çek tarihçesi tablosunda hareket ile ilgili kayıt atılır.
 *     
 * @author yigit
 */
@Stateful
@Name("chequeToContactPayrollHome")
@Scope(value = ScopeType.CONVERSATION)
public class ChequeToContactPayrollHomeBean extends EntityBase<ChequeToContactPayroll> implements ChequeToContactPayrollHome<ChequeToContactPayroll> {

	@In(create = true)
    SequenceManager sequenceManager;
    @In
    CalendarManager calendarManager;
    @In
    FinanceTxnAction financeTxnAction;
    @In
    ChequeSuggestion chequeSuggestion;
    @In
    ChequeAction chequeAction;
    @In
    ChequePopupHome chequePopupHome;
    @In
    ChequeChangeLastStatus chequeChangeLastStatus;
    @In
    CurrencyManager currencyManager;
    @In( create=true )
    private JasperHandlerBean jasperReport;
    
    private Boolean isEditable = Boolean.TRUE;

    private Boolean isEditCheque = Boolean.FALSE;

    @Create
    @Begin(join=true,flushMode = org.jboss.seam.annotations.FlushModeType.MANUAL)
    public void init() {

    }
    
    @Out(required = false)
	public ChequeToContactPayroll getChequeToContactPayroll(){
		return getEntity();
	}
	
	@In(required = false)
	public void setChequeToContactPayroll(ChequeToContactPayroll chequeToContactPayroll){
		setEntity(chequeToContactPayroll);
	}

	@Override
    public void createNew() {
        log.debug("Yeni ChequeToContactPayroll");

        entity = new ChequeToContactPayroll();
        entity.setSerial(sequenceManager.getNewSerialNumber(SequenceType.SERIAL_CHEQUE_TO_CONTACT_PAYROLL));
        entity.setActive(true);
        entity.setDate(calendarManager.getCurrentDate());
        
        log.info("entity.setActive : {0}", entity.getActive());
        log.info("entity.setDate : {0}", entity.getDate());
        isEditable=Boolean.TRUE;
    }

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
				
				for(ChequeToContactPayrollDetail cpd : entity.getDetails()){
					Cheque cheque = cpd.getCheque();

					if (! chequeAction.alreadySaved(cheque, DocumentType.ChequePaymentPayroll, entity.getId())) {
					
                        cheque.setContact(entity.getContact());
                                        
						ChequeParamModel cpm = new ChequeParamModel();
						cpm.setCheque(cheque);
						cpm.setToContact(entity.getContact());
						if (cpd.getCheque().getClientCheque() == true) {
							cpm.setNewStatus(ChequeStatus.Ciro);
						} else {
							cpm.setNewStatus(ChequeStatus.Cikis);
						}
						cpm.setDocumentId(entity.getId());
	                                        cpm.setDocumentSerial(entity.getSerial());
						cpm.setDocumentType(DocumentType.ChequePaymentPayroll);
	                                        cpm.setStatusDate(entity.getDate());
                        	                                        	
	                                        
						chequeAction.changePosition(cpm);					
						currencyManager.setLocalAmountOf(cheque.getMoney(), entity.getDate());
						entityManager.merge(cheque);
						
					}
				}
	            financeTxnAction.saveChequeToContactPayroll(entity);

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
        			
					for(ChequeToContactPayrollDetail items : getEntity().getDetails()) {

						if(items.getId() == null){
							return;
						}
						Cheque cheque = items.getCheque();
						if(cheque.getClientCheque().equals(true) && cheque.getLastStatus() != ChequeStatus.Ciro
								|| cheque.getClientCheque().equals(false) && cheque.getLastStatus() != ChequeStatus.Cikis){
						
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

    	financeTxnAction.deleteChequeToContactPayroll(entity);
    	
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
			facesMessages.add("Silmede hata!");
		}
    }

    public void findCheque(int rowKey) {
        Cheque c = entity.getDetails().get(rowKey).getCheque();
        chequePopupHome.setIsClient(false);
        chequePopupHome.setCheque(c);
        chequePopupHome.setCallerObserveString("chequeToContactPayrollHome:popupNotice:client.cheque");
        
        isEditCheque=Boolean.TRUE;
    }
    
    public void createNewLine() {
        manualFlush();
        if (entity == null) {
            return;
        }
        ChequeToContactPayrollDetail detail = new ChequeToContactPayrollDetail();
        detail.setOwner(entity);
        entity.getDetails().add(detail);
        log.debug("yeni detay eklendi");
    }
    
    public void deleteLine(ChequeToContactPayrollDetail detail) {
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
    	
    	if (clientOrFirm == 0) {
    	
    		chequeSuggestion.setCallerObserveString("chequeToContactPayrollHome:popupNotice:client.cheque");
    		chequeSuggestion.setClientCheque(Boolean.TRUE);
    		chequeSuggestion.setTargetStatus(ChequeStatus.Ciro);
    		
    		log.debug("Client cheque selected.");
    	} else {
    		
    		chequeSuggestion.setCallerObserveString("chequeToContactPayrollHome:popupNotice:firm.cheque");
    		chequeSuggestion.setClientCheque(Boolean.FALSE);
    		chequeSuggestion.setTargetStatus(ChequeStatus.Cikis);
    		
    		if( chequeSuggestion.getChequeList()!= null && chequeSuggestion.getChequeList().size() != 0 ) {
    			
    			chequeSuggestion.getChequeList().clear();
    		}
 
    		
    		log.debug("Firm cheque selected.");
    	}
    	
	}
 
    public void popupChequeCreate() {
		chequePopupHome.setCallerObserveString("chequeToContactPayrollHome:popupNotice:client.cheque");
		chequePopupHome.createNew();
		chequePopupHome.getCheque().setContact(entity.getContact());
	}
    
    @Observer("chequeToContactPayrollHome:popupNotice:client.cheque")
	public void selectClientCheque(Cheque cheque) {
	 	manualFlush();
	     
	 	if (entity == null) {
	         return;
     	}

	    if(isEditCheque){
	        isEditCheque=Boolean.FALSE;
	        return;
	    }
 	
	    ChequeToContactPayrollDetail cpd = new ChequeToContactPayrollDetail();
	    cpd.setOwner(entity);
	    cpd.setCheque(cheque);
	    cpd.getCheque().setMoney(cheque.getMoney());
	    cpd.getCheque().getMoney().setCurrency(cheque.getMoney().getCurrency());
	    cpd.getCheque().getMoney().setValue(cheque.getMoney().getValue());
	    cpd.getCheque().setInfo(cheque.getInfo());
	    cpd.getCheque().setReferenceNo(cheque.getReferenceNo());
    
	    if( cpd.getCheque().getId() != null && entity.getDetails().contains(cpd)){
	 		facesMessages.add("�ek zaten mevcut");
	 		return ;
	 	}
    
	    entity.getDetails().add(cpd);
     
    	log.debug("yeni item eklendi");
	}
    
    public Boolean getIsEditable() {
		return isEditable;
	}

	public void setIsEditable(Boolean isEditable) {
		this.isEditable = isEditable;
	}

	@Override
	public void setEntity(ChequeToContactPayroll entity) {
		super.setEntity(entity);
		controllItem();
	}

    public Boolean getIsEditCheque() {
        return isEditCheque;
    }

    public void setIsEditCheque(Boolean isEditCheque) {
        this.isEditCheque = isEditCheque;
    }
    
    public void manualFlush() {
        Conversation.instance().changeFlushMode(org.jboss.seam.annotations.FlushModeType.MANUAL);
    }

    public void pdf(ChequeToContactPayroll payroll){
        Map<Object, Object> params = new HashMap<Object, Object>();
        
        params.put("pBordroId", payroll.getId());
        try{
            jasperReport.reportToPDF("cekCikisBordrosu", "CekCikisBordrosu", params);
        }catch(JRException ex){
            log.error("Rapor Hatası", ex);
            facesMessages.add("Rapor Çalıştırılamadı");
        }
    }

}
