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
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;

import com.ut.tekir.entities.Cheque;
import com.ut.tekir.entities.ChequeStatus;
import com.ut.tekir.entities.DocumentType;
import com.ut.tekir.entities.FinanceAction;
import com.ut.tekir.entities.Payment;
import com.ut.tekir.entities.PaymentItem;
import com.ut.tekir.entities.PaymentItemCheque;
import com.ut.tekir.entities.PaymentItemPromissoryNote;
import com.ut.tekir.entities.PaymentType;
import com.ut.tekir.entities.PromissoryNote;
import com.ut.tekir.framework.BaseConsts;
import com.ut.tekir.framework.CurrencyManager;
import com.ut.tekir.framework.SequenceType;

/**
 *
 * @author haky , volkan
 */
@Stateful
@Name("paymentHome")
@Scope(value = ScopeType.CONVERSATION)
public class PaymentHomeBean extends PaymentHomeBeanBase<Payment> implements PaymentHome<Payment> {

	@In
	CurrencyManager currencyManager;
    @In
    ChequeChangeLastStatus chequeChangeLastStatus;
    @In
    PromissoryChangeLastStatus promissoryChangeLastStatus;
	
	@Override
	@Out(required=false)
	public Payment getPayment() {
		return getEntity();
	}

	@Override
	@In(required=false)
	public void setPayment(Payment payment) {
		setEntity(payment);
	}

	@Override
	public void createNew() {
		super.createNew();
        entity.setAction(FinanceAction.Debit);
        entity.setSerial(sequenceManager.getNewSerialNumber(SequenceType.SERIAL_FUND_PAYMENT));
	}
	
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public String save() {
		//FIXME:bu metottaki kodlar çek-senet modülü elden geçirildiğinde düzenlenecekler.
		String res = BaseConsts.FAIL;
		manualFlush();
		recalculate();

		try {
			makeValidations();

			res = super.save();

			if(res.equals(BaseConsts.SUCCESS)){

				saveMatches();

	        	for(PaymentItem pItem : entity.getItems()) {
	
	        		if(pItem instanceof PaymentItemCheque){
	        			
	        			Cheque pic = ((PaymentItemCheque) pItem).getCheque();
		        		ChequeStatus chequeStatus;
		        		ChequeParamModel cpm;

		        		if( pic != null ) {

                            //siparis fisi ile olusan kapora odemesi
                            if ( entity.getOrderNote() != null && entity.getOrderNote().getId() != null) {
                                pic.setTradein(Boolean.TRUE);
                            }
		        			
                            pic.setContact(entity.getContact());
	
		        			if (!chequeAction.alreadySaved(pic, DocumentType.Payment, entity.getId())) {
	
			        			if(pic.getClientCheque() == true){
			        				
			        				chequeStatus = ChequeStatus.Ciro;	
			        			} else {
			        				chequeStatus = ChequeStatus.Cikis;
			        			}
			        			
			        			cpm = new ChequeParamModel();
			        			cpm.setCheque(pic);
			        			cpm.setDocumentId(entity.getId());
								cpm.setDocumentSerial(entity.getSerial());
								cpm.setToContact(entity.getContact());
								cpm.setToAccount(entity.getAccount());
			        			cpm.setDocumentType(DocumentType.Payment);
			        			cpm.setNewStatus(chequeStatus);
                                cpm.setStatusDate(entity.getDate());
                                cpm.setWorkBunch(pItem.getWorkBunch());
			        			chequeAction.changePosition(cpm);
			        			/* Çek döviz tutarının yerel tutarı hesaplanıyor */
			        			
			        			if (pic.getMoney().getCurrency().equals(BaseConsts.SYSTEM_CURRENCY_CODE)){
                                    pic.getMoney().setLocalAmount(pic.getMoney().getValue());
                                } else {
                                    pic.getMoney().setLocalAmount(currencyManager.convertToLocal(pic.getMoney().getValue(), pic.getMoney().getCurrency(),entity.getDate()));
                                }

			        			entityManager.merge(pic);
		        			} 
	
		        		}
		        		
		        	} else if (pItem instanceof PaymentItemPromissoryNote) {
						
	        			PromissoryNote pipn = ((PaymentItemPromissoryNote) pItem).getPromissoryNote();
		        		ChequeStatus chequeStatus;
		        		PromissoryParamModel cpm;
		        		
		        		if( pipn != null ) {
	
		        			pipn.setContact(entity.getContact());

                            //siparis fisi ile olusan kapora odemesi
                            if ( entity.getOrderNote() != null && entity.getOrderNote().getId() != null) {
                                pipn.setTradein(Boolean.TRUE);
                            }

		        			if (! promissoryAction.alreadySaved(pipn, DocumentType.Payment, entity.getId())) {
		        			
			        			if(pipn.getClientPromissoryNote() == true){
			        				
			        				chequeStatus = ChequeStatus.Ciro;	
			        			} else {
			        				chequeStatus = ChequeStatus.Cikis;
			        			}
			        			
			        			cpm = new PromissoryParamModel();
			        			cpm.setPromissory(pipn);
			        			cpm.setDocumentId(entity.getId());
			        			cpm.setDocumentType(DocumentType.Payment);
			        			cpm.setNewStatus(chequeStatus);
                                cpm.setWorkBunch(pItem.getWorkBunch());
		
			        			promissoryAction.changePosition(cpm);
		        				/* Senet döviz tutarının yerel tutarı hesaplanıyor */
                                if (pipn.getMoney().getCurrency().equals(BaseConsts.SYSTEM_CURRENCY_CODE)){
                                    pipn.getMoney().setLocalAmount(pipn.getMoney().getValue());
                                } else {
                                    pipn.getMoney().setLocalAmount(currencyManager.convertToLocal(pipn.getMoney().getValue(), pipn.getMoney().getCurrency(),entity.getDate()));
                                }

			        			entityManager.merge(pipn);
		        			} 
		        			
		        		}
		        		
		        	}
	        		
	        	}

				financeTxnAction.savePayment(entity);
				accountTxnAction.savePayment(entity);

				if (entity.getItems() != null && entity.getItems().size() != 0) {

					for (PaymentItem pItem : entity.getItems()) {

						if (pItem.getLineType() != PaymentType.Cash) {

							financeTxnAction.savePaymentItem(pItem);
						}
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

	@Override
    public void controllItem(){
		//FIXME:bu metottaki kodlar çek-senet modülü elden geçirildiğinde düzenlenecekler.
    	if (getEntity() == null)
			return;
    	
    	try {
    		if(getEntity().getId() != null){
        		if( getEntity().getItems() != null && getEntity().getItems().size() != 0){
        			for(PaymentItem items : getEntity().getItems()) {

						if(items.getId() == null){
							return;
						}
        				if(items instanceof PaymentItemCheque){
        					
        					Cheque cheque = ((PaymentItemCheque) items).getCheque();
        					
							if(cheque.getClientCheque().equals(false) && cheque.getLastStatus() != ChequeStatus.Cikis 
									|| cheque.getClientCheque().equals(true) && cheque.getLastStatus() != ChequeStatus.Ciro){
							
								setIsEditable(false);
								break;
							}
        			}else if(items instanceof PaymentItemPromissoryNote){
	        				PromissoryNote promissory = ((PaymentItemPromissoryNote) items).getPromissoryNote();
	        				
	        				if(promissory.getClientPromissoryNote().equals(false) && promissory.getLastStatus() != ChequeStatus.Cikis 
	        						|| promissory.getClientPromissoryNote().equals(true) &&  promissory.getLastStatus() != ChequeStatus.Ciro){
								
								setIsEditable(false);
								break;
							}
	        			}
	        		}
	        	}else {
					setIsEditable(true);
	        	}
    		}
		} catch (Exception e) {
			log.error("Hata :", e);
		}
    }

	@Override
    public void deleteItem (int i){
    	PaymentItem item = entity.getItems().get(i);
    	try {
			
    		if (item instanceof PaymentItemCheque) {
        		Cheque cheque = ((PaymentItemCheque) item).getCheque();
        		
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
			}
			else if(item instanceof PaymentItemPromissoryNote) {
				PromissoryNote promissory = ((PaymentItemPromissoryNote) item).getPromissoryNote();
				
				promissory.getHistory().remove(promissory.getHistory().size()-1);
				
        		if(promissory.getClientPromissoryNote().equals(Boolean.TRUE)){
        			promissoryChangeLastStatus.setNewStatusOfPromissory(promissory);
        			entityManager.merge(promissory);
    			} else {
        			if(promissory.getHistory().size() == 0){
        				entityManager.remove(promissory);	
        			} else {
        				promissoryChangeLastStatus.setNewStatusOfPromissory(promissory);
        				entityManager.merge(promissory);
        			}
    			}
			}
		} catch (Exception e) {
			// TODO: handle exception
			facesMessages.add("Silmede hata!");
		}
    }

    /**
     *  0 -> Müşteri Senedi
     *  1 -> Firma Senedi
     */
    @Override
    public void popupPromissorySelect(int clientOrFirm) {
    	
    	if (clientOrFirm == 0) {
    	
    		promissorySuggestion.setCallerObserveString("paymentHome:popupNotice:client.promissory");
    		promissorySuggestion.setClientPromissory(Boolean.TRUE);
    		promissorySuggestion.setTargetStatus(ChequeStatus.Ciro); //Cirolamak istiyoruz
    		
    		if( promissorySuggestion.getPromissoryList() != null && promissorySuggestion.getPromissoryList().size() != 0 ){
    			promissorySuggestion.getPromissoryList().clear();
    		}
    	
    		log.debug("Client promissory selected.");

    	} else {
    		
    		promissorySuggestion.setCallerObserveString("paymentHome:popupNotice:firm.promissory");
    		promissorySuggestion.setClientPromissory(Boolean.FALSE);
    		promissorySuggestion.setTargetStatus(ChequeStatus.Ciro); //Cirolamak istiyoruz
    		
    		if( promissorySuggestion.getPromissoryList() != null && promissorySuggestion.getPromissoryList().size() != 0 ){
    			promissorySuggestion.getPromissoryList().clear();
    		}
    		
    		log.debug("Firm promissory selected.");
    	}
    	
    }

    /**
     *  0 -> Müşteri Çeki
     *  1 -> Firma Çeki
     */
    @Override
    public void popupChequeSelect(int clientOrFirm) {
    	
    	if (clientOrFirm == 0) {
    	
    		chequeSuggestion.setCallerObserveString("paymentHome:popupNotice:client.cheque");
    		chequeSuggestion.setClientCheque(Boolean.TRUE);
    		chequeSuggestion.setTargetStatus(ChequeStatus.Ciro); //Cirolamak istiyoruz
    		
    		if( chequeSuggestion.getChequeList()!= null && chequeSuggestion.getChequeList().size() != 0 ) {
    			chequeSuggestion.getChequeList().clear();
    		}
 
    		log.debug("Client cheque selected.");
    		
    	} else {
    		
    		chequeSuggestion.setCallerObserveString("paymentHome:popupNotice:firm.cheque");
    		chequeSuggestion.setClientCheque(Boolean.FALSE);
    		chequeSuggestion.setTargetStatus(ChequeStatus.Ciro); //Cirolamak istiyoruz
    		
    		if( chequeSuggestion.getChequeList()!= null && chequeSuggestion.getChequeList().size() != 0 ) {
    			chequeSuggestion.getChequeList().clear();
    		}
 
    		
    		log.debug("Firm cheque selected.");
    	}
    	
    }
    
    @Override
    public void popupChequeCreate() {
		//FIXME:bu metottaki kodlar çek-senet modülü elden geçirildiğinde düzenlenecekler.
    	
		chequePopupHome.setCallerObserveString("paymentHome:popupNotice:client.cheque");
		chequePopupHome.createNew();
		chequePopupHome.getCheque().setContact(entity.getContact());
		chequePopupHome.getCheque().setChequeOwner(optionManager.getOption("company.Title").getAsString());
        duzenlemeMi = Boolean.FALSE;
    }

    @Override
    public void popupPromissoryCreate() {
		//FIXME:bu metottaki kodlar çek-senet modülü elden geçirildiğinde düzenlenecekler.
    	
		promissoryPopupHome.setCallerObserveString("paymentHome:popupNotice:client.promissory");
		promissoryPopupHome.createNew();
		promissoryPopupHome.getPromissory().setContact(entity.getContact());
        promissoryPopupHome.getPromissory().setPromissorynoteOwner(optionManager.getOption("company.Title").getAsString());
        duzenlemeMi = Boolean.FALSE;
    }
    
	
}
