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

import com.ut.tekir.entities.BankToAccountTransfer;
import com.ut.tekir.entities.FinanceAction;
import com.ut.tekir.framework.BrowserBase;
import com.ut.tekir.framework.CalendarManager;

/**
 *
 * @author huseyin
 */
@Stateful
@Name("bankToAccountTransferBrowse")
@Scope(ScopeType.SESSION)
public class BankToAccountTransferBrowseBean extends BrowserBase<BankToAccountTransfer, BankToAccountTransferFilterModel> implements BankToAccountTransferBrowse<BankToAccountTransfer, BankToAccountTransferFilterModel> {

    @In
    CalendarManager calendarManager;

    @Override
    public BankToAccountTransferFilterModel newFilterModel() {
    	BankToAccountTransferFilterModel fm = new BankToAccountTransferFilterModel();
        fm.setBeginDate(calendarManager.getLastTenDay());
        fm.setEndDate(calendarManager.getCurrentDate());
        return fm;
    }

    @Override
    public DetachedCriteria buildCriteria() {

        DetachedCriteria crit = DetachedCriteria.forClass(BankToAccountTransfer.class);

        if (isNotEmpty(filterModel.getSerial())) {
            crit.add(Restrictions.ilike("serial", filterModel.getSerial(),MatchMode.START));
        }

        if (isNotEmpty(filterModel.getReference())) {
            crit.add(Restrictions.ilike("reference", filterModel.getReference(),MatchMode.START));
        }

        if (isNotEmpty(filterModel.getCode())) {
            crit.add(Restrictions.ilike("code", filterModel.getCode(),MatchMode.START));
        }

        if (filterModel.getBeginDate() != null) {
            crit.add(Restrictions.ge("date", filterModel.getBeginDate()));
        }

        if (filterModel.getEndDate() != null) {
            crit.add(Restrictions.le("date", filterModel.getEndDate()));
        }
        
        if (filterModel.getAccount() != null) {
            crit.add(Restrictions.eq("account", filterModel.getAccount()));
        }
        
        if (filterModel.getBank() != null) {
            crit.add(Restrictions.eq("bank", filterModel.getBank()));
        }
        
        if (filterModel.getBankBranch() != null) {
            crit.add(Restrictions.eq("bankBranch", filterModel.getBankBranch()));
        }
        
        if (filterModel.getBankAccount() != null) {
            crit.add(Restrictions.eq("bankAccount", filterModel.getBankAccount()));
        }

        if (filterModel.getProcessType() != null) {
        	crit.add(Restrictions.eq("this.processType", filterModel.getProcessType()));
        }
        
        if (filterModel.getWorkBunch() != null){
            crit.add(Restrictions.eq("this.workBunch", filterModel.getWorkBunch()));
        }

        crit.add(Restrictions.eq("action", FinanceAction.Debit));
        crit.addOrder(Order.desc("date"));
        crit.addOrder(Order.desc("serial"));
        return crit;
    }

    @Observer("refreshBrowser:com.ut.tekir.entities.BankToAccountTransferFilterModel")
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



        if (filterModel.getSerial() != null && filterModel.getSerial().length() > 0) {
            params.put("pSeri", filterModel.getSerial());
        }

        if (filterModel.getReference() != null && filterModel.getReference().length() > 0) {
            params.put("pRef", filterModel.getReference());
        }

        if (filterModel.getCode() != null && filterModel.getCode().length() > 0) {
            params.put("pCode", filterModel.getCode());
        }
        
        if (filterModel.getAccount() != null) {
        	params.put("account", filterModel.getAccount());
        }
        
        if (filterModel.getBank() != null) {
        	params.put("bank", filterModel.getBank());
        }
        
        if (filterModel.getBankBranch() != null) {
        	params.put("bankBranch", filterModel.getBankBranch());
        }
        
        if (filterModel.getBankAccount() != null) {
        	params.put("bankAccount", filterModel.getBankAccount());
        }

    	params.put("action", FinanceAction.Debit);

        Calendar c = Calendar.getInstance();
        c.set(1900, 1, 1);
        if (filterModel.getBeginDate() != null) {
            params.put("pBDate", filterModel.getBeginDate());
        }

        c.set(2100, 12, 31);
        if (filterModel.getEndDate() != null) {
            params.put("pEDate", filterModel.getEndDate());
        }

        if (filterModel.getWorkBunch() != null){
        	params.put("workBunch", filterModel.getWorkBunch()); 
        }
        
        params.put("pAct", 0);


        execPdf("banka_kasa_transfer_listesi", "Banka Kasa Transfer Listesi", params);

    }

    @SuppressWarnings("unchecked")
	public void detailedPdf() {
        Map params = new HashMap();



        if (filterModel.getSerial() != null && filterModel.getSerial().length() > 0) {
            params.put("pSeri", filterModel.getSerial());
        }

        if (filterModel.getReference() != null && filterModel.getReference().length() > 0) {
            params.put("pRef", filterModel.getReference());
        }

        if (filterModel.getCode() != null && filterModel.getCode().length() > 0) {
            params.put("pCode", filterModel.getCode());
        }
        
        if (filterModel.getAccount() != null) {
        	params.put("account", filterModel.getAccount());
        }
        
        if (filterModel.getBank() != null) {
            params.put("bank", filterModel.getBank());
        }
        
        if (filterModel.getBankBranch() != null) {
        	params.put("bankBranch", filterModel.getBankBranch());
        }
        
        if (filterModel.getBankAccount() != null) {
        	params.put("bankAccount", filterModel.getBankAccount());
        }

    	params.put("action", FinanceAction.Debit);

        Calendar c = Calendar.getInstance();
        c.set(1900, 1, 1);
        if (filterModel.getBeginDate() != null) {
            params.put("pBDate", filterModel.getBeginDate());
        }

        c.set(2100, 12, 31);
        if (filterModel.getEndDate() != null) {
            params.put("pEDate", filterModel.getEndDate());
        }

        if (filterModel.getWorkBunch() != null){
        	params.put("workBunch", filterModel.getWorkBunch()); 
        }
        
        params.put("pAct", 0);

        execPdf("banka_kasa_transfer_detay", "Banka Kasa Transfer Detayi", params);
    }

	public void clearBankAccount() {
		filterModel.setBankBranch(null);
		filterModel.setBankAccount(null);
	}
}
