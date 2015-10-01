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

import javax.persistence.EntityManager;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.log.Log;

/**
 *
 * @author haky
 */
public interface IEntityBase<E> {

    String close();

    void createNew();

    String delete();

    void delete(E e);

    void destroy();

    String edit();

    void edit(E e);

    Long entityId();

    E getEntity();

    Class<E> getEntityClass();

    EntityManager getEntityManager();

    FacesMessages getFacesMessages();

    Long getId();

    Log getLog();

    String save();

    String saveAndNew();

    void setEntity(E entity);

    void setEntityClass(Class<E> entityClass);

    void setEntityManager(EntityManager em);

    void setFacesMessages(FacesMessages facesMessages);

    void setId(Long id);

    void setLog(Log log);

}
