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

import java.util.HashMap;
import java.util.Map;

import javax.ejb.Stateful;

import net.sf.jasperreports.engine.JRException;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;

import com.ut.tekir.entities.Cheque;
import com.ut.tekir.entities.ChequeStatus;
import com.ut.tekir.entities.ChequeStub;
import com.ut.tekir.entities.DocumentType;
import com.ut.tekir.framework.BaseConsts;
import com.ut.tekir.framework.CalendarManager;
import com.ut.tekir.framework.EntityBase;
import com.ut.tekir.framework.JasperHandlerBean;
import com.ut.tekir.framework.SequenceManager;
import com.ut.tekir.framework.SequenceType;
import com.ut.tekir.util.NumberToText;
import com.ut.tekir.util.NumberToTextTR;

/**
 *
 * @author selman
 */
@Stateful
@Name("chequeHome")
@Scope(value = ScopeType.CONVERSATION)
public class ChequeHomeBean extends EntityBase<Cheque> implements ChequeHome<Cheque> {
    
	private ChequeStub chequeStub;

	@In
	ChequeAction chequeAction;
    @In(create = true)
    SequenceManager sequenceManager;
    @In
    CalendarManager calendarManager;
    @In( create=true )
    private JasperHandlerBean jasperReport;

    Map params = new HashMap();

    @Create 
    @Begin(flushMode = org.jboss.seam.annotations.FlushModeType.MANUAL, join=true)
    public void init() {

    }
    
    @Out(required = false) 
    public Cheque getCheque() {
        return getEntity();
    }

    @In(required = false)
    public void setCheque(Cheque cheque) {
        setEntity(cheque);
    }
    
    public ChequeStatus[] getChequeStatus(){
    	return ChequeStatus.values();
    }
    
    @Override
	public String save() {

    	ChequeParamModel cpm = new ChequeParamModel();
    	cpm.setCheque(entity);
    	cpm.setDocumentId(null);
    	cpm.setDocumentType(DocumentType.ChequeStatusChanging);
        cpm.setStatusDate(entity.getIssueDate());
    	
    	chequeAction.changePosition(cpm);
    	
    	return super.save();
	}

	public void setChequeFromChequeStub(){
    	if(chequeStub != null){	
    	entity.setAccountNo(chequeStub.getAccountNo());
    	entity.setBankBranch(chequeStub.getBankBranch());
    	entity.setBankName(chequeStub.getBankName());
    	entity.setPaymentPlace(chequeStub.getPaymentPlace());
    	}
    }
    
    @Override
    public void createNew() {
        log.debug("Yeni Çek");
        entity = new Cheque();
        entity.setReferenceNo(sequenceManager.getNewSerialNumber( SequenceType.SERIAL_CHEQUE));
        entity.getMoney().setCurrency(BaseConsts.SYSTEM_CURRENCY_CODE);
        entity.setEntryDate(calendarManager.getCurrentDate());

    }

	/**
	 * @return the chequeStub
	 */
	public ChequeStub getChequeStub() {
		return chequeStub;
	}

	/**
	 * @param chequeStub the chequeStub to set
	 */
	public void setChequeStub(ChequeStub chequeStub) {
		this.chequeStub = chequeStub;
	}

    public void chequePrint(Cheque cheque){
        params.put("pCekId", cheque.getId());
        NumberToText text = new NumberToTextTR();
        // NumberToText sınıfını gözden geçirmek gerek
        params.put("pTutarText", text.convert(cheque.getMoney().getValue().doubleValue(), "TL", "KR"));
        try{
            jasperReport.reportToPDF("cek", "cek", params);
        }catch(JRException ex){
            log.error("Rapor Hatası", ex);
            facesMessages.add("Rapor Çalıştırılamadı");
        }
    }
	
}
