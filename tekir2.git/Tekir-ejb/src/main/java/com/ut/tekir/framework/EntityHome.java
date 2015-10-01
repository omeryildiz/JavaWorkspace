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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;

import javax.ejb.Remove;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

import org.hibernate.exception.ConstraintViolationException;
import org.jboss.seam.Component;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.End;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.log.Log;

import com.ut.tekir.entities.AuditBase;
import com.ut.tekir.entities.User;

/**
 * Base class form Entity Home beans
 * @author haky
 */
public abstract class EntityHome<E> {

    protected Long id;
    protected E entity;
    private Class<E> entityClass;
    @Logger
    protected Log log;
    @PersistenceContext(type = PersistenceContextType.EXTENDED)
    protected EntityManager entityManager;
    @In
    protected FacesMessages facesMessages;

    public void init() {

    }

    public Long entityId() {
        Long id = null;
        Method m;

        try {
            m = entity.getClass().getMethod("getId");

            if (m != null) {

                id = (Long) m.invoke(entity);

            }
        } catch (SecurityException ex) {
            ex.printStackTrace();
        } catch (NoSuchMethodException ex) {
            ex.printStackTrace();
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
        } catch (InvocationTargetException ex) {
            ex.printStackTrace();
        }


        return id;
    }

    @Begin
    public String browse() {
        return BaseConsts.SUCCESS;
    }

    public String edit() {
        return BaseConsts.SUCCESS;
    }

    @End
    public String close() {
        return BaseConsts.SUCCESS;
    }

    /**
     * Kayıt işlemlerinden sonra çağırılır. Eğer isteniyor ise listenin tekrardan çelkilmesi içindir.
     * 
     */
    public void refreshEntityList() {

    }

    @SuppressWarnings("unchecked")
	public String save() {
        if (entity == null) {
            return BaseConsts.FAIL;
        }

        try {
        	setAuditRecords();
        	
        	if (entityId() == null) {
                //New Record
                getEntityManager().persist(entity);

                List ls = getEntityList();
                if (ls != null) {
                    log.debug("Listeye ekleniyor : #0", ls);
                    ls.add(entity);
                }
            } else {
                //Update Record
                getEntityManager().merge(entity);
            }
            getEntityManager().flush();

            if (id == null) id = entityId();
	    } catch (Exception e) {
	    	log.debug("Hata : #0", e);
	    	if (e instanceof ConstraintViolationException){
	    		facesMessages.add("#{messages['general.message.record.NotUnique']}");
	    	} else {
	    		facesMessages.add("#{messages['general.message.record.SaveFail']}");
	    	}
	        return BaseConsts.FAIL;
	    }
        log.debug("Entity Saved : {0} ", entity);
        facesMessages.add("#{messages['general.message.record.SaveSuccess']}");

        refreshEntityList();

        return BaseConsts.SUCCESS;
    }

    private void setAuditRecords() {
    	if (entity instanceof AuditBase) {
    	    User activeUser = (User)Component.getInstance("activeUser",true);

    		AuditBase ab = (AuditBase)entity;
    		if (entityId() == null) {
    			ab.setCreateDate(new Date());
    			ab.setCreateUser(activeUser.getUserName());
    		} 
			ab.setUpdateDate(new Date());
			ab.setUpdateUser(activeUser.getUserName());
    	}
    }

    @SuppressWarnings("unchecked")
	public String delete() {
        if (entity == null) {
            return BaseConsts.FAIL;
        }

        try {
            getEntityManager().remove(entity);
            getEntityManager().flush();
        } catch (Exception e) {
            log.debug("Hata : #0", e);
            facesMessages.add("#{messages['general.message.record.DeleteFaild']}");
            return BaseConsts.FAIL;
        }


        List ls = getEntityList();
        if (ls != null) {
            log.debug("Listeden çıkartılıyor : #0", ls);
            ls.remove(entity);
        }

        log.debug("Entity Removed : {0} ", entity);
        entity = null;
        facesMessages.add("#{messages['general.message.record.DeleteSuccess']}");

        refreshEntityList();

        return BaseConsts.SUCCESS;
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

    public void setEntityManager(EntityManager em) {
        this.entityManager = em;
    }

    public E getEntity() {
        if (entity == null) {
            log.debug("getEntity : Entity boş yeni ataacak.");
            createNew();

        }
        return entity;
    }

    public void setEntity(E entity) {
        /*
        if (entityManager != null && entity != null ) {
        this.entity = getEntityManager().merge(entity);
        }
         */
        this.entity = entity;
        log.debug("Entity Setlendi : {0}", entity);
    }

    public void createNew() {
        try {
            entity = getEntityClass().newInstance();
            log.debug("evet yeni bi tane şey ettik...");
        } catch (InstantiationException ex) {
            log.error("Create New Error", ex);
        } catch (IllegalAccessException ex) {
            log.error("Create New Error", ex);
        }
    }

    public String saveAndNew() {
        String s = save();
        createNew();
        return s;
    }

    public void edit(E e) {
        log.debug("Edit edicedik : #0", e);
        entity = e;
    }

    public void delete(E e) {
        entity = e;
        delete();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
        log.debug("ID ile setleniyor. ID : {0} ", id);
        entity = getEntityManager().find(getEntityClass(), id);
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

    public Log getLog() {
        return log;
    }

    public void setLog(Log log) {
        this.log = log;
    }

    public FacesMessages getFacesMessages() {
        return facesMessages;
    }

    public void setFacesMessages(FacesMessages facesMessages) {
        this.facesMessages = facesMessages;
    }

    public List<E> getEntityList() {
        log.debug("super.getEntityList");
        return null;
    }

    @Remove
    @Destroy
    public void destroy() {
    }
}
