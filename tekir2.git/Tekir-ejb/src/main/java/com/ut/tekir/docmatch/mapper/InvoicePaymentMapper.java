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

package com.ut.tekir.docmatch.mapper;

import java.io.Serializable;

import javax.ejb.Local;

/**
 * 
 * @author sinan.yumak
 *
 */
@Local
public interface InvoicePaymentMapper extends Serializable {
	void initialize();
	MapperModel getModel();
	void mapSelected();
	
	void destroy();
}
