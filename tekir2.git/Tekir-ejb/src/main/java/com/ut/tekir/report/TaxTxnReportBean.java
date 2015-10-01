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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.persistence.EntityManager;

import net.sf.jasperreports.engine.JRException;

import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.log.Log;
import org.jboss.seam.persistence.HibernateSessionProxy;

import com.google.common.base.Joiner;
import com.ut.tekir.configuration.SystemConfiguration;
import com.ut.tekir.entities.DocumentType;
import com.ut.tekir.entities.TaxTxn;
import com.ut.tekir.framework.CalendarManager;
import com.ut.tekir.framework.JasperHandlerBean;

/**
 *
 * @author yigit
 */
@Stateful
@Name("taxTxnReport")
@Scope(ScopeType.SESSION)
public class TaxTxnReportBean implements TaxTxnReport {

    @Logger
	protected Log log;
	@In
	private EntityManager entityManager;
	@In
	FacesMessages facesMessages;
    @In(create = true)
	JasperHandlerBean jasperReport;
    @In
    CalendarManager calendarManager;

    private TaxTxnFilterModel filterModel;
    private List<TaxTxn> resultList;
    private DocumentType[] documentTypes;
    private int exid;

    @Create
    public void init(){

        setFilterModel(new TaxTxnFilterModel());
        getFilterModel().setBeginDate(calendarManager.getLastTenDay());
        getFilterModel().setEndDate(calendarManager.getCurrentDate());
    }

    @Remove
    @Destroy
    public void destroy(){
        
    }

    @SuppressWarnings("unchecked")
	public void search() {

		HibernateSessionProxy session = (HibernateSessionProxy) entityManager.getDelegate();

		Criteria ecrit = buildCriteria().getExecutableCriteria(session);
		ecrit.setCacheable(false);
		setResultList(ecrit.list());

	}

    public DetachedCriteria buildCriteria() {

        DetachedCriteria crit = DetachedCriteria.forClass(TaxTxn.class);


        if (getFilterModel().getSerial() != null && getFilterModel().getSerial().length() > 0) {
            crit.add(Restrictions.ilike("serial", getFilterModel().getSerial() ,MatchMode.START));
        }

        if (getFilterModel().getReference() != null && getFilterModel().getReference().length() > 0) {
            crit.add(Restrictions.ilike("reference", getFilterModel().getReference(),MatchMode.START));
        }

        if (getFilterModel().getCode() != null && getFilterModel().getCode().length() > 0) {
            crit.add(Restrictions.ilike("code", getFilterModel().getCode() ,MatchMode.START));
        }

        if (getFilterModel().getBeginDate() != null) {
            crit.add(Restrictions.ge("date", getFilterModel().getBeginDate()));
        }

        if (getFilterModel().getEndDate() != null) {
            crit.add(Restrictions.le("date", getFilterModel().getEndDate()));
        }

        /*
        if (getFilterModel().getDocumentType() != null && getFilterModel().getDocumentType() != DocumentType.Unknown) {
            crit.add(Restrictions.eq("documentType", getFilterModel().getDocumentType()));
        }
         */

        if ( (getFilterModel().getTaxReturnType() != null && getFilterModel().getTaxReturnType().getDocumentTypesAsArray().length!=0)
        		|| getFilterModel().getDocumentType() != null) {
        	List<DocumentType> documentTypes = new ArrayList<DocumentType>();

        	if(getFilterModel().getTaxReturnType() != null  && getFilterModel().getTaxReturnType().getDocumentTypesAsArray().length!=0){
        		documentTypes.addAll( getFilterModel().getTaxReturnType().getDocumentTypesAsList() );
        	}
        	
        	if (getFilterModel().getDocumentType() != null) {
        		documentTypes.add( getFilterModel().getDocumentType() );
        	}
        	crit.add(Restrictions.in("documentType", documentTypes));
        }
        
        if (getFilterModel().getTaxType() != null) {
            crit.add(Restrictions.eq("taxType", getFilterModel().getTaxType()));
        }

        if(getFilterModel().getAction() != null){
            crit.add(Restrictions.eq("action", getFilterModel().getAction()));
        }
        
        if(filterModel.getWorkBunch() != null){
        	crit.add(Restrictions.eq("workBunch", filterModel.getWorkBunch()));
        }

        crit.add(Restrictions.eq("this.active", true));
        crit.addOrder(Order.desc("this.date"));
        crit.addOrder(Order.desc("this.id"));
        //crit.addOrder(Order.desc("serial"));

        log.debug("Sonuç : #0", crit);

        return crit;
    }

    @SuppressWarnings("unchecked")
    public void pdf(){
    	
    	Map params = new HashMap();

    	if( filterModel.getSerial() != null && filterModel.getSerial().length()>0 ){
    		params.put("pSerial", filterModel.getSerial());
    	}
    	
    	if( filterModel.getReference() != null && filterModel.getReference().length()>0 ){
    		params.put("pReference", filterModel.getReference());
    	}
    	
    	if( filterModel.getCode() != null && filterModel.getCode().length()>0 ){
    		params.put("pCode", filterModel.getCode());
    	}
    	
    	if ( (getFilterModel().getTaxReturnType() != null && getFilterModel().getTaxReturnType().getDocumentTypesAsArray().length!=0)
        		|| getFilterModel().getDocumentType() != null) {
    		List<Integer> documentTypes = new ArrayList<Integer>();

        	if(getFilterModel().getTaxReturnType() != null  && getFilterModel().getTaxReturnType().getDocumentTypesAsArray().length!=0){
        		for ( DocumentType docType : getFilterModel().getTaxReturnType().getDocumentTypesAsList() ) {
        			documentTypes.add( docType.ordinal() );
        		}
        	}
        	
        	if (getFilterModel().getDocumentType() != null) {
        		documentTypes.add( getFilterModel().getDocumentType().ordinal() );
        	}
        	
			params.put("pDocType", Joiner.on(",").join(documentTypes.toArray()));
        }
    	
    	if( filterModel.getBeginDate() != null){
    		params.put("pBDate", filterModel.getBeginDate());
    	}
    	
    	if( filterModel.getEndDate() != null){
    		params.put("pEDate", filterModel.getEndDate());
    	}
    	
    	if( filterModel.getTaxType() != null){
    		params.put("pTaxType", filterModel.getTaxType().ordinal());
    	}
    	
    	if( filterModel.getAction() != null){
    		params.put("pAction", filterModel.getAction().ordinal());
    	}
    	
    	if(filterModel.getWorkBunch() != null){ 
    		params.put("pWorkBunchId", filterModel.getWorkBunch().getId());
    	}

    	params.put("pProductLabel", SystemConfiguration.PRODUCTLABEL);
    	
    	try {
			jasperReport.reportToPDF("taxTxnReport", "Vergi_Raporu", params);
		} catch (JRException ex) {
			log.error("Vergi raporu çalıştırılırken hata meydana geldi. Sebebi :{0}", ex);
			facesMessages.add("Vergi raporu çalıştırılırken hata meydana geldi. Sebebi :{0}",ex.getMessage());
		}
    	
    }
    
    @SuppressWarnings("unchecked")
    public void xls(){
    	
    	Map params = new HashMap();

    	if( filterModel.getSerial() != null && filterModel.getSerial().length()>0 ){
    		params.put("pSerial", filterModel.getSerial());
    	}
    	
    	if( filterModel.getReference() != null && filterModel.getReference().length()>0 ){
    		params.put("pReference", filterModel.getReference());
    	}
    	
    	if( filterModel.getCode() != null && filterModel.getCode().length()>0 ){
    		params.put("pCode", filterModel.getCode());
    	}
    	
    	
    	if ( (getFilterModel().getTaxReturnType() != null 
    			&& getFilterModel().getTaxReturnType().getDocumentTypesAsArray().length!=0)
        		|| getFilterModel().getDocumentType() != null) {
    					List<Integer> documentTypes = new ArrayList<Integer>();

			        	if(getFilterModel().getTaxReturnType() != null  
			        			&& getFilterModel().getTaxReturnType().getDocumentTypesAsArray().length!=0){
					        		for ( DocumentType docType : getFilterModel().getTaxReturnType().getDocumentTypesAsList() ) {
					        			documentTypes.add( docType.ordinal() );
			        		}
        	}
        	
        	if (getFilterModel().getDocumentType() != null) {
        		documentTypes.add( getFilterModel().getDocumentType().ordinal() );
        	}
        	
			params.put("pDocType", Joiner.on(",").join(documentTypes.toArray()));
        }
    	
    	
//    	if(getFilterModel().getDocumentType() != null 
//        		&& getFilterModel().getTaxReturnType().getDocumentTypesAsArray().length!=0){
//			params.put("pDocType", getFilterModel().getTaxReturnType().getDocumentTypes());
//    	}
//    	
    	if( filterModel.getBeginDate() != null){
    		params.put("pBDate", filterModel.getBeginDate());
    	}
    	
    	if( filterModel.getEndDate() != null){
    		params.put("pEDate", filterModel.getEndDate());
    	}
    	
    	if( filterModel.getTaxType() != null){
    		params.put("pTaxType", filterModel.getTaxType().ordinal());
    	}
    	
    	if( filterModel.getAction() != null){
    		params.put("pAction", filterModel.getAction().ordinal());
    	}
    	
    	if(filterModel.getWorkBunch() != null){ 
    		params.put("pWorkBunchId", filterModel.getWorkBunch().getId());
    	}
    	
    	params.put("pProductLabel", SystemConfiguration.PRODUCTLABEL);
    	
    	try {
			jasperReport.reportToXls("taxTxnReport_excel", "Vergi_Raporu", params);
		} catch (JRException ex) {
			log.error("Vergi raporu çalıştırılırken hata meydana geldi. Sebebi :{0}", ex);
			facesMessages.add("Vergi raporu çalıştırılırken hata meydana geldi. Sebebi :{0}",ex.getMessage());
		}
    	
    }
    
    
    public DocumentType[] getDocumentTypes() {
    	if (documentTypes == null) {
    		documentTypes = new DocumentType[]{DocumentType.PurchaseInvoice,
    										   DocumentType.SaleInvoice,
											   DocumentType.PurchaseShipmentInvoice,
											   DocumentType.SaleShipmentInvoice,
											   DocumentType.RetailSaleShipmentInvoice, 
											   //DocumentType.Payment,
											   //DocumentType.Collection,
											   DocumentType.ExpenseNote,
											   DocumentType.ReturnExpenseNote };
    	}
    	return documentTypes;
    }
    
    /**
     * @return the resultList
     */
    public List<TaxTxn> getResultList() {
        return resultList;
    }

    /**
     * @param resultList the resultList to set
     */
    public void setResultList(List<TaxTxn> resultList) {
        this.resultList = resultList;
    }

    /**
     * @return the filterModel
     */
    public TaxTxnFilterModel getFilterModel() {
        return filterModel;
    }

    /**
     * @param filterModel the filterModel to set
     */
    public void setFilterModel(TaxTxnFilterModel filterModel) {
        this.filterModel = filterModel;
    }

	public int getExid() {
		return exid;
	}

	public void setExid(int exid) {
		this.exid = exid;
	}

}
