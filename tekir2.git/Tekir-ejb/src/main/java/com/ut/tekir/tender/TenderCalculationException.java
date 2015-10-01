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

import java.util.ArrayList;
import java.util.List;

/**
 * Teklif, sipariş, irsaliye ve fatura hesaplamaları sırasında 
 * çıkacak hatalar için kullanılacak.
 * @author sinan.yumak
 *
 */
public class TenderCalculationException extends Exception {

	private static final long serialVersionUID = 1L;
	
	private List<String> messageList = new ArrayList<String>();
	
	public TenderCalculationException() {
		super();
	}

	public TenderCalculationException(String message) {
		super(message);
		messageList.add(message);
	}

	public TenderCalculationException(List<String> messageList) {
		super(messageList.toString());
		messageList.addAll(messageList);
	}

	@Override
	public String getMessage() {
		//TODO: farklı birşeyler düşünmeli.
		StringBuilder sb = new StringBuilder();
		for (String message : messageList) {
			sb.append(message);
		}
		return sb.toString();
	}

	public List<String> getMessageList() {
		return messageList;
	}

}
