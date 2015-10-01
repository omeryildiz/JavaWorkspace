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
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.log.Log;

import com.ut.tekir.entities.AdvanceProcessType;
import com.ut.tekir.entities.AuditBase;
import com.ut.tekir.entities.BankAccount;
import com.ut.tekir.entities.BankToAccountTransfer;
import com.ut.tekir.entities.BankToBankTransfer;
import com.ut.tekir.entities.BankToContactTransfer;
import com.ut.tekir.entities.BankTxn;
import com.ut.tekir.entities.Cheque;
import com.ut.tekir.entities.ChequeCollectedAtBankPayroll;
import com.ut.tekir.entities.ChequeToBankPaymentPayroll;
import com.ut.tekir.entities.ChequeToBankPaymentPayrollDetail;
import com.ut.tekir.entities.DepositAccount;
import com.ut.tekir.entities.DocumentBase;
import com.ut.tekir.entities.DocumentType;
import com.ut.tekir.entities.FinanceAction;
import com.ut.tekir.entities.FinanceActionType;
import com.ut.tekir.entities.ForeignExchange;
import com.ut.tekir.entities.MoneySet;
import com.ut.tekir.entities.PromissoryCollectedAtBankPayroll;
import com.ut.tekir.entities.PromissoryNote;
import com.ut.tekir.entities.PromissoryToBankPaymentPayroll;
import com.ut.tekir.entities.PromissoryToBankPaymentPayrollDetail;
import com.ut.tekir.framework.BaseConsts;
import com.ut.tekir.framework.CalendarManager;
import com.ut.tekir.framework.CurrencyManager;

/**
 * Banka hareketlerinin yansıtıldığı home sınıfıdır.
 * @author huseyin
 */
@Stateful()
@Name("bankTxnAction")
@Scope(value = ScopeType.APPLICATION)
@AutoCreate
public class BankTxnActionBean implements BankTxnAction {
    @Logger
    protected Log log;
    @In
    protected EntityManager entityManager;
    @In
    protected FacesMessages facesMessages;
    @In
    protected CalendarManager calendarManager;
    @In
    CurrencyManager currencyManager;
    
    @Create
    public void initComponent() {

    }

    @Remove
    @Destroy
    public void destroy() {
    }

	public Boolean deleteBankToAccount(BankToAccountTransfer doc) {
		
		if(doc != null && doc.getId() != null){
				
			deleteDocument(doc.getId(), DocumentType.BankToAccountTransfer);
			return true;
		}
		return false;
	}

	public Boolean deleteAccountToBank(BankToAccountTransfer doc) {

		if(doc != null && doc.getId() != null){

			deleteDocument(doc.getId(), DocumentType.AccountToBankTransfer);
			return true;
		}
		return false;
	}

	public Boolean deleteBankToBankTransfer(BankToBankTransfer doc) {
		
		if(doc != null && doc.getId() != null){
				
			deleteDocument(doc.getId(), DocumentType.BankToBankTransfer);
			return true;
		}
		return false;
	}

	public Boolean deleteBankToContactTransfer(BankToContactTransfer doc) {
		
		if(doc != null && doc.getId() != null){
			deleteDocument(doc.getId(), DocumentType.BankToContactTransfer);
			return true;
		}
		return false;
	}

	public Boolean deleteContactToBankTransfer(BankToContactTransfer doc) {
		if(doc != null && doc.getId() != null){
			deleteDocument(doc.getId(), DocumentType.ContactToBankTransfer);
			return true;
		}
		return false;
	}
	
	public Boolean deleteForeignExchange(ForeignExchange doc) {
		
		if(doc != null && doc.getId() != null){
			deleteDocument(doc.getId(), DocumentType.ForeignExchange);
			return true;
		}
		return false;
	}
	
    private void saveAuditRecords( BankTxn txn, AuditBase entityAuditBase) {
        // audit kayıtlarını atıyoruz.
        AuditBase ab = txn;
        ab.setCreateUser(entityAuditBase.getCreateUser());
        ab.setCreateDate(entityAuditBase.getCreateDate());
        ab.setUpdateUser(entityAuditBase.getUpdateUser());
        ab.setUpdateDate(entityAuditBase.getUpdateDate());
    }

	public void saveBankToAccount(BankToAccountTransfer item) {
		deleteBankToAccount(item);
		
		BankTxn txn = new BankTxn();
		
		txn.setActive(item.getActive());
		txn.setAction(item.getAction() == FinanceAction.Credit ? FinanceAction.Debit : FinanceAction.Credit);
		txn.setAmount(new MoneySet(item.getAmount()));
		txn.setBankAccount(item.getBankAccount());
		txn.setAdverseCode(item.getAccount().getCode());
		txn.setAdverseName(item.getAccount().getName());
		txn.setCode(item.getCode());
		txn.setDate(item.getDate());
		txn.setDocumentId(item.getId());
		txn.setDocumentType(DocumentType.BankToAccountTransfer);
		txn.setInfo(item.getInfo());
		txn.setReference(item.getReference());
		txn.setSerial(item.getSerial());
		txn.setWorkBunch(item.getWorkBunch());
		

        saveAuditRecords(txn, item);

		entityManager.persist(txn);

        if (!item.getCost().getValue().equals(BigDecimal.ZERO)) {
            BankTxn txn2 = new BankTxn();
            //masrafı da işleyelim.
            txn2.setActive(item.getActive());
            txn2.setAction(FinanceAction.Credit);
            txn2.setActionType(FinanceActionType.Cost);

            txn2.setAmount(item.getCost());
            
            txn2.setBankAccount(item.getBankAccount());
            txn2.setAdverseCode(item.getAccount().getCode());
            txn2.setAdverseName(item.getAccount().getName());
            txn2.setCode(item.getCode());
            txn2.setDate(item.getDate());
            txn2.setDocumentId(item.getId());
            txn2.setDocumentType(DocumentType.BankToAccountTransfer);
            txn2.setInfo(item.getSerial()+" in masrafı");
            txn2.setReference(item.getReference());
            txn2.setSerial(item.getSerial());
            txn2.setWorkBunch(item.getWorkBunch());

            saveAuditRecords(txn2, item);

            entityManager.persist(txn2);
        }

        entityManager.flush();
	}

	public void saveAccountToBank(BankToAccountTransfer item) {
		deleteAccountToBank(item);

		BankTxn txn = new BankTxn();

		txn.setActive(item.getActive());
		txn.setAction(item.getAction() == FinanceAction.Credit ? FinanceAction.Debit : FinanceAction.Credit);
		txn.setAmount(new MoneySet(item.getAmount()));
		txn.setAdverseCode(item.getAccount().getCode());
		txn.setAdverseName(item.getAccount().getName());
		txn.setBankAccount(item.getBankAccount());
		txn.setCode(item.getCode());
		txn.setDate(item.getDate());
		txn.setDocumentId(item.getId());
		txn.setDocumentType(DocumentType.AccountToBankTransfer);
		txn.setInfo(item.getInfo());
		txn.setReference(item.getReference());
		txn.setSerial(item.getSerial());
		txn.setWorkBunch(item.getWorkBunch());

        saveAuditRecords(txn, item);

		entityManager.persist(txn);

        if (!item.getCost().getValue().equals(BigDecimal.ZERO)) {
            BankTxn txn2 = new BankTxn();
            //masrafı da işleyelim.
            txn2.setActive(item.getActive());
            txn2.setAction(FinanceAction.Credit);
            txn2.setActionType(FinanceActionType.Cost);

            txn2.setAmount(item.getCost());
            
            txn2.setBankAccount(item.getBankAccount());
            txn2.setAdverseCode(item.getAccount().getCode());
    		txn2.setAdverseName(item.getAccount().getName());
            txn2.setCode(item.getCode());
            txn2.setDate(item.getDate());
            txn2.setDocumentId(item.getId());
            txn2.setDocumentType(DocumentType.AccountToBankTransfer);
            txn2.setInfo(item.getSerial()+" in masrafı");
            txn2.setReference(item.getReference());
            txn2.setSerial(item.getSerial());
            txn2.setWorkBunch(item.getWorkBunch());

            saveAuditRecords(txn2, item);

            entityManager.persist(txn2);
        }

        entityManager.flush();
	}

	public Boolean saveBankToBankTransfer(BankToBankTransfer doc) {
		deleteBankToBankTransfer(doc);
		
		BankTxn txn1 = new BankTxn();
		BankTxn txn2 = new BankTxn();
		
		txn1.setActive(doc.getActive());
		txn1.setAction(FinanceAction.Credit);
		txn1.setAmount(new MoneySet (doc.getFromAmount()));
		txn1.setBankAccount(doc.getFromBankAccount());
		txn1.setAdverseCode(doc.getToBankAccount().getAccountNo() + '-' + doc.getToBankAccount().getCurrency());
		txn1.setAdverseName(doc.getToBankAccount().getIban());
		txn1.setCode(doc.getCode());
		txn1.setDate(doc.getDate());
		txn1.setDocumentId(doc.getId());
		txn1.setDocumentType(DocumentType.BankToBankTransfer);
		txn1.setInfo(doc.getInfo());
		txn1.setReference(doc.getReference());
		txn1.setSerial(doc.getSerial());
		txn1.setWorkBunch(doc.getWorkBunch());

        saveAuditRecords(txn1, doc);

		entityManager.persist(txn1);
		
		
		txn2.setActive(doc.getActive());
		txn2.setAction(FinanceAction.Debit);
		txn2.setAmount(new MoneySet (doc.getToAmount()));
		txn2.setBankAccount(doc.getToBankAccount());
		txn2.setAdverseCode(doc.getFromBankAccount().getAccountNo() + '-' + doc.getFromBankAccount().getCurrency());
		txn2.setAdverseName(doc.getFromBankAccount().getIban());
		txn2.setCode(doc.getCode());
		txn2.setDate(doc.getDate());
		txn2.setDocumentId(doc.getId());
		txn2.setDocumentType(DocumentType.BankToBankTransfer);
		txn2.setInfo(doc.getInfo());
		txn2.setReference(doc.getReference());
		txn2.setSerial(doc.getSerial());
		txn2.setWorkBunch(doc.getWorkBunch());

        saveAuditRecords(txn2, doc);

		entityManager.persist(txn2);

        if (!doc.getCost().getValue().equals(BigDecimal.ZERO)) {
            BankTxn txn3 = new BankTxn();
            //masrafı da işleyelim.
            txn3.setActive(doc.getActive());
            txn3.setAction(FinanceAction.Credit);
            txn3.setActionType(FinanceActionType.Cost);
            
            txn3.setAmount(doc.getCost());
            
            txn3.setBankAccount(doc.getFromBankAccount());
            txn3.setAdverseCode(doc.getToBankAccount().getAccountNo() + '-' + doc.getToBankAccount().getCurrency());
    		txn3.setAdverseName(doc.getToBankAccount().getIban());
            txn3.setCode(doc.getCode());
            txn3.setDate(doc.getDate());
            txn3.setDocumentId(doc.getId());
            txn3.setDocumentType(DocumentType.BankToBankTransfer);
            txn3.setInfo(doc.getSerial()+" in masrafı");
            txn3.setReference(doc.getReference());
            txn3.setSerial(doc.getSerial());
            txn3.setWorkBunch(doc.getWorkBunch());

            saveAuditRecords(txn3, doc);

            entityManager.persist(txn3);
        }
		
		entityManager.flush();
		return true;
	}

	public void saveChequeCollectedAtBankPayroll(ChequeCollectedAtBankPayroll doc) {
		deleteChequeCollectedAtBankPayroll(doc);

        BankTxn txn = new BankTxn();
		
		txn.setActive(doc.getActive());
		txn.setAction(FinanceAction.Debit);
		
		MoneySet tempMoneySet = new MoneySet();
		tempMoneySet.setCurrency(BaseConsts.SYSTEM_CURRENCY_CODE);
		tempMoneySet.setLocalAmount(doc.getTotals().getLocalTotal());
		tempMoneySet.setValue(doc.getTotals().getLocalTotal());
		
		txn.setAmount(tempMoneySet);
		txn.setBankAccount(doc.getBankAccount());
		txn.setCode(doc.getCode());
		txn.setDate(doc.getDate());
		txn.setDocumentId(doc.getId());
		txn.setDocumentType(DocumentType.ChequeCollectedAtBankPayroll);
		txn.setInfo(doc.getInfo());
		txn.setReference(doc.getReference());
		txn.setSerial(doc.getSerial());
		txn.setAdverseName("Çek bankada tahsil edildi.");

        saveAuditRecords(txn, doc);

		entityManager.persist(txn);
		entityManager.flush();
	}

    public void saveChequeToBankPaymentPayroll(ChequeToBankPaymentPayroll doc) {
        deleteChequeToBankPaymentPayroll(doc);

        // Gerekli islem burada yapilacak.

        for (ChequeToBankPaymentPayrollDetail detail : doc.getDetails() ) {
        	
	        BankTxn txn = new BankTxn();
	
	        txn.setActive(doc.getActive());
	        txn.setAction(FinanceAction.Credit);
	        txn.setActionType(FinanceActionType.Capital);
	
	
	        //FIXME burda ceklerin moneyset tipinde toplamları donecek metod yazılacak.
	        //dovizli ceklerde bankladan doviz cıkmalı
	        MoneySet tempMoney = new MoneySet();
	
	        tempMoney.setCurrency(detail.getCheque().getMoney().getCurrency());
	        tempMoney.setLocalAmount( detail.getCheque().getMoney().getLocalAmount());
	        tempMoney.setValue(detail.getCheque().getMoney().getValue());
	        txn.setAmount(tempMoney);
	
	        txn.setBankAccount(doc.getBankAccount());
	        txn.setCode(doc.getCode());
	        txn.setDate(doc.getDate());
	        txn.setDocumentId(doc.getId());
	        txn.setDocumentType(DocumentType.ChequeBankPaymentPayroll);
	        txn.setInfo(doc.getInfo());
	        txn.setReference(doc.getReference());
	        txn.setSerial(doc.getSerial());
	        
			txn.setAdverseCode(detail.getCheque().getReferenceNo());
			txn.setAdverseName(detail.getCheque().getChequeOwner());
		
	        saveAuditRecords(txn, doc);
	        
	        entityManager.persist(txn);
        }
    }

    public void savePromissoryToBankPaymentPayroll(PromissoryToBankPaymentPayroll doc) {
    	deletePromissoryToBankPaymentPayroll(doc);
    	
    	for (PromissoryToBankPaymentPayrollDetail detail : doc.getDetails() ) {
    		
	    	BankTxn txn = new BankTxn();
	    	
	    	txn.setActive(doc.getActive());
	    	txn.setAction(FinanceAction.Credit);
	    	txn.setActionType(FinanceActionType.Capital);
	    	
	    	MoneySet tempMoney = new MoneySet();
	    	tempMoney.setCurrency(BaseConsts.SYSTEM_CURRENCY_CODE);
	    	
	    	tempMoney.setLocalAmount(doc.getTotals().getLocalTotal());
	    	tempMoney.setValue(doc.getTotals().getLocalTotal());
	    	txn.setAmount(tempMoney);
	    	
	    	txn.setBankAccount(doc.getBankAccount());
	    	txn.setCode(doc.getCode());
	    	txn.setDate(doc.getDate());
	    	txn.setDocumentId(doc.getId());
	    	txn.setDocumentType(DocumentType.PromissoryBankPaymentPayroll);
	    	txn.setInfo(doc.getInfo());
	    	txn.setReference(doc.getReference());
	    	txn.setSerial(doc.getSerial());
	    	
			txn.setAdverseCode(detail.getPromissory().getReferenceNo());
			txn.setAdverseName(detail.getPromissory().getPromissorynoteOwner());
			
	    	saveAuditRecords(txn, doc);
	    	
	    	entityManager.persist(txn);
    	}
    }

    public void deleteChequeToBankPaymentPayroll(ChequeToBankPaymentPayroll doc) {

        if (doc.getId() != null) {
            deleteDocument(doc.getId(), DocumentType.ChequeBankPaymentPayroll);
        }
    }

    public void deletePromissoryToBankPaymentPayroll(PromissoryToBankPaymentPayroll doc) {
    	
    	if (doc.getId() != null) {
    		deleteDocument(doc.getId(), DocumentType.PromissoryBankPaymentPayroll);
    	}
    }
    
	public Boolean deleteChequeCollectedAtBankPayroll(ChequeCollectedAtBankPayroll doc){
		
		if(doc != null && doc.getId() != null){
			deleteDocument(doc.getId(), DocumentType.ChequeCollectedAtBankPayroll);
			return true;
		}
		return false;
	}
	
	public void saveBankToContactTransfer(BankToContactTransfer item) {
		deleteBankToContactTransfer(item);
				
        BankTxn txn = new BankTxn();
		
		txn.setActive(item.getActive());
		txn.setAction(item.getAction() == FinanceAction.Debit ? FinanceAction.Credit : FinanceAction.Debit);
        txn.setActionType(FinanceActionType.Capital);
		txn.setAmount(new MoneySet(item.getAmount()));
		txn.setBankAccount(item.getBankAccount());
		txn.setAdverseCode(item.getContact().getCode());
		if (item.getContact().getPerson()) {
			txn.setAdverseName(item.getContact().getFullname());	
		} else {
			txn.setAdverseName(item.getContact().getCompany());
		}			
		txn.setCode(item.getCode());
		txn.setDate(item.getDate());
		txn.setDocumentId(item.getId());
		txn.setDocumentType(DocumentType.BankToContactTransfer);
		txn.setInfo(item.getInfo());
		txn.setReference(item.getReference());
		txn.setSerial(item.getSerial());
		txn.setProcessType(item.getProcessType());
		txn.setWorkBunch(item.getWorkBunch());
		txn.setProcessType(item.getProcessType());

        saveAuditRecords(txn, item);

        entityManager.persist(txn);

        if (item.getCost().getValue().compareTo(BigDecimal.ZERO) == 1) {
            BankTxn txn2 = new BankTxn();
            //masrafı da işleyelim.
            txn2.setActive(item.getActive());
            txn2.setAction(FinanceAction.Credit);
            txn2.setActionType(FinanceActionType.Cost);

			txn2.setAmount(item.getCost());
            
            txn2.setBankAccount(item.getBankAccount());
            txn2.setAdverseCode(item.getContact().getCode());
    		if (item.getContact().getPerson()) {
    			txn2.setAdverseName(item.getContact().getFullname());	
    		} else {
    			txn2.setAdverseName(item.getContact().getCompany());
    		}	            
            txn2.setCode(item.getCode());
            txn2.setDate(item.getDate());
            txn2.setDocumentId(item.getId());
            txn2.setDocumentType(DocumentType.BankToContactTransfer);
            txn2.setInfo(item.getSerial()+" in masrafı");
            txn2.setReference(item.getReference());
            txn2.setSerial(item.getSerial());
            txn2.setWorkBunch(item.getWorkBunch());
            txn2.setProcessType(item.getProcessType());

            saveAuditRecords(txn2, item);

            entityManager.persist(txn2);
        }
		
        entityManager.flush();
	}
	
	public void saveContactToBankTransfer(BankToContactTransfer item) {
		deleteContactToBankTransfer(item);

        BankTxn txn = new BankTxn();

		txn.setActive(item.getActive());
		txn.setAction(item.getAction() == FinanceAction.Debit ? FinanceAction.Credit : FinanceAction.Debit);
        txn.setActionType(FinanceActionType.Capital);
		txn.setAmount(new MoneySet(item.getAmount()));
		txn.setBankAccount(item.getBankAccount());
		txn.setAdverseCode(item.getContact().getCode());
		if (item.getContact().getPerson()) {
			txn.setAdverseName(item.getContact().getFullname());	
		} else {
			txn.setAdverseName(item.getContact().getCompany());
		}		
		txn.setCode(item.getCode());
		txn.setDate(item.getDate());
		txn.setDocumentId(item.getId());
		txn.setDocumentType(DocumentType.ContactToBankTransfer);
		txn.setInfo(item.getInfo());
		txn.setReference(item.getReference());
		txn.setSerial(item.getSerial());
		txn.setWorkBunch(item.getWorkBunch());
		txn.setProcessType(item.getProcessType());

        saveAuditRecords(txn, item);

        entityManager.persist(txn);

		//Masrafı yansıtıyoruz.
        if (item.getCost().getValue().compareTo(BigDecimal.ZERO) == 1) {
        	BankTxn txn2 = new BankTxn();
            txn2.setActive(item.getActive());
            txn2.setAction(FinanceAction.Credit);
            txn2.setActionType(FinanceActionType.Cost);
        
            txn2.setAmount(item.getCost());
            
            txn2.setBankAccount(item.getBankAccount());
            txn2.setAdverseCode(item.getContact().getCode());
    		if (item.getContact().getPerson()) {
    			txn2.setAdverseName(item.getContact().getFullname());	
    		} else {
    			txn2.setAdverseName(item.getContact().getCompany());
    		}	                		
            txn2.setCode(item.getCode());
            txn2.setDate(item.getDate());
            txn2.setDocumentId(item.getId());
            txn2.setDocumentType(DocumentType.ContactToBankTransfer);
            txn2.setInfo(item.getSerial()+" in masrafı");
            txn2.setReference(item.getReference());
            txn2.setSerial(item.getSerial());
            txn2.setWorkBunch(item.getWorkBunch());
            txn2.setProcessType(item.getProcessType());

            saveAuditRecords(txn2, item);

            entityManager.persist(txn2);
        }

        entityManager.flush();
	}

	public void deleteDepositAccount(DepositAccount doc) {
		deleteDocument(doc);
	}

	public void saveDepositAccount(DepositAccount doc) {
		//FIXME: burada nasıl bi mantık yürütmeli?
		deleteDocument(doc);
		
		BankTxn txn1 = new BankTxn();
		
		txn1.setActive(doc.getActive());
		txn1.setAction(FinanceAction.Debit);
		txn1.setAmount(new MoneySet(doc.getAmount()));
		txn1.setBankAccount(doc.getBankAccount());
		txn1.setAdverseCode(doc.getDepositBankAccount().getAccountNo());
		txn1.setAdverseName(doc.getDepositBankAccount().getName());
		txn1.setCode(doc.getCode());
		txn1.setDate(doc.getDate());
		txn1.setDocumentId(doc.getId());
		txn1.setDocumentType(DocumentType.DepositAccount);
		txn1.setInfo(doc.getInfo());
		txn1.setReference(doc.getReference());
		txn1.setSerial(doc.getSerial());

        saveAuditRecords(txn1, doc);

        
		entityManager.persist(txn1);
		
		BankTxn txn2 = new BankTxn();

		txn2.setActive(doc.getActive());
		txn2.setAction(FinanceAction.Credit);
		txn2.setAmount(new MoneySet(doc.getAmount()));
		txn2.setBankAccount(doc.getDepositBankAccount());
		txn2.setAdverseCode(doc.getBankAccount().getAccountNo());
		txn2.setAdverseName(doc.getBankAccount().getName());
		txn2.setCode(doc.getCode());
		txn2.setDate(doc.getDate());
		txn2.setDocumentId(doc.getId());
		txn2.setDocumentType(DocumentType.DepositAccount);
		txn2.setInfo(doc.getInfo());
		txn2.setReference(doc.getReference());
		txn2.setSerial(doc.getSerial());

        saveAuditRecords(txn2, doc);

		entityManager.persist(txn2);
	}

    public void deleteDocument(Long docId, DocumentType docType) {
        entityManager.createQuery("delete BankTxn where DocumentId = :docId and documentType = :docType").setParameter("docId", docId).setParameter("docType", docType).executeUpdate();
    }

    private void deleteDocument(DocumentBase document) {
    	entityManager.createQuery("delete BankTxn where " +
    							  "documentId = :docId and " +
    							  "documentType = :docType")
    							  .setParameter("docId", document.getId())
    							  .setParameter("docType", document.getDocumentType())
    							  .executeUpdate();
    }
	
	public Boolean deleteCheque(Cheque cheque, DocumentType documentType) {
		
		if(cheque != null && documentType != null && cheque.getId() != null){
				
			deleteDocument(cheque.getId(), documentType);
			return true;
		}
		return false;
	}

	public BankTxn saveCheque(Cheque cheque, BankAccount account, FinanceAction action, DocumentType documentType) {
//		deleteCheque(cheque, documentType);
		
		BankTxn txn = new BankTxn();
		
		txn.setAmount(new MoneySet(cheque.getMoney()));
		txn.setBankAccount(account);
		txn.setCode(cheque.getReferenceNo());
		txn.setDate(cheque.getEntryDate());
		txn.setDocumentId(cheque.getId());
		txn.setAction(action);
		txn.setDocumentType(documentType);
		txn.setInfo(cheque.getInfo());
		txn.setReference(cheque.getReferenceNo());
		txn.setSerial(cheque.getSerialNo());


		entityManager.persist(txn);
	    
		return txn;
	}
	
	public Boolean deletePromissory(PromissoryNote promissory, DocumentType documentType) {
		
		if(promissory != null && documentType != null && promissory.getId() != null){
				
			deleteDocument(promissory.getId(), documentType);
			return true;
		}
		return false;
	}
	
	public BankTxn savePromissory(PromissoryNote promissory, BankAccount account, FinanceAction action, DocumentType documentType) {
//		deletePromissory(promissory, documentType);
		
		BankTxn txn = new BankTxn();
		
		txn.setAmount(new MoneySet(promissory.getMoney()));
		txn.setBankAccount(account);
		txn.setCode(promissory.getReferenceNo());
		txn.setDate(promissory.getEntryDate());
		txn.setDocumentId(promissory.getId());
		txn.setAction(action);
		txn.setDocumentType(documentType);
		txn.setInfo(promissory.getInfo());
		txn.setReference(promissory.getReferenceNo());
		txn.setSerial(promissory.getSerialNo());


		entityManager.persist(txn);
		
		return txn;
	}

	public void savePromissoryCollectedAtBankPayroll(PromissoryCollectedAtBankPayroll doc) {
		deletePromissoryCollectedAtBankPayroll(doc);

		BankTxn txn = new BankTxn();

		txn.setActive(doc.getActive());
		txn.setAction(FinanceAction.Debit);

		MoneySet tempMoneySet = new MoneySet();
		tempMoneySet.setCurrency(BaseConsts.SYSTEM_CURRENCY_CODE);
		tempMoneySet.setLocalAmount(doc.getTotals().getLocalTotal());
		tempMoneySet.setValue(doc.getTotals().getLocalTotal());

		txn.setAmount(tempMoneySet);
		txn.setBankAccount(doc.getBankAccount());
		txn.setCode(doc.getCode());
		txn.setDate(doc.getDate());
		txn.setDocumentId(doc.getId());
		txn.setDocumentType(DocumentType.PromissoryCollectedAtBankPayroll);
		txn.setInfo(doc.getInfo());
		txn.setReference(doc.getReference());
		txn.setSerial(doc.getSerial());
		txn.setAdverseName("Senet bankada tahsil edildi.");


        saveAuditRecords(txn, doc);

		entityManager.persist(txn);
		entityManager.flush();
	}

	public Boolean deletePromissoryCollectedAtBankPayroll(PromissoryCollectedAtBankPayroll doc) {

		if (doc != null && doc.getId() != null) {
			deleteDocument(doc.getId(),
					DocumentType.PromissoryCollectedAtBankPayroll);
			return true;
		}
		return false;
	}
	
	public Boolean saveForeignExchange(ForeignExchange doc) {
		deleteForeignExchange(doc);
		
		BankTxn txn1 = new BankTxn();
		BankTxn txn2 = new BankTxn();
		
		txn1.setActive(doc.getActive());
		txn1.setAction(FinanceAction.Credit);
		txn1.setAmount(new MoneySet (doc.getFromAmount()));
		txn1.setBankAccount(doc.getFromBankAccount());
		txn1.setAdverseCode(doc.getToBankAccount().getAccountNo() + '-' + doc.getToBankAccount().getCurrency());
		txn1.setAdverseName(doc.getToBankAccount().getIban());
		txn1.setCode(doc.getCode());
		txn1.setDate(doc.getDate());
		txn1.setDocumentId(doc.getId());
		txn1.setDocumentType(DocumentType.ForeignExchange);
		txn1.setInfo(doc.getInfo());
		txn1.setReference(doc.getReference());
		txn1.setSerial(doc.getSerial());


        saveAuditRecords(txn1, doc);

		entityManager.persist(txn1);
		
		
		txn2.setActive(doc.getActive());
		txn2.setAction(FinanceAction.Debit);
		txn2.setAmount(new MoneySet (doc.getToAmount()));
		txn2.setBankAccount(doc.getToBankAccount());
		txn2.setAdverseCode(doc.getFromBankAccount().getAccountNo() + '-' + doc.getFromBankAccount().getCurrency());
		txn2.setAdverseName(doc.getFromBankAccount().getIban());
		txn2.setCode(doc.getCode());
		txn2.setDate(doc.getDate());
		txn2.setDocumentId(doc.getId());
		txn2.setDocumentType(DocumentType.ForeignExchange);
		txn2.setInfo(doc.getInfo());
		txn2.setReference(doc.getReference());
		txn2.setSerial(doc.getSerial());

        saveAuditRecords(txn2, doc);

		entityManager.persist(txn2);

        if (!doc.getCost().getValue().equals(BigDecimal.ZERO)) {
            BankTxn txn3 = new BankTxn();
            //masrafı da işleyelim.
            txn3.setActive(doc.getActive());
            txn3.setAction(FinanceAction.Credit);
            txn3.setActionType(FinanceActionType.Cost);
            
            txn3.setAmount(doc.getCost());
            
            txn3.setBankAccount(doc.getFromBankAccount());
            txn3.setAdverseCode(doc.getToBankAccount().getAccountNo() + '-' + doc.getToBankAccount().getCurrency());
    		txn3.setAdverseName(doc.getToBankAccount().getIban());
            txn3.setCode(doc.getCode());
            txn3.setDate(doc.getDate());
            txn3.setDocumentId(doc.getId());
            txn3.setDocumentType(DocumentType.ForeignExchange);
            txn3.setInfo(doc.getSerial()+" in masrafı");
            txn3.setReference(doc.getReference());
            txn3.setSerial(doc.getSerial());


            saveAuditRecords(txn3, doc);

            entityManager.persist(txn3);
        }
		
		entityManager.flush();
		return true;
	}

}