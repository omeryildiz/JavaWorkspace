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
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.Events;

import com.ut.tekir.entities.ChequeFromContactPayroll;
import com.ut.tekir.framework.BrowserBase;
import com.ut.tekir.framework.CalendarManager;

/**
 * Çek giriş bordrosu browse bileşenidir.
 * 
 * @author sinan.yumak
 */
@Stateful
@Name("chequeFromContactPayrollBrowse")
@Scope(value = ScopeType.SESSION)
public class ChequeFromContactPayrollBrowseBean extends BrowserBase<ChequeFromContactPayroll, ChequeFromContactPayrollFilterModel> implements ChequeFromContactPayrollBrowse<ChequeFromContactPayroll, ChequeFromContactPayrollFilterModel> {

	@In
    CalendarManager calendarManager;

    @In
    protected Events events;
    
    @Override
    public ChequeFromContactPayrollFilterModel newFilterModel() {
    	ChequeFromContactPayrollFilterModel fm = new ChequeFromContactPayrollFilterModel();
        fm.setBeginDate(calendarManager.getLastTenDay());
        fm.setEndDate(calendarManager.getCurrentDate());
        return fm;
    }

    @Override
    public DetachedCriteria buildCriteria() {
        DetachedCriteria crit = DetachedCriteria.forClass(ChequeFromContactPayroll.class);

        crit.createAlias("this.contact", "contact");
        crit.createAlias("this.account", "account");

        crit.setProjection(Projections.projectionList()
				.add(Projections.property("this.id"), "id")
				.add(Projections.property("this.serial"), "serial")
				.add(Projections.property("this.reference"), "reference")
				.add(Projections.property("this.code"), "code")
				.add(Projections.property("this.date"), "date")
				.add(Projections.property("this.info"), "info")
				.add(Projections.property("contact.code"), "contactCode")
				.add(Projections.property("contact.person"), "person")
				.add(Projections.property("contact.company"), "company")
				.add(Projections.property("contact.fullname"), "contactName")
				.add(Projections.property("account.code"), "accountCode")
				.add(Projections.property("account.name"), "accountName")
		)
		.setResultTransformer(Transformers.aliasToBean(ChequeFromContactPayrollFilterModel.class));

        if (isNotEmpty(filterModel.getSerial())) {
            crit.add(Restrictions.like("this.serial", filterModel.getSerial() + "%"));
        }

        if (isNotEmpty(filterModel.getReference())) {
            crit.add(Restrictions.like("this.reference", filterModel.getReference() + "%"));
        }

        if (isNotEmpty(filterModel.getCode())) {
            crit.add(Restrictions.like("this.code", filterModel.getCode() + "%"));
        }

        if( isNotEmpty(filterModel.getContactCode())){
        	crit.add( Restrictions.ilike("contact.code", filterModel.getContactCode(), MatchMode.START ));
        }

        if( isNotEmpty(filterModel.getContactName())){
        	crit.add( Restrictions.ilike("contact.fullname", filterModel.getContactName(), MatchMode.ANYWHERE));
        }
        
        if (filterModel.getBeginDate() != null) {
            crit.add(Restrictions.ge("this.date", filterModel.getBeginDate()));
        }

        if (filterModel.getEndDate() != null) {
            crit.add(Restrictions.le("this.date", filterModel.getEndDate()));
        }
        
        crit.addOrder(Order.desc("this.date"));
        crit.addOrder(Order.desc("this.serial"));
        
        return crit;
    }

    @Observer("refreshBrowser:com.ut.tekir.entities.ChequeFromContactPayroll")
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

        params.put("pAct", 0);

        execPdf("çek_giris_bordro_listesi", "Çek Giriş Bordro Listesi", params);

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

        params.put("pAct", 0);

        execPdf("cek_giris_bordro_detay", "Çek Giriş Bordro Detayi", params);
    }
    
}
