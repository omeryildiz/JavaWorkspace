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
import com.ut.tekir.entities.User;
import com.ut.tekir.entities.Warehouse;
import com.ut.tekir.entities.WorkBunch;
import com.ut.tekir.framework.JasperHandlerBean;

/**
 * Tezgahtar raporu home bileşenidir.
 * @author sinan.yumak
 */
@Stateful
@Name("clerkReport")
@Scope(ScopeType.SESSION)
public class ClerkReportBean implements ClerkReport {

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
    private OrganizationLevel organizationLevel;
    private Warehouse warehouse;
    private User user;
    private User clerk;
    private WorkBunch workBunch;
    
    @SuppressWarnings("unchecked")
	public void pdf() {
		Map params = new HashMap();

		if (beginDate != null) {
			params.put("pBDate", beginDate);
		}

		if (endDate != null) {
			params.put("pEDate", endDate);
		}

		if (account != null) {
			params.put("pAccount", account.getId());
		}

		if (organizationLevel != null) {
			params.put("pOrgLevel", organizationLevel.getId());
		}

		if (warehouse != null) {
			params.put("pWare", warehouse.getId());
		}
		
		if (clerk != null) {
			params.put("pClerk", clerk.getId());
		}

		if (user != null) {
			params.put("pUser", user.getUserName());
		}

		if(getWorkBunch() != null){
			params.put("pWorkBunchId", getWorkBunch().getId());
		}
		
		params.put("pProductLabel", SystemConfiguration.PRODUCTLABEL);
		
		try {
			jasperReport.reportToPDF("gunsonu_tezgahtar_m", "Satici_Performans_Raporu", params);
		} catch (JRException ex) {
			log.error("Tezgahtar raporu çalıştırılırken hata meydana geldi. Sebebi :{0}", ex);
			facesMessages.add("Tezgahtar raporu çalıştırılırken hata meydana geldi. Sebebi :{0}",ex.getMessage());
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

	public OrganizationLevel getOrganizationLevel() {
		return organizationLevel;
	}

	public void setOrganizationLevel(OrganizationLevel organizationLevel) {
		this.organizationLevel = organizationLevel;
	}

	public Warehouse getWarehouse() {
		return warehouse;
	}

	public void setWarehouse(Warehouse warehouse) {
		this.warehouse = warehouse;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public User getClerk() {
		return clerk;
	}

	public void setClerk(User clerk) {
		this.clerk = clerk;
	}
	
	public WorkBunch getWorkBunch() {
		return workBunch;
	}

	public void setWorkBunch(WorkBunch workBunch) {
		this.workBunch = workBunch;
	}

	@Remove
    @Destroy
    public void destroy() {
    }

}
