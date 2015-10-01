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

package com.ut.tekir.framework;

import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.End;
import org.jboss.seam.annotations.FlushModeType;
import org.jboss.seam.persistence.HibernateSessionProxy;

/**
 *
 * @author haky
 */
public class BrowseHome<E, F> extends EntityHome<E> implements IBrowseHome<E, F>{
    
    protected List<E> entityList;
    protected F filterModel; 
    
    
    @Override
    public void init(){
        filterModel = newFilterModel();
    }
    
    /**
     *  Geriye FilterModel dönmeli eğer filtre kullanılmayacak ise birşey döndürülmek durumunda değil
     * 
     * @return
     */
    public  F newFilterModel(){
        return null;
    }
    
    @Override @Begin(nested=true, flushMode=FlushModeType.MANUAL)
    public void createNew() {
        super.createNew();
    }
    
    @Override @End
    public String save() {
        String s = super.save();
        //TODO: browser'ı refreshlesek mi?
        return s;
    }
    
    @Override @Begin(nested=true, flushMode=FlushModeType.MANUAL)
    public String edit() {
        return super.edit();
    }

    @Override @Begin(nested=true, flushMode=FlushModeType.MANUAL)
    public void edit(E e) {
        super.edit(e);
    }

    @Override @End
    public String delete() {
        entity = getEntityManager().merge(entity);
        return super.delete();
    }
    
    @Override
    public void refreshEntityList(){
        search();
    }
    
    @SuppressWarnings("unchecked")
	public void search(){
        
    	HibernateSessionProxy session = (HibernateSessionProxy) getEntityManager().getDelegate();
        
        Criteria ecrit =  buildCriteria().getExecutableCriteria( session );
        ecrit.setMaxResults( 100 );
        
        setEntityList( ecrit.list());
    }
    
    /**
     * Bu fonksiyonun üzeri yazılıp gerekli sorgu yapılmalı...
     * 
     * @return
     */
    public DetachedCriteria buildCriteria(){
        DetachedCriteria crit = DetachedCriteria.forClass( getEntityClass() );
        return crit;
    }

    @Override
    public List<E> getEntityList() {
        return entityList;
    }
    
    /**
     * Bu method mutlaka override edilmeli...
     * 
     * @param list
     */
    public void setEntityList(List<E> list) {
        entityList = list;
    }
    
    public F getFilterModel() {
        return filterModel;
    }

    public void setFilterModel(F filterModel) {
        this.filterModel = filterModel;
    }
    
    
    
}
