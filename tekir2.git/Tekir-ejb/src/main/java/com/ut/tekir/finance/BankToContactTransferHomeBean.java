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
import org.jboss.seam.core.Conversation;
import org.jboss.seam.faces.FacesMessages;

import com.ut.tekir.entities.BankToContactTransfer;
import com.ut.tekir.entities.DocumentMatch;
import com.ut.tekir.entities.DocumentType;
import com.ut.tekir.entities.FinanceAction;
import com.ut.tekir.entities.MoneySet;
import com.ut.tekir.entities.TradeAction;
import com.ut.tekir.framework.BaseConsts;
import com.ut.tekir.framework.CalendarManager;
import com.ut.tekir.framework.CurrencyManager;
import com.ut.tekir.framework.EntityBase;
import com.ut.tekir.framework.SequenceManager;
import com.ut.tekir.framework.SequenceType;
import com.ut.tekir.general.GeneralSuggestion;


/**
 *
 * @author burak
 */
@Stateful
@Name("bankToContactTransferHome")
@Scope(value = ScopeType.CONVERSATION)
public class BankToContactTransferHomeBean extends EntityBase<BankToContactTransfer> implements BankToContactTransferHome<BankToContactTransfer> {

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

    @Create 
    @Begin(join=true,flushMode = org.jboss.seam.annotations.FlushModeType.MANUAL)
    public void init() {

    }

    @Out(required = false)
    public BankToContactTransfer getBankToContactTransfer() {
        return getEntity();
    }

    @In(required = false)
    public void setBankToContactTransfer(BankToContactTransfer bankToContactTransfer) {
        setEntity(bankToContactTransfer);
    }

    @Override
    public void createNew() {
        log.debug("Yeni Bankadan Cariye");

        entity = new BankToContactTransfer();
        entity.setActive(true);
        //entity.setFromAmount(new MoneySet());
        entity.setAmount(new MoneySet());
        entity.setCost(new MoneySet());
        entity.setSerial(sequenceManager.getNewSerialNumber(SequenceType.SERIAL_BANKTO_CONTACT_TRANSFER));
        //entity.setReference(sequenceManager.getNewSerialNumber(SequenceType.REFERENCE_SHIPMENT_TRANSFER));
        entity.setDate(calendarManager.getCurrentDate());
        entity.setAction(FinanceAction.Debit);

    }

    @Observer("bankToContactTransferHome:com.ut.tekir.entities.Invoice")
    public void createNewInvoiceMatch(InvoiceMatchFilterModel invoiceModel) {
    	manualFlush();
    	if (entity == null) {
    		return;
    	}
    	DocumentMatch match = new DocumentMatch();
		match.setDocumentSerial(entity.getSerial());
		match.setDocumentType(DocumentType.BankToContactTransfer);
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
    	invoiceMatchHome.setTradeAction(TradeAction.Purchase);
    	invoiceMatchHome.setInvoiceMatchObserver("bankToContactTransferHome:com.ut.tekir.entities.Invoice");
    }

    @Override
    public String save() {
    	//FIXME: bu metodu document match provider yapısına geçirmek gerek.
    	String res = null;

        Map<String, BigDecimal> updateMap = new HashMap<String, BigDecimal>();
		List<DocumentMatch> matchList = new ArrayList<DocumentMatch>();
		BigDecimal matchAmount = null;
		BigDecimal oldAmount = null;

        //TODO: Hatalara dil desteği eklenecek
       Boolean hata = false;

       entity.getCost().setCurrency(entity.getBankAccount().getCurrency());

        try {

            if ((entity.getAmount().getValue().compareTo(BigDecimal.ZERO)) <= 0) {
                facesMessages.add("Yatırılan tutar için sıfırdan büyük bir değer girmelisiniz");
                return BaseConsts.FAIL;
            }

            // banka hesabının kuru yerel kura eşitse yerel tutar için hesaplama yapmaya gerek yok.
            if(entity.getBankAccount().getCurrency().equals(BaseConsts.SYSTEM_CURRENCY_CODE)){
                entity.getAmount().setLocalAmount(entity.getAmount().getValue());
                entity.getCost().setLocalAmount(entity.getCost().getValue());
            }else{

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
                                                                                   entity.getDate()).setScale(2, RoundingMode.HALF_UP));

                } else {
                    facesMessages.add("#{messages['general.message.DailyRatesUndefined']}");
                    return BaseConsts.FAIL;
                }
            }

            res = super.save();
            
            if (BaseConsts.SUCCESS.equals(res)) {
            	
            	bankTxnAction.saveBankToContactTransfer(entity);
            	financeTxnAction.saveBankToContactTransfer(entity);//TODO:kontrol edilmeli
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
    public String delete() {
        bankTxnAction.deleteBankToContactTransfer(entity);
        financeTxnAction.deleteBankToContactTransfer(entity);
        return super.delete();
    }


    public void manualFlush() {
        Conversation.instance().changeFlushMode(org.jboss.seam.annotations.FlushModeType.MANUAL);
    }

    public void clearBankAccount() {
       entity.setBankBranch(null);
       entity.setBankAccount(null);
    }
}
