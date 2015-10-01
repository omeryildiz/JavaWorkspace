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

import java.util.ArrayList;
import java.util.List;

/**
 * @author sinan.yumak
 *
 */
public class ContactImportException extends Exception {

	private static final long serialVersionUID = 1L;

	private List<String> messageList = new ArrayList<String>();

	public ContactImportException() {
		super();
	}

	public ContactImportException(String message) {
		super(message);
		messageList.add(message);
	}

	public ContactImportException(List<String> messageList) {
		super(messageList.toString());
		messageList.addAll(messageList);
	}

	@Override
	public String getMessage() {
		return messageList.size() > 0 ? messageList.size() > 1 ? messageList.toString() : messageList.get(0) : "";
	}

	public List<String> getMessageList() {
		return messageList;
	}

	public void add( String message ){
		messageList.add(message);
	}

	public void add(List<String> e) {
		messageList.addAll(e);
	}
	
}
