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

import javax.ejb.Stateful;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.Conversation;

import com.ut.tekir.entities.Cheque;
import com.ut.tekir.entities.ChequeStatus;
import com.ut.tekir.entities.DocumentType;
import com.ut.tekir.framework.BaseConsts;
import com.ut.tekir.framework.BrowserBase;
import com.ut.tekir.framework.CalendarManager;

/**
 *
 * @author huseyin
 */
@Stateful
@Name("chequeChangeStatus")
@Scope(ScopeType.CONVERSATION)
public class ChequeChangeStatusBean extends BrowserBase<Cheque, ChequeFilterModel> implements ChequeChangeStatus<Cheque, ChequeFilterModel> {
	
	private ChequeStatus newStatus;
	private Boolean isClientCheque;
	
    @In
    CalendarManager calendarManager;
    @In 
    ChequeAction chequeAction;

    @Override
    public ChequeFilterModel newFilterModel() {
    	ChequeFilterModel fm = new ChequeFilterModel();
        fm.setBeginDate(calendarManager.getLastTenDay());
        fm.setEndDate(calendarManager.getCurrentDate());
        return fm;
    }
    
    public String savePosition() {

    	int counter = 0;
    	manualFlush();
    	
    	try {
    		if(getEntityList() == null){
        		facesMessages.add("Sonuç Listesi Boş. Kayıt Yapılamaz !");
        		throw new RuntimeException("Hatalar Var! Kayıt gerçekleşmedi!");
    		}
    		List <ChequeParamModel> cpml = new ArrayList<ChequeParamModel> ();
    		for(Cheque cm : getEntityList()){
    			
    			if (cm.getChecked()) {
    				Cheque cheque = entityManager.find(Cheque.class, cm.getId());
    				
    				ChequeParamModel cpm = new ChequeParamModel();
    				cpm.setCheque(cheque);
    				cpm.setDocumentId(cheque.getId());
    				cpm.setNewStatus(getNewStatus());
                                //TODO burada document serial olarak ne gonderilecek..
    				if(!chequeAction.isValidAction(cheque, getNewStatus())){
    					facesMessages.add(cheque.getLastStatus() +" Pozisyonundaki Çek İçin Bu İşlemi Gerçekleştirmek Uygun Değil !");
    					throw new RuntimeException("Hatalar Var! Kayıt gerçekleşmedi!");
    				}
    				cpm.setDocumentType(DocumentType.ChequeStatusChanging);
    				cpml.add(cpm);
    				counter++;
    			}
    		}
    		
    		if(counter == 0){
    			
    			facesMessages.add("Çek Seçmelisiniz !");
				throw new RuntimeException("Hatalar Var! Kayıt gerçekleşmedi!");
				
    		}
    		
    		chequeAction.changePosition(cpml);

        	getEntityManager().flush();

    		refreshResults();
    		
		} catch (Exception e) {
			log.error("Hata :", e);
            return BaseConsts.FAIL;
		}
		
        facesMessages.add("#{messages['general.message.record.SaveSuccess']}");	
        return BaseConsts.SUCCESS;
    }

    
    @Override
    public DetachedCriteria buildCriteria() {

        DetachedCriteria crit = DetachedCriteria.forClass(Cheque.class);

/**
 *
 * TODO: contact için outer join gerektiğinden kapatıldı
 *
 *
        crit.createAlias("this.history", "history");
        crit.createAlias("contact", "contact");
        
        crit.setProjection(Projections.projectionList()
             	.add(Projections.groupProperty("this.id"), "id")
                .add(Projections.groupProperty("this.maturityDate"), "maturityDate")
                .add(Projections.groupProperty("this.referenceNo"), "referenceNo")
                .add(Projections.groupProperty("this.bankName"), "bankName")
                .add(Projections.groupProperty("this.bankBranch"), "bankBranch")
                .add(Projections.groupProperty("this.accountNo"), "accountNo")
                .add(Projections.groupProperty("this.chequeOwner"), "chequeOwner")
                .add(Projections.groupProperty("contact.id"), "contactId")
                .add(Projections.groupProperty("contact.name"), "contactName")
                .add(Projections.groupProperty("contact.code"), "contactCode")
                .add(Projections.groupProperty("this.lastStatus"), "lastStatus")
                .add(Projections.groupProperty("this.previousStatus"), "previousStatus")
                .add(Projections.groupProperty("this.info"), "info")
                .add(Projections.groupProperty("money.currency"), "moneyCurrency")
                .add(Projections.groupProperty("money.value"), "moneyValue")
                .add(Projections.groupProperty("this.serialNo"), "serialNo"));
       	 
            crit.setResultTransformer(Transformers.aliasToBean(ChequeSumModelContact.class));
*/
        
        if (isNotEmpty(filterModel.getReferenceNo())) {
            //cheque number
            crit.add(Restrictions.ilike("this.referenceNo", filterModel.getReferenceNo() + "%"));
        }
        
        if (isNotEmpty(filterModel.getBankName())) {
            crit.add(Restrictions.ilike("this.bankName", filterModel.getBankName() + "%"));
        }

        if (isNotEmpty(filterModel.getBankBranch())) {
            crit.add(Restrictions.ilike("this.bankBranch", filterModel.getBankBranch() + "%"));
        }

        if (filterModel.getContact()!=null) {
            crit.add(Restrictions.eq("this.contact", filterModel.getContact()));
        }

        if (isNotEmpty(filterModel.getChequeOwner())) {
            crit.add(Restrictions.ilike("this.chequeOwner", filterModel.getChequeOwner()+ "%"));
        }
        
        if (filterModel.getBeginDate() != null) {
            crit.add(Restrictions.ge("this.statusDate", filterModel.getBeginDate()));
        }

        if (filterModel.getEndDate() != null) {
            crit.add(Restrictions.le("this.statusDate", filterModel.getEndDate()));
        }
        
        if(filterModel.getLastStatus() != null){
        	crit.add(Restrictions.eq("this.lastStatus", filterModel.getLastStatus()));
        }
        
        if(getIsClientCheque() != null){
            crit.add(Restrictions.eq("this.clientCheque", getIsClientCheque()));
        }

        return crit;
    }

    @Observer("refreshBrowser:com.ut.tekir.entities.ChequeFilterModel")
    public void refreshResults() {
        log.debug("Uyarı geldi resultSet tazeleniyor");
        if (getEntityList() == null || getEntityList().isEmpty()) {
            return;
        } 
        search();
    }
    
    public String close() {
        return BaseConsts.SUCCESS;
    }

	public ChequeStatus getNewStatus() {
		return newStatus;
	}

	public void setNewStatus(ChequeStatus newStatus) {
		this.newStatus = newStatus;
	}

	public Boolean getIsClientCheque() {
		return isClientCheque;
	}

	public void setIsClientCheque(Boolean isClientCheque) {
		this.isClientCheque = isClientCheque;
	}

    public ChequeStatus[] getLastStatus(){
    	return ChequeStatus.values();
    }
	
    private void manualFlush() {
        Conversation.instance().changeFlushMode(org.jboss.seam.annotations.FlushModeType.MANUAL);
    }


}
