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
 * Banka Hareket tipleri
 * 
 * @author haky
 *
 */
public enum BankTransferType {
	Unknown,
	Virman,
	Havale,
	Eft,
	Swift,
	Vezne //banka veznesinden direkt yapılan işlemler
}
