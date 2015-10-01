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

import java.util.List;

import javax.ejb.Stateful;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.core.Conversation;

import com.ut.tekir.entities.ChequeStatus;
import com.ut.tekir.entities.PromissoryNote;
import com.ut.tekir.entities.PromissoryNoteHistory;
import com.ut.tekir.framework.BaseConsts;
import com.ut.tekir.framework.BrowserBase;
import com.ut.tekir.framework.CalendarManager;

/**
 * @author sinan.yumak
 *
 */
@Stateful
@Name("promissoryChangeLastStatus")
@Scope(ScopeType.CONVERSATION)
@AutoCreate
public class PromissoryChangeLastStatusBean extends BrowserBase<PromissoryNote, PromissoryFilterModel> implements PromissoryChangeLastStatus<PromissoryNote, PromissoryFilterModel> {

	private ChequeStatus newStatus;
	private Boolean isClientPromissory;
	
    @In
    CalendarManager calendarManager;
    
    @Override
    public PromissoryFilterModel newFilterModel() {
    	PromissoryFilterModel fm = new PromissoryFilterModel();
        fm.setBeginDate(calendarManager.getLastTenDay());
        fm.setEndDate(calendarManager.getCurrentDate());
        return fm;
    }
	
    @Transactional
    public String changeLastStatus(){
    	
    	manualFlush();
    	
    	try {
			
    		if(entityList != null ){
			
    			for(PromissoryNote promissory: getEntityList()){
    				
    				if(promissory.getChecked()){
    					
	    				//PromissoryNoteHistory den son kaydı silip, çeke uygun statülerin atamalarının yapılması
    					List<PromissoryNoteHistory> historyList = promissory.getHistory();
	    				int historyListLength = historyList.size();

	    				setNewStatusOfPromissory(promissory);
	    				
	    				//Finance, Account ve BankTxn den gerekli kayıtların silinmesi
	    				PromissoryNoteHistory promissoryHistory = historyList.get(historyListLength - 1);
	    				
	    				if(promissoryHistory != null){
	    					
		    				//Söz konusu history kaydını siliyoruz. 
		    				promissory.getHistory().remove(historyListLength - 1);
		    				entityManager.persist(promissory);
		    				entityManager.flush();
		    				
		    				//Finance, Account ve BankTxn den gerekli kayıtların silinmesi
//		    				txnKayitlariniSil(tempHistory);	
	    				}
	    				
	    			}
    		
    			}
    		}
    		
		} catch (Exception e) {
			
			facesMessages.add("Hata!");
			log.info("hata : {0}", e.fillInStackTrace());
			return BaseConsts.FAIL;
		}
    	
		search();
		facesMessages.add("Hareket başarıyla geri alındı");
    	return BaseConsts.SUCCESS;
    }
    
    public void setNewStatusOfPromissory(PromissoryNote promissory){
    	
    	List<PromissoryNoteHistory> historyList = promissory.getHistory();
    	int historyListLength = historyList.size();
    	ChequeStatus previousStatus = null;
    	ChequeStatus lastStatus = null;
    	
		if(historyListLength >= 3){
			
			previousStatus = promissory.getHistory().get(historyListLength - 3).getStatus();
			lastStatus = promissory.getHistory().get(historyListLength - 2).getStatus();
		}else{
			
			previousStatus = promissory.getHistory().get(0).getStatus();
			lastStatus =  previousStatus;
		}
		
		promissory.setPreviousStatus(previousStatus);
		promissory.setLastStatus(lastStatus);
    }
    
//    public void txnKayitlariniSil(PromissoryNoteHistory promissoryHistory){
//
//    	if(promissoryHistory.getAccountTxn() != null){
//			entityManager.createQuery("delete from AccountTxn at where at.id = :id")
//						 .setParameter("id", promissoryHistory.getAccountTxn().getId())
//						 .executeUpdate(); 
//		}
//		
//		if(promissoryHistory.getFinanceTxn()!= null){
//			entityManager.createQuery("delete from FinanceTxn ft where ft.id = :id")
//						 .setParameter("id", promissoryHistory.getFinanceTxn().getId())
//						 .executeUpdate(); 
//		}
//		
//		if(promissoryHistory.getBankTxn()!= null){
//			entityManager.createQuery("delete from BankTxn bt where bt.id = :id")
//						 .setParameter("id", promissoryHistory.getBankTxn().getId())
//						 .executeUpdate(); 
//		}
//			
//    }
    
    @Override
    public DetachedCriteria buildCriteria() {

        DetachedCriteria crit = DetachedCriteria.forClass(PromissoryNote.class);
        
        crit.createAlias("this.history", "history");
        
        if (isNotEmpty(filterModel.getReferenceNo())) {
            crit.add(Restrictions.eq("referenceNo", filterModel.getReferenceNo()));
        }
        
        if (filterModel.getContact()!=null) {
            crit.add(Restrictions.eq("contact", filterModel.getContact()));
        }

        if (isNotEmpty(filterModel.getPromissorynoteOwner())) {
            crit.add(Restrictions.like("promissorynoteOwner", filterModel.getPromissorynoteOwner()+ "%"));
        }

        if (filterModel.getBeginDate() != null) {
            crit.add(Restrictions.ge("history.date", filterModel.getBeginDate()));
        }

        if (filterModel.getEndDate() != null) {
            crit.add(Restrictions.le("history.date", filterModel.getEndDate()));
        }

        if (filterModel.getLastStatus() != null) {
            crit.add(Restrictions.eq("lastStatus", filterModel.getLastStatus()));
            crit.add(Restrictions.ne("previousStatus", filterModel.getLastStatus()));
        }

        if (getIsClientPromissory() != null) {
            crit.add(Restrictions.eq("clientPromissoryNote", getIsClientPromissory()));
        }

        crit.add(Restrictions.eqProperty("history.status", "this.lastStatus"));
        
        return crit;
    }
	
   @Observer("refreshBrowser:com.ut.tekir.entities.PromissoryNote")
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
}
