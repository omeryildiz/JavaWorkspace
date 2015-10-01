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

public class TekirException extends Exception{
	   
	private static final long serialVersionUID = 1L;
	
	public TekirException(String message){
		super(message);
	}

	public TekirException() {
		super();
		// TODO Auto-generated constructor stub
	}

	public TekirException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public TekirException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}
}
