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

package com.ut.tekir.finance;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import javax.ejb.Stateful;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.Conversation;

import com.ut.tekir.entities.DiscountOrExpense;
import com.ut.tekir.entities.FoundationTxn;
import com.ut.tekir.entities.Product;
import com.ut.tekir.entities.ProductType;
import com.ut.tekir.entities.inv.TekirInvoice;
import com.ut.tekir.entities.inv.TekirInvoiceDetail;
import com.ut.tekir.framework.BaseConsts;
import com.ut.tekir.framework.BrowserBase;
import com.ut.tekir.invoice.yeni.SaleInvoiceHome;

/**
 * Kurum geri ödeme işlemlerinin yapıldığı home sınıfıdır.
 * @author sinan.yumak
 *
 */
@Name("foundationRefundingHome")
@Scope(ScopeType.CONVERSATION)
@Stateful
public class FoundationRefundingHomeBean extends BrowserBase<FoundationTxn, FoundationRefundingFilterModel> implements FoundationRefundingHome<FoundationTxn, FoundationRefundingFilterModel> {

	@Create
	@Begin(join=true, flushMode = org.jboss.seam.annotations.FlushModeType.MANUAL)
	public void init() {
		filterModel = newFilterModel();
	}

    @Override
    public FoundationRefundingFilterModel newFilterModel() {
    	FoundationRefundingFilterModel fm = new FoundationRefundingFilterModel();
        return fm;
    }

    @Override
    public DetachedCriteria buildCriteria() {
        DetachedCriteria crit = DetachedCriteria.forClass(FoundationTxn.class);

        if (filterModel.getTxnBeginDate() != null) {
            crit.add(Restrictions.ge("this.date", filterModel.getTxnBeginDate()));
        }

        if (filterModel.getTxnEndDate() != null) {
        	crit.add(Restrictions.le("this.date", filterModel.getTxnEndDate()));
        }

        if (filterModel.getMaturityBeginDate() != null) {
        	crit.add(Restrictions.ge("this.maturityDate", filterModel.getMaturityBeginDate()));
        }
        
        if (filterModel.getMaturityEndDate() != null) {
        	crit.add(Restrictions.le("this.maturityDate", filterModel.getMaturityEndDate()));
        }

		if (filterModel.getFoundation() != null) {
			crit.add(Restrictions.eq("this.foundation", filterModel.getFoundation()));
		}        

		if (filterModel.getRepaidStatus() != null) {
			crit.add(Restrictions.eq("this.repaidStatus", filterModel.getRepaidStatus()));
		}        

		crit.addOrder(Order.desc("serial"));

        return crit;
    }
    
	@SuppressWarnings("unchecked")
	public String createInvoiceFromSelectedItems() {
		log.info("Creating invoice from selected items...");
		manualFlush();
		String result = BaseConsts.FAIL;
		try {
			
			Map<Product, BigDecimal> resultMap = groupTxnRecordsByReferenceProduct();
	
			SaleInvoiceHome<TekirInvoice> sih = (SaleInvoiceHome<TekirInvoice>)Component.getInstance("saleInvoiceHome",true);
			sih.setActionType(1);
			sih.setInvoiceType(0);
			sih.createNew();
			
			TekirInvoice invoice = sih.getEntity();
			
			TekirInvoiceDetail detail = null;
			for (Product product : resultMap.keySet()) {
	
				detail = new TekirInvoiceDetail();
				detail.setProduct(product);
				detail.setProductType(product.getProductType());
			
				DiscountOrExpense doe = null;
				if (detail.getProductType().equals(ProductType.DiscountAddition)) {

					doe = detail.getDiscount();
					doe.setPercentage(false);
					doe.setValue(resultMap.get(product));
					doe.setLocalAmount(resultMap.get(product));
				} else {
					
					detail.getQuantity().setValue(1d);
					detail.getUnitPrice().setLocalAmount(resultMap.get(product));
					detail.getUnitPrice().setValue(resultMap.get(product));
					detail.getTaxExcludedAmount().setLocalAmount(resultMap.get(product));
					detail.getTaxExcludedAmount().setValue(resultMap.get(product));
				}

				if (detail.getProductType().equals(ProductType.Product) || 
						detail.getProductType().equals(ProductType.Service)) {
					invoice.getItems().add(0,detail);
				} else {
					invoice.getItems().add(detail);
				}
				detail.setOwner(invoice);
			}
			sih.prepareAllProductAndServiceLines();
			
			invoice.setContact(filterModel.getFoundation().getContact());

			sih.setDefaultWarehouse();
			sih.setDefaultAccount();
			
			for (FoundationTxn txn : entityList) {
				if (txn.getActive() && !txn.isRepaidStatus()) {
					sih.getTxnList().add(txn);
				}
			}
			
			result = BaseConsts.SUCCESS;
		} catch (Exception e) {
			log.error("Fatura hazırlanırken hata meydana geldi. Sebebi #0", e);
			facesMessages.add("Fatura hazırlanırken hata meydana geldi. Sebebi :{0}", e.getMessage());
		}
		return result;
	}

	private Map<Product, BigDecimal> groupTxnRecordsByReferenceProduct() {
		Map<Product, BigDecimal> result = new HashMap<Product, BigDecimal>();

		if (getEntityList() != null) {
			
			Product product;
			for (FoundationTxn txn : getEntityList()) {
				if (txn.getActive() && !txn.isRepaidStatus()) {

					product = txn.getProduct();
					if ( result.containsKey(product.getReferenceProduct()) )  {
						
						BigDecimal totalAmount = result.get(product.getReferenceProduct());
						
						totalAmount = totalAmount.add(txn.getAmount().getLocalAmount());
						
						result.put(product.getReferenceProduct(), totalAmount);
					} else {
						result.put(product.getReferenceProduct(), txn.getAmount().getLocalAmount());
					}
						
				}
			}
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public void xls(){
		
		Map params = new HashMap();
		
		if(filterModel.getFoundation()!=null){
			params.put("pFoundationId", filterModel.getFoundation().getId());
		}
		
		if(filterModel.getTxnBeginDate()!=null){
			params.put("pBDate", filterModel.getTxnBeginDate());
		}
		
		if(filterModel.getTxnEndDate()!=null){
			params.put("pEDate", filterModel.getTxnEndDate());
		}
		
		if(filterModel.getMaturityBeginDate()!=null){
			params.put("pMBDate", filterModel.getMaturityBeginDate());
		}
		
		if(filterModel.getMaturityEndDate()!=null){
			params.put("pMEDate", filterModel.getMaturityEndDate());
		}
		
		if (filterModel.getRepaidStatus() != null) {
			params.put("pRepaid", filterModel.getRepaidStatus());
		}
		
		execXls("kurum_odeme_excel","KURUM_ODEME_LISTESI",params);
		
	}
	
	@SuppressWarnings("unchecked")
	public void pdf(){
		
		Map params = new HashMap();

		if(filterModel.getFoundation()!=null){
			params.put("pFoundationId", filterModel.getFoundation().getId());
		}
		
		if(filterModel.getTxnBeginDate()!=null){
			params.put("pBDate", filterModel.getTxnBeginDate());
		}
		
		if(filterModel.getTxnEndDate()!=null){
			params.put("pEDate", filterModel.getTxnEndDate());
		}
		
		if(filterModel.getMaturityBeginDate()!=null){
			params.put("pMBDate", filterModel.getMaturityBeginDate());
		}
		
		if(filterModel.getMaturityEndDate()!=null){
			params.put("pMEDate", filterModel.getMaturityEndDate());
		}
		
		if (filterModel.getRepaidStatus() != null) {
			params.put("pRepaid", filterModel.getRepaidStatus());
		}
		
		execPdf("kurum_odeme_excel","KURUM_ODEME_LISTESI",params);
		
	}
	
	public void manualFlush() {
		Conversation.instance().changeFlushMode(org.jboss.seam.annotations.FlushModeType.MANUAL);
	}

}
