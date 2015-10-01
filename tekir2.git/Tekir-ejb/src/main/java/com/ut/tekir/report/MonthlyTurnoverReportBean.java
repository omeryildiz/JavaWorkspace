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

import java.util.HashMap;
import java.util.Map;

import javax.ejb.Stateful;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import com.ut.tekir.entities.inv.TekirInvoice;
import com.ut.tekir.framework.BrowserBase;
import com.ut.tekir.framework.CalendarManager;
import com.ut.tekir.framework.InvoiceFilterModel;

@Stateful
@Name("monthlyTurnoverReport")
@Scope(value=ScopeType.SESSION)
public class MonthlyTurnoverReportBean extends BrowserBase<TekirInvoice, InvoiceFilterModel> implements MonthlyTurnoverReport<TekirInvoice, InvoiceFilterModel> {

	@In
	CalendarManager calendarManager;
	
	@Override
	public InvoiceFilterModel newFilterModel(){
		InvoiceFilterModel fm = new InvoiceFilterModel();
		fm.setBeginDate(calendarManager.getFirstDayOfMonth());
		fm.setEndDate(calendarManager.getLastDayOfMonth());
		return fm;
	}
	
	@SuppressWarnings("unchecked")
	public void pdf(){
		
		Map params = new HashMap();
			
		if(filterModel.getBeginDate()!= null){
			params.put("pBDate", filterModel.getBeginDate());
		}
		
		if(filterModel.getEndDate()!= null){
			params.put("pEDate", filterModel.getEndDate());
		}
		
		/* null ise varsayılan olarak 0 ataması ile tüm kayıtlar alınır */
		if(filterModel.getWorkBunch() != null){
			params.put("pWorkBunchId", filterModel.getWorkBunch().getId());
		} 

		/* 
		 * organizasyon aktif ise, raporda organizasyon kademesine bağlı depolar filtreye eklenecek 
		 * null ise varsayılan olarak 0 ataması ile tüm kayıtlar alınır 	
		 */
		if(filterModel.getOrganization() != null){
			params.put("pOrganizationId", filterModel.getOrganization().getId());
		} 
		
		execPdf("aylik_ciro_avm_excel","Aylik_Ciro",params);
		
	}
	
	@SuppressWarnings("unchecked")
	public void xls(){
		Map params = new HashMap();
		
		if(filterModel.getBeginDate()!= null){
			params.put("pBDate", filterModel.getBeginDate());
		}
	
		if(filterModel.getEndDate()!= null){
			params.put("pEDate", filterModel.getEndDate());
		}
		
		if(filterModel.getWorkBunch() != null){
			params.put("pWorkBunchId", filterModel.getWorkBunch().getId());
		}
		
		if(filterModel.getOrganization() != null){
			params.put("pOrganizationId", filterModel.getOrganization().getId());
		}
		
		execXls("aylik_ciro_avm_excel","Aylik_Ciro",params);
	}
	
}
