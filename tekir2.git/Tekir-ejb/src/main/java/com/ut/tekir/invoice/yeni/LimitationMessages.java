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

package com.ut.tekir.invoice.yeni;

import java.util.ArrayList;
import java.util.List;

import com.ut.tekir.entities.ControlType;
import com.ut.tekir.options.OptionKey;

/**
 * 
 * @author sinan.yumak
 *
 */
public class LimitationMessages {
	private List<LimitationMessage> messages = new ArrayList<LimitationMessage>();

	public void add(OptionKey key, ControlType controlType, String message) {
		if (get(key) == null) {
			messages.add( new LimitationMessage(key, controlType, message) );
		} else {
			get(key).add(message);
		}
	}

	private LimitationMessage get(OptionKey key) {
		for (LimitationMessage lm : messages) {
			if (key.equals(lm.getKey())) return lm;
		}
		return null;
	}

	public List<LimitationMessage> getMessages() {
		return messages;
	}
	
	public void clear() {
		messages.clear();
	}
}
