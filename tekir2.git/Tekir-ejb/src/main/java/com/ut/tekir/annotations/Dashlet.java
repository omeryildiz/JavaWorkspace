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

package com.ut.tekir.annotations;

import com.ut.tekir.dashboard.DashletCapability;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Dashlet bileşenlerini tanımlamak için annotation
 * Bütün dashlet'ler aynı zamanda bir seam bileşeni olmak zorunda dolayısı ile isim almıyoruz.
 * Seam ismini kullanıyor olacağız.
 *
 * kullanıcıya sunarken lazım olan Caption, Icon, View, EditView gibi şeyleri ise naming convetion ile alacağız.
 * @author Hakan Uygun
 */
@Target(TYPE)
@Retention(RUNTIME)
@Documented
public @interface Dashlet {
    DashletCapability[] capabilities() default {};
}
