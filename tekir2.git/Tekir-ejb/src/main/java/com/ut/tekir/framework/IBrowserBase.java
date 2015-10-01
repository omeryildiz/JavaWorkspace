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
import javax.ejb.Remove;
import org.hibernate.criterion.DetachedCriteria;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Destroy;

/**
 *
 * @author haky
 */
public interface IBrowserBase<E, F> {

    DetachedCriteria buildCriteria();

    @Remove
    @Destroy
    void destroy();

    Class<E> getEntityClass();

    List<E> getEntityList();

    F getFilterModel();

    @Create
    void init();

    F newFilterModel();

    void search();

    void setEntityClass(Class<E> entityClass);

    void setEntityList(List<E> list);

    void setFilterModel(F filterModel);

    
}
