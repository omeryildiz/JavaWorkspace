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
import java.util.Iterator;
import java.util.Map;

import javax.ejb.Stateful;

import net.sf.jasperreports.engine.JRException;

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

import com.ut.tekir.entities.Cheque;
import com.ut.tekir.entities.ChequeFromContactPayroll;
import com.ut.tekir.entities.ChequeFromContactPayrollDetail;
import com.ut.tekir.entities.ChequeStatus;
import com.ut.tekir.entities.DocumentType;
import com.ut.tekir.framework.BaseConsts;
import com.ut.tekir.framework.CalendarManager;
import com.ut.tekir.framework.CurrencyManager;
import com.ut.tekir.framework.EntityBase;
import com.ut.tekir.framework.JasperHandlerBean;
import com.ut.tekir.framework.SequenceManager;
import com.ut.tekir.framework.SequenceType;

/**
 * Çek giriş bordrosu home bileşenidir.
 * 
 * Açıklama:
 *     Müşteriden alınan çeklerin topluca girilebileceği bordrodur.
 *     Alınan çekler portföye girer.
 *     Finance hareket tablosuna müşteri için alacak yazar.
 *     Çek tarihçesi tablosunda hareket ile ilgili kayıt atılır.
 *
 * 
 * @author sinan.yumak
 */
@Stateful
@Name("chequeFromContactPayrollHome")
@Scope(value = ScopeType.CONVERSATION)
public class ChequeFromContactPayrollHomeBean extends EntityBase<ChequeFromContactPayroll> implements ChequeFromContactPayrollHome<ChequeFromContactPayroll> {

	@In(create = true)
    SequenceManager sequenceManager;
    @In
    CalendarManager calendarManager;
    @In
    FinanceTxnAction financeTxnAction;
    @In
    ChequeSuggestion chequeSuggestion;
    @In
    ChequeAction chequeAction;
    @In
	CurrencyManager currencyManager;
    @In
    ChequePopupHome chequePopupHome;
    @In( create=true )
    private JasperHandlerBean jasperReport;

    private Boolean isEditable = Boolean.TRUE;
    
    private Boolean isEditCheque = Boolean.FALSE;
    
    @Create 
    @Begin(join=true,flushMode = org.jboss.seam.annotations.FlushModeType.MANUAL)
    public void init() {

    }
    
    @Out(required = false)
	public ChequeFromContactPayroll getChequeFromContactPayroll(){
		return getEntity();
	}
	
	@In(required = false)
	public void setChequeFromContactPayroll(ChequeFromContactPayroll chequeFromContactPayroll){
		setEntity(chequeFromContactPayroll);
	}
	
	@Override
    public void createNew() {
		log.debug("Yeni ChequeFromContactPayroll");
        
		entity = new ChequeFromContactPayroll();
        entity.setSerial(sequenceManager.getNewSerialNumber(SequenceType.SERIAL_CHEQUE_FROM_CONTACT_PAYROLL));
        entity.setActive(true);
        log.info("entity.setActive : {0}", entity.getActive());
        entity.setDate(calendarManager.getCurrentDate());

        log.info("entity.setDate : {0}", entity.getDate());
        isEditable=Boolean.TRUE;
    }

	@Transactional
	public String save() {
		String res = null;
		String idFlag = "insert";
		if(entity.getId() != null){
			idFlag = "update";
		}

		try {
			if(entity.getDetails().size() <= 0){
				facesMessages.add("#{messages['chequePromissory.label.DetailListEmpty']}");
				return BaseConsts.FAIL;
			}

			res = super.save();

			if(res.equals(BaseConsts.SUCCESS)){
				
				for(ChequeFromContactPayrollDetail cpd : entity.getDetails()){
					
					Cheque cheque = cpd.getCheque();
                    // çekin carisini kaydet metodunda da kaydedelim. popupChequeCreate metodunda master da cari seçmeden çek eklenirse çekin carisi null oluyordu.
                    cheque.setContact(entity.getContact());
					if (! chequeAction.alreadySaved(cheque, DocumentType.ChequeFromContactPayroll, entity.getId())) {
						
                    	cheque.setContact(entity.getContact());
	                                        
						ChequeParamModel cpm = new ChequeParamModel();
						cpm.setCheque(cheque);
						cpm.setNewStatus(ChequeStatus.Portfoy);
						cpm.setDocumentId(entity.getId());
	                    cpm.setDocumentSerial(entity.getSerial());
						cpm.setDocumentType(DocumentType.ChequeFromContactPayroll);
	                    cpm.setToContact(entity.getContact());
	                    cpm.setToAccount(entity.getAccount());
	                    cpm.setStatusDate(entity.getDate());
						chequeAction.changePosition(cpm);
						
						if (cheque.getMoney().getCurrency().equals(BaseConsts.SYSTEM_CURRENCY_CODE)){
							cheque.getMoney().setLocalAmount(cheque.getMoney().getValue());
                        } else {
                        	cheque.getMoney().setLocalAmount(currencyManager.convertToLocal(cheque.getMoney().getValue(), cheque.getMoney().getCurrency(),entity.getDate()));
                        }
						
						entityManager.merge(cheque);
					}
					
				}

	            //financeTxnAction.saveChequeFromContactPayroll(entity);
				
				if(idFlag.equals("update")){
					financeTxnAction.deleteChequeFromContactPayrollItem(entity);
				}
				
				for(ChequeFromContactPayrollDetail detail : entity.getDetails()){
					financeTxnAction.saveChequeFromContactPayrollItem(detail);
				}
					
				entityManager.flush();
			}

		} catch (Exception e) {
			FacesMessages.afterPhase();
			facesMessages.clear();
			facesMessages.add(e.getMessage());
			log.error("Hata :", e);
			res = BaseConsts.FAIL;
		}
        return res;
    }

	public void findCheque(int rowKey) {
        Cheque c = entity.getDetails().get(rowKey).getCheque();
        chequePopupHome.setIsClient(false);
        chequePopupHome.setCheque(c);
        chequePopupHome.setCallerObserveString("chequeToContactPayrollHome:popupNotice:client.cheque");        
        isEditCheque=Boolean.TRUE;
    }
	
    @Override
    @Transactional
    public String delete() {
		financeTxnAction.deleteChequeFromContactPayroll(entity);
			
        return super.delete();
    }
    
    public void createNewLine() {
        manualFlush();
        if (entity == null) {
            return;
        }
        ChequeFromContactPayrollDetail detail = new ChequeFromContactPayrollDetail();
        detail.setOwner(entity);
        entity.getDetails().add(detail);
        log.debug("yeni detay eklendi");
    }
   
    public void deleteLine(ChequeFromContactPayrollDetail detail) {
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
 
	public void popupChequeCreate() {
		manualFlush();
		
		chequePopupHome.setCallerObserveString("chequeFromContactPayrollHome:popupNotice:client.cheque");
		chequePopupHome.setIsClient(Boolean.TRUE);
		chequePopupHome.createNew();
		chequePopupHome.getCheque().setContact(entity.getContact());

	}
 
	@Observer("chequeFromContactPayrollHome:popupNotice:client.cheque")
	public void selectClientCheque(Cheque cheque) {
		manualFlush();

		if (entity == null) {
			return;
		}

		ChequeFromContactPayrollDetail cpd = new ChequeFromContactPayrollDetail();
		cpd.setOwner(entity);
		cpd.setCheque(cheque);
		cpd.getCheque().setMoney(cheque.getMoney());
		cpd.getCheque().getMoney().setCurrency(cheque.getMoney().getCurrency());
		cpd.getCheque().getMoney().setValue(cheque.getMoney().getValue());
		cpd.getCheque().setInfo(cheque.getInfo());
		cpd.getCheque().setReferenceNo(cheque.getReferenceNo());

		if (cpd.getCheque().getId() != null && entity.getDetails().contains(cpd)) {
			facesMessages.add("Çek zaten mevcut");
			return;
		}

		entity.getDetails().add(cpd);

		log.debug("yeni item eklendi");
	}
	
    @Override
	public void setEntity(ChequeFromContactPayroll entity) {
		super.setEntity(entity);
		controllItem();
    }

    public void controllItem(){
    	if (getEntity() == null)
			return;
    	
		try {
    		
			if(entity.getId() != null){
        		
				if( entity.getDetails() != null && entity.getDetails().size() > 0){
        			
					for(ChequeFromContactPayrollDetail item : getEntity().getDetails()) {

						if(item.getId() == null){
							return;
						}

						Cheque cheque = item.getCheque();
						if(cheque.getLastStatus() != ChequeStatus.Portfoy){
						
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

    public void manualFlush() {
        Conversation.instance().changeFlushMode(org.jboss.seam.annotations.FlushModeType.MANUAL);
    }

	public Boolean getIsEditable() {
		return isEditable;
	}

	public void setIsEditable(Boolean isEditable) {
		this.isEditable = isEditable;
	}

    public void pdf(ChequeFromContactPayroll payroll){
        Map<Object, Object> params = new HashMap<Object, Object>();
        
    	params.put("pBordroId", payroll.getId());
        try{
            jasperReport.reportToPDF("cekGirisBordrosu", "CekGirisBordrosu", params);
        }catch(JRException ex){
            log.error("Rapor Hatası", ex);
            facesMessages.add("Rapor Çalıştırılamadı");
        }
    }

}
