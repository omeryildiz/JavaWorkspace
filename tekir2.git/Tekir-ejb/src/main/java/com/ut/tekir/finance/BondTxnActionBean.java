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

import com.ut.tekir.entities.AuditBase;
import com.ut.tekir.entities.BondPurchaseSale;
import com.ut.tekir.entities.BondPurchaseSaleDetail;
import com.ut.tekir.entities.DocumentType;
import com.ut.tekir.entities.PortfolioToPortfolioTransfer;
import com.ut.tekir.entities.PortfolioToPortfolioTransferItem;
import com.ut.tekir.entities.BondTxn;
import com.ut.tekir.entities.FinanceAction;
import com.ut.tekir.framework.CalendarManager;
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

/**
 *
 * @author bilge
 */
@Stateful()
@Name("bondTxnAction")
@Scope(value = ScopeType.APPLICATION)
@AutoCreate
public class BondTxnActionBean implements BondTxnAction {

    @Logger
    protected Log log;
    @In
    protected EntityManager entityManager;
    @In
    protected FacesMessages facesMessages;
    @In
    protected CalendarManager calendarManager;

    @Create
    public void initComponent() {

    }

    private void saveAuditRecords( BondTxn txn, AuditBase entityAuditBase) {
        // audit kayıtlarını atıyoruz.
        AuditBase ab = (AuditBase)txn;
        ab.setCreateUser(entityAuditBase.getCreateUser());
        ab.setCreateDate(entityAuditBase.getCreateDate());
        ab.setUpdateUser(entityAuditBase.getUpdateUser());
        ab.setUpdateDate(entityAuditBase.getUpdateDate());
    }

    @Remove
    @Destroy
    public void destroy() {
    }

    protected void deleteDocument(Long docId, DocumentType docType) {
        entityManager.createQuery("delete BondTxn where DocumentId = :docId and documentType = :docType").setParameter("docId", docId).setParameter("docType", docType).executeUpdate();
    }

    /**
     * PortfolioToPortfolioTransfer'un her satırını dolaşır ve ilgili borç alacak hesaplarını kaydeder.
     *
     */
    public Boolean savePortfolioToPortfolioTransfer(PortfolioToPortfolioTransfer doc) {

        deletePortfolioToPortfolioTransfer(doc);


        for (PortfolioToPortfolioTransferItem dci : doc.getItems()) {


            BondTxn txn = null;

            txn = new BondTxn();
            txn.setPortfolio(doc.getFromPortfolio());
            txn.setSecurity(dci.getSecurity());
            txn.setNominal(dci.getNominal());
            txn.setAction(FinanceAction.Debit);

            txn.setActive(doc.getActive());
            txn.setSerial(doc.getSerial());
            txn.setReference(doc.getReference());
            txn.setInfo(dci.getInfo());
            txn.setCode(dci.getLineCode());
            txn.setDate(doc.getDate());
            txn.setDocumentId(doc.getId());
            txn.setDocumentType(DocumentType.PorfolioToPortfolioTransfer);


            saveAuditRecords(txn, (AuditBase)doc);

            entityManager.persist(txn);

            txn = new BondTxn();
            txn.setPortfolio(doc.getToPortfolio());
            txn.setSecurity(dci.getSecurity());
            txn.setNominal(dci.getNominal());
            txn.setAction(FinanceAction.Credit);

            txn.setActive(doc.getActive());
            txn.setSerial(doc.getSerial());
            txn.setReference(doc.getReference());
            txn.setInfo(dci.getInfo());
            txn.setCode(dci.getLineCode());
            txn.setDate(doc.getDate());
            txn.setDocumentId(doc.getId());
            txn.setDocumentType(DocumentType.PorfolioToPortfolioTransfer);

            saveAuditRecords(txn, (AuditBase)doc);

            entityManager.persist(txn);
            
        }
        
        entityManager.flush();
        
        return true;
    }

    public Boolean deletePortfolioToPortfolioTransfer(PortfolioToPortfolioTransfer doc) {
    	
    	if(doc != null && doc.getId() != null){
    		
    		deleteDocument(doc.getId(), DocumentType.PorfolioToPortfolioTransfer);
    		return true;
    	}
    	return false;
    }
    
	public Boolean deleteBondPurchase(BondPurchaseSale doc) {
		
		if(doc != null && doc.getId() != null){
			
			deleteDocument(doc.getId(), DocumentType.BondPurchase);
			return true;
		}
		return false;
	}

	public Boolean deleteBondSale(BondPurchaseSale doc) {
		
		if(doc != null && doc.getId() != null){
			
			deleteDocument(doc.getId(), DocumentType.BondSale);
			return true;
		}
		return false;
	}

	public Boolean saveBondPurchase(BondPurchaseSale doc) {
		
		deleteBondPurchase(doc);
		
		try {
			
			for(BondPurchaseSaleDetail bondDetail:doc.getDetails()){
				
				BondTxn txn = new BondTxn();
				txn.setPortfolio(bondDetail.getPortfolio());
				txn.setSecurity(doc.getSecurity());
				txn.setNominal(doc.getNominal());
				// FIXME: Debit ? Credit ? Emin degilim.
				txn.setAction(FinanceAction.Debit);
				txn.setActive(doc.getActive());
				txn.setSerial(doc.getSerial());
				txn.setReference(doc.getReference());
				txn.setInfo(doc.getInfo());
				txn.setCode(doc.getCode());
				txn.setDate(doc.getDate());
				txn.setDocumentId(doc.getId());
				txn.setDocumentType(DocumentType.BondPurchase);
				
	            saveAuditRecords(txn, (AuditBase)doc);

				entityManager.persist(txn);
				entityManager.flush();
			}

		} catch (Exception e) {
			facesMessages.add("Bono detaylarını kaydederken hata oldu!");
			log.debug("BondTxnAction", e);
		}

        return true;
	}

	public Boolean saveBondSale(BondPurchaseSale doc) {
		deleteBondSale(doc);
		
		try {
			
			for(BondPurchaseSaleDetail bondDetail : doc.getDetails()){
				
				BondTxn txn = new BondTxn();
				txn.setPortfolio(bondDetail.getPortfolio());
				txn.setSecurity(doc.getSecurity());
				txn.setNominal(bondDetail.getNominal());
				// FIXME: Debit ? Credit ? Emin degilim.        
				txn.setAction(FinanceAction.Credit);
				txn.setActive(doc.getActive());
				txn.setSerial(doc.getSerial());
				txn.setReference(doc.getReference());
				txn.setInfo(doc.getInfo());
				txn.setCode(doc.getCode());
				txn.setDate(doc.getDate());
				txn.setDocumentId(doc.getId());
				txn.setDocumentType(DocumentType.BondSale);
	
	            saveAuditRecords(txn, (AuditBase)doc);

				entityManager.persist(txn);
				entityManager.flush();
			}

		} catch (Exception e) {
			facesMessages.add("Bono detaylarını kaydederken hata oldu!");
			log.debug("BondTxnAction", e);
		}

		return true;
	}


}
