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

import javax.ejb.Local;

/**
 * Local interface for EntityHome based Session Beans
 * @author haky
 */
@Local
public interface IEntityHome<E> {

    void init();
    
    Long getId();
    void setId(Long id);

    String browse();
    
    void createNew();
    
    String save();
    String saveAndNew();
    String delete();
    
    String edit();
    String close();
    
    void edit(E e);
    void delete(E e);
    
    void destroy();
}
