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
import java.util.HashMap;
import java.util.Map;

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
import com.ut.tekir.framework.CalendarManager;
import com.ut.tekir.framework.JasperHandlerBean;
import com.ut.tekir.framework.OptionManager;
import com.ut.tekir.options.CompanyOptionKey;
import com.ut.tekir.util.StringUtils;
import com.ut.tekir.framework.SystemProperties;

/**
 *
 * @author bilga, volkan
 */
@Name( "extractAccountReport" )
@Scope(ScopeType.SESSION)
public class ExtractAccountReportBean {

    @Logger
    private Log log;
    @In
    private FacesMessages facesMessages;
    @In( create=true )
    private JasperHandlerBean jasperReport;
    @In( create=true )
    private OptionManager optionManager;
    @In
    private CalendarManager calendarManager;

    private ExtractAccountFilterModel fm;
    
    @In( create=true )
    SystemProperties systemProperties;
    
    @Create
    public void init() {
    	initFilterModel();
    }
    
    private void initFilterModel() {
    	fm = new ExtractAccountFilterModel();
    	fm.setBeginDate(calendarManager.getLastMonthDate());
    	fm.setEndDate(calendarManager.getCurrentDate());
    }

    @SuppressWarnings("unchecked")
	public void pdf(){
		Map params = new HashMap();

		if (fm.getContact() != null) {
			params.put("pContact", fm.getContact().getId());
			params.put("pContactCode", fm.getContact().getCode());
			if (fm.getContact().getPerson()) {
				params.put("pContactFullname", fm.getContact().getFullname());
			} else {
				params.put("pContactFullname", fm.getContact().getCompany());					
			};			
		}
        
        Calendar c = Calendar.getInstance();
        c.set(1900, 1, 1);
        
        if (fm.getBeginDate() != null) {
            params.put("pBDate", fm.getBeginDate());
        }
        
        c.set(2100, 1, 1);
        
        if (fm.getEndDate() != null) {
        	params.put("pEDate", fm.getEndDate());
        }
        
        if (StringUtils.isNotEmpty(fm.getCurrency())){
        	params.put("pCurrency", fm.getCurrency());
        }
        
        if (StringUtils.isNotEmpty(fm.getFinanceTxnCode())){
        	params.put("pCode", fm.getFinanceTxnCode());
        }
        
        if(fm.getWorkBunch() != null){
        	params.put("pWorkBunchId", fm.getWorkBunch().getId());
        }
        
        params.put("pCompany", optionManager.getOption(CompanyOptionKey.COMPANY_NAME, true).getAsString());
        params.put("pCompanyName", optionManager.getOption(CompanyOptionKey.COMPANY_TITLE, true).getAsString());
        params.put("pCompanyTaxOffice", optionManager.getOption(CompanyOptionKey.COMPANY_TAXOFFICE,true).getAsString());
        params.put("pCompanyTaxNumber", optionManager.getOption(CompanyOptionKey.COMPANY_TAX_NUMBER,true).getAsString());
        params.put("pCompanyPhone", optionManager.getOption(CompanyOptionKey.COMPANY_PHONE,true).getAsString());
        params.put("pCompanyFax", optionManager.getOption(CompanyOptionKey.COMPANY_FAX,true).getAsString());
        params.put("pCompanyEmail", optionManager.getOption(CompanyOptionKey.COMPANY_EMAIL,true).getAsString());
        params.put("pCompanyWeb", optionManager.getOption(CompanyOptionKey.COMPANY_WEB,true).getAsString());
        params.put("pLogopath", systemProperties.getProperty("report.logo.file") );	
        params.put("pProductLabel", SystemConfiguration.PRODUCTLABEL); 
        
        log.debug("Parametreler : #0", params );
        
        try {
        	
        	/*
        	 * 
        	 * bu sekilde ayri bir rapor yok, o sebeple iptal edildi
        	 * 
	        	if (StringUtils.isNotEmpty(fm.getFinanceTxnCode())){
	        		//jasperReport.reportToPDF( "codeExtractAccount", "Cari_Hesap_Ekstresi", params);
	        		jasperReport.reportToPDF( "extractAccount", "Cari_Hesap_Ekstresi", params);
	        	}else 
        	*/
            if (fm.getCurrency() == null) {
        		//yerel doviz uzerinden raporlar
        		jasperReport.reportToPDF( "extractAccount", "Cari_Hesap_Ekstresi", params);
        	} else {
        		//doviz tipi es islemler uzerinden raporlar
        		jasperReport.reportToPDF( "currencyExtractAccount", "Cari_Hesap_Ekstresi", params);
        	}
        } catch (JRException ex) {
            log.error("Rapor Hatası", ex);
            facesMessages.add("Rapor Çalıştırılamadı");
        }
        
    }

    public ExtractAccountFilterModel getFilterModel() {
    	return fm;
    }

    public void setFilterModel(ExtractAccountFilterModel fm) {
    	this.fm = fm;
    }
}
