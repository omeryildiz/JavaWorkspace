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

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.log.Log;

import com.ut.tekir.entities.Cheque;
import com.ut.tekir.entities.ChequeHistory;
import com.ut.tekir.entities.ChequeStatus;
import com.ut.tekir.entities.DocumentType;
import com.ut.tekir.framework.CalendarManager;

/**
 *
 * @author dumlupinar
 */
@Stateful()
@Name("chequeAction")
@Scope(value = ScopeType.APPLICATION)
@AutoCreate
public class ChequeActionBean implements ChequeAction {

    @Logger
    protected Log log;
    @In
    protected EntityManager entityManager;
    @In
    protected CalendarManager calendarManager;

    public Boolean changePosition(ChequeParamModel cpm) {
		//FIXME: çek ve senetin ortak olarak kullanabilecekleri bir kontrol sınıfı yazmalı.
    	
    	if (!isValidParamModel(cpm)) return false;

    	if (cpm.getCheque().getLastStatus() == null) {
        	cpm.getCheque().setLastStatus(ChequeStatus.Portfoy);
        }

    	if (isValidAction(cpm.getCheque(), cpm.getNewStatus())) {

            ChequeHistory ch = new ChequeHistory();
            ch.setOwner(cpm.getCheque());
            ch.setStatus(cpm.getNewStatus());
            ch.setSource(cpm.getDocumentType());
            ch.setSourceId(cpm.getDocumentId());
            ch.setSerial(cpm.getDocumentSerial());
            ch.setBankAccount(cpm.getToBankAccount());
            ch.setContact(cpm.getToContact());
            ch.setAccount(cpm.getToAccount());
            ch.setDate(cpm.getStatusDate());
            ch.setWorkBunch(cpm.getWorkBunch());
            
            cpm.getCheque().setPreviousStatus(cpm.getCheque().getLastStatus());
            cpm.getCheque().setLastStatus(cpm.getNewStatus());
            cpm.getCheque().setStatusDate(cpm.getStatusDate());

            if (cpm.getCheque().getHistory() == null) {
            	cpm.getCheque().setHistory(new ArrayList<ChequeHistory>());
            }
            cpm.getCheque().getHistory().add(ch);

            return true;
        }
        return false;
    }

    private boolean isValidParamModel(ChequeParamModel cpm) {
        return (cpm.getCheque() != null && cpm.getNewStatus() != null && (cpm.getCheque().getLastStatus() == null || !cpm.getCheque().getLastStatus().equals(cpm.getNewStatus()) || (cpm.getCheque().getLastStatus().equals(ChequeStatus.Portfoy) && cpm.getNewStatus().equals(ChequeStatus.Portfoy))));
    }
    
    public List<Boolean> changePosition(List<ChequeParamModel> cpmList) {
        List<Boolean> result = new ArrayList<Boolean>();
        for (ChequeParamModel cpm : cpmList) {
        	result.add( changePosition(cpm) );
        }
        return result;
    }

    /**
     * Verilen Ã§ekin istenilen pozisyona gidip-gidemeyeceÄŸi kontrol edilir.
     *
     * @param cheque
     * @param newStatus - istenilen yeni pozisyon
     * @return boolean
     */
    public boolean isValidAction(Cheque cheque, ChequeStatus newStatus) {
        boolean result = false;

        //MÃ¼ÅŸteri Ã§eki ise
        if (cheque.getClientCheque()) {

            switch (cheque.getLastStatus()) {

                case Portfoy:
                    result = (newStatus.equals(ChequeStatus.Ciro) || newStatus.equals(ChequeStatus.Portfoy) //Ilk eklenme sirasinda olabilir sadece
                            || newStatus.equals(ChequeStatus.KasaTahsilat) || newStatus.equals(ChequeStatus.BankaTahsilatta) || newStatus.equals(ChequeStatus.BankaTeminat) || newStatus.equals(ChequeStatus.Karsiliksiz) || newStatus.equals(ChequeStatus.Takipte));
                    break;

                case Ciro:
                case BankaTahsilatta:
                    result = (newStatus.equals(ChequeStatus.BankaTahsilEdildi) || newStatus.equals(ChequeStatus.Kapandi) || newStatus.equals(ChequeStatus.Portfoy) || newStatus.equals(ChequeStatus.Karsiliksiz) || newStatus.equals(ChequeStatus.Takipte));
                    break;

                case BankaTeminat:
                    result = (newStatus.equals(ChequeStatus.Portfoy) || newStatus.equals(ChequeStatus.Karsiliksiz) || newStatus.equals(ChequeStatus.Takipte));
                    break;

                case Supheli:
                case KasaTahsilat:
                case BankaTahsilEdildi:
                    result = (newStatus.equals(ChequeStatus.Kapandi));
                    break;

                case Takipte:
                case Karsiliksiz:
                    result = (newStatus.equals(ChequeStatus.Kapandi) || newStatus.equals(ChequeStatus.KasaTahsilat) || newStatus.equals(ChequeStatus.Supheli));
                    break;

            }

            //Firma Ã§eki ise
        } else {

            switch (cheque.getLastStatus()) {

                case Portfoy:
                    result = (newStatus.equals(ChequeStatus.BankaTeminat) || newStatus.equals(ChequeStatus.Cikis) || newStatus.equals(ChequeStatus.Kapandi));
                    break;

                case Cikis:
                    result = (newStatus.equals(ChequeStatus.Portfoy) || newStatus.equals(ChequeStatus.BankaOdeme) || newStatus.equals(ChequeStatus.KasaOdeme));
                    break;

                case KasaOdeme:
                case BankaOdeme:
                    result = (newStatus.equals(ChequeStatus.Kapandi));
                    break;

                case BankaTeminat:
                    result = (newStatus.equals(ChequeStatus.Portfoy));
                    break;

            }

        }

        return result;
    }

    /**
     * Ã‡ek, verilen dÃ¶kÃ¼man tipi ve numarasÄ± ile daha Ã¶nceden kaydedilmiÅŸse
     * tekrar history kaydÄ± oluÅŸmasÄ±nÄ± engellemek iÃ§in, bu dÃ¶kÃ¼man tipi ve
     * numarasÄ± ile daha Ã¶nceden kaydedilmiÅŸ mi diye bakÄ±lÄ±r
     *
     * @param Cheque Ã‡ek
     * @param DocumentType DÃ¶kÃ¼man Tipi @see DocumentType
     * @param DocumentId DÃ¶kÃ¼man Id
     * @return boolean (Ã‡ek in ilgili hareketi yapÄ±p/yapmadÄ±ÄŸÄ±)
     */
    public boolean alreadySaved(Cheque cheque, DocumentType documentType, Long documentId) {

    	Cheque chq = entityManager.find(Cheque.class, cheque.getId());
    	
        if (cheque != null && cheque.getHistory() != null && documentType != null && documentId != null) {

            for (ChequeHistory ch : chq.getHistory()) {
                if (ch.getSource() != null && ch.getSourceId() != null) {
                    if (ch.getSource().equals(documentType) && ch.getSourceId().equals(documentId)) return true;
                }
            }

        }
        return false;
    }

    @Remove
    @Destroy
    public void destroy() {
    }
}
