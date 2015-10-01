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

package com.ut.tekir.invoice.yeni;

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

import com.ut.tekir.entities.DocumentType;
import com.ut.tekir.entities.TradeAction;
import com.ut.tekir.entities.inv.TekirInvoice;
import com.ut.tekir.framework.BrowserBase;
import com.ut.tekir.framework.CalendarManager;
import com.ut.tekir.framework.InvoiceFilterModel;

//FIXME: projectionları düzenlemek lazım.

/**
 *
 * @author sinan.yumak
 */
@Stateful
@Name("saleInvoiceBrowse")
@Scope(ScopeType.SESSION)
public class SaleInvoiceBrowseBean extends BrowserBase<TekirInvoice, InvoiceFilterModel> implements SaleInvoiceBrowse<TekirInvoice, InvoiceFilterModel> {

	public Integer exid;
	
	@In
    CalendarManager calendarManager;

	private boolean onlyReturned = false;

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
        
        crit.setProjection(Projections.projectionList()
                .add(Projections.property("id"), "id")
                .add(Projections.property("serial"), "serial")
                .add(Projections.property("reference"), "reference")
                .add(Projections.property("code"), "code")
                .add(Projections.property("date"), "date")
                .add(Projections.property("info"), "info")
                .add(Projections.property("contact.code"), "contactCode")
                .add(Projections.property("contact.fullname"), "contactName")
                .add(Projections.property("contact.company"), "contactCompany")
                .add(Projections.property("contact.person"), "contactPerson")
                .add(Projections.property("documentType"), "documentType")
                .add(Projections.property("tradeAction"), "tradeAction")
                .add(Projections.property("totalBeforeTax.value"), "totalBeforeTaxValue")
                .add(Projections.property("totalBeforeTax.currency"), "totalBeforeTaxCurrency")
                .add(Projections.property("totalTax.value"), "totalTaxValue")
                .add(Projections.property("totalTax.currency"), "totalTaxCurrency")
                .add(Projections.property("grandTotal.value"), "grandTotalValue")
                .add(Projections.property("grandTotal.currency"), "grandTotalCurrency")
                .add(Projections.property("workBunch"), "workBunch")
                );

        crit.setResultTransformer(Transformers.aliasToBean(InvoiceFilterModel.class));
        
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
        
        if (isNotEmpty(filterModel.getInfo())) {
            crit.add(Restrictions.ilike("this.info", filterModel.getInfo(),MatchMode.ANYWHERE));
        }

        if( filterModel.getActive() != null){
        	crit.add( Restrictions.eq("this.active", filterModel.getActive()));
        }
        
        if (filterModel.getWorkBunch() != null){
        	crit.add(Restrictions.eq("this.workBunch", filterModel.getWorkBunch()));
        }

        if(  filterModel.getReturned() != null ){
        	if (filterModel.getReturned()) {
        		crit.add( Restrictions.eq("this.tradeAction", TradeAction.PurchseReturn) );
        	} else {
        		crit.add( Restrictions.eq("this.tradeAction", TradeAction.Sale));
        	}
        } else {
        	crit.add( Restrictions.or(Restrictions.eq("this.tradeAction", TradeAction.PurchseReturn), 
        							  Restrictions.eq("this.tradeAction", TradeAction.Sale)) );
        }

        if (filterModel.getProformaInvoice() != null) {
        	if (filterModel.getProformaInvoice()) {
        		crit.add(Restrictions.eq("this.documentType", DocumentType.SaleProformaInvoice));
        	} else {
            	crit.add(Restrictions.in("this.documentType",new DocumentType[]{DocumentType.SaleInvoice, 
						DocumentType.SaleShipmentInvoice,
						DocumentType.RetailSaleShipmentInvoice}));
        	}
        } else {
        	crit.add(Restrictions.in("this.documentType",new DocumentType[]{DocumentType.SaleInvoice, 
        																	DocumentType.SaleShipmentInvoice,
        																	DocumentType.RetailSaleShipmentInvoice,
        																	DocumentType.SaleProformaInvoice}));
        }
        

        crit.addOrder(Order.desc("this.date"));
        crit.addOrder(Order.desc("this.serial"));

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
        
        if (isNotEmpty(filterModel.getInfo())) {
            params.put("pInfo", filterModel.getInfo());
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

        if(filterModel.getWorkBunch()!=null){
        	params.put("pWorkBunchId", filterModel.getWorkBunch().getId());
        }

        if(filterModel.getProformaInvoice()!=null){
        		params.put("pProporma", filterModel.getProformaInvoice());
        }
        
        if(  filterModel.getReturned() != null){
        	if(filterModel.getReturned()){
        		params.put("pReturned", true);
        	}else{
        		params.put("pReturned", false);
        	}
        }
        
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
        
        if(filterModel.getWorkBunch()!=null){
        	params.put("pWorkBunchId", filterModel.getWorkBunch().getId());
        }


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
        
        if(isNotEmpty(filterModel.getInfo())){
        	params.put("pInfo", filterModel.getInfo());
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

        if(filterModel.getWorkBunch()!=null){
        	params.put("pWorkBunchId", filterModel.getWorkBunch().getId());
        }
        
        if(filterModel.getProformaInvoice()!=null){
    		params.put("pProporma", filterModel.getProformaInvoice());
	    }
	    
	    if(  filterModel.getReturned() != null){
	    	if(filterModel.getReturned()){
	    		params.put("pReturned", true);
	    	}else{
	    		params.put("pReturned", false);
	    	}
	    }
        

        execXls("satis_fatura_listesi_excel", "Satis_Fatura_Listesi", params);

    }

	public Integer getExid() {
		return exid;
	}

	public void setExid(Integer exid) {
		this.exid = exid;
	}

	public boolean isOnlyReturned() {
		return onlyReturned;
	}

	public void setOnlyReturned(boolean onlyReturned) {
		if (onlyReturned) getFilterModel().setReturned(true);
		this.onlyReturned = onlyReturned;
	}
    
}
