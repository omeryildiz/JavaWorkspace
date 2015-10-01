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

package com.ut.tekir.finance;

import com.ut.tekir.entities.Contact;
import com.ut.tekir.entities.WorkBunchStatus;
import com.ut.tekir.framework.DefaultDocumentFilterModel;

/**
 * @author sinan.yumak
 *
 */
public class WorkBunchFilterModel extends DefaultDocumentFilterModel {

	private Contact contact;
	private WorkBunchStatus workBunchStatus;
	private String name;

	public WorkBunchStatus getWorkBunchStatus() {
		return workBunchStatus;
	}

	public void setWorkBunchStatus(WorkBunchStatus workBunchStatus) {
		this.workBunchStatus = workBunchStatus;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Contact getContact() {
		return contact;
	}

	public void setContact(Contact contact) {
		this.contact = contact;
	}
	
}
