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



/**
 *
 * @author haky
 */
public class StringUtils {
    
    /**
     * Verilen String değişken null ya da boşluk ise geriye true döner.
     */
    public static boolean isEmpty( String s ){
        return s == null ? true : s.length() == 0 ? true : false;
    }
    
    /**
     * Verilen String değişken null ya da boşluk ise geriye false döner.
     */
    public static boolean isNotEmpty( String s ){
        return !isEmpty( s );
    }
    
    public static String concat( String s1, String s2 ){
        return s1 + s2;
    }
    
    public static Boolean isOdd( Integer i ){
        return (( i % 2 ) == 0 ) ? true : false;
    }
    
    public static String oddEven( Integer i ){
        if ( i == null ) i = 0;
        return (( i % 2 ) == 0 ) ? "Odd" : "Even";
    }

    public static String returnPaddingLeft( Integer i){
        String result = "padding-left:";
    	if ( i != null ) {
        	result = result + i * 10 + "px;";
        }
        return result;
    }
    
}
