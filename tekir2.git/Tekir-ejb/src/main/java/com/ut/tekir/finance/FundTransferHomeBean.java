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

import com.ut.tekir.entities.CurrencyPair;
import com.ut.tekir.entities.CurrencyRate;
import java.math.BigDecimal;

import com.ut.tekir.entities.FundTransfer;
import com.ut.tekir.entities.FundTransferCurrencyRate;
import com.ut.tekir.entities.FundTransferItem;
import com.ut.tekir.entities.Money;
import com.ut.tekir.framework.BaseConsts;
import com.ut.tekir.framework.CalendarManager;
import com.ut.tekir.framework.CurrencyManager;
import com.ut.tekir.framework.EntityBase;
import com.ut.tekir.framework.SequenceManager;
import com.ut.tekir.framework.SequenceType;
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
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.Conversation;

/**
 *
 * @author haky
 */
@Stateful
@Name("fundTransferHome")
@Scope(value = ScopeType.CONVERSATION)
public class FundTransferHomeBean extends EntityBase<FundTransfer> implements FundTransferHome<FundTransfer> {

    @In(create = true)
    SequenceManager sequenceManager;
    @In
    CalendarManager calendarManager;
    @In
    CurrencyManager currencyManager;
    @In
    AccountTxnAction accountTxnAction;

    private List<String> ccyList;
    private Map<String, FundTransferCurrencyRate> ccyRates = new HashMap<String, FundTransferCurrencyRate>();

    @Create 
    @Begin(join=true,flushMode = org.jboss.seam.annotations.FlushModeType.MANUAL)
    public void init() {

    }

    @Out(required = false)
    public FundTransfer getFundTransfer() {
        return getEntity();
    }

    @In(required = false)
    public void setFundTransfer(FundTransfer fundTransfer) {
        setEntity(fundTransfer);
    }

    @Override
    public void createNew() {
        log.debug("Yeni FundTransfer");

        entity = new FundTransfer();
        entity.setActive(true);
        entity.setSerial(sequenceManager.getNewSerialNumber(SequenceType.SERIAL_FUND_TRANSFER));
        //entity.setReference(sequenceManager.getNewSerialNumber(SequenceType.REFERENCE_FUND_TRANSFER));
        entity.setDate(calendarManager.getCurrentDate());

    }

    @Override
    public String save() {

        try {
            if (entity.getItems().size() == 0) {
                facesMessages.add("En az bir detay girmelisiniz!");
                throw new RuntimeException("Hatalar Var! Kayıt gerçekleşmedi!");
            }

            //TODO: Metinlere dildesteği eklenecek
            for (FundTransferItem it : entity.getItems()) {

                if (it.getFromAccount() == null && it.getToAccount() == null) {
                    facesMessages.add("En az bir Hesap seçmelisiniz!");
                    throw new RuntimeException("Hatalar Var! Kayıt gerçekleşmedi!");
                }

                if (it.getFromAccount() != null && it.getFromAccount().equals(it.getToAccount()) ) {
                    facesMessages.add("Giriş ve çıkış hesapları aynı olamaz!");
                    throw new RuntimeException("Hatalar Var! Kayıt gerçekleşmedi!");
                }
                
                if (( it.getAmount().getValue().compareTo(BigDecimal.ZERO) ) < 0) {
                    facesMessages.add("Sıfırdan büyük bir değer girmelisiniz");
                    throw new RuntimeException("Hatalar Var! Kayıt gerçekleşmedi!");
                }

              //FIXME: Yerel para birimi Åşey edilecek -- bu kısım düzenlendi, bilge..
                it.getAmount().setCurrency(it.getAmount().getCurrency());
                it.getAmount().setLocalAmount(currencyManager.convertToLocal(it.getAmount().getValue(), it.getAmount().getCurrency(), entity.getDate()));
            }
        } catch (Exception e) {
            log.error("Hata :", e);
            return BaseConsts.FAIL;
        }

        recalculate();

        String res = super.save();

        if( BaseConsts.SUCCESS.equals(res) ){
            accountTxnAction.saveFundTransfer(entity);
        }

        return res;
    }


     /**
     * Satırları tek tek dolaşarak tüm fiş toplamlarını yeniden hesaplar...
     */
    public void recalculate() {
    	manualFlush();
        ccyList = new ArrayList<String>();
        entity.setTotalAmount(new Money());
        ccyRates.clear();

        //Kur ve Döviz toplamları hesaplanıyor
        for (FundTransferItem it : entity.getItems()) {
                calculateCcy(it);
                calculateTotals(it);
        }

    }

    public void calculateTotals(FundTransferItem item){

    	BigDecimal totalAmount = BigDecimal.ZERO;;

        if (item.getAmount().getValue().compareTo(BigDecimal.ZERO) < 0) {
            facesMessages.add("Sıfırdan büyük bir değer girmelisiniz");
            throw new RuntimeException("Hatalar Var! Kayıt gerçekleşmedi!");
        }

        totalAmount = item.getAmount().getLocalAmount();
        totalAmount = entity.getTotalAmount().getValue().add(totalAmount);
        entity.getTotalAmount().setValue(totalAmount);

    }

    public void calculateCcy(FundTransferItem it) {
		String ccy = it.getAmount().getCurrency();
		FundTransferCurrencyRate ftcr = null;

		// Kolaylık olsun döviz listesi hazırlanıyor
		if (!ccyList.contains(ccy)) {
			ccyList.add(ccy);
		}

		// Kurlar toplanıyor
		if (!ccy.equals(BaseConsts.SYSTEM_CURRENCY_CODE)) {

			CurrencyPair cp = currencyManager.getCurrencyPair(ccy,
					BaseConsts.SYSTEM_CURRENCY_CODE);

			ftcr = findCurrencyRate(cp);
			if (ftcr == null) {

				CurrencyRate cr = currencyManager.getCurrencyRate(entity
						.getDate(), cp);
				ftcr = new FundTransferCurrencyRate();
				ftcr.setCurrencyPair(cp);
				ftcr.setFundTransfer(entity);
				ftcr.setAsk(cr != null ? cr.getAsk() : BigDecimal.ZERO);
				ftcr.setAsk(cr != null ? cr.getBid() : BigDecimal.ZERO);

				entity.getCurrencyRates().add(ftcr);

				ccyRates.put(ccy, ftcr);
			}

		}

		if (ccy.equals(BaseConsts.SYSTEM_CURRENCY_CODE)) {
			it.getAmount().setLocalAmount(it.getAmount().getValue());
		} else {
			it.getAmount().setLocalAmount(it.getAmount().getValue().multiply(ftcr.getAsk()));
		}
	}


    public FundTransferCurrencyRate findCurrencyRate(CurrencyPair cp) {
        for (FundTransferCurrencyRate cr : entity.getCurrencyRates()) {
            if (cr.getCurrencyPair().equals(cp)) {
                return cr;
            }
        }
        return null;
    }


    @Override
    public String delete() {

        accountTxnAction.deleteFundTransfer(entity);

        return super.delete();
    }

    public void createNewLine() {
        manualFlush();
        if (entity == null) {
            return;
        }

        log.debug("EntityManager.FlushMode : #0", entityManager.getFlushMode());

        FundTransferItem it = new FundTransferItem();
        it.setOwner(entity);
        it.getAmount().setCurrency(BaseConsts.SYSTEM_CURRENCY_CODE);
        entity.getItems().add(it);
        log.debug("yeni item eklendi");
    }

    public void deleteLine(FundTransferItem item) {
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
