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

import com.ut.tekir.entities.ProductCategory;
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
 *
 * @author haky
 */
@Name("productCategoryConverter")
@BypassInterceptors
@Converter
public class ProductCategoryConverter implements javax.faces.convert.Converter {

    
    @Transactional()
    public Object getAsObject(FacesContext facesContext, UIComponent uIComponent, String string) {
        Object o = null;
        
        
        EntityManager entityManager = (EntityManager) Component.getInstance("entityManager");
        entityManager.joinTransaction();
        
        if( string.length() > 0 ) {
            try{
                o = entityManager.createQuery(" select c from ProductCategory c where code='" + string + "' ").getSingleResult();
            }catch( NoResultException ex ){
                //log.warn("No Entity Found for ProductCategory Code : {0} " , string );
            }
        }
        
        return o;
    }
    
    public String getAsString(FacesContext facesContext, UIComponent uIComponent, Object object) {
        if ( object != null && object instanceof ProductCategory ){
            return ((ProductCategory)object).getCode();
        }
        
        return "";
    }
}
