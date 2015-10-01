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

import com.ut.tekir.entities.Category;

/**
 * 
 * @author sinan.yumak
 *
 */
public class CategoryItem {
	private int index;//line index
	private int pindex;//parent index
	private Category cat;
	
	public CategoryItem() {
	}

	public CategoryItem(Category cat,int index,int pindex){
		this.cat = cat;
		this.index = index;
		this.pindex = pindex;
	}

	public String lineClass() {
		return cat.getParent() != null ? "child-of-node-"+getPindex():"";
	}
	
	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public int getPindex() {
		return pindex;
	}

	public void setPindex(int pindex) {
		this.pindex = pindex;
	}

	public Category getCat() {
		return cat;
	}

	public void setCat(Category cat) {
		this.cat = cat;
	}

	@Override
	public boolean equals(Object obj) {
		return this.cat.equals( ((CategoryItem)obj).getCat() );
	}

	public boolean equals(Category cat) {
		return this.cat.equals( cat );
	}
	
}
