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

package com.ut.tekir.stock;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
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

import com.ut.tekir.entities.AuditBase;
import com.ut.tekir.entities.CountNote;
import com.ut.tekir.entities.DocumentType;
import com.ut.tekir.entities.ExpenseNote;
import com.ut.tekir.entities.ExpenseNoteItem;
import com.ut.tekir.entities.FinanceAction;
import com.ut.tekir.entities.Money;
import com.ut.tekir.entities.MoneySet;
import com.ut.tekir.entities.ProductTransfer;
import com.ut.tekir.entities.ProductTransferItem;
import com.ut.tekir.entities.ProductTxn;
import com.ut.tekir.entities.ProductVirement;
import com.ut.tekir.entities.ProductVirementItem;
import com.ut.tekir.entities.Quantity;
import com.ut.tekir.entities.ShipmentItem;
import com.ut.tekir.entities.ShipmentNote;
import com.ut.tekir.entities.SpendingNote;
import com.ut.tekir.entities.SpendingNoteItem;
import com.ut.tekir.entities.TradeAction;
import com.ut.tekir.entities.User;
import com.ut.tekir.entities.ord.TekirOrderNote;
import com.ut.tekir.entities.ord.TekirOrderNoteDetail;
import com.ut.tekir.entities.shp.TekirShipmentNote;
import com.ut.tekir.entities.shp.TekirShipmentNoteDetail;
import com.ut.tekir.framework.CalendarManager;

/**
 *
 * @author haky
 */
@Stateful()
@Name("productTxnAction")
@Scope(value = ScopeType.APPLICATION)
@AutoCreate
public class ProductTxnActionBean implements ProductTxnAction {

	@Logger
    protected Log log;
    @In
    protected EntityManager entityManager;
    @In
    protected FacesMessages facesMessages;
    @In
    protected CalendarManager calendarManager;
	@In
	User activeUser;
	@In
	Map<Object, String> messages;
	
	@Create
    public void initComponent() {
    }

    @Remove
    @Destroy
    public void destroy() {
    }

    private void saveAuditRecords( ProductTxn txn, AuditBase entityAuditBase) {
        // audit kayıtlarını atıyoruz.
        AuditBase ab = (AuditBase)txn;
        ab.setCreateUser(entityAuditBase.getCreateUser());
        ab.setCreateDate(entityAuditBase.getCreateDate());
        ab.setUpdateUser(entityAuditBase.getUpdateUser());
        ab.setUpdateDate(entityAuditBase.getUpdateDate());
    }

    protected void deleteDocument(Long docId, DocumentType docType) {
        entityManager.createQuery("delete ProductTxn where " +
        						  "documentId = :docId and documentType = :docType")
			        			 .setParameter("docId", docId)
			        			 .setParameter("docType", docType)
			        			 .executeUpdate();
    }

    /**
     * ProductTransfer'un her satırını dolaşır ve ilgili cari kayıtları için borç alacak hesaplarını kaydeder.
     *
     */
    public void saveProductTransfer(ProductTransfer doc) {
        deleteProductTransfer(doc);

        for (ProductTransferItem dci : doc.getItems()) {
        	createProductTxn(dci, TradeAction.Sale, DocumentType.ProductTransfer);
        	createProductTxn(dci, TradeAction.Purchase, DocumentType.ProductTransfer);
        }
        entityManager.flush();
    }

    private void createProductTxn(ProductTransferItem item, TradeAction action, DocumentType documentType) {
    	ProductTxn result = new ProductTxn();

        if (action.equals(TradeAction.Sale)) {
            result.setWarehouse(item.getOwner().getFromWarehouse());
            result.setAdverseCode(item.getOwner().getToWarehouse().getCode());
            result.setAdverseName(item.getOwner().getToWarehouse().getName());

        } else {
            result.setWarehouse(item.getOwner().getToWarehouse());
            result.setAdverseCode(item.getOwner().getFromWarehouse().getCode());
            result.setAdverseName(item.getOwner().getFromWarehouse().getName());
        }

    	result.moveFieldsOfProductItem(item);
        result.setAction(action);
        result.setDocumentType(documentType);
        result.setFinanceAction(fixFinanceAction(result.getAction()));

        saveAuditRecords(result, (AuditBase)item.getOwner());

        entityManager.persist(result);
    }
    
    public Boolean deleteProductTransfer(ProductTransfer doc) {
        deleteDocument(doc.getId(), DocumentType.ProductTransfer);

        return true;
    }

    //FIXME: product txn kayıtlarını atacak metotlar daha generic bir yapıya 
    //kavuşturulmalı. Her bir döküman türü için ayrı ayrı implemente etmek 
    //çok saçma!
    /**
     * Verilen irsaliye için gereken product txn kayıtlarını oluşturur.
     * @param aShipmentNote
     */
    public void createProductTxnRecordsForShipment(TekirShipmentNote doc) {
    	deleteDocument(doc.getId(),doc.getDocumentType());

    	ProductTxn txn = null;
    	for (TekirShipmentNoteDetail item : doc.getProductItems()) {
    		txn = new ProductTxn();
            txn.setWarehouse(doc.getWarehouse());
            txn.setProduct(item.getProduct());
            txn.setProductType(item.getProduct().getProductType());

            txn.setAdverseCode(doc.getContact().getCode());
            if (doc.getContact().getPerson()) {
                txn.setAdverseName(doc.getContact().getFullname());
            } else txn.setAdverseName(doc.getContact().getCompany());
            
            txn.setQuantity(item.getQuantity());

            txn.setAction(doc.getTradeAction());

            //txn.setUnitPrice(item.getTaxExcludedTotal());
            txn.setUnitPrice(new MoneySet(item.getTotalAmount())); //vergi dahil birim fiyat
            
            txn.setActive(doc.getActive());
            txn.setSerial(doc.getSerial());
            txn.setReference(doc.getReference());
            txn.setInfo(item.getInfo());
            //txn.setCode(item.getLineCode());
            txn.setCode(doc.getCode());
            txn.setDate(doc.getDate());
            txn.setDocumentId(doc.getId());
            
//            if (doc.getTradeAction() == TradeAction.Purchase
//                    || doc.getTradeAction() == TradeAction.SaleReturn ) {
//                txn.setDocumentType(DocumentType.PurchaseShipmentNote);
//            } else if (doc.getTradeAction() == TradeAction.Sale
//                    || doc.getTradeAction() == TradeAction.PurchseReturn ) {
//                txn.setDocumentType(DocumentType.SaleShipmentNote);
//            }
            txn.setDocumentType(doc.getDocumentType());  
            txn.setContact(doc.getContact());
            txn.setFinanceAction(fixFinanceAction(txn.getAction()));
            txn.setWorkBunch(doc.getWorkBunch());
            
            saveAuditRecords(txn, (AuditBase)doc);

            entityManager.persist(txn);


            if (item.getReferenceDocumentId() != null && 
            		(item.getReferenceDocumentType().equals(DocumentType.SaleOrder) || 
            		 item.getReferenceDocumentType().equals(DocumentType.PurchaseOrder)) ) {
            	txn = new ProductTxn();
            	txn.setWarehouse(doc.getWarehouse());
            	txn.setProduct(item.getProduct());
            	txn.setProductType(item.getProduct().getProductType());
            	
            	txn.setAdverseCode(doc.getContact().getCode());
            	//txn.setAdverseName(doc.getContact().getFullname());
                if (doc.getContact().getPerson()) {
                    txn.setAdverseName(doc.getContact().getFullname());
                } else txn.setAdverseName(doc.getContact().getCompany());
            	
            	txn.setQuantity(item.getQuantity());
            	
            	txn.setAction(TradeAction.Delivered);
            	
                //vergili birim fiyati
                txn.setUnitPrice(new MoneySet(item.getTotalAmount()));
            
            	txn.setActive(doc.getActive());
            	txn.setSerial(doc.getSerial());
            	txn.setReference(doc.getReference());
            	txn.setInfo(item.getInfo());
            	txn.setCode(doc.getCode());
            	txn.setDate(doc.getDate());
            	txn.setDocumentId(doc.getId());
            	
//            	if (doc.getTradeAction() == TradeAction.Purchase) {
//            		txn.setDocumentType(DocumentType.PurchaseShipmentNote);
//            	} else {
//            		txn.setDocumentType(DocumentType.SaleShipmentNote);
//                    txn.setContact(doc.getContact());
//            	}
                txn.setDocumentType(doc.getDocumentType());
                txn.setContact(doc.getContact());
            	txn.setFinanceAction(fixFinanceAction(txn.getAction()));
            	txn.setWorkBunch(doc.getWorkBunch());
            	
                saveAuditRecords(txn, (AuditBase)doc);

            	entityManager.persist(txn);
            }
    	}
    }

    public void deleteProductTxnRecordsForOrderNote(TekirOrderNote doc) {
    	deleteDocument(doc.getId(),doc.getDocumentType());
    }

    public void createProductTxnRecordsForOrderNote(TekirOrderNote doc) {
    	deleteDocument(doc.getId(), doc.getDocumentType());
    	
    	ProductTxn txn = null;
    	for (TekirOrderNoteDetail item : doc.getProductItems()) {
    		txn = new ProductTxn();
            txn.setWarehouse(doc.getWarehouse());
            txn.setProduct(item.getProduct());
            txn.setProductType(item.getProductType());

            txn.setAdverseCode(doc.getContact().getCode());
            
            if (doc.getContact().getPerson()) {
                    txn.setAdverseName(doc.getContact().getFullname());
                } else txn.setAdverseName(doc.getContact().getCompany());
            
            txn.setQuantity(item.getQuantity());
            
            txn.setAction(TradeAction.Reserved);

            if (doc.getContact() != null) {
            	txn.setContact(doc.getContact());
            }
            //FIXME:acaba birim fiyat mı yoksa satır toplam tutar mı yazılmalı?
            // birim fiyat olarak kdv'li tutar yazilmali.
            txn.setUnitPrice(new MoneySet(item.getTotalAmount()));

            txn.setActive(doc.getActive());
            txn.setSerial(doc.getSerial());
            txn.setReference(doc.getReference());
            txn.setInfo(item.getInfo());
            txn.setCode(doc.getCode());
            txn.setDate(doc.getDate());
            txn.setDocumentId(doc.getId());
            txn.setWorkBunch(doc.getWorkBunch());
            if (doc.getTradeAction() == TradeAction.Purchase) {
                txn.setDocumentType(DocumentType.PurchaseOrder); //alis siparis fisi : 71
            } else {
                txn.setDocumentType(DocumentType.SaleOrder); //satis siparis fisi : 70
                txn.setContact(doc.getContact());
            }
            //FIXME: satış siparişinde rezerve eder, peki ya alış siparişinde?
            
            if (doc.getDocumentType().equals(DocumentType.SaleOrder)) {
            	txn.setFinanceAction(FinanceAction.Credit);
            } else if (doc.getDocumentType().equals(DocumentType.PurchaseOrder)) {
            	txn.setFinanceAction(FinanceAction.Debit);
            }

            saveAuditRecords(txn, (AuditBase)doc);

            entityManager.persist(txn);
    	}
    	
    }
    
    public void deleteProductTxnRecordsForShipmentNote(TekirShipmentNote doc) {
    	deleteDocument(doc.getId(),doc.getDocumentType());
    }
    
    //FIXME:Eski entity ve sınıflarda kullanılan metotlar silinecekler.
    public void saveShipmentNote(ShipmentNote doc) {
        deleteShipmentNote(doc);


        for (ShipmentItem dci : doc.getItems()) {


            ProductTxn txn = null;

            txn = new ProductTxn();
            txn.setWarehouse(doc.getWarehouse());
            txn.setProduct(dci.getProduct());
            txn.setAdverseCode(doc.getContact().getCode());

            if (doc.getContact().getPerson()) {
                    txn.setAdverseName(doc.getContact().getFullname());
                } else txn.setAdverseName(doc.getContact().getCompany());

            txn.setQuantity(new Quantity(dci.getQuantity()));
            txn.setUnitPrice(new MoneySet(dci.getTotalAmount())); // .getUnitPrice()));
            txn.setAction(doc.getAction());
            txn.setProductType(dci.getProduct().getProductType());

            txn.setActive(doc.getActive());
            txn.setSerial(doc.getSerial());
            txn.setReference(doc.getReference());
            txn.setInfo(dci.getInfo());
            txn.setCode(doc.getCode());
            txn.setDate(doc.getDate());
            txn.setDocumentId(doc.getId());
            if (doc.getAction() == TradeAction.Purchase) {
                txn.setDocumentType(DocumentType.PurchaseShipmentNote);
            } else {
                txn.setDocumentType(DocumentType.SaleShipmentNote);
                txn.setContact(doc.getContact());
            }
            txn.setFinanceAction(fixFinanceAction(txn.getAction()));

            saveAuditRecords(txn, (AuditBase)doc);

            entityManager.persist(txn);
            entityManager.flush();

        }

    }

    public void deleteShipmentNote(ShipmentNote doc) {
        if (doc.getAction() == TradeAction.Purchase) {
            deleteDocument(doc.getId(), DocumentType.PurchaseShipmentNote);

        } else {
            deleteDocument(doc.getId(), DocumentType.SaleShipmentNote);

        }
    }
    
    public void saveSpendingNote(SpendingNote doc) {
        deleteSpendingNote(doc);


        for (SpendingNoteItem sni : doc.getItems()) {


            ProductTxn txn = null;

            txn = new ProductTxn();
            txn.setWarehouse(doc.getWarehouse());
            txn.setProduct(sni.getProduct());
            txn.setQuantity(new Quantity(sni.getQuantity()));
            //FIXME: buradaki fiyat vergili birim fiyat (.getTotalAmount()) olarak duzeltilmeli, ama modelde yok
            txn.setUnitPrice(new MoneySet(sni.getUnitPrice()));
            txn.setAction(doc.getAction());
            txn.setProductType(sni.getProduct().getProductType());

            txn.setActive(doc.getActive());
            txn.setSerial(doc.getSerial());
            txn.setReference(doc.getReference());
            txn.setInfo(sni.getInfo());
            txn.setCode(doc.getCode());
            txn.setDate(doc.getDate());
            txn.setDocumentId(doc.getId());
            txn.setDocumentType(DocumentType.SpendingNote);
            txn.setFinanceAction(fixFinanceAction(txn.getAction()));

            saveAuditRecords(txn, (AuditBase)doc);

            entityManager.persist(txn);
            entityManager.flush();

        }

    }

    public void deleteSpendingNote(SpendingNote doc) {
            deleteDocument(doc.getId(), DocumentType.SpendingNote);

    }
    
    public Boolean saveProductVirement(ProductVirement doc) {

        deleteProductVirement(doc);

        for (ProductVirementItem dci : doc.getItems()) {


            ProductTxn txn = null;

            txn = new ProductTxn();
            txn.setWarehouse(doc.getWarehouse());
            txn.setProduct(dci.getFromProduct());
            txn.setAdverseCode(dci.getToProduct().getCode());
            txn.setAdverseName(dci.getToProduct().getName());
            txn.setQuantity(new Quantity(dci.getQuantity()));
            txn.setAction(TradeAction.Sale);
            txn.setProductType(dci.getFromProduct().getProductType());

            txn.setActive(doc.getActive());
            txn.setSerial(doc.getSerial());
            txn.setReference(doc.getReference());
            txn.setInfo(dci.getInfo());
            txn.setCode(doc.getCode());
            txn.setDate(doc.getDate());
            txn.setDocumentId(doc.getId());
            txn.setDocumentType(DocumentType.ProductVirement);
            txn.setFinanceAction(fixFinanceAction(txn.getAction()));

            saveAuditRecords(txn, (AuditBase)doc);

            entityManager.persist(txn);

            
            txn = new ProductTxn();
            txn.setWarehouse(doc.getWarehouse());
            txn.setProduct(dci.getToProduct());
            txn.setAdverseCode(dci.getFromProduct().getCode());
            txn.setAdverseName(dci.getFromProduct().getName());
            txn.setQuantity(new Quantity(dci.getQuantity()));
            //txn.setUnitPrice(new Money());
            txn.setAction(TradeAction.Purchase);
            txn.setProductType(dci.getToProduct().getProductType());

            txn.setActive(doc.getActive());
            txn.setSerial(doc.getSerial());
            txn.setReference(doc.getReference());
            txn.setInfo(dci.getInfo());
            txn.setCode(doc.getCode());
            txn.setDate(doc.getDate());
            txn.setDocumentId(doc.getId());
            txn.setDocumentType(DocumentType.ProductVirement);
            txn.setFinanceAction(fixFinanceAction(txn.getAction()));


            saveAuditRecords(txn, (AuditBase)doc);

            entityManager.persist(txn);

        }
        
        entityManager.flush();
        return true;
    }

    public Boolean deleteProductVirement(ProductVirement doc) {
        deleteDocument(doc.getId(), DocumentType.ProductVirement);

        return true;
    }
    
    public void deleteExpenseNote(ExpenseNote expenseNote){
        deleteDocument(expenseNote.getId(), expenseNote.getDocumentType());
    }

    public void saveExpenseNote(ExpenseNote expenseNote){

        deleteExpenseNote(expenseNote);
        
        for(ExpenseNoteItem item : expenseNote.getItems()){

            ProductTxn txn = new ProductTxn();

            txn.setAction( expenseNote.getTradeAction() );
            txn.setAdverseCode(expenseNote.getContact().getCode());
            
            if (expenseNote.getContact().getPerson()) {
                txn.setAdverseName(expenseNote.getContact().getFullname());
            } else {
            	txn.setAdverseName(expenseNote.getContact().getCompany());
            }
            
            txn.setCode(expenseNote.getCode());
            txn.setDate(expenseNote.getDate());
            txn.setDocumentId(expenseNote.getId());
            txn.setDocumentType(expenseNote.getDocumentType());
            txn.setInfo(item.getInfo());
            txn.setQuantity(item.getQuantity());
            txn.setReference(expenseNote.getReference());
            txn.setSerial(expenseNote.getSerial());
            txn.setProduct(item.getService());

            //FIXME: buradaki birim fiyat vergili olmali. .getTotalAmount()  modelde yok
            txn.setUnitPrice(new MoneySet(item.getUnitPrice()));
            txn.setFinanceAction(fixFinanceAction(txn.getAction()));
            txn.setWarehouse(expenseNote.getWarehouse());
            txn.setProductType(item.getService().getProductType());
            txn.setWorkBunch(expenseNote.getWorkBunch());
            
            saveAuditRecords(txn, (AuditBase)expenseNote);

            entityManager.persist(txn);
            entityManager.flush();
        }

        entityManager.flush();
    }

    /**
     * tradeaction a göre giris / cikis durumu belirleniyor.
     * @param tradeAction
     * @return financeAction
     */
    public FinanceAction fixFinanceAction(TradeAction tradeAction){
        //FIXME: TradeAction enumunda degisiklik yapildiginda bu metod gozden gecirilmeli!
        if(tradeAction.equals(TradeAction.Purchase) || tradeAction.equals(TradeAction.SaleReturn)){
            return FinanceAction.Debit;
        }else
            return FinanceAction.Credit;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void createProductTxnRecordsForCountNote(CountNote doc, List<CountNoteCompareModel> compareList) {
    	log.info("Creating txn records for count note. Serial:{0}", doc.getSerial());

    	for (int i=0;i<compareList.size();i++) {
    		CountNoteCompareModel model = compareList.get(i);
    		
    		if ( model.hasDifference() && model.isProcess() ) {
	    		ProductTxn txn = new ProductTxn();
	    		txn.setWarehouse( doc.getWarehouse() );
	    		txn.setProduct( compareList.get(i).getItem().getProduct() );
	    		txn.setProductType( txn.getProduct().getProductType() );
	
    			if (model.getTradeAction().equals(TradeAction.Sale)) {
    				txn.setAdverseName( messages.get("countNote.message.MissingCount") );
    			} else {
    				txn.setAdverseName( messages.get("countNote.message.ExceededCount") );
    			}
	    		if ( model.getItem().getUser() != null ) {
	    			txn.setContact(model.getItem().getUser().getContact());
	    			txn.setAdverseCode(model.getItem().getUser().getUserName());
	    			txn.setAdverseName(model.getItem().getUser().getFullName());
	    		}

	    		txn.setDocumentType( doc.getDocumentType() );
	    		txn.setDocumentId( doc.getId() );
	            txn.setQuantity( new Quantity( model.getAbsoluteDifference(), model.getUnit() ) );
	            txn.setFinanceAction( model.getFinanceAction() );
	            txn.setAction( model.getTradeAction() );
	            txn.setSerial( doc.getSerial() );
	            txn.setReference( doc.getReference() );
	            txn.setInfo( doc.getInfo() );
	            txn.setDate( doc.getDate() );
	            txn.setUnitPrice( new MoneySet( doc.getItems().get(i).getPrice() ) );

	            saveAuditRecords( txn, doc );

	            model.getItem().setCorrQuantity((int)model.getDifference());

	            entityManager.persist(txn);
    		}
    	}
    	entityManager.flush();
    }

    private void saveAuditRecords(ProductTxn txn, CountNote doc) {
		txn.setCreateUser(activeUser.getUserName());
		txn.setCreateDate(new Date());
    	txn.setUpdateDate(txn.getCreateDate());
    	txn.setUpdateUser(txn.getCreateUser());
    }
    
    public void deleteCountNote(CountNote countNote) {
        deleteDocument( countNote.getId(), countNote.getDocumentType() );
    }

}
