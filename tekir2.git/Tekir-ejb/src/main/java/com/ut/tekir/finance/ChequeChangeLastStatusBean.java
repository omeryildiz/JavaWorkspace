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

import com.ut.tekir.entities.Cheque;
import com.ut.tekir.entities.ChequeHistory;
import com.ut.tekir.entities.ChequeStatus;
import com.ut.tekir.framework.BaseConsts;
import com.ut.tekir.framework.BrowserBase;
import com.ut.tekir.framework.CalendarManager;

/**
 * @author sinan.yumak
 *
 */
@Stateful
@Name("chequeChangeLastStatus")
@Scope(ScopeType.CONVERSATION)
@AutoCreate
public class ChequeChangeLastStatusBean extends BrowserBase<Cheque, ChequeFilterModel> implements ChequeChangeLastStatus<Cheque, ChequeFilterModel> {

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
	
    @Transactional
    public String changeLastStatus(){
    	
    	manualFlush();
    	
    	try {
			
    		if(entityList != null ){
			
    			for(Cheque cheque: getEntityList()){
    				
    				if(cheque.getChecked()){
    					
	    				//ChequeHistory den son kaydı silip, çeke uygun statülerin atamalarının yapılması
    					List<ChequeHistory> historyList = cheque.getHistory();
	    				int historyListLength = historyList.size();

	    				setNewStatusOfCheque(cheque);
	    				
	    				//Finance, Account ve BankTxn den gerekli kayıtların silinmesi
	    				ChequeHistory chequeHistory = historyList.get(historyListLength - 1);
	    				
	    				if(chequeHistory != null){
	    					
		    				//Söz konusu history kaydını siliyoruz. 
		    				cheque.getHistory().remove(historyListLength - 1);
		    				entityManager.persist(cheque);
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
    
    public void setNewStatusOfCheque(Cheque cheque){
    	
    	List<ChequeHistory> historyList = cheque.getHistory();
    	int historyListLength = historyList.size();
    	ChequeStatus previousStatus = null;
    	ChequeStatus lastStatus = null;
    	
		if(historyListLength >= 3){
			
			previousStatus = cheque.getHistory().get(historyListLength - 3).getStatus();
			lastStatus = cheque.getHistory().get(historyListLength - 2).getStatus();
		}else{
			
			previousStatus = cheque.getHistory().get(0).getStatus();
			lastStatus =  previousStatus;
		}
		
		cheque.setPreviousStatus(previousStatus);
		cheque.setLastStatus(lastStatus);
    }
    
//    public void txnKayitlariniSil(ChequeHistory chequeHistory){
//
//    	if(chequeHistory.getAccountTxn() != null){
//			entityManager.createQuery("delete from AccountTxn at where at.id = :id")
//						 .setParameter("id", chequeHistory.getAccountTxn().getId())
//						 .executeUpdate(); 
//		}
//		
//		if(chequeHistory.getFinanceTxn()!= null){
//			entityManager.createQuery("delete from FinanceTxn ft where ft.id = :id")
//						 .setParameter("id", chequeHistory.getFinanceTxn().getId())
//						 .executeUpdate(); 
//		}
//		
//		if(chequeHistory.getBankTxn()!= null){
//			entityManager.createQuery("delete from BankTxn bt where bt.id = :id")
//						 .setParameter("id", chequeHistory.getBankTxn().getId())
//						 .executeUpdate(); 
//		}
//			
//    }
    
    @Override
    public DetachedCriteria buildCriteria() {

        DetachedCriteria crit = DetachedCriteria.forClass(Cheque.class);
        
        crit.createAlias("this.history", "history");
        
        if (isNotEmpty(filterModel.getReferenceNo())) {
            crit.add(Restrictions.eq("referenceNo", filterModel.getReferenceNo()));
        }
        
        if (isNotEmpty(filterModel.getBankName())) {
            crit.add(Restrictions.like("bankName", filterModel.getBankName() + "%"));
        }

        if (isNotEmpty(filterModel.getBankBranch())) {
            crit.add(Restrictions.like("bankBranch", filterModel.getBankBranch() + "%"));
        }

        if (filterModel.getContact()!=null) {
            crit.add(Restrictions.eq("contact", filterModel.getContact()));
        }

        if (isNotEmpty(filterModel.getChequeOwner())) {
            crit.add(Restrictions.like("chequeOwner", filterModel.getChequeOwner()+ "%"));
        }
        
        if (filterModel.getBeginDate() != null) {
            crit.add(Restrictions.ge("history.date", filterModel.getBeginDate()));
        }

        if (filterModel.getLastStatus() != null) {
            crit.add(Restrictions.eq("lastStatus", filterModel.getLastStatus()));
            crit.add(Restrictions.ne("previousStatus", filterModel.getLastStatus()));
        }
        
        if (filterModel.getEndDate() != null) {
            crit.add(Restrictions.le("history.date", filterModel.getEndDate()));
        }

        if (getIsClientCheque() != null) {
            crit.add(Restrictions.eq("clientCheque", getIsClientCheque()));
        }
        
        crit.add(Restrictions.eqProperty("history.status", "this.lastStatus"));
        
        return crit;
    }
	
   @Observer("refreshBrowser:com.ut.tekir.entities.Cheque")
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
	
    private void manualFlush() {
        Conversation.instance().changeFlushMode(org.jboss.seam.annotations.FlushModeType.MANUAL);
    }
}
