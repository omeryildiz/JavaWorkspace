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

package com.ut.tekir.tender;

import java.io.Serializable;

import javax.ejb.Local;

import com.ut.tekir.entities.inv.TekirInvoice;

/**
 * Faturaların pos yazıcılar için çıktılarını hazırlar. Hazırlanan çıktının 
 * sistemde çalışan Java yazıcı client'ına aktarılması için gereken işlemleri 
 * yapar. 
 * @author sinan.yumak
 *
 */
@Local
public interface PosPrinterHome extends Serializable {

	/**
	 * Fatura için pos çıktısını hazırlar.
	 * @param aDoc
	 * @return
	 */
	String getPosOutput(TekirInvoice anInvoice);
	
	/**
	 * Faturanın pos çıktısını hazırlayıp yazıcıya gönderir.
	 * @param aDoc
	 */
	void sendToPosPrinter(TekirInvoice anInvoice);
}
