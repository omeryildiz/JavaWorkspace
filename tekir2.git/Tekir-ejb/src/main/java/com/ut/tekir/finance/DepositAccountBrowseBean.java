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

import com.ut.tekir.entities.DepositAccount;
import com.ut.tekir.framework.BrowserBase;
import com.ut.tekir.framework.CalendarManager;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import javax.ejb.Stateful;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Scope;

/**
 *
 * @author huseyin
 */
@Stateful
@Name("depositAccountBrowse")
@Scope(ScopeType.SESSION)
public class DepositAccountBrowseBean extends BrowserBase<DepositAccount, DepositAccountFilterModel> implements DepositAccountBrowse<DepositAccount, DepositAccountFilterModel> {

    @In
    CalendarManager calendarManager;

    @Override
    public DepositAccountFilterModel newFilterModel() {
    	DepositAccountFilterModel fm = new DepositAccountFilterModel();
        fm.setBeginDate(calendarManager.getLastTenDay());
        fm.setEndDate(calendarManager.getCurrentDate());
        return fm;
    }

    @Override
    public DetachedCriteria buildCriteria() {

        DetachedCriteria crit = DetachedCriteria.forClass(DepositAccount.class);

        if (isNotEmpty(filterModel.getSerial())) {
            crit.add(Restrictions.like("serial", filterModel.getSerial() + "%"));
        }

        if (isNotEmpty(filterModel.getReference())) {
            crit.add(Restrictions.like("reference", filterModel.getReference() + "%"));
        }

        if (isNotEmpty(filterModel.getCode())) {
            crit.add(Restrictions.like("code", filterModel.getCode() + "%"));
        }
        
        if (filterModel.getBeginDate() != null) {
            crit.add(Restrictions.ge("date", filterModel.getBeginDate()));
        }

        if (filterModel.getEndDate() != null) {
            crit.add(Restrictions.le("date", filterModel.getEndDate()));
        }
        
        return crit;
    }

    @Observer("refreshBrowser:com.ut.tekir.entities.DepositAccount")
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

        params.put("pAct", 0);


        execPdf("vadeli_hesap_listesi", "Banka Kasa Transfer Listesi", params);

    }

    @SuppressWarnings("unchecked")
	public void detailedPdf() {
        Map params = new HashMap();


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

        params.put("pAct", 0);

        execPdf("vadeli_hesap_detay", "Banka Kasa Transfer Detayi", params);
    }


}
