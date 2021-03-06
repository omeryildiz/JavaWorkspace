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

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import org.jboss.seam.Component;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.annotations.faces.Converter;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import com.ut.tekir.entities.PaymentCommission;

/**
 *
 * @author sinan.yumak
 */
@Name("paymentCommissionConverter")
@BypassInterceptors
@Converter
public class PaymentCommisionConverter implements javax.faces.convert.Converter {
    
    @Transactional()
    public Object getAsObject(FacesContext facesContext, UIComponent uIComponent, String string) {
        Object o = null;
        
        EntityManager entityManager = (EntityManager) Component.getInstance("entityManager");
        entityManager.joinTransaction();
        
        int b = string.indexOf("[");
        int e = string.indexOf("]");
        
        if ( b > -1 && e > b ){
            String s = string.substring( b + 1, e );
            
            if( s.length() > 0 ) {
                try{
                    o = entityManager.createQuery(" select cc from PaymentCommission cc where code='" + s + "' ")
                                     .setHint( "org.hibernate.cacheable", true )
                                     .getSingleResult();
                }catch( NoResultException ex ){
                    //do nothing...
                }
            }
        }
        return o;
    }
    
    public String getAsString(FacesContext facesContext, UIComponent uIComponent, Object object) {
        if ( object != null && object instanceof PaymentCommission ){
            return ((PaymentCommission)object).getCaption();
        }
        return "";
    }
    
}
