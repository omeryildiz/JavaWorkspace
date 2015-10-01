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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import net.sf.jasperreports.engine.JRException;

import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
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
import com.ut.tekir.framework.CalendarManager;
import com.ut.tekir.framework.JasperHandlerBean;

/**
 * 
 * @author haky
 */
@Name("accountTxnReport")
@Scope(ScopeType.SESSION)
public class AccountTxnReportBean {

	@Logger
	protected Log log;
	@In
	private EntityManager entityManager;
	@In
	protected FacesMessages facesMessages;
	@In
	CalendarManager calendarManager;
	
	@In(create = true)
	JasperHandlerBean jasperReport;
	private List<AccountTxn> resultList;
	private Account account;
	private String serial;
    private String reference;
    private String code;
	private Date beginDate;
	private Date endDate;
	private DocumentType documentType;
	private AdvanceProcessType processType;
	private WorkBunch workBunch;
    private String adverseCode;
    private String adverseName;
    private String currency;

	@Create
	public void initReport() {
		beginDate = calendarManager.getLastTenDay();
		endDate = calendarManager.getCurrentDate();
	}

	@SuppressWarnings("unchecked")
	public void pdf() {
		Map params = new HashMap();
		params.put("pBDate", getDefaultBeginDate());
		params.put("pEDate", getDefaultEndDate());

		if (serial != null && serial.length() > 0) {
			params.put("pSerial", serial);
		}
        if (reference != null && reference.length() > 0) {
			params.put("pReference", reference);
		}
        if (code != null && code.length() > 0) {
			params.put("pCode", code);
		}
		if (account != null) {
			params.put("pAccount", account.getId());
		}
		if (documentType != null) {
			params.put("pDocu", documentType.ordinal());
		}
		if (beginDate != null) {
			params.put("pBDate", beginDate);
		}
		if (endDate != null) {
			params.put("pEDate", endDate);
		}
		if (getProcessType() != null) {
			params.put("pProcessType", getProcessType().ordinal());
		}
		if (getWorkBunch() != null){
			params.put("pWorkBunchId", getWorkBunch().getId());
		}
		
        if (getAdverseCode() != null && getAdverseCode().length() > 0) {
            params.put("pAdvCode", getAdverseCode());
        }

        if (getAdverseName() != null && getAdverseName().length() > 0) {
            params.put("pAdvName", getAdverseName());
        }		

        if (getCurrency() != null) {
        	params.put("pCurrency", getCurrency());
        }
        
        params.put("pProductLabel", SystemConfiguration.PRODUCTLABEL);
        
		try {
			jasperReport.reportToPDF("accountTxnReport", "Kasa_Hareket_Raporu", params);
		} catch (JRException ex) {
			log.error("Rapor Hatası", ex);
			facesMessages.add("Rapor Çalıştırılamadı");
		}

	}
	
	@SuppressWarnings("unchecked")
	public void xls() {
		Map params = new HashMap();
		params.put("pBDate", getDefaultBeginDate());
		params.put("pEDate", getDefaultEndDate());

		if (serial != null && serial.length() > 0) {
			params.put("pSerial", serial);
		}
        if (reference != null && reference.length() > 0) {
			params.put("pReference", reference);
		}
        if (code != null && code.length() > 0) {
			params.put("pCode", code);
		}
		if (account != null) {
			params.put("pAccount", account.getId());
		}
		if (documentType != null) {
			params.put("pDocu", documentType.ordinal());
		}
		if (beginDate != null) {
			params.put("pBDate", beginDate);
		}
		if (endDate != null) {
			params.put("pEDate", endDate);
		}
		if (getProcessType() != null) {
			params.put("pProcessType", getProcessType().ordinal());
		}
		if (getWorkBunch() != null){
			params.put("pWorkBunchId", getWorkBunch().getId());
		}
		
        if (getAdverseCode() != null && getAdverseCode().length() > 0) {
            params.put("pAdvCode", getAdverseCode());
        }

        if (getAdverseName() != null && getAdverseName().length() > 0) {
            params.put("pAdvName", getAdverseName());
        }		

        if (getCurrency() != null) {
        	params.put("pCurrency", getCurrency());
        }
        
        params.put("pProductLabel", SystemConfiguration.PRODUCTLABEL);
        
		try {
			jasperReport.reportToXls("accountTxnReport_xls", "Kasa_Hareket_Raporu", params);
		} catch (JRException ex) {
			log.error("Rapor Hatası", ex);
			facesMessages.add("Rapor Çalıştırılamadı");
		}

	}	

    private Date getDefaultBeginDate() {
        Calendar c = Calendar.getInstance();
        c.set(1900, 1, 1);
    	return c.getTime();
    }

    private Date getDefaultEndDate() {
    	Calendar c = Calendar.getInstance();
		c.set(2100, 12, 31);
    	return c.getTime();
    }
    
	@SuppressWarnings("unchecked")
	public void executeReport() {

		HibernateSessionProxy session = (HibernateSessionProxy) entityManager
				.getDelegate();

		Criteria ecrit = buildCriteria().getExecutableCriteria(session);
		ecrit.setCacheable(true);
		setResultList(ecrit.list());

		// log.debug("Result : #0", getResultList());

		//calculateSummary();
	}

	public DetachedCriteria buildCriteria() {

		DetachedCriteria crit = DetachedCriteria.forClass(AccountTxn.class);

		crit.createAlias("account", "account");

		crit.add(Restrictions.eq("active", true));

		if (getSerial() != null && getSerial().length() > 0) {
			crit.add(Restrictions.ilike("serial", getSerial(), MatchMode.START));
		}

		if (getReference() != null && getReference().length() > 0) {
			crit.add(Restrictions.ilike("reference", getReference(), MatchMode.START));
		}

		if (getCode() != null && getCode().length() > 0) {
			crit.add(Restrictions.ilike("code", getCode(), MatchMode.START));
		}

		if (getAccount() != null) {
			crit.add(Restrictions.eq("account", getAccount()));
		}

		if (getDocumentType() != null
				&& getDocumentType() != DocumentType.Unknown) {
			crit.add(Restrictions.eq("documentType", getDocumentType()));
		}

		if (getBeginDate() != null) {
			crit.add(Restrictions.ge("date", getBeginDate()));
		}

		if (getEndDate() != null) {
			crit.add(Restrictions.le("date", getEndDate()));
		}
		
		if (getProcessType() != null) {
			crit.add(Restrictions.eq("processType", getProcessType()));
		}
		
		if(getWorkBunch() != null){
			crit.add(Restrictions.eq("workBunch", getWorkBunch()));
		}
		
        if (getAdverseCode() != null && getAdverseCode().length() > 0) {
            crit.add(Restrictions.ilike("this.adverseCode", getAdverseCode(),MatchMode.START));
        }

        if (getAdverseName() != null && getAdverseName().length() > 0) {
            crit.add(Restrictions.ilike("this.adverseName", getAdverseName(),MatchMode.START));
        }

        if (getCurrency() != null) {
            crit.add(Restrictions.eq("this.amount.currency", getCurrency()));
        }        
		
        crit.add(Restrictions.eq("this.active", true));
        crit.addOrder(Order.desc("this.date"));
        crit.addOrder(Order.desc("this.id"));
		//crit.addOrder(Order.desc("this.serial"));
		//crit.addOrder(Order.asc("account.code"));
		//crit.addOrder(Order.asc("documentType"));

		return crit;
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

	public List<AccountTxn> getResultList() {
		return resultList;
	}

	public void setResultList(List<AccountTxn> resultList) {
		this.resultList = resultList;
	}

	public DocumentType getDocumentType() {
		return documentType;
	}

	public void setDocumentType(DocumentType documentType) {
		this.documentType = documentType;
	}

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
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

	public String getAdverseCode() {
		return adverseCode;
	}

	public void setAdverseCode(String adverseCode) {
		this.adverseCode = adverseCode;
	}

	public String getAdverseName() {
		return adverseName;
	}

	public void setAdverseName(String adverseName) {
		this.adverseName = adverseName;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

}
