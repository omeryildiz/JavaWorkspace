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

import com.ut.tekir.entities.BankToBankTransfer;
import com.ut.tekir.entities.BankTransferType;
import com.ut.tekir.entities.MoneySet;
import com.ut.tekir.framework.BaseConsts;
import com.ut.tekir.framework.CalendarManager;
import com.ut.tekir.framework.CurrencyManager;
import com.ut.tekir.framework.EntityBase;
import com.ut.tekir.framework.SequenceManager;
import com.ut.tekir.framework.SequenceType;
import com.ut.tekir.general.GeneralSuggestion;

import javax.ejb.Stateful;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.core.Conversation;


/**
 *
 * @author haky
 */
@Stateful
@Name("bankToBankTransferHome")
@Scope(value = ScopeType.CONVERSATION)
public class BankToBankTransferHomeBean extends EntityBase<BankToBankTransfer> implements BankToBankTransferHome<BankToBankTransfer> {

    @In(create = true)
    SequenceManager sequenceManager;
    @In
    CalendarManager calendarManager;
    @In
    CurrencyManager currencyManager;
    @In
    BankTxnAction bankTxnAction;
    @In
    GeneralSuggestion generalSuggestion;
    
    @Create 
    @Begin(join=true,flushMode = org.jboss.seam.annotations.FlushModeType.MANUAL)
    public void init() {
    }

    @Out(required = false)
    public BankToBankTransfer getBankToBankTransfer() {
        return getEntity();
    }

    @In(required = false)
    public void setBankToBankTransfer(BankToBankTransfer bankToBankTransfer) {
        setEntity(bankToBankTransfer);
    }

    @Override
    public void createNew() {
        log.debug("Yeni Bankadan Bankaya Transfer");

        entity = new BankToBankTransfer();
        entity.setActive(true);
        entity.setFromAmount(new MoneySet());
        entity.setToAmount(new MoneySet());
        entity.setCost(new MoneySet());
        entity.setSerial(sequenceManager.getNewSerialNumber(SequenceType.SERIAL_BANKTO_BANK_TRANSFER));
        entity.setDate(calendarManager.getCurrentDate());
        entity.setTransferType(BankTransferType.Eft);

    }
    
    public void initToAmountCurrency(){
    	
    	entity.getToAmount().setCurrency(entity.getToBankAccount().getCurrency().toString());
    }
    
    public void initFromAmountCurrency(){
    	
    	entity.getFromAmount().setCurrency(entity.getFromBankAccount().getCurrency().toString());
        // masraf çıkış bankasından kesilecek, haliyle kuru da çıkış bankasıyla aynı olmalı.
        entity.getCost().setCurrency(entity.getFromBankAccount().getCurrency());
    }
    
    public void clearFromBankAccount(){
    	entity.setFromBankBranch(null);
    	entity.setFromBankAccount(null);
    	entity.getFromAmount().setCurrency(null);
    }
    
    public void clearToBankAccount(){
    	entity.setToBankBranch(null);
    	entity.setToBankAccount(null);
    	entity.getToAmount().setCurrency(null);
    }

    @Override
    @Transactional
	public String save() {
    	String res = null;
    	boolean kurKontrol;

        // TODO: Hatalara dil desteği eklenecek
		Boolean hata = false;

        try {
            if (entity.getFromBankAccount() != null && entity.getFromBankAccount().equals(entity.getToBankAccount())) {
                facesMessages.add("Giriş ve Çıkış hesapları aynı olamaz!");
                hata = true;
            }
            
	    	//günün kurları alınmış mı kontrolü
	        //TODO: işlemin yapıldığı tarih(bugün) mi yoksa entity nin tarihi mi baz alınmalı ?
	    	kurKontrol = generalSuggestion.kurKontrol(entity.getDate());

            if(entity.getFromAmount().getCurrency().equals(BaseConsts.SYSTEM_CURRENCY_CODE)) {
            	entity.getFromAmount().setLocalAmount(entity.getFromAmount().getValue());
                entity.getCost().setLocalAmount(entity.getCost().getValue());
            } else{
            	if(!kurKontrol){
            		facesMessages.add("#{messages['general.message.DailyRatesUndefined']}");
            		return BaseConsts.FAIL;
            	}
            	entity.getFromAmount().setLocalAmount(currencyManager.convertToLocal(entity.getFromAmount().getValue(), 
            																		 entity.getFromAmount().getCurrency(), 
            																		 entity.getDate())); 

            	entity.getFromAmount().setLocalAmount(entity.getFromAmount().getLocalAmount().setScale(2, RoundingMode.HALF_UP));
            	
            	entity.getCost().setLocalAmount(currencyManager.convertToLocal(entity.getCost().getValue(),
            																		 entity.getCost().getCurrency(),
            																		 entity.getDate()));

            	entity.getCost().setLocalAmount(entity.getCost().getLocalAmount().setScale(2, RoundingMode.HALF_UP));

            }

            if(entity.getToAmount().getCurrency().equals(BaseConsts.SYSTEM_CURRENCY_CODE)) {
            	entity.getToAmount().setLocalAmount(entity.getToAmount().getValue()); 
            } else{
            	if(!kurKontrol){
            		facesMessages.add("#{messages['general.message.DailyRatesUndefined']}");
            		return BaseConsts.FAIL;
            	}
            	entity.getToAmount().setLocalAmount(currencyManager.convertToLocal(entity.getToAmount().getValue(), 
            																		 entity.getToAmount().getCurrency(), 
            																		 entity.getDate())); 

            	entity.getToAmount().setLocalAmount(entity.getToAmount().getLocalAmount().setScale(2, RoundingMode.HALF_UP));
            	
            }

            /*if (entity.getFromBankAccount().getCurrency().getCode().compareTo(entity.getFromAmount().getCurrency()) != 0) {
                facesMessages.add("Çıkış hesabının döviz değeriyle çekilen tutarın döviz değeri farklı olamaz!");
                hata = true;
            }
            
            if (entity.getToBankAccount().getCurrency().getCode().compareTo(entity.getToAmount().getCurrency()) != 0) {
                facesMessages.add("Giriş hesabının döviz değeriyle yatırılan tutarın döviz değeri farklı olamaz!");
                hata = true;
            }*/
            
            if (entity.getFromAmount().getValue() == null || (entity.getFromAmount().getValue().compareTo(BigDecimal.ZERO)) <= 0) {
                facesMessages.add("Çekilen tutar için sıfırdan büyük bir değer girmelisiniz");
                hata = true;
            }
            
            if (entity.getToAmount().getValue() == null || (entity.getToAmount().getValue().compareTo(BigDecimal.ZERO)) <= 0) {
                facesMessages.add("Yatırılan tutar için sıfırdan büyük bir değer girmelisiniz");
                hata = true;
            }
            
            if (hata) {
                throw new RuntimeException("Hata!");
            }

            res = super.save();
            
            if (BaseConsts.SUCCESS.equals(res)) {
            	
            	bankTxnAction.saveBankToBankTransfer(entity);
            }

        } catch (Exception e) {
            log.error("Hata :", e);
            return BaseConsts.FAIL;
        }
        return res;
    }

    @Override
    @Transactional
    public String delete() {

        bankTxnAction.deleteBankToBankTransfer(entity);

        return super.delete();
    }

    public void selectBankAccount(Integer ix) {
        manualFlush();
        if (entity == null) {
            return;
        }
        log.debug("İşlenecek IX : {0}", ix);

    }

    public void manualFlush() {
        Conversation.instance().changeFlushMode(org.jboss.seam.annotations.FlushModeType.MANUAL);
    }
}
