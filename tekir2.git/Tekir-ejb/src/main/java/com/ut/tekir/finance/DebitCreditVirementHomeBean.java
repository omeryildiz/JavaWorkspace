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

import javax.ejb.Stateful;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.Conversation;

import com.ut.tekir.entities.DebitCreditVirement;
import com.ut.tekir.entities.DebitCreditVirementItem;
import com.ut.tekir.framework.BaseConsts;
import com.ut.tekir.framework.CalendarManager;
import com.ut.tekir.framework.CurrencyManager;
import com.ut.tekir.framework.EntityBase;
import com.ut.tekir.framework.SequenceManager;
import com.ut.tekir.framework.SequenceType;
import com.ut.tekir.general.GeneralSuggestion;

/**
 *
 * @author haky
 */
@Stateful
@Name("debitCreditVirementHome")
@Scope(value = ScopeType.CONVERSATION)
public class DebitCreditVirementHomeBean extends EntityBase<DebitCreditVirement> implements DebitCreditVirementHome<DebitCreditVirement> {

    @In(create = true)
    SequenceManager sequenceManager;
    @In
    CalendarManager calendarManager;
    @In
    CurrencyManager currencyManager;
    @In
    FinanceTxnAction financeTxnAction;
    @In
    GeneralSuggestion generalSuggestion;

    @Create 
    @Begin(join=true, flushMode = org.jboss.seam.annotations.FlushModeType.MANUAL)
    public void init() {

    }

    @Out(required = false)
    public DebitCreditVirement getDebitCreditVirement() {
        return getEntity();
    }

    @In(required = false)
    public void setDebitCreditVirement(DebitCreditVirement debitCreditVirement) {
        setEntity(debitCreditVirement);
    }

    @Override
    public void createNew() {
        log.debug("Yeni DebitCreditVirement");

        entity = new DebitCreditVirement();
        entity.setActive(true);
        entity.setSerial(sequenceManager.getNewSerialNumber(SequenceType.SERIAL_FUND_DEBITCREDIT));
        //entity.setReference(sequenceManager.getNewSerialNumber(SequenceType.REFERENCE_FUND_DEBITCREDIT));
        entity.setDate(calendarManager.getCurrentDate());

    }

    @Override
    public String save() {

        try {
            if (entity.getItems().size() == 0) {
                facesMessages.add("En az bir detay girmelisiniz!");
                throw new RuntimeException("Hatalar Var! Kayıt gerçekleşmedi!");
            }

            for (DebitCreditVirementItem it : entity.getItems()) {

                if (it.getFromContact() == null && it.getToContact() == null) {
                    facesMessages.add("En az bir Cari seçmelisiniz!");
                    throw new RuntimeException("Hatalar Var! Kayıt gerçekleşmedi!");
                }

                if( it.getFromContact() != null && it.getFromContact().equals(it.getToContact())){
                    facesMessages.add("Giriş ve çıkış carileri aynı olamaz!");
                    throw new RuntimeException("Hatalar Var! Kayıt gerçekleşmedi!");
                }
                
                if ( ( it.getAmount().getValue().compareTo(BigDecimal.ZERO) ) < 0) {
                    facesMessages.add("Sıfırdan büyük bir değer girmelisiniz");
                    throw new RuntimeException("Hatalar Var! Kayıt gerçekleşmedi!");
                }

                // yerel kura esitse yerel tutar icin hesaplama yapmaya gerek yok.
                if(it.getAmount().getCurrency().equals(BaseConsts.SYSTEM_CURRENCY_CODE)){
                    it.getAmount().setLocalAmount(it.getAmount().getValue());
                }else{
                    // kur farklı ise günün kurları alınmışmı kontrol et.
                    if(!generalSuggestion.kurKontrol(entity.getDate())){
                        
                        facesMessages.add("#{messages['general.message.DailyRatesUndefined']}");
                        return BaseConsts.FAIL;
                    }else{

                        it.getAmount().setLocalAmount(currencyManager.convertToLocal(it.getAmount().getValue(), it.getAmount().getCurrency(), entity.getDate()));
                    }
                }
                

                //FIXME: Kur Çevrimi düzeltilecek.
                //it.getAmount().setLocalAmount(currencyManager.convertLocale(it.getAmount(), entity.getDate()));
            }
        } catch (Exception e) {
            log.error("Hata :", e);
            return BaseConsts.FAIL;
        }
        
	String res = super.save();

        if( BaseConsts.SUCCESS.equals(res) ){
            financeTxnAction.saveDebitCreditVirement(entity);
        }

        return res;

    }

    @Override
    public String delete() {

        financeTxnAction.deleteDebitCreditVirement(entity);

        return super.delete();
    }

    public void createNewLine() {
        manualFlush();
        if (entity == null) {
            return;
        }

        log.debug("EntityManager.FluashMode : #0", entityManager.getFlushMode());

        DebitCreditVirementItem it = new DebitCreditVirementItem();
        it.setOwner(entity);
        it.getAmount().setCurrency(BaseConsts.SYSTEM_CURRENCY_CODE);
        entity.getItems().add(it);
        log.debug("yeni item eklendi");
    }

    public void deleteLine(DebitCreditVirementItem item) {
        manualFlush();
        if (entity == null) {
            return;
        }
        log.debug("Kayıt Silinecek : {0}", item);
        entity.getItems().remove(item);
    }

    public void deleteLine(Integer ix) {
        manualFlush();
        if (entity == null) {
            return;
        }
        log.debug("Kayıt Silinecek IX : {0}", ix);
        log.debug("Toplam Kayıt : {0}", entity.getItems().size());
        Object o = entity.getItems().remove(ix.intValue());
        log.debug("Silinen : {0}", o);
        log.debug("Toplam Kayıt : {0}", entity.getItems().size());
    }

    public void manualFlush() {
        Conversation.instance().changeFlushMode(org.jboss.seam.annotations.FlushModeType.MANUAL);
    }
}
