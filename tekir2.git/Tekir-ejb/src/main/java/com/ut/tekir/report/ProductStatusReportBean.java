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
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

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
import org.hibernate.type.Type;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.log.Log;
import org.jboss.seam.persistence.HibernateSessionProxy;

import com.ut.tekir.configuration.SystemConfiguration;
import com.ut.tekir.entities.DocumentType;
import com.ut.tekir.entities.ProductCategory;
import com.ut.tekir.entities.ProductGroup;
import com.ut.tekir.entities.ProductTxn;
import com.ut.tekir.entities.ProductType;
import com.ut.tekir.entities.Warehouse;
import com.ut.tekir.entities.WorkBunch;
import com.ut.tekir.framework.CalendarManager;
import com.ut.tekir.framework.JasperHandlerBean;

/**
 *
 * @author haky
 */
@Name("productStatusReport")
@Scope(ScopeType.SESSION)
public class ProductStatusReportBean {

    @Logger
    protected Log log;
    @In
    private EntityManager entityManager;
    @In
    protected FacesMessages facesMessages;
    @In
    CalendarManager calendarManager;
    @In(create = true)
    JasperHandlerBean jasperReport;
    private String code;
    private String name;
    private ProductCategory category;
    private Warehouse warehouse;
    private Date beginDate;
    private Date endDate;
    private Boolean warehouseBase;//Depo kırılımı olup olmayacağı bilgisini tutar.
	private WorkBunch workBunch;
    
    @SuppressWarnings("unchecked")
	private List<Map> reportResult;
    private List<Map<String, Object>> summaryList;
    private Double grandTotal = 0d;
    private Integer productType;
    private ProductGroup group;
    private String barcode;
    private String docCode;
    private DocumentType documentType;
    
    @Create
	public void initReport() {
		beginDate = calendarManager.getFirstDayOfMonth();
		endDate = calendarManager.getCurrentDate();
	}
    
    public Integer exid;
    
    
    
    @SuppressWarnings("unchecked")
    public void detailPdf(){

        Map params = new HashMap();

        if (code != null && code.length() > 0) {
            params.put("pScode", code);
        }

        if (name != null && name.length() > 0) {
            params.put("pSname", name);
        }


        Calendar c = Calendar.getInstance();
        c.set(1900, 1, 1);
        if (beginDate != null) {
            params.put("pBDate", beginDate);
        }

        c.set(2100, 12, 31);
        if (endDate != null) {
            params.put("pEDate", endDate);
        }

        if (category != null) {
            params.put("pScate", category.getId());
        }

        if (getProductType() != null ){
        	if (getProductType() == 0 ) {
            	params.put("pProductType", 0);
            }else if(getProductType()  == 1){
            	params.put("pProductType", 1);
            }else if(getProductType()  == 2){
            	params.put("pProductType", 2);
            }
        }

        if (warehouseBase) {
            if (warehouse != null) {
                params.put("pWare", warehouse.getId());
            }
        }

        if (barcode != null && barcode.length() > 0) {
                    params.put("pSbarcode", barcode);
                }

        if (group != null) {
            params.put("pSmark", group.getId());
        }
        
		if (docCode != null && docCode.length() > 0) {
			params.put("pDocCode", docCode);
		}
        
        if (getDocumentType() != null) {
            params.put("pDocu", getDocumentType().ordinal());
        }
        
        if (getWorkBunch() != null) {
            params.put("pWorkBunchId", getWorkBunch().getId());
        }
        
        params.put("pProductLabel", SystemConfiguration.PRODUCTLABEL);
        
        try {
            if (warehouseBase) {
                jasperReport.reportToPDF("depostok_durum", "Depo_Stok_Durum_Raporu", params);
            } else {
                jasperReport.reportToPDF("depostok_durum", "Depo_Stok_Durum_Raporu", params);
                //jasperReport.reportToPDF("stok_durum", "Stok_Durum_Raporu", params);
            }
        } catch (JRException ex) {
            log.error("Rapor Hatası", ex);
            facesMessages.add("Rapor Çalıştırılamadı");
        }



    }
    

    @SuppressWarnings("unchecked")
    public void xls(){

        Map params = new HashMap();

        if (code != null && code.length() > 0) {
            params.put("pScode", code);
        }

        if (name != null && name.length() > 0) {
            params.put("pSname", name);
        }


        Calendar c = Calendar.getInstance();
        c.set(1900, 1, 1);
        if (beginDate != null) {
            params.put("pBDate", beginDate);
        }

        c.set(2100, 12, 31);
        if (endDate != null) {
            params.put("pEDate", endDate);
        }

        if (category != null) {
            params.put("pScate", category.getId());
        }

        if (getProductType() != null ){
        	if (getProductType() == 0 ) {
            	params.put("pProductType", 0);
            }else if(getProductType()  == 1){
            	params.put("pProductType", 1);
            }else if(getProductType()  == 2){
            	params.put("pProductType", 2);
            }
        }

        if (warehouseBase) {
            if (warehouse != null) {
                params.put("pWare", warehouse.getId());
            }
        }

        if (barcode != null && barcode.length() > 0) {
                    params.put("pSbarcode", barcode);
                }

        if (group != null) {
            params.put("pSmark", group.getId());
        }
		if (docCode != null && docCode.length() > 0) {
			params.put("pDocCode", docCode);
		}
        
        if (getDocumentType() != null) {
            params.put("pDocu", getDocumentType().ordinal());
        }
        
        if (getWorkBunch() != null) {
            params.put("pWorkBunchId", getWorkBunch().getId());
        }
        
        params.put("pProductLabel", SystemConfiguration.PRODUCTLABEL);
        
        try {
            if (warehouseBase) {
                jasperReport.reportToXls("depostok_durum_excel", "Depo_Stok_Durum_Raporu", params);
            } else {
                jasperReport.reportToXls("depostok_durum_excel", "Depo_Stok_Durum_Raporu", params);
                //jasperReport.reportToXls("stok_durum_excel", "Stok_Durum_Raporu", params);
            }
        } catch (JRException ex) {
            log.error("Rapor Hatası", ex);
            facesMessages.add("Rapor Çalıştırılamadı");
        }



    }
    

    @SuppressWarnings("unchecked")
	public void pdf() {
        Map params = new HashMap();

        if (code != null && code.length() > 0) {
            params.put("pScode", code);
        }

        if (name != null && name.length() > 0) {
            params.put("pSname", name);
        }


        Calendar c = Calendar.getInstance();
        c.set(1900, 1, 1);

        if (beginDate != null) {
            params.put("pBDate", beginDate);
        }

        c.set(2100, 12, 31);
        if (endDate != null) {
            params.put("pEDate", endDate);
        }

        if (category != null) {
            params.put("pScate", category.getId());
        }
        
        if (getProductType() != null ){
        	if (getProductType() == 0 ) {
            	params.put("pProductType", 0);
            }else if(getProductType()  == 1){
            	params.put("pProductType", 1);
            }else if(getProductType()  == 2){
            	params.put("pProductType", 2);
            }
        }

        if (warehouseBase) {
            if (warehouse != null) {
                params.put("pWare", warehouse.getId());
            }
        }

        if (barcode != null && barcode.length() > 0) {
                    params.put("pSbarcode", barcode);
                }

        if (group != null) {
            params.put("pSmark", group.getId());
        }
		if (docCode != null && docCode.length() > 0) {
			params.put("pDocCode", docCode);
		}
        
        if (getDocumentType() != null) {
            params.put("pDocu", getDocumentType().ordinal());
        }
        
        if (getWorkBunch() != null) {
            params.put("pWorkBunchId", getWorkBunch().getId());
        }

        try {
            if (warehouseBase) {
                jasperReport.reportToPDF("depostok_durum", "Depo_Stok_Durum_Raporu", params);
            } else {
                jasperReport.reportToPDF("stok_durum", "Stok_Durum_Raporu", params);
            }
        } catch (JRException ex) {
            log.error("Rapor Hatası", ex);
            facesMessages.add("Rapor Çalıştırılamadı");
        }


    }

    @SuppressWarnings("unchecked")
	public void executeReport() {
    	HibernateSessionProxy session = (HibernateSessionProxy) getEntityManager().getDelegate();

        Criteria ecrit = buildCriteria().getExecutableCriteria(session);
        //ecrit.setCacheable(true);
        //ecrit.setMaxResults( 100 );
        ecrit.setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP);
        reportResult = ecrit.list();
        calculateSummary();
    }

    @SuppressWarnings("unchecked")
	public void calculateSummary() {

        setSummaryList(new ArrayList<Map<String, Object>>());
        setGrandTotal((Double) 0d);


        for (Iterator it = reportResult.iterator(); it.hasNext();) {
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

    public DetachedCriteria buildCriteria() {

        DetachedCriteria crit = DetachedCriteria.forClass(ProductTxn.class);

        crit.createAlias("product", "product");
        crit.createAlias("warehouse", "warehouse");

        //TODO: Unit Price'ın farklı dövizlerde olma sorunu çözüle...
        ProjectionList pl = Projections.projectionList();
        pl.add(Projections.groupProperty("product.code"), "prodcode").add(Projections.groupProperty("product.name"), "prodname") //.add( Projections.groupProperty("action"), "action" )
                //.add( Projections.groupProperty("amount.currency"), "currency" )
                .add(Projections.groupProperty("product.group"),"group")
                .add(Projections.groupProperty("product.barcode1"),"barcode")
                .add(Projections.sum("quantity.value"), 
                		"quantity").add(Projections.avg("unitPrice.value"), 
                				"unitPrice").add(Projections.sqlGroupProjection("{alias}.UNIT as unit, " +
                						//"sum( case {alias}.finance_action when 0 then {alias}.QUANTITY else 0 end ) as INQTY, " +
                						//"sum( case {alias}.finance_action when 0 then 0 else {alias}.QUANTITY end ) as OUTQTY",
                                        "sum( case {alias}.trade_action when 0 then {alias}.QUANTITY else 0 end ) as INQTY, " +
                                        "sum( case {alias}.trade_action when 1 then {alias}.QUANTITY else 0 end ) as OUTQTY , "+
                                        "sum( case {alias}.trade_action when 2 then {alias}.QUANTITY else 0 end ) as BUYRETQTY, "+
                                        "sum( case {alias}.trade_action when 3 then {alias}.QUANTITY else 0 end ) as SELLRETQTY, "+
                                        "sum( case {alias}.trade_action when 6 then {alias}.QUANTITY else 0 end ) as RESQTY , "+
                                        "sum( case {alias}.trade_action when 7 then {alias}.QUANTITY else 0 end ) as DELQTY ",
                						"UNIT", new String[]{"unit", "inqty", "outqty","buyretqty","sellretqty","resqty","delqty"}, new Type[]{Hibernate.STRING, Hibernate.DOUBLE, Hibernate.DOUBLE, Hibernate.DOUBLE, Hibernate.DOUBLE, Hibernate.DOUBLE, Hibernate.DOUBLE}));


        if (warehouseBase) {
            //Depo krlml
            pl.add(Projections.groupProperty("warehouse.code"), "warecode").add(Projections.groupProperty("warehouse.name"), "warename");
            crit.addOrder(Order.asc("warehouse.code"));
        }

        crit.setProjection(pl);

        crit.add(Restrictions.eq("active", true));

        if (code != null && code.length() > 0) {
            crit.add(Restrictions.ilike("product.code", code ,MatchMode.START));
        }

        if (name != null && name.length() > 0) {
            crit.add(Restrictions.ilike("product.name", name ,MatchMode.START));
        }

        if (productType != null) {

            if (getProductType() == 1) {
                crit.add( Restrictions.like("this.productType", ProductType.Product ));
            }else if (getProductType() == 2) {
                crit.add( Restrictions.like("this.productType", ProductType.Service ));

            }
        }

        if( barcode != null && barcode.length() > 0 ){
        	Criterion criteria1 = Restrictions.like("product.barcode1", barcode, MatchMode.START);
        	Criterion criteria2 = Restrictions.like("product.barcode2", barcode, MatchMode.START);
        	Criterion criteria3 = Restrictions.like("product.barcode3", barcode, MatchMode.START);

        	crit.add(Restrictions.or(criteria1,
        			     Restrictions.or(criteria2,criteria3)));
        }

        if(group != null){
            crit.add(Restrictions.eq("product.group", group));
        }

        if (warehouse != null) {
            crit.add(Restrictions.eq("warehouse", warehouse));
        }

        if (beginDate != null) {
            crit.add(Restrictions.ge("date", beginDate));
        }

        if (endDate != null) {
            crit.add(Restrictions.le("date", endDate));
        }
        
        if(category != null){
        	crit.add(Restrictions.eq("product.category", category));
        }
        if (docCode != null && docCode.length() > 0) {
            crit.add(Restrictions.ilike("code", docCode,MatchMode.START ));
        }
        
        if (getDocumentType() != null && getDocumentType() != DocumentType.Unknown) {
            crit.add(Restrictions.eq("this.documentType", getDocumentType()));
        }
        
        if (getWorkBunch() != null) {
            crit.add(Restrictions.eq("this.workBunch", getWorkBunch()));
        }
        
        /*
        crit.addOrder( Order.asc("date"));
        crit.addOrder( Order.asc("serial"));
         * 
         */

        crit.addOrder(Order.asc("product.name"));


        log.debug("Sonuç : #0", crit);

        return crit;
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @SuppressWarnings("unchecked")
	public List<Map> getReportResult() {
        return reportResult;
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

    public boolean renderColumn(int row, String col) {
        if (reportResult == null) {
            return false;
        }
        if (row == 0) {
            return true;
        }
        String curval = (String) reportResult.get(row).get(col);
        String oldval = (String) reportResult.get(row - 1).get(col);
        if (curval == null || oldval == null) {
            return false;
        }
        return !curval.equals(oldval);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public ProductCategory getCategory() {
        return category;
    }

    public void setCategory(ProductCategory category) {
        this.category = category;
    }

    public Warehouse getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(Warehouse warehouse) {
        this.warehouse = warehouse;
    }

    public Boolean getWarehouseBase() {
        return warehouseBase;
    }

    public void setWarehouseBase(Boolean warehouseBase) {
        this.warehouseBase = warehouseBase;
    }


    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public ProductGroup getGroup() {
        return group;
    }

    public void setGroup(ProductGroup group) {
        this.group = group;
    }


	public Integer getExid() {
		return exid;
	}


	public void setExid(Integer exid) {
		this.exid = exid;
	}


	public String getDocCode() {
		return docCode;
	}


	public void setDocCode(String docCode) {
		this.docCode = docCode;
	}


	public DocumentType getDocumentType() {
		return documentType;
	}


	public void setDocumentType(DocumentType documentType) {
		this.documentType = documentType;
	}


	public Integer getProductType() {
		return productType;
	}


	public void setProductType(Integer productType) {
		this.productType = productType;
	}


	public WorkBunch getWorkBunch() {
		return workBunch;
	}


	public void setWorkBunch(WorkBunch workBunch) {
		this.workBunch = workBunch;
	}
    
    
}
