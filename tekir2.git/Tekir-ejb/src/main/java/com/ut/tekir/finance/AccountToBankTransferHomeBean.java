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

import com.ut.tekir.entities.BankToAccountTransfer;
import com.ut.tekir.entities.FinanceAction;
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
import org.jboss.seam.faces.FacesMessages;

/**
 *
 * @author haky
 */
@Stateful
@Name("accountToBankTransferHome")
@Scope(value = ScopeType.CONVERSATION)
public class AccountToBankTransferHomeBean extends EntityBase<BankToAccountTransfer> implements AccountToBankTransferHome<BankToAccountTransfer> {
    
    @In(create = true)
    SequenceManager sequenceManager;
    @In
    CalendarManager calendarManager;
    @In
    CurrencyManager currencyManager;
    @In
    AccountTxnAction accountTxnAction;
    @In
    BankTxnAction bankTxnAction;
    @In
    GeneralSuggestion generalSuggestion;

    @Create 
    @Begin(join=true,flushMode = org.jboss.seam.annotations.FlushModeType.MANUAL)
    public void init() {

    }
    
    @Out(required = false) 
    public BankToAccountTransfer getAccountToBankTransfer() {
        return getEntity();
    }

    @In(required = false)
    public void setAccountToBankTransfer(BankToAccountTransfer accountToBankTransfer) {
        setEntity(accountToBankTransfer);
    }

    @Override
    public void createNew() {
        log.debug("Yeni AccountToBankTransfer");
        entity = new BankToAccountTransfer();
        entity.setActive(true);
        entity.setAction(FinanceAction.Credit);
        entity.setAmount(new MoneySet());
        entity.setCost(new MoneySet());
        entity.setSerial(sequenceManager.getNewSerialNumber( SequenceType.SERIAL_ACCOUNT_TO_BANK_TRANSFER));
        entity.setDate(calendarManager.getCurrentDate());
        //TODO: Default Kasa eklenecek.
    }

    @Override
    @Transactional
    public String save() {
    	String res = null;
    	boolean kurKontrol;
    	
    	try {
    		if (( entity.getAmount().getValue().compareTo(BigDecimal.ZERO) ) < 0) {
    			facesMessages.add("Sıfırdan büyük bir değer girmelisiniz");
    			throw new RuntimeException("Hatalar Var! Kayıt gerçekleşmedi!");
    		}
    		entity.getAmount().setCurrency(entity.getBankAccount().getCurrency());
			entity.getCost().setCurrency(entity.getBankAccount().getCurrency());
    		
	    	//günün kurları alınmış mı kontrolü
	        //TODO: işlemin yapıldığı tarih(bugün) mi yoksa entity nin tarihi mi baz alınmalı ?
	    	kurKontrol = generalSuggestion.kurKontrol(entity.getDate());

    		if(entity.getAmount().getCurrency().equals(BaseConsts.SYSTEM_CURRENCY_CODE)) {
            	entity.getAmount().setLocalAmount(entity.getAmount().getValue());
                entity.getCost().setLocalAmount(entity.getCost().getValue());
            } else{
            	if(!kurKontrol){
            		facesMessages.add("#{messages['general.message.DailyRatesUndefined']}");
            		return BaseConsts.FAIL;
            	}
            	entity.getAmount().setLocalAmount(currencyManager.convertToLocal(entity.getAmount().getValue(), 
            																	 entity.getAmount().getCurrency(), 
            																     entity.getDate())); 

            	entity.getAmount().setLocalAmount(entity.getAmount().getLocalAmount().setScale(2, RoundingMode.HALF_UP));

                entity.getCost().setLocalAmount(currencyManager.convertToLocal(entity.getCost().getValue(),
                                                                               entity.getCost().getCurrency(),
                                                                               entity.getDate()).setScale(2, RoundingMode.HALF_UP));
            	
            }

    		res = super.save();
    		
    		if( BaseConsts.SUCCESS.equals(res) ){
    			
    			bankTxnAction.saveAccountToBank(entity);
    			accountTxnAction.saveAccountToBankTransfer(entity);
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
    @Transactional
    public String delete() {

        bankTxnAction.deleteAccountToBank(entity);
        accountTxnAction.deleteAccountToBankTransfer(entity);

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
