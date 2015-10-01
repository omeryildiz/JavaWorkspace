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

import java.util.Date;

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

import com.ut.tekir.entities.BankAccount;
import com.ut.tekir.entities.BankBranch;
import com.ut.tekir.entities.Cheque;
import com.ut.tekir.entities.ChequeStatus;
import com.ut.tekir.entities.ChequeToBankPaymentPayroll;
import com.ut.tekir.entities.ChequeToBankPaymentPayrollDetail;
import com.ut.tekir.entities.DocumentType;
import com.ut.tekir.framework.BaseConsts;
import com.ut.tekir.framework.CalendarManager;
import com.ut.tekir.framework.CurrencyManager;
import com.ut.tekir.framework.EntityBase;
import com.ut.tekir.framework.SequenceManager;
import com.ut.tekir.framework.SequenceType;
import com.ut.tekir.general.GeneralSuggestion;

/**
 * Bankadan çek ödeme bordrosu home bileşenidir.
 * 
 * Açıklama:
 *     Müşteriden alınan çeklerin topluca girilebileceği bordrodur.
 *     Alınan çekler banka ödeme statüsüne girerler.
 *     Banka hareket tablosuna banka için alacak yazar.
 *     Çek tarihçesi tablosunda hareket ile ilgili kayıt atılır.
 * 
 * @author yigit, sinan , volkan
 */
@Stateful
@Name("chequeToBankPaymentPayrollHome")
@Scope(value = ScopeType.CONVERSATION)
public class ChequeToBankPaymentPayrollHomeBean extends EntityBase<ChequeToBankPaymentPayroll> implements ChequeToBankPaymentPayrollHome<ChequeToBankPaymentPayroll> {

    @In(create = true)
    SequenceManager sequenceManager;
    @In
    CalendarManager calendarManager;
    @In
    BankTxnAction bankTxnAction;
    @In
    ChequeSuggestion chequeSuggestion;
    @In
    ChequeAction chequeAction;
    @In
    ChequePopupHome chequePopupHome;
    @In
    ChequeChangeLastStatus chequeChangeLastStatus;
    @In
    CurrencyManager currencyManager;
    @In
    GeneralSuggestion generalSuggestion;
    private Boolean isEditable = Boolean.TRUE;
  
    @Create
    @Begin(join = true, flushMode = org.jboss.seam.annotations.FlushModeType.MANUAL)
    public void init() {
    }

    @Out(required = false)
    public ChequeToBankPaymentPayroll getChequeToBankPaymentPayroll() {
        return getEntity();
    }

    @In(required = false)
    public void setChequeToBankPaymentPayroll(ChequeToBankPaymentPayroll chequeToBankPaymentPayroll) {
        setEntity(chequeToBankPaymentPayroll);
    }

    @Override
    public void createNew() {
        log.debug("Yeni ChequeToBankPaymentPayroll");
        entity = new ChequeToBankPaymentPayroll();
        entity.setSerial(sequenceManager.getNewSerialNumber(SequenceType.SERIAL_CHEQUE_TO_BANK_PAYMENT_PAYROLL));
        entity.setActive(true);
        entity.setDate(calendarManager.getCurrentDate());
    }

    @Override
    @Transactional
    public String save() {
        String res = null;

        //entity tarihi geçmiş bir güne aitse o günün, bugün veya geleceğe ait bir günse bugünün kurlarını kullanmak adına
        Date rateDate = entity.getDate();
        if (entity.getDate().compareTo(calendarManager.getCurrentDate()) > 0) {
            rateDate = calendarManager.getCurrentDate();
        }

        try {
            if (entity.getDetails().size() <= 0) {
                facesMessages.add("#{messages['cheque.hint.chequeListEmpty']}");
                return BaseConsts.FAIL;
            }

            Long accountMustBe = entity.getDetails().get(0).getCheque().getBankAccountId();
            Long branchMustBe = entity.getDetails().get(0).getCheque().getBankBranchId();
            entity.setBankBranch(entityManager.find(BankBranch.class, branchMustBe));
            entity.setBank(entity.getBankBranch().getBank());
            entity.setBankAccount(entityManager.find(BankAccount.class, accountMustBe));

            for(ChequeToBankPaymentPayrollDetail detail : entity.getDetails()){
	            if(!detail.getCheque().getBankAccountId().equals(accountMustBe) && !detail.getCheque().getBankBranchId().equals(branchMustBe)){
	                    facesMessages.add("#{messages['cheque.hint.BankAccountsMustBeSame']}");
	                    return BaseConsts.FAIL;
	            }
            }

            res = super.save();

            if (BaseConsts.SUCCESS.equals(res)) {
                for (ChequeToBankPaymentPayrollDetail cpd : entity.getDetails()) {

                    Cheque cheque = cpd.getCheque();

                    if (!chequeAction.alreadySaved(cheque, DocumentType.ChequeBankPaymentPayroll, entity.getId())) {

                        ChequeParamModel cpm = new ChequeParamModel();
                        cpm.setToBankAccount(entity.getBankAccount());
                        cpm.setDocumentId(entity.getId());
                        cpm.setDocumentSerial(entity.getSerial());
                        cpm.setNewStatus(ChequeStatus.BankaOdeme);
                        cpm.setDocumentType(DocumentType.ChequeBankPaymentPayroll);
                        cpm.setStatusDate(entity.getDate()); //INFO: cek tarihcesine bordro kesim tarihi gitmeli

                        
                        if (cheque.getMoney().getCurrency().equals(BaseConsts.SYSTEM_CURRENCY_CODE)) // cpm.getCheque().getMoney().setLocalAmount(cpm.getCheque().getMoney().getValue());
                        {
                            cheque.getMoney().setLocalAmount(cheque.getMoney().getValue());
                        } else {
                           /* if (!kurKontrol) {
                                facesMessages.add("#{messages['general.message.DailyRatesUndefined']}");
                                return BaseConsts.FAIL;
                            }*/
                            cheque.getMoney().setLocalAmount(currencyManager.convertToLocal(cheque.getMoney().getValue(), cheque.getMoney().getCurrency(), rateDate));
                        }
                        cpm.setCheque(cheque);
                        chequeAction.changePosition(cpm);
                        entityManager.merge(cheque);
                    }
                    
                }
                //txn icin ayrica detaylar satir olarak kontrol ediliyor
                bankTxnAction.saveChequeToBankPaymentPayroll(entity);
            }
            entityManager.flush();
            
            log.info("Yeni Bordro Eklendi");
        } catch (Exception e) {
            log.error("Hata :", e);
            e.printStackTrace();
            return BaseConsts.FAIL;
        }
        return res;
    }

    @Override
    public void setEntity(ChequeToBankPaymentPayroll entity) {
        super.setEntity(entity);
        controllItem();
    }

    @Override
    @Transactional
    public String delete() {

        try {
			bankTxnAction.deleteChequeToBankPaymentPayroll(entity);

            for (int i = 0; i < entity.getDetails().size(); i++) {
                deleteItem(i);
            }
            return super.delete();
        } catch (Exception e) {
            facesMessages.add("#{messages['general.message.exception.Exception']}");
            return BaseConsts.FAIL;
        }
    }

    public void deleteItem(int i) {

        Cheque cheque = entity.getDetails().get(i).getCheque();
        try {

            if (cheque.getHistory().size() == 0) {
                entityManager.remove(cheque);
            } else {
                chequeChangeLastStatus.setNewStatusOfCheque(cheque);
                cheque.getHistory().remove(cheque.getHistory().size() - 1);
                entityManager.merge(cheque);
            }

        } catch (Exception e) {
            facesMessages.add("#{messages['general.message.exception.Exception']}");
        }
    }

    public void createNewLine() {
        manualFlush();
        if (entity == null) {
            return;
        }
        ChequeToBankPaymentPayrollDetail detail = new ChequeToBankPaymentPayrollDetail();
        detail.setOwner(entity);
        entity.getDetails().add(detail);
        log.debug("yeni detay eklendi");
    }

    public void deleteLine(ChequeToBankPaymentPayrollDetail detail) {
        manualFlush();
        if (entity == null) {
            return;
        }
        log.debug("Kayıt Silinecek : {0}", detail);
        entity.getDetails().remove(detail);
    }

    public void deleteLine(Integer ix) {
        manualFlush();
        if (entity == null) {
            return;
        }
        log.debug("Kayıt Silinecek IX : {0}", ix);
        log.debug("Toplam Kayıt : {0}", entity.getDetails().size());
        Object o = entity.getDetails().remove(ix.intValue());
        log.debug("Silinen : {0}", o);
        log.debug("Toplam Kayıt : {0}", entity.getDetails().size());
    }

    public void popupChequeSelect(int clientOrFirm) {
        
        chequeSuggestion.setCallerObserveString("ChequeToBankPaymentPayrollHome:popupNotice:client.cheque");
        chequeSuggestion.setClientCheque(Boolean.FALSE);
        chequeSuggestion.setTargetStatus(ChequeStatus.BankaOdeme);
        if(entity.getBankAccount() != null){
            chequeSuggestion.setBankAccountId(entity.getBankAccount());
        }else{
            facesMessages.add("#{messages['chequeToBankPaymentPayroll.message.selectBankFirst']}");
        }
    }

    public void controllItem() {
        if (getEntity() == null) {
            return;
        }

        try {
            if (getEntity().getId() != null) {
                if (getEntity().getDetails() != null && getEntity().getDetails().size() != 0) {
                    for (ChequeToBankPaymentPayrollDetail items : getEntity().getDetails()) {

                        if (items.getId() == null) {
                            return;
                        }

                        Cheque cheque = items.getCheque();
                        if (cheque.getLastStatus() != ChequeStatus.BankaOdeme) {

                            isEditable = false;
                            break;
                        }

                    }
                }
            } else {
                isEditable = true;
            }

        } catch (Exception e) {
            log.error("Hata :", e);
        }
    }

    @Observer("ChequeToBankPaymentPayrollHome:popupNotice:client.cheque")
    public void selectClientCheque(Cheque cheque) {

        manualFlush();

        if (entity == null) {
            return;
        }

        ChequeToBankPaymentPayrollDetail cpd = new ChequeToBankPaymentPayrollDetail();
        cpd.setOwner(entity);
        cpd.setCheque(cheque);
        cpd.getCheque().setMoney(cheque.getMoney());
        cpd.getCheque().getMoney().setCurrency(cheque.getMoney().getCurrency());
        cpd.getCheque().getMoney().setValue(cheque.getMoney().getValue());
        cpd.getCheque().setInfo(cheque.getInfo());
        cpd.getCheque().setReferenceNo(cheque.getReferenceNo());
        cpd.getCheque().getMoney().setCurrency(cheque.getMoney().getCurrency());

        if (entity.getDetails().contains(cpd)) {
            facesMessages.add("#{messages['chequeCollectionAtBankPayroll.message.chequeExist']}");
            FacesMessages.afterPhase();
            return;
        }

        entity.getDetails().add(cpd);

        log.debug("yeni item eklendi");
    }

    @Override
    public String close() {
        Conversation.instance().endBeforeRedirect();
        return super.close();
    }

    public void manualFlush() {
        Conversation.instance().changeFlushMode(org.jboss.seam.annotations.FlushModeType.MANUAL);
    }

    public Boolean getIsEditable() {
        return isEditable;
    }

    public void setIsEditable(Boolean isEditable) {
        this.isEditable = isEditable;
    }
    
}
