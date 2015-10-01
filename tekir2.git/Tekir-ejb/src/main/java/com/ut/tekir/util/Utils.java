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

package com.ut.tekir.util;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import javax.jms.JMSException;

import org.hibernate.validator.InvalidStateException;
import org.hibernate.validator.InvalidValue;

import com.ut.tekir.framework.BaseConsts;
import com.ut.tekir.tender.PriceNotFoundException;

/**
 * @author sinan.yumak
 *
 */
public class Utils {

	public static boolean isEmpty( String aString ){
        return ( aString == null ) ? true : aString.length() > 0 ? false : true;
    }
    
    public static boolean isNotEmpty( String s ){
        return !isEmpty(s);
    }

    public static String getFileSeperator() {
    	return System.getProperty("file.separator");
    }
    
    public static String getExceptionMessage(Exception ex) {
    	StringBuilder result = new StringBuilder();
    	if (ex instanceof JMSException) {
    		result.append("JMS hatası oluştu!. Sebebi: \n")
    			  .append(ex.getMessage());
    	} else if (ex instanceof FileNotFoundException) {
    		result.append("Dosya bulunamadı hatası oluştu!. Sebebi: \n")
    		      .append(ex.getMessage());
    	} else if (ex instanceof InvalidStateException) {
    		result.append("Doğrulama hatası meydana geldi. Sebepleri: \n");
    		for (InvalidValue iv : ((InvalidStateException) ex).getInvalidValues()) {
    			result.append(iv.getMessage()).append("\n");
    		}
    	} else if (ex instanceof PriceNotFoundException) {
    		result.append(ex.getMessage());
    	} else {
    		result.append(" Hata sebebi: ")
    			  .append(ex.getMessage());
    	}
    	return result.toString();
    }

    @SuppressWarnings("unchecked")
    public static <F> List<F> changeListType(List aList, Class<F> a) {
    	List<F> result = new ArrayList<F>();
    	
    	for (int i=0;i<aList.size();i++) {
    		result.add( (F)aList.get(i) );
    	}
    	return result;
    }

    public static boolean isSystemCurrency(String currency) {
    	return currency.equals(BaseConsts.SYSTEM_CURRENCY_CODE);
    }
}
