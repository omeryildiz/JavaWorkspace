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
import java.util.Date;

import javax.ejb.Remove;
import javax.ejb.Stateful;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.core.Conversation;
import org.jboss.seam.faces.FacesMessages;

import com.ut.tekir.entities.CurrencyPair;
import com.ut.tekir.entities.CurrencyRate;
import com.ut.tekir.entities.ForeignExchange;
import com.ut.tekir.entities.MoneySet;
import com.ut.tekir.framework.BaseConsts;
import com.ut.tekir.framework.CalendarManager;
import com.ut.tekir.framework.CurrencyManager;
import com.ut.tekir.framework.EntityBase;
import com.ut.tekir.framework.SequenceManager;
import com.ut.tekir.framework.SequenceType;
import com.ut.tekir.general.GeneralSuggestion;

/**
 *
 * @author selman
 */
@Stateful
@Name("foreignExchangeHome")
@Scope(value = ScopeType.CONVERSATION)
public class ForeignExchangeHomeBean extends EntityBase<ForeignExchange> implements ForeignExchangeHome<ForeignExchange> {
    @In
    BankTxnAction bankTxnAction;
    @In(create = true)
    SequenceManager sequenceManager;
    @In
    CalendarManager calendarManager;
    @In
    CurrencyManager currencyManager;
    @In
    GeneralSuggestion generalSuggestion;

    private BigDecimal yatirilanMiktar = BigDecimal.ZERO;
    private BigDecimal cekilenMasraf = BigDecimal.ZERO;
    
    @Create 
    @Begin(join=true,flushMode = org.jboss.seam.annotations.FlushModeType.MANUAL)
    public void init() {
    }

    @Out(required = false)
    public ForeignExchange getForeignExchange() {
        return getEntity();
    }

    @In(required = false)
    public void setForeignExchange(ForeignExchange foreignExchange) {
        setEntity(foreignExchange);
    }

    @Override
    public void createNew() {
        log.debug("Yeni Döviz Transferi");

        entity = new ForeignExchange();
        entity.setActive(true);
        entity.setFromAmount(new MoneySet());
        entity.setToAmount(new MoneySet());
        entity.setCost(new MoneySet());
        entity.getCost().setCurrency(BaseConsts.SYSTEM_CURRENCY_CODE);
        //FIXME: yeni seri numarası almalı.
        entity.setSerial(sequenceManager.getNewSerialNumber(SequenceType.SERIAL_BANKTO_BANK_TRANSFER));

        entity.setDate(calendarManager.getCurrentDate());
    }
    
    public void clearFromBankAccount(){
    	entity.setFromBankAccount(null);
    	entity.getFromAmount().setCurrency(null);
    }
    
    public void clearToBankAccount(){
    	entity.setToBankAccount(null);
    	entity.getToAmount().setCurrency(null);
    }
    
    public String hardWeakCurrency() {
    	String hardWeakCurr = "Empty";
    	if(entity.getFromBankAccount() == null || entity.getToBankAccount() == null){
    		return hardWeakCurr = "Empty";
    	}
    	CurrencyPair cp = currencyManager.getCurrencyPair(entity.getFromBankAccount().getCurrency(), entity.getToBankAccount().getCurrency());
        if (cp == null){
        	return hardWeakCurr = "Empty";
        }
    	if(cp.getHardCurrency().getCode().equals(entity.getFromBankAccount().getCurrency())){
        	hardWeakCurr = "Hard";
        }else{
        	hardWeakCurr = "Weak";
        }
    	return hardWeakCurr;
    }

    @Transactional
    @Override
    public String save() {
        boolean kurKontrol;
        // entity tarihi geçmiş bir güne aitse o günün, bugün veya geleceğe ait bir günse bugünün kurlarını kullanmak adına
		Date rateDate = entity.getDate();
		if (entity.getDate().compareTo(calendarManager.getCurrentDate()) > 0)
			rateDate = calendarManager.getCurrentDate();
   	
        if (entity.getFromBankAccount() != null && entity.getFromBankAccount().equals(entity.getToBankAccount())) {
            facesMessages.add("#{messages['entityBase.message.accountsCannotBeSame']}");
            return BaseConsts.FAIL;
        }
        
        if (entity.getFromBankAccount() != null && !entity.getFromBankAccount().getCurrency().equals(entity.getFromAmount().getCurrency())) {
            facesMessages.add("#{messages['foreignExchange.message.wrongCurrency']}");
            return BaseConsts.FAIL;
        }

        if(entity.getFromBankAccount().getCurrency().equals(entity.getToBankAccount().getCurrency())){
            facesMessages.add("#{messages['foreignExchange.message.sameCurrency']}");
            return BaseConsts.FAIL;
        }

        kurKontrol = generalSuggestion.kurKontrol(rateDate);
        if(!kurKontrol){
            facesMessages.add("#{messages['general.message.DailyRatesUndefined']}");
            return BaseConsts.FAIL;
        }

        // bankadan çekilen tutarın yerel değeri hesaplanıyor.
        if((!entity.getFromBankAccount().getCurrency().equals(BaseConsts.SYSTEM_CURRENCY_CODE)) && (!entity.getToBankAccount().getCurrency().equals(BaseConsts.SYSTEM_CURRENCY_CODE))){
            CurrencyRate cr =currencyManager.getLocalCurrencyRate(entity.getDate(), entity.getFromBankAccount().getCurrency());
            BigDecimal amount = entity.getFromAmount().getValue().multiply(cr.getBid());
            entity.getFromAmount().setLocalAmount(amount);
        }else if (!entity.getFromBankAccount().getCurrency().equals(BaseConsts.SYSTEM_CURRENCY_CODE)){
        	
        	CurrencyPair cp = currencyManager.getCurrencyPair(entity.getFromBankAccount().getCurrency(), entity.getToBankAccount().getCurrency());
            if(cp.getHardCurrency().getCode().equals(entity.getFromBankAccount().getCurrency())){
            	entity.getFromAmount().setLocalAmount(entity.getFromAmount().getValue().multiply(entity.getCurrencyRate()));
            	entity.getFromAmount().setLocalAmount(entity.getFromAmount().getLocalAmount().setScale(2, RoundingMode.HALF_UP));
            }else{
            	entity.getFromAmount().setLocalAmount(entity.getFromAmount().getValue().divide(entity.getCurrencyRate(), 2, RoundingMode.HALF_UP));
            }

        }else{	
            entity.getFromAmount().setLocalAmount(entity.getFromAmount().getValue());
        }

        // yatırılan tutarın yerel değeri hesaplanıyor.
        if((!entity.getToBankAccount().getCurrency().equals(BaseConsts.SYSTEM_CURRENCY_CODE)) && (!entity.getFromBankAccount().getCurrency().equals(BaseConsts.SYSTEM_CURRENCY_CODE))){
            CurrencyRate cr =currencyManager.getLocalCurrencyRate(entity.getDate(), entity.getToBankAccount().getCurrency());
            BigDecimal amount = entity.getToAmount().getValue().multiply(cr.getBid());
            entity.getToAmount().setLocalAmount(amount);
        }else if (!entity.getToBankAccount().getCurrency().equals(BaseConsts.SYSTEM_CURRENCY_CODE)){
        	
        	CurrencyPair cp = currencyManager.getCurrencyPair(entity.getFromBankAccount().getCurrency(), entity.getToBankAccount().getCurrency());
            if(cp.getHardCurrency().getCode().equals(entity.getToBankAccount().getCurrency())){
            	entity.getToAmount().setLocalAmount(entity.getToAmount().getValue().multiply(entity.getCurrencyRate()));
            	entity.getToAmount().setLocalAmount(entity.getToAmount().getLocalAmount().setScale(2, RoundingMode.HALF_UP));
            }else{
            	entity.getToAmount().setLocalAmount(entity.getToAmount().getValue().divide(entity.getCurrencyRate(), 2, RoundingMode.HALF_UP));
            }

        }else {
        	entity.getToAmount().setLocalAmount(entity.getToAmount().getValue());
        }
        
      
//        CurrencyPair cp = currencyManager.getCurrencyPair(entity.getFromBankAccount().getCurrency(), entity.getToBankAccount().getCurrency());
//
//        if(cp.getHardCurrency().getCode().equals(entity.getFromBankAccount().getCurrency())){
//        	entity.getToAmount().setLocalAmount(entity.getFromAmount().getValue().multiply(entity.getCurrencyRate()));
//        }else{
//        	entity.getToAmount().setLocalAmount(entity.getFromAmount().getValue().divide(entity.getCurrencyRate(), 2, RoundingMode.HALF_UP));
//        }
      
        // masraf sadece TL olacak
        entity.getCost().setLocalAmount(entity.getCost().getValue());

        // masrafı çekilen banka hesabının döviz türüne çeviriyoruz.
        if(!entity.getFromBankAccount().getCurrency().equals(BaseConsts.SYSTEM_CURRENCY_CODE)){

            CurrencyPair cpCost = currencyManager.getCurrencyPair(entity.getFromBankAccount().getCurrency(), BaseConsts.SYSTEM_CURRENCY_CODE);
            CurrencyRate cr = currencyManager.getCurrencyRate(entity.getDate(), cpCost);

            if(cpCost.getHardCurrency().getCode().equals(entity.getFromBankAccount().getCurrency())){
                setCekilenMasraf(entity.getCost().getValue().divide(cr.getBid(), 2, RoundingMode.HALF_UP));
            }else{
                setCekilenMasraf(entity.getCost().getValue().multiply(cr.getBid()));
            }
        }else{
            setCekilenMasraf(entity.getCost().getLocalAmount());
        }
        /*
         * masraf sadece TL olacağı için bu bölüm kaldırıldı.
         *
        BigDecimal costAmount;
        if(!entity.getCost().getCurrency().equals(BaseConsts.SYSTEM_CURRENCY_CODE)){

        costAmount = entity.getCost().getValue().multiply(entity.getCurrencyRate());
        }else{
        costAmount = entity.getCost().getValue();
        }
        entity.getCost().setLocalAmount(costAmount);*/
        
        String res = null;
        try {
            res = super.save();

            if (BaseConsts.SUCCESS.equals(res)) {
                //FIXME: yansımaları düzelt.
            	bankTxnAction.saveForeignExchange(entity);
            }

        } catch (Exception ex) {
        	FacesMessages.afterPhase();
        	facesMessages.clear();
        	facesMessages.add(ex.getMessage());
            log.error("Hata :", ex);
	        return BaseConsts.FAIL;
        }

        return res;

    }

    @Transactional
    @Override
    public String delete() {
        try {
            bankTxnAction.deleteForeignExchange(entity);
            return super.delete();
            
        } catch (Exception e) {
            facesMessages.add(e.getMessage());
            return BaseConsts.FAIL;
        }
    }

    public void selectBankAccount(Integer ix) {
        manualFlush();
        if (entity == null) {
            return;
        }
        log.debug("İşlenecek IX : {0}", ix);

    }

    public void initFromAmountCurrency(){

    	entity.getFromAmount().setCurrency(entity.getFromBankAccount().getCurrency());
    }
    
    public void initToAmountCurrency(){

    	entity.getToAmount().setCurrency(entity.getToBankAccount().getCurrency());
    }

    public void manualFlush() {
        Conversation.instance().changeFlushMode(org.jboss.seam.annotations.FlushModeType.MANUAL);
    }

    public BigDecimal getYatirilanMiktar() {
        return yatirilanMiktar;
    }

    public void setYatirilanMiktar(BigDecimal yatirilanMiktar) {
        this.yatirilanMiktar = yatirilanMiktar;
    }

    public BigDecimal getCekilenMasraf() {
        return cekilenMasraf;
    }

    public void setCekilenMasraf(BigDecimal cekilenMasraf) {
        this.cekilenMasraf = cekilenMasraf;
    }

	@Remove @Destroy
    public void destroy() {
    }

}
