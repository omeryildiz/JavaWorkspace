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

package com.ut.tekir.entities;

import java.util.List;

/**
 * @author sinan.yumak
 *
 */
public interface Category {
	Long getId();
	Category getParent();
	List getChildList();

	void setPath(String path);
	String getPath();
	String getCode();
	Integer getWeight();
	Boolean getActive();
}
