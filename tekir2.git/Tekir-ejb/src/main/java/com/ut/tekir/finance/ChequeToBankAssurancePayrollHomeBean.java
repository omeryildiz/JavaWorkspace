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
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.core.Conversation;

import com.ut.tekir.entities.Cheque;
import com.ut.tekir.entities.ChequeStatus;
import com.ut.tekir.entities.ChequeToBankAssurancePayroll;
import com.ut.tekir.entities.ChequeToBankAssurancePayrollDetail;
import com.ut.tekir.entities.DocumentType;
import com.ut.tekir.framework.BaseConsts;
import com.ut.tekir.framework.CalendarManager;
import com.ut.tekir.framework.EntityBase;
import com.ut.tekir.framework.SequenceManager;
import com.ut.tekir.framework.SequenceType;

/**
 * Bankaya çek teminat bordrosu home bileşenidir.
 * 
 * Açıklama:
 *     Portföydeki çeklerin(firma çeklerinin, cirolanan) topluca girilebileceği bordrodur.
 *     Çek tarihçesi tablosunda hareket ile ilgili kayıt atılır.
 *
 * @author sinan.yumak
 */
@Stateful
@Name("chequeToBankAssurancePayrollHome")
@Scope(value = ScopeType.CONVERSATION)
public class ChequeToBankAssurancePayrollHomeBean extends EntityBase<ChequeToBankAssurancePayroll> implements ChequeToBankAssurancePayrollHome<ChequeToBankAssurancePayroll> {

    @In(create = true)
    SequenceManager sequenceManager;
    @In
    CalendarManager calendarManager;
    @In
    ChequeSuggestion chequeSuggestion;
    @In
    ChequeAction chequeAction;
    @In
    ChequePopupHome chequePopupHome;
    @In
    ChequeChangeLastStatus chequeChangeLastStatus;
    private Boolean isEditable = Boolean.TRUE;
    private Boolean isEditCheque = Boolean.FALSE;

    @Create
    @Begin(join = true, flushMode = org.jboss.seam.annotations.FlushModeType.MANUAL)
    public void init() {
    }

    /* (non-Javadoc)
     * @see com.ut.tekir.finance.ChequeToBankAssurancePayrollHome#getChequeToBankAssurancePayroll()
     */
    @Out(required = false)
    public ChequeToBankAssurancePayroll getChequeToBankAssurancePayroll() {
        return getEntity();
    }

    /* (non-Javadoc)
     * @see com.ut.tekir.finance.ChequeToBankAssurancePayrollHome#setChequeToBankAssurancePayroll(com.ut.tekir.entities.ChequeToBankAssurancePayroll)
     */
    @In(required = false)
    public void setChequeToBankAssurancePayroll(ChequeToBankAssurancePayroll chequeToBankAssurancePayroll) {
        setEntity(chequeToBankAssurancePayroll);
    }

    /* (non-Javadoc)
     * @see com.ut.tekir.finance.ChequeToBankAssurancePayrollHome#createNew()
     */
    @Override
    public void createNew() {
        log.debug("Yeni ChequeToBankAssurancePayroll");
        entity = new ChequeToBankAssurancePayroll();
        //TODO:yeni seri numaras� al!
        entity.setSerial(sequenceManager.getNewSerialNumber(SequenceType.SERIAL_CHEQUE_TO_BANK_ASSURANCE_PAYROLL));
        entity.setActive(true);
        log.info("entity.setActive : {0}", entity.getActive());
        entity.setDate(calendarManager.getCurrentDate());
        log.info("entity.setDate : {0}", entity.getDate());
        isEditable = Boolean.TRUE;
    }

    @Override
    public String close() {
        Conversation.instance().endBeforeRedirect();
        return super.close();
    }

    /* (non-Javadoc)
     * @see com.ut.tekir.finance.ChequeToBankAssurancePayrollHome#clearBankAccount()
     */
    public void clearBankAccount() {
        entity.setBankBranch(null);
        entity.setBankAccount(null);
    }

    /* (non-Javadoc)
     * @see com.ut.tekir.finance.ChequeToBankAssurancePayrollHome#save()
     */
    @Transactional
    public String save() {

        Boolean hata;
        hata = false;
        String res = null;
        try {
            if (entity.getDetails().size() <= 0) {
                facesMessages.add("�ek Bilgisi Girilmedi!");
                hata = true;
            }
            if (hata) {
                throw new RuntimeException("Hata!");
            }
        } catch (Exception e) {
            log.error("Hata :", e);
            e.printStackTrace();
            res = BaseConsts.FAIL;
        }

        /* Giriste zorunlu olmadigi icin banka-sube bos kalmasin */
        entity.setBank(entity.getBankAccount().getBankBranch().getBank());
        entity.setBankBranch(entity.getBankAccount().getBankBranch());
        
        res = super.save();
        log.info("Yeni Bordro Eklendi");

        if (res.equals(BaseConsts.SUCCESS)) {

            for (ChequeToBankAssurancePayrollDetail cpd : entity.getDetails()) {

                Cheque cheque = cpd.getCheque();

                if (!chequeAction.alreadySaved(cheque, DocumentType.ChequeToBankAssurancePayroll, entity.getId())) {

                    ChequeParamModel cpm = new ChequeParamModel();
                    cpm.setCheque(cheque);
                    cpm.setDocumentId(entity.getId());
                    cpm.setNewStatus(ChequeStatus.BankaTeminat);
                    cpm.setDocumentSerial(entity.getSerial());
                    cpm.setDocumentType(DocumentType.ChequeToBankAssurancePayroll);
                    cpm.setToBankAccount(entity.getBankAccount());
                    cpm.setStatusDate(entity.getDate());
                    chequeAction.changePosition(cpm);

                    entityManager.merge(cheque);
                }

            }

            entityManager.flush();
        }

        return res;

    }

    /* (non-Javadoc)
     * @see com.ut.tekir.finance.ChequeToBankAssurancePayrollHome#createNewLine()
     */
    public void createNewLine() {
        manualFlush();
        if (entity == null) {
            return;
        }
        ChequeToBankAssurancePayrollDetail detail = new ChequeToBankAssurancePayrollDetail();
        detail.setOwner(entity);
        entity.getDetails().add(detail);
        log.debug("yeni detay eklendi");
    }

    /* (non-Javadoc)
     * @see com.ut.tekir.finance.ChequeToBankAssurancePayrollHome#deleteLine(com.ut.tekir.entities.ChequeToBankAssurancePayrollDetail)
     */
    public void deleteLine(ChequeToBankAssurancePayrollDetail detail) {
        manualFlush();
        if (entity == null) {
            return;
        }
        log.debug("Kay�t Silinecek : {0}", detail);
        entity.getDetails().remove(detail);
    }

    /* (non-Javadoc)
     * @see com.ut.tekir.finance.ChequeToBankAssurancePayrollHome#deleteLine(java.lang.Integer)
     */
    public void deleteLine(Integer ix) {
        manualFlush();
        if (entity == null) {
            return;
        }

        if (entity.getId() != null) {

            deleteItem(ix);
        }

        log.debug("Kay�t Silinecek IX : {0}", ix);
        log.debug("Toplam Kay�t : {0}", entity.getDetails().size());
        Object o = entity.getDetails().remove(ix.intValue());
        log.debug("Silinen : {0}", o);
        log.debug("Toplam Kay�t : {0}", entity.getDetails().size());
    }

    /* (non-Javadoc)
     * @see com.ut.tekir.finance.ChequeToBankAssurancePayrollHome#popupChequeSelect(int)
     */
    public void popupChequeSelect(int clientOrFirm) {

        chequeSuggestion.setCallerObserveString("chequeToBankAssurancePayrollHome:popupNotice:client.cheque");
        chequeSuggestion.setClientCheque(Boolean.TRUE);
        chequeSuggestion.setTargetStatus(ChequeStatus.BankaTeminat);
    }

    /* (non-Javadoc)
     * @see com.ut.tekir.finance.ChequeToBankAssurancePayrollHome#popupChequeCreate()
     */
    public void popupChequeCreate() {

        chequePopupHome.setCallerObserveString("chequeToBankAssurancePayrollHome:popupNotice:client.cheque");
        chequePopupHome.createNew();
    }

    /* (non-Javadoc)
     * @see com.ut.tekir.finance.ChequeToBankAssurancePayrollHome#selectClientCheque(com.ut.tekir.entities.Cheque)
     */
    @Observer("chequeToBankAssurancePayrollHome:popupNotice:client.cheque")
    public void selectClientCheque(Cheque cheque) {

        manualFlush();

        if (entity == null) {
            return;
        }

        ChequeToBankAssurancePayrollDetail cpd = new ChequeToBankAssurancePayrollDetail();
        cpd.setOwner(entity);
        cpd.setCheque(cheque);
        cpd.getCheque().setMoney(cheque.getMoney());
        cpd.getCheque().getMoney().setCurrency(cheque.getMoney().getCurrency());
        cpd.getCheque().getMoney().setValue(cheque.getMoney().getValue());
        cpd.getCheque().setInfo(cheque.getInfo());
        cpd.getCheque().setReferenceNo(cheque.getReferenceNo());
        cpd.getCheque().getMoney().setCurrency(cheque.getMoney().getCurrency());

        if (cpd.getCheque().getId() != null && entity.getDetails().contains(cpd)) {
            facesMessages.add("�ek zaten mevcut");
            return;
        }

        entity.getDetails().add(cpd);

        log.debug("yeni item eklendi");
    }

    @Override
    @Transactional
    public String delete() {

        for (int i = 0; i < entity.getDetails().size(); i++) {

            deleteItem(i);
        }

        return super.delete();
    }

    public void deleteItem(int i) {

        Cheque cheque = entity.getDetails().get(i).getCheque();
        try {

            cheque.getHistory().remove(cheque.getHistory().size() - 1);

            if (cheque.getClientCheque().equals(Boolean.TRUE)) {
                chequeChangeLastStatus.setNewStatusOfCheque(cheque);
                entityManager.merge(cheque);
            } else {
                if (cheque.getHistory().size() == 0) {
                    entityManager.remove(cheque);
                } else {
                    chequeChangeLastStatus.setNewStatusOfCheque(cheque);
                    entityManager.merge(cheque);
                }
            }
        } catch (Exception e) {
            // TODO: handle exception
            facesMessages.add("Silmede hata!");
        }
    }

    public void findCheque(int rowKey) {

        Cheque c = entity.getDetails().get(rowKey).getCheque();
        chequePopupHome.setIsClient(false);
        chequePopupHome.setCheque(c);
        chequePopupHome.setCallerObserveString("chequeToBankAssurancePayrollHome:popupNotice:client.cheque");

        isEditCheque = Boolean.TRUE;
    }

    public void controllItem() {

        if (entity == null) {
            return;
        }

        try {

            if (entity.getId() != null) {

                if (entity.getDetails() != null && entity.getDetails().size() > 0) {

                    for (ChequeToBankAssurancePayrollDetail items : getEntity().getDetails()) {

                        if (items.getId() == null) {
                            return;
                        }
                        Cheque cheque = items.getCheque();
                        if (cheque.getLastStatus() != ChequeStatus.BankaTeminat) {

                            isEditable = false;
                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("Hata :", e);
        }
    }

    /* (non-Javadoc)
     * @see com.ut.tekir.finance.ChequeToBankAssurancePayrollHome#manualFlush()
     */
    public void manualFlush() {
        Conversation.instance().changeFlushMode(org.jboss.seam.annotations.FlushModeType.MANUAL);
    }

    public Boolean getIsEditable() {
        return isEditable;
    }

    public void setIsEditable(Boolean isEditable) {
        this.isEditable = isEditable;
    }

    @Override
    public void setEntity(ChequeToBankAssurancePayroll entity) {
        super.setEntity(entity);
        controllItem();
    }

    public Boolean getIsEditCheque() {
        return isEditCheque;
    }

    public void setIsEditCheque(Boolean isEditCheque) {
        this.isEditCheque = isEditCheque;
    }
}
