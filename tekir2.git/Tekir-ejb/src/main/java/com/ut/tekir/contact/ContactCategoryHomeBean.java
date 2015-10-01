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
import com.ut.tekir.framework.EntityHome;
import java.util.List;
import javax.ejb.Stateful;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.datamodel.DataModel;

/**
 *
 * @author haky
 */
@Stateful
@Name("contactCategoryHome")
@Scope(value=ScopeType.CONVERSATION)
public class ContactCategoryHomeBean extends EntityHome<ContactCategory> implements ContactCategoryHome<ContactCategory> {
    
    @DataModel("contactCategoryList")
    private List<ContactCategory> entityList;
    
    @SuppressWarnings("unchecked")
	@Factory("contactCategoryList")
    public void initContactCategoryList() {
        log.debug("Contact Category Listesi hazırlanıyor...");
        
        setEntityList(getEntityManager().createQuery("select c from ContactCategory c order by weight,code")
        //.setMaxResults(100)
        //.setHint("org.hibernate.cacheable", true)
        .getResultList());
    }
    
    
    @Out(required=false)
    public ContactCategory getContactCategory() {
        return getEntity();
    }

    @In(required=false)
    public void setContactCategory(ContactCategory contactCategory) {
        setEntity( contactCategory );
    }
    
        
    @Override
    public void createNew() {
        log.debug("Yeni Cari Kategorisi");
        entity = new ContactCategory();
        entity.setActive(true);
        entity.setWeight(10);
    }

    @Override
    public List<ContactCategory> getEntityList() {
        return entityList;
    }

    public void setEntityList(List<ContactCategory> entityList) {
        this.entityList = entityList;
    }
    
}
