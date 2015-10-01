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

import java.util.HashMap;
import java.util.Map;

import javax.ejb.Stateful;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.LogicalExpression;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Scope;

import com.ut.tekir.entities.Product;
import com.ut.tekir.entities.ProductType;
import com.ut.tekir.framework.BrowserBase;

/**
 *
 * @author haky
 */
@Stateful
@Name("productBrowse")
@Scope(value=ScopeType.SESSION)
public class ProductBrowseBean extends BrowserBase<Product, ProductFilterModel> implements ProductBrowse<Product, ProductFilterModel>{

	private boolean bo;
	public Integer exid;
	
    @Override
    public ProductFilterModel newFilterModel() {
        return new ProductFilterModel();
    }
        
    @Override
    public DetachedCriteria buildCriteria(){
        DetachedCriteria crit = DetachedCriteria.forClass( Product.class );
        
        crit.setProjection(Projections.projectionList()
        		.add(Projections.property("id"), "id")
        		.add(Projections.property("code"), "code")
        		.add(Projections.property("name"), "name")
        		.add(Projections.property("category"), "category")
        		.add(Projections.property("info"), "info")
        		.add(Projections.property("productType"), "productType")
        		.add(Projections.property("barcode1"), "barcode1")
        		.add(Projections.property("barcode2"), "barcode2")
        		.add(Projections.property("barcode3"), "barcode3")
        		.add(Projections.property("unit"), "unit")
                .add(Projections.property("group"), "group")
                .add(Projections.property("openDate"),"openDate")
                .add(Projections.property("category"), "category")
                .add(Projections.property("system"), "system")
                .add(Projections.property("active"), "active")
                .add(Projections.property("buyTax"), "buyTax")
                .add(Projections.property("sellTax"), "sellTax")
                .add(Projections.property("buyTax2"), "buyTax2")
                .add(Projections.property("sellTax2"), "sellTax2")
                .add(Projections.property("buyTax3"), "buyTax3")
                .add(Projections.property("sellTax3"), "sellTax3")
                .add(Projections.property("buyTax4"), "buyTax4")
                .add(Projections.property("sellTax4"), "sellTax4")
                .add(Projections.property("buyTax5"), "buyTax5")
                .add(Projections.property("sellTax5"), "sellTax5")
                .add(Projections.property("expenseType"), "expenseType")
                .add(Projections.property("taxIncluded"), "taxIncluded")
                .add(Projections.property("tax2Included"), "tax2Included")
                .add(Projections.property("tax3Included"), "tax3Included")
                .add(Projections.property("tax4Included"), "tax4Included")
                .add(Projections.property("tax5Included"), "tax5Included")
                .add(Projections.property("shelfPlace"), "shelfPlace")
                .add(Projections.property("labelName"), "labelName")
                .add(Projections.property("lastPurchasePrice.value"), "lastPurchasePriceValue")
                .add(Projections.property("lastSalePrice.value"), "lastSalePriceValue")
                .add(Projections.property("createUser"), "createUser")
                .add(Projections.property("createDate"), "createDate")
                .add(Projections.property("updateUser"), "updateUser")
                .add(Projections.property("updateDate"), "updateDate")

                );
        
        crit.setResultTransformer(Transformers.aliasToBean(Product.class));
        		
         
        if( filterModel.getCode() != null && filterModel.getCode().length() > 0 ){
        	crit.add( Restrictions.or(Restrictions.ilike("this.code", filterModel.getCode() ,MatchMode.START ), 
        							  Restrictions.like("this.code", filterModel.getCode() ,MatchMode.START )) );
        }
        
        if( filterModel.getName() != null && filterModel.getName().length() > 0 ){
            crit.add( Restrictions.ilike("this.name", filterModel.getName()  ,MatchMode.START ));
        }
        
        if( filterModel.getBarcode() != null && filterModel.getBarcode().length() > 0 ){
        	Criterion criteria1 = Restrictions.like("this.barcode1", filterModel.getBarcode(), MatchMode.START);
        	Criterion criteria2 = Restrictions.like("this.barcode2", filterModel.getBarcode(), MatchMode.START);
        	Criterion criteria3 = Restrictions.like("this.barcode3", filterModel.getBarcode(), MatchMode.START);
            
        	crit.add(Restrictions.or(criteria1,
        			     Restrictions.or(criteria2,criteria3)));
        }
        
        if( filterModel.getProductType() != ProductType.Unknown ){
        	crit.add( Restrictions.eq("this.productType", filterModel.getProductType() ));
        } else
            { Criterion prod = Restrictions.eq("this.productType", ProductType.Product );
              Criterion serv = Restrictions.eq("this.productType", ProductType.Service );
              LogicalExpression orExp = Restrictions.or(prod,serv);
              crit.add(orExp);
            }


        if( filterModel.getGroup() != null ){
            crit.add( Restrictions.eq("this.group", filterModel.getGroup()));
        }

        if( filterModel.getCategory() != null ){
            crit.add( Restrictions.eq("this.category", filterModel.getCategory() ));
        }
        crit.addOrder(Order.asc("this.code"));
        
        return crit;
    }
    
    @Observer("refreshBrowser:com.ut.tekir.entities.Product")
    public void refreshResults(){
        log.debug("Uyarı geldi resultSet tazeleniyor");
        if ( getEntityList() == null || getEntityList().isEmpty() ) return;
        search();
    }
    
    
    @SuppressWarnings("unchecked")
	public void pdf(){
        Map params = new HashMap();
        
       
        if( filterModel.getCode() != null && filterModel.getCode().length() > 0 ){
            params.put("pCode", filterModel.getCode() + "%" );
        }
        
        if( filterModel.getName() != null && filterModel.getName().length() > 0 ){
            params.put("pName", filterModel.getName() + "%" );
        }
        
        if( filterModel.getBarcode() != null && filterModel.getBarcode().length() > 0 ){
            params.put("pBarcode", filterModel.getBarcode() );
        }
        if (filterModel.getProductType() != null) {
            if (filterModel.getProductType().equals(ProductType.Product)) {
                params.put("pProduct_Type", filterModel.getProductType().Product.ordinal());
            } else if (filterModel.getProductType().equals(ProductType.Service)) {
                params.put("pProduct_Type", filterModel.getProductType().Service.ordinal());
            }
        }

        if( filterModel.getCategory() != null ){
            params.put("pCate", filterModel.getCategory().getId() );
        }
        
        if( filterModel.getGroup() != null ){
            params.put("pGroupId", filterModel.getGroup().getId() );
        }

        if (filterModel.getProductType().equals(ProductType.Product)) {
                execPdf("stok_listesi", "Stok_Listesi", params);
            } else if (filterModel.getProductType().equals(ProductType.Service)) {
                execPdf("stok_listesi", "Hizmet_Listesi", params);
            } else execPdf("stok_listesi", "Stok_Hizmet_Listesi", params);
        
    }

    @SuppressWarnings("unchecked")
	public void detailedPdf() {
        Map params = new HashMap();

        if( filterModel.getCode() != null && filterModel.getCode().length() > 0 ){
            params.put("pCode", filterModel.getCode() + "%" );
        }
        
        if( filterModel.getName() != null && filterModel.getName().length() > 0 ){
            params.put("pName", filterModel.getName() + "%" );
        }
        
        if( filterModel.getBarcode() != null && filterModel.getBarcode().length() > 0 ){
            params.put("pBarcode", filterModel.getBarcode() );
        }
       
        if( filterModel.getCategory() != null ){
            params.put("pCate", filterModel.getCategory().getId() );
        }
        
        if( filterModel.getGroup() != null ){
            params.put("pGroupId", filterModel.getGroup().getId() );
        }

        execPdf("stok_detay", "Stok Detay Listesi", params);
    }
    
    @SuppressWarnings("unchecked")
	public void stockXls(){
        Map params = new HashMap();
        
       
        if( filterModel.getCode() != null && filterModel.getCode().length() > 0 ){
            params.put("pCode", filterModel.getCode() + "%" );
        }
        
        if( filterModel.getName() != null && filterModel.getName().length() > 0 ){
            params.put("pName", filterModel.getName() + "%" );
        }
        
        if( filterModel.getBarcode() != null && filterModel.getBarcode().length() > 0 ){
            params.put("pBarcode", filterModel.getBarcode() );
        }

        if (filterModel.getProductType() != null) {
            if (filterModel.getProductType().equals(ProductType.Product)) {
                params.put("pProduct_Type", filterModel.getProductType().Product.ordinal());
            } else if (filterModel.getProductType().equals(ProductType.Service)) {
                params.put("pProduct_Type", filterModel.getProductType().Service.ordinal());
            }
        }
        //	params.put("pProduct_Type", ProductType.Product.ordinal());
        
        if( filterModel.getCategory() != null ){
            params.put("pCate", filterModel.getCategory().getId() );
        }
        if( filterModel.getGroup() != null ){
            params.put("pGroupId", filterModel.getGroup().getId() );
        }
        
        if (filterModel.getProductType().equals(ProductType.Product)) {
                execXls("stok_listesi_excel", "Stok_Listesi", params);
            } else if (filterModel.getProductType().equals(ProductType.Service)) {
                execXls("stok_listesi_excel", "Hizmet_Listesi", params);
            } else execXls("stok_listesi_excel", "Stok_Hizmet_Listesi", params);
        
    }
    
    @SuppressWarnings("unchecked")
	public void serviceXls(){
        Map params = new HashMap();
        
       
        if( filterModel.getCode() != null && filterModel.getCode().length() > 0 ){
            params.put("pCode", filterModel.getCode() + "%" );
        }
        
        if( filterModel.getName() != null && filterModel.getName().length() > 0 ){
            params.put("pName", filterModel.getName() + "%" );
        }
        
        if( filterModel.getBarcode() != null && filterModel.getBarcode().length() > 0 ){
            params.put("pBarcode", filterModel.getBarcode() );
        }

        if (filterModel.getProductType() != null) {
            if (filterModel.getProductType().equals(ProductType.Product)) {
                params.put("pProduct_Type", filterModel.getProductType().Product.ordinal());
            } else if (filterModel.getProductType().equals(ProductType.Service)) {
                params.put("pProduct_Type", filterModel.getProductType().Service.ordinal());
            }
        }
        //	params.put("pProduct_Type", ProductType.Service.ordinal());
        
        if( filterModel.getCategory() != null ){
            params.put("pCate", filterModel.getCategory().getId() );
        }             
        
        if( filterModel.getGroup() != null ){
            params.put("pGroupId", filterModel.getGroup().getId() );
        }

        if (filterModel.getProductType().equals(ProductType.Product)) {
                execXls("stok_listesi_excel", "Stok_Listesi", params);
            } else if (filterModel.getProductType().equals(ProductType.Service)) {
                execXls("stok_listesi_excel", "Hizmet_Listesi", params);
            } else execXls("stok_listesi_excel", "Stok_Hizmet_Listesi", params);
        
    }
    
    @SuppressWarnings("unchecked")
	public void xls(){
        Map params = new HashMap();
        
       
        if( filterModel.getCode() != null && filterModel.getCode().length() > 0 ){
            params.put("pCode", filterModel.getCode() + "%" );
        }
        
        if( filterModel.getName() != null && filterModel.getName().length() > 0 ){
            params.put("pName", filterModel.getName() + "%" );
        }
        
        if( filterModel.getBarcode() != null && filterModel.getBarcode().length() > 0 ){
            params.put("pBarcode", filterModel.getBarcode() );
        }
        if (filterModel.getProductType() != null) {
            if (filterModel.getProductType().equals(ProductType.Product)) {
                params.put("pProduct_Type", filterModel.getProductType().Product.ordinal());
            } else if (filterModel.getProductType().equals(ProductType.Service)) {
                params.put("pProduct_Type", filterModel.getProductType().Service.ordinal());
            }
        }
        	
        if( filterModel.getCategory() != null ){
            params.put("pCate", filterModel.getCategory().getId() );
        }        
        
        if( filterModel.getGroup() != null ){
            params.put("pGroupId", filterModel.getGroup().getId() );
        }
        
        if (filterModel.getProductType().equals(ProductType.Product)) {
                execXls("stok_listesi_excel", "Stok_Listesi", params);
            } else if (filterModel.getProductType().equals(ProductType.Service)) {
                execXls("stok_listesi_excel", "Hizmet_Listesi", params);
            } else execXls("stok_listesi_excel", "Stok_Hizmet_Listesi", params);
        
    }

	public Integer getExid() {
		return exid;
	}

	public void setExid(Integer exid) {
		this.exid = exid;
	}
    
    
}
