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
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.Conversation;

import com.ut.tekir.entities.ChequeStatus;
import com.ut.tekir.entities.DocumentType;
import com.ut.tekir.entities.PromissoryNote;
import com.ut.tekir.framework.BaseConsts;
import com.ut.tekir.framework.BrowserBase;
import com.ut.tekir.framework.CalendarManager;

/**
 *
 * @author selman
 */
@Stateful
@Name("promissoryChangeStatus")
@Scope(ScopeType.CONVERSATION)
public class PromissoryChangeStatusBean extends BrowserBase<PromissorySumModel, PromissoryFilterModel> implements PromissoryChangeStatus<PromissorySumModel, PromissoryFilterModel>{
	
	private ChequeStatus newStatus;
	private Boolean isClientPromissory;
	
    @In
    CalendarManager calendarManager;
    @In 
    PromissoryAction promissoryAction;

    @Override
    public PromissoryFilterModel newFilterModel() {
    	PromissoryFilterModel fm = new PromissoryFilterModel();
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
	    	List <PromissoryParamModel> cpml = new ArrayList<PromissoryParamModel> ();
			for(PromissorySumModel pm: getEntityList()){
				
				if (pm.getChecked()) {
					PromissoryNote promissory = entityManager.find(PromissoryNote.class, pm.getId());
					
					PromissoryParamModel cpm = new PromissoryParamModel();
					cpm.setPromissory(promissory);
					cpm.setDocumentId(promissory.getId());
					cpm.setNewStatus(getNewStatus());
					if(!promissoryAction.isValidAction(promissory, getNewStatus())){
    					facesMessages.add(promissory.getLastStatus() +" Pozisyonundaki Senet İçin Bu İşlemi Gerçekleştirmek Uygun Değil ! !");
    					throw new RuntimeException("Hatalar Var! Kayıt gerçekleşmedi!");
    				}
					cpm.setDocumentType(DocumentType.PromissoryStatusChanging);
					cpml.add(cpm);
					counter++;
				}
			}
			
			if(counter == 0){
    			
    			facesMessages.add("Senet Seçmelisiniz !");
				throw new RuntimeException("Hatalar Var! Kayıt gerçekleşmedi!");
				
    		}
			
			promissoryAction.changePosition(cpml);
	
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

        DetachedCriteria crit = DetachedCriteria.forClass(PromissoryNote.class);
        
        crit.createAlias("this.history", "history");
        crit.createAlias("contact", "contact");
        
        crit.setProjection(Projections.projectionList()
             	.add(Projections.groupProperty("this.id"), "id")
                .add(Projections.groupProperty("this.maturityDate"), "maturityDate")
                .add(Projections.groupProperty("this.referenceNo"), "referenceNo")
                .add(Projections.groupProperty("this.promissorynoteOwner"), "promissorynoteOwner")
                .add(Projections.groupProperty("contact.id"), "contactId")
                .add(Projections.groupProperty("contact.name"), "contactName")
                .add(Projections.groupProperty("this.lastStatus"), "lastStatus")
                .add(Projections.groupProperty("this.previousStatus"), "previousStatus")
                .add(Projections.groupProperty("this.info"), "info")
                .add(Projections.groupProperty("money.currency"), "moneyCurrency")
                .add(Projections.groupProperty("money.value"), "moneyValue")
                .add(Projections.groupProperty("this.serialNo"), "serialNo"));
        	 
            crit.setResultTransformer(Transformers.aliasToBean(PromissorySumModel.class));
        
        if (isNotEmpty(filterModel.getReferenceNo())) {
            crit.add(Restrictions.eq("this.referenceNo", filterModel.getReferenceNo()));
        }
        
        if (filterModel.getContact()!=null) {
            crit.add(Restrictions.eq("this.contact", filterModel.getContact()));
        }

        if (isNotEmpty(filterModel.getPromissorynoteOwner())) {
            crit.add(Restrictions.ilike("this.promissorynoteOwner", filterModel.getPromissorynoteOwner()+ "%"));
        }
        
        if (filterModel.getBeginDate() != null) {
            crit.add(Restrictions.ge("history.date", filterModel.getBeginDate()));
        }

        if (filterModel.getEndDate() != null) {
            crit.add(Restrictions.le("history.date", filterModel.getEndDate()));
        }
        
        if(filterModel.getLastStatus() != null){
        	crit.add(Restrictions.eq("this.lastStatus", filterModel.getLastStatus()));
        }
        if(getIsClientPromissory() != null){
            crit.add(Restrictions.eq("this.clientPromissoryNote", getIsClientPromissory()));
        }
        return crit;
    }

    @Observer("refreshBrowser:com.ut.tekir.entities.PromissoryFilterModel")
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

	public Boolean getIsClientPromissory() {
		return isClientPromissory;
	}

	public void setIsClientPromissory(Boolean isClientPromissory) {
		this.isClientPromissory = isClientPromissory;
	}
	
    private void manualFlush() {
        Conversation.instance().changeFlushMode(org.jboss.seam.annotations.FlushModeType.MANUAL);
    }

    public ChequeStatus[] getLastStatus(){
    	return ChequeStatus.values();
    }

}
