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

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;

import org.jboss.seam.Component;
import org.jboss.seam.log.Log;
import org.jboss.seam.log.Logging;

import com.ut.tekir.docmatch.annotation.MatchProvider;
import com.ut.tekir.entities.DocumentMatch;
import com.ut.tekir.entities.DocumentType;
import com.ut.tekir.entities.MoneySet;
import com.ut.tekir.entities.inv.TekirInvoice;

/**
 * @author sinan.yumak
 *
 */
@MatchProvider(types={DocumentType.SaleInvoice,DocumentType.SaleShipmentInvoice,DocumentType.PurchaseInvoice,
					  DocumentType.PurchaseShipmentInvoice})
public class InvoiceDocumentMatchProvider extends DocumentMatchProviderBase {
	private static final long serialVersionUID = 1L;
	private Log log = Logging.getLog(PaymentDocumentMatchProvider.class);
	private EntityManager entityManager;

	@Override
	public DocumentMatch createDocumentMatch(DocumentMatchResultModel model) {
		DocumentMatch match = new DocumentMatch();
		match.setDocumentSerial(model.getSerial());
		match.setDocumentType(model.getDocumentType());
		match.setMatchedDocumentSerial(model.getSerial());
        match.setMatchedDocumentId(model.getId());
		match.setMatchedDocumentType(model.getDocumentType());
		match.setAmount(new MoneySet(model.getRemainingAmount()));
		match.setMatchDate(new Date());
		return match;
	}

	@Override
	public void deleteMatch(DocumentMatch dm) {
		getEntityManager().remove(dm);
		getEntityManager().flush();
	}

	@Override
	public void deleteMatch(List<DocumentMatch> matches) {
		log.info("Deleting matches...");
		for (DocumentMatch dm : matches) {
			deleteMatch(dm);
		}
	}

	@Override
	public void delete(List<DocumentMatchModel> matchModels) {
		//FIXME: implement me!
	}

	@Override
	public void delete(Object doc) {
		deleteMatch( findMatches(doc) );
	}
	
	@Override
	public List<DocumentMatch> findMatches(Object doc) {
		DocumentMatchMetaModel model = getMatchMetaData(doc);
		log.info("Finding matches for #0", model);
		return DocumentMatchHelper.findMatches(model);
	}

	@Override
	public DocumentMatchFilterModel getDocumentMatchPopupFilters(Object doc) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DocumentMatchMetaModel getMatchMetaData(Object doc) {
		TekirInvoice invoice = (TekirInvoice) doc;
		DocumentMatchMetaModel model = new DocumentMatchMetaModel();
		model.setId(invoice.getId());
		model.setSerial(invoice.getSerial());
		model.setType(invoice.getDocumentType());
		model.setAmount(invoice.getGrandTotal());
		return model;
	}

	@Override
	public void save(List<DocumentMatchModel> matchModels) {
		saveMatches(matchModels);
	}
	
	@Override
	public void save(Object actualDoc, List<DocumentMatch> matches) {
		saveMatch(actualDoc, matches);
	}

	private void saveMatch(Object actualDoc, List<DocumentMatch> matches) {
		for (DocumentMatch dm : matches) {
			if (dm.getId() == null) {
				DocumentMatchMetaModel model = getMatchMetaData(actualDoc);

				dm.setDocumentId( model.getId() );
				dm.setDocumentSerial( model.getSerial() );
				dm.setDocumentType( model.getType() );
			}
			getEntityManager().persist(dm);
			getEntityManager().flush();
		}
	}
	
	private void saveMatches(List<DocumentMatchModel> matchModels) {
		for (DocumentMatchModel dmm : matchModels) {
			saveMatch(dmm.getActualDoc(), dmm.getMatches());
		}
	}

	private EntityManager getEntityManager() {
		if (entityManager == null || !entityManager.isOpen()) {
			entityManager = (EntityManager)Component.getInstance("entityManager");
		}
		return entityManager;
	}

}
