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

import java.math.BigDecimal;
import java.util.List;

import com.ut.tekir.entities.DocumentMatch;
import com.ut.tekir.entities.WorkBunch;

/**
 * Eşleme sağlayıcılarının ortak fonksiyonaliteleri için kullanılacak.
 * @author sinan.yumak
 *
 */
public abstract class DocumentMatchProviderBase implements DocumentMatchProvider {
	private static final long serialVersionUID = 1L;

	public BigDecimal getMatchTotal(DocumentMatchModel dmm) {
		return DocumentMatchHelper.getMatchTotal(dmm);
	}

	public BigDecimal getMatchTotal(List<DocumentMatch> matches) {
		return DocumentMatchHelper.getMatchTotal(matches);
	}

	public BigDecimal getMatchTotal(Object doc) {
		return DocumentMatchHelper.getMatchTotal(getMatchMetaData(doc));
	}
	
	public WorkBunch getWorkBunch(DocumentMatch dm) {
		return DocumentMatchHelper.getWorkBunch(dm.getMatchedDocumentId(),dm.getMatchedDocumentType());
	}

	public WorkBunch getWorkBunch(DocumentMatchMetaModel model) {
		return DocumentMatchHelper.getWorkBunch(model.getId(), model.getType());
	}

	public BigDecimal findUsedAmountInMatchesBefore(DocumentMatchMetaModel model) {
		return DocumentMatchHelper.findUsedAmountInMatchesBefore(model);
	}

	public BigDecimal findUsedAmountInMatchesBefore(Object doc) {
		return DocumentMatchHelper.findUsedAmountInMatchesBefore(getMatchMetaData(doc));
	}

	public BigDecimal findMatchedDocumentTotal(DocumentMatchMetaModel model) {
		return DocumentMatchHelper.findMatchedDocumentTotal(model);
	}

	public BigDecimal findMatchedDocumentTotal(Object doc) {
		return DocumentMatchHelper.findMatchedDocumentTotal(getMatchMetaData(doc));
	}

}
