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
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.jboss.seam.Component;
import org.jboss.seam.log.Log;
import org.jboss.seam.log.Logging;

import com.ut.tekir.entities.DocumentMatch;
import com.ut.tekir.entities.DocumentType;
import com.ut.tekir.entities.WorkBunch;


/**
 * Match provider sınıflarında ortak olarak kullanılan işlevleri barındırır.
 * 
 * @author sinan.yumak
 *
 */
public class DocumentMatchHelper implements Serializable {
	private static final long serialVersionUID = 1L;

	private static Log log = Logging.getLog(DocumentMatchHelper.class);
	private static EntityManager entityManager;

	public static BigDecimal findUsedAmountInMatchesBefore(DocumentMatchMetaModel model) {
		log.info("Finding used amount in matches before for #0", model);
		BigDecimal result = BigDecimal.ZERO;
		try {
			result = (BigDecimal)getEntityManager().createQuery("select sum(m.amount.value) from DocumentMatch m where " +
														   "m.matchedDocumentSerial =:documentSerial and " +
														   "m.matchedDocumentId =:documentId and " +
														   "m.matchedDocumentType =:documentType ")
														   .setParameter("documentSerial", model.getSerial())
														   .setParameter("documentType", model.getType())
														   .setParameter("documentId", model.getId())
														   .getSingleResult();
			
			if (result == null) result = BigDecimal.ZERO;
		} catch (Exception e) {
			log.error("#0 tipindeki belgenin kullanılan miktarı sorgulanırken hata meydana geldi. Sebebi #1", model.getType(), e);
		}
		log.info("Found used amount: #0", result);
		return result;
	}

	@SuppressWarnings("unchecked")
	public static BigDecimal findMatchedDocumentTotal(DocumentMatchMetaModel model) {
		//FIXME: şuan fix olarak fatura kabul ettik. Bunu jenerik bi hale getirmeli.
		log.info("Finding matched document total for: #0", model);
		BigDecimal result = BigDecimal.ZERO;
		try {
			List<BigDecimal>  grandTotals = getEntityManager().createQuery("select doc." + DocumentTypeTableMatch.getTotalColumnName(model) + ".value " +
																		   "from " + DocumentTypeTableMatch.getTableName(model) + " doc where " +
																           "doc.id =:docId")
																          .setParameter("docId", model.getId())
																          .getResultList();
			
			if (grandTotals != null && grandTotals.size() == 1) {
				result = (BigDecimal)grandTotals.get(0);
			}
		} catch (Exception e) {
			log.error("#0 id li faturanın toplam tutarı sorgulanırken hata meydana geldi.", model.getId());
		}
		log.info("Found document total: #0", result);
		return result;
	}

	@SuppressWarnings("unchecked")
	public static List<DocumentMatch> findMatches(DocumentMatchMetaModel model) {
		List<DocumentMatch> result = new ArrayList<DocumentMatch>();
		try {
			result = getEntityManager().createQuery("select dm from DocumentMatch dm where " +
											   	  "dm.documentId=:documentId and " +
											   	  "dm.documentSerial=:documentSerial and " +
											      "dm.documentType=:documentType")
											      .setParameter("documentId", model.getId())
											      .setParameter("documentType", model.getType())
											      .setParameter("documentSerial", model.getSerial())
											      .getResultList();
		} catch (Exception e) {
			log.error("#0 için eşlemeler bulunurken hata meydana geldi. Sebebi #1", model, e);
		}
		return result;
	}
	
	public static BigDecimal getMatchTotal(DocumentMatchMetaModel model) {
		return getMatchTotal(findMatches(model));
	}
	
	public static BigDecimal getMatchTotal(DocumentMatchModel dmm) {
		return getMatchTotal(dmm.getMatches());
	}

	public static BigDecimal getMatchTotal(List<DocumentMatch> matches) {
		BigDecimal total = BigDecimal.ZERO;
		for (DocumentMatch item : matches) {
			total = total.add(item.getAmount().getLocalAmount());
		}
		return total.setScale(2, RoundingMode.HALF_UP);
	}
	
	@SuppressWarnings("unchecked")
	public static WorkBunch getWorkBunch(Long documentId, DocumentType type) {
		log.info("Querying workbunch with document id: #0, document type: #1", documentId, type);
		try {
			List<WorkBunch> workBunchs = getEntityManager().createQuery("select ent.workBunch from " + DocumentTypeTableMatch.getTableName(type)+ " ent where " +
																	  "ent.id=:id")
																	  .setParameter("id", documentId)
																	  .getResultList();
			if (workBunchs != null && workBunchs.size() == 1) {
				return workBunchs.get(0);
			}
		} catch (Exception e) {
			log.error("İş takibi sorgulanırken hata meydana geldi. Sebebi #0", e);
		}
		return null;
	}

	private static EntityManager getEntityManager() {
		if (entityManager == null || !entityManager.isOpen()) {
			entityManager = (EntityManager)Component.getInstance("entityManager");
		}
		return entityManager;
	}

}
