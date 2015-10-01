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
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Scope;

import com.ut.tekir.entities.ForeignExchange;
import com.ut.tekir.framework.BrowserBase;
import com.ut.tekir.framework.CalendarManager;

/**
 *
 * @author selman
 */
@Stateful
@Name("foreignExchangeBrowse")
@Scope(ScopeType.SESSION)
public class ForeignExchangeBrowseBean extends BrowserBase<ForeignExchange, ForeignExchangeFilterModel> implements ForeignExchangeBrowse<ForeignExchange, ForeignExchangeFilterModel> {

    @In
    CalendarManager calendarManager;

    @Override
    public ForeignExchangeFilterModel newFilterModel() {
        ForeignExchangeFilterModel fm = new ForeignExchangeFilterModel();
        fm.setBeginDate(calendarManager.getLastTenDay());
        fm.setEndDate(calendarManager.getCurrentDate());
        return fm;
    }

    public void clearFromBankAccount(){
    	filterModel.setfromBankAccount(null);
    }
    
    public void clearToBankAccount(){
    	filterModel.setToBankAccount(null);
    }
    
    @Override
    public DetachedCriteria buildCriteria() {

        DetachedCriteria crit = DetachedCriteria.forClass(ForeignExchange.class);

        if (isNotEmpty(filterModel.getSerial())) {
            crit.add(Restrictions.ilike("serial", filterModel.getSerial() + "%"));
        }

        if (isNotEmpty(filterModel.getReference())) {
            crit.add(Restrictions.ilike("reference", filterModel.getReference() + "%"));
        }

        if (isNotEmpty(filterModel.getCode())) {
            crit.add(Restrictions.ilike("code", filterModel.getCode() + "%"));
        }

        if (filterModel.getBeginDate() != null) {
            crit.add(Restrictions.ge("date", filterModel.getBeginDate()));
        }

        if (filterModel.getEndDate() != null) {
            crit.add(Restrictions.le("date", filterModel.getEndDate()));
        }
        
        if (filterModel.getBank() != null) {
            crit.add(Restrictions.eq("bank", filterModel.getBank()));
        }

        if (filterModel.getBankBranch() != null) {
            crit.add(Restrictions.eq("bankBranch", filterModel.getBankBranch()));
        }
       
        if (filterModel.getFromBankAccount() != null) {
            crit.add(Restrictions.eq("fromBankAccount", filterModel.getFromBankAccount()));
        }

        if (filterModel.getToBankAccount() != null) {
            crit.add(Restrictions.eq("toBankAccount", filterModel.getToBankAccount()));
        }

        crit.addOrder(Order.desc("this.date"));
        return crit;
    }

    @Observer("refreshBrowser:com.ut.tekir.entities.ForeignExchange")
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


        Calendar c = Calendar.getInstance();
        c.set(1900, 1, 1);
        if (filterModel.getBeginDate() != null) {
            params.put("pBDate", filterModel.getBeginDate());
        }

        c.set(2100, 12, 31);
        if (filterModel.getEndDate() != null) {
            params.put("pEDate", filterModel.getEndDate());
        }


        if (filterModel.getFromBankAccount() != null) {
            params.put("pFrom", filterModel.getFromBankAccount());
        }

        if (filterModel.getToBankAccount() != null) {
            params.put("pTo", filterModel.getToBankAccount());
        }

        execPdf("bankadan_bankaya_transfer_listesi", "Bankadan Bankaya Transfer Listesi", params);
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


        Calendar c = Calendar.getInstance();
        c.set(1900, 1, 1);
        if (filterModel.getBeginDate() != null) {
            params.put("pBDate", filterModel.getBeginDate());
        }

        c.set(2100, 12, 31);
        if (filterModel.getEndDate() != null) {
            params.put("pEDate", filterModel.getEndDate());
        }


        if (filterModel.getFromBankAccount() != null) {
            params.put("pFrom", filterModel.getFromBankAccount());
        }

        if (filterModel.getToBankAccount() != null) {
            params.put("pTo", filterModel.getToBankAccount());
        }

        execPdf("bankadan_bankaya_transfer_detay", "Bankadan Bankaya Transfer Detay", params);
    }
}
