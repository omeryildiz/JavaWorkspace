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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.List;

import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.log.Log;

import com.ut.tekir.entities.AuditBase;
import com.ut.tekir.entities.BankToContactTransfer;
import com.ut.tekir.entities.DocumentType;
import com.ut.tekir.entities.FinanceAction;
import com.ut.tekir.entities.MoneySet;
import com.ut.tekir.entities.Payment;
import com.ut.tekir.entities.PaymentActionType;
import com.ut.tekir.entities.PaymentContract;
import com.ut.tekir.entities.PaymentContractCommision;
import com.ut.tekir.entities.PaymentContractPeriod;
import com.ut.tekir.entities.PaymentItem;
import com.ut.tekir.entities.PaymentItemCreditCard;
import com.ut.tekir.entities.PaymentTable;
import com.ut.tekir.entities.PaymentTableDetail;
import com.ut.tekir.entities.PosTxn;
import com.ut.tekir.entities.TaxKind;
import com.ut.tekir.general.GeneralSuggestion;

/**
 *
 * @author sinan.yumak
 */
@Stateful()
@Name("posTxnAction")
@Scope(value = ScopeType.APPLICATION)
@AutoCreate
public class PosTxnActionBean implements PosTxnAction {

	private static final long serialVersionUID = 1L;

	@Logger
    private Log log;
    @In
    private EntityManager entityManager;
    @In
    ContractFinder contractFinder;
    @In
    GeneralSuggestion generalSuggestion;
    
    @Create
    public void initComponent() {
    }

    @Remove
    @Destroy
    public void destroy() {
    }

    private void saveAuditRecords( PosTxn txn, AuditBase entityAuditBase) {
        // audit kayıtlarını atıyoruz.
        AuditBase ab = (AuditBase)txn;
        ab.setCreateUser(entityAuditBase.getCreateUser());
        ab.setCreateDate(entityAuditBase.getCreateDate());
        ab.setUpdateUser(entityAuditBase.getUpdateUser());
        ab.setUpdateDate(entityAuditBase.getUpdateDate());
    }

    public void createPosTxnRecordsForCollection(Payment doc) {
    	log.info("Creating txn records for collection. Collection serial:{0}", doc.getSerial());
    	deleteDocument(doc.getId(), DocumentType.Collection);
    	
    	PaymentContract contract = null;
		Calendar calendar = Calendar.getInstance();

    	PaymentItemCreditCard item = null;
    	for (PaymentItem pi : doc.getItems()) {
    		if (pi instanceof PaymentItemCreditCard) {
    			item = (PaymentItemCreditCard)pi;
    			
        		contract = contractFinder.findContract(item.getPos(), item.getLineType(), doc.getDate(), item.getBank());

    			
        		PaymentContractPeriod period = contract.getPeriod(item.getPeriod());
        		
        		if (period == null) {
        			throw new RuntimeException(contract.getName() + " sözleşmesi içerisinde " +
        									  item.getPeriod() + " taksit için tanımlama bulunamadı.");
        		}

        		int processedMonth = calendar.get(Calendar.MONTH);
        		
        		//eğer geri ödeme tek seferde yapılacaksa,
        		int maxPeriodNumber = 1;
    			if (period.getCollectionType()) {
    				maxPeriodNumber = item.getPeriod();
    			} 
        		
        		for (int periodNumber=1;periodNumber<=maxPeriodNumber;periodNumber++) {
        			PosTxn mainTxn = new PosTxn();
        			
        			saveAuditRecords(mainTxn, (AuditBase)doc);

                    if (doc.getAction() == FinanceAction.Debit) {
                    	mainTxn.setDocumentType(DocumentType.Payment);
                    } else {
                    	mainTxn.setDocumentType(DocumentType.Collection);
                    }

                    //siparis fisi ile olusan kapora odemesi
        			if (doc.getOrderNote() != null && doc.getOrderNote().getId() != null) {
        				mainTxn.setTradein(Boolean.TRUE);
                    }
        			mainTxn.setCode(doc.getCode());
        			mainTxn.setPos(item.getPos());
        			mainTxn.setDate(doc.getDate());
        			mainTxn.setSerial(doc.getSerial());
        			mainTxn.setReference(doc.getReference());
        			mainTxn.setDocumentId(doc.getId());
        			mainTxn.setCreditCardNumber(item.getCreditCardNumber());
        			mainTxn.setActive(true);
        			mainTxn.setAction(FinanceAction.Debit);
        			mainTxn.setPaymentTableReferenceId(item.getPaymentTableReferenceId());

        			BigDecimal periodDivider = BigDecimal.valueOf(maxPeriodNumber);
        			BigDecimal calculatedAmount = pi.getAmount().getLocalAmount();

        			if (period.getCollectionType()) {
        				calculatedAmount = calculatedAmount.divide(periodDivider, 2, RoundingMode.HALF_UP);
        			}
        			
        			MoneySet baseAmount = new MoneySet();
        			baseAmount.setLocalAmount(calculatedAmount);
        			baseAmount.setValue(calculatedAmount);
        			baseAmount.setCurrency(pi.getAmount().getCurrency());
        			mainTxn.setBaseAmount(baseAmount);
        			
					mainTxn.setAmount(baseAmount);
					
        			mainTxn.setAdverseCode(item.getBank().getCode());
        			mainTxn.setAdverseName(item.getBank().getName());

        			PaymentActionType actionType = null;
        			if (doc.getAction().equals(FinanceAction.Debit)) {
        				actionType = generalSuggestion.findPaymentActionType("ALIŞ");
        				
        				mainTxn.setInfo(maxPeriodNumber + "/" + periodNumber + " " + actionType.getName());
        			} else {
        				actionType = generalSuggestion.findPaymentActionType("SATIŞ");

        				mainTxn.setInfo(maxPeriodNumber + "/" + periodNumber + " " + actionType.getName());
        			}
        			mainTxn.setPaymentActionType(actionType);
        			
        			
        			mainTxn.setValor(period.getBlockingDay());
        			calendar.setTime(doc.getDate());
        			
        			//eğer geri ödeme tek seferde yapılacaksa,
        			processedMonth++; 

        			if (period.getBlockingType()) {
            			
            			calendar.set(Calendar.MONTH, processedMonth);

            			calendar.set(Calendar.DAY_OF_MONTH, period.getBlockingDay());
        			} else {
        				calendar.add(Calendar.DAY_OF_YEAR, period.getBlockingDay());
        			}


    				//blokaj tarihinden sonra belirli sabit bir gun alınacaksa,
    				if (period.getMaturityType()) {
            			calendar.set(Calendar.MONTH, processedMonth);

    					calendar.set(Calendar.DAY_OF_MONTH, period.getMaturityDay());
    					
    				} else {
        				//blokaj tarihinden sonra ilave gun
    					calendar.add(Calendar.DAY_OF_YEAR, period.getMaturityDay() * periodNumber);
    					
    				}
					checkPaymentDay(period, calendar);
					
					mainTxn.setMaturityDate(calendar.getTime());
        			
					entityManager.persist(mainTxn);
        			//Komisyonlarla ilgili kayıtlar...

	        		PaymentContractCommision commission = null;
	        		int commisionListSize = period.getCommisionList().size();
	        		for (int i=0;i<commisionListSize;i++) {
	        			commission = period.getCommisionList().get(i);
	        			
						PosTxn txn = new PosTxn();

	                    saveAuditRecords(txn, (AuditBase)doc);

	                    if (doc.getAction() == FinanceAction.Debit) {
	                        txn.setDocumentType(DocumentType.Payment);
	                    } else {
	                        txn.setDocumentType(DocumentType.Collection);
	                    }

            			txn.setAction(FinanceAction.Credit);
	                    
	                    //siparis fisi ile olusan kapora odemesi
	        			if (doc.getOrderNote() != null && doc.getOrderNote().getId() != null) {
	                        txn.setTradein(Boolean.TRUE);
	                    }

	        			txn.setCode(doc.getCode());
	                    txn.setPos(item.getPos());
	        			txn.setDate(doc.getDate());
	        			txn.setSerial(doc.getSerial());
	        			txn.setReference(doc.getReference());
	        			txn.setDocumentId(doc.getId());
	        			txn.setCreditCardNumber(item.getCreditCardNumber());
	        			txn.setPaymentTableReferenceId(item.getPaymentTableReferenceId());
	        			txn.setActive(true);


	        			txn.setBaseAmount(baseAmount);
	        			
	        			txn.setAdverseCode(item.getBank().getCode());
	        			txn.setAdverseName(item.getBank().getName());

	        			txn.setPaymentActionType(commission.getCommission().getPaymentActionType());
	        			
	        			txn.setInfo(maxPeriodNumber + "/" + periodNumber + " " +
	        					    commission.getCommission().getPaymentActionType().getName());
	        			

	        			txn.setValor(period.getBlockingDay());
	        			calendar.setTime(doc.getDate());
	        			
	        			//eğer geri ödeme tek seferde yapılacaksa,
	        			if (period.getBlockingType()) {
	            			calendar.set(Calendar.MONTH, processedMonth);

	            			calendar.set(Calendar.DAY_OF_MONTH, period.getBlockingDay());
	        			} else {
	        				calendar.add(Calendar.DAY_OF_YEAR, period.getBlockingDay());
	        			}


	    				//blokaj tarihinden sonra belirli sabit bir gun alınacaksa,
	    				if (period.getMaturityType()) {
	            			calendar.set(Calendar.MONTH, processedMonth);

	    					calendar.set(Calendar.DAY_OF_MONTH, period.getMaturityDay());
	    					
	    				} else {
	        				//blokaj tarihinden sonra ilave gun
	    					calendar.add(Calendar.DAY_OF_YEAR, period.getMaturityDay() * periodNumber);
	    					
	    				}
						checkPaymentDay(period, calendar);
						
						txn.setMaturityDate(calendar.getTime());

						if (commission.getKind().equals(TaxKind.Amount)) {
							txn.setDiscount(commission.getAmount());
						} else if (commission.getKind().equals(TaxKind.Rate)) {
							txn.setRate(commission.getRate().doubleValue());
							
							BigDecimal hundred = BigDecimal.valueOf(100);
							BigDecimal commissionRate = commission.getRate();
							BigDecimal totalAmount = baseAmount.getLocalAmount();
							
							BigDecimal calculatedDiscount = totalAmount.multiply(commissionRate)
																	   .divide(hundred, 2, RoundingMode.HALF_UP);
							
							txn.setDiscount(calculatedDiscount);
						}
						

						MoneySet amount = new MoneySet();
						amount.setCurrency(pi.getAmount().getCurrency());
						amount.setLocalAmount(txn.getDiscount());
						amount.setValue(txn.getDiscount());
						txn.setAmount(amount);
	        			
						entityManager.persist(txn);
	        		}					
        		}
    		}
    	}
    }

    private void updateTradeinField(Long aReferenceId, boolean newState) {
    	entityManager.createQuery("update PosTxn p set p.tradein = :newState where " +
    							  "p.paymentTableReferenceId=:referenceId")
    							  .setParameter("newState", newState)
    							  .setParameter("referenceId", aReferenceId)
    							  .executeUpdate();
    	
    }

    public void updateTradeinFields(PaymentTable paymentTable, boolean newState) {
    	for (PaymentTableDetail item : paymentTable.getDetailList()) {
    	
    		if (item.getReferenceId() != null) {
    			updateTradeinField(item.getReferenceId(), newState);
    		}
    	}
    }

    private void checkPaymentDay(PaymentContractPeriod period, Calendar calendar) {
    	int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
    	log.info("Checking payment day. Period id :{0}, Day of Week", period.getPeriod(), dayOfWeek);

    	int additionTime = 0;
    	List<Boolean> paymentDays = period.paymentDaysAsList();
    	
    	if (!paymentDays.get(dayOfWeek)) {
    		for (int i=dayOfWeek+1; i<paymentDays.size();i++) {
    			if (!paymentDays.get(i)) {
    				additionTime++;

    				if (i+1<7 && paymentDays.get(i+1) ) {
    					break;
    				}
    			}
    		}
    		for (int i=0;i<dayOfWeek;i++) {
    			if (!paymentDays.get(i)) {
    				additionTime++;

    				if (paymentDays.get(i+1)) {
    					break;
    				}
    			}
    		}
    	}

    	if (additionTime != 0) {
    		log.info("Eklenecek gün sayısı:{0}", additionTime);
    		calendar.add(Calendar.DAY_OF_YEAR, additionTime + 1);
    	}
    }
    
    @Override
	public void deletePosTxnRecordsForCollection(Payment aCollection) {
    	deleteDocument(aCollection.getId(), DocumentType.Collection);
	}

    @Override
    public void deletePosTxnRecordsForContactToBankTransfer(BankToContactTransfer doc) {
    	deleteDocument(doc.getId(), DocumentType.ContactToBankTransfer);
    }
    
    private void deleteDocument(Long docId, DocumentType docType) {
        entityManager.createQuery("delete PosTxn where " +
        						  "documentId = :docId and " +
        						  "documentType = :docType")
        						  .setParameter("docId", docId)
        						  .setParameter("docType", docType)
        						  .executeUpdate();

        entityManager.createQuery("update PosTxn set referenceId = null , " +
      		  "repaidStatus = false , referenceDocumentType = null " +
      		  "where referenceId = :docId and " +
		      "referenceDocumentType = :docType")
		      .setParameter("docId", docId)
		      .setParameter("docType", docType)
		      .executeUpdate();
    
    }

}
