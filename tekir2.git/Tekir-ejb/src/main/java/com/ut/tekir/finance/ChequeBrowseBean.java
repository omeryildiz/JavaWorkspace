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

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Scope;

import com.ut.tekir.entities.Cheque;
import com.ut.tekir.entities.ChequeStatus;
import com.ut.tekir.framework.BrowserBase;
import com.ut.tekir.framework.CalendarManager;

/**
 *
 * @author selman
 */
@Stateful
@Name("chequeBrowse")
@Scope(ScopeType.SESSION)
public class ChequeBrowseBean extends BrowserBase<Cheque, ChequeFilterModel> implements ChequeBrowse<Cheque, ChequeFilterModel> {

	private Boolean isClientCheque;
	
    @In
    CalendarManager calendarManager;

    @Override
    public ChequeFilterModel newFilterModel() {
    	ChequeFilterModel fm = new ChequeFilterModel();
        fm.setBeginDate(calendarManager.getLastTenDay());
        fm.setEndDate(calendarManager.getCurrentDate());
        return fm;
    }

    @Override
    public DetachedCriteria buildCriteria() {

        DetachedCriteria crit = DetachedCriteria.forClass(Cheque.class);
        
        if (isNotEmpty(filterModel.getReferenceNo())) {
            crit.add(Restrictions.ilike("referenceNo", filterModel.getReferenceNo(), MatchMode.ANYWHERE));
        }
        
        if (isNotEmpty(filterModel.getBankName())) {
            crit.add(Restrictions.ilike("bankName", filterModel.getBankName(),MatchMode.START));
        }

        if (isNotEmpty(filterModel.getBankBranch())) {
            crit.add(Restrictions.ilike("bankBranch", filterModel.getBankBranch(),MatchMode.START));
        }

        if (isNotEmpty(filterModel.getBankAccountName())) {
            crit.add(Restrictions.ilike("accountNo", filterModel.getBankAccountName(),MatchMode.START));
        }

        if (filterModel.getContact()!=null) {
            crit.add(Restrictions.eq("contact", filterModel.getContact()));
        }

        if (isNotEmpty(filterModel.getChequeOwner())) {
            crit.add(Restrictions.ilike("chequeOwner", filterModel.getChequeOwner(),MatchMode.START));
        }
        
        if (filterModel.getBeginDate() != null) {
            crit.add(Restrictions.ge("maturityDate", filterModel.getBeginDate()));
        }

        if (filterModel.getEndDate() != null) {
            crit.add(Restrictions.le("maturityDate", filterModel.getEndDate()));
        }
        
        if(filterModel.getIssueBeginDate() != null){
        	crit.add(Restrictions.ge("issueDate", filterModel.getIssueBeginDate()));
        }
        
        if(filterModel.getIssueEndDate() != null){
        	crit.add(Restrictions.le("issueDate", filterModel.getIssueEndDate()));
        }
        
        if(filterModel.getLastStatus() != null){
        	crit.add(Restrictions.eq("lastStatus", filterModel.getLastStatus()));
        }
        
        if(filterModel.getPreviousStatus() != null){
        	crit.add(Restrictions.eq("previousStatus", filterModel.getPreviousStatus()));
        }

        if(getIsClientCheque() != null){
            crit.add(Restrictions.eq("clientCheque", getIsClientCheque()));
        }

        crit.addOrder(Order.desc("maturityDate"));
        crit.addOrder(Order.desc("serialNo"));
        crit.addOrder(Order.desc("referenceNo"));
        
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


        execPdf("cek_listesi", "Cek Listesi", params);

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

        execPdf("cek_detay", "Cek Listesi Detayi", params);
    }
    
    public ChequeStatus[] getLastStatus(){
    	return ChequeStatus.values();
    }

	public Boolean getIsClientCheque() {
		return isClientCheque;
	}

	public void setIsClientCheque(Boolean isClientCheque) {
		this.isClientCheque = isClientCheque;
	}

}
