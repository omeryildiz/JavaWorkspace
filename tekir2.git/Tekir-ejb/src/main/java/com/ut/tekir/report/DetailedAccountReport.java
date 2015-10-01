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

package com.ut.tekir.report;

import java.io.Serializable;
import java.util.Date;

import javax.ejb.Local;

import com.ut.tekir.entities.Account;
import com.ut.tekir.entities.OrganizationLevel;

/**
 * @author sinan.yumak
 *
 */
@Local
public interface DetailedAccountReport extends Serializable {

	Date getBeginDate();
	void setBeginDate(Date beginDate);

	Date getEndDate();
	void setEndDate(Date endDate);

	Account getAccount();
	void setAccount(Account account);

	OrganizationLevel getOrganizationLevel();
	void setOrganizationLevel(OrganizationLevel organizationLevel);
	
	void destroy();
	void pdf();

}
