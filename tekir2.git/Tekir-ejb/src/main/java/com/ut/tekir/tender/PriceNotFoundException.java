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

/**
 * @author sinan.yumak
 *
 */
public class PriceNotFoundException extends Exception {

	private static final long serialVersionUID = 1L;

	public PriceNotFoundException() {
		super();
	}

	public PriceNotFoundException(String message) {
		super(message);
	}
	
}
