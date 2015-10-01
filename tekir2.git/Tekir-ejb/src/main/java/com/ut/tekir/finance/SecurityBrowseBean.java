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
import org.hibernate.criterion.Restrictions;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.ScopeType;

import com.ut.tekir.entities.Security;
import com.ut.tekir.entities.SecurityType;
import com.ut.tekir.framework.BrowserBase;
import com.ut.tekir.framework.CalendarManager;


@Stateful
@Name("securityBrowse")
@Scope(ScopeType.SESSION)
public class SecurityBrowseBean extends BrowserBase<Security, SecurityFilterModel> implements SecurityBrowse<Security, SecurityFilterModel> {

	@In
    CalendarManager calendarManager;

    @Override
    public SecurityFilterModel newFilterModel() {
        SecurityFilterModel fm = new SecurityFilterModel();
        fm.setBeginIssueDate(calendarManager.getLastTenDay());
        fm.setBeginMaturityDate(calendarManager.getLastTenDay());
        fm.setEndIssueDate(calendarManager.getCurrentDate());
        fm.setEndMaturityDate(calendarManager.getCurrentDate());
        return fm;
    }
	
	@SuppressWarnings("unchecked")
	public void detailedPdf() {
		// TODO Auto-generated method stub
		Map params = new HashMap();
		if (filterModel.getIsin() != null && filterModel.getIsin().length() > 0) {
            params.put("pSeri", filterModel.getIsin());
        }
		if (filterModel.getCode() != null && filterModel.getCode().length() > 0) {
            params.put("pCode", filterModel.getCode());
        }
		Calendar c = Calendar.getInstance();
        c.set(1900, 1, 1);
        if (filterModel.getBeginIssueDate() != null) {
            params.put("pBDate", filterModel.getBeginIssueDate());
        }

        c.set(2100, 12, 31);
        if (filterModel.getEndIssueDate() != null) {
            params.put("pEDate", filterModel.getEndIssueDate());
        }
        Calendar c2 = Calendar.getInstance();
        c2.set(1900, 1, 1);
        if (filterModel.getBeginMaturityDate() != null) {
            params.put("pBDate", filterModel.getBeginMaturityDate());
        }

        c2.set(9999, 12, 31);
        if (filterModel.getEndMaturityDate() != null) {
            params.put("pEDate", filterModel.getEndMaturityDate());
        }
        
        execPdf("bono_detay", "Bono Detay", params);
	}

	@SuppressWarnings("unchecked")
	public void pdf() {
		// TODO Auto-generated method stub
		Map params = new HashMap();
		if (filterModel.getIsin() != null && filterModel.getIsin().length() > 0) {
            params.put("pSeri", filterModel.getIsin());
        }
		if (filterModel.getCode() != null && filterModel.getCode().length() > 0) {
            params.put("pCode", filterModel.getCode());
        }
		Calendar c = Calendar.getInstance();
        c.set(1900, 1, 1);
        if (filterModel.getBeginIssueDate() != null) {
            params.put("pBDate", filterModel.getBeginIssueDate());
        }

        c.set(2100, 12, 31);
        if (filterModel.getEndIssueDate() != null) {
            params.put("pEDate", filterModel.getEndIssueDate());
        }
        Calendar c2 = Calendar.getInstance();
        c2.set(1900, 1, 1);
        if (filterModel.getBeginMaturityDate() != null) {
            params.put("pBDate", filterModel.getBeginMaturityDate());
        }

        c2.set(9999, 12, 31);
        if (filterModel.getEndMaturityDate() != null) {
            params.put("pEDate", filterModel.getEndMaturityDate());
        }
        
        execPdf("bono_listesi", "Bono Listesi", params);
	}
	
	@Observer("refreshBrowser:com.ut.tekir.entities.Security")
	public void refreshResults() {
		// TODO Auto-generated method stub
		log.debug("Uyarı geldi resultSet tazeleniyor");
        if (getEntityList() == null || getEntityList().isEmpty()) {
            return;
        }
        search();
	}
	
	
	@Override
    public DetachedCriteria buildCriteria() {

        DetachedCriteria crit = DetachedCriteria.forClass(Security.class);

        if (isNotEmpty(filterModel.getIsin())) {
            crit.add(Restrictions.like("isin", filterModel.getIsin() + "%"));
        }

        if (isNotEmpty(filterModel.getCode())) {
            crit.add(Restrictions.like("code", filterModel.getCode() + "%"));
        }

        if (filterModel.getBeginIssueDate() != null) {
            crit.add(Restrictions.ge("issueDate", filterModel.getBeginIssueDate()));
        }

        if (filterModel.getEndIssueDate() != null) {
            crit.add(Restrictions.le("issueDate", filterModel.getEndIssueDate()));
        }
        if (filterModel.getBeginMaturityDate() != null) {
            crit.add(Restrictions.ge("maturityDate", filterModel.getBeginMaturityDate()));
        }

        if (filterModel.getEndMaturityDate() != null) {
            crit.add(Restrictions.le("maturityDate", filterModel.getEndMaturityDate()));
        }
        
        if (filterModel.getSecurityType() != null && !filterModel.getSecurityType().equals(SecurityType.All)) {
            crit.add(Restrictions.eq("securityType", filterModel.getSecurityType()));
        }
        return crit;
    }

}
