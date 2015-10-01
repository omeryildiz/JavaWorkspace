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

import com.ut.tekir.entities.ChequeStatus;
import com.ut.tekir.entities.DocumentType;
import com.ut.tekir.entities.PromissoryNote;
import com.ut.tekir.entities.PromissoryToBankPaymentPayroll;
import com.ut.tekir.entities.PromissoryToBankPaymentPayrollDetail;
import com.ut.tekir.framework.BaseConsts;
import com.ut.tekir.framework.CalendarManager;
import com.ut.tekir.framework.CurrencyManager;
import com.ut.tekir.framework.EntityBase;
import com.ut.tekir.framework.SequenceManager;
import com.ut.tekir.framework.SequenceType;
import com.ut.tekir.general.GeneralSuggestion;

/**
 * Bankadan senet ödeme bordrosu home bileşenidir.
 * 
 * Açıklama:
 *     Müşteriden alınan çeklerin topluca girilebileceği bordrodur.
 *     Alınan çekler banka ödeme statüsüne girerler.
 *     Banka hareket tablosuna banka için alacak yazar.
 *     Çek tarihçesi tablosunda hareket ile ilgili kayıt atılır.
 * 
 * @author sinan.yumak
 */
@Stateful
@Name("promissoryToBankPaymentPayrollHome")
@Scope(value = ScopeType.CONVERSATION)
public class PromissoryToBankPaymentPayrollHomeBean extends EntityBase<PromissoryToBankPaymentPayroll> implements PromissoryToBankPaymentPayrollHome<PromissoryToBankPaymentPayroll> {

    @In(create = true)
    SequenceManager sequenceManager;
    @In
    CalendarManager calendarManager;
    @In
    BankTxnAction bankTxnAction;
    @In
    PromissorySuggestion promissorySuggestion;
    @In
    PromissoryAction promissoryAction;
    @In
    PromissoryPopupHome promissoryPopupHome;
    @In
    PromissoryChangeLastStatus promissoryChangeLastStatus;
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
    public PromissoryToBankPaymentPayroll getPromissoryToBankPaymentPayroll() {
        return getEntity();
    }

    @In(required = false)
    public void setPromissoryToBankPaymentPayroll(PromissoryToBankPaymentPayroll promissoryToBankPaymentPayroll) {
        setEntity(promissoryToBankPaymentPayroll);
    }

    @Override
    public void createNew() {
        log.debug("Yeni PromissoryToBankPaymentPayroll");
        entity = new PromissoryToBankPaymentPayroll();
        entity.setSerial(sequenceManager.getNewSerialNumber(SequenceType.SERIAL_PROMISSORY_TO_BANK_PAYMENT_PAYROLL));
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

            entity.setBankBranch(entity.getBankAccount().getBankBranch());
            entity.setBank(entity.getBankBranch().getBank());

            res = super.save();

            if (BaseConsts.SUCCESS.equals(res)) {
                for (PromissoryToBankPaymentPayrollDetail cpd : entity.getDetails()) {

                	PromissoryNote promissory = cpd.getPromissory();
                	
                    if (!promissoryAction.alreadySaved(promissory, DocumentType.PromissoryBankPaymentPayroll, entity.getId())) {

                        PromissoryParamModel cpm = new PromissoryParamModel();
                        cpm.setToBankAccount(entity.getBankAccount());
                        cpm.setDocumentId(entity.getId());
                        cpm.setDocumentSerial(entity.getSerial());
                        cpm.setNewStatus(ChequeStatus.BankaOdeme);
                        cpm.setDocumentType(DocumentType.PromissoryBankPaymentPayroll);

                        if (promissory.getMoney().getCurrency().equals(BaseConsts.SYSTEM_CURRENCY_CODE)) // cpm.getCheque().getMoney().setLocalAmount(cpm.getCheque().getMoney().getValue());
                        {
                        	promissory.getMoney().setLocalAmount(promissory.getMoney().getValue());
                        } else {
                        	promissory.getMoney().setLocalAmount(currencyManager.convertToLocal(promissory.getMoney().getValue(),promissory.getMoney().getCurrency(), rateDate));
                        }
                        cpm.setPromissory(promissory);
                        promissoryAction.changePosition(cpm);
                        entityManager.merge(promissory);
                    }
                    bankTxnAction.savePromissoryToBankPaymentPayroll(entity);
                }
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
    public void setEntity(PromissoryToBankPaymentPayroll entity) {
        super.setEntity(entity);
        controllItem();
    }

    @Override
    @Transactional
    public String delete() {

        try {
			bankTxnAction.deletePromissoryToBankPaymentPayroll(entity);

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

    	PromissoryNote promissory = entity.getDetails().get(i).getPromissory();
        try {

            if (promissory.getHistory().size() == 0) {
                entityManager.remove(promissory);
            } else {
            	promissoryChangeLastStatus.setNewStatusOfPromissory(promissory);
            	promissory.getHistory().remove(promissory.getHistory().size() - 1);
                entityManager.merge(promissory);
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
        PromissoryToBankPaymentPayrollDetail detail = new PromissoryToBankPaymentPayrollDetail();
        detail.setOwner(entity);
        entity.getDetails().add(detail);
        log.debug("yeni detay eklendi");
    }

    public void deleteLine(PromissoryToBankPaymentPayrollDetail detail) {
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

    public void popupPromissorySelect(int clientOrFirm) {
        
    	promissorySuggestion.setCallerObserveString("PromissoryToBankPaymentPayrollHome:popupNotice:client.promissory");
    	promissorySuggestion.setClientPromissory(Boolean.FALSE);
    	promissorySuggestion.setTargetStatus(ChequeStatus.BankaOdeme);

    }

    public void controllItem() {
        if (getEntity() == null) {
            return;
        }

        try {
            if (getEntity().getId() != null) {
                if (getEntity().getDetails() != null && getEntity().getDetails().size() != 0) {
                    for (PromissoryToBankPaymentPayrollDetail items : getEntity().getDetails()) {

                        if (items.getId() == null) {
                            return;
                        }

                        PromissoryNote promissory = items.getPromissory();
                        if (promissory.getLastStatus() != ChequeStatus.BankaOdeme) {

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

    @Observer("PromissoryToBankPaymentPayrollHome:popupNotice:client.promissory")
    public void selectClientPromissory(PromissoryNote promissory) {

        manualFlush();

        if (entity == null) {
            return;
        }

        PromissoryToBankPaymentPayrollDetail cpd = new PromissoryToBankPaymentPayrollDetail();
        cpd.setOwner(entity);
        cpd.setPromissory(promissory);
        cpd.getPromissory().setMoney(promissory.getMoney());
        cpd.getPromissory().getMoney().setCurrency(promissory.getMoney().getCurrency());
        cpd.getPromissory().getMoney().setValue(promissory.getMoney().getValue());
        cpd.getPromissory().setInfo(promissory.getInfo());
        cpd.getPromissory().setReferenceNo(promissory.getReferenceNo());
        cpd.getPromissory().getMoney().setCurrency(promissory.getMoney().getCurrency());

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
