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
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import com.ut.tekir.entities.inv.TekirInvoice;
import com.ut.tekir.framework.BrowserBase;

/**
*
* @author rustem
*/
@Stateful
@Name("purchaseSaleReport")
@Scope(ScopeType.SESSION)
public class PurchaseSaleReportBean extends BrowserBase<TekirInvoice, PurchaseSaleReportFilterModel> implements PurchaseSaleReport<TekirInvoice, PurchaseSaleReportFilterModel> {

	@Override
	public PurchaseSaleReportFilterModel newFilterModel() {
		return new PurchaseSaleReportFilterModel();
	}

	
	@SuppressWarnings("unchecked")
	public void pdf() {
        Map params = new HashMap();

        if (isNotEmpty(filterModel.getProductCode())) {
            params.put("pCode", filterModel.getProductCode());
        }

        if (isNotEmpty(filterModel.getProductName())) {
            params.put("pName", filterModel.getProductName());
        }
        
        if(isNotEmpty(filterModel.getBarcode())){
        	params.put("pBarcode", filterModel.getBarcode());
        }
        
        if(isNotEmpty(filterModel.getLabelName())){
        	params.put("pLabelName", filterModel.getLabelName());
        }

        if (filterModel.getBeginDate() != null) {
            params.put("pBDate", filterModel.getBeginDate());
        }

        if (filterModel.getEndDate() != null) {
            params.put("pEDate", filterModel.getEndDate());
        }
        
        if(filterModel.getType()!=null){
        	params.put("pTradeAction", filterModel.getType());
        }

        if(filterModel.getWorkBunch()!=null){
        	params.put("pWorkBunchId", filterModel.getWorkBunch().getId());
        }
        
        execPdf("alis_satis_listesi_excel", "Detayli_Satis_Fatura_Listesi", params);

    }

	
	@SuppressWarnings("unchecked")
	public void xls() {
        Map params = new HashMap();

        if (isNotEmpty(filterModel.getProductCode())) {
            params.put("pCode", filterModel.getProductCode());
        }

        if (isNotEmpty(filterModel.getProductName())) {
            params.put("pName", filterModel.getProductName());
        }
        
        if(isNotEmpty(filterModel.getBarcode())){
        	params.put("pBarcode", filterModel.getBarcode());
        }
        
        if(isNotEmpty(filterModel.getLabelName())){
        	params.put("pLabelName", filterModel.getLabelName());
        }

        if (filterModel.getBeginDate() != null) {
            params.put("pBDate", filterModel.getBeginDate());
        }

        if (filterModel.getEndDate() != null) {
            params.put("pEDate", filterModel.getEndDate());
        }

        if(filterModel.getType()!=null){
        	params.put("pTradeAction", filterModel.getType());
        }

        if(filterModel.getWorkBunch()!=null){
        	params.put("pWorkBunchId", filterModel.getWorkBunch().getId());
        }        
        
        execXls("alis_satis_listesi_excel", "Detayli_Satis_Fatura_Listesi", params);

    }
	
}
