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

import java.util.Map.Entry;

import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.FlushModeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.Conversation;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.log.Log;

import com.ut.tekir.entities.AdvanceProcessType;
import com.ut.tekir.entities.AuditBase;
import com.ut.tekir.entities.BankToContactTransfer;
import com.ut.tekir.entities.Cheque;
import com.ut.tekir.entities.ChequeFromContactPayroll;
import com.ut.tekir.entities.ChequeFromContactPayrollDetail;
import com.ut.tekir.entities.ChequeToContactPayroll;
import com.ut.tekir.entities.Contact;
import com.ut.tekir.entities.DebitCreditNote;
import com.ut.tekir.entities.DebitCreditVirement;
import com.ut.tekir.entities.DebitCreditVirementItem;
import com.ut.tekir.entities.DocumentTotal;
import com.ut.tekir.entities.DocumentType;
import com.ut.tekir.entities.ExpenseNote;
import com.ut.tekir.entities.ExpenseNoteItem;
import com.ut.tekir.entities.FinanceAction;
import com.ut.tekir.entities.FinanceTxn;
import com.ut.tekir.entities.Invoice;
import com.ut.tekir.entities.InvoiceCurrencySummary;
import com.ut.tekir.entities.MoneySet;
import com.ut.tekir.entities.Payment;
import com.ut.tekir.entities.PaymentDocumentTotal;
import com.ut.tekir.entities.PaymentItem;
import com.ut.tekir.entities.PaymentType;
import com.ut.tekir.entities.PromissoryFromContactPayroll;
import com.ut.tekir.entities.PromissoryNote;
import com.ut.tekir.entities.PromissoryToContactPayroll;
import com.ut.tekir.entities.TradeAction;
import com.ut.tekir.entities.WorkBunchCurrency;
import com.ut.tekir.entities.inv.TekirInvoice;
import com.ut.tekir.entities.ord.TekirOrderNote;
import com.ut.tekir.entities.ord.TekirOrderNoteCurrencySummary;
import com.ut.tekir.framework.BaseConsts;
import com.ut.tekir.framework.CalendarManager;
import com.ut.tekir.framework.CurrencyManager;

/**
 *
 * @author haky
 */
@Stateful()
@Name("financeTxnAction")
@Scope(value = ScopeType.APPLICATION)
@AutoCreate
public class FinanceTxnActionBean implements FinanceTxnAction {

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
    @Begin(join=true,flushMode=FlushModeType.MANUAL)
    public void initComponent() {

    }

    private void saveAuditRecords( FinanceTxn txn, AuditBase entityAuditBase) {
        // audit kayıtlarını atıyoruz.
        AuditBase ab = txn;
        ab.setCreateUser(entityAuditBase.getCreateUser());
        ab.setCreateDate(entityAuditBase.getCreateDate());
        ab.setUpdateUser(entityAuditBase.getUpdateUser());
        ab.setUpdateDate(entityAuditBase.getUpdateDate());
    }

    @Remove
    @Destroy
    public void destroy() {
    }

    /**
     * DebitCreditVirement'un her satırını dolaşır ve ilgili cari kayıtları için borç alacak hesaplarını kaydeder.
     *
     */
    public Boolean saveDebitCreditVirement(DebitCreditVirement dcn) {

        deleteDebitCreditVirement(dcn);

        for (DebitCreditVirementItem dci : dcn.getItems()) {

            FinanceTxn txn = null;

            if (dci.getFromContact() != null) {
                txn = new FinanceTxn();

                txn.setActive(dcn.getActive());
                txn.setContact(dci.getFromContact());
                txn.setAdverseCode(dci.getToContact().getCode());
                txn.setAdverseName(dci.getToContact().getName());
                txn.setAmount(new MoneySet(dci.getAmount()));
                txn.setAction(FinanceAction.Credit);
                txn.setSerial(dcn.getSerial());
                txn.setReference(dcn.getReference());
                txn.setInfo(dci.getInfo());
                txn.setCode(dcn.getCode());
                txn.setDate(dcn.getDate());
                txn.setDocumentId(dcn.getId());
                txn.setDocumentType(DocumentType.DebitCreditVirement);
                txn.setProcessType(AdvanceProcessType.Normal);

                saveAuditRecords(txn, dcn);

                entityManager.persist(txn);
            }

            if (dci.getToContact() != null) {
                txn = new FinanceTxn();

                txn.setActive(dcn.getActive());
                txn.setContact(dci.getToContact());
                txn.setAdverseCode(dci.getFromContact().getCode());
                txn.setAdverseName(dci.getFromContact().getName());
                txn.setAmount(new MoneySet(dci.getAmount()));
                txn.setAction(FinanceAction.Debit);
                txn.setSerial(dcn.getSerial());
                txn.setReference(dcn.getReference());
                txn.setInfo(dci.getInfo());
                txn.setCode(dcn.getCode());
                txn.setDate(dcn.getDate());
                txn.setDocumentId(dcn.getId());
                txn.setDocumentType(DocumentType.DebitCreditVirement);
                txn.setProcessType(AdvanceProcessType.Normal);

                saveAuditRecords(txn, dcn);

                entityManager.persist(txn);
            }
        }
        
        entityManager.flush();

        return true;
    }

    public Boolean deleteDebitCreditVirement(DebitCreditVirement dcn) {
    	
    	if(dcn != null && dcn.getId() != null){
    		
    		deleteDocument(dcn.getId(), DocumentType.DebitCreditVirement);
    		return true;
    	}
    	return false;
    }

    public void createFinanceTxnRecordsForPayment(Payment doc) {
        deletePayment( doc );

        PaymentDocumentTotal totals = PaymentDocumentTotal.instance(doc);

        for (Entry<WorkBunchCurrency, MoneySet> item :  totals.getTotals().entrySet()) {
            FinanceTxn txn = new FinanceTxn();

            txn.setActive(doc.getActive());
            txn.setContact(doc.getContact());
            txn.setAdverseCode(doc.getAccount().getCode());
            txn.setAdverseName(doc.getAccount().getName());
            txn.setAmount(item.getValue());
            txn.setWorkBunch(item.getKey().getBunch());
            txn.setProcessType(doc.getProcessType());
            
            
//            currencyManager.setLocalAmountOf(txn.getAmount(), doc.getDate());
            
            txn.setAction(doc.getAction());
            txn.setSerial(doc.getSerial());
            txn.setReference(doc.getReference());
            if (doc.getInfo() != null) {
                txn.setInfo(doc.getInfo());
            }
            txn.setCode(doc.getCode());
            txn.setDate(doc.getDate());
            txn.setDocumentId(doc.getId());
            if (doc.getAction() == FinanceAction.Debit) {
                txn.setDocumentType(DocumentType.Payment);
            } else {
                txn.setDocumentType(DocumentType.Collection);
            }

            saveAuditRecords(txn, doc);

            entityManager.persist(txn);
        }
        
        for (PaymentItem item : doc.getItems()) {
        	if (!item.getLineType().equals(PaymentType.Cash)) {
        		savePaymentItem(item);
        	}
        }
    
    }
    
    public void savePayment(Payment doc) {
    	manualFlush();
        deletePayment( doc );

        PaymentDocumentTotal totals = PaymentDocumentTotal.instance(doc);

        for (Entry<WorkBunchCurrency, MoneySet> item :  totals.getTotals().entrySet()) {
            FinanceTxn txn = new FinanceTxn();

            txn.setActive(doc.getActive());
            txn.setContact(doc.getContact());
            txn.setAdverseCode(doc.getAccount().getCode());
            txn.setAdverseName(doc.getAccount().getName());
            txn.setAmount(item.getValue());
            txn.setWorkBunch(item.getKey().getBunch());
            txn.setAction(doc.getAction());
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
            saveAuditRecords(txn, doc);
            
            entityManager.persist(txn);
        }
        entityManager.flush();

    }

    public void deletePayment(Payment doc) {
    	
    	if(doc != null && doc.getId() != null && doc.getAction() != null){
    		
	        if (doc.getAction() == FinanceAction.Debit) {
	            deleteDocument(doc.getId(), DocumentType.Payment);
	        } else {
	            deleteDocument(doc.getId(), DocumentType.Collection);
	        }
    	}
    }
    
    public void savePaymentItem(PaymentItem item){
    	manualFlush();

    	FinanceTxn txn = new FinanceTxn();
    	txn.setAction(item.getOwner().getAction());
    	txn.setActive(item.getOwner().getActive());
    	txn.setAmount(item.getAmount());
    	txn.setAdverseCode(item.getOwner().getAccount().getCode());
    	txn.setAdverseName(item.getOwner().getAccount().getName());
    	txn.setCode(item.getOwner().getCode());
    	txn.setContact(item.getOwner().getContact());
    	txn.setDate(item.getOwner().getDate());
    	txn.setDocumentId(item.getOwner().getId());
    	if(item.getOwner().getAction() == FinanceAction.Debit){
    		
    		txn.setDocumentType(DocumentType.Payment);
    	}else{
    		
    		txn.setDocumentType(DocumentType.Collection);
    	}
    	
    	txn.setInfo(item.getInfo());
    	txn.setReference(item.getOwner().getReference());
    	txn.setSerial(item.getOwner().getSerial());
    	txn.setWorkBunch(item.getWorkBunch());
    	txn.setProcessType(item.getOwner().getProcessType());

    	saveAuditRecords(txn, item.getOwner());

    	entityManager.persist(txn);
    	entityManager.flush();
    }
    
    public void saveChequeFromContactPayrollItem(ChequeFromContactPayrollDetail detail){
    	   	
    	manualFlush();
    	
    	FinanceTxn txn = new FinanceTxn();
    	txn.setAction(FinanceAction.Credit);
    	txn.setActive(detail.getOwner().getActive());
    	txn.setAdverseCode(detail.getOwner().getAccount().getCode());
    	txn.setAdverseName("Cariden portföye alındı.");
    	txn.setAmount(detail.getCheque().getMoney());
    	txn.setCode(detail.getOwner().getCode());
    	txn.setContact(detail.getOwner().getContact());
    	txn.setDate(detail.getOwner().getDate());
    	txn.setDocumentId(detail.getOwner().getId());
    	txn.setDocumentType(DocumentType.ChequeFromContactPayroll);
    	txn.setInfo(getChequeInfo(detail.getCheque()));
    	txn.setReference(detail.getOwner().getReference());
    	txn.setSerial(detail.getOwner().getSerial());
    	
    	
    	saveAuditRecords(txn, detail.getOwner());
    	
    	entityManager.persist(txn);
    	entityManager.flush();
	
    }
    
    private String getChequeInfo(Cheque cheque){
    	StringBuilder result = new StringBuilder();
    	
    	result.append("Çek No: ").append(cheque.getReferenceNo())
    		  .append(",")
    		  .append(" Banka: ").append(cheque.getBankName()).append("/").append(cheque.getBankBranch())
    		  .append(",")
    		  .append(" Hesap No: ").append(cheque.getAccountNo())
    		  .append(",")
    		  .append(" Sahibi: ").append(cheque.getChequeOwner())
    		  .append(",")
    		  .append(" Vade: ").append(cheque.getMaturityDate())
    		  .append(" ").append(cheque.getInfo());
    	
    	return result.toString();
    	   	  	
    }

    protected void deleteDocument(Long docId, DocumentType docType) {
        log.debug("Delete DocID : #0", docId);
        entityManager.createQuery("delete FinanceTxn where DocumentId = :docId and documentType = :docType").setParameter("docId", docId).setParameter("docType", docType).executeUpdate();
    }

    protected FinanceAction tradeToFinance(TradeAction a) {
        return a == TradeAction.Purchase || a == TradeAction.SaleReturn ? FinanceAction.Credit : FinanceAction.Debit;
    }

    public void createFinanceTxnRecordsForInvoice(TekirInvoice doc) { 
    	log.info("Create financeTxn records for invoice. Invoice id :{0}", doc.getId());
    	deleteDocument(doc.getId(),doc.getDocumentType());

    	FinanceTxn txn = new FinanceTxn();
        txn.setActive(doc.getActive());
        txn.setContact(doc.getContact());
        txn.setAdverseCode(doc.getWarehouse().getCode());
        txn.setAdverseName(doc.getWarehouse().getName());
    	txn.setAmount(doc.getGrandTotal());
        txn.setAction(tradeToFinance(doc.getTradeAction()));
        txn.setSerial(doc.getSerial());
        txn.setReference(doc.getReference());
        if (doc.getInfo() != null) {
            txn.setInfo(doc.getInfo());
        }
        txn.setCode(doc.getCode());
        txn.setDate(doc.getDate());
        txn.setDocumentId(doc.getId());

        txn.setDocumentType(doc.getDocumentType());
        txn.setWorkBunch(doc.getWorkBunch());

        saveAuditRecords(txn, doc);

        entityManager.persist(txn);
    }

    public void createFinanceTxnRecordsForOrderNote(TekirOrderNote doc) { 
    	log.info("Create financeTxn records for order note. Order id :{0}", doc.getId());
    	deleteDocument(doc.getId(),doc.getDocumentType());
    	
    	FinanceTxn txn = null;
    	for (TekirOrderNoteCurrencySummary item : doc.getCurrencySummaryList()) {
    		txn = new FinanceTxn();
    		
    		txn.setActive(doc.getActive());
    		txn.setContact(doc.getContact());
    		
    		txn.setAdverseCode(doc.getWarehouse().getCode());
    		txn.setAdverseName(doc.getWarehouse().getName());
    		
    		txn.setAmount(item.getAmount());
    		txn.setAction(tradeToFinance(doc.getTradeAction()));
    		txn.setSerial(doc.getSerial());
    		txn.setReference(doc.getReference());
    		txn.setInfo(doc.getInfo());
    		txn.setCode(doc.getCode());
    		txn.setDate(doc.getDate());
    		txn.setDocumentId(doc.getId());
    		txn.setWorkBunch(doc.getWorkBunch());
    		
    		txn.setDocumentType(doc.getDocumentType());
    		//FIXME:döküman tiplerine uygun olarak burada gerekli düzenlemeler yapılmalı.
//            if (doc.getTradeAction() == TradeAction.Purchase) {
//                if (doc.getShipmentInvoice()) {
//                    txn.setDocumentType(DocumentType.PurchaseShipmentInvoice);
//                } else {
//                    txn.setDocumentType(DocumentType.PurchaseInvoice);
//                }
//            } else if (doc.getTradeAction() == TradeAction.Sale)  {
//                if (doc.getShipmentInvoice()) {
//                    txn.setDocumentType(DocumentType.SaleShipmentInvoice);
//                } else {
//                    txn.setDocumentType(DocumentType.SaleInvoice);
//                }
//            } else if (doc.getTradeAction() == TradeAction.Transport)  {
//                if (doc.getShipmentInvoice()) {
//                    txn.setDocumentType(DocumentType.TransportShipmentInvoice);
//                } else {
//                    txn.setDocumentType(DocumentType.TransportInvoice);
//                }
//            }
            saveAuditRecords(txn, doc);

    		entityManager.persist(txn);
    	}
    	
    }
    
    public void deleteFinanceTxnRecordsForInvoice(TekirInvoice anInvoice) {
    	deleteDocument(anInvoice.getId(), anInvoice.getDocumentType());
    }

    public void deleteFinanceTxnRecordsForOrderNote(TekirOrderNote doc) {
    	deleteDocument(doc.getId(), doc.getDocumentType());
    }

    public void saveInvoice(Invoice doc) {

        deleteInvoice(doc);

        for (InvoiceCurrencySummary ics : doc.getCurrencySummaries()) {

            FinanceTxn txn = new FinanceTxn();

            txn.setActive(doc.getActive());
            txn.setContact(doc.getContact());
            txn.setAdverseCode(doc.getWarehouse().getCode());
            txn.setAdverseName(doc.getWarehouse().getName());
            txn.setAmount(new MoneySet(ics.getAmount()));
            txn.setAction(tradeToFinance(doc.getAction()));
            txn.setSerial(doc.getSerial());
            txn.setReference(doc.getReference());
            txn.setInfo(doc.getInfo());
            txn.setCode(doc.getCode());
            txn.setDate(doc.getDate());
            txn.setDocumentId(doc.getId());
            
            if (doc.getAction() == TradeAction.Purchase) {
                if (doc.getShipmentInvoice()) {
                    txn.setDocumentType(DocumentType.PurchaseShipmentInvoice);
                } else {
                    txn.setDocumentType(DocumentType.PurchaseInvoice);
                }
            } else if (doc.getAction() == TradeAction.Sale)  {
                if (doc.getShipmentInvoice()) {
                    txn.setDocumentType(DocumentType.SaleShipmentInvoice);
                } else {
                    txn.setDocumentType(DocumentType.SaleInvoice);
                }
            } else if (doc.getAction() == TradeAction.Transport)  {
                if (doc.getShipmentInvoice()) {
                    txn.setDocumentType(DocumentType.TransportShipmentInvoice);
                } else {
                    txn.setDocumentType(DocumentType.TransportInvoice);
                }
            }


            saveAuditRecords(txn, doc);

            entityManager.persist(txn);
        }
        
        entityManager.flush();

    }

    public void deleteInvoice(Invoice doc) {

    	if(doc != null && doc.getId() != null && doc.getShipmentInvoice() != null 
    			&& doc.getAction() != null){
    		
	        if (doc.getAction() == TradeAction.Purchase) {
	            if (doc.getShipmentInvoice()) {
	                deleteDocument(doc.getId(), DocumentType.PurchaseShipmentInvoice);
	            } else {
	                deleteDocument(doc.getId(), DocumentType.PurchaseInvoice);
	            }
	        } else {
	            if (doc.getShipmentInvoice()) {
	                deleteDocument(doc.getId(), DocumentType.SaleShipmentInvoice);
	            } else {
	                deleteDocument(doc.getId(), DocumentType.SaleInvoice);
	            }
	        }
    	}
    }

	public void deleteDebitCreditNote(DebitCreditNote doc) {
		
		if(doc != null && doc.getId() != null){
			
	        if (doc.getAction() == FinanceAction.Debit) {
	            deleteDocument(doc.getId(), DocumentType.DebitCreditNotePayment);
	        } else {
	            deleteDocument(doc.getId(), DocumentType.DebitCreditNoteCollection);
	        }
		}
	}

	public Boolean deleteBankToContactTransfer(BankToContactTransfer doc) {
		
		if(doc != null && doc.getId() != null){
			
			deleteDocument(doc.getId(), DocumentType.BankToContactTransfer);
			return true;
		}
		return false;
	}

	public Boolean saveBankToContactTransfer(BankToContactTransfer doc) {
        deleteBankToContactTransfer(doc);
		FinanceTxn txn = new FinanceTxn();

        txn.setActive(doc.getActive());
        txn.setAmount(new MoneySet(doc.getAmount()));
        txn.setAction(doc.getAction());
        txn.setSerial(doc.getSerial());
        txn.setReference(doc.getReference());
        txn.setInfo(doc.getInfo());
        txn.setCode(doc.getCode());
        txn.setDate(doc.getDate());
        txn.setDocumentId(doc.getId());
        txn.setDocumentType(DocumentType.BankToContactTransfer);
        txn.setContact(doc.getContact());
        txn.setAdverseCode(doc.getBankAccount().getAccountNo());
        txn.setAdverseName(doc.getBankAccount().getName());
        txn.setProcessType(doc.getProcessType());
        txn.setWorkBunch(doc.getWorkBunch());
        
        saveAuditRecords(txn, doc);

        entityManager.persist(txn);
        
        entityManager.flush();
		return true;
	}
	
    public Boolean deleteContactToBankTransfer(BankToContactTransfer doc) {

		if(doc != null && doc.getId() != null){

			deleteDocument(doc.getId(), DocumentType.ContactToBankTransfer);
			return true;
		}
		return false;
	}

	public void saveContactToBankTransfer(BankToContactTransfer doc) {
        deleteContactToBankTransfer(doc);

		FinanceTxn txn = new FinanceTxn();

        txn.setActive(doc.getActive());
        txn.setAmount(new MoneySet(doc.getAmount()));
        txn.setAction(doc.getAction());
        txn.setSerial(doc.getSerial());
        txn.setReference(doc.getReference());
        txn.setInfo(doc.getInfo());
        txn.setCode(doc.getCode());
        txn.setDate(doc.getDate());
        txn.setDocumentId(doc.getId());
        txn.setDocumentType(DocumentType.ContactToBankTransfer);
        txn.setContact(doc.getContact());
        txn.setAdverseCode(doc.getBankAccount().getAccountNo());
        txn.setAdverseName(doc.getBankAccount().getName());
        txn.setProcessType(doc.getProcessType());
        txn.setWorkBunch(doc.getWorkBunch());

        saveAuditRecords(txn, doc);

        entityManager.persist(txn);

        entityManager.flush();
	}

	public void saveDebitCreditNote(DebitCreditNote doc) {
		deleteDebitCreditNote( doc );

        DocumentTotal totals = doc.getTotals();

        for (MoneySet item : totals.getTotals()) {
            FinanceTxn txn = new FinanceTxn();

            txn.setActive(doc.getActive());
            txn.setContact(doc.getContact());
            txn.setAmount(new MoneySet(item));
            txn.setAction(doc.getAction());
            txn.setSerial(doc.getSerial());
            txn.setReference(doc.getReference());
            txn.setInfo(doc.getInfo());
            txn.setCode(doc.getCode());
            txn.setDate(doc.getDate());
            txn.setDocumentId(doc.getId());

            if (doc.getAction() == FinanceAction.Debit) {
                txn.setDocumentType(DocumentType.DebitCreditNotePayment);
            } else {
                txn.setDocumentType(DocumentType.DebitCreditNoteCollection);
            }

            saveAuditRecords(txn, doc);

            entityManager.persist(txn);
        }
        
        entityManager.flush();
		
	}
	
	public Boolean deleteCheque(Cheque cheque, DocumentType documentType) {
		
		if(cheque != null && cheque.getId() != null && documentType != null){
			
			deleteDocument(cheque.getId(), documentType);
			return true;
		}
		return false;
	}

	public FinanceTxn saveCheque(Cheque cheque, Contact contact, FinanceAction action, DocumentType documentType) {
		deleteCheque(cheque, documentType);
		
		FinanceTxn txn = new FinanceTxn();
		
		txn.setAmount(new MoneySet(cheque.getMoney()));
		txn.setContact(contact);
		txn.setCode(cheque.getReferenceNo());
		txn.setDate(cheque.getEntryDate());
		txn.setDocumentId(cheque.getId());
		txn.setAction(action);
		txn.setDocumentType(documentType);
		txn.setInfo(cheque.getInfo());
		txn.setReference(cheque.getReferenceNo());
		txn.setSerial(cheque.getSerialNo());

		
		entityManager.persist(txn);
	    entityManager.flush();
		return txn;
	}
	
	public Boolean deletePromissory(PromissoryNote promissory, DocumentType documentType) {
		
		if(promissory != null && promissory.getId() != null && documentType != null){
			
			deleteDocument(promissory.getId(), documentType);
			return true;
		}
		return false;
	}
	
	public FinanceTxn savePromissory(PromissoryNote promissory, Contact contact, FinanceAction action, DocumentType documentType) {
		deletePromissory(promissory, documentType);
		
		FinanceTxn txn = new FinanceTxn();
		
		txn.setAmount(new MoneySet(promissory.getMoney()));
		txn.setContact(contact);
		txn.setCode(promissory.getReferenceNo());
		txn.setDate(promissory.getEntryDate());
		txn.setDocumentId(promissory.getId());
		txn.setAction(action);
		txn.setDocumentType(documentType);
		txn.setInfo(promissory.getInfo());
		txn.setReference(promissory.getReferenceNo());
		txn.setSerial(promissory.getSerialNo());
		
		entityManager.persist(txn);
		entityManager.flush();
		
		return txn;
	}

	public void saveChequeFromContactPayroll(ChequeFromContactPayroll doc){
		deleteChequeFromContactPayroll(doc);
		
		FinanceTxn txn = new FinanceTxn();

		MoneySet tempMoneySet = new MoneySet();
		tempMoneySet.setCurrency(BaseConsts.SYSTEM_CURRENCY_CODE);
		tempMoneySet.setLocalAmount(doc.getTotals().getLocalTotal());
		tempMoneySet.setValue(doc.getTotals().getLocalTotal());
		txn.setAmount(tempMoneySet);
		txn.setAdverseName("Cariden portföye alındı.");

		txn.setActive(doc.getActive());
		txn.setContact(doc.getContact());
		txn.setAction(FinanceAction.Credit);
		txn.setSerial(doc.getSerial());
		txn.setReference(doc.getReference());
		txn.setInfo(doc.getInfo());
		txn.setCode(doc.getCode());
		txn.setDate(doc.getDate());
		txn.setDocumentType(DocumentType.ChequeFromContactPayroll);
		txn.setDocumentId(doc.getId());

        saveAuditRecords(txn, doc);

		entityManager.persist(txn);
		entityManager.flush();
	}	

	public void saveChequeToContactPayroll(ChequeToContactPayroll doc){
		deleteChequeToContactPayroll(doc);
		
		DocumentTotal totals = doc.getTotals();

        for (MoneySet item : totals.getTotals()) {
            FinanceTxn txn = new FinanceTxn();

            txn.setActive(doc.getActive());
            txn.setContact(doc.getContact());
            txn.setAmount(new MoneySet(item));
            txn.setAction(FinanceAction.Debit);
            txn.setSerial(doc.getSerial());
            txn.setReference(doc.getReference());
            txn.setInfo(doc.getInfo());
            txn.setCode(doc.getCode());
            txn.setDate(doc.getDate());
            txn.setDocumentType(DocumentType.ChequePaymentPayroll);
            txn.setDocumentId(doc.getId());
            txn.setAdverseName("Cariye portföden çıkarıldı.");
            

            saveAuditRecords(txn, doc);

            entityManager.persist(txn);
        }
        
        entityManager.flush();
	}

	public void deleteChequeToContactPayroll(ChequeToContactPayroll doc){
		
		if(doc != null && doc.getId() != null){
			
			deleteDocument(doc.getId(), DocumentType.ChequePaymentPayroll);
		}
	}
	
	public void deleteChequeFromContactPayroll(ChequeFromContactPayroll doc){
		
		if(doc != null && doc.getId() != null){
			
			deleteDocument(doc.getId(), DocumentType.ChequeFromContactPayroll);
		}
	}
	
	public void savePromissoryToContactPayroll(PromissoryToContactPayroll doc){
		deletePromissoryToContactPayroll(doc);
		
		DocumentTotal totals = doc.getTotals();

        for (MoneySet item : totals.getTotals()) {
            FinanceTxn txn = new FinanceTxn();

            txn.setActive(doc.getActive());
            txn.setContact(doc.getContact());
            txn.setAmount(new MoneySet(item));
            txn.setAction(FinanceAction.Debit);
            txn.setSerial(doc.getSerial());
            txn.setReference(doc.getReference());
            txn.setInfo(doc.getInfo());
            txn.setCode(doc.getCode());
            txn.setDate(doc.getDate());
            txn.setDocumentType(DocumentType.PromissoryPaymentPayroll);
            txn.setDocumentId(doc.getId());
            txn.setAdverseName("Cariye portföyden çıkarıldı.");
            

            saveAuditRecords(txn, doc);

            entityManager.persist(txn);
        }
        
        entityManager.flush();

	}
	public void deletePromissoryToContactPayroll(PromissoryToContactPayroll doc){
		
		if(doc != null && doc.getId() != null){
				
			deleteDocument(doc.getId(), DocumentType.PromissoryPaymentPayroll);
		}
	}
	
	public void savePromissoryFromContactPayroll(PromissoryFromContactPayroll doc){
		deletePromissoryFromContactPayroll(doc);
		
		FinanceTxn txn = new FinanceTxn();

		MoneySet tempMoneySet = new MoneySet();
		tempMoneySet.setCurrency(BaseConsts.SYSTEM_CURRENCY_CODE);
		tempMoneySet.setLocalAmount(doc.getTotals().getLocalTotal());
		tempMoneySet.setValue(doc.getTotals().getLocalTotal());
		txn.setAmount(tempMoneySet);

		txn.setActive(doc.getActive());
		txn.setContact(doc.getContact());
		txn.setAmount(tempMoneySet);
		txn.setAction(FinanceAction.Credit);
		txn.setSerial(doc.getSerial());
		txn.setReference(doc.getReference());
		txn.setInfo(doc.getInfo());
		txn.setCode(doc.getCode());
		txn.setDate(doc.getDate());
		txn.setDocumentType(DocumentType.PromissoryFromContactPayroll);
		txn.setDocumentId(doc.getId());
		txn.setAdverseName("Cariden portföye alındı.");

		
        saveAuditRecords(txn, doc);

		entityManager.persist(txn);
		entityManager.flush();
	}


    public void deleteExpenseNote(ExpenseNote expenseNote) {
		deleteDocument(expenseNote.getId(), expenseNote.getDocumentType());
	}

    // TODO: adverse kayıtları atılmıyor. gerekli ise eklenmeli!
	public void saveExpenseNote(ExpenseNote expenseNote) {
		deleteExpenseNote(expenseNote);

        for(ExpenseNoteItem item : expenseNote.getItems()){
            FinanceTxn txn = new FinanceTxn();

            // cariye net miktar ödeme yapılacak...
            txn.setAmount(new MoneySet(item.getNetAmount()));
            txn.setContact(expenseNote.getContact());
            txn.setCode(item.getLineCode());
            txn.setDate(expenseNote.getDate());
            txn.setDocumentId(expenseNote.getId());
            txn.setAction(FinanceAction.Credit);
            txn.setDocumentType(expenseNote.getDocumentType());
            txn.setInfo(item.getInfo());
            txn.setReference(expenseNote.getReference());
            txn.setSerial(expenseNote.getSerial());
            txn.setAdverseCode(expenseNote.getWarehouse().getCode());
            txn.setAdverseName(expenseNote.getWarehouse().getName());
            txn.setWorkBunch(expenseNote.getWorkBunch());
            
            saveAuditRecords(txn, expenseNote);

            entityManager.persist(txn);
            entityManager.flush();
        }
	}
	
	public void deleteChequeFromContactPayrollItem(ChequeFromContactPayroll doc){
		if(doc != null && doc.getId() != null) {

			deleteDocument(doc.getId(),DocumentType.ChequeFromContactPayroll);
		}
		
	}
	
	public void deletePromissoryFromContactPayroll(PromissoryFromContactPayroll doc){
		
		if(doc != null && doc.getId() != null){
			
			deleteDocument(doc.getId(), DocumentType.PromissoryFromContactPayroll);
		}
	}

	public void manualFlush() {
		Conversation.instance().changeFlushMode(org.jboss.seam.annotations.FlushModeType.MANUAL);
	}


}
