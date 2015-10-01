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

import com.ut.tekir.entities.PortfolioToPortfolioTransfer;
import com.ut.tekir.framework.BrowserBase;
import com.ut.tekir.framework.CalendarManager;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import javax.ejb.Stateful;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Scope;

/**
 *
 * @author bilge
 */
@Stateful
@Name("portfolioToPortfolioTransferBrowse")
@Scope(ScopeType.SESSION)
public class PortfolioToPortfolioTransferBrowseBean extends BrowserBase<PortfolioToPortfolioTransfer, PortfolioToPortfolioTransferFilterModel> implements PortfolioToPortfolioTransferBrowse<PortfolioToPortfolioTransfer, PortfolioToPortfolioTransferFilterModel> {

    @In
    CalendarManager calendarManager;

    @Override
    public PortfolioToPortfolioTransferFilterModel newFilterModel() {
        PortfolioToPortfolioTransferFilterModel fm = new PortfolioToPortfolioTransferFilterModel();
        fm.setBeginDate(calendarManager.getLastTenDay());
        fm.setEndDate(calendarManager.getCurrentDate());
        return fm;
    }

    @Override
    public DetachedCriteria buildCriteria() {

        DetachedCriteria crit = DetachedCriteria.forClass(PortfolioToPortfolioTransfer.class);

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

        if (filterModel.getFromPortfolio() != null) {
            crit.add(Restrictions.eq("fromPortfolio", filterModel.getFromPortfolio()));
        }

        if (filterModel.getToPortfolio() != null) {
            crit.add(Restrictions.eq("toPortfolio", filterModel.getToPortfolio()));
        }

        return crit;
    }

    @Observer("refreshBrowser:com.ut.tekir.entities.PortfolioToPortfolioTransfer")
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


        if (filterModel.getFromPortfolio() != null) {
            params.put("pFrom", filterModel.getFromPortfolio().getCode());
        }

        if (filterModel.getToPortfolio() != null) {
            params.put("pTo", filterModel.getToPortfolio().getCode());
        }



        execPdf("depo_sevk_listesi", "Depo Sevk Listesi", params);

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


        if (filterModel.getFromPortfolio() != null) {
            params.put("pFrom", filterModel.getFromPortfolio().getCode());
        }

        if (filterModel.getToPortfolio() != null) {
            params.put("pTo", filterModel.getToPortfolio().getCode());
        }


        execPdf("depo_sevk_detay", "Depo Sevk Detay", params);
    }
}
