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

package com.ut.tekir.invoice.yeni;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.jboss.seam.Component;
import org.jboss.seam.log.Log;
import org.jboss.seam.log.Logging;
import org.jboss.seam.persistence.HibernateSessionProxy;
import org.hibernate.type.Type;

import com.ut.tekir.entities.Contact;
import com.ut.tekir.entities.ControlType;
import com.ut.tekir.entities.PriceItemDetail;
import com.ut.tekir.entities.Product;
import com.ut.tekir.entities.ProductTxn;
import com.ut.tekir.entities.ProductType;
import com.ut.tekir.entities.TradeAction;
import com.ut.tekir.framework.OptionManager;
import com.ut.tekir.options.LimitationOptionKey;
import com.ut.tekir.options.OptionKey;
import com.ut.tekir.stock.ProductTxnFilterModel;
import com.ut.tekir.stock.ProductTxnReport;

/**
 * @author sinan.yumak, deniz.korkmaz
 *
 */
public class LimitationChecker implements Serializable {
	private static final long serialVersionUID = 1L;
	private Log log = Logging.getLog(LimitationChecker.class);
	private ContactLimitController contactLimitController;
	private OptionManager optionManager;
	private Map<String, String> messages;
	private EntityManager entityManager;
	private ProductTxnReport<ProductTxn, ProductTxnFilterModel> productTxnReport;
	
	private LimitationMessages limMessages;
	private List<Map> warehouseResult;
	private ProductTxnFilterModel filterModel;

	public LimitationChecker() {
		initComponents();
	}
	
	private void initComponents() {
		log.info("Initializing components...");
		productTxnReport = (ProductTxnReport<ProductTxn, ProductTxnFilterModel>)Component.getInstance("productTxnReport");
		messages = (Map<String, String>)Component.getInstance("org.jboss.seam.international.messages", true);
		optionManager = (OptionManager)Component.getInstance("optionManager");
		contactLimitController = ContactLimitController.instance();
	}

	public void saleInvoiceZeroLineAmount(int rowKey, BigDecimal lineAmt) {
		zeroLineAmount(LimitationOptionKey.SALEINVOICE_ZERO_LINEAMOUNT, rowKey, lineAmt);
	}

	public void purchaseInvoiceZeroLineAmount(int rowKey, BigDecimal lineAmt) {
		zeroLineAmount(LimitationOptionKey.PURCHASEINVOICE_ZERO_LINEAMOUNT, rowKey, lineAmt);
	}

	private void zeroLineAmount(OptionKey key, int rowKey, BigDecimal lineAmt) {
		ControlType type = getOption(key);
		if ( !ControlType.NoControl.equals(type) ) {
			if (lineAmt.compareTo(BigDecimal.ZERO) <=0) {
				getLimMessages().add(key, type, rowKey+1 + messages.get("limitationCheck.label." + type + "LineAmountZero") );
			}
		}
	}
	
	public void stockLevel(int rowKey, Product product, Double quantity,String warehouseCode, Long docId) {
		Double remain = 0d;
		String wareCode = "";
		ControlType type = getOption(LimitationOptionKey.STOCK_LEVEL);
		//Uyarı ve Zorunlu olduğu hallerde Stok ise denetlenecek
		if ( !ControlType.NoControl.equals(type) && product.getProductType().equals(ProductType.Product) ) {

			filterModel = new ProductTxnFilterModel();
			filterModel.setProduct(product);
                        filterModel.setDocId(docId);
			getPrices();	
			
			for (Iterator it = warehouseResult.iterator(); it.hasNext();) {
	            Map map = (Map) it.next();
	            if(map.get("warecode").toString().equals(warehouseCode)){
	            	wareCode = warehouseCode;
	            	remain = (Double)map.get("remainStock");
	            }
	            
	        }
			log.debug("Depo kodu : #0", warehouseCode);
			
			if(remain<quantity){
				getLimMessages().add(LimitationOptionKey.STOCK_LEVEL, type, rowKey+1 + messages.get("limitationCheck.label." + type + "StockLevel")
						+ " " + warehouseCode + " " + messages.get("limitationCheck.label.WarehouseLevel") + " " + remain.toString() );
			}
			log.debug(remain);
		}
	}

	public void contactCreditAndRisk(Contact contact, BigDecimal operationAmt) {
		contactCredit(contact, operationAmt);
		contactRisk(contact, operationAmt);
	}
	
	public void contactCredit(Contact contact, BigDecimal operationAmt) {
		ControlType type = getOption(LimitationOptionKey.CONTACT_CREDIT_LIMIT);

		if ( !ControlType.NoControl.equals(type) ) {
			try {
				contactLimitController.checkCreditLimit(contact, operationAmt);
			} catch (Exception e) {
				//TODO: buraya özel bi exception yazmalı.
				getLimMessages().add(LimitationOptionKey.CONTACT_CREDIT_LIMIT, type, e.getMessage());
			}
		}
	}

	public void contactRisk(Contact contact, BigDecimal operationAmt) {
		ControlType type = getOption(LimitationOptionKey.CONTACT_RISK_LIMIT);
		if ( !ControlType.NoControl.equals(type) ) {
			try {
				contactLimitController.checkRiskLimit(contact, operationAmt);
			} catch (Exception e) {
				//TODO: buraya özel bi exception yazmalı.
				getLimMessages().add(LimitationOptionKey.CONTACT_RISK_LIMIT, type, e.getMessage());
			}
		}
	}

	public void accountLevel() {
		//TODO:implement me!
	}

	public void clearMessages() {
		getLimMessages().clear();
	}
	
	public ControlType getOption(OptionKey key) {
		return optionManager.getOption(key, true).getAsEnum(ControlType.class);
	}

	public LimitationMessages getLimMessages() {
		if (limMessages == null) {
			limMessages = new LimitationMessages();
		}
		return limMessages;
	}

	public boolean hasWarningOrRequired() {
		return has(ControlType.Warning) || has(ControlType.Required);
	}

	public boolean hasRequired() {
		return has(ControlType.Required);
	}
	
	public boolean isRequired(){
		for(LimitationMessage lm : getLimMessages().getMessages()){
			if(lm.getControlType().equals(ControlType.Required)){
				return true;
			}
		}
		return false;
	}
	
	public boolean has(ControlType controlType) {
		if (controlType.equals(getOption(LimitationOptionKey.STOCK_LEVEL))) return true;
		if (controlType.equals(getOption(LimitationOptionKey.CONTACT_CREDIT_LIMIT))) return true;
		if (controlType.equals(getOption(LimitationOptionKey.CONTACT_RISK_LIMIT))) return true;
		if (controlType.equals(getOption(LimitationOptionKey.PURCHASEINVOICE_ZERO_LINEAMOUNT))) return true;
		if (controlType.equals(getOption(LimitationOptionKey.SALEINVOICE_ZERO_LINEAMOUNT))) return true;	
		return false;
	}
	
	public static LimitationChecker instance() {
		return new LimitationChecker();
	}

	@SuppressWarnings( "rawtypes" )
	public ProductTxnReport getProductTxnReport() {
		return (ProductTxnReport)Component.getInstance("productTxnReport");
	}

	public DetachedCriteria buildCriteriaForWarehouse() {

        DetachedCriteria crit = DetachedCriteria.forClass(ProductTxn.class);

        crit.createAlias("product", "product");
        crit.createAlias("warehouse", "warehouse");

        ProjectionList pl = Projections.projectionList();
        pl.add(Projections.groupProperty("product.code"), "prodcode").add(Projections.groupProperty("product.name"), "prodname") 
                .add(Projections.groupProperty("product.group"),"group")
                .add(Projections.groupProperty("product.barcode1"),"barcode")
                .add(Projections.groupProperty("warehouse.code"), "warecode").add(Projections.groupProperty("warehouse.name"), "warename")
                .add(Projections.sum("quantity.value"), 
                		"quantity").add(Projections.avg("unitPrice.value"), 
                				"unitPrice").add(Projections.sqlGroupProjection("{alias}.UNIT as unit, " +
                                        "sum( case {alias}.trade_action when 0 then {alias}.QUANTITY else 0 end ) as INQTY, " +
                                        "sum( case {alias}.trade_action when 1 then {alias}.QUANTITY else 0 end ) as OUTQTY , "+
                                        "sum( case {alias}.trade_action when 2 then {alias}.QUANTITY else 0 end ) as BUYRETQTY, "+
                                        "sum( case {alias}.trade_action when 3 then {alias}.QUANTITY else 0 end ) as SELLRETQTY, "+
                                        "sum( case {alias}.trade_action when 6 then {alias}.QUANTITY else 0 end ) as RESQTY , "+
                                        "sum( case {alias}.trade_action when 7 then {alias}.QUANTITY else 0 end ) as DELQTY ",
                						"UNIT", new String[]{"unit", "inqty", "outqty","buyretqty","sellretqty","resqty","delqty"}, new Type[]{Hibernate.STRING, Hibernate.DOUBLE, Hibernate.DOUBLE, Hibernate.DOUBLE, Hibernate.DOUBLE, Hibernate.DOUBLE, Hibernate.DOUBLE}));


        
        crit.setProjection(pl);

        crit.add(Restrictions.eq("active", true));
        
        //Evrağın kendisini toplam değerlere eklemiyoruz.
        if( filterModel.getDocId() != null ){
            crit.add(Restrictions.ne("documentId", filterModel.getDocId()));
        }
        
        if( filterModel.getBarcode() != null && filterModel.getBarcode().length() > 0 ){
        	Criterion criteria1 = Restrictions.eq("product.barcode1", filterModel.getBarcode());
        	Criterion criteria2 = Restrictions.eq("product.barcode2", filterModel.getBarcode());
        	Criterion criteria3 = Restrictions.eq("product.barcode3", filterModel.getBarcode());

        	crit.add(Restrictions.or(criteria1,
        			     Restrictions.or(criteria2,criteria3)));
        }
        
        if(filterModel.getProduct() != null){
        	crit.add(Restrictions.eq("product", filterModel.getProduct()));
        }	
        crit.addOrder(Order.asc("product.name"));

        return crit;
    }
	
	@SuppressWarnings("unchecked")
    public void getPrices(){
    	
    	HibernateSessionProxy session = (HibernateSessionProxy) getEntityManager().getDelegate();

        Criteria ecrit = buildCriteriaForWarehouse().getExecutableCriteria(session);
        ecrit.setCacheable(true);
        ecrit.setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP);
        warehouseResult = ecrit.list();
        calculateSummary();
        
    }
	
	 @SuppressWarnings("unchecked")
		public void calculateSummary() {
	        for (Iterator it = warehouseResult.iterator(); it.hasNext();) {
	            Map map = (Map) it.next();



	            Double in = (Double) map.get("inqty");
	            Double out = (Double) map.get("outqty");
	            Double buyret = (Double) map.get("buyretqty");
	            Double selret = (Double) map.get("sellretqty");
	            Double reservation = (Double) map.get("resqty");
	            Double deliver = (Double) map.get("delqty");

	            Double balance = (in + selret + deliver) - (out + buyret + reservation);
	            Double remainStock = (in + selret) - (out + buyret);
	            map.put("balance", balance);
	            map.put("remainStock", remainStock);

	            log.debug("Row : #0", map);
	        }



	    }

	public EntityManager getEntityManager() {
		if(entityManager==null){
			entityManager = (EntityManager)Component.getInstance("entityManager");
		}
		return entityManager;
	}

	public List<Map> getWarehouseResult() {
		return warehouseResult;
	}

	public void setWarehouseResult(List<Map> warehouseResult) {
		this.warehouseResult = warehouseResult;
	}

	
	
	
	
}
