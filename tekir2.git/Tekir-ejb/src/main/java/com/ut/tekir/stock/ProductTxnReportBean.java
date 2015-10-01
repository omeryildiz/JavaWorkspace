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

package com.ut.tekir.stock;

import com.ut.tekir.configuration.SystemConfiguration;
import com.ut.tekir.entities.DocumentType;
import com.ut.tekir.entities.PriceItemDetail;
import com.ut.tekir.entities.Product;
import com.ut.tekir.entities.ProductTxn;
import com.ut.tekir.entities.ProductType;
import com.ut.tekir.entities.TradeAction;
import com.ut.tekir.framework.BrowserBase;
import com.ut.tekir.framework.CalendarManager;
import com.ut.tekir.framework.JasperHandlerBean;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.ejb.Stateful;

import net.sf.jasperreports.engine.JRException;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.Type;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.persistence.HibernateSessionProxy;

/**
 *
 * @author haky
 */
@Stateful
@Name("productTxnReport")
@Scope(ScopeType.SESSION)
public class ProductTxnReportBean extends BrowserBase<ProductTxn, ProductTxnFilterModel> implements ProductTxnReport<ProductTxn, ProductTxnFilterModel> {

	private List<ProductTxn> purchaseEntityList;
	private List<ProductTxn> saleEntityList;
	private List<Product> productList;
	private List<PriceItemDetail> salePriceListResult;
	private List<PriceItemDetail> purchasePriceListResult;
	@SuppressWarnings("unchecked")
	private List<Map> warehouseResult;
    private List<Map<String, Object>> summaryList;
    private Double grandTotal = 0d;
    
    public Integer exid;
    
    @In
    CalendarManager calendarManager;
    @In(create = true)
    JasperHandlerBean jasperReport;

    
    @Override
    public ProductTxnFilterModel newFilterModel() {
        ProductTxnFilterModel fm = new ProductTxnFilterModel();
        fm.setBeginDate(calendarManager.getLastTenDay());
        fm.setEndDate(calendarManager.getCurrentDate());
        return fm;
    }

    @SuppressWarnings("unchecked")
	@Override
    public void search() {
        //super.search();

    	
    	HibernateSessionProxy session = (HibernateSessionProxy) getEntityManager().getDelegate();

        Criteria ecrit = buildCriteria().getExecutableCriteria(session);
        ecrit.setCacheable(true);

        List ls = ecrit.list();
      
        setEntityList(ls);

    }

    @Override
    public DetachedCriteria buildCriteria() {

        DetachedCriteria crit = DetachedCriteria.forClass(ProductTxn.class);

        crit.createAlias("product", "product");
        crit.createAlias("warehouse", "warehouse");
        crit.createAlias("workBunch", "workBunch", Criteria.LEFT_JOIN);
        
        crit.setProjection(Projections.projectionList()
        		.add(Projections.property("documentType"), "documentType" )
        		.add(Projections.property("documentId"), "documentId" )
        		.add(Projections.property("product.id"), "productId" )
        		.add(Projections.property("serial"), "serial")
        		.add(Projections.property("reference"), "reference")
        		.add(Projections.property("code"), "code")
        		.add(Projections.property("date"), "date")
        		.add(Projections.property("warehouse.name"), "wareName")
        		.add(Projections.property("product.name"), "prodName")
        		.add(Projections.property("product.code"), "prodCode")
                .add(Projections.property("product.group"), "group")
                .add(Projections.property("product.barcode1"), "barcode")
        		.add(Projections.property("adverseCode"), "adverseCode")
        		.add(Projections.property("adverseName"), "adverseName")
        		.add(Projections.property("info"), "info")
        		.add(Projections.property("action"), "action")
        		.add(Projections.property("quantity.value"), "quantityValue")
        		.add(Projections.property("quantity.unit"), "quantityUnit")
        		.add(Projections.property("unitPrice.currency"), "unitPriceCurrency")
        		.add(Projections.property("unitPrice.value"), "unitPriceValue")
                .add(Projections.property("financeAction"), "financeAction")
                .add(Projections.property("workBunch.code"), "workBunchCode") );
        
        crit.setResultTransformer(Transformers.aliasToBean(ProductTxnModel.class));

        if (isNotEmpty(filterModel.getSerial())) {
            crit.add(Restrictions.ilike("this.serial", filterModel.getSerial(),MatchMode.START));
        }

        if (isNotEmpty(filterModel.getProductCode())) {
            crit.add(Restrictions.ilike("product.code", filterModel.getProductCode(),MatchMode.START));
        }

        if (isNotEmpty(filterModel.getAdverseCode())) {
            crit.add(Restrictions.ilike("this.adverseCode", filterModel.getAdverseCode(),MatchMode.START));
        }

        if (isNotEmpty(filterModel.getAdverseName())) {
            crit.add(Restrictions.ilike("this.adverseName", filterModel.getAdverseName(),MatchMode.START));
        }

        if (filterModel.getGroup() != null) {
            crit.add(Restrictions.eq("product.group", filterModel.getGroup()));
        }

        if( filterModel.getBarcode() != null && filterModel.getBarcode().length() > 0 ){
        	Criterion criteria1 = Restrictions.like("product.barcode1", filterModel.getBarcode(), MatchMode.START);
        	Criterion criteria2 = Restrictions.like("product.barcode2", filterModel.getBarcode(), MatchMode.START);
        	Criterion criteria3 = Restrictions.like("product.barcode3", filterModel.getBarcode(), MatchMode.START);

        	crit.add(Restrictions.or(criteria1,
        			     Restrictions.or(criteria2,criteria3)));
        }

        if (isNotEmpty(filterModel.getProductName())) {
            crit.add(Restrictions.ilike("product.name", filterModel.getProductName() ,MatchMode.START));
        }
        
        if( filterModel.getProductType() != null ) {

            if (filterModel.getProductType() == 1) {
                crit.add( Restrictions.like("this.productType", ProductType.Product ));
            }else if (filterModel.getProductType() == 2) {
                crit.add( Restrictions.like("this.productType", ProductType.Service ));

            }
        }

        if (isNotEmpty(filterModel.getReference())) {
            crit.add(Restrictions.ilike("this.reference", filterModel.getReference(),MatchMode.START));
        }

        if (isNotEmpty(filterModel.getCode())) {
            crit.add(Restrictions.ilike("this.code", filterModel.getCode(),MatchMode.START));
        }

        if (filterModel.getBeginDate() != null) {
            crit.add(Restrictions.ge("this.date", filterModel.getBeginDate()));
        }

        if (filterModel.getEndDate() != null) {
            crit.add(Restrictions.le("this.date", filterModel.getEndDate()));
        }


        if (filterModel.getContact() != null) {
            crit.add(Restrictions.eq("contact", filterModel.getContact()));
        }

        if (filterModel.getWarehouse() != null) {
            crit.add(Restrictions.eq("warehouse", filterModel.getWarehouse()));
        }

        if (filterModel.getCategory() != null) {
            crit.add(Restrictions.eq("product.category", filterModel.getCategory()));
        }

        if (filterModel.getProduct() != null) {
            crit.add(Restrictions.eq("product", filterModel.getProduct()));
        }
        
        if (filterModel.getWorkBunch() != null){
        	crit.add(Restrictions.eq("workBunch", filterModel.getWorkBunch()));
        }

        if (filterModel.getDocumentType() != null && filterModel.getDocumentType() != DocumentType.Unknown) {
            crit.add(Restrictions.eq("this.documentType", filterModel.getDocumentType()));
        }

        /*
        if( filterModel.getLocalCurrencyOnly()){
        crit.setProjection( Projections.projectionList()
        .add( Projections.groupProperty("documentType"), "documentType" )
        .add( Projections.groupProperty("documentId"), "documentId" )
        .add( Projections.groupProperty("serial"), "serial" )
        .add( Projections.groupProperty("reference"), "reference" )
        .add( Projections.groupProperty("date"), "date" )
        .add( Projections.groupProperty("contact"), "contact" )
        .add( Projections.groupProperty("code"), "code" )
        .add( Projections.groupProperty("info"), "info" )
        .add( Projections.groupProperty("action"), "action" )
        .add( Projections.sum("localAmount.value"), "localAmount" )
        );
        }
         */

        crit.add(Restrictions.eq("this.active", true));
        crit.addOrder(Order.desc("this.date"));
        crit.addOrder(Order.desc("this.id"));
        //crit.addOrder(Order.asc("warehouse"));
        //crit.addOrder(Order.desc("this.serial"));
        //crit.addOrder(Order.asc("product"));

        log.debug("Sonuç : #0", crit);

        return crit;
    }

    @SuppressWarnings("unchecked")
	public void pdf() {
        Map params = new HashMap();

        if (filterModel.getSerial() != null && filterModel.getSerial().length() > 0) {
            params.put("pSeri", filterModel.getSerial());
        }

        if (filterModel.getProductCode() != null && filterModel.getProductCode().length() > 0) {
            params.put("pPCode", filterModel.getProductCode());
        }

        if (filterModel.getProductName() != null && filterModel.getProductCode().length() > 0) {
            params.put("pName", filterModel.getProductName());
        }

        if (filterModel.getAdverseCode() != null && filterModel.getAdverseCode().length() > 0) {
            params.put("pAdvCode", filterModel.getAdverseCode());
        }

        if (filterModel.getAdverseName() != null && filterModel.getAdverseName().length() > 0) {
            params.put("pAdvName", filterModel.getAdverseName());
        }

        if (filterModel.getCategory() != null) {
            params.put("pCate", filterModel.getCategory().getId());
        }

        if (filterModel.getGroup() != null) {
            params.put("pGroup", filterModel.getGroup().getId());
        }

        if (filterModel.getReference() != null && filterModel.getReference().length() > 0) {
            params.put("pRef", filterModel.getReference());
        }

        if (filterModel.getCode() != null && filterModel.getCode().length() > 0) {
            params.put("pCode", filterModel.getCode());
        }

        if (filterModel.getWarehouse() != null) {
            params.put("pWare", filterModel.getWarehouse().getId());
        }
        
        if (filterModel.getDocumentType() != null) {
            params.put("pDocu", filterModel.getDocumentType().ordinal());
        }
        
        if (filterModel.getProductType() != null ){
        	if (filterModel.getProductType() == 0 ) {
            	params.put("pProductType", 0);
            }else if(filterModel.getProductType()  == 1){
            	params.put("pProductType", 1);
            }else if(filterModel.getProductType()  == 2){
            	params.put("pProductType", 2);
            }
        }

        if (filterModel.getBarcode() != null && filterModel.getBarcode().length() > 0) {
            params.put("pBarcode", filterModel.getBarcode());
        }
        
        Calendar c = Calendar.getInstance();
        c.set(1900, 1, 1);

        if (filterModel.getBeginDate() != null) {
            params.put("pBDate", filterModel.getBeginDate());
        }

        c.set(2100, 12, 31);
        if (filterModel.getEndDate() != null) {
            params.put("pEDate", filterModel.getEndDate());
        }
        
        if(filterModel.getWorkBunch() != null){
        	params.put("pWorkBunchId", filterModel.getWorkBunch().getId());
        }

        params.put("pProductLabel", SystemConfiguration.PRODUCTLABEL);
        
        execPdf("productTxnReport", "Stok_Hareket_Raporu", params);

    }
    
    @SuppressWarnings("unchecked")
	public void xls() {
        Map params = new HashMap();

        if (filterModel.getSerial() != null && filterModel.getSerial().length() > 0) {
            params.put("pSeri", filterModel.getSerial());
        }

        if (filterModel.getProductCode() != null && filterModel.getProductCode().length() > 0) {
            params.put("pPCode", filterModel.getProductCode());
        }

        if (filterModel.getProductName() != null && filterModel.getProductCode().length() > 0) {
            params.put("pName", filterModel.getProductName());
        }

        if (filterModel.getAdverseCode() != null && filterModel.getAdverseCode().length() > 0) {
            params.put("pAdvCode", filterModel.getAdverseCode());
        }

        if (filterModel.getAdverseName() != null && filterModel.getAdverseName().length() > 0) {
            params.put("pAdvName", filterModel.getAdverseName());
        }

        if (filterModel.getCategory() != null) {
            params.put("pCate", filterModel.getCategory().getId());
        }

        if (filterModel.getGroup() != null) {
            params.put("pGroup", filterModel.getGroup().getId());
        }

        if (filterModel.getReference() != null && filterModel.getReference().length() > 0) {
            params.put("pRef", filterModel.getReference());
        }

        if (filterModel.getCode() != null && filterModel.getCode().length() > 0) {
            params.put("pCode", filterModel.getCode());
        }

        if (filterModel.getWarehouse() != null) {
            params.put("pWare", filterModel.getWarehouse().getId());
        }
        
        if (filterModel.getDocumentType() != null) {
            params.put("pDocu", filterModel.getDocumentType().ordinal());
        }
        
        if (filterModel.getProductType() != null ){
        	if (filterModel.getProductType() == 0 ) {
            	params.put("pProductType", 0);
            }else if(filterModel.getProductType()  == 1){
            	params.put("pProductType", 1);
            }else if(filterModel.getProductType()  == 2){
            	params.put("pProductType", 2);
            }
        }

        if (filterModel.getBarcode() != null && filterModel.getBarcode().length() > 0) {
            params.put("pBarcode", filterModel.getBarcode());
        }
        
        Calendar c = Calendar.getInstance();
        c.set(1900, 1, 1);

        if (filterModel.getBeginDate() != null) {
            params.put("pBDate", filterModel.getBeginDate());
        }

        c.set(2100, 12, 31);
        if (filterModel.getEndDate() != null) {
            params.put("pEDate", filterModel.getEndDate());
        }
        
        if(filterModel.getWorkBunch() != null){
        	params.put("pWorkBunchId", filterModel.getWorkBunch().getId());
        }

        params.put("pProductLabel", SystemConfiguration.PRODUCTLABEL);
        
        try {
            
            jasperReport.reportToXls("productTxnReport_excel", "Stok_Hareket_Raporu", params);
            
        } catch (JRException ex) {
            log.error("Rapor Hatası", ex);
            facesMessages.add("Rapor Çalıştırılamadı");
        }
        

    }    
    
    
    @SuppressWarnings("unchecked")
    public void getPrices(){
    	
    	if(this.filterModel.isEmpty()){
    		facesMessages.add("Lütfen en az bir alanı doldurunuz.");
    		return;
    	}
    	
    	HibernateSessionProxy session = (HibernateSessionProxy) getEntityManager().getDelegate();

        Criteria ecrit = buildCriteriaForPrice(TradeAction.Purchase).getExecutableCriteria(session);
        //ecrit.setCacheable(true);

        List ls = ecrit.list();
        setPurchaseEntityList(ls);
        
        
        setProductList(null);
        ecrit = buildCriteriaForProduct().getExecutableCriteria(session);
        ls = ecrit.list();
        setProductList(ls);
        
//        
//        setEntityList(null);
//        if (ls.size()>0){
//        	setEntityList(ls);
//        }
//      

        ecrit = buildCriteriaForPrice(TradeAction.Sale).getExecutableCriteria(session);
        ecrit.setCacheable(true);

        ls = ecrit.list();
      
//        if (ls.size()>0){
//        	setEntityList(ls);
//        }
//        
        setSaleEntityList(ls);

        ecrit = buildCriteriaForWarehouse().getExecutableCriteria(session);
        ecrit.setCacheable(true);
        ecrit.setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP);
        warehouseResult = ecrit.list();
        calculateSummary();
        
        ecrit = buildCriteriaForPriceList(TradeAction.Sale).getExecutableCriteria(session);
        ls = ecrit.list();
        setSalePriceListResult(ls);
        
        ecrit = buildCriteriaForPriceList(TradeAction.Purchase).getExecutableCriteria(session);
        ls = ecrit.list();
        setPurchasePriceListResult(ls);

    }

    public DetachedCriteria buildCriteriaForPrice(TradeAction tradeAction) {

        DetachedCriteria crit = DetachedCriteria.forClass(ProductTxn.class);

        crit.createAlias("product", "product");
        crit.createAlias("warehouse", "warehouse");
        
        crit.setProjection(Projections.projectionList()	
        		.add(Projections.property("documentType"), "documentType" )
        		.add(Projections.property("documentId"), "documentId" )
        		.add(Projections.property("code"), "code")
        		.add(Projections.property("action"), "action")
        		.add(Projections.property("warehouse.name"), "wareName")
        		.add(Projections.property("product.id"), "prodId")
        		.add(Projections.property("product.name"), "prodName")
        		.add(Projections.property("product.code"), "prodCode")
                .add(Projections.property("product.barcode1"), "barcode")
                .add(Projections.property("product.barcode2"), "barcode2")
                .add(Projections.property("product.barcode3"), "barcode3")
                .add(Projections.property("product.buyTax"), "buyTax1")
                .add(Projections.property("product.buyTax2"), "buyTax2")
                .add(Projections.property("product.buyTax3"), "buyTax3")
                .add(Projections.property("product.buyTax4"), "buyTax4")
                .add(Projections.property("product.buyTax5"), "buyTax5")
                .add(Projections.property("product.sellTax"), "sellTax1")
                .add(Projections.property("product.sellTax2"), "sellTax2")
                .add(Projections.property("product.sellTax3"), "sellTax3")
                .add(Projections.property("product.sellTax4"), "sellTax4")
                .add(Projections.property("product.sellTax5"), "sellTax5")
                .add(Projections.property("product.labelName"), "labelName")
        		.add(Projections.property("quantity.value"), "quantityValue")
        		.add(Projections.property("quantity.unit"), "quantityUnit")
        		.add(Projections.property("unitPrice.currency"), "unitPriceCurrency")
        		.add(Projections.property("unitPrice.value"), "unitPriceValue")
                .add(Projections.property("financeAction"), "financeAction")
                .add(Projections.property("workBunch"), "workBunch") );

        
        crit.setResultTransformer(Transformers.aliasToBean(ProductTxnModel.class));



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
        
        crit.add(Restrictions.eq("this.action", tradeAction));
        crit.add(Restrictions.eq("this.active", true));
        crit.addOrder(Order.desc("this.date"));
        crit.addOrder(Order.desc("this.id"));
        return crit;
    }
    
    
    public DetachedCriteria buildCriteriaForProduct() {

        DetachedCriteria crit = DetachedCriteria.forClass(Product.class);
        
        crit.setProjection(Projections.projectionList()	
        		.add(Projections.property("id"), "prodId")
        		.add(Projections.property("name"), "prodName")
        		.add(Projections.property("code"), "prodCode")
                .add(Projections.property("barcode1"), "barcode")
                .add(Projections.property("barcode2"), "barcode2")
                .add(Projections.property("barcode3"), "barcode3")
                .add(Projections.property("buyTax"), "buyTax1")
                .add(Projections.property("buyTax2"), "buyTax2")
                .add(Projections.property("buyTax3"), "buyTax3")
                .add(Projections.property("buyTax4"), "buyTax4")
                .add(Projections.property("buyTax5"), "buyTax5")
                .add(Projections.property("sellTax"), "sellTax1")
                .add(Projections.property("sellTax2"), "sellTax2")
                .add(Projections.property("sellTax3"), "sellTax3")
                .add(Projections.property("sellTax4"), "sellTax4")
                .add(Projections.property("sellTax5"), "sellTax5")
                .add(Projections.property("unit"), "quantityUnit")
                .add(Projections.property("labelName"), "labelName") );

        
        crit.setResultTransformer(Transformers.aliasToBean(ProductTxnModel.class));
        
        if( filterModel.getBarcode() != null && filterModel.getBarcode().length() > 0 ){
        	Criterion criteria1 = Restrictions.eq("this.barcode1", filterModel.getBarcode());
        	Criterion criteria2 = Restrictions.eq("this.barcode2", filterModel.getBarcode());
        	Criterion criteria3 = Restrictions.eq("this.barcode3", filterModel.getBarcode());

        	crit.add(Restrictions.or(criteria1,
        			     Restrictions.or(criteria2,criteria3)));
        }
        
        if(filterModel.getProduct() != null){
        	crit.add(Restrictions.eq("this.id", filterModel.getProduct().getId()));
        }
        crit.add(Restrictions.eq("this.active", true));
        crit.addOrder(Order.desc("this.id"));
        return crit;
    }
    
    public DetachedCriteria buildCriteriaForPriceList(TradeAction tradeAction) {

        DetachedCriteria crit = DetachedCriteria.forClass(PriceItemDetail.class);

        crit.createAlias("this.owner", "owner");
        crit.createAlias("product", "product");
//        crit.createAlias("owner.owner", "ownerPriceList",DetachedCriteria.LEFT_JOIN);

        crit.setProjection(Projections.projectionList()	
        		.add(Projections.property("product"), "product" )
        		.add(Projections.property("netPrice.value"), "netPriceValue" )
        		.add(Projections.property("netPrice.currency"), "netPriceCurrency" )
        		.add(Projections.property("grossPrice.value"), "grossPriceValue" )
        		.add(Projections.property("grossPrice.currency"), "grossPriceCurrency" )
        		.add(Projections.property("owner.code"), "ownerCode")
        		.add(Projections.property("owner.beginDate"), "ownerBeginDate")
        		.add(Projections.property("owner.endDate"), "ownerEndDate")
        		.add(Projections.property("owner.active"), "ownerIsActive")
        		.add(Projections.property("owner.info"), "priceListName") 
//        		FIXME: listeden ad mı gelmeli yoksa info mu? 
//        		.add(Projections.property("ownerPriceList.listName"), "priceListName")
        );
        
        crit.setResultTransformer(Transformers.aliasToBean(ProductTxnModel.class));

        crit.add(Restrictions.eq("owner.action", tradeAction));
        
        
        if( filterModel.getBarcode() != null && filterModel.getBarcode().length() > 0 ){
        	Criterion criteria1 = Restrictions.eq("product.barcode1", filterModel.getBarcode());
        	Criterion criteria2 = Restrictions.eq("product.barcode2", filterModel.getBarcode());
        	Criterion criteria3 = Restrictions.eq("product.barcode3", filterModel.getBarcode());

        	crit.add(Restrictions.or(criteria1,
        			     Restrictions.or(criteria2,criteria3)));
        }
        
        if(filterModel.getProduct() != null){
        	crit.add(Restrictions.eq("this.product", filterModel.getProduct()));
        }	
        crit.addOrder(Order.desc("this.id"));
        return crit;
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
	public void calculateSummary() {

        setSummaryList(new ArrayList<Map<String, Object>>());
        setGrandTotal((Double) 0d);


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

    
    
	public List<ProductTxn> getPurchaseEntityList() {
		return purchaseEntityList;
	}

	public void setPurchaseEntityList(List<ProductTxn> purchaseEntityList) {
		this.purchaseEntityList = purchaseEntityList;
	}

	public List<ProductTxn> getSaleEntityList() {
		return saleEntityList;
	}

	public void setSaleEntityList(List<ProductTxn> saleEntityList) {
		this.saleEntityList = saleEntityList;
	}

	

	public List<Map> getWarehouseResult() {
		return warehouseResult;
	}

	public void setWarehouseResult(List<Map> warehouseResult) {
		this.warehouseResult = warehouseResult;
	}

	public List<Map<String, Object>> getSummaryList() {
		return summaryList;
	}

	public void setSummaryList(List<Map<String, Object>> summaryList) {
		this.summaryList = summaryList;
	}

	public Double getGrandTotal() {
		return grandTotal;
	}

	public void setGrandTotal(Double grandTotal) {
		this.grandTotal = grandTotal;
	}

	public List<Product> getProductList() {
		return productList;
	}

	public void setProductList(List<Product> productList) {
		this.productList = productList;
	}

	public List<PriceItemDetail> getSalePriceListResult() {
		return salePriceListResult;
	}

	public void setSalePriceListResult(List<PriceItemDetail> salePriceListResult) {
		this.salePriceListResult = salePriceListResult;
	}

	public List<PriceItemDetail> getPurchasePriceListResult() {
		return purchasePriceListResult;
	}

	public void setPurchasePriceListResult(
			List<PriceItemDetail> purchasePriceListResult) {
		this.purchasePriceListResult = purchasePriceListResult;
	}

	public Integer getExid() {
		return exid;
	}


	public void setExid(Integer exid) {
		this.exid = exid;
	}



}
