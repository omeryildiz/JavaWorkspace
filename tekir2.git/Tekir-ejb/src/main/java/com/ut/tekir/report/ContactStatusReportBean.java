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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import net.sf.jasperreports.engine.JRException;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.Type;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.log.Log;
import org.jboss.seam.persistence.HibernateSessionProxy;

import com.ut.tekir.configuration.SystemConfiguration;
import com.ut.tekir.entities.ContactType;
import com.ut.tekir.entities.DocumentType;
import com.ut.tekir.entities.FinanceTxn;
import com.ut.tekir.framework.BaseConsts;
import com.ut.tekir.framework.CalendarManager;
import com.ut.tekir.framework.CurrencyManager;
import com.ut.tekir.framework.JasperHandlerBean;
import com.ut.tekir.util.StringUtils;

/**
 *
 * @author haky
 */
@Name( "contactStatusReport" )
@Scope(ScopeType.SESSION)
public class ContactStatusReportBean {

    @Logger
    protected Log log;
    
    @In private EntityManager entityManager;
    
    @In
    protected FacesMessages facesMessages;

    @In CalendarManager calendarManager;
    @In CurrencyManager currencyManager;
    
    @In( create=true )
    JasperHandlerBean jasperReport;
    
    
    
    
    @SuppressWarnings("unchecked")
	private List<Map> reportResult;
    private List<Map<String, Object>> summaryList;
    private BigDecimal grandTotal = BigDecimal.ZERO;
    
    private ContactStatusFilterModel fm ;
    
	@Create
	public void initReport() {
		fm = new ContactStatusFilterModel();
		fm.setBeginDate(calendarManager.getFirstDayOfMonth());
		fm.setEndDate(calendarManager.getCurrentDate());
	}
    
    @SuppressWarnings("unchecked")
	public void pdf(){
		Map params = new HashMap();
		params.put("pBDate", ReportParameters.getDefaultBeginDate());
		params.put("pEDate", ReportParameters.getDefaultEndDate());

		if (StringUtils.isNotEmpty(fm.getCode())) {
			params.put("pCode", fm.getCode());
		}
		if (StringUtils.isNotEmpty(fm.getName())) {
			params.put("pName", fm.getName());
		}
		if (fm.getCategory() != null) {
			params.put("pCategory", fm.getCategory().getId());
		}
        if (fm.getBeginDate() != null) {
            params.put("pBDate", fm.getBeginDate());
        }
        if (fm.getEndDate() != null) {
            params.put("pEDate", fm.getEndDate());
        }
        if (fm.getActive() != null) {
        	params.put("pActive", fm.getActive());
        }
        if(fm.getCompanyType() != null && !fm.getCompanyType().equals("All")){
        	if(fm.getCompanyType().equals("Person")){
        		params.put("pComType", Boolean.TRUE);
        	}
        	else{
        		params.put("pComType", Boolean.FALSE);
        	}
        }
        if (fm.getType() != null && fm.getType() != ContactType.All) {
        	params.put("pType",fm.getType().toString().toLowerCase() + "Type"); 
        }
        if (fm.getExCode1() != null && fm.getExCode1().length() > 0) {
            params.put("pEx1", fm.getExCode1());
        }
        if (fm.getExCode2() != null && fm.getExCode2().length() > 0) {
            params.put("pEx2", fm.getExCode2());
        }
        if(fm.getOrganization() != null){
        	params.put("pOrg", fm.getOrganization().getId());
        }
		if (fm.getDocCode() != null && fm.getDocCode().length() > 0) {
			params.put("pDocCode", fm.getDocCode());
		}
        if (fm.getDocumentType() != null) {
            params.put("pDocu", fm.getDocumentType().ordinal());
        }
        if (fm.getProcessType() != null) {
        	params.put("pProcessType", fm.getProcessType().ordinal());
        }
        if(fm.getWorkBunch() != null){
        	params.put("pWorkBunchId", fm.getWorkBunch().getId());
        }
        if(fm.getContact() != null){
        	params.put("pConId", fm.getContact().getId());
        }
        
        params.put("pProductLabel", SystemConfiguration.PRODUCTLABEL); 
        
        log.debug("Parametreler : #0", params );
        
        try {
            jasperReport.reportToPDF( "cari_durum", "Cari_Durum_Raporu", params);
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

		if (StringUtils.isNotEmpty(fm.getCode())) {
			params.put("pCode", fm.getCode());
		}
		if (StringUtils.isNotEmpty(fm.getName())) {
			params.put("pName", fm.getName());
		}
		if (fm.getCategory() != null) {
			params.put("pCategory", fm.getCategory().getId());
		}
        if (fm.getBeginDate() != null) {
            params.put("pBDate", fm.getBeginDate());
        }
        if (fm.getEndDate() != null) {
            params.put("pEDate", fm.getEndDate());
        }
        if (fm.getActive() != null) {
        	params.put("pActive", fm.getActive());
        }
        if(fm.getCompanyType() != null && !fm.getCompanyType().equals("All")){
        	if(fm.getCompanyType().equals("Person")){
        		params.put("pComType", Boolean.TRUE);
        	}
        	else{
        		params.put("pComType", Boolean.FALSE);
        	}
        }
        if (fm.getType() != null && fm.getType() != ContactType.All) {
        	params.put("pType",fm.getType().toString().toLowerCase() + "Type"); 
        }
        if (fm.getExCode1() != null && fm.getExCode1().length() > 0) {
            params.put("pEx1", fm.getExCode1());
        }
        if (fm.getExCode2() != null && fm.getExCode2().length() > 0) {
            params.put("pEx2", fm.getExCode2());
        }
        if(fm.getOrganization() != null){
        	params.put("pOrg", fm.getOrganization().getId());
        }
		if (fm.getDocCode() != null && fm.getDocCode().length() > 0) {
			params.put("pDocCode", fm.getDocCode());
		}
        if (fm.getDocumentType() != null) {
            params.put("pDocu", fm.getDocumentType().ordinal());
        }
        if (fm.getProcessType() != null) {
        	params.put("pProcessType", fm.getProcessType().ordinal());
        }
        if(fm.getWorkBunch() != null){
        	params.put("pWorkBunchId", fm.getWorkBunch().getId());
        }
        if(fm.getContact() != null){
        	params.put("pConId", fm.getContact().getId());
        }
        
        params.put("pProductLabel", SystemConfiguration.PRODUCTLABEL); 
        
        log.debug("Parametreler : #0", params );
        
        try {
            jasperReport.reportToXls( "cari_durum_xls", "Cari_Durum_Raporu", params);
        } catch (JRException ex) {
            log.error("Rapor Hatası", ex);
            facesMessages.add("Rapor Çalıştırılamadı");
        }
        
    }    
    
    @SuppressWarnings("unchecked")
	public void executeReport(){
    	HibernateSessionProxy session = (HibernateSessionProxy) getEntityManager().getDelegate();
        
        Criteria ecrit =  buildCriteria().getExecutableCriteria( session );
        //ecrit.setCacheable(true);
        //ecrit.setMaxResults( 100 );
        ecrit.setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP);
        reportResult = ecrit.list();
        calculateSummary();
    }
    
    @SuppressWarnings("unchecked")
	public void calculateSummary() {

        summaryList = new ArrayList<Map<String, Object>>();
        grandTotal = BigDecimal.ZERO;

        Map<String, BigDecimal> ccyMap = new HashMap<String, BigDecimal>();
        
        for (Iterator it = reportResult.iterator(); it.hasNext();) {
            Map map = (Map) it.next();

            String ccy = (String) map.get("currency");
            BigDecimal amt = (BigDecimal) map.get("amount");
            
            BigDecimal debit = (BigDecimal) map.get("debit");
            BigDecimal credit = (BigDecimal) map.get("credit");
            
            BigDecimal balance = debit.subtract(credit);
            map.put("balance", balance);
     
            
            //FIXME: MoneySet Karışıklığı... düzeltile
            
            BigDecimal rate = currencyManager.getLocalAskRate(ccy);
            map.put( "rate", rate );
            
            BigDecimal ldebit  = currencyManager.convertToLocal( debit, ccy );
            BigDecimal lcredit  = currencyManager.convertToLocal( credit, ccy );
            BigDecimal lbalance = ldebit.subtract(lcredit);
            
            map.put( "localDebit", ldebit );
            map.put( "localCredit", lcredit );
            map.put( "localBalance", lbalance );
            
            
            if( ccyMap.containsKey(ccy) ){
                ccyMap.put( ccy, balance.add(ccyMap.get(ccy)) );
            } else {
                ccyMap.put( ccy, balance );
            }
            
            log.debug("Row : #0", map);
        }
        
        for( Iterator<String> it = ccyMap.keySet().iterator(); it.hasNext(); ){
            String ccy = it.next();
            
            Map<String, Object> m = new HashMap<String, Object>();
            m.put( "currency", ccy);
            m.put( "amount", ccyMap.get(ccy));
            //BurayaKur hesaplar gelecek...
             
            try{
                //Money lamt = currencyManager.convertLocale(new Money(ccyMap.get(ccy), ccy ));
                
                BigDecimal r;
                
                if( !BaseConsts.SYSTEM_CURRENCY_CODE.equals(ccy)){
                    r = currencyManager.getLocalCurrencyTodayRate(ccy).getAsk();
                } else {
                    r = BigDecimal.ONE;
                }
                
                //FIXME: MoneySet karışıklığı
                BigDecimal la = r.multiply(ccyMap.get(ccy));
                m.put( "rate", r );
                m.put( "localAmount", la );
                
                grandTotal = grandTotal.add(la);
                
            } catch( Exception e ){
                //TODO: Hata mesajı dil dosyasına taşınacak.
                facesMessages.add("#0 için günlük kur bulunamadı.", ccy);
                m.put( "rate", BigDecimal.ZERO  );
                m.put( "localAmount", BigDecimal.ZERO  );
            }
            
            getSummaryList().add(m);
        }
        
    }
    
   
    
    /*
    public void executeReport(){
        FullTextSession session = (FullTextSession) getEntityManager().getDelegate();
        
        Criteria ecrit =  buildCriteria().getExecutableCriteria( session );
        //ecrit.setCacheable(true);
        //ecrit.setMaxResults( 100 );
        ecrit.setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP);
        
        List<Map<String, Object>> res = (List<Map<String, Object>>) ecrit.list();
        //Geriye dönen result üzerinde dönüp istenilen kırılımda sonuç hazırlanacak.
        
        Map< String, Map<String, Map<String, Object>>> contactMap = new HashMap();
        
        for( Map m : res ){
            String contact = "[" + (String) m.get("concode") + "] " + (String) m.get("conname");
            
            Map<String, Map<String, Object>> ccyMap;
            
            if ( contactMap.containsKey(contact)){
                ccyMap = contactMap.get(contact);
            } else {
                ccyMap = new HashMap();
                contactMap.put(contact, ccyMap);
            }
            
            String ccy = (String) m.get("currency");
            
            Map<String, Object> dataMap;
            
            if ( ccyMap.containsKey(ccy)){
                dataMap = ccyMap.get(ccy);
            } else {
                dataMap = new HashMap();
                dataMap.put("currency", ccy );
                ccyMap.put(ccy, dataMap);
            }
            
            if( m.get("amount") == FinanceAction.Credit ){
                Double cr = dataMap.containsKey("credit")? (Double) dataMap.get("credit") : 0d;
                dataMap.put("credit", cr + (Double)m.get("amount"));
            } else {
                Double cr = dataMap.containsKey("debit")? (Double) dataMap.get("debit") : 0d;
                dataMap.put("debit", cr + (Double)m.get("amount"));
            }
            
            log.debug( "Row : #0", ccyMap );
            //TODO : Kurları nasıl yapmalı?
        }
        
        
        reportResult = new ArrayList();
        
        for( Entry< String, Map<String, Map<String, Object>>> ent : contactMap.entrySet() ){
            
            Map<String, Object> rowMap = new HashMap();
            List<Map> dataList = new ArrayList();
            
            rowMap.put( "contact", ent.getKey() );
            rowMap.put( "data", dataList );
            
            dataList.addAll(ent.getValue().values());
            
            reportResult.add(rowMap);
        }
        
        log.debug( "Result : #0", contactMap );
        log.debug( "Result : #0", reportResult );
        
        
    }
    */
    
    public DetachedCriteria buildCriteria() {
        
        DetachedCriteria crit = DetachedCriteria.forClass( FinanceTxn.class );
        
        crit.createAlias("contact", "contact");
        
        crit.setProjection( Projections.projectionList()
                .add( Projections.groupProperty("contact.code"), "concode" )
                .add( Projections.groupProperty("contact.fullname"), "conname" )
                .add( Projections.groupProperty("contact.company"), "company" )
                .add( Projections.groupProperty("contact.person"), "person" )
                //.add( Projections.groupProperty("action"), "action" )
                //.add( Projections.groupProperty("amount.currency"), "currency" )
                .add( Projections.sum("amount.value"), "amount" )
                .add( Projections.sum("amount.localAmount"), "localAmount" )
                .add( Projections.sqlGroupProjection( "{alias}.CCY as currency, sum( case {alias}.finance_action when 0 then {alias}.CCYVAL else 0 end ) as DEBIT, sum( case {alias}.finance_action when 0 then 0 else {alias}.CCYVAL end ) as CREDIT",  "{alias}.CCY", new String[]{"currency","debit","credit"}, new Type[] {Hibernate.STRING, Hibernate.BIG_DECIMAL,Hibernate.BIG_DECIMAL}))
            );

        if (fm.getActive() != null) {
        	crit.add( Restrictions.eq("active", fm.getActive()) );
        }
        
        if (StringUtils.isNotEmpty(fm.getCode())) {
            crit.add(Restrictions.ilike("contact.code", fm.getCode(),MatchMode.START ));
        }
        
        if (StringUtils.isNotEmpty(fm.getName())) {
            crit.add(Restrictions.ilike("contact.name", fm.getName(),MatchMode.START ));
        }

        if (fm.getCategory() != null) {
            crit.add(Restrictions.eq("contact.category", fm.getCategory()));
        }

        if (fm.getBeginDate() != null) {
            crit.add(Restrictions.ge("date", fm.getBeginDate()));
        }

        if (fm.getEndDate() != null) {
            crit.add(Restrictions.le("date", fm.getEndDate()));
        }
        
        
        if (fm.getCompanyType() !=null && !fm.getCompanyType().equals("All")){
            if(fm.getCompanyType().equals("Person")){
                crit.add(Restrictions.eq("contact.person", Boolean.TRUE));
            }else
                crit.add(Restrictions.eq("contact.person", Boolean.FALSE));
        }
        
        if (fm.getType() != null && fm.getType() != ContactType.All) {
    		crit.add(Restrictions.eq("contact." + fm.getType().toString().toLowerCase() + "Type", Boolean.TRUE));
        }
        
        if (StringUtils.isNotEmpty(fm.getExCode1())) {
            crit.add(Restrictions.ilike("contact.exCode1", fm.getExCode1()  ,MatchMode.START));
        }

        if (StringUtils.isNotEmpty(fm.getExCode2())) {
            crit.add(Restrictions.ilike("contact.exCode2", fm.getExCode2()  ,MatchMode.START));
        }
        
        if (fm.getOrganization() != null) {
        	crit.add(Restrictions.eq("contact.organization", fm.getOrganization()));
        }
        
        if (StringUtils.isNotEmpty(fm.getDocCode())) {
            crit.add(Restrictions.ilike("code", fm.getDocCode(),MatchMode.START ));
        }
        
        if (fm.getDocumentType() != null && fm.getDocumentType() != DocumentType.Unknown) {
            crit.add(Restrictions.eq("this.documentType", fm.getDocumentType()));
        }
        
        if (fm.getProcessType() != null) {
        	crit.add(Restrictions.eq("this.processType", fm.getProcessType()));
        }
        
        if(fm.getWorkBunch() != null){
        	crit.add(Restrictions.eq("this.workBunch", fm.getWorkBunch()));
        }
        
        if(fm.getContact() != null){
        	crit.add(Restrictions.eq("contact.id", fm.getContact().getId()));
        }
        
        /*
        crit.addOrder( Order.asc("date"));
        crit.addOrder( Order.asc("serial"));
         * 
         */
        crit.addOrder( Order.asc("contact.code"));
        crit.addOrder( Order.asc("amount.currency"));
        
        
        log.debug("Sonuç : #0", crit);
        
        return crit;
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @SuppressWarnings("unchecked")
	public List<Map> getReportResult() {
        return reportResult;
    }

    public List<Map<String, Object>> getSummaryList() {
        return summaryList;
    }

    public void setSummaryList(List<Map<String, Object>> summaryList) {
        this.summaryList = summaryList;
    }

    public BigDecimal getGrandTotal() {
        return grandTotal;
    }

    public void setGrandTotal(BigDecimal grandTotal) {
        this.grandTotal = grandTotal;
    }

    public boolean renderContact( int ix ){
        if( reportResult == null ) return false;
        if( ix == 0 ) return true;
        String curval = (String) reportResult.get(ix).get("concode");
        String oldval = (String) reportResult.get(ix - 1 ).get("concode");
        return !curval.equals( oldval );
    }

	public ContactStatusFilterModel getFm() {
		return fm;
	}

	public void setFm(ContactStatusFilterModel fm) {
		this.fm = fm;
	}    
    
    
}
