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

package com.ut.tekir.docmatch;

import java.util.ArrayList;
import java.util.List;

import com.ut.tekir.entities.DocumentMatch;

/**
 * Eşleme yapılacak dökümanı ve eşlemelerini tutan model sınıfıdır.
 * 
 * @author sinan.yumak
 *
 */
public class DocumentMatchModel {

	private Object actualDoc;
	private List<DocumentMatch> matches;

	public DocumentMatchModel() {
	}
	
	public DocumentMatchModel(Object actualDoc) {
		this.actualDoc = actualDoc;
	}

	public DocumentMatchModel(Object actualDoc, List<DocumentMatch> matches) {
		this.actualDoc = actualDoc;
		this.matches = matches;
	}
	
	public Object getActualDoc() {
		return actualDoc;
	}

	public void setActualDoc(Object actualDoc) {
		this.actualDoc = actualDoc;
	}

	public List<DocumentMatch> getMatches() {
		if (matches == null) {
			matches = new ArrayList<DocumentMatch>();
		}
		return matches;
	}

	public void setMatches(List<DocumentMatch> matches) {
		this.matches = matches;
	}
}
