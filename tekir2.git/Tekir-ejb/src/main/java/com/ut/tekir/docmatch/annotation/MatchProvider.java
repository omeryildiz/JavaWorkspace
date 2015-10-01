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

package com.ut.tekir.docmatch.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.ut.tekir.entities.DocumentType;

/**
 * Document match providerları yakalamak için kullanacağımız annotation dır.
 * 
 * @author sinan.yumak
 *
 */
@Target(TYPE)
@Retention(RUNTIME)
@Documented
public @interface MatchProvider {
	/**
	 * Provider ın hangi döküman tipleri için kullanılabileceği bilgisini tutar.
	 */
	DocumentType[] types() default {};
}
