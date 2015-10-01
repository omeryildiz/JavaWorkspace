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

import com.ut.tekir.entities.Cheque;
import com.ut.tekir.entities.ChequeStatus;
import com.ut.tekir.entities.ChequeToAccountPaymentPayroll;
import com.ut.tekir.entities.ChequeToAccountPaymentPayrollDetail;
import com.ut.tekir.entities.DocumentType;
import com.ut.tekir.framework.BaseConsts;
import com.ut.tekir.framework.CalendarManager;
import com.ut.tekir.framework.CurrencyManager;
import com.ut.tekir.framework.EntityBase;
import com.ut.tekir.framework.SequenceManager;
import com.ut.tekir.framework.SequenceType;

/**
 * Kasadan çek ödeme bordrosu home bileşenidir.
 * 
 * Açıklama:
 *     Müşteriden alınan çeklerin topluca girilebileceği bordrodur.
 *     Alınan çekler kasa ödeme statüsüne girerler.
 *     Kasa hareket tablosuna kasa için alacak yazar.
 *     Çek tarihçesi tablosunda hareket ile ilgili kayıt atılır.
 * 
 * @author sinan.yumak
 */
@Stateful
@Name("chequeToAccountPaymentPayrollHome")
@Scope(value = ScopeType.CONVERSATION)
public class ChequeToAccountPaymentPayrollHomeBean extends EntityBase<ChequeToAccountPaymentPayroll> implements ChequeToAccountPaymentPayrollHome<ChequeToAccountPaymentPayroll> {

	@In(create = true)
    SequenceManager sequenceManager;
    @In
    CalendarManager calendarManager;
    @In
    AccountTxnAction accountTxnAction;
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

    private Boolean isEditable = Boolean.TRUE;
    
    @Create 
    @Begin(join=true,flushMode = org.jboss.seam.annotations.FlushModeType.MANUAL)
    public void init() {

    }
    
    @Out(required = false)
	public ChequeToAccountPaymentPayroll getChequeToAccountPaymentPayroll(){
		return getEntity();
	}
	
	@In(required = false)
	public void setChequeToAccountPaymentPayroll(ChequeToAccountPaymentPayroll chequeToAccountPaymentPayroll){
		setEntity(chequeToAccountPaymentPayroll);
	}
	
	@Override
    public void createNew() {
        log.debug("Yeni ChequeToAccountPaymentPayroll");

        entity = new ChequeToAccountPaymentPayroll();
        entity.setSerial(sequenceManager.getNewSerialNumber(SequenceType.SERIAL_CHEQUE_TO_ACCOUNT_PAYMENT_PAYROLL));
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
				facesMessages.add("Çek Bilgisi Girilmedi!");
				return BaseConsts.FAIL;	
			}

			res = super.save();

			if(res.equals(BaseConsts.SUCCESS)){
				
				for(ChequeToAccountPaymentPayrollDetail cpd : entity.getDetails()){
					
					Cheque cheque = cpd.getCheque();

					currencyManager.setLocalAmountOf(cheque.getMoney(), entity.getDate());
	    			
					if (! chequeAction.alreadySaved(cheque, DocumentType.ChequeAccountPaymentPayroll, entity.getId())) {

						ChequeParamModel cpm = new ChequeParamModel();
						cpm.setCheque(cheque);
						cpm.setToAccount(entity.getAccount());
						cpm.setDocumentId(entity.getId());
	                                        cpm.setDocumentSerial(entity.getSerial());
						cpm.setNewStatus(ChequeStatus.KasaOdeme);
						cpm.setDocumentType(DocumentType.ChequeAccountPaymentPayroll);
	                                        cpm.setStatusDate(entity.getDate());
						chequeAction.changePosition(cpm);
						
						entityManager.merge(cheque);
					}

				}
				accountTxnAction.saveChequeToAccountPaymentPayroll(entity);

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

    public void createNewLine() {
        manualFlush();
        if (entity == null) {
            return;
        }
        ChequeToAccountPaymentPayrollDetail detail = new ChequeToAccountPaymentPayrollDetail();
        detail.setOwner(entity);
        entity.getDetails().add(detail);
        log.debug("yeni detay eklendi");
    }
    
    public void deleteLine(ChequeToAccountPaymentPayrollDetail detail) {
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
    	manualFlush();
    	
		chequeSuggestion.setCallerObserveString("ChequeToAccountPaymentPayrollHomeBean:popupNotice:client.cheque");
		chequeSuggestion.setClientCheque(Boolean.FALSE);
		chequeSuggestion.setTargetStatus(ChequeStatus.KasaOdeme);
		
    }
  
	@Observer("ChequeToAccountPaymentPayrollHomeBean:popupNotice:client.cheque")
	public void selectClientCheque(Cheque cheque) {
		manualFlush();

		if (entity == null) {
			return;
		}
	 	
	 	ChequeToAccountPaymentPayrollDetail cpd = new ChequeToAccountPaymentPayrollDetail();
	 	cpd.setOwner(entity);
	    cpd.setCheque(cheque);
	    cpd.getCheque().setMoney(cheque.getMoney());
	    cpd.getCheque().getMoney().setCurrency(cheque.getMoney().getCurrency());
	    cpd.getCheque().getMoney().setValue(cheque.getMoney().getValue());
	    cpd.getCheque().setInfo(cheque.getInfo());
	    cpd.getCheque().setReferenceNo(cheque.getReferenceNo());
    
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
    
    @Override
    @Transactional
    public String delete() {
        accountTxnAction.deleteChequeToAccountPaymentPayroll(entity);

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

    public void controllItem(){
    	
    	if(entity == null){
    		return;
    	}
    	
		try {
    		
			if(entity.getId() != null){
        		
				if( entity.getDetails() != null && entity.getDetails().size() > 0){
        			
					for(ChequeToAccountPaymentPayrollDetail items : getEntity().getDetails()) {

						if(items.getId() == null){
							return;
						}
						Cheque cheque = items.getCheque();
						if(cheque.getLastStatus() != ChequeStatus.KasaOdeme){
						
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
    
	public Boolean getIsEditable() {
		return isEditable;
	}

	public void setIsEditable(Boolean isEditable) {
		this.isEditable = isEditable;
	}

	@Override
	public void setEntity(ChequeToAccountPaymentPayroll entity) {
		super.setEntity(entity);
		controllItem();
	}
}
