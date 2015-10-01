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

import java.util.List;

import javax.ejb.Local;

import com.ut.tekir.entities.Organization;
import com.ut.tekir.entities.OrganizationLevel;
import com.ut.tekir.framework.IEntityBase;

/**
 *
 * @author sinan.yumak
 */
@Local
public interface OrganizationHome<E> extends IEntityBase<E>{
	
	void init();
	
    Organization getOrganization();

    void setOrganization(Organization organization);
	
    Organization createNewChildOrganization();

    List<Organization> findParentList();

	Boolean getShowOrganizationCombobox();
	
	void setShowOrganizationCombobox(Boolean showOrganizationCombobox);

	OrganizationLevel getLevel();
	
	void setLevel(OrganizationLevel level);
}
