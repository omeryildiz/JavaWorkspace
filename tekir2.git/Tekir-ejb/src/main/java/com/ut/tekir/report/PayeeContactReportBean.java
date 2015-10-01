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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import net.sf.jasperreports.engine.JRException;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.log.Log;

import com.ut.tekir.configuration.SystemConfiguration;
import com.ut.tekir.entities.ContactType;
import com.ut.tekir.entities.DocumentType;
import com.ut.tekir.framework.CalendarManager;
import com.ut.tekir.framework.ContactReportModel;
import com.ut.tekir.framework.ContactReportSummaryModel;
import com.ut.tekir.framework.CurrencyManager;
import com.ut.tekir.framework.JasperHandlerBean;
import com.ut.tekir.util.StringUtils;

/**
 *
 * @author bilga
 * 
 * borclu ile ayni hale getirildi. 
 */
@Name( "payeeContactReport" )
@Scope(ScopeType.SESSION)
public class PayeeContactReportBean {

    @Logger
    protected Log log;
    
    @In private EntityManager entityManager;
    
    @In
    protected FacesMessages facesMessages;

    @In CalendarManager calendarManager;
    @In CurrencyManager currencyManager;
    
    @In( create=true )
    JasperHandlerBean jasperReport;
    
    private List<ContactReportModel> payeeContacts;
    private List<ContactReportSummaryModel> payeeContactSummary;
    private ContactStatusFilterModel filterModel;
    
    @Create
    public void initFilters(){
    	filterModel = new ContactStatusFilterModel();
    	filterModel.setBeginDate(calendarManager.getLastMonthDate());
    	filterModel.setEndDate(calendarManager.getCurrentDate());
    }
    
    public void search(){
    	initPayeeContacts();
    	initPayeeContactSummary();
    }
 
    private void initPayeeContacts() {
    	
    	if(payeeContacts != null){
    		payeeContacts.clear();
		}
		else{
			payeeContacts = new ArrayList<ContactReportModel>();
		}
    	
    	List<Object []> payee = getPayeeContact();
    	
    	for (Object[] item : payee){
			ContactReportModel model = new ContactReportModel();
			
			model.setContactCode((String)item[0]);
			model.setContactName((String)item[1]);
			model.setCompany((String)item[2]);
			model.setPerson((Boolean)item[3]);
			model.setCcy((String)item[4]);
			model.setLcyval((BigDecimal)item[5]);
			model.setTotalval((BigDecimal)item[6]);
			model.setAvgrate((BigDecimal)item[7]);
			
			payeeContacts.add(model);
		}
//    	payeeContacts.addAll(getPayeeContact());
	}

    private void initPayeeContactSummary() {
    	
    	if(payeeContactSummary != null){
    		payeeContactSummary.clear();
		}
		else{
			payeeContactSummary = new ArrayList<ContactReportSummaryModel>();
		}
    	
    	List<Object []> payee = getPayeeContactsSummary();
    	
    	for (Object[] item : payee){
			ContactReportSummaryModel model = new ContactReportSummaryModel();
			
			model.setCcy((String)item[0]);
			model.setCcyval((BigDecimal)item[1]);
			model.setLcyval((BigDecimal)item[2]);
			
			payeeContactSummary.add(model);
		}
    	
    	
//    	payeeContactSummary.addAll(getPayeeContactsSummary());
    }

    public BigDecimal getGrandTotal() {
    	BigDecimal total = BigDecimal.ZERO;
    	for (ContactReportSummaryModel model : payeeContactSummary) {
    		total = total.add(model.getLcyval());
    	}
    	return total;
    }
    
    @SuppressWarnings("unchecked")
	public List<Object []> getPayeeContact(){
    	String query="select * from (select con.code as contactCode, "+
    				 " con.fullname as contactName, " +
    				 " con.company as company, " +
					 " con.person as person, " +
					 " ft.ccy, " +
					 " sum(ft.LCYVAL * case when ft.finance_action = 1 then -1 else 1 end ) as totallcyval, " +
					 " sum(ft.ccyval * case when ft.finance_action = 1 then -1 else 1 end ) as generalTotal, " +
					 " (ft.LCYVAL/ft.CCYVAL) as avgrate " +
					 " from FINANCE_TXN as ft, CONTACT as con " +
					 " where ft.contact_id=con.id ";
    	
    	//Dikkat : eklenecek her string sonunda bosluk birakin
    	if(StringUtils.isNotEmpty(filterModel.getCode())){
			query = query.concat("and con.code like :code " );
		}
    	
    	if(StringUtils.isNotEmpty(filterModel.getName())){
    		query = query.concat("and con.fullname like :name " );
    	}
    	
    	if(StringUtils.isNotEmpty(filterModel.getExCode1())){
			query = query.concat("and con.exCode1 like :exCode1 " );
		}
    	
    	if(StringUtils.isNotEmpty(filterModel.getExCode2())){
    		query = query.concat("and con.exCode2 like :exCode2 " );
    	}
    	
    	if(filterModel.getCategory() != null){
    		query = query.concat("and con.CONTACT_CATEGORY_ID = :category " );
    	}
    	
    	if(filterModel.getBeginDate() != null){
    		query = query.concat("and ft.txn_date >= :beginDate " );
    	}
    	
    	if(filterModel.getEndDate() != null){
    		query = query.concat("and ft.txn_date <= :endDate " );
    	}
    	
    	if(filterModel.getWorkBunch() != null){
    		query = query.concat(" and ft.WORK_BUNCH_ID =:workBunchId ");
    	}
    	//yeni
        if (filterModel.getCompanyType() !=null && !filterModel.getCompanyType().equals("All")){
            if(filterModel.getCompanyType().equals("Person")){
            	query = query.concat("and con.person = true " );
            }else
            	query = query.concat("and con.person = false " );
        }
        
        if (filterModel.getType() != null && filterModel.getType() != ContactType.All) {
        	query = query.concat("and con." + filterModel.getType().toString().toLowerCase() + "_type  = true " );
        }
    	
        if(filterModel.getContact() != null){
        	query = query.concat("and con.ID = :contactId " );
        }
        
        if (filterModel.getActive() != null) {
           		query = query.concat("and ft.isActive = :active " );
        }
    	
        if (filterModel.getOrganization() != null) {
        		query = query.concat("and con.ORGANIZATION_ID = :organization " );
        }
        
    	if(StringUtils.isNotEmpty(filterModel.getDocCode())){
			query = query.concat("and ft.Code like :docCode " );
		}
        
        if (filterModel.getDocumentType() != null && filterModel.getDocumentType() != DocumentType.Unknown) {
        	query = query.concat("and ft.DOCUMENT_TYPE = :documentType ");
        }
        
        if (filterModel.getProcessType() != null) {
        	query = query.concat("and ft.PROCESS_TYPE = :processType ");
        }
        
    	
    	query = query.concat(" group by con.code,con.fullname,ft.ccy ) as res "+
														" where  res.generalTotal < 0.00 "+
														" order by res.contactCode asc ");
    	//log.info("sorgu : #0 ", query);
    	
    	Query myQuery = entityManager.createNativeQuery(query);

    	if (StringUtils.isNotEmpty(filterModel.getCode())) {
    		myQuery.setParameter("code", filterModel.getCode() + "%");
    	}
    	
    	if (StringUtils.isNotEmpty(filterModel.getName())) {
    		myQuery.setParameter("name", filterModel.getName() + "%");
    	}
    	
    	if (StringUtils.isNotEmpty(filterModel.getExCode1())) {
    		myQuery.setParameter("exCode1", filterModel.getExCode1() + "%");
    	}
    	
    	if (StringUtils.isNotEmpty(filterModel.getExCode2())) {
    		myQuery.setParameter("exCode2", filterModel.getExCode2() + "%");
    	}
    	
    	if (filterModel.getCategory() != null) {
    		myQuery.setParameter("category", filterModel.getCategory());
    	}
    	    	
    	if (filterModel.getBeginDate() != null) {
    		myQuery.setParameter("beginDate", filterModel.getBeginDate());
    	}
    	    	
    	if (filterModel.getEndDate() != null) {
    		myQuery.setParameter("endDate", filterModel.getEndDate());
    	}
    	
    	if(filterModel.getWorkBunch() != null){
    		myQuery.setParameter("workBunchId", filterModel.getWorkBunch().getId());
    	}
    	
    	if(filterModel.getContact() != null){
    		myQuery.setParameter("contactId", filterModel.getContact().getId());
    	}
    	
        if (filterModel.getActive() != null) {
        	myQuery.setParameter("active", filterModel.getActive() );
        }
    	
        if (filterModel.getOrganization() != null) {
        	myQuery.setParameter("organization", filterModel.getOrganization() );
        	//log.info("org : #0 ", filterModel.getOrganization());
        }
        
    	if (StringUtils.isNotEmpty(filterModel.getDocCode())) {
    		myQuery.setParameter("docCode", filterModel.getDocCode() + "%");
    	}
    	
        if (filterModel.getDocumentType() != null && filterModel.getDocumentType() != DocumentType.Unknown) {
        	myQuery.setParameter("documentType", filterModel.getDocumentType().ordinal() );    
        	//log.info("doctype : #0 ", filterModel.getDocumentType().ordinal());
        }
        
        if (filterModel.getProcessType() != null) {
        	myQuery.setParameter("processType", filterModel.getProcessType() );  
        }
        
        //log.info("my sorgu : #0 ", myQuery);
    	return myQuery.getResultList();
    	
    	
	}
    
    @SuppressWarnings("unchecked")
    public List<Object []> getPayeeContactsSummary(){
    	String summaryQuery="select ccy, "+
					    	" sum(generalTotal) as ccyvalSummary, "+
					    	" sum(totallcyval) as lcyvalSummary "+
					    	" from (select con.code as contactCode, "+
							" con.fullname as contactName, " +
							" ft.ccy, " +
							" sum(ft.LCYVAL * case when ft.finance_action = 1 then -1 else 1 end ) as totallcyval, " +
							" sum(ft.ccyval * case when ft.finance_action = 1 then -1 else 1 end ) as generalTotal " +
							" from FINANCE_TXN as ft, CONTACT as con " +
							" where ft.contact_id=con.id ";

       	//Dikkat : eklenecek her string sonunda bosluk birakin
    	if(StringUtils.isNotEmpty(filterModel.getCode())){
    		summaryQuery = summaryQuery.concat("and con.code like :code " );
		}
    	
    	if(StringUtils.isNotEmpty(filterModel.getName())){
    		summaryQuery = summaryQuery.concat("and con.fullname like :name " );
    	}
    	
    	if(StringUtils.isNotEmpty(filterModel.getExCode1())){
    		summaryQuery = summaryQuery.concat("and con.exCode1 like :exCode1 " );
		}
    	
    	if(StringUtils.isNotEmpty(filterModel.getExCode2())){
    		summaryQuery = summaryQuery.concat("and con.exCode2 like :exCode2 " );
    	}
    	
    	if(filterModel.getCategory() != null){
    		summaryQuery = summaryQuery.concat("and con.CONTACT_CATEGORY_ID = :category " );
    	}
    	
    	if(filterModel.getBeginDate() != null){
    		summaryQuery = summaryQuery.concat("and ft.txn_date >= :beginDate " );
    	}
    	
    	if(filterModel.getEndDate() != null){
    		summaryQuery = summaryQuery.concat("and ft.txn_date <= :endDate " );
    	}
    	
    	if(filterModel.getWorkBunch() != null){
    		summaryQuery = summaryQuery.concat(" and ft.WORK_BUNCH_ID =:workBunchId ");
    	}
    	//yeni
        if (filterModel.getCompanyType() !=null && !filterModel.getCompanyType().equals("All")){
            if(filterModel.getCompanyType().equals("Person")){
            	summaryQuery = summaryQuery.concat("and con.person = true " );
            }else
            	summaryQuery = summaryQuery.concat("and con.person = false " );
        }
        
        if (filterModel.getType() != null && filterModel.getType() != ContactType.All) {
        	summaryQuery = summaryQuery.concat("and con." + filterModel.getType().toString().toLowerCase() + "_type  = true " );
        }
    	
        if(filterModel.getContact() != null){
        	summaryQuery = summaryQuery.concat("and con.ID = :contactId " );
        }
        
        if (filterModel.getActive() != null) {
        	summaryQuery = summaryQuery.concat("and ft.isActive = :active " );
        }
    	
        if (filterModel.getOrganization() != null) {
        	summaryQuery = summaryQuery.concat("and con.ORGANIZATION_ID = :organization " );
        }
        
    	if(StringUtils.isNotEmpty(filterModel.getDocCode())){
    		summaryQuery = summaryQuery.concat("and ft.Code like :docCode " );
		}
        
        if (filterModel.getDocumentType() != null && filterModel.getDocumentType() != DocumentType.Unknown) {
        	summaryQuery = summaryQuery.concat("and ft.DOCUMENT_TYPE = :documentType ");
        }
        
        if (filterModel.getProcessType() != null) {
        	summaryQuery = summaryQuery.concat("and ft.PROCESS_TYPE = :processType ");
        }
    	
    	
    	summaryQuery=summaryQuery.concat(" group by con.code,con.fullname,ft.ccy ) as res "+
    																	" where  res.generalTotal < 0.00 "+
    																	" group by ccy ");
    	
    	Query mySummaryQuery = entityManager.createNativeQuery(summaryQuery);
    	
    	if (StringUtils.isNotEmpty(filterModel.getCode())) {
    		mySummaryQuery.setParameter("code", filterModel.getCode() + "%");
    	}
    	
    	if (StringUtils.isNotEmpty(filterModel.getName())) {
    		mySummaryQuery.setParameter("name", filterModel.getName() + "%");
    	}
    	
    	if (StringUtils.isNotEmpty(filterModel.getExCode1())) {
    		mySummaryQuery.setParameter("exCode1", filterModel.getExCode1() + "%");
    	}
    	
    	if (StringUtils.isNotEmpty(filterModel.getExCode2())) {
    		mySummaryQuery.setParameter("exCode2", filterModel.getExCode2() + "%");
    	}
    	
    	if (filterModel.getCategory() != null) {
    		mySummaryQuery.setParameter("category", filterModel.getCategory());
    	}
    	    	
    	if (filterModel.getBeginDate() != null) {
    		mySummaryQuery.setParameter("beginDate", filterModel.getBeginDate());
    	}
    	    	
    	if (filterModel.getEndDate() != null) {
    		mySummaryQuery.setParameter("endDate", filterModel.getEndDate());
    	}
    	
    	if(filterModel.getWorkBunch() != null){
    		mySummaryQuery.setParameter("workBunchId", filterModel.getWorkBunch().getId());
    	}
    	
    	if(filterModel.getContact() != null){
    		mySummaryQuery.setParameter("contactId", filterModel.getContact().getId());
    	}
    	
        if (filterModel.getActive() != null) {
        	mySummaryQuery.setParameter("active", filterModel.getActive() );
        }
    	
        if (filterModel.getOrganization() != null) {
        	mySummaryQuery.setParameter("organization", filterModel.getOrganization() );
        	//log.info("org : #0 ", filterModel.getOrganization());
        }
        
    	if (StringUtils.isNotEmpty(filterModel.getDocCode())) {
    		mySummaryQuery.setParameter("docCode", filterModel.getDocCode() + "%");
    	}
    	
        if (filterModel.getDocumentType() != null && filterModel.getDocumentType() != DocumentType.Unknown) {
        	mySummaryQuery.setParameter("documentType", filterModel.getDocumentType().ordinal() );    
        	//log.info("doctype : #0 ", filterModel.getDocumentType().ordinal());
        }
        
        if (filterModel.getProcessType() != null) {
        	mySummaryQuery.setParameter("processType", filterModel.getProcessType() );  
        }

    	return mySummaryQuery.getResultList();
    }
    
    @SuppressWarnings("unchecked")
	public void pdf(){
        Map params = new HashMap();
 		params.put("pBDate", ReportParameters.getDefaultBeginDate());
		params.put("pEDate", ReportParameters.getDefaultEndDate());

		if (StringUtils.isNotEmpty(filterModel.getCode())) {
			params.put("pCode", filterModel.getCode());
		}
		if (StringUtils.isNotEmpty(filterModel.getName())) {
			params.put("pName", filterModel.getName());
		}
		if (filterModel.getCategory() != null) {
			params.put("pCategory", filterModel.getCategory().getId());
		}
        if (filterModel.getBeginDate() != null) {
            params.put("pBDate", filterModel.getBeginDate());
        }
        if (filterModel.getEndDate() != null) {
            params.put("pEDate", filterModel.getEndDate());
        }
        if (filterModel.getActive() != null) {
        	params.put("pActive", filterModel.getActive());
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
        if (filterModel.getExCode1() != null && filterModel.getExCode1().length() > 0) {
            params.put("pEx1", filterModel.getExCode1());
        }
        if (filterModel.getExCode2() != null && filterModel.getExCode2().length() > 0) {
            params.put("pEx2", filterModel.getExCode2());
        }
        if (filterModel.getOrganization() != null){
        	params.put("pOrg", filterModel.getOrganization().getId());
        }
		if (filterModel.getDocCode() != null && filterModel.getDocCode().length() > 0) {
			params.put("pDocCode", filterModel.getDocCode());
		}
        if (filterModel.getDocumentType() != null) {
            params.put("pDocu", filterModel.getDocumentType().ordinal());
        }
        if (filterModel.getProcessType() != null) {
        	params.put("pProcessType", filterModel.getProcessType().ordinal());
        }
        if (filterModel.getWorkBunch() != null){
        	params.put("pWorkBunchId", filterModel.getWorkBunch().getId());
        }
        if (filterModel.getContact() != null){
        	params.put("pConId", filterModel.getContact().getId());
        }
        
        params.put("pProductLabel", SystemConfiguration.PRODUCTLABEL); 

        log.debug("Parametreler : #0", params );
        
        try {
            jasperReport.reportToPDF( "payeeContactList", "Alacakli_Cari_Raporu", params);
        } catch (JRException ex) {
            log.error("Rapor Hatası", ex);
            facesMessages.add("Rapor Çalıştırılamadı");
        }
        
    }

    @SuppressWarnings("unchecked")
	public void xls(){
        Map params = new HashMap();
 		params.put("pBDate", ReportParameters.getDefaultBeginDate());
		params.put("pEDate", ReportParameters.getDefaultEndDate());

		if (StringUtils.isNotEmpty(filterModel.getCode())) {
			params.put("pCode", filterModel.getCode());
		}
		if (StringUtils.isNotEmpty(filterModel.getName())) {
			params.put("pName", filterModel.getName());
		}
		if (filterModel.getCategory() != null) {
			params.put("pCategory", filterModel.getCategory().getId());
		}
        if (filterModel.getBeginDate() != null) {
            params.put("pBDate", filterModel.getBeginDate());
        }
        if (filterModel.getEndDate() != null) {
            params.put("pEDate", filterModel.getEndDate());
        }
        if (filterModel.getActive() != null) {
        	params.put("pActive", filterModel.getActive());
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
        if (filterModel.getExCode1() != null && filterModel.getExCode1().length() > 0) {
            params.put("pEx1", filterModel.getExCode1());
        }
        if (filterModel.getExCode2() != null && filterModel.getExCode2().length() > 0) {
            params.put("pEx2", filterModel.getExCode2());
        }
        if (filterModel.getOrganization() != null){
        	params.put("pOrg", filterModel.getOrganization().getId());
        }
		if (filterModel.getDocCode() != null && filterModel.getDocCode().length() > 0) {
			params.put("pDocCode", filterModel.getDocCode());
		}
        if (filterModel.getDocumentType() != null) {
            params.put("pDocu", filterModel.getDocumentType().ordinal());
        }
        if (filterModel.getProcessType() != null) {
        	params.put("pProcessType", filterModel.getProcessType().ordinal());
        }
        if (filterModel.getWorkBunch() != null){
        	params.put("pWorkBunchId", filterModel.getWorkBunch().getId());
        }
        if (filterModel.getContact() != null){
        	params.put("pConId", filterModel.getContact().getId());
        }
        
        params.put("pProductLabel", SystemConfiguration.PRODUCTLABEL); 

        log.debug("Parametreler : #0", params );
        
        try {
        	 jasperReport.reportToXls( "payeeContactList_xls", "Alacakli_Cari_Raporu", params);
        } catch (JRException ex) {
            log.error("Rapor Hatası", ex);
            facesMessages.add("Rapor Çalıştırılamadı");
        }
        
    }
    
    public EntityManager getEntityManager() {
        return entityManager;
    }

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public boolean renderContact( int ix ){
        if( payeeContacts == null ) return false;
        if( ix == 0 ) return true;
        String curval = payeeContacts.get(ix).getContactCode();
        String oldval = payeeContacts.get(ix - 1 ).getContactName();
        return !curval.equals( oldval );
    }

	public void setPayeeContacts(List<ContactReportModel> payeeContacts) {
		this.payeeContacts = payeeContacts;
	}

	public List<ContactReportModel> getPayeeContacts() {
		if (payeeContacts == null) {
			payeeContacts = new ArrayList<ContactReportModel>();
		}
		return payeeContacts;
	}

	public void setPayeeContactSummary(List<ContactReportSummaryModel> payeeContactSummary) {
		this.payeeContactSummary = payeeContactSummary;
	}

	public List<ContactReportSummaryModel> getPayeeContactSummary() {
		if (payeeContactSummary == null) {
			payeeContactSummary = new ArrayList<ContactReportSummaryModel>();
		}
		return payeeContactSummary;
	}

	public ContactStatusFilterModel getFilterModel() {
		return filterModel;
	}

	public void setFilterModel(ContactStatusFilterModel filterModel) {
		this.filterModel = filterModel;
	}
    
}