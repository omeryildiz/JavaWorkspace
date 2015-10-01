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

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.ejb.Stateful;
import javax.persistence.EntityManager;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.Events;
import org.jboss.seam.log.Log;

import com.ut.tekir.entities.ChequeStatus;
import com.ut.tekir.entities.PromissoryNote;
import com.ut.tekir.framework.BaseConsts;
import com.ut.tekir.framework.BrowserBase;
import com.ut.tekir.framework.CalendarManager;

/**
 *
 * @author selman
 */
@Stateful
@Name("promissoryBrowse")
@Scope(ScopeType.SESSION)
@AutoCreate
public class PromissoryBrowseBean extends BrowserBase<PromissoryNote, PromissoryFilterModel> implements PromissoryBrowse<PromissoryNote, PromissoryFilterModel> {
	
	private PromissoryNote promissory;
	private String callerObserveString;
	
	@Logger
    private Log log;
    
    @In
    private EntityManager entityManager;

    @In
    protected Events events;
    
    @In
    CalendarManager calendarManager;

    @Override
    public PromissoryFilterModel newFilterModel() {
    	PromissoryFilterModel fm = new PromissoryFilterModel();
        fm.setBeginDate(calendarManager.getLastTenDay());
        fm.setEndDate(calendarManager.getCurrentDate());
        return fm;
    }

    @Override
    public DetachedCriteria buildCriteria() {

        DetachedCriteria crit = DetachedCriteria.forClass(PromissoryNote.class);
        
        if (isNotEmpty(filterModel.getReferenceNo())) {
            crit.add(Restrictions.ilike("referenceNo", filterModel.getReferenceNo(),MatchMode.ANYWHERE));
        }

        if (filterModel.getContact()!=null) {
            crit.add(Restrictions.eq("contact", filterModel.getContact()));
        }

        if (isNotEmpty(filterModel.getPromissorynoteOwner())) {
            crit.add(Restrictions.ilike("promissorynoteOwner", filterModel.getPromissorynoteOwner(),MatchMode.START));
        }
        
        if (filterModel.getBeginDate() != null) {
            crit.add(Restrictions.ge("maturityDate", filterModel.getBeginDate()));
        }

        if (filterModel.getEndDate() != null) {
            crit.add(Restrictions.le("maturityDate", filterModel.getEndDate()));
        }
        
        if (filterModel.getIssueDate() != null) {
            crit.add(Restrictions.le("issueDate", filterModel.getIssueDate()));
        }
        
        if (filterModel.getIssueBeginDate() != null) {
            crit.add(Restrictions.ge("issueDate", filterModel.getIssueBeginDate()));
        }
        
        if (filterModel.getIssueEndDate() != null) {
            crit.add(Restrictions.le("issueDate", filterModel.getIssueEndDate()));
        }
        
        if(filterModel.getLastStatus() != null){
        	crit.add(Restrictions.eq("lastStatus", filterModel.getLastStatus()));
        }
        
        if(filterModel.getPreviousStatus() != null){
        	crit.add(Restrictions.eq("previousStatus", filterModel.getPreviousStatus()));
        }
        
        if(filterModel.getClientPromissoryNote() != null){
        	crit.add(Restrictions.eq("clientPromissoryNote", filterModel.getClientPromissoryNote()));
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

    @SuppressWarnings("unchecked")
	public void pdf() {
        Map params = new HashMap();

        if (filterModel.getReferenceNo() != null && filterModel.getReferenceNo().length() > 0) {
            params.put("pCode", filterModel.getReferenceNo());
        }

        Calendar c = Calendar.getInstance();
        c.set(1900, 1, 1);
        if (filterModel.getBeginDate() != null) {
            params.put("pBDate", filterModel.getBeginDate());
        }

        c.set(2100, 12, 31);
        if (filterModel.getEndDate() != null) {
            params.put("pEDate", filterModel.getEndDate());
        }

        params.put("pAct", 0);


        execPdf("cek_listesi", "Senet Listesi", params);

    }

    @SuppressWarnings("unchecked")
  public void detailedPdf() {
        Map params = new HashMap();


        if (filterModel.getReferenceNo() != null && filterModel.getReferenceNo().length() > 0) {
            params.put("pCode", filterModel.getReferenceNo());
        }
        
        Calendar c = Calendar.getInstance();
        c.set(1900, 1, 1);
        if (filterModel.getBeginDate() != null) {
            params.put("pBDate", filterModel.getBeginDate());
        }

        c.set(2100, 12, 31);
        if (filterModel.getEndDate() != null) {
            params.put("pEDate", filterModel.getEndDate());
        }

        params.put("pAct", 0);

        execPdf("cek_detay", "Senet Listesi Detayi", params);
    }
    
    public ChequeStatus[] getLastStatus(){
    	return ChequeStatus.values();
    }
    
    public String close() {
        return BaseConsts.SUCCESS;
    }
    
    public void selectedPromissory(Long id) {
    	
    	if (id != null) {
    		promissory= entityManager.find(PromissoryNote.class, id);
    	}
    	
    	if (callerObserveString != null) {
    		events.raiseTransactionSuccessEvent(callerObserveString, promissory);
    		log.debug("Event raised!");
    	} else {
    		log.debug("Event did not raise!");
    	}

    }
	public void setCallerObserveString(String callerString) {
		this.callerObserveString = callerString;
	}


}
