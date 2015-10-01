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
import java.util.Date;
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
import com.ut.tekir.entities.Account;
import com.ut.tekir.entities.AccountTxn;
import com.ut.tekir.entities.AdvanceProcessType;
import com.ut.tekir.entities.DocumentType;
import com.ut.tekir.entities.WorkBunch;
import com.ut.tekir.framework.BaseConsts;
import com.ut.tekir.framework.CalendarManager;
import com.ut.tekir.framework.CurrencyManager;
import com.ut.tekir.framework.JasperHandlerBean;

/**
 *
 * @author haky
 */
@Name("accountStatusReport")
@Scope(ScopeType.SESSION)
public class AccountStatusReportBean {

    @Logger
    protected Log log;
    @In
    private EntityManager entityManager;
    @In
    protected FacesMessages facesMessages;
    @In CalendarManager calendarManager;
    @In CurrencyManager currencyManager;
    
    @In( create=true )
    JasperHandlerBean jasperReport;
    
    private Account account;
    private String code;
    private Date beginDate;
    private Date endDate;
	private DocumentType documentType;
	private AdvanceProcessType processType;
	private WorkBunch workBunch;
    
    private List<Map<String, Object>> resultList;
    private List<Map<String, Object>> summaryList;
    private BigDecimal grandTotal = BigDecimal.ZERO;
            
	@Create
	public void initReport() {
		beginDate = calendarManager.getFirstDayOfMonth();
		endDate = calendarManager.getCurrentDate();
	}
    
	@SuppressWarnings("unchecked")
	public void pdf() {
		Map params = new HashMap();
		params.put("pBDate", ReportParameters.getDefaultBeginDate());
		params.put("pEDate", ReportParameters.getDefaultEndDate());

		if (account != null) {
			params.put("pAccount", account.getId());
		}
		if (code != null && code.length() > 0) {
			params.put("pCode", code);
		}
		if (beginDate != null) {
			params.put("pBDate", beginDate);
		}
		if (endDate != null) {
			params.put("pEDate", endDate);
		}
		if (documentType != null) {
			params.put("pDocu", documentType.ordinal());
		}
		if (getWorkBunch() != null) {
			params.put("pWorkBunchId", getWorkBunch().getId());
		}
		if (getProcessType() != null) {
			params.put("pProcessType", getProcessType().ordinal());
		}

		params.put("pProductLabel", SystemConfiguration.PRODUCTLABEL);
		
		try {
			jasperReport.reportToPDF("kasa_durum", "Kasa_Durum_Raporu", params);
		} catch (JRException ex) {
			log.error("Rapor Hatası", ex);
			facesMessages.add("Rapor Çalıştırılamadı");
		}
        
    }
	
	@SuppressWarnings("unchecked")
	public void xls() {
		Map params = new HashMap();
		params.put("pBDate", ReportParameters.getDefaultBeginDate());
		params.put("pEDate", ReportParameters.getDefaultEndDate());

		if (account != null) {
			params.put("pAccount", account.getId());
		}
		if (code != null && code.length() > 0) {
			params.put("pCode", code);
		}
		if (beginDate != null) {
			params.put("pBDate", beginDate);
		}
		if (endDate != null) {
			params.put("pEDate", endDate);
		}
		if (documentType != null) {
			params.put("pDocu", documentType.ordinal());
		}
		if (getWorkBunch() != null) {
			params.put("pWorkBunchId", getWorkBunch().getId());
		}
		if (getProcessType() != null) {
			params.put("pProcessType", getProcessType().ordinal());
		}

		params.put("pProductLabel", SystemConfiguration.PRODUCTLABEL);
		
		try {
			jasperReport.reportToXls("kasa_durum_xls", "Kasa_Durum_Raporu", params);
		} catch (JRException ex) {
			log.error("Rapor Hatası", ex);
			facesMessages.add("Rapor Çalıştırılamadı");
		}
        
    }
    
    @SuppressWarnings("unchecked")
	public void executeReport() {

    	HibernateSessionProxy session = (HibernateSessionProxy) getEntityManager().getDelegate();

        Criteria ecrit = buildCriteria().getExecutableCriteria(session);
        //ecrit.setCacheable(true);
        //ecrit.setResultTransformer(new AliasToBeanResultTransformer(AccountStatusModel.class));
        ecrit.setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP);
        setResultList(ecrit.list());


        log.debug("Result : #0", getResultList());
        
        calculateSummary();
    }

    @SuppressWarnings("unchecked")
	public void calculateSummary() {

        summaryList = new ArrayList<Map<String, Object>>();
        grandTotal = BigDecimal.ZERO;

        Map<String, BigDecimal> ccyMap = new HashMap<String, BigDecimal>();
        
        for (Iterator it = getResultList().iterator(); it.hasNext();) {
            Map map = (Map) it.next();

            
            String ccy = (String) map.get("currency");
            BigDecimal amt = (BigDecimal) map.get("amount");
            
            if( ccyMap.containsKey(ccy) ){
                ccyMap.put( ccy, amt.add(ccyMap.get(ccy)) );
            } else {
                ccyMap.put( ccy, amt );
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
                m.put( "rate", BigDecimal.ZERO );
                m.put( "localAmount", BigDecimal.ZERO );
            }
            
            summaryList.add(m);
        }

    }

    public DetachedCriteria buildCriteria() {

        DetachedCriteria crit = DetachedCriteria.forClass(AccountTxn.class);

        crit.createAlias("account", "account");

        crit.setProjection(Projections.projectionList()
                .add(Projections.groupProperty("account.code"), "accountCode")
                .add(Projections.groupProperty("account.name"), "accountName")
                .add(Projections.groupProperty("amount.currency"), "currency")
                .add(Projections.sum("amount.value"), "amount")
                .add(Projections.sum("amount.localAmount"), "localAmount")
                .add(Projections.property("this.processType"), "processType")
                .add( Projections.sqlGroupProjection( "{alias}.CCY as currency, sum( ( case {alias}.finance_action when 0 then 1 else -1 end ) * {alias}.CCYVAL ) as AMOUNT, sum( case {alias}.finance_action when 0 then {alias}.CCYVAL else 0 end ) as DEBIT, sum( case {alias}.finance_action when 0 then 0 else {alias}.CCYVAL end ) as CREDIT",  "CCY", new String[]{"currency","amount","debit","credit"}, new Type[] {Hibernate.STRING, Hibernate.BIG_DECIMAL,Hibernate.BIG_DECIMAL,Hibernate.BIG_DECIMAL})));
        
        crit.add(Restrictions.eq("active", true));

        if (code != null && code.length() > 0) {
            crit.add(Restrictions.ilike("code", code,MatchMode.START));
            
        }

        if (account != null) {
            crit.add(Restrictions.eq("account", account));
        }

        if (beginDate != null) {
            crit.add(Restrictions.ge("date", beginDate));
        }

        if (endDate != null) {
            crit.add(Restrictions.le("date", endDate));
        }

		if (getDocumentType() != null
				&& getDocumentType() != DocumentType.Unknown) {
			crit.add(Restrictions.eq("documentType", getDocumentType()));
		}

		if (getProcessType() != null) {
			crit.add(Restrictions.eq("this.processType", getProcessType()));
		}

		if(getWorkBunch() != null){
			crit.add(Restrictions.eq("workBunch", getWorkBunch()));
		}

        crit.addOrder( Order.asc("account.code"));
        
        return crit;
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public List<Map<String, Object>> getResultList() {
        return resultList;
    }

    public void setResultList(List<Map<String, Object>> resultList) {
        this.resultList = resultList;
    }

    public BigDecimal getSum(String prop) {
        BigDecimal d = BigDecimal.ZERO;

        if (resultList == null) {
            return BigDecimal.ZERO;
        }

        for (Map<String, Object> m : resultList) {
            d = d.add((BigDecimal) m.get(prop));
        }

        return d;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Date getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(Date beginDate) {
        this.beginDate = beginDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
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

	public DocumentType getDocumentType() {
		return documentType;
	}

	public void setDocumentType(DocumentType documentType) {
		this.documentType = documentType;
	}

	public WorkBunch getWorkBunch() {
		return workBunch;
	}

	public void setWorkBunch(WorkBunch workBunch) {
		this.workBunch = workBunch;
	}

	public AdvanceProcessType getProcessType() {
		return processType;
	}

	public void setProcessType(AdvanceProcessType processType) {
		this.processType = processType;
	}
	
}
