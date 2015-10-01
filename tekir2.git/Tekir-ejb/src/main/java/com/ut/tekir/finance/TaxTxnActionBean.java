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
import com.ut.tekir.entities.ExpenseNote;
import com.ut.tekir.entities.FinanceAction;
import com.ut.tekir.entities.MoneySet;
import com.ut.tekir.entities.TaxTxn;
import com.ut.tekir.entities.TaxType;
import com.ut.tekir.entities.inv.TekirInvoice;
import com.ut.tekir.entities.inv.TekirInvoiceTaxSummary;

/**
 *
 * @author yigit
 */
@Stateful
@Name("taxTxnAction")
@Scope(ScopeType.APPLICATION)
@AutoCreate
public class TaxTxnActionBean implements TaxTxnAction {

	private static final long serialVersionUID = 1L;
	
	@Logger
    protected Log log;
    @In
    protected EntityManager entityManager;

    @Create
    public void init(){
    }

    private void saveAuditRecords( TaxTxn txn, AuditBase entityAuditBase) {
        // audit kayıtlarını atıyoruz.
        AuditBase ab = (AuditBase)txn;
        ab.setCreateUser(entityAuditBase.getCreateUser());
        ab.setCreateDate(entityAuditBase.getCreateDate());
        ab.setUpdateUser(entityAuditBase.getUpdateUser());
        ab.setUpdateDate(entityAuditBase.getUpdateDate());
    }

    public void createTaxTxnRecordsForInvoice(TekirInvoice doc) {
    	deleteDocument(doc.getId(), doc.getDocumentType());
    	
        TaxTxn txn = null;
        for (TekirInvoiceTaxSummary taxItem: doc.getTaxSummaryList()) {            	
            txn = new TaxTxn();
            if (doc.getDocumentType().equals(DocumentType.PurchaseInvoice)
                    || doc.getDocumentType().equals(DocumentType.PurchaseShipmentInvoice ) ) {
                txn.setAction(FinanceAction.Debit);
            } else if (doc.getDocumentType().equals(DocumentType.SaleInvoice)
                    || doc.getDocumentType().equals(DocumentType.SaleShipmentInvoice )
                    || doc.getDocumentType().equals(DocumentType.RetailSaleShipmentInvoice ) ) {
                txn.setAction(FinanceAction.Credit);
            }
            txn.setActive(doc.getActive());
            txn.setCode(doc.getCode());
            txn.setDate(doc.getDate());
            txn.setDocumentId(doc.getId());
            txn.setDocumentType(doc.getDocumentType());
            txn.setInfo(doc.getInfo());
            txn.setReference(doc.getReference());
            txn.setSerial(doc.getSerial());

            //tutar
            txn.getAmount().setLocalAmount(taxItem.getAmount().getLocalAmount()); 
            txn.getAmount().setValue(taxItem.getAmount().getValue());
            txn.getAmount().setCurrency(taxItem.getAmount().getCurrency());

            //matrah
            txn.getBasisValue().moveFieldsOf(taxItem.getBase());

            txn.setTaxType(taxItem.getType());
            txn.setRate(taxItem.getSourceRate());
            txn.setWorkBunch(doc.getWorkBunch());

            saveAuditRecords(txn, (AuditBase)doc);

            entityManager.persist(txn);
        }
    }

    public void deleteTaxTxnRecordsForInvoice(TekirInvoice doc) {
    	deleteDocument(doc.getId(), doc.getDocumentType());
    }
    
    protected void deleteDocument(Long docId, DocumentType docType) {
        log.debug("Delete DocID : #0", docId);
        entityManager.createQuery("delete TaxTxn where documentId = :docId and documentType = :docType").setParameter("docId", docId).setParameter("docType", docType).executeUpdate();
    }

    public void saveExpenseNote(ExpenseNote expenseNote){
        deleteExpenseNote(expenseNote);

        TaxTxn txn = new TaxTxn();
        txn.setActive(expenseNote.getActive());
        txn.setDocumentType(expenseNote.getDocumentType());

        if (expenseNote.getDocumentType().equals(DocumentType.ExpenseNote)) {
        	txn.setAmount(new MoneySet(expenseNote.getTotalWithhold()));
            txn.setTaxType(TaxType.STOPAJ);
            txn.setAction(FinanceAction.Debit);
        } else {
        	txn.setAmount(new MoneySet(expenseNote.getTotalTaxAmount()));
        	txn.setTaxType(TaxType.VAT);
            txn.setAction(FinanceAction.Credit);
        }
        
        txn.setBasisValue(new MoneySet(expenseNote.getTotalAmount()));
        txn.setCode(expenseNote.getCode());
        txn.setDate(expenseNote.getDate());
        txn.setDocumentId(expenseNote.getId());
        txn.setInfo(expenseNote.getInfo());
        txn.setReference(expenseNote.getReference());
        txn.setSerial(expenseNote.getSerial());
        txn.setWorkBunch(expenseNote.getWorkBunch());
        
        saveAuditRecords(txn, (AuditBase)expenseNote);

        entityManager.persist(txn);
        entityManager.flush();
    }

    public Boolean deleteExpenseNote(ExpenseNote expenseNote) {

		if(expenseNote != null && expenseNote.getId() != null){

			deleteDocument(expenseNote.getId(), expenseNote.getDocumentType());
			return true;
		}
		return false;
	}

    @Remove
    @Destroy
    public void destroy(){
    }

}
