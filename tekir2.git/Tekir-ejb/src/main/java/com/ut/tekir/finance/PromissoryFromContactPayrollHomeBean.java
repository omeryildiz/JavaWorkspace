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
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.core.Conversation;

import com.ut.tekir.entities.Cheque;
import com.ut.tekir.entities.ChequeStatus;
import com.ut.tekir.entities.DocumentType;
import com.ut.tekir.entities.PromissoryFromContactPayroll;
import com.ut.tekir.entities.PromissoryFromContactPayrollDetail;
import com.ut.tekir.entities.PromissoryNote;
import com.ut.tekir.framework.BaseConsts;
import com.ut.tekir.framework.CalendarManager;
import com.ut.tekir.framework.EntityBase;
import com.ut.tekir.framework.JasperHandlerBean;
import com.ut.tekir.framework.SequenceManager;
import com.ut.tekir.framework.SequenceType;

/**
 * Senet giriş bordrosu home bileşenidir.
 * 
 * Açıklama:
 *     Müşteriden alınan senetlerin topluca girilebileceği bordrodur.
 *     Alınan senetler portföye girer.
 *     Finance hareket tablosuna müşteri için alacak yazar.
 *     Senet tarihçesi tablosunda hareket ile ilgili kayıt atılır.
 *
 * 
 * @author sinan.yumak
 */
@Stateful
@Name("promissoryFromContactPayrollHome")
@Scope(value = ScopeType.CONVERSATION)
public class PromissoryFromContactPayrollHomeBean extends EntityBase<PromissoryFromContactPayroll> implements PromissoryFromContactPayrollHome<PromissoryFromContactPayroll> {

	@In(create = true)
    SequenceManager sequenceManager;
    @In
    CalendarManager calendarManager;
    @In
    FinanceTxnAction financeTxnAction;
    @In
    PromissorySuggestion promissorySuggestion;
    @In
    PromissoryAction promissoryAction;
    @In
    PromissoryPopupHome promissoryPopupHome;
    @In( create=true )
    private JasperHandlerBean jasperReport;

    private Boolean isEditable = Boolean.TRUE;
    
    private Boolean isEditPromissory = Boolean.TRUE;
    
    @Create 
    @Begin(join=true,flushMode = org.jboss.seam.annotations.FlushModeType.MANUAL)
    public void init() {

    }
    
    @Out(required = false)
	public PromissoryFromContactPayroll getPromissoryFromContactPayroll(){
		return getEntity();
	}
	
	@In(required = false)
	public void setPromissoryFromContactPayroll(PromissoryFromContactPayroll promissoryFromContactPayroll){
		setEntity(promissoryFromContactPayroll);
	}
	
	@Override
    public void createNew() {
		log.debug("Yeni PromissoryFromContactPayroll");

		entity = new PromissoryFromContactPayroll();
        entity.setSerial(sequenceManager.getNewSerialNumber(SequenceType.SERIAL_PROMISSORY_FROM_CONTACT_PAYROLL));
        entity.setActive(true);
        log.info("entity.setActive : {0}", entity.getActive());
        entity.setDate(calendarManager.getCurrentDate());

        log.info("entity.setDate : {0}", entity.getDate());
    }

	@Transactional
	public String save() {
		String res = null;
		
		try {
		
			if(entity.getDetails().size() <= 0){
				facesMessages.add("Senet Bilgisi Girilmedi!");
				return BaseConsts.FAIL;
			}

			res = super.save();

			if(res.equals(BaseConsts.SUCCESS)){
				
				for(PromissoryFromContactPayrollDetail cpd : entity.getDetails()){
					PromissoryNote promissory = cpd.getPromissory();
                    // senedin carisini kaydet metodunda da kaydedelim. popupPromissoryCreate metodunda master da cari seçmeden senet eklenirse senedin carisi null oluyordu.
                    promissory.setContact(entity.getContact());
					if (! promissoryAction.alreadySaved(promissory, DocumentType.PromissoryFromContactPayroll, entity.getId())) {
						
						PromissoryParamModel cpm = new PromissoryParamModel();
						cpm.setPromissory(promissory);
						cpm.setNewStatus(ChequeStatus.Portfoy);
						cpm.setDocumentId(entity.getId());
						cpm.setDocumentType(DocumentType.PromissoryFromContactPayroll);
						promissoryAction.changePosition(cpm);
						
	    				entityManager.merge(promissory);
					}
				}
	            financeTxnAction.savePromissoryFromContactPayroll(entity);

				entityManager.flush();
			}

		} catch (Exception e) {
			log.error("Hata :", e);
			e.printStackTrace();
            res = BaseConsts.FAIL;
		}
        return res;
    }
	
	public void controllItem() {
		if (getEntity() == null)
			return;

		try {
			if (getEntity().getId() != null) {
				if (getEntity().getDetails() != null
						&& getEntity().getDetails().size() != 0) {
					for (PromissoryFromContactPayrollDetail items : getEntity().getDetails()) {
						
						if(items.getId() == null){
							return;
						}

							PromissoryNote promissoryNote=  items.getPromissory();

							if (promissoryNote.getLastStatus() != ChequeStatus.Portfoy) {

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

	public void findPromissory(int rowKey) {
        PromissoryNote p = entity.getDetails().get(rowKey).getPromissory();
        promissoryPopupHome.setIsClient(false);
        promissoryPopupHome.setPromissory(p);
        promissoryPopupHome.setCallerObserveString("promissoryToContactPayrollHome:popupNotice:client.promissory");        
        isEditPromissory=Boolean.TRUE;
		
	}
	
	
	
    @Override
    @Transactional
    public String delete() {

    	financeTxnAction.deletePromissoryFromContactPayroll(entity);
    	
        return super.delete();
    }

    public void createNewLine() {
        manualFlush();
        if (entity == null) {
            return;
        }
        PromissoryFromContactPayrollDetail detail = new PromissoryFromContactPayrollDetail();
        detail.setOwner(entity);
        entity.getDetails().add(detail);
        log.debug("yeni detay eklendi");
    }

    public void deleteLine(PromissoryFromContactPayrollDetail detail) {
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

	public void popupPromissoryCreate() {
		manualFlush();
		
		promissoryPopupHome.setCallerObserveString("promissoryFromContactPayrollHome:popupNotice:client.promissory");
		promissoryPopupHome.setIsClient(Boolean.TRUE);
		promissoryPopupHome.createNew();
		promissoryPopupHome.getPromissory().setContact(entity.getContact());
		
	}
 
	@Observer("promissoryFromContactPayrollHome:popupNotice:client.promissory")
	public void selectClientPromissory(PromissoryNote promissory) {
		manualFlush();

		if (entity == null) {
			return;
		}

		PromissoryFromContactPayrollDetail cpd = new PromissoryFromContactPayrollDetail();
		cpd.setOwner(entity);
		cpd.setPromissory(promissory);
		cpd.getPromissory().setMoney(promissory.getMoney());
		cpd.getPromissory().getMoney().setCurrency(promissory.getMoney().getCurrency());
		cpd.getPromissory().getMoney().setValue(promissory.getMoney().getValue());
		cpd.getPromissory().setInfo(promissory.getInfo());
		cpd.getPromissory().setReferenceNo(promissory.getReferenceNo());

		if (cpd.getPromissory().getId() != null && entity.getDetails().contains(cpd)) {
			facesMessages.add("Senet zaten mevcut");
			return;
		}

		entity.getDetails().add(cpd);

		log.debug("yeni item eklendi");
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
    
    @Override
	public void setEntity(PromissoryFromContactPayroll entity) {
		super.setEntity(entity);
		controllItem();
	}

    public void pdf(PromissoryFromContactPayroll payroll){
        Map<Object, Object> params = new HashMap<Object, Object>();

    	params.put("pBordroId", payroll.getId());
        try{
            jasperReport.reportToPDF("senetGirisBordrosu", "senetGirisBordrosu", params);
        }catch(JRException ex){
            log.error("Rapor Hatası", ex);
            facesMessages.add("Rapor Çalıştırılamadı");
        }
    }

}
