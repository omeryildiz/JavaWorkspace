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

import java.util.List;

import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.persistence.EntityManager;

import org.hibernate.Criteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.SimpleExpression;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.log.Log;
import org.jboss.seam.persistence.HibernateSessionProxy;

import com.ut.tekir.entities.Product;
import com.ut.tekir.entities.ProductCategory;
import com.ut.tekir.entities.ProductGroup;
import com.ut.tekir.entities.ProductType;

/**
 *
 * @author haky
 */
@Stateful
@Name("stockSuggestion")
@Scope(ScopeType.SESSION)
public class StockSuggestionBean implements StockSuggestion {

    @Logger
    private Log log;
    @In
    private EntityManager entityManager;

    @SuppressWarnings("unchecked")
	private List productList;
    @SuppressWarnings("unchecked")
    private List expenseAndDiscountList;
    private String code;
    private String name;
    private String barcode;
    private ProductType productType;
    private Boolean disableProductCombo = false;
    private ProductGroup group;
    private ProductCategory category;
    
    @SuppressWarnings("unchecked")
	public List<ProductCategory> suggestProductCategory(Object event){
        String pref = event.toString();

        log.debug("suggest category  : {0}", pref );
        
        return entityManager.createQuery("select c from ProductCategory c where ( c.code like :code ) and active = 1" )
                .setParameter("code", pref + "%")
                .setMaxResults(30)
                .setHint("org.hibernate.cacheable", true)
                .getResultList();
    }

    @SuppressWarnings("unchecked")
	public List<Product> suggestProduct(Object event){
        String pref = event.toString();

        log.debug("suggest product  : {0}", pref );
        
        return entityManager.createQuery("select c from Product c where ( c.code like :code or c.name like :name) and active = 1" )
                .setParameter("code", pref + "%")
                .setParameter("name", "%" + pref + "%")
                .setMaxResults(30)
                .setHint("org.hibernate.cacheable", true)
                .getResultList();
    }

    /**
     *
     * @return sadece stok kayitları
     */
    @SuppressWarnings("unchecked")
    public List<Product> suggestIsProduct(Object event){
    	String pref = event.toString();
    	
    	log.debug("suggest product  : {0}", pref );
    	
    	return entityManager.createQuery("select c from Product c where ( c.code like :code or c.name like :name) and active = 1  and c.productType = 1" )
    	.setParameter("code", pref + "%")
    	.setParameter("name", "%" + pref + "%")
    	.setMaxResults(30)
    	.setHint("org.hibernate.cacheable", true)
    	.getResultList();
    }
    
    /**
     * Barcode lu stok sugestion 
     */
    @SuppressWarnings("unchecked")
    public List<Product> suggestIsProductWithBarcode(Object event){
    	String pref = event.toString();
    	
    	log.debug("suggest product  : {0}", pref );
    	
    	return entityManager.createQuery("select c from Product c where ( c.code like :code or c.name like :name or c.barcode1 like :barcode1 or c.barcode2 like :barcode2 or c.barcode3 like :barcode3) and active = 1  and c.productType = 1" )
    	.setParameter("code", pref + "%")
    	.setParameter("name", "%" + pref + "%")
    	.setParameter("barcode1", "%" + pref + "%")
    	.setParameter("barcode2", "%" + pref + "%")
    	.setParameter("barcode3", "%" + pref + "%")
    	.setMaxResults(30)
    	.setHint("org.hibernate.cacheable", true)
    	.getResultList();
    }

    /**
     *
     * @return sadece hizmet kayitları
     */
    @SuppressWarnings("unchecked")
    public List<Product> suggestIsService(Object event){
    	String pref = event.toString();

    	log.debug("suggest product  : {0}", pref );

    	return entityManager.createQuery("select c from Product c where ( c.code like :code or c.name like :name) and active = 1  and c.productType = 2" )
    	.setParameter("code", pref + "%")
    	.setParameter("name", "%" + pref + "%")
    	.setMaxResults(30)
    	.setHint("org.hibernate.cacheable", true)
    	.getResultList();
    }
    
    /**
     * Barcode lu stok ve hizmet sugestion 
     */
    @SuppressWarnings("unchecked")
    public List<Product> suggestWithBarcode(Object event){
    	String pref = event.toString();
    	
    	log.debug("suggest product  : {0}", pref );
    	
    	return entityManager.createQuery("select c from Product c where ( c.code like :code or c.name like :name or c.barcode1 like :barcode1 or c.barcode2 like :barcode2 or c.barcode3 like :barcode3) and active = 1" )
    	.setParameter("code", pref + "%")
    	.setParameter("name", "%" + pref + "%")
    	.setParameter("barcode1", "%" + pref + "%")
    	.setParameter("barcode2", "%" + pref + "%")
    	.setParameter("barcode3", "%" + pref + "%")
    	.setMaxResults(30)
    	.setHint("org.hibernate.cacheable", true)
    	.getResultList();
    }
    
    /**
     *
     * @return masraf ve indirim kayitları
     */
    @SuppressWarnings("unchecked")
    public List<Product> suggestExpenseAndDiscount(Object event){
    	String pref = event.toString();
    	
    	log.debug("suggest product  : {0}", pref );
    	
    	return entityManager.createQuery("select c from Product c where ( c.code like :code or c.name like :name) and active = 1  and ( c.productType = 3 or c.productType = 4 )" )
    	.setParameter("code", pref + "%")
    	.setParameter("name", "%" + pref + "%")
    	.setMaxResults(30)
    	.setHint("org.hibernate.cacheable", true)
    	.getResultList();
    }
    
    @SuppressWarnings("unchecked")
	public List<ProductCategory> getProductCategoryList(){
        
        return entityManager.createQuery("select c from ProductCategory c where active = 1 order by weight,code" )
                .setHint("org.hibernate.cacheable", true)
                .getResultList();
    }
    
    public void selectProductList(){
        
    	HibernateSessionProxy session = (HibernateSessionProxy) entityManager.getDelegate();

    	Criteria crit = session.createCriteria( Product.class );
        
        if( getCode() != null && getCode().length() > 0 ){
            crit.add( Restrictions.like("this.code", getCode() + "%" ));
        }
        
        if( getName() != null && getName().length() > 0 ){
            crit.add( Restrictions.like("this.name", getName() + "%" ));
        }

        if( getBarcode() != null && getBarcode().length() > 0 ){
        	SimpleExpression barcodeCrit1 = Restrictions.like("this.barcode1", getBarcode() + "%" );
        	SimpleExpression barcodeCrit2 = Restrictions.like("this.barcode2", getBarcode() + "%" );
        	SimpleExpression barcodeCrit3 = Restrictions.like("this.barcode3", getBarcode() + "%" );
        	
        	crit.add(Restrictions.or(Restrictions.or(barcodeCrit1, barcodeCrit2), barcodeCrit3));
        }
        
        if( getProductType() != ProductType.Unknown ){
        	crit.add( Restrictions.eq("this.productType", getProductType() ));
        }

        if(getCategory() != null){
            crit.add(Restrictions.eq("this.category", getCategory()));
        }

        if(getGroup() != null){
            crit.add(Restrictions.eq("this.group", getGroup()));
        }

        crit.setProjection(Projections.projectionList()
                .add(Projections.property("code"), "code")
                .add(Projections.property("name"), "name")
                .add(Projections.property("productType"), "productType")
                .add(Projections.property("barcode1"), "barcode1")
                .add(Projections.property("category"),"category")
                .add(Projections.property("group"),"group"));
        
        crit.add(Restrictions.eq("this.active", true));
        
        crit.setMaxResults(30);
//        crit.setCacheable(true);
        //TODO: Map niye çalışmıyor kine?
        //crit.setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP);
        productList = crit.list();
        
    }
    
    public void selectExpenseAndDiscountList(){
    	
    	HibernateSessionProxy session = (HibernateSessionProxy) entityManager.getDelegate();
    	Criteria crit = session.createCriteria( Product.class );
    	
    	if( getCode() != null && getCode().length() > 0 ){
    		crit.add( Restrictions.like("this.code", getCode() + "%" ));
    	}
    	
    	if( getName() != null && getName().length() > 0 ){
    		crit.add( Restrictions.like("this.name", getName() + "%" ));
    	}
    	
    	if( getProductType() != null && getProductType() == ProductType.Unknown){
    		crit.add( Restrictions.or( Restrictions.eq("this.productType", ProductType.Expense ), Restrictions.eq("this.productType", ProductType.Discount) ));
    	}else{
    		crit.add(Restrictions.eq("this.productType", getProductType()));
    	}
    	
    	crit.setProjection(Projections.projectionList()
    			.add(Projections.property("code"), "code")
    			.add(Projections.property("name"), "name")
    			.add(Projections.property("productType"), "productType"));
    	
    	crit.add(Restrictions.eq("active", true));
    	
    	crit.setMaxResults(30);
    	crit.setCacheable(true);
    	//TODO: Map niye çalışmıyor kine?
    	//crit.setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP);
    	expenseAndDiscountList = crit.list();
    	
    }
    
    @Remove @Destroy
    public void destroy() {
    }

    @SuppressWarnings("unchecked")
	public List getProductList() {
        return productList;
    }

    @SuppressWarnings("unchecked")
	public void setProductList(List productList) {
        this.productList = productList;
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

	public void setProductType(ProductType productType) {
		this.productType = productType;
	}

	public ProductType getProductType() {
		return productType;
	}

	public void setDisableProductCombo(Boolean disableProductCombo) {
		this.disableProductCombo = disableProductCombo;
	}

	public Boolean getDisableProductCombo() {
		return disableProductCombo;
	}

	public String getBarcode() {
		return barcode;
	}

	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}

	@SuppressWarnings("unchecked")
	public void setExpenseAndDiscountList(List expenseAndDiscountList) {
		this.expenseAndDiscountList = expenseAndDiscountList;
	}

	@SuppressWarnings("unchecked")
	public List getExpenseAndDiscountList() {
		return expenseAndDiscountList;
	}

    public ProductCategory getCategory() {
        return category;
    }

    public void setCategory(ProductCategory category) {
        this.category = category;
    }

    public ProductGroup getGroup() {
        return group;
    }

    public void setGroup(ProductGroup group) {
        this.group = group;
    }

}
