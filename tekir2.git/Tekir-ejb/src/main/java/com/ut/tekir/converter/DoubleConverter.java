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

import java.util.Locale;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import org.jboss.seam.Component;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.faces.Converter;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.faces.FacesMessages;

/**
 *
 * @author volkan
 */
@Name("doubleConverter")
@BypassInterceptors
@Converter
public class DoubleConverter implements javax.faces.convert.Converter{


    public Object getAsObject(FacesContext arg0, UIComponent arg1, String arg2) {
        Double d = null;

        try {
        	String s = "0.00";
        	
        	if( arg2.contains(",") && arg2.contains(".")){
        		if( arg2.lastIndexOf(".") > arg2.lastIndexOf(",") ){
        	
        			s = arg2.replaceAll(",", "");
        			
        		} else {
        			s = arg2.replaceAll("\\.", "");
                    s = s.replaceAll(",", ".");
                    
        		}
        	} else if( arg2.contains(",") ){
        		s = arg2.replaceAll(",", ".");
        	} else {
        		s = arg2;
        	}
        	
        	if( !s.contains(".")) {
            	s = s + ".00";
            }
            d =  Double.parseDouble(s); 
        	
            /*
            String s = arg2.replaceAll("\\.", "");
            s = s.replaceAll(",", ".");
            d = Double.parseDouble(s);
            */
        } catch (NumberFormatException ex) {
            FacesMessages facesMessages = (FacesMessages)Component.getInstance("facesMessages");
            facesMessages.add("Format Hatalı");
            
        }

        return d;

    }

    public String getAsString(FacesContext arg0, UIComponent arg1, Object arg2) {
        Double d = (Double) arg2;
        Locale locale = (Locale)Component.getInstance("locale");
        String s = String.format(locale, "%,.2f", d);
        return s;
    }

}
