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

import com.ut.tekir.entities.WorkBunch;
import com.ut.tekir.entities.WorkBunchStatus;
import com.ut.tekir.framework.BrowserBase;
import com.ut.tekir.framework.CalendarManager;

/**
 * @author cenk.canarslan
 *
 */
@Stateful
@Name("workBunchBrowse")
@Scope(ScopeType.SESSION)
public class WorkBunchBrowseBean extends BrowserBase<WorkBunch, WorkBunchFilterModel> implements WorkBunchBrowse<WorkBunch, WorkBunchFilterModel> {

    @In
    CalendarManager calendarManager;

    @Override
    public WorkBunchFilterModel newFilterModel() {
    	WorkBunchFilterModel fm = new WorkBunchFilterModel();
        //fm.setBeginDate(calendarManager.getLastTenDay());
        //fm.setEndDate(calendarManager.getCurrentDate());
        return fm;
    }

    @Override
    public DetachedCriteria buildCriteria() {

        DetachedCriteria crit = DetachedCriteria.forClass(WorkBunch.class);
        if (isNotEmpty(filterModel.getCode())) {
        	crit.add(Restrictions.ilike("this.code", filterModel.getCode(),MatchMode.START));
        }
        if (filterModel.getContact() != null) {
        	crit.add(Restrictions.eq("this.contact", filterModel.getContact()));
        }
        if (filterModel.getBeginDate() != null) {
        	crit.add(Restrictions.ge("this.beginDate", filterModel.getBeginDate()));
        }
        if (filterModel.getEndDate() != null) {
        	crit.add(Restrictions.le("this.endDate", filterModel.getEndDate()));
        }
        if (filterModel.getWorkBunchStatus() != null && filterModel.getWorkBunchStatus() != WorkBunchStatus.All){
        	crit.add(Restrictions.eq("this.workBunchStatus",filterModel.getWorkBunchStatus()));
        }
        if (isNotEmpty(filterModel.getName())){
        	crit.add(Restrictions.ilike("this.name", filterModel.getName(), MatchMode.ANYWHERE));
        }
        crit.addOrder(Order.desc("this.beginDate"));
        crit.addOrder(Order.desc("this.endDate"));
        crit.addOrder(Order.asc("this.code"));
        
        return crit;
    }

    @Observer("refreshBrowser:com.ut.tekir.entities.WorkBunch")
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


/*
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
*/

        execPdf("kasa_cikis_listesi", "Kasa Cikis Listesi", params);

    }

    @SuppressWarnings("unchecked")
	public void detailedPdf() {
        Map params = new HashMap();


/*
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
*/

        execPdf("kasa_cikis_detay", "Kasa Cikis Detayi", params);
    }
}
