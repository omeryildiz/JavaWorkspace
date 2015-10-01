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
import com.ut.tekir.entities.DocumentType;
import com.ut.tekir.entities.FinanceAction;
import com.ut.tekir.entities.Foundation;
import com.ut.tekir.entities.FoundationTxn;
import com.ut.tekir.entities.MoneySet;
import com.ut.tekir.entities.PaymentContract;
import com.ut.tekir.entities.PaymentContractPeriod;
import com.ut.tekir.entities.TenderDetailBase;
import com.ut.tekir.entities.inv.TekirInvoice;
import com.ut.tekir.entities.ord.TekirOrderNote;

/**
 * Kurum txn kayıtlarını atan home bileşenimizdir.
 * @author sinan.yumak
 */
@Stateful
@Name("foundationTxnAction")
@Scope(ScopeType.EVENT)
@AutoCreate
public class FoundationTxnActionBean implements FoundationTxnAction {

	private static final long serialVersionUID = 1L;
	
	@Logger
    private Log log;
    @In
    private EntityManager entityManager;
    @In
    private ContractFinder contractFinder;
    
    @Create
    public void init(){
    }

    public void createFoundationTxnRecordsForInvoice(TekirInvoice doc) {
    	log.info("Creating txn records for invoice. Invoice serial:{0}", doc.getSerial());
    	
    	deleteDocument(doc.getId(), doc.getDocumentType());
    	
    	PaymentContract contract = null;
		FoundationTxn txn = null;
		Calendar calendar = Calendar.getInstance();
    	for (TenderDetailBase item : doc.getDiscountAdditionItems()) {
    		Foundation foundation = item.getProduct().getFoundation();

    		if (foundation != null) {
    			
	    		contract = contractFinder.findContract(foundation, doc.getDate());
	
	    		PaymentContractPeriod period = null;
	    		if (foundation.getMaxPeriod()==1) {
	    			period = getPeriod(1, contract);
	    		} else {
	    			//FIXME:ekrandan dönem bilgisi alındığında buradaki 1 parametresi değiştirilecek.
	    			period = getPeriod(1, contract);
	    		}
	    		
				txn = new FoundationTxn();
				
				setAuditRecords(txn, doc);
				
				txn.setFoundation(item.getProduct().getFoundation());
				txn.setDate(doc.getDate());
				txn.setSerial(doc.getSerial());
				txn.setReference(doc.getReference());
				txn.setDocumentId(doc.getId());
				txn.setDocumentType(doc.getDocumentType());
				txn.setInfo(item.getProduct().getName());
				txn.setActive(true);
				txn.setProduct(item.getProduct());
				txn.setAction(FinanceAction.Debit);
	
				txn.setDiscount(item.getDiscount().getLocalAmount());
				
				MoneySet amount = new MoneySet();
				amount.setCurrency(item.getDiscount().getCurrency());
				amount.setLocalAmount(item.getDiscount().getLocalAmount());
				amount.setValue(item.getDiscount().getValue());
			
				txn.setAmount(amount);
				
				
				txn.setBaseAmount(doc.getTotalAmount());
				
				txn.setAdverseCode(foundation.getCode());
				txn.setAdverseName(foundation.getName());
				
	
				txn.setValor(period.getBlockingDay());
	
					
				calendar.setTime(doc.getDate());
				
				calendar.add(Calendar.DAY_OF_YEAR, txn.getValor());
	
				checkPaymentDay(period, calendar);
				
				txn.setMaturityDate(calendar.getTime());
	
				txn.setPaymentActionType(item.getProduct().getPaymentActionType());
				txn.setWorkBunch(doc.getWorkBunch());
				
				entityManager.persist(txn);
    		}
		}
    	
    	FoundationTxn contactTxn = null;
    	for (TenderDetailBase item : doc.getExpenseAdditionItems()) {
    		Foundation foundation = item.getProduct().getFoundation();

    		if (foundation != null) {
    			
	    		contract = contractFinder.findContract(foundation, doc.getDate());
	
	    		PaymentContractPeriod period = null;
	    		if (foundation.getMaxPeriod()==1) {
	    			period = getPeriod(1, contract);
	    		} else {
	    			//FIXME:ekrandan dönem bilgisi alındığında buradaki 1 parametresi değiştirilecek.
	    			period = getPeriod(1, contract);
	    		}
	
				//cari yansıması...
				contactTxn = new FoundationTxn();
				
				setAuditRecords(contactTxn, doc);
				
				contactTxn.setFoundation(item.getProduct().getFoundation());
				contactTxn.setDate(doc.getDate());
				contactTxn.setSerial(doc.getSerial());
				contactTxn.setReference(doc.getReference());
				contactTxn.setDocumentId(doc.getId());
				contactTxn.setDocumentType(doc.getDocumentType());
				contactTxn.setInfo(item.getProduct().getName());
				contactTxn.setActive(true);
				contactTxn.setProduct(item.getProduct());
				
				contactTxn.setAction(FinanceAction.Credit);
	
				contactTxn.setAdded(item.getExpense().getLocalAmount());
				
				MoneySet expense = new MoneySet();
				expense.setCurrency(item.getExpense().getCurrency());
				expense.setLocalAmount(item.getExpense().getLocalAmount());
				expense.setValue(item.getExpense().getValue());
			
				contactTxn.setAmount(expense);
				
				contactTxn.setBaseAmount(doc.getTotalAmount());
				
				contactTxn.setAdverseCode(doc.getContact().getCode());
				contactTxn.setAdverseName(doc.getContact().getFullname());
				
	
				contactTxn.setValor(period.getBlockingDay());
				
				calendar.setTime(doc.getDate());
				
				calendar.add(Calendar.DAY_OF_YEAR, contactTxn.getValor());
	
				checkPaymentDay(period, calendar);
				
				txn.setMaturityDate(calendar.getTime());
	
				
				contactTxn.setMaturityDate(calendar.getTime());
	
				contactTxn.setPaymentActionType(item.getProduct().getPaymentActionType());
	
				
				entityManager.persist(contactTxn);
    		}
    	}
    }

    public void createFoundationTxnRecordsForOrderNote(TekirOrderNote doc) {
    	log.info("Creating txn records for invoice. Invoice serial:{0}", doc.getSerial());
    	
    	deleteDocument(doc.getId(), doc.getDocumentType());
    	
    	PaymentContract contract = null;
		FoundationTxn txn = null;
		Calendar calendar = Calendar.getInstance();
    	for (TenderDetailBase item : doc.getDiscountAdditionItems()) {
    		Foundation foundation = item.getProduct().getFoundation();

    		if (foundation != null) {
    			
	    		contract = contractFinder.findContract(foundation, doc.getDate());
	
	    		PaymentContractPeriod period = null;
	    		if (foundation.getMaxPeriod()==1) {
	    			period = getPeriod(1, contract);
	    		} else {
	    			//FIXME:ekrandan dönem bilgisi alındığında buradaki 1 parametresi değiştirilecek.
	    			period = getPeriod(1, contract);
	    		}
	    		
				txn = new FoundationTxn();
				
				setAuditRecords(txn, doc);
				
				txn.setFoundation(item.getProduct().getFoundation());
				txn.setDate(doc.getDate());
				txn.setSerial(doc.getSerial());
				txn.setReference(doc.getReference());
				txn.setDocumentId(doc.getId());
				txn.setDocumentType(doc.getDocumentType());
				txn.setInfo(item.getProduct().getName());
				txn.setActive(true);
				txn.setProduct(item.getProduct());
				txn.setAction(FinanceAction.Debit);
	
				txn.setDiscount(item.getDiscount().getLocalAmount());
				
				MoneySet amount = new MoneySet();
				amount.setCurrency(item.getDiscount().getCurrency());
				amount.setLocalAmount(item.getDiscount().getLocalAmount());
				amount.setValue(item.getDiscount().getValue());
			
				txn.setAmount(amount);
				
				
				txn.setBaseAmount(doc.getTotalAmount());
				
				txn.setAdverseCode(foundation.getCode());
				txn.setAdverseName(foundation.getName());
				
	
				txn.setValor(period.getBlockingDay());
	
					
				calendar.setTime(doc.getDate());
				
				calendar.add(Calendar.DAY_OF_YEAR, txn.getValor());
	
				checkPaymentDay(period, calendar);
				
				txn.setMaturityDate(calendar.getTime());
	
				txn.setPaymentActionType(item.getProduct().getPaymentActionType());
				txn.setWorkBunch(doc.getWorkBunch());
				
				entityManager.persist(txn);
    		}
		}
    	
    	FoundationTxn contactTxn = null;
    	for (TenderDetailBase item : doc.getExpenseAdditionItems()) {
    		Foundation foundation = item.getProduct().getFoundation();

    		if (foundation != null) {
    			
	    		contract = contractFinder.findContract(foundation, doc.getDate());
	
	    		PaymentContractPeriod period = null;
	    		if (foundation.getMaxPeriod()==1) {
	    			period = getPeriod(1, contract);
	    		} else {
	    			//FIXME:ekrandan dönem bilgisi alındığında buradaki 1 parametresi değiştirilecek.
	    			period = getPeriod(1, contract);
	    		}
	
				//cari yansıması...
				contactTxn = new FoundationTxn();
				
				setAuditRecords(contactTxn, doc);
				
				contactTxn.setFoundation(item.getProduct().getFoundation());
				contactTxn.setDate(doc.getDate());
				contactTxn.setSerial(doc.getSerial());
				contactTxn.setReference(doc.getReference());
				contactTxn.setDocumentId(doc.getId());
				contactTxn.setDocumentType(doc.getDocumentType());
				contactTxn.setInfo(item.getProduct().getName());
				contactTxn.setActive(true);
				contactTxn.setProduct(item.getProduct());
				
				contactTxn.setAction(FinanceAction.Credit);
	
				contactTxn.setAdded(item.getExpense().getLocalAmount());
				
				MoneySet expense = new MoneySet();
				expense.setCurrency(item.getExpense().getCurrency());
				expense.setLocalAmount(item.getExpense().getLocalAmount());
				expense.setValue(item.getExpense().getValue());
			
				contactTxn.setAmount(expense);
				
				contactTxn.setBaseAmount(doc.getTotalAmount());
				
				contactTxn.setAdverseCode(doc.getContact().getCode());
				contactTxn.setAdverseName(doc.getContact().getFullname());
				
	
				contactTxn.setValor(period.getBlockingDay());
				
				calendar.setTime(doc.getDate());
				
				calendar.add(Calendar.DAY_OF_YEAR, contactTxn.getValor());
	
				checkPaymentDay(period, calendar);
				
				txn.setMaturityDate(calendar.getTime());
	
				
				contactTxn.setMaturityDate(calendar.getTime());
	
				contactTxn.setPaymentActionType(item.getProduct().getPaymentActionType());
				contactTxn.setWorkBunch(doc.getWorkBunch());
				
				entityManager.persist(contactTxn);
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
    
    private PaymentContractPeriod getPeriod(int period, PaymentContract contract) {
		for (PaymentContractPeriod item : contract.getPeriodList()) {
			if (item.getPeriod() == period) {
				return item;
			}
    	}
    	return null;
    }
    
    private void setAuditRecords( FoundationTxn txn, AuditBase entityAuditBase) {
        // audit kayıtlarını atıyoruz.
        AuditBase ab = (AuditBase)txn;
        ab.setCreateUser(entityAuditBase.getCreateUser());
        ab.setCreateDate(entityAuditBase.getCreateDate());
        ab.setUpdateUser(entityAuditBase.getUpdateUser());
        ab.setUpdateDate(entityAuditBase.getUpdateDate());
    }

    public void deleteFoundationTxnRecordsForInvoice(TekirInvoice doc) {
    	deleteDocument(doc.getId(), doc.getDocumentType());
    }
    
    public void deleteFoundationTxnRecordsForOrderNote(TekirOrderNote doc) {
    	deleteDocument(doc.getId(), doc.getDocumentType());
    }
    
    protected void deleteDocument(Long docId, DocumentType docType) {
        log.debug("Delete DocID : #0", docId);
        entityManager.createQuery("delete FoundationTxn where " +
        						  "documentId = :docId and " +
        						  "documentType = :docType")
        						  .setParameter("docId", docId)
        						  .setParameter("docType", docType)
        						  .executeUpdate();
        
        entityManager.createQuery("update FoundationTxn set referenceId = null , " +
        		  "repaidStatus = false , referenceDocumentType = null " +
        		  "where referenceId = :docId and " +
				  "referenceDocumentType = :docType")
				  .setParameter("docId", docId)
				  .setParameter("docType", docType)
				  .executeUpdate();
    }

    @Remove
    @Destroy
    public void destroy(){
    }

}
