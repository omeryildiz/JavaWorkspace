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
 * Ödeme sözleşmelerinde kullanılacak kartların aidiyet
 * bilgilerini tutar.
 * @author sinan.yumak
 *
 */
public enum CardOwnerType {
	Own, //kendi
	Domestic, //yurt içi
	International //yurt dışı
}
