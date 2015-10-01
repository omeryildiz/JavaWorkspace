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

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import com.ut.tekir.entities.DocumentMatch;

/**
 * @author sinan.yumak
 *
 */
public interface DocumentMatchProvider extends Serializable {
	
	void save(List<DocumentMatchModel> matchModels);

	void save(Object actualDoc, List<DocumentMatch> matches);

	/**
	 * Verilen dökümanın eşlemelerini bulur ve siler.
	 * 
	 * @param doc
	 */
	void delete(Object doc);
	void delete(List<DocumentMatchModel> matchModels);
    
	void deleteMatch(DocumentMatch dm);

	void deleteMatch(List<DocumentMatch> matches);

	/**
	 * Verilen döküman için eşlemede kullanılabilecek meta bilgileri
	 * döndürür.
	 * 
	 * @param doc
	 * @return
	 */
	DocumentMatchMetaModel getMatchMetaData(Object doc);

	/**
	 * Verilen döküman için tüm eşlemeleri döndürür.
	 * @param doc
	 * @return matches
	 */
	List<DocumentMatch> findMatches(Object doc);

	/**
	 * Döküman eşleme popunda kullanılacak olan default filtreleri döndürür.
	 * 
	 * @return filtre modeli
	 */
    DocumentMatchFilterModel getDocumentMatchPopupFilters(Object doc);

    /**
     * Popup ta eşleme seçildikten sonra döküman eşlemesi oluşturur.
     * @param doc
     * @param model
     */
    DocumentMatch createDocumentMatch(DocumentMatchResultModel model);
    
    /**
     * Verilen bir dökümanın daha önce eşlemelerde ne kadarının kullanıldığı bilgisini 
     * döndürür.
     * @param model
     * @return
     */
    BigDecimal findUsedAmountInMatchesBefore(DocumentMatchMetaModel model);
    BigDecimal findUsedAmountInMatchesBefore(Object doc);
    
    BigDecimal getMatchTotal(Object doc);
	BigDecimal getMatchTotal(DocumentMatchModel dmm);
	BigDecimal getMatchTotal(List<DocumentMatch> matches);
	
}
