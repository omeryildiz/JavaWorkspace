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

import com.ut.tekir.entities.OrganizationLevel;


/**
 * @author sinan.yumak
 * 
 */
public class OrganizationFilterModel {

	private Long id;
	private String code;
	private String name;
	private String info;
	private String title;
	private OrganizationLevel level;
	private Boolean showHierarchy = Boolean.FALSE;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public OrganizationLevel getLevel() {
		return level;
	}

	public void setLevel(OrganizationLevel level) {
		this.level = level;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Boolean getShowHierarchy() {
		return showHierarchy;
	}

	public void setShowHierarchy(Boolean showHierarchy) {
		this.showHierarchy = showHierarchy;
	}
	
}
