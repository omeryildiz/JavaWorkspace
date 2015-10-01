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

import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import net.sf.jasperreports.engine.JRException;

//import org.hibernate.criterion.Restrictions;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.log.Log;

import com.ut.tekir.configuration.SystemConfiguration;
import com.ut.tekir.entities.Contact;
import com.ut.tekir.entities.Currency;
//import com.ut.tekir.entities.Region;
import com.ut.tekir.entities.User;
import com.ut.tekir.finance.print.ReconciliationContactModel;
import com.ut.tekir.framework.JasperHandlerBean;
import com.ut.tekir.framework.OptionManager;
import com.ut.tekir.framework.SystemProperties;

/**
 *
 * @author bilga, volkan
 */
@Name( "reconciliationContactReport" )
@Scope(ScopeType.SESSION)
public class ReconciliationContactReportBean {

    @Logger
    protected Log log;

    @In
    protected FacesMessages facesMessages;

    @In( create=true )
    JasperHandlerBean jasperReport;

    @In( create=true )
    OptionManager optionManager;
    @In
    private EntityManager entityManager;

    @In User activeUser;

    @In( create=true )
    SystemProperties systemProperties;

    private Contact contact;
    private Date date;
    private String currency;  
    //private Region region;

    @SuppressWarnings("unchecked")
    private ReconciliationContactModel reconciliationContact;

    @SuppressWarnings("unchecked")
    public ReconciliationContactModel getReconciliation(){

    	String query="";
    	
    	if(getCurrency() != null) {
	    	query="select cn.fullname, cn.company as company, ft.ccy , "+
	    	" sum(ft.ccyval * case when ft.finance_action = 1 then -1 else 1 end) as totallcyval"+
	    	" from FINANCE_TXN ft " +
	        " inner join CONTACT cn on cn.id = ft.contact_id "+
	    	" where ft.isActive=true ";
	    	//dovizli
	    	
    	} else {
	    	query="select cn.fullname, cn.company as company, ft.ccy , "+
	    	" sum(ft.lcyval * case when ft.finance_action = 1 then -1 else 1 end) as totallcyval"+
	    	" from FINANCE_TXN ft " +
	        " inner join CONTACT cn on cn.id = ft.contact_id "+
	    	" where ft.isActive=true ";
	    	//yerel karsilik
	    	
    	}
    	//log.info("asil sorgu: #0", query);
    	
    	if(getContact() != null){
    		query = query.concat(" and ft.contact_id = :contact");
    	}

    	if(getDate() != null){
    		query = query.concat(" and ft.txn_date <= :date");
    	}
    	
    	if(getCurrency() != null) {
    		query = query.concat(" and ft.ccy = :currency");
    		log.debug("dovizli sorgu: #0", query);
    	}

    	query = query.concat(" group by cn.name,cn.company");

    	log.debug(" tum sorgu: #0", query);
    	
    	Query myQuery = entityManager.createNativeQuery(query);
    	
    	if(getContact() != null){
    		myQuery.setParameter("contact", getContact().getId());
    	}

    	if(getDate() != null){
    		myQuery.setParameter("date", getDate());
    	}
    	
    	if(getCurrency() != null){
    		myQuery.setParameter("currency", getCurrency());
    	}

        System.out.println("\n\n\n\n\nmyq: "+myQuery);

        List l=myQuery.getResultList();
        if(l.size() > 0){
        	log.debug(" sonuc single: #0", l);
            return new ReconciliationContactModel((Object[])myQuery.getSingleResult());
        }else{
            ReconciliationContactModel rcm= new ReconciliationContactModel();
            rcm.setCompany(contact.getCompany());
            rcm.setFullname(contact.getName());
            rcm.setTotallcyval(BigDecimal.ZERO);
            rcm.setCurrency(currency);
            log.debug(" sonuc rcm: #0", rcm);
            return rcm;
        }

    }

	@SuppressWarnings("unchecked")
	public void print() {

		try {

			log.info("ReconciliationContact Print");

			Map params = new HashMap();
            
            params.put("pReportDate", getDate());            
			params.put("pCompanyName", optionManager.getOption("company.Title").getAsString());
            params.put("pCompanyAddress", optionManager.getOption("company.Address").getAsString());
            params.put("pCompanyTaxOffice", optionManager.getOption("company.TaxOffice").getAsString());
            params.put("pCompanyTaxNumber", optionManager.getOption("company.TaxNumber").getAsString());
            params.put("pCompanyPhone", optionManager.getOption("company.Phone").getAsString());
            params.put("pCompanyFax", optionManager.getOption("company.Fax").getAsString());
            params.put("pCompanyEmail", optionManager.getOption("company.Email").getAsString());
            params.put("pCompanyWeb", optionManager.getOption("company.Web").getAsString());
            params.put("pLogopath", systemProperties.getProperty("report.logo.file") );	
            params.put("pProductLabel", SystemConfiguration.PRODUCTLABEL);
            
            if(getCurrency() != null){
            	params.put("pReportCurrency", getCurrency());
            } else {
            	params.put("pReportCurrency", optionManager.getOption("systemOption.CurrencyCode").getAsString());
            }
            
            BigDecimal temp = getReconciliation().getTotallcyval();
            if (temp.compareTo(BigDecimal.ZERO) < 0){
            	temp = temp.negate();
            }

        	DecimalFormat pattern = new DecimalFormat( "###,##0.00" );
        	double dtotal = temp.doubleValue();
        	String stotal = pattern.format(dtotal);
        	params.put("totallcyval", stotal);

        	

            if (getContact().getPerson().booleanValue() == false){
            	params.put("person", 0);
            }else{
            	params.put("person", 1);
            }

            /**
             * printTemplate (sablonlar) altindaki dosyayi kullanir. Yazi icerigi degistirilebilsin diye. 
             */

            List<ReconciliationContactModel> resultList = new ArrayList<ReconciliationContactModel>();
            resultList.add(getReconciliation());
            jasperReport.printObjectToPDF( "Cari_Mutabakat_Mektubu", "cariMutabakatMektubu", params, resultList);

		} catch (JRException ex) {
			log.error("Collection print error", ex);
		} catch (FileNotFoundException e) {
			log.error("Collection template not found #0", e);
			facesMessages.add("Cari mutabakat mektubu basım şablonu bulunamadı!");
		}
	}

	public void setContact(Contact contact) {
		this.contact = contact;
	}

	public Contact getContact() {
		return contact;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Date getDate() {
		return date;
	}
	
	public void setReconciliationContact(ReconciliationContactModel reconciliationContact) {
		this.reconciliationContact = reconciliationContact;
	}

	public ReconciliationContactModel getReconciliationContact() {
		return reconciliationContact;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}



}
