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

import java.math.BigDecimal;
import java.util.Locale;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import org.jboss.seam.Component;
import org.jboss.seam.faces.FacesMessages;

/**
 * @author sinan.yumak
 *
 */
public abstract class DecimalConverterBase implements Converter {

    public Object getAsObject(FacesContext arg0, UIComponent arg1, String arg2) {
        BigDecimal b = null;
        try {
        	String s = getFormatString();
        	
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
            b =  new BigDecimal( Double.parseDouble(s)); //DatatypeConverter.parseDecimal(s);
        } catch (NumberFormatException ex) {
            FacesMessages facesMessages = (FacesMessages)Component.getInstance("facesMessages");
            facesMessages.add("Format Hatalı");
        }

        return b;

    }

    public String getAsString(FacesContext arg0, UIComponent arg1, Object arg2) {
        BigDecimal b = (BigDecimal) arg2;
        Locale locale = (Locale)Component.getInstance("locale");
        String s = String.format(locale, "%,."+ formatLength() + "f", b);
        return s;
    }

	abstract String getFormatString();
	
	abstract int formatLength();
}
