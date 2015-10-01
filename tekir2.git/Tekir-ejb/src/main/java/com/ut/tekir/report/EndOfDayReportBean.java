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

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.persistence.EntityManager;

import net.sf.jasperreports.engine.JRException;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.log.Log;

import com.ut.tekir.configuration.SystemConfiguration;
import com.ut.tekir.entities.Account;
import com.ut.tekir.entities.OrganizationLevel;
import com.ut.tekir.entities.Warehouse;
import com.ut.tekir.framework.JasperHandlerBean;

/**
 * Gün sonu raporu home bileşenidir.
 * @author sinan.yumak
 */
@Stateful
@Name("endOfDayReport")
@Scope(ScopeType.SESSION)
public class EndOfDayReportBean implements EndOfDayReport {

	private static final long serialVersionUID = 1L;

	@Logger
    Log log;
    @In
    EntityManager entityManager;
    @In
    FacesMessages facesMessages;
    @In( create=true )
    JasperHandlerBean jasperReport;
    
    private Date beginDate;
    private Date endDate;
    private Account account;
    private Warehouse warehouse;
    private OrganizationLevel organizationLevel;
    
    @SuppressWarnings("unchecked")
	public void pdf() {
		Map params = new HashMap();

		
		if (endDate != null){
			params.put("pEDate", endDate);
		}
		else{
			params.put("pEDate", beginDate);
		}
		
		if (beginDate != null) {
			params.put("pBDate", beginDate);
		}

		if (account != null) {
			params.put("pAccount", account.getId());
            params.put("pAccountName", account.getName() );
		}

		if (warehouse != null) {
			params.put("pWare", warehouse.getId());
            params.put("pWareName", warehouse.getName());
		}
		
		if (organizationLevel != null) {
			params.put("pOrgLevel", organizationLevel.getId());
            params.put("pOrgLevelName", organizationLevel.getName());
		}
		
		params.put("pProductLabel", SystemConfiguration.PRODUCTLABEL);
		
		try {
			jasperReport.reportToPDF("gunsonu_durum_m", "Gun_Sonu_Durum_Raporu", params);
		} catch (JRException ex) {
			log.error("Gün sonu raporu çalıştırılken hata meydana geldi. Sebebi :{0}", ex);
			facesMessages.add("Gün sonu raporu çalıştırılken hata meydana geldi. Sebebi :{0}",ex.getMessage());
		}
        
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

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public Warehouse getWarehouse() {
		return warehouse;
	}

	public void setWarehouse(Warehouse warehouse) {
		this.warehouse = warehouse;
	}

	public OrganizationLevel getOrganizationLevel() {
		return organizationLevel;
	}

	public void setOrganizationLevel(OrganizationLevel organizationLevel) {
		this.organizationLevel = organizationLevel;
	}
	
    @Remove
    @Destroy
    public void destroy() {
    }

}
