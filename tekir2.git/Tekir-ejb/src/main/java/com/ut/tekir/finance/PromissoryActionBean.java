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

import com.ut.tekir.entities.ChequeStatus;
import com.ut.tekir.entities.DocumentType;
import com.ut.tekir.entities.PromissoryNote;
import com.ut.tekir.entities.PromissoryNoteHistory;
import com.ut.tekir.framework.CalendarManager;
/**
 *
 * @author dumlupinar
 */
@Stateful()
@Name("promissoryAction")
@Scope(value = ScopeType.APPLICATION)
@AutoCreate
public class PromissoryActionBean implements PromissoryAction {

    @Logger
    protected Log log;
    
    @In
    protected EntityManager entityManager;
    
    @In
    protected CalendarManager calendarManager;
    
	public Boolean changePosition(PromissoryParamModel ppm) {
		//FIXME: çek ve senetin ortak olarak kullanabilecekleri bir kontrol sınıfı yazmalı.
		if (!isValidParamModel(ppm)) return false;
	
		if (ppm.getPromissory().getLastStatus() == null) ppm.getPromissory().setLastStatus(ChequeStatus.Portfoy);

		if (isValidAction(ppm.getPromissory(), ppm.getNewStatus())) {
			
			PromissoryNoteHistory ph = new PromissoryNoteHistory();
			ph.setOwner(ppm.getPromissory());
			ph.setStatus(ppm.getNewStatus());
			ph.setSource(ppm.getDocumentType());
			ph.setSourceId(ppm.getDocumentId());
			ph.setWorkBunch(ppm.getWorkBunch());
			ph.setContact(ppm.getToContact());
			ph.setAccount(ppm.getToAccount());
			ph.setSerial(ppm.getDocumentSerial());
			ppm.getPromissory().setPreviousStatus(ppm.getPromissory().getLastStatus());
			ppm.getPromissory().setLastStatus(ppm.getNewStatus());

			if (ppm.getPromissory().getHistory()==null) ppm.getPromissory().setHistory(new ArrayList<PromissoryNoteHistory>());
			ppm.getPromissory().getHistory().add(ph);

			return true;
			
		}
		return false;
	}
	
    private boolean isValidParamModel(PromissoryParamModel ppm) {
		return (ppm.getPromissory() != null && ppm.getNewStatus() != null && (ppm.getPromissory().getLastStatus() == null || ! ppm.getPromissory().getLastStatus().equals(ppm.getNewStatus()) || (ppm.getPromissory().getLastStatus().equals(ChequeStatus.Portfoy) && ppm.getNewStatus().equals(ChequeStatus.Portfoy))));

    }
	
    public List<Boolean> changePosition(List<PromissoryParamModel> ppmList) {
		List<Boolean> result = new ArrayList<Boolean>();
		for(PromissoryParamModel ppm: ppmList) {
			result.add( changePosition(ppm) );
		}
		return result;
	}
	
	/**
	 * Verilen çekin istenilen pozisyona gidip-gidemeyeceği kontrol edilir.
	 * 
	 * @param promissory
	 * @param newStatus - istenilen yeni pozisyon
	 * @return boolean
	 */
	public boolean isValidAction(PromissoryNote promissory, ChequeStatus newStatus) {
		boolean result = false;

		//Müşteri çeki ise
		if (promissory.getClientPromissoryNote()) {
			
			switch (promissory.getLastStatus()) {
			
				case Portfoy:
					result = (newStatus.equals(ChequeStatus.Ciro)
							   || newStatus.equals(ChequeStatus.Portfoy) //Ilk eklenme sirasinda olabilir sadece
							   || newStatus.equals(ChequeStatus.KasaTahsilat)
							   || newStatus.equals(ChequeStatus.BankaTahsilatta)
							   || newStatus.equals(ChequeStatus.BankaTeminat)
							   || newStatus.equals(ChequeStatus.Karsiliksiz)
							   || newStatus.equals(ChequeStatus.Takipte)
							   );
					break;
		
				case Ciro:
				case BankaTahsilatta:
					result = (newStatus.equals(ChequeStatus.BankaTahsilEdildi)
							   || newStatus.equals(ChequeStatus.Kapandi)
							   || newStatus.equals(ChequeStatus.Portfoy)
							   || newStatus.equals(ChequeStatus.Karsiliksiz)
							   || newStatus.equals(ChequeStatus.Takipte)
							   );
					break;
		
				case BankaTeminat:
					result = (newStatus.equals(ChequeStatus.Portfoy)
							   || newStatus.equals(ChequeStatus.Karsiliksiz)
							   || newStatus.equals(ChequeStatus.Takipte)
							   );
					break;
					
				case Supheli:
				case KasaTahsilat:
				case BankaTahsilEdildi:
					result = (newStatus.equals(ChequeStatus.Kapandi)
							   	);
					break;
		
				case Takipte:
				case Karsiliksiz:
					result = (newStatus.equals(ChequeStatus.Kapandi)
							   || newStatus.equals(ChequeStatus.KasaTahsilat)
							   || newStatus.equals(ChequeStatus.Supheli)
							   );
					break;
					
			}

		//Firma çeki ise
		} else {
			
			switch (promissory.getLastStatus()) {
			
				case Portfoy:
					result = (newStatus.equals(ChequeStatus.BankaTeminat)
							   || newStatus.equals(ChequeStatus.Cikis)
							   || newStatus.equals(ChequeStatus.Kapandi)
							   );
					break;
		
				case Cikis:
					result = (newStatus.equals(ChequeStatus.Portfoy)
								|| newStatus.equals(ChequeStatus.BankaOdeme)
								|| newStatus.equals(ChequeStatus.KasaOdeme)
								);
					break;
					
				case KasaOdeme:
				case BankaOdeme:
					result = (newStatus.equals(ChequeStatus.Kapandi)
							   );
					break;
		
				case BankaTeminat:
					result = (newStatus.equals(ChequeStatus.Portfoy)
								);
					break;
					
			}
			
		}
	
		return result;
	}
	
	/**
	 * Senet, verilen döküman tipi ve numarası ile daha önceden kaydedilmişse 
	 * tekrar history kaydı oluşmasını engellemek için, bu döküman tipi ve 
	 * numarası ile daha önceden kaydedilmiş mi diye bakılır
	 * 
	 * @param PromissoryNote Senet
	 * @param DocumentType Döküman Tipi @see DocumentType
	 * @param DocumentId Döküman Id
	 * @return boolean (Senet in ilgili hareketi yapıp/yapmadığı)
	 */
	public boolean alreadySaved(PromissoryNote promissory, DocumentType documentType, Long documentId) {
		
		PromissoryNote pn = entityManager.find(PromissoryNote.class, promissory.getId());
		
		if (promissory != null && promissory.getHistory() != null && documentType != null && documentId != null) {
			
			for (PromissoryNoteHistory ph: pn.getHistory()) {
				if (ph.getSource() != null && ph.getSourceId() != null) {
					if (ph.getSource().equals(documentType) && ph.getSourceId().equals(documentId)) return true;
				}
			}
			
		}
		return false;
	}

    @Remove @Destroy
    public void destroy() {

    }

}