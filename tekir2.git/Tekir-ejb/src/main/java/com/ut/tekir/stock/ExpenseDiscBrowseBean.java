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
 * @author volkan
 */
@Stateful
@Name("expenseDiscBrowse")
@Scope(value=ScopeType.SESSION)
public class ExpenseDiscBrowseBean extends BrowserBase<Product, ExpenseDiscFilterModel> implements ExpenseDiscBrowse<Product, ExpenseDiscFilterModel>{

    @Override
    public ExpenseDiscFilterModel newFilterModel() {
        return new ExpenseDiscFilterModel();
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
        		.add(Projections.property("unit"), "unit"));
        
        crit.setResultTransformer(Transformers.aliasToBean(Product.class));
        		
         
        if( filterModel.getCode() != null && filterModel.getCode().length() > 0 ){
            crit.add( Restrictions.ilike("this.code", filterModel.getCode() ,MatchMode.START ));
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
        } else { 
//        	Criterion exps = Restrictions.eq("this.productType", ProductType.Expense );
//            Criterion disc = Restrictions.eq("this.productType", ProductType.Discount );
//            LogicalExpression orExp = Restrictions.or(exps,disc);
//            crit.add(orExp);

            crit.add(Restrictions.in("this.productType", new ProductType[]{ProductType.Expense,
            															   ProductType.Discount,
            															   ProductType.DiscountAddition,
            															   ProductType.ExpenseAddition}));
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
       
        if( filterModel.getCategory() != null ){
            params.put("pCate", filterModel.getCategory().getId() );
        }
        
        

        execPdf("masraf_listesi", "Masraf/İndirim Listesi", params);
        
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

        execPdf("masraf_detay", "Masraf/İndirim Detay Listesi", params);
    }
}
