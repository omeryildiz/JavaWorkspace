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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateful;

import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.persistence.HibernateSessionProxy;

import com.ut.tekir.configuration.SystemConfiguration;
import com.ut.tekir.entities.AdvanceProcessType;
import com.ut.tekir.entities.Contact;
import com.ut.tekir.entities.ContactType;
import com.ut.tekir.entities.DocumentType;
import com.ut.tekir.entities.FinanceAction;
import com.ut.tekir.entities.FinanceTxn;
import com.ut.tekir.entities.WorkBunch;
import com.ut.tekir.framework.BrowserBase;
import com.ut.tekir.framework.CalendarManager;
import com.ut.tekir.report.ReportParameters;

/**
 *
 * @author haky
 */
@Stateful
@Name("financeTxnReport")
@Scope(ScopeType.SESSION)
public class FinanceTxnReportBean extends BrowserBase<FinanceTxn, FinanceTxnFilterModel> implements FinanceTxnReport<FinanceTxn, FinanceTxnFilterModel> {

    @In
    CalendarManager calendarManager;
    
   
    @Override
    public FinanceTxnFilterModel newFilterModel() {
        FinanceTxnFilterModel fm = new FinanceTxnFilterModel();
        fm.setBeginDate(calendarManager.getLastTenDay());
        fm.setEndDate(calendarManager.getCurrentDate());
        return fm;
    }

    @SuppressWarnings("unchecked")
	@Override
    public void search() {
        //super.search();
        log.debug("Rapora Başlıyoruz");

        HibernateSessionProxy session = (HibernateSessionProxy) getEntityManager().getDelegate();

        Criteria ecrit = buildCriteria().getExecutableCriteria(session);
        ecrit.setCacheable(true);

        log.debug("Kriterler build edildi");

        List ls = ecrit.list();

        log.debug("Sorgu çekildi");

        if (filterModel.getLocalCurrencyOnly()) {

            log.debug("Gruplama yapıcağız...");

            List<FinanceTxn> res = new ArrayList<FinanceTxn>();
            FinanceTxn row;

            for (Iterator it = ls.iterator(); it.hasNext();) {
                Object obj[] = (Object[]) it.next();

                row = new FinanceTxn();
                row.setContact(new Contact());

                row.setDocumentType((DocumentType) obj[0]);
                row.setDocumentId((Long) obj[1]);
                row.setSerial((String) obj[2]);
                row.setReference((String) obj[3]);
                row.setDate((Date) obj[4]);
                row.getContact().setName((String) obj[5]);
                row.getContact().setCode((String) obj[6]);
                row.setCode((String) obj[7]);
                row.setInfo((String) obj[8]);
                row.setAction((FinanceAction) obj[9]);
                row.getAmount().setLocalAmount((BigDecimal) obj[10]); 
                row.setProcessType((AdvanceProcessType)obj[11]);
                row.getAmount().setCurrency((String) obj[12]);
                //log.info("istakip icin arraydegeri : #0", obj[13]);
                if (obj[13] != null ) {
                	row.setWorkBunch((WorkBunch) obj[13] ); 
                	//log.info("istakip icin eklenen1 : #0", row.getWorkBunch().getCode());
                } 
                
                res.add(row);

                log.debug("Row : #0", row);

            }

            setEntityList(res);

        } else {
            setEntityList(ls);
        }




    }

    @Override
    public DetachedCriteria buildCriteria() {

        DetachedCriteria crit = DetachedCriteria.forClass(FinanceTxn.class);
        
        crit.createAlias("contact", "contact");

        if (isNotEmpty(filterModel.getSerial())) {
            crit.add(Restrictions.ilike("this.serial", filterModel.getSerial() ,MatchMode.START));
        }

        if (isNotEmpty(filterModel.getReference())) {
            crit.add(Restrictions.ilike("this.reference", filterModel.getReference(),MatchMode.START));
        }

        if (isNotEmpty(filterModel.getCode())) {
            crit.add(Restrictions.ilike("this.code", filterModel.getCode() ,MatchMode.START));
        }

        if (filterModel.getBeginDate() != null) {
            crit.add(Restrictions.ge("this.date", filterModel.getBeginDate()));
        }

        if (filterModel.getEndDate() != null) {
            crit.add(Restrictions.le("this.date", filterModel.getEndDate()));
        }

        if (isNotEmpty(filterModel.getContactName())){
        	crit.add(Restrictions.ilike("contact.fullname", filterModel.getContactName(),MatchMode.START));
        }

        if (isNotEmpty(filterModel.getContactCode())){
        	crit.add(Restrictions.ilike("contact.code", filterModel.getContactCode(),MatchMode.START));
        }
        
        if (filterModel.getCompanyType() !=null && !filterModel.getCompanyType().equals("All")){
            if(filterModel.getCompanyType().equals("Person")){
                crit.add(Restrictions.eq("contact.person", Boolean.TRUE));
            }else
                crit.add(Restrictions.eq("contact.person", Boolean.FALSE));
        }
        
        if (filterModel.getType() != null && filterModel.getType() != ContactType.All) {
    		crit.add(Restrictions.eq("contact." + filterModel.getType().toString().toLowerCase() + "Type", Boolean.TRUE));
        }
        
        if (filterModel.getCategory() != null) {
            crit.add(Restrictions.eq("contact.category", filterModel.getCategory()));
        }
        
        if (filterModel.getExCode1() != null && filterModel.getExCode1().length() > 0) {
            crit.add(Restrictions.ilike("contact.exCode1", filterModel.getExCode1()  ,MatchMode.START));
        }

        if (filterModel.getExCode2() != null && filterModel.getExCode2().length() > 0) {
            crit.add(Restrictions.ilike("contact.exCode2", filterModel.getExCode2()  ,MatchMode.START));
        }
        
        if (filterModel.getOrganization() != null) {
        	crit.add(Restrictions.eq("contact.organization", filterModel.getOrganization()));
        }
        
        if ( filterModel.getActive() != null ){
        	crit.add(Restrictions.eq("this.active", filterModel.getActive()));
        }

        if (filterModel.getDocumentType() != null && filterModel.getDocumentType() != DocumentType.Unknown) {
            crit.add(Restrictions.eq("this.documentType", filterModel.getDocumentType()));
        }
        if(filterModel.getContact() != null){
        	crit.add(Restrictions.eq("contact.id", filterModel.getContact().getId()));
        }
        
        if(filterModel.getWorkBunch() != null ){
        	crit.add(Restrictions.eq("this.workBunch", filterModel.getWorkBunch()));
        }

        if (filterModel.getCurrency() != null) {
            crit.add(Restrictions.eq("this.amount.currency", filterModel.getCurrency()));
        }
        
        if (filterModel.getLocalCurrencyOnly() ) {
            crit.setProjection(Projections.projectionList()
            		.add(Projections.groupProperty("documentType"), "documentType")
            		.add(Projections.groupProperty("documentId"), "documentId")
            		.add(Projections.groupProperty("serial"), "serial")
            		.add(Projections.groupProperty("reference"), "reference")
            		.add(Projections.groupProperty("date"), "date")
            		.add(Projections.groupProperty("contact.fullname"), "contactName")
            		.add(Projections.groupProperty("contact.code"), "contactCode")
            		.add(Projections.groupProperty("code"), "code")
            		.add(Projections.groupProperty("info"), "info")
            		.add(Projections.groupProperty("action"), "action")
            		.add(Projections.sum("amount.localAmount"), "localAmount")
            		.add(Projections.sum("processType"), "processType") 
            		.add(Projections.sum("amount.currency"), "currency")
           			.add(Projections.groupProperty("workBunch"), "workBunch")          		
            		);
            log.info("yerel secili : #0", crit);
        }
        
        if (filterModel.getProcessType() != null) {
        	crit.add(Restrictions.eq("this.processType", filterModel.getProcessType()));
        }

        crit.addOrder(Order.desc("this.date"));
        crit.addOrder(Order.desc("this.id"));
        //crit.addOrder(Order.desc("serial"));
        //crit.addOrder(Order.asc("contact.name"));

        log.debug("Sonuç : #0", crit);

        return crit;
    }

    @SuppressWarnings("unchecked")
	public void pdf() {
        Map params = new HashMap();
		params.put("pBDate", ReportParameters.getDefaultBeginDate());
		params.put("pEDate", ReportParameters.getDefaultEndDate());

        if (filterModel.getSerial() != null && filterModel.getSerial().length() > 0) {
            params.put("pSeri", filterModel.getSerial());
        }

        if (filterModel.getContactName() != null && filterModel.getContactName().length() > 0) {
        	params.put("pName", filterModel.getContactName());
        }

        if (filterModel.getContactCode() != null && filterModel.getContactCode().length() > 0) {
        	params.put("pContactCode", filterModel.getContactCode());
        }
        
        if (filterModel.getReference() != null && filterModel.getReference().length() > 0) {
            params.put("pRef", filterModel.getReference());
        }

        if (filterModel.getCode() != null && filterModel.getCode().length() > 0) {
            params.put("pCode", filterModel.getCode());
        }

        if(filterModel.getCompanyType() != null && !filterModel.getCompanyType().equals("All")){
        	if(filterModel.getCompanyType().equals("Person")){
        		params.put("pComType", Boolean.TRUE);
        	}
        	else{
        		params.put("pComType", Boolean.FALSE);
        	}
        }
        
        if (filterModel.getType() != null && filterModel.getType() != ContactType.All) {
        	params.put("pType",filterModel.getType().toString().toLowerCase() + "Type"); 
        }
        
        if (filterModel.getCategory() != null) {
            params.put("pCate", filterModel.getCategory().getId());
        }
        
        if (filterModel.getExCode1() != null && filterModel.getExCode1().length() > 0) {
            params.put("pEx1", filterModel.getExCode1());
        }
        
        if (filterModel.getExCode2() != null && filterModel.getExCode2().length() > 0) {
            params.put("pEx2", filterModel.getExCode2());
        }
        
        if(filterModel.getOrganization() != null){
        	params.put("pOrg", filterModel.getOrganization().getId());
        }

        if (filterModel.getDocumentType() != null) {
            params.put("pDocu", filterModel.getDocumentType().ordinal());
        }

        if (filterModel.getActive() != null) {
        	params.put("pActive", filterModel.getActive());
        }
        
        if(filterModel.getContact() != null){
        	params.put("pConId", filterModel.getContact().getId());
        }

        if (filterModel.getBeginDate() != null) {
            params.put("pBDate", filterModel.getBeginDate());
        }

        if (filterModel.getEndDate() != null) {
            params.put("pEDate", filterModel.getEndDate());
        }
        
        if (filterModel.getProcessType() != null) {
        	params.put("pProcessType", filterModel.getProcessType().ordinal());
        }

        if(filterModel.getWorkBunch() != null){
        	params.put("pWorkBunchId", filterModel.getWorkBunch().getId());
        }

        if (filterModel.getCurrency() != null) {
        	params.put("pCurrency", filterModel.getCurrency());
        }
        
        params.put("pProductLabel", SystemConfiguration.PRODUCTLABEL); 
        
        execPdf("financeTxnReport", "Cari_Hareket_Raporu", params);


    }
    
    @SuppressWarnings("unchecked")
	public void xls() {
        Map params = new HashMap();
		
        params.put("pBDate", ReportParameters.getDefaultBeginDate());
		params.put("pEDate", ReportParameters.getDefaultEndDate());

        if (filterModel.getSerial() != null && filterModel.getSerial().length() > 0) {
            params.put("pSeri", filterModel.getSerial());
        }

        if (filterModel.getContactName() != null && filterModel.getContactName().length() > 0) {
        	params.put("pName", filterModel.getContactName());
        }

        if (filterModel.getContactCode() != null && filterModel.getContactCode().length() > 0) {
        	params.put("pContactCode", filterModel.getContactCode());
        }
        
        if (filterModel.getReference() != null && filterModel.getReference().length() > 0) {
            params.put("pRef", filterModel.getReference());
        }

        if (filterModel.getCode() != null && filterModel.getCode().length() > 0) {
            params.put("pCode", filterModel.getCode());
        }

        if(filterModel.getCompanyType() != null && !filterModel.getCompanyType().equals("All")){
        	if(filterModel.getCompanyType().equals("Person")){
        		params.put("pComType", Boolean.TRUE);
        	}
        	else{
        		params.put("pComType", Boolean.FALSE);
        	}
        }
        
        if (filterModel.getType() != null && filterModel.getType() != ContactType.All) {
        	params.put("pType",filterModel.getType().toString().toLowerCase() + "Type"); 
        }
        
        if (filterModel.getCategory() != null) {
            params.put("pCate", filterModel.getCategory().getId());
        }
        
        if (filterModel.getExCode1() != null && filterModel.getExCode1().length() > 0) {
            params.put("pEx1", filterModel.getExCode1());
        }
        
        if (filterModel.getExCode2() != null && filterModel.getExCode2().length() > 0) {
            params.put("pEx2", filterModel.getExCode2());
        }
        
        if(filterModel.getOrganization() != null){
        	params.put("pOrg", filterModel.getOrganization().getId());
        }

        if (filterModel.getDocumentType() != null) {
            params.put("pDocu", filterModel.getDocumentType().ordinal());
        }

        if (filterModel.getActive() != null) {
        	params.put("pActive", filterModel.getActive());
        }
        
        if(filterModel.getContact() != null){
        	params.put("pConId", filterModel.getContact().getId());
        }

        if (filterModel.getBeginDate() != null) {
            params.put("pBDate", filterModel.getBeginDate());
        }

        if (filterModel.getEndDate() != null) {
            params.put("pEDate", filterModel.getEndDate());
        }
        
        if (filterModel.getProcessType() != null) {
        	params.put("pProcessType", filterModel.getProcessType().ordinal());
        }

        if(filterModel.getWorkBunch() != null){
        	params.put("pWorkBunchId", filterModel.getWorkBunch().getId());
        }

        if (filterModel.getCurrency() != null) {
        	params.put("pCurrency", filterModel.getCurrency());
        }

        params.put("pProductLabel", SystemConfiguration.PRODUCTLABEL); 
        
        execXls("financeTxnReport_xls", "Cari_Hareket_Raporu", params);

    }
    
}
