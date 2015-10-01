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
 * 
 * Hesap planı tanıtımında ödenecek tutarın hesaplanması için hedef seçimi.
 * 
 * @author dumlupinar
 *
 */
public enum PaymentPlanDestType {
	
	Total, //toplam tutar üzerinden
	Tax, //vergi üzerinden
	Cost, //masraf üzerinden
	Fee, //harç üzerinden
	TaxBase //Vergi matrahı üzerinden
}
