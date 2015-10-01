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

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import javax.ejb.Remove;
import javax.persistence.EntityManager;

import net.sf.jasperreports.engine.JRException;

import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.log.Log;
import org.jboss.seam.persistence.HibernateSessionProxy;


/**
 *
 * @author haky
 */
public class BrowserBase<E, F> implements IBrowserBase<E, F> {
    
    private Class<E> entityClass;
    protected List<E> entityList;
    protected F filterModel; 
    
    @Logger
    protected Log log;
    
    @In
    protected EntityManager entityManager;
    
    @In
    protected FacesMessages facesMessages;

    @In( create=true )
    private JasperHandlerBean jasperReport;
    
    @Create
    public void init(){
        filterModel = newFilterModel();
    }
    
    /**
     *  Geriye FilterModel dönmeli eğer filtre kullanılmayacak ise birşey döndürülmek durumunda değil
     * 
     * @return
     */
    public  F newFilterModel(){
        return  null;
    }
    
    @SuppressWarnings("unchecked")
	public void search(){

        log.debug("Search Execute");
        
        HibernateSessionProxy session = (HibernateSessionProxy) getEntityManager().getDelegate();
        
        Criteria ecrit =  buildCriteria().getExecutableCriteria( session );
        //ecrit.setCacheable(true);
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

    public  EntityManager getEntityManager() {
        return entityManager;
    }

    @SuppressWarnings("unchecked")
	public Class<E> getEntityClass() {
        if (entityClass == null) {
            Type type = getClass().getGenericSuperclass();
            if (type instanceof ParameterizedType) {
                ParameterizedType paramType = (ParameterizedType) type;
                entityClass = (Class<E>) paramType.getActualTypeArguments()[0];
            } else {
                throw new IllegalArgumentException("Could not guess entity class by reflection");
            }
        }
        return entityClass;
    }

    public void setEntityClass(Class<E> entityClass) {
        this.entityClass = entityClass;
    }
    
    @Remove @Destroy
    public void destroy() {
    }
    
    /**
     * Filtreleme sırasında kullanılacak util fonksiyonları.
     * 
     * @param s
     * @return
     */
    protected boolean isEmpty( String s ){
        return ( s == null ) ? true : s.length() > 0 ? false : true;
    }
    
    protected boolean isNotEmpty( String s ){
        return !isEmpty(s);
    }
    
    /**
     * Verilen isimli jasper reportu verilen isimli pdf olarak istemciye gönderir.
     * Rapor parametreleri MAp içerisinde gönderilecektir.
     * @param name Jasper Dosya adı. /jasper/{name}.jasper olarak aranacaktır
     * @param fileName İstemciye gönderilecek olan pdf adı {fileName}.pdf olarak yollanacaktır.
     * @param params rapor parametreleri
     * @throws net.sf.jasperreports.engine.JRException
     */
    @SuppressWarnings("unchecked")
	protected void execPdf( String name, String fileName, Map params ){
       try {
            jasperReport.reportToPDF( name, fileName, params);
        } catch (JRException ex) {
            log.error("Rapor Hatası", ex);
            facesMessages.add("Rapor Çalıştırılamadı");
        } 
    }
    
    @SuppressWarnings("unchecked")
	protected void execXls( String name, String fileName, Map params ){
       try {
            jasperReport.reportToXls(name, fileName, params);
        } catch (JRException ex) {
            log.error("Xls Hatası", ex);
            facesMessages.add("Xls Raporu Çalıştırılamadı");
        } 
    }
    
    
}
