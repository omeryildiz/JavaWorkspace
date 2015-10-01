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

package com.ut.tekir.framework;

import javax.ejb.ApplicationException;


/**
 * @author sinan.yumak
 *
 */
@ApplicationException(rollback=true)
public class TekirRuntimeException extends RuntimeException {
	   
	private static final long serialVersionUID = 1L;
	
	public TekirRuntimeException(String message) {
		super(message);
	}

    public TekirRuntimeException(Throwable cause) {
        super(cause);
    }

}
