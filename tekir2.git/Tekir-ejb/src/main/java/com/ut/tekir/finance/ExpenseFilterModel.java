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
import com.ut.tekir.entities.WorkBunch;
import com.ut.tekir.framework.DefaultDocumentFilterModel;

/**
 *
 * @author sinan.yumak
 */
public class ExpenseFilterModel extends DefaultDocumentFilterModel{
    
    private Contact contact;
    private String expenseOwner;
    private WorkBunch workBunch;

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

	public String getExpenseOwner() {
		return expenseOwner;
	}

	public void setExpenseOwner(String expenseOwner) {
		this.expenseOwner = expenseOwner;
	}

	public WorkBunch getWorkBunch() {
		return workBunch;
	}

	public void setWorkBunch(WorkBunch workBunch) {
		this.workBunch = workBunch;
	}

}
