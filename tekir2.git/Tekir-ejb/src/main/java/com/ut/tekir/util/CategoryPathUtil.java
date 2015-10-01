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

package com.ut.tekir.util;

import java.util.StringTokenizer;

import com.ut.tekir.entities.Category;

/**
 * @author sinan.yumak
 *
 */
public class CategoryPathUtil {
	private StringBuilder catPath;
	
	private StringBuilder codePath;

	public void setCategoryPath(Category cat) {
		iterateCategory(cat);
		cat.setPath(getPath().toString());
	}
	
	private void iterateCategory(Category cat) {
		appendPath(cat);
		if (cat.getParent() != null) iterateCategory(cat.getParent());
	}

	private void appendPath(Category cat) {
		getPath().insert(0, cat.getId());
		getPath().insert(0,"/");
	}
	
	private StringBuilder getPath(){
		if (catPath == null) {
			catPath = new StringBuilder();
		}
		return catPath;
	}

	public static CategoryPathUtil instance() {
		return new CategoryPathUtil();
	}

	public static void main(String[] args) {
		
		String harala = "ali veli 49 50";
		StringTokenizer st = new StringTokenizer(harala);
		int alpha = 3;
//		while (st.hasMoreTokens()){
//			System.out.println(st.nextToken() + "|");
//		}
//		System.out.println("********************");
		StringBuilder sb = new StringBuilder();
		
		if (st.countTokens() > alpha){
			for(int i = 0; i < alpha; i++){
				sb.append(st.nextToken() + " ");
			}
			sb.append("...");
		}
		System.out.println(sb.toString());
		
		
//		ContactCategory cat1 = new ContactCategory();
//		cat1.setCode("cat1");
//		
//		ContactCategory cat2 = new ContactCategory();
//		cat2.setCode("cat2");
//		cat2.setParent(cat1);
//		
//		ContactCategory cat3 = new ContactCategory();
//		cat3.setCode("cat3");
//		cat3.setParent(cat2);
//		
//		CategoryPathUtil.instance().setCategoryPath(cat1);
//		
//		System.out.println(cat1.getPath());
	}

	public String generateCodePath(Category cat){
		if (cat == null){
			return "";
		}
		iterThruCat(cat);
		return getCodePath().toString();
	}
	
	private void iterThruCat(Category cat) {
		appendCodePath(cat);
		if (cat.getParent() != null) iterThruCat(cat.getParent());
	}

	private void appendCodePath(Category cat) {
		getCodePath().insert(0, cat.getCode());
		getCodePath().insert(0, "/");
		
	}

	private StringBuilder getCodePath(){
		if (codePath == null) {
			codePath = new StringBuilder();
		}
		return codePath;
	}

}
