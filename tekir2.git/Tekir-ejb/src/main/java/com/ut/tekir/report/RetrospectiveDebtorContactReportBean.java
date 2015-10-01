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
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateful;
import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.joda.time.DateTime;

import com.ut.tekir.configuration.SystemConfiguration;
import com.ut.tekir.contact.ContactSuggestion;
import com.ut.tekir.framework.BrowserBase;
import com.ut.tekir.framework.CalendarManager;
import com.ut.tekir.general.GeneralSuggestion;

/**
 * @author rustem
 *
 */

@Stateful
@Name("retrospectiveDebtorContactReport")
@Scope(ScopeType.SESSION)
public class RetrospectiveDebtorContactReportBean extends BrowserBase<RetrospectiveDebtorContactViewModel, RetrospectiveDebtorContactReportFilterModel> implements RetrospectiveDebtorContactReportBrowse<RetrospectiveDebtorContactViewModel, RetrospectiveDebtorContactReportFilterModel> {

	@In
	GeneralSuggestion generalSuggestion;
	
	@In
	ContactSuggestion contactSuggestion;
	
	@In
	CalendarManager calendarManager;
	
	
    @Override
    public RetrospectiveDebtorContactReportFilterModel newFilterModel() {
    	RetrospectiveDebtorContactReportFilterModel rt = new RetrospectiveDebtorContactReportFilterModel();
    	rt.setDate(calendarManager.getCurrentDate());
        return rt;
    }


    private final int FIRST_SLICE = 30;  // 0-30
    private final int SECOND_SLICE = 60; // 30-60
    private final int THIRD_SLICE = 90;  // 60-90 
    private final int FOURTH_SLICE = 120; // 90-120

    	
    	
    @Override
	@SuppressWarnings("unchecked")
	public void search(){
    	
    	Date tarih1 = getRolledTime(FIRST_SLICE);
        Date tarih2 = getRolledTime(SECOND_SLICE);
        Date tarih3 = getRolledTime(THIRD_SLICE);
        Date tarih4 = getRolledTime(FOURTH_SLICE);
    	
		if(entityList != null){
			entityList.clear();
		}
		else{
			entityList = new ArrayList<RetrospectiveDebtorContactViewModel>();
		}

		StringBuilder outerQuery = new StringBuilder();
		
		outerQuery.append("select * from ( ");
		
		StringBuilder firstInnerQuery = new StringBuilder();
		
		firstInnerQuery.append(" select ft.TXN_DATE, ft.CONTACT_ID, ft.ccy ,sum(ft.CCYVAL * case when ft.finance_action = 1 then -1 else 1 end ");
	
		StringBuilder secondInnerQuery = new StringBuilder();
		
		secondInnerQuery.append(" select sum(ft1.CCYVAL) from FINANCE_TXN ft1 where ft1.TXN_DATE >=:tarih1 and ft1.CONTACT_ID=ft.CONTACT_ID and ft1.CCY=ft.CCY and ft1.finance_action =0  ");

		StringBuilder thirdInnerQuery = new StringBuilder();
		thirdInnerQuery.append(" select sum(ft1.CCYVAL) from FINANCE_TXN ft1 where ft1.TXN_DATE >=:tarih2 and ft1.TXN_DATE <:tarih1 and ft1.CONTACT_ID=ft.CONTACT_ID and ft1.CCY=ft.CCY  and ft1.finance_action =0  ");
		
		StringBuilder fourthInnerQuery = new StringBuilder();
		fourthInnerQuery.append(" select sum(ft1.CCYVAL) from FINANCE_TXN ft1 where ft1.TXN_DATE >=:tarih3 and ft1.TXN_DATE <:tarih2 and ft1.CONTACT_ID=ft.CONTACT_ID and ft1.CCY=ft.CCY and ft1.finance_action =0  ");
		
		StringBuilder fifthInnerQuery = new StringBuilder();
		fifthInnerQuery.append(" select sum(ft1.CCYVAL) from FINANCE_TXN ft1 where ft1.TXN_DATE >=:tarih4 and ft1.TXN_DATE <:tarih3 and ft1.CONTACT_ID=ft.CONTACT_ID and ft1.CCY=ft.CCY  and ft1.finance_action =0  ");
		
		StringBuilder sixthInnerQuery = new StringBuilder();
		sixthInnerQuery.append(" select sum(ft1.CCYVAL) from FINANCE_TXN ft1 where ft1.TXN_DATE <:tarih4 and ft1.CONTACT_ID=ft.CONTACT_ID  and ft1.CCY=ft.CCY  and ft1.finance_action =0  ");
		
		StringBuilder seventhInnerQuery = new StringBuilder();
		seventhInnerQuery.append(" select sum(ft1.CCYVAL) from FINANCE_TXN ft1 where ft1.finance_action= 1 and ft1.CONTACT_ID=ft.CONTACT_ID and ft1.CCY=ft.CCY  ");

		if(filterModel.getWorkBunch() != null ){ 
			secondInnerQuery.append(" and ft1.WORK_BUNCH_ID= :workBunchId ");
			thirdInnerQuery.append(" and ft1.WORK_BUNCH_ID= :workBunchId ");
			fourthInnerQuery.append(" and ft1.WORK_BUNCH_ID= :workBunchId ");
			fifthInnerQuery.append(" and ft1.WORK_BUNCH_ID= :workBunchId ");
			sixthInnerQuery.append(" and ft1.WORK_BUNCH_ID= :workBunchId ");
			seventhInnerQuery.append(" and ft1.WORK_BUNCH_ID= :workBunchId ");
			
		}
		
		
		outerQuery.append(firstInnerQuery.toString());
		outerQuery.append(" ) as total, ( ");
		outerQuery.append(secondInnerQuery.toString());
		outerQuery.append(" ) as otuz_gune_kadar, ( ");
		outerQuery.append(thirdInnerQuery);
		outerQuery.append(" ) as atmis_gune_kadar, ( ");
		outerQuery.append(fourthInnerQuery);
		outerQuery.append("  ) as doksan_gune_kadar, ( ");
		outerQuery.append(fifthInnerQuery);
		outerQuery.append("  ) as yuzyirmi_gune_kadar, ( ");
		outerQuery.append(sixthInnerQuery);
		outerQuery.append(" ) as yuzyirmi_gunluk_ve_ustu, ( ");
		outerQuery.append(seventhInnerQuery);
		outerQuery.append(" ) as totalOdenen ");
		outerQuery.append(" ,ft.WORK_BUNCH_ID ");
		outerQuery.append("  from FINANCE_TXN ft group by ft.CONTACT_ID, ft.ccy) as ilk where ilk.total > 0 and ");
		
		if(filterModel.getContact() != null){
			outerQuery.append(" ilk.CONTACT_ID=:contactId and ");
		}
		
		if (filterModel.getProcessType() != null) {
			outerQuery.append(" ilk.PROCESS_TYPE = :processType and ");
		}		
		
		if(filterModel.getWorkBunch() != null){
			outerQuery.append(" ilk.WORK_BUNCH_ID =:workBunchId and ");
		}
		
		outerQuery.append(" ilk.CONTACT_ID IN ( ");
		
		StringBuilder eighthInnerQuery = new StringBuilder();
		eighthInnerQuery.append(" select debt.id from (select cn.id, cn.code, cn.fullname, ft.ccy, cn.person, cn.company, sum(ft.LCYVAL * case when ft.finance_action = 1 then -1 else 1 end ) as totallcyval, sum(ft.CCYVAL * case when ft.finance_action = 1 then -1 else 1 end ) as totalccyval from FINANCE_TXN ft inner join CONTACT cn on cn.id = ft.contact_id where ft.isactive=true group by cn.code, cn.fullname, ft.ccy ) as debt where debt.totalccyval > 0 order by debt.code ) ");
		
		outerQuery.append(eighthInnerQuery);
		outerQuery.append(" group by ilk.CONTACT_ID, ilk.ccy ");
		
		Query query = entityManager.createNativeQuery(outerQuery.toString());
		
		query.setParameter("tarih1", tarih1);
		query.setParameter("tarih2", tarih2);
		query.setParameter("tarih3", tarih3);
		query.setParameter("tarih4", tarih4);
		
		if(filterModel.getContact() != null){
			query.setParameter("contactId", filterModel.getContact().getId());
		}
		
		if(filterModel.getWorkBunch() != null){
			query.setParameter("workBunchId", filterModel.getWorkBunch().getId());
		}

		
		List<Object[]> resultList = query.getResultList();
		
		for (Object[] item : resultList){
			RetrospectiveDebtorContactViewModel model = new RetrospectiveDebtorContactViewModel();
			
			model.setDate((Date)item[0]);
			Long conId = new BigDecimal((BigInteger)item[1]).longValue();
			model.setContact(contactSuggestion.findContact(conId));
			model.setCcy((String)item[2]);
			model.setTotal((BigDecimal)item[3]);
			model.setOtuzGuneKadar((BigDecimal)item[4]);
			model.setAtmisGuneKadar((BigDecimal)item[5]);
			model.setDoksanGuneKadar((BigDecimal)item[6]);
			model.setYuzyirmiGuneKadar((BigDecimal)item[7]);
			model.setYuzyirmiGunVeSonrasi((BigDecimal)item[8]);
			model.setTotalOdenen((BigDecimal)item[9]);
			model.setContactCode(model.getContact().getCode());
			model.setPerson(model.getContact().getPerson());
			model.setContactFullName(model.getContact().getFullname());
			model.setCompany(model.getContact().getCompany());
			
			if(model.getTotalOdenen() != null && model.getTotalOdenen().compareTo(BigDecimal.ZERO)>0){
				downPayments(model);
			}
			
			entityList.add(model);
		}
	}

    private Date getRolledTime(int interval) {   	
    	log.debug("gelen param : #0", interval);
    	
    	DateTime dt = new DateTime(filterModel.getDate());
    	log.debug("rapor gunu  : #0", dt.toDate());
    	
    	dt = dt.minusDays(interval);    	
    	log.debug("dt edilen : #0", dt.toDate());
    	
    	return dt.toDate();
    }
    
    private void downPayments(RetrospectiveDebtorContactViewModel model){    	
    	if (model.getYuzyirmiGunVeSonrasi() != null){
    		if(model.getTotalOdenen().compareTo(model.getYuzyirmiGunVeSonrasi())<=0){
    			model.setYuzyirmiGunVeSonrasi(model.getYuzyirmiGunVeSonrasi().subtract(model.getTotalOdenen()));
    			model.setTotalOdenen(BigDecimal.ZERO);
    			return;
    		}
    		else{
    			model.setTotalOdenen(model.getTotalOdenen().subtract(model.getYuzyirmiGunVeSonrasi()));
    			model.setYuzyirmiGunVeSonrasi(BigDecimal.ZERO);
    		}
    	}
    	
    	if (model.getYuzyirmiGuneKadar() != null){
    		if(model.getTotalOdenen().compareTo(model.getYuzyirmiGuneKadar())<=0){
    			model.setYuzyirmiGuneKadar(model.getYuzyirmiGuneKadar().subtract(model.getTotalOdenen()));
    			model.setTotalOdenen(BigDecimal.ZERO);
    			return;
    		}
    		else{
    			model.setTotalOdenen(model.getTotalOdenen().subtract(model.getYuzyirmiGuneKadar()));
    			model.setYuzyirmiGuneKadar(BigDecimal.ZERO);
    		}
    	}
    	
    	if (model.getDoksanGuneKadar() != null){
    		if(model.getTotalOdenen().compareTo(model.getDoksanGuneKadar())<=0){
    			model.setDoksanGuneKadar(model.getDoksanGuneKadar().subtract(model.getTotalOdenen()));
    			model.setTotalOdenen(BigDecimal.ZERO);
    			return;
    		}
    		else{
    			model.setTotalOdenen(model.getTotalOdenen().subtract(model.getDoksanGuneKadar()));
    			model.setDoksanGuneKadar(BigDecimal.ZERO);
    		}
    	}
    	
    	if (model.getAtmisGuneKadar() != null){
    		if(model.getTotalOdenen().compareTo(model.getAtmisGuneKadar())<=0){
    			model.setAtmisGuneKadar(model.getAtmisGuneKadar().subtract(model.getTotalOdenen()));
    			model.setTotalOdenen(BigDecimal.ZERO);
    			return;
    		}
    		else{
    			model.setTotalOdenen(model.getTotalOdenen().subtract(model.getAtmisGuneKadar()));
    			model.setAtmisGuneKadar(BigDecimal.ZERO);
    		}
    	}
    	
    	if (model.getOtuzGuneKadar() != null){
    		if(model.getTotalOdenen().compareTo(model.getOtuzGuneKadar())<=0){
    			model.setOtuzGuneKadar(model.getOtuzGuneKadar().subtract(model.getTotalOdenen()));
    			model.setTotalOdenen(BigDecimal.ZERO);
    			return;
    		}
    		else{
    			model.setTotalOdenen(model.getTotalOdenen().subtract(model.getOtuzGuneKadar()));
    			model.setOtuzGuneKadar(BigDecimal.ZERO);
    		}
    	}
    	
    	
    }
    
    
    @SuppressWarnings("unchecked")
    public void pdf(){
    	Map params = new HashMap();
    	
    	Date tarih1 = getRolledTime(FIRST_SLICE);
        Date tarih2 = getRolledTime(SECOND_SLICE);
        Date tarih3 = getRolledTime(THIRD_SLICE);
        Date tarih4 = getRolledTime(FOURTH_SLICE);
    	
    	if(filterModel.getContact() != null){
    		params.put("pConId", filterModel.getContact().getId());
    		
    	}
    	
    	params.put("pTarih1", tarih1);
    	params.put("pTarih2", tarih2);
    	params.put("pTarih3", tarih3);
    	params.put("pTarih4", tarih4);
    	params.put("pBeginDate", filterModel.getDate());
    	
    	if(filterModel.getWorkBunch()!= null){
    		params.put("pWorkBunchId", filterModel.getWorkBunch().getId());
    	}
    	
    	params.put("pProductLabel", SystemConfiguration.PRODUCTLABEL); 
    	
    	execPdf("geriye_donuk_borclu_listesi", "Geriye_Donuk_Cari_Borclu_Raporu", params);
    }
    
    @SuppressWarnings("unchecked")
    public void xls(){
    	
    	Map params = new HashMap();
    	
    	Date tarih1 = getRolledTime(FIRST_SLICE);
        Date tarih2 = getRolledTime(SECOND_SLICE);
        Date tarih3 = getRolledTime(THIRD_SLICE);
        Date tarih4 = getRolledTime(FOURTH_SLICE);
    	
    	if(filterModel.getContact() != null){
    		params.put("pConId", filterModel.getContact().getId());
    	}
    	
    	params.put("pTarih1", tarih1);
    	params.put("pTarih2", tarih2);
    	params.put("pTarih3", tarih3);
    	params.put("pTarih4", tarih4);
    	params.put("pBeginDate", filterModel.getDate());
    	
    	if(filterModel.getWorkBunch()!= null){
    		params.put("pWorkBunchId", filterModel.getWorkBunch().getId());
    	}

    	params.put("pProductLabel", SystemConfiguration.PRODUCTLABEL); 
    	
    	execXls("geriye_donuk_borclu_listesi_excel", "Geriye_Donuk_Cari_Borclu_Raporu", params);
    }
    
}
