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

import javax.ejb.Stateful;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;

import com.ut.tekir.entities.Bank;
import com.ut.tekir.entities.BankBranch;
import com.ut.tekir.entities.ChequeStub;
import com.ut.tekir.framework.CalendarManager;
import com.ut.tekir.framework.EntityBase;
import com.ut.tekir.framework.OptionManager;
import com.ut.tekir.framework.SequenceManager;

/**
 *
 * @author selman
 */
@Stateful
@Name("chequeStubHome")
@Scope(value = ScopeType.CONVERSATION)
public class ChequeStubHomeBean extends EntityBase<ChequeStub> implements ChequeStubHome<ChequeStub> {
    
    @In(create = true)
    SequenceManager sequenceManager;
    @In
    CalendarManager calendarManager;
    @In
    OptionManager optionManager;

	private Bank bank;
	private BankBranch bankBranch;

    @Create 
    @Begin(flushMode = org.jboss.seam.annotations.FlushModeType.MANUAL, join=true)
    public void init() {

    }
    
    @Out(required = false) 
    public ChequeStub getChequeStub() {
        return getEntity();
    }

    @In(required = false)
    public void setChequeStub(ChequeStub chequeStub) {
        setEntity(chequeStub);
    }
    
      
      @Override
    public void createNew() {
        log.debug("Yeni Çek");
        entity = new ChequeStub();
        entity.setChequeOwner(optionManager.getOption("company.Title").getAsString());
        //entity.setEntryDate(calendarManager.getCurrentDate());

    }

//    public void manualFlush() {
//        Conversation.instance().changeFlushMode(org.jboss.seam.annotations.FlushModeType.MANUAL);
//    }
    
    @Override
    public String save() {
        entity.setBankName(entity.getBankAccount().getBankBranch().getBank().getName());
        entity.setBankBranch(entity.getBankAccount().getBankBranch().getName());
        entity.setAccountNo(entity.getBankAccount().getAccountNo());
        entity.setIban(entity.getBankAccount().getIban());
        return super.save();
    }

    @Override
    public String edit() {
        if (entity.getBankAccount() != null ) {
            setBankBranch(entity.getBankAccount().getBankBranch());
            setBank(getBankBranch().getBank());
        }
        return super.edit();
    }
    @Destroy
    public void destroy() {
    }

    public void clearBankAccount() {
       setBankBranch(null);
       entity.setBankAccount(null);
    }

    public Bank getBank() {
        return bank;
    }

    public void setBank(Bank bank) {
        this.bank = bank;
    }

    public BankBranch getBankBranch() {
        return bankBranch;
    }

    public void setBankBranch(BankBranch bankBranch) {
        this.bankBranch = bankBranch;
    }

}
