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

package com.ut.tekir.report;
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

import com.ut.tekir.entities.DocumentType;
import com.ut.tekir.entities.TradeAction;
import com.ut.tekir.entities.inv.TekirInvoice;
import com.ut.tekir.framework.BrowserBase;
import com.ut.tekir.framework.CalendarManager;
import com.ut.tekir.framework.InvoiceFilterModel;

/**
 *
 * @author rustem
 */
@Stateful
@Name("saleInvoiceReport")
@Scope(ScopeType.SESSION)
public class SaleInvoiceReportBean extends BrowserBase<TekirInvoice, InvoiceFilterModel> implements SaleInvoiceReport<TekirInvoice, InvoiceFilterModel> {

    @In
    CalendarManager calendarManager;

    @Override
    public InvoiceFilterModel newFilterModel() {
    	InvoiceFilterModel fm = new InvoiceFilterModel();
        fm.setBeginDate(calendarManager.getLastTenDay());
        fm.setEndDate(calendarManager.getCurrentDate());
        return fm;
    }

    @Override
    public DetachedCriteria buildCriteria() {

        DetachedCriteria crit = DetachedCriteria.forClass(TekirInvoice.class);

        crit.createAlias("this.contact", "contact");

        if (isNotEmpty(filterModel.getSerial())) {
            crit.add(Restrictions.ilike("this.serial", filterModel.getSerial(),MatchMode.START));
        }

        if (isNotEmpty(filterModel.getReference())) {
            crit.add(Restrictions.ilike("this.reference", filterModel.getReference(),MatchMode.START));
        }

        if (isNotEmpty(filterModel.getCode())) {
            crit.add(Restrictions.ilike("this.code", filterModel.getCode(),MatchMode.START));
        }

        if (filterModel.getBeginDate() != null) {
            crit.add(Restrictions.ge("this.date", filterModel.getBeginDate()));
        }

        if (filterModel.getEndDate() != null) {
            crit.add(Restrictions.le("this.date", filterModel.getEndDate()));
        }

        if( isNotEmpty(filterModel.getContactCode())){
        	crit.add( Restrictions.ilike("contact.code", filterModel.getContactCode(), MatchMode.START ));
        }

        if( isNotEmpty(filterModel.getContactName())){
        	crit.add( Restrictions.ilike("contact.fullname", filterModel.getContactName(), MatchMode.START));
        }

        crit.add(Restrictions.or(Restrictions.eq("this.tradeAction", TradeAction.Sale),
        						 Restrictions.eq("this.tradeAction", TradeAction.SaleReturn)));

        crit.add(Restrictions.or(Restrictions.eq("this.documentType", DocumentType.RetailSaleShipmentInvoice),Restrictions.or(Restrictions.eq("this.documentType", DocumentType.SaleInvoice), Restrictions.eq("this.documentType", DocumentType.SaleShipmentInvoice))));

        crit.addOrder(Order.desc("this.serial"));
        crit.addOrder(Order.desc("this.date"));

        return crit;
    }

    @Observer("refreshBrowser:com.ut.tekir.entities.inv.TekirInvoice")
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
        if (filterModel.getContactName() != null && filterModel.getContactName().length() > 0) {
            params.put("pName", filterModel.getContactName());
        }

        if (filterModel.getContactCode() != null && filterModel.getContactCode().length() > 0) {
            params.put("pConCode", filterModel.getContactCode());
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

        params.put("pAct", 1);



        execPdf("satis_fatura_listesi", "Satis_Fatura_Listesi", params);

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

        params.put("pAct", 1);


        execPdf("alis-satis-fatura", "Satis_Fatura_Listesi_Detayli", params);
    }
    
    @SuppressWarnings("unchecked")
	public void xls() {
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

        if (filterModel.getContactName() != null && filterModel.getContactName().length() > 0) {
            params.put("pName", filterModel.getContactName());
        }
        
        if (filterModel.getContactCode() != null && filterModel.getContactCode().length() > 0) {
            params.put("pConCode", filterModel.getContactCode());
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

        params.put("pAct", 1);



        execXls("satis_fatura_listesi_excel", "Satis_Fatura_Listesi", params);

    }

    
}
