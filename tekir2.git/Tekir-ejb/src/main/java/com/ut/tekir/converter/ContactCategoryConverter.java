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

package com.ut.tekir.converter;

import com.ut.tekir.entities.ContactCategory;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import org.jboss.seam.Component;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.annotations.faces.Converter;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

/**
 * Bu sınıf Category Code alanını kullanarak çevrim işlemlerini yapar.
 * @author haky
 */
@Name("contactCategoryConverter")
@BypassInterceptors
@Converter
public class ContactCategoryConverter implements javax.faces.convert.Converter{
    
    @Transactional()
    public Object getAsObject(FacesContext facesContext, UIComponent uIComponent, String string) {
        Object o = null;
        
        
        EntityManager entityManager = (EntityManager) Component.getInstance("entityManager");
        entityManager.joinTransaction();
        
        if( string.length() > 0 ) {
            try{
                o = entityManager.createQuery(" select c from ContactCategory c where code='" + string + "' ").getSingleResult();
            }catch( NoResultException ex ){
                //log.warn("No Entity Found for ContactCategory Code : {0} " , string );
            }
        }
        
        return o;
    }
    
    public String getAsString(FacesContext facesContext, UIComponent uIComponent, Object object) {
        if ( object != null && object instanceof ContactCategory ){
            return ((ContactCategory)object).getCode();
        }
        
        return "";
    }
    
}
