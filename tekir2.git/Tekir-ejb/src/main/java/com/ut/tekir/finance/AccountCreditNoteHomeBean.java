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
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.core.Conversation;
import org.jboss.seam.international.StatusMessage.Severity;

import com.ut.tekir.entities.AccountCreditNote;
import com.ut.tekir.entities.AccountCreditNoteDetail;
import com.ut.tekir.framework.BaseConsts;
import com.ut.tekir.framework.CalendarManager;
import com.ut.tekir.framework.CurrencyManager;
import com.ut.tekir.framework.EntityBase;
import com.ut.tekir.framework.SequenceManager;
import com.ut.tekir.framework.SequenceType;
import com.ut.tekir.util.Utils;



//FIXME: buradaki kur hesaplama ve toplam hesaplama rutinlerini 
//diğer sınıflara (tahsilat, tediye, hareket fişleri vb. yerlere aktarmalı.)

/**
 * Kasa çıkış fişi home bileşenidir.
 * @author sinan.yumak
 */
@Stateful
@Name("accountCreditNoteHome")
@Scope(value = ScopeType.CONVERSATION)
public class AccountCreditNoteHomeBean extends EntityBase<AccountCreditNote> implements AccountCreditNoteHome<AccountCreditNote> {

    @In(create = true)
    SequenceManager sequenceManager;
    @In
    CalendarManager calendarManager;
    @In
    CurrencyManager currencyManager;
    @In
    AccountTxnAction accountTxnAction;
    @In(create=true)
    DocumentCalculationHome documentCalculationHome;
    
    @Create 
    @Begin(join=true,flushMode = org.jboss.seam.annotations.FlushModeType.MANUAL)
    public void init() {

    }

    @Out(required = false)
    public AccountCreditNote getAccountCreditNote() {
        return getEntity();
    }

    @In(required = false)
    public void setAccountCreditNote(AccountCreditNote accountCreditNote) {
        setEntity(accountCreditNote);
    }

    @Override
    public void createNew() {
        log.debug("New AccountCreditNote");

        entity = new AccountCreditNote();
        entity.setActive(true);
        //FIXME:yeni numara al.
        entity.setSerial(sequenceManager.getNewSerialNumber(SequenceType.SERIAL_FUND_TRANSFER));

        entity.setDate(calendarManager.getCurrentDate());
    }

    @Override
    @Transactional
    public String save() {
		log.info("Saving account credit note. Credit note serial :{0}", entity.getSerial());
		String result = BaseConsts.FAIL;
    	try {
    		makeEntityValidations();
    		
            recalculate();

            result = super.save();
            
            if(result.equals(BaseConsts.SUCCESS)){
            	accountTxnAction.saveDocument(entity);
            }
            
    	} catch (Exception e) {
            facesMessages.add(Utils.getExceptionMessage(e));
            log.error("Hata :#0", e);
            return BaseConsts.FAIL;
        }
        return result;
    }

	private void makeEntityValidations() {
		if (entity.getItems().size() == 0) {
			throw new RuntimeException("En az bir detay girmelisiniz!");
		}

		AccountCreditNoteDetail detail = null;
		for (int i = 0; i < entity.getItems().size(); i++) {
			detail = entity.getItems().get(i);
			
			if (detail.getProduct() == null) {
				throw new RuntimeException(i+1 + ". satırda hizmet/ürün seçilmemiş!" );
			}

			if (detail.getAmount().getValue().compareTo(BigDecimal.ZERO) <= 0) {
				throw new RuntimeException(i+1 + ". satırda Sıfırdan büyük bir değer girmelisiniz!" );
			}
		}
	}

	/**
     * Satırları tek tek dolaşarak tüm fiş toplamlarını yeniden hesaplar...
     */
    public void recalculate() {
    	manualFlush();
    	try {
    		documentCalculationHome.fillCurrencyRates(entity);
    		documentCalculationHome.calculateDocumentTotal(entity);
		} catch (Exception e) {
			log.error("Döküman hesaplamaları yapılırken hata meydana geldi. Sebebi :#0", e);
			facesMessages.add(Severity.ERROR, "Döküman hesaplamaları yapılırken hata meydana geldi. Sebebi : {0}",Utils.getExceptionMessage(e));
		}
    }

    @Override
    @Transactional
    public String delete() {
    	String result = BaseConsts.FAIL;
    	try {
    		accountTxnAction.deleteDocument(entity);
    		result = super.delete();
		} catch (Exception e) {
			log.error("Döküman silinirken hata oluştu. Sebebi :{0}", e.getMessage());
			facesMessages.add(Severity.ERROR, "Döküman silinirken hata oluştu. Sebebi :{0}",e);
		}
        return result;
    }

    public void createNewLine() {
        manualFlush();
        if (entity == null) {
            return;
        }

        AccountCreditNoteDetail it = new AccountCreditNoteDetail();
        it.setOwner(entity);
        entity.getItems().add(it);

        log.debug("yeni item eklendi");
    }

    public void deleteLine(Integer ix) {
        manualFlush();
        if (entity == null) {
            return;
        }
        entity.getItems().remove(ix.intValue());

        log.debug("Toplam Kayıt : {0}", entity.getItems().size());
    }

    public void manualFlush() {
        Conversation.instance().changeFlushMode(org.jboss.seam.annotations.FlushModeType.MANUAL);
    }

}
