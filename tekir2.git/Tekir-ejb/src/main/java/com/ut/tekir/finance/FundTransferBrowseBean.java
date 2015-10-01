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

import com.ut.tekir.entities.FundTransfer;
import com.ut.tekir.framework.BrowserBase;
import com.ut.tekir.framework.CalendarManager;
import com.ut.tekir.framework.DefaultDocumentFilterModel;
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

/**
 *
 * @author haky
 */
@Stateful
@Name("fundTransferBrowse")
@Scope(ScopeType.SESSION)
public class FundTransferBrowseBean extends BrowserBase<FundTransfer, DefaultDocumentFilterModel> implements FundTransferBrowse<FundTransfer, DefaultDocumentFilterModel> {

    @In
    CalendarManager calendarManager;

    @Override
    public DefaultDocumentFilterModel newFilterModel() {
        DefaultDocumentFilterModel fm = new DefaultDocumentFilterModel();
        fm.setBeginDate(calendarManager.getLastTenDay());
        fm.setEndDate(calendarManager.getCurrentDate());
        return fm;
    }

    @Override
    public DetachedCriteria buildCriteria() {

        DetachedCriteria crit = DetachedCriteria.forClass(FundTransfer.class);

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
        crit.addOrder(Order.desc("serial"));
        crit.addOrder(Order.desc("date"));
        
        return crit;
    }

    @Observer("refreshBrowser:com.ut.tekir.entities.FundTransfer")
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


        execPdf("kasa_hareket_listesi", "Kasa Hareketi Listesi", params);

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


        execPdf("kasa_hareketi_detay", "Kasa Hareketi Detayi", params);
    }
}
