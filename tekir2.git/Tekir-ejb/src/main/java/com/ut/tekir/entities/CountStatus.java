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

package com.ut.tekir.entities;

/**
 * Sayım fişi durumları...
 * @author sinan.yumak
 *
 */
public enum CountStatus {
	Open, // açık
	Counting, // sayılıyor
	Counted, // sayıldı
	Editing, // sayım bitti daha sonra düzenlemeye açıldı.
	Finished // sayım bitti.
}
