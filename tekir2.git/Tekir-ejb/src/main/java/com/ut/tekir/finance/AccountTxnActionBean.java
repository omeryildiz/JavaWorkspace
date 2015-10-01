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

import java.util.Map;
import java.util.Map.Entry;

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

import com.ut.tekir.entities.Account;
import com.ut.tekir.entities.AccountCreditNote;
import com.ut.tekir.entities.AccountCreditNoteDetail;
import com.ut.tekir.entities.AccountTxn;
import com.ut.tekir.entities.AdvanceProcessType;
import com.ut.tekir.entities.AuditBase;
import com.ut.tekir.entities.BankToAccountTransfer;
import com.ut.tekir.entities.Cheque;
import com.ut.tekir.entities.ChequeToAccountCollectionPayroll;
import com.ut.tekir.entities.ChequeToAccountPaymentPayroll;
import com.ut.tekir.entities.Contact;
import com.ut.tekir.entities.DocumentBase;
import com.ut.tekir.entities.DocumentItemBase;
import com.ut.tekir.entities.DocumentTotal;
import com.ut.tekir.entities.DocumentType;
import com.ut.tekir.entities.FinanceAction;
import com.ut.tekir.entities.ForeignDocumentBase;
import com.ut.tekir.entities.FundTransfer;
import com.ut.tekir.entities.FundTransferItem;
import com.ut.tekir.entities.MoneySet;
import com.ut.tekir.entities.Option;
import com.ut.tekir.entities.Payment;
import com.ut.tekir.entities.PaymentDocumentTotal;
import com.ut.tekir.entities.PaymentTable;
import com.ut.tekir.entities.PaymentTableDetail;
import com.ut.tekir.entities.PromissoryNote;
import com.ut.tekir.entities.PromissoryToAccountCollectionPayroll;
import com.ut.tekir.entities.PromissoryToAccountPaymentPayroll;
import com.ut.tekir.entities.WorkBunchCurrency;
import com.ut.tekir.framework.BaseConsts;
import com.ut.tekir.framework.CalendarManager;
import com.ut.tekir.framework.OptionManager;
import com.ut.tekir.general.GeneralSuggestion;
import com.ut.tekir.util.Utils;

/**
 *
 * @author haky
 */
@Stateful()
@Name("accountTxnAction")
@Scope(value = ScopeType.APPLICATION)
@AutoCreate
public class AccountTxnActionBean implements AccountTxnAction {

    @Logger
    protected Log log;
    @In
    protected EntityManager entityManager;
    @In
    protected FacesMessages facesMessages;
    @In
    protected CalendarManager calendarManager;
    @In
    OptionManager optionManager;
    @In
    GeneralSuggestion generalSuggestion;
    @In(value="org.jboss.seam.international.messages")
    Map<String, String> messages;
    
    @Create
    public void initComponent() {
    }

    @Remove
    @Destroy
    public void destroy() {
    }

    /**
     * FundTransfer'un her satırını dolaşır ve ilgili cari kayıtları için borç alacak hesaplarını kaydeder.
     *
     */
    public Boolean saveFundTransfer(FundTransfer doc) {

        deleteFundTransfer(doc);


        for (FundTransferItem dci : doc.getItems()) {


            AccountTxn txn = null;

            if (dci.getFromAccount() != null) {
                txn = new AccountTxn();

                txn.setActive(doc.getActive());
                txn.setAccount(dci.getFromAccount());
                txn.setAdverseCode(dci.getToAccount().getCode());
                txn.setAdverseName(dci.getToAccount().getName());
                txn.setAmount(new MoneySet(dci.getAmount()));
                txn.setAction(FinanceAction.Credit);
                txn.setSerial(doc.getSerial());
                txn.setReference(doc.getReference());
                txn.setInfo(dci.getInfo());
                txn.setCode(dci.getLineCode());
                txn.setDate(doc.getDate());
                txn.setDocumentId(doc.getId());
                txn.setDocumentType(DocumentType.FundTransfer);

                
                saveAuditRecords(txn, (AuditBase)doc);

                entityManager.persist(txn);
            }

            if (dci.getToAccount() != null) {
                txn = new AccountTxn();

                txn.setActive(doc.getActive());
                txn.setAccount(dci.getToAccount());
                txn.setAdverseCode(dci.getFromAccount().getCode());
                txn.setAdverseName(dci.getFromAccount().getName());
                txn.setAmount(new MoneySet(dci.getAmount()));
                txn.setAction(FinanceAction.Debit);
                txn.setSerial(doc.getSerial());
                txn.setReference(doc.getReference());
                txn.setInfo(dci.getInfo());
                txn.setCode(dci.getLineCode());
                txn.setDate(doc.getDate());
                txn.setDocumentId(doc.getId());
                txn.setDocumentType(DocumentType.FundTransfer);

                saveAuditRecords(txn, (AuditBase)doc);

                entityManager.persist(txn);
            }

        }
        entityManager.flush();

        return true;
    }

    private void saveAuditRecords( AccountTxn txn, AuditBase entityAuditBase) {
        // audit kayıtlarını atıyoruz.
        AuditBase ab = (AuditBase)txn;
        ab.setCreateUser(entityAuditBase.getCreateUser());
        ab.setCreateDate(entityAuditBase.getCreateDate());
        ab.setUpdateUser(entityAuditBase.getUpdateUser());
        ab.setUpdateDate(entityAuditBase.getUpdateDate());
    }

    public Boolean deleteFundTransfer(FundTransfer doc) {
        deleteDocument(doc.getId(),DocumentType.FundTransfer);

        return true;
    }

    public void savePayment(Payment doc) {
        deletePayment(doc);

        PaymentDocumentTotal totals = PaymentDocumentTotal.instance(doc);
        
        for (Entry<WorkBunchCurrency, MoneySet> item :  totals.getTotals().entrySet()) {
            AccountTxn txn = new AccountTxn();

            txn.setActive(doc.getActive());
            txn.setAccount(doc.getAccount());
            txn.setAdverseCode(doc.getContact().getCode());
        	
			if (doc.getContact().getPerson()) {
				txn.setAdverseName(doc.getContact().getFullname());
			} else {
				txn.setAdverseName(doc.getContact().getCompany());
			}

            txn.setAmount(new MoneySet(item.getValue()));
            txn.setWorkBunch(item.getKey().getBunch());
	        txn.setAction(doc.getAction() == FinanceAction.Debit ? FinanceAction.Credit : FinanceAction.Debit); //Cari Borçlu iken Kasa alacaklıdır.
	        txn.setSerial(doc.getSerial());
	        txn.setReference(doc.getReference());
	        txn.setInfo(doc.getInfo());
	        txn.setCode(doc.getCode());
	        txn.setDate(doc.getDate());            
	        txn.setDocumentId(doc.getId());
	        txn.setProcessType(doc.getProcessType());
            
            if (doc.getAction() == FinanceAction.Debit) {
                txn.setDocumentType(DocumentType.Payment);
            } else {
                txn.setDocumentType(DocumentType.Collection);
            }

            if (doc.getOrderNote() == null) {
            	txn.setTradein(Boolean.FALSE);
            } else {
            	txn.setTradein(Boolean.TRUE);
            }
            
            saveAuditRecords(txn, (AuditBase)doc);

            entityManager.persist(txn);
            entityManager.flush();
        }
    }

    public void saveDocument(ForeignDocumentBase doc) {
        deleteDocument(doc);

    	switch(doc.getDocumentType()) {
    		case AccountCreditNote:
    			saveAccountCreditNote(doc);
    	}
    }
    
    private void saveAccountCreditNote(ForeignDocumentBase doc) {
        for (DocumentItemBase item : doc.getDocumentItemList()) {
            AccountTxn txn = new AccountTxn();

            txn.setActive(doc.getActive());
            txn.setAccount( ((AccountCreditNote)doc).getAccount());

            Contact expenseContact = getGeneralExpenseContact();
            txn.setAdverseCode(expenseContact.getCode());
            txn.setAdverseName(expenseContact.getFullname());
            
            txn.setAmount(new MoneySet(item.getAmount()));
            txn.setAction(FinanceAction.Credit);
            txn.setSerial(doc.getSerial());
            txn.setReference(doc.getReference());
            txn.setInfo( ((AccountCreditNoteDetail)item).getInfo() );
            txn.setCode(doc.getCode());
            txn.setDate(doc.getDate());            
            txn.setDocumentId(doc.getId());
            txn.setDocumentType(doc.getDocumentType());
        	
            saveAuditRecords(txn, (AuditBase)doc);
            
            entityManager.persist(txn);
            entityManager.flush();
        }
    }

    private Contact getGeneralExpenseContact() {
    	
    	Option contactOption = optionManager.getOption("systemSettings.value.GeneralExpenseContact", "GGC");

    	Contact expenseContact = generalSuggestion.findContact(contactOption.getAsString());
    	
    	if (expenseContact == null) {
    		log.info("Yeni genel gider carisi oluşturuluyor...");
    		expenseContact = new Contact();
    		expenseContact.setCode(contactOption.getAsString());
    		expenseContact.setName(messages.get("systemOptions.value.ExpenseContactName"));
    		expenseContact.setFullname(expenseContact.getName());
    		expenseContact.setPerson(Boolean.TRUE);
    		
    		//FIXME:organizasyonu ne yapmalı? Acaba carinin organizasyonunu mu setlemeli?
    		//expenseContact.setOrganization(organization);
    		expenseContact.setIsPublic(Boolean.TRUE);
    		expenseContact.setSsn("10000000000");
    		
    		try {
    			//FIXME:flushlamak gerekebilir.
    			entityManager.persist(expenseContact);
    			entityManager.flush();
    			log.info("Yeni genel gider carisi oluşturuldu. Cari id #0", expenseContact.getId());
    		} catch (Exception e) {
    			log.info("Yeni genel gider carisi oluşturulurken hata meydana geldi. Sebebi #0", e);
    			throw new RuntimeException("Yeni genel gider carisi oluşturulurken hata meydana geldi. Sebebi " + Utils.getExceptionMessage(e));
    		}
    	}
    	return expenseContact;
    }

	private void updateTradeinField(Long aReferenceId, boolean newState) {
    	entityManager.createQuery("update AccountTxn p set p.tradein = :newState where " +
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

    public void deletePayment(Payment doc) {
        if (doc.getAction() == FinanceAction.Debit) {
            deleteDocument(doc.getId(), DocumentType.Payment);
        } else {
            deleteDocument(doc.getId(), DocumentType.Collection);
        }

    }

	public Boolean deleteBankToAccountTransfer(BankToAccountTransfer doc) {
		deleteDocument(doc.getId(), DocumentType.BankToAccountTransfer);
		return true;
	}

	public Boolean deleteAccountToBankTransfer(BankToAccountTransfer doc) {
		deleteDocument(doc.getId(), DocumentType.AccountToBankTransfer);
		return true;
	}

	public void deleteChequeToAccountPaymentPayroll(ChequeToAccountPaymentPayroll doc){

		if(doc.getId() != null){
			deleteDocument(doc.getId(), DocumentType.ChequeAccountPaymentPayroll);
		}
	}

	public void deleteChequeToAccountCollectionPayroll(ChequeToAccountCollectionPayroll doc){

		if(doc.getId() != null){
			deleteDocument(doc.getId(), DocumentType.ChequeAccountCollectionPayroll);
		}
	}
	
	public void deletePromissoryToAccountPaymentPayroll(PromissoryToAccountPaymentPayroll doc){

		if(doc.getId() != null){
			deleteDocument(doc.getId(), DocumentType.PromissoryAccountPaymentPayroll);
		}
	}
	
	public void deletePromissoryToAccountCollectionPayroll(PromissoryToAccountCollectionPayroll doc){

		if(doc.getId() != null){
			deleteDocument(doc.getId(), DocumentType.PromissoryAccountCollectionPayroll);
		}
	}
	
	public void saveChequeToAccountCollectionPayroll(ChequeToAccountCollectionPayroll doc){
		deleteChequeToAccountCollectionPayroll(doc);
		
		AccountTxn accountTxn = new AccountTxn();
		accountTxn.setActive(doc.getActive());
		accountTxn.setAccount(doc.getAccount());
		
		MoneySet tempMoney = new MoneySet();
		tempMoney.setCurrency(BaseConsts.SYSTEM_CURRENCY_CODE);
		tempMoney.setLocalAmount(doc.getTotals().getLocalTotal());
		tempMoney.setValue(doc.getTotals().getLocalTotal());
		accountTxn.setAmount(tempMoney);
		accountTxn.setAdverseName("Kasaya çek ödemesi alındı.");

		accountTxn.setAction(FinanceAction.Debit);
		accountTxn.setSerial(doc.getSerial());
		accountTxn.setReference(doc.getReference());
		accountTxn.setInfo(doc.getInfo());
		accountTxn.setCode(doc.getCode());
		accountTxn.setDate(doc.getDate());
		accountTxn.setDocumentId(doc.getId());
		accountTxn.setDocumentType(DocumentType.ChequeAccountCollectionPayroll);

        saveAuditRecords(accountTxn, (AuditBase)doc);

		entityManager.persist(accountTxn);
		entityManager.flush();
	}
	
	public void saveChequeToAccountPaymentPayroll(ChequeToAccountPaymentPayroll doc){
		
		try {
			deleteChequeToAccountPaymentPayroll(doc);
			
			AccountTxn accountTxn = new AccountTxn();
			accountTxn.setActive(doc.getActive());
			accountTxn.setAccount(doc.getAccount());
			
			MoneySet tempMoney = new MoneySet();
			tempMoney.setCurrency(BaseConsts.SYSTEM_CURRENCY_CODE);
			
			DocumentTotal total = doc.getTotals();
			
			tempMoney.setLocalAmount(total.getLocalTotal());
			tempMoney.setValue(doc.getTotals().getLocalTotal());
			accountTxn.setAmount(tempMoney);
			accountTxn.setAdverseName("Kasadan çek ödemesi yapıldı.");
			
			accountTxn.setAction(FinanceAction.Credit);
			accountTxn.setSerial(doc.getSerial());
			accountTxn.setReference(doc.getReference());
			accountTxn.setInfo(doc.getInfo());
			accountTxn.setCode(doc.getCode());
			accountTxn.setDate(doc.getDate());
			accountTxn.setDocumentId(doc.getId());
			accountTxn.setDocumentType(DocumentType.ChequeAccountPaymentPayroll);
			accountTxn.setProcessType(AdvanceProcessType.Normal);
			
            saveAuditRecords(accountTxn, (AuditBase)doc);

			entityManager.persist(accountTxn);
			entityManager.flush();
		} catch (Exception e) {
			e.printStackTrace();
			facesMessages.add("Hata oluştu!");
		}
	
	}
	
	public Boolean saveBankToAccountTransfer(BankToAccountTransfer doc) {
		deleteBankToAccountTransfer(doc);
		
		AccountTxn txn = new AccountTxn();

        txn.setActive(doc.getActive());
        txn.setAccount(doc.getAccount());
        txn.setAdverseCode(doc.getBankAccount().getAccountNo());
        txn.setWorkBunch(doc.getWorkBunch());

        String s1 = "";
        if (doc.getBankAccount().getBankBranch().getBank().getName().length() >= 23) {
            s1 = doc.getBankAccount().getBankBranch().getBank().getName().substring(0, 23);
        } else {
        	s1 = doc.getBankAccount().getBankBranch().getBank().getName();
        }

        String s2 = "";
        if (doc.getBankAccount().getBankBranch().getName().length() >= 23) {
            s2 = doc.getBankAccount().getBankBranch().getName().substring(0, 23);
        } else {
        	s2 = doc.getBankAccount().getBankBranch().getName();
        }
        
        txn.setAdverseName( s1 + ' ' + s2 );

        txn.setAmount(new MoneySet(doc.getAmount()));
        // transfertype or financeaction ?
        txn.setAction(doc.getAction() == FinanceAction.Credit ? FinanceAction.Credit : FinanceAction.Debit); 
        txn.setSerial(doc.getSerial());
        txn.setReference(doc.getReference());
        txn.setInfo(doc.getInfo());
        txn.setCode(doc.getCode());
        txn.setDate(doc.getDate());
        txn.setDocumentId(doc.getId());
        txn.setDocumentType(DocumentType.BankToAccountTransfer);
        txn.setProcessType(AdvanceProcessType.Normal);
 
        saveAuditRecords(txn, (AuditBase)doc);

        entityManager.persist(txn);
        entityManager.flush();
		return true;
	}    

	public Boolean saveAccountToBankTransfer(BankToAccountTransfer doc) {
		deleteAccountToBankTransfer(doc);

		AccountTxn txn = new AccountTxn();

        txn.setActive(doc.getActive());
        txn.setAccount(doc.getAccount());
        txn.setAdverseCode(doc.getBankAccount().getAccountNo());
        txn.setWorkBunch(doc.getWorkBunch());

        String s1 = new String();
        if (doc.getBankAccount().getBankBranch().getBank().getName().length() >= 23) {
            s1 = doc.getBankAccount().getBankBranch().getBank().getName().substring(0, 23);
        } else s1 = doc.getBankAccount().getBankBranch().getBank().getName();

        String s2 = new String();
        if (doc.getBankAccount().getBankBranch().getName().length() >= 23) {
            s2 = doc.getBankAccount().getBankBranch().getName().substring(0, 23);
        } else s2 = doc.getBankAccount().getBankBranch().getName();

        txn.setAdverseName( s1 + ' ' + s2 );

        txn.setAmount(new MoneySet(doc.getAmount()));
        // transfertype or financeaction ?
        txn.setAction(doc.getAction() == FinanceAction.Credit ? FinanceAction.Credit : FinanceAction.Debit);
        txn.setSerial(doc.getSerial());
        txn.setReference(doc.getReference());
        txn.setInfo(doc.getInfo());
        txn.setCode(doc.getCode());
        txn.setDate(doc.getDate());
        txn.setDocumentId(doc.getId());
        txn.setDocumentType(DocumentType.AccountToBankTransfer);
        txn.setProcessType(AdvanceProcessType.Normal);


        saveAuditRecords(txn, (AuditBase)doc);

        entityManager.persist(txn);
        entityManager.flush();
		return true;
	}

	public Boolean deleteCheque(Cheque cheque, DocumentType documentType) {
		deleteDocument(cheque.getId(), documentType);
		return true;
	}

	public AccountTxn saveCheque(Cheque cheque, Account account, FinanceAction action, DocumentType documentType) {
//		deleteCheque(cheque, documentType);
		
		AccountTxn txn = new AccountTxn();
		
		txn.setAmount(new MoneySet(cheque.getMoney()));
		txn.setAccount(account);
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
		deleteDocument(promissory.getId(), documentType);
		return true;
	}
	
	public AccountTxn savePromissory(PromissoryNote promissory, Account account, FinanceAction action, DocumentType documentType) {
//		deletePromissory(promissory, documentType);
		
		AccountTxn txn = new AccountTxn();
		
		txn.setAmount(new MoneySet(promissory.getMoney()));
		txn.setAccount(account);
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
	
	public void savePromissoryToAccountPaymentPayroll(PromissoryToAccountPaymentPayroll doc){
		
		try {
			deletePromissoryToAccountPaymentPayroll(doc);
			
			AccountTxn accountTxn = new AccountTxn();
			accountTxn.setActive(doc.getActive());
			accountTxn.setAccount(doc.getAccount());
			
			MoneySet tempMoney = new MoneySet();
			tempMoney.setCurrency(BaseConsts.SYSTEM_CURRENCY_CODE);
			tempMoney.setLocalAmount(doc.getTotals().getLocalTotal());
			tempMoney.setValue(doc.getTotals().getLocalTotal());
			accountTxn.setAmount(tempMoney);
			
			accountTxn.setAction(FinanceAction.Credit);
			accountTxn.setSerial(doc.getSerial());
			accountTxn.setReference(doc.getReference());
			accountTxn.setInfo(doc.getInfo());
			accountTxn.setCode(doc.getCode());
			accountTxn.setDate(doc.getDate());
			accountTxn.setDocumentId(doc.getId());
			accountTxn.setDocumentType(DocumentType.PromissoryAccountPaymentPayroll);
			accountTxn.setAdverseName("Kasadan senet ödemesi yapıldı.");
			
	
            saveAuditRecords(accountTxn, (AuditBase)doc);
            
            entityManager.persist(accountTxn);
			entityManager.flush();
		} catch (Exception e) {
			e.printStackTrace();
			facesMessages.add("Hata oluştu!");
		}
	
	}

	public void savePromissoryToAccountCollectionPayroll(PromissoryToAccountCollectionPayroll doc){
	
		try {
			deletePromissoryToAccountCollectionPayroll(doc);
			
			AccountTxn accountTxn = new AccountTxn();
			accountTxn.setActive(doc.getActive());
			accountTxn.setAccount(doc.getAccount());
			
			MoneySet tempMoney = new MoneySet();
			tempMoney.setCurrency(BaseConsts.SYSTEM_CURRENCY_CODE);
			tempMoney.setLocalAmount(doc.getTotals().getLocalTotal());
			tempMoney.setValue(doc.getTotals().getLocalTotal());
			accountTxn.setAmount(tempMoney);
	
			accountTxn.setAction(FinanceAction.Debit);
			accountTxn.setSerial(doc.getSerial());
			accountTxn.setReference(doc.getReference());
			accountTxn.setInfo(doc.getInfo());
			accountTxn.setCode(doc.getCode());
			accountTxn.setDate(doc.getDate());
			accountTxn.setDocumentId(doc.getId());
			accountTxn.setDocumentType(DocumentType.PromissoryAccountCollectionPayroll);
			accountTxn.setAdverseName("Kasaya senet ödemesi alındı.");
			
            saveAuditRecords(accountTxn, (AuditBase)doc);
            
            entityManager.persist(accountTxn);
			entityManager.flush();
		} catch (Exception e) {
			e.printStackTrace();
			facesMessages.add("Hata oluştu!");
		}
	}
	
    protected void deleteDocument(Long docId, DocumentType docType) {
        log.info("Silme islemi yapilacal doc tipi :", docType);
        log.info("Id :", docId);
        entityManager.createQuery("delete AccountTxn where " +
        						  "DocumentId = :docId and " +
        						  "documentType = :docType")
        						  .setParameter("docId", docId)
        						  .setParameter("docType", docType)
        						  .executeUpdate();
    }

    public void deleteDocument(DocumentBase doc) {
    	log.info("Silme islemi yapilacal doc tipi :", doc.getDocumentType());
    	log.info("Id :", doc.getId());
    	entityManager.createQuery("delete AccountTxn where " +
    							  "DocumentId = :docId and " +
						    	  "documentType = :docType")
						    	  .setParameter("docId", doc.getId())
						    	  .setParameter("docType", doc.getDocumentType())
						    	  .executeUpdate();
    }

}
