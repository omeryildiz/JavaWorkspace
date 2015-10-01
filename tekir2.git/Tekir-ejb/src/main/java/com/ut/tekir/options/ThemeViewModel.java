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

package com.ut.tekir.options;

/**
 * @author sinan.yumak
 *
 */
public class ThemeViewModel {

	private String themeName;
	private boolean selected;
	
	public String getRowStyle() {
		if (selected) return "selectedRowStyle";
		return "";
	}
	
	public String getThemeName() {
		return themeName;
	}
	
	public void setThemeName(String themeName) {
		this.themeName = themeName;
	}
	
	public boolean isSelected() {
		return selected;
	}
	
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	
}
