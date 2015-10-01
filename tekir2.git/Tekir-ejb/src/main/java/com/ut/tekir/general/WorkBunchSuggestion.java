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

package com.ut.tekir.general;

import java.util.Date;
import java.util.List;

import javax.ejb.Remove;

import org.jboss.seam.annotations.Destroy;

import com.ut.tekir.entities.Contact;
import com.ut.tekir.entities.WorkBunch;
import com.ut.tekir.entities.WorkBunchStatus;

/**
 * @author cenk.canarslan
 *
 */
public interface WorkBunchSuggestion {

    @Remove
    @Destroy
    void destroy();

    List<WorkBunch> suggestWorkBunch(Object event);
	List<WorkBunch> getActiveWorkBunchs();
	void selectWorkBunchList();
	
	List<WorkBunch> getWorkBunchList();
	void setWorkBunchList(List<WorkBunch> workBunchList);
	

	/*
	 * getters & setters for fields
	 */
	Contact getContact();
	void setContact(Contact contact);
	
	String getCode();
	void setCode(String code);
	
	String getName();
	void setName(String name);
	
	WorkBunchStatus getWorkBunchStatus();
	void setWorkBunchStatus(WorkBunchStatus workBunchStatus);
	
	Date getBeginDate();
	void setBeginDate(Date beginDate);
	
	Date getEndDate();
	void setEndDate(Date endDate);

}
