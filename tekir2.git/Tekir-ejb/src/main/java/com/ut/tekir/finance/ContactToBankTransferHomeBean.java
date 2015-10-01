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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.jboss.seam.international.StatusMessage.Severity;

import com.ut.tekir.entities.BankToContactTransfer;
import com.ut.tekir.entities.DocumentMatch;
import com.ut.tekir.entities.DocumentType;
import com.ut.tekir.entities.FinanceAction;
import com.ut.tekir.entities.MoneySet;
import com.ut.tekir.entities.PosTxn;
import com.ut.tekir.entities.TradeAction;
import com.ut.tekir.framework.BaseConsts;
import com.ut.tekir.framework.CalendarManager;
import com.ut.tekir.framework.CurrencyManager;
import com.ut.tekir.framework.EntityBase;
import com.ut.tekir.framework.SequenceManager;
import com.ut.tekir.framework.SequenceType;
import com.ut.tekir.general.GeneralSuggestion;
import com.ut.tekir.report.CreditCardPosCommisionViewModel;


/**
 *
 * @author selman
 */
@Stateful
@Name("contactToBankTransferHome")
@Scope(value = ScopeType.CONVERSATION)
public class ContactToBankTransferHomeBean extends EntityBase<BankToContactTransfer> implements ContactToBankTransferHome<BankToContactTransfer> {

    @In(create = true)
    SequenceManager sequenceManager;
    @In
    CalendarManager calendarManager;
    @In
    CurrencyManager currencyManager;
    @In
    BankTxnAction bankTxnAction;
    @In
    FinanceTxnAction financeTxnAction;
    @In
    GeneralSuggestion generalSuggestion;
    @In(create=true)
    InvoiceMatchHome invoiceMatchHome;
    @In
    PosTxnAction posTxnAction;
    
    List<CreditCardPosCommisionViewModel> posCommissionViewList = new ArrayList<CreditCardPosCommisionViewModel>();
    
    @Create 
    @Begin(join=true,flushMode = org.jboss.seam.annotations.FlushModeType.MANUAL)
    public void init() {

    }

    @Out(required = false)
    public BankToContactTransfer getContactToBankTransfer() {
        return getEntity();
    }

    @In(required = false)
    public void setContactToBankTransfer(BankToContactTransfer contactToBankTransfer) {
        setEntity(contactToBankTransfer);
    }

    @Override
    public void createNew() {
        log.debug("Yeni Cariden Bankaya");

        entity = new BankToContactTransfer();
        entity.setActive(true);
        //entity.setFromAmount(new MoneySet());
        entity.setAmount(new MoneySet());
        entity.setCost(new MoneySet());
        entity.setSerial(sequenceManager.getNewSerialNumber(SequenceType.SERIAL_CONTACT_TO_BANK_TRANSFER));
        entity.setDate(calendarManager.getCurrentDate());
        entity.setAction(FinanceAction.Credit);

    }

    @Observer("contactToBankTransferHome:com.ut.tekir.entities.Invoice")
    public void createNewInvoiceMatch(InvoiceMatchFilterModel invoiceModel) {
    	manualFlush();
    	if (entity == null) {
    		return;
    	}
    	DocumentMatch match = new DocumentMatch();
		match.setDocumentSerial(entity.getSerial());
		match.setDocumentType(DocumentType.ContactToBankTransfer);
		match.setMatchDate(entity.getDate());
    	//FIXME: bu kodları document match provider yapısına geçirmek gerek.
//		match.setTotalAmount(invoiceModel.getInvoiceValue());
		match.setMatchedDocumentSerial(invoiceModel.getSerial());
        match.setMatchedDocumentId(invoiceModel.getMatchedDocumentId());
		if (invoiceModel.getAction().equals(TradeAction.Purchase)) {
			match.setMatchedDocumentType(DocumentType.PurchaseInvoice);
		} else {
			match.setMatchedDocumentType(DocumentType.SaleInvoice);
		}
//		entity.setDocumentMatch(match);
    }

    public void openMatchPopup() {
		if (entity.getContact() != null) {
			invoiceMatchHome.setContact(entity.getContact());
			invoiceMatchHome.setDisableContactSelect(Boolean.TRUE);
		}
    	invoiceMatchHome.setTradeAction(TradeAction.Sale);
    	invoiceMatchHome.setInvoiceMatchObserver("contactToBankTransferHome:com.ut.tekir.entities.Invoice");
    }


    @Transactional
    @Override
    public String save() {
    	//FIXME: bu bileşenin döküman eşleme ile ilgili eksiklikleri var. Düzenlemek gerek.
    	String res = null;

        Map<String, BigDecimal> updateMap = new HashMap<String, BigDecimal>();
		List<DocumentMatch> matchList = new ArrayList<DocumentMatch>();
		BigDecimal matchAmount = null;
		BigDecimal oldAmount = null;
        
        entity.getCost().setCurrency(entity.getBankAccount().getCurrency());
    	//TODO: Hatalara dil desteği eklenecek
        try {
            if ((entity.getAmount().getValue().compareTo(BigDecimal.ZERO)) <= 0) {
                facesMessages.add("Yatırılan tutar için sıfırdan büyük bir değer girmelisiniz");
                return BaseConsts.FAIL;
            }

            // eğer banka hesabının döviz türü yerel döviz türünde ise kontrole gerek yok.
            if(!entity.getBankAccount().getCurrency().equals(BaseConsts.SYSTEM_CURRENCY_CODE)){

                //gunun kurlari kayitli mi?
                if (generalSuggestion.kurKontrol(entity.getDate())) {
                    BigDecimal localAmount = entity.getAmount().getLocalAmount();

                    entity.getAmount().setCurrency(entity.getBankAccount().getCurrency());

                    localAmount = currencyManager.convertToLocal(entity.getAmount().getValue(),
                                                                 entity.getAmount().getCurrency(),
                                                                 entity.getDate());

                    entity.getAmount().setLocalAmount(localAmount.setScale(2,RoundingMode.HALF_UP));
                    
                    entity.getCost().setLocalAmount(currencyManager.convertToLocal(entity.getCost().getValue(),
                                                                                   entity.getCost().getCurrency(),
                                                                                   entity.getDate()).setScale(2,RoundingMode.HALF_UP));

                } else {
                    facesMessages.add("#{messages['general.message.DailyRatesUndefined']}");
                    return BaseConsts.FAIL;
                }
            }else{
                // yerel tutar için hesaplama yapmaya gerek yok.
                entity.getAmount().setLocalAmount(entity.getAmount().getValue());
                entity.getCost().setLocalAmount(entity.getCost().getValue());
            }

            res = super.save();
            
            if (BaseConsts.SUCCESS.equals(res)) {

            	bankTxnAction.saveContactToBankTransfer(entity);
            	financeTxnAction.saveContactToBankTransfer(entity);
            }
            updatePosTxnInformations();
        } catch (Exception e) {
        	FacesMessages.afterPhase();
			facesMessages.clear();
        	facesMessages.add(e.getMessage());
        	log.error("Hata :", e);
            res = BaseConsts.FAIL;
        }
        return res;
    }

	private void updatePosTxnInformations() {
		log.info("Updating pos txn informations...");
		for (CreditCardPosCommisionViewModel item : posCommissionViewList) {

			entityManager.createQuery("update PosTxn p set " +
									  "p.repaidStatus = true, " +
									  "p.referenceId=:referenceId," +
									  "p.referenceDocumentType=:referenceDocumentType where " +
									  "p.documentId=:documentId and " +
									  "p.documentType=:documentType")
									  .setParameter("referenceId", entity.getId())
									  .setParameter("referenceDocumentType", DocumentType.ContactToBankTransfer)
									  .setParameter("documentId", item.getDocumentId())
									  .setParameter("documentType", item.getDocumentType())
									  .executeUpdate();
		}

		if (posCommissionViewList != null && posCommissionViewList.size() >0 ) {

			PosTxn txn = new PosTxn();

			CreditCardPosCommisionViewModel firstElement = posCommissionViewList.get(0);
			
			txn.setPos(firstElement.getPos());
			txn.setDate(entity.getDate());
			txn.setSerial(entity.getSerial());
			txn.setReference(entity.getReference());
			txn.setDocumentId(entity.getId());
			txn.setDocumentType(DocumentType.ContactToBankTransfer);
			
			txn.setInfo(entity.getContact().getName() + " kurumunun yapmış olduğu ödemedir.");
			txn.setActive(true);
			txn.setReferenceId(entity.getId());
			txn.setReferenceDocumentType(DocumentType.ContactToBankTransfer);
			
			txn.setRepaidStatus(true);
			
			txn.setAction(FinanceAction.Credit);

			txn.setAmount(new MoneySet(entity.getAmount()));
			
			txn.setAdverseCode(entity.getContact().getCode());
			txn.setAdverseName(entity.getContact().getName());
			
			txn.setMaturityDate(entity.getDate());

			entityManager.persist(txn);
		}
	}

    private void addToMatchList(List<DocumentMatch> matchList, DocumentMatch documentMatch) {
    	for(DocumentMatch match : matchList ) {
    		if (match.getMatchedDocumentSerial().equals(documentMatch.getMatchedDocumentSerial())) {
    			return;
    		}
    	}
    	matchList.add(documentMatch);
	}

    @Override
    @Transactional
    public String delete() {
		String result = BaseConsts.SUCCESS;

    	try {
    		bankTxnAction.deleteContactToBankTransfer(entity);
    		financeTxnAction.deleteContactToBankTransfer(entity);

    		posTxnAction.deletePosTxnRecordsForContactToBankTransfer(entity);

    		result = super.delete();
    	} catch (Exception e) {
			log.error("Fiş silinirken hata oluştu. Sebebi :#0", e);
			facesMessages.add(Severity.ERROR, "Fiş silinirken hata oluştu. Sebebi :{0}",e);
			result = BaseConsts.FAIL;
		}
        return result;
    }


    public void manualFlush() {
        Conversation.instance().changeFlushMode(org.jboss.seam.annotations.FlushModeType.MANUAL);
    }

    public void clearBankAccount() {
		entity.setBankBranch(null);
		entity.setBankAccount(null);
	}

	public List<CreditCardPosCommisionViewModel> getPosCommissionViewList() {
		return posCommissionViewList;
	}

	public void setPosCommissionViewList(
			List<CreditCardPosCommisionViewModel> posCommissionViewList) {
		this.posCommissionViewList = posCommissionViewList;
	}

}
