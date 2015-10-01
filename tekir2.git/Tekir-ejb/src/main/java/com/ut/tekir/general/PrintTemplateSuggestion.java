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

import java.io.Serializable;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import com.ut.tekir.entities.DocumentType;
import com.ut.tekir.entities.PrintTemplates;

//TODO:filtre modeli zenginleştirilse iyi olur.
/**
 * @author sinan.yumak
 *
 */
@Name("printTemplateSuggestion")
@Scope(ScopeType.SESSION)
public class PrintTemplateSuggestion implements Serializable {
	private static final long serialVersionUID = 1L;

	@In
	private EntityManager entityManager;

	@SuppressWarnings("unchecked")
	public List<PrintTemplates> getTemplates(DocumentType documentType) {
		StringBuilder queryString = new StringBuilder();
		queryString.append("select pt from PrintTemplates pt where pt.active = true ");
		if (documentType != null) queryString.append("and pt.documentType =:documentType ");

		Query query = entityManager.createQuery(queryString.toString());
		
		if (documentType != null) query.setParameter("documentType", documentType);
		
		return query.getResultList();
	}

	public List<PrintTemplates> getTemplates() {
		return getTemplates(null);
	}

}
