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

package com.ut.tekir.contact;

import com.ut.tekir.entities.ContactCategory;
import com.ut.tekir.framework.IEntityHome;
import java.util.List;

/**
 *
 * @author haky
 */
public interface ContactCategoryHome<E> extends IEntityHome<E> {
    
    void createNew();

    
    ContactCategory getContactCategory();

    List<ContactCategory> getEntityList();

    
    void initContactCategoryList();

    
    void setContactCategory(ContactCategory contactCategory);

    void setEntityList(List<ContactCategory> entityList);
    
}
