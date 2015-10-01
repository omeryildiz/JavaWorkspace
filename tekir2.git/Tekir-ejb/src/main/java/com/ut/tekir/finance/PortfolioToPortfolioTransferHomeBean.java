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

import com.ut.tekir.entities.PortfolioToPortfolioTransfer;
import com.ut.tekir.entities.PortfolioToPortfolioTransferItem;
import com.ut.tekir.framework.BaseConsts;
import com.ut.tekir.framework.CalendarManager;
import com.ut.tekir.framework.EntityBase;
import com.ut.tekir.framework.SequenceManager;
import com.ut.tekir.framework.SequenceType;

/**
 *
 * @author bilge
 */
@Stateful
@Name("portfolioToPortfolioTransferHome")
@Scope(value = ScopeType.CONVERSATION)
public class PortfolioToPortfolioTransferHomeBean extends EntityBase<PortfolioToPortfolioTransfer> implements PortfolioToPortfolioTransferHome<PortfolioToPortfolioTransfer> {

    @In(create = true)
    SequenceManager sequenceManager;
    @In
    CalendarManager calendarManager;
    @In
    BondTxnAction bondTxnAction;

    @Create 
    
    
    @Begin(join=true,flushMode = org.jboss.seam.annotations.FlushModeType.MANUAL)
    public void init() {

    }

    @Out(required = false)
    public PortfolioToPortfolioTransfer getPortfolioToPortfolioTransfer() {
        return getEntity();
    }

    @In(required = false)
    public void setPortfolioToPortfolioTransfer(PortfolioToPortfolioTransfer portfolioToPortfolioTransfer) {
        setEntity(portfolioToPortfolioTransfer);
    }

    @Override
    public void createNew() {
        log.debug("Yeni Portföy Transfer");

        entity = new PortfolioToPortfolioTransfer();
        entity.setActive(true);
        entity.setSerial(sequenceManager.getNewSerialNumber(SequenceType.SERIAL_PORTFOLIOTO_PORTFOLIO));
        //entity.setReference(sequenceManager.getNewSerialNumber(SequenceType.REFERENCE_SHIPMENT_TRANSFER));
        entity.setDate(calendarManager.getCurrentDate());

    }

    @Override
    public String save() {

        //TODO: Hatalara dil desteği eklenecek
        Boolean hata = false;





        try {
            if (entity.getFromPortfolio() != null && entity.getFromPortfolio().equals(entity.getToPortfolio())) {
                facesMessages.add("Giriş ve Çıkış portföyü aynı olamaz!");
                hata = true;
            }


            if (entity.getItems().size() == 0) {
                facesMessages.add("En az bir detay girmelisiniz!");
                hata = true;
            }

            //TODO: Metinlere dildesteği eklenecek
            for (PortfolioToPortfolioTransferItem it : entity.getItems()) {

                if (it.getSecurity() == null) {
                    facesMessages.add("Bono seçmelisiniz!");
                    hata = true;
                }

                if (it.getNominal().compareTo(BigDecimal.ZERO) <= 0) {
                    facesMessages.add("Sıfırdan büyük bir değer girmelisiniz");
                    hata = true;
                }

            }

            if (hata) {
                throw new RuntimeException("Hata!");
            }


        } catch (Exception e) {
            log.error("Hata :", e);
            return BaseConsts.FAIL;
        }


        String res = super.save();

        if (BaseConsts.SUCCESS.equals(res)) {
            bondTxnAction.savePortfolioToPortfolioTransfer(entity);
        }

        return res;

    }

    @Override
    public String delete() {

    	bondTxnAction.deletePortfolioToPortfolioTransfer(entity);

        return super.delete();
    }

    public void createNewLine() {
        manualFlush();
        if (entity == null) {
            return;
        }

        log.debug("EntityManager.FlushMode : #0", entityManager.getFlushMode());

        PortfolioToPortfolioTransferItem it = new PortfolioToPortfolioTransferItem();
        it.setOwner(entity);
        entity.getItems().add(it);
        log.debug("yeni item eklendi");
    }

    public void deleteLine(PortfolioToPortfolioTransferItem item) {
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
    
    public void selectSecurity(Integer ix) {
        manualFlush();
        if (entity == null) {
            return;
        }
        log.debug("İşlenecek IX : {0}", ix);

        PortfolioToPortfolioTransferItem o = entity.getItems().get(ix.intValue());
        if (o != null && o.getSecurity() != null) {
            o.setCurrency(o.getSecurity().getCurrency());
        }
    }
    
    public void manualFlush() {
        Conversation.instance().changeFlushMode(org.jboss.seam.annotations.FlushModeType.MANUAL);
    }
}
