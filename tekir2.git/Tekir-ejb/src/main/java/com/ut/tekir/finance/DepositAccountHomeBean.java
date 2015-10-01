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

import com.ut.tekir.entities.DepositAccount;
import com.ut.tekir.framework.BaseConsts;
import com.ut.tekir.framework.CalendarManager;
import com.ut.tekir.framework.CurrencyManager;
import com.ut.tekir.framework.EntityBase;
import com.ut.tekir.framework.SequenceManager;
import com.ut.tekir.framework.SequenceType;
import com.ut.tekir.util.Utils;

/**
 * Vadeli hesap home bileşenidir.
 * @author huseyin
 */
@Stateful
@Name("depositAccountHome")
@Scope(value = ScopeType.CONVERSATION)
public class DepositAccountHomeBean extends EntityBase<DepositAccount> implements DepositAccountHome<DepositAccount> {
    
    @In
    BankTxnAction bankTxnAction;
    @In(create = true)
    SequenceManager sequenceManager;
    @In
    CalendarManager calendarManager;
    @In
    CurrencyManager currencyManager;
    
    @Create 
    @Begin(join=true, flushMode = org.jboss.seam.annotations.FlushModeType.MANUAL)
    public void init() {
    }
    
    @Out(required = false) 
    public DepositAccount getDepositAccount() {
        return getEntity();
    }

    @In(required = false)
    public void setDepositAccount(DepositAccount depositAccount) {
        setEntity(depositAccount);
    }

    @Override
    public void createNew() {
        log.debug("Yeni DepositAccount");
        entity = new DepositAccount();
        entity.setDepositBeginDate(calendarManager.getCurrentDate());
        entity.setDepositEndDate(calendarManager.getCurrentDate());
        entity.setSerial(sequenceManager.getNewSerialNumber( SequenceType.SERIAL_FINANCE_DEPOSITACCOUNT));
        entity.setDate(calendarManager.getCurrentDate());
    }

    @Override
    @Transactional
    public String save() {
		log.info("Saving deposit account. Serial :{0}", entity.getSerial());
		String result = BaseConsts.FAIL;
		
        try {
			makeEntityValidations();

			entity.getAmount().setCurrency(entity.getDepositBankAccount().getCurrency());

			currencyManager.setLocalAmountOf(entity.getAmount(), entity.getDate());

			result = super.save();

			if( result.equals(BaseConsts.SUCCESS)){
				bankTxnAction.saveDepositAccount(entity);
			}
        } catch (Exception e) {
        	facesMessages.add(Utils.getExceptionMessage(e));
        	log.error("Hata : #0", e);
        }
        return result;
    }

    private void makeEntityValidations() {
    	if (entity.getDepositBeginDate().after(entity.getDepositEndDate())) {
            throw new RuntimeException("Başlangıç tarihi bitiş tarihinden sonrası olamaz!");
    	}
    	if (( entity.getAmount().getValue().compareTo(BigDecimal.ZERO) ) < 0) {
            throw new RuntimeException("Yatırılan tutar sıfırdan büyük olmalıdır!");
    	}
    	if (( entity.getRate().compareTo(BigDecimal.ZERO) ) < 0) {
            throw new RuntimeException("Faiz oranı sıfırdan büyük olmalıdır!");
    	}
    	if (( entity.getProfit().compareTo(BigDecimal.ZERO) ) < 0) {
            throw new RuntimeException("Kazanç sıfırdan büyük olmalıdır!");
    	}
    	if (( entity.getTax().compareTo(BigDecimal.ZERO) ) < 0) {
            throw new RuntimeException("Vergi sıfırdan büyük olmalıdır!");
    	}
	}

	@Override
	@Transactional
	public String delete() {
		String result = BaseConsts.SUCCESS;
		try {
			bankTxnAction.deleteDepositAccount(entity);
			
			result = super.delete();
		} catch (Exception e) {
			facesMessages.add(Utils.getExceptionMessage(e));
			log.info("Fiş silinirken hata meydana geldi. Sebebi : #0", e);
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

	public void clearDepositBankAccount() {
		entity.setDepositBankBranch(null);
		entity.setDepositBankAccount(null);
	}

}
