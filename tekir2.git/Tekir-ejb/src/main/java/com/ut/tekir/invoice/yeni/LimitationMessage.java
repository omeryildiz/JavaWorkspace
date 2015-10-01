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
public class LimitationMessage {
	private OptionKey key;
	private ControlType controlType;
	private List<String> messages;

	public LimitationMessage() {
		this.messages = new ArrayList<String>();
	}

	public LimitationMessage(OptionKey key, String message) {
		this.key = key;
		this.messages = createMessageList(message);
	}

	public LimitationMessage(OptionKey key, ControlType controlType, String message) {
		this.key = key;
		this.controlType = controlType;
		this.messages = createMessageList(message);
	}

	public LimitationMessage(OptionKey key, List<String> messages) {
		this.key = key;
		this.messages = messages;
	}

	private List<String> createMessageList(String message) {
		List<String> messageList = new ArrayList<String>();
		messageList.add(message);
		return messageList;
	}
	
	public void add(String message) {
		getMessages().add(message);
	}
	
	public OptionKey getKey() {
		return key;
	}

	public void setKey(OptionKey key) {
		this.key = key;
	}

	public List<String> getMessages() {
		return messages;
	}

	public void setMessages(List<String> messages) {
		this.messages = messages;
	}

	public ControlType getControlType() {
		return controlType;
	}

	public void setControlType(ControlType controlType) {
		this.controlType = controlType;
	}

}
