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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;

import org.jboss.seam.Component;
import org.jboss.seam.log.Log;
import org.jboss.seam.log.Logging;

import com.ut.tekir.docmatch.annotation.MatchProvider;
import com.ut.tekir.entities.DocumentMatch;
import com.ut.tekir.entities.DocumentType;
import com.ut.tekir.entities.FinanceAction;
import com.ut.tekir.entities.MoneySet;
import com.ut.tekir.entities.PaymentItem;
import com.ut.tekir.entities.TradeAction;
import com.ut.tekir.entities.inv.TekirInvoice;

/**
 * @author sinan.yumak
 *
 */
@MatchProvider(types={DocumentType.Payment,DocumentType.Collection})
public class PaymentDocumentMatchProvider extends DocumentMatchProviderBase {
	private static final long serialVersionUID = 1L;

	private Log log = Logging.getLog(PaymentDocumentMatchProvider.class);
	private EntityManager entityManager;

	private List<DocumentMatch> deletedMatches;
	
	public DocumentMatchMetaModel getMatchMetaData(Object doc) {
		PaymentItem paymentItem = (PaymentItem) doc;
		DocumentMatchMetaModel model = new DocumentMatchMetaModel();
		model.setId(paymentItem.getId());
		model.setSerial(paymentItem.getOwner().getSerial());
		model.setType(paymentItem.getOwner().getDocumentType().equals(DocumentType.Collection) ? DocumentType.CollectionItem:DocumentType.PaymentItem);
		model.setAmount(paymentItem.getAmount());
		return model;
	}

	@Override
	public void deleteMatch(DocumentMatch dm) {
		getDeletedMatches().add(dm);
	}
	
	@Override
	public void deleteMatch(List<DocumentMatch> matches) {
		log.info("Deleting matches...");
		for (DocumentMatch dm : matches) {
			deleteMatch(dm);
		}

		matches.clear();
	}
	
	@Override
	public void delete(Object doc) {
		for (DocumentMatch dm : findMatches(doc)) {
			getEntityManager().remove(dm);
		}
		getEntityManager().flush();
	}

	@Override
	public void delete(List<DocumentMatchModel> matchModels) {
		for (DocumentMatchModel dmm : matchModels) {
			for (DocumentMatch dm : dmm.getMatches()) {
				getEntityManager().remove(getEntityManager().getReference(DocumentMatch.class, dm.getId()));
			}
		}
		getEntityManager().flush();
	
		setDocumentMatchingFinishedFlag(matchModels);
	}
	

	@Override
	public void save(Object actualDoc, List<DocumentMatch> matches) {
		//FIXME: implement me!
	}

	@Override
	public void save(List<DocumentMatchModel> matchModels) {
		log.info("Saving match models...");
		makeValidations(matchModels);

		saveMatches(matchModels);
	}

	private void makeValidations(List<DocumentMatchModel> matchModels) {
		log.info("Making validations...");
		//FIXME: acaba satırlar için validasyon yapmaya gerek var mı?
		for (DocumentMatchModel dmm : matchModels) {
			workBunchValidation(dmm);
		}
	}

	private void workBunchValidation(DocumentMatchModel dmm) {
		log.info("Making work bunch validations...");
		PaymentItem item = (PaymentItem) dmm.getActualDoc();
		for (DocumentMatch dm : dmm.getMatches()) {
			if (item.getWorkBunch() != null && !item.getWorkBunch().equals( getWorkBunch(dm) ))
				throw new RuntimeException("Satırdaki iş takip kodu ile eşlenen dökümanın iş takip kodu uyuşmuyor.");
		}
	}
	
	private boolean lineAmountExceedsMatchTotal(DocumentMatchModel dmm) {
		PaymentItem doc = (PaymentItem)dmm.getActualDoc();
		return doc.getAmount().getLocalAmount().compareTo(getMatchTotal(dmm)) == 1;
	}

	private void saveMatches(List<DocumentMatchModel> matchModels) {
		for (DocumentMatchModel dmm : matchModels) {
			for (DocumentMatch dm : dmm.getMatches()) {
				if (dm.getId() == null) {
					DocumentMatchMetaModel model = getMatchMetaData(dmm.getActualDoc());

					dm.setDocumentId( model.getId() );
					dm.setDocumentSerial( model.getSerial() );
					dm.setDocumentType( model.getType() );

					getEntityManager().persist(dm);
				} else {
					getEntityManager().merge(dm);
				}
				getEntityManager().flush();
			}
			setLineMatchingFinishedFlag(dmm);
		}
		removeDeletedMatches();

		setDocumentMatchingFinishedFlag(matchModels);
	}

	private void setDocumentMatchingFinishedFlag(List<DocumentMatchModel> matchModels) {
		List<DocumentMatchMetaModel> matchedDocs = getMatchedDocs(matchModels);
		
		for (DocumentMatchMetaModel model : matchedDocs) {
			if ( documentTotalExceedsMatchTotal(model) ) {
				setDocumentMatchingFinishedFlag(model, true);
			} else {
				setDocumentMatchingFinishedFlag(model, false);
			}
		}
	}

	private boolean documentTotalExceedsMatchTotal(DocumentMatchMetaModel model) {
		BigDecimal usedAmount = findUsedAmountInMatchesBefore(model);
		BigDecimal documentTotal = findMatchedDocumentTotal(model);
		return documentTotal.compareTo(usedAmount) <= 0;
	}

	private void setDocumentMatchingFinishedFlag(DocumentMatchMetaModel model, boolean matchingFinished) {
		log.info("Setting Document matching finished flag with doc: #0, flag: #1", model, matchingFinished);
		getEntityManager().createQuery("update " + DocumentTypeTableMatch.getTableName(model) + " ent" +
									   " set ent.matchingFinished = :matchingFinished where " + 
									   " ent.id=:id")
									   .setParameter("id", model.getId())
									   .setParameter("matchingFinished", matchingFinished)
									   .executeUpdate();
	}
	
	/**
	 * Belge içerisinde yer alan eşlemeleri gruplayarak bir listeye doldurur.
	 * 
	 * @param matchModels
	 * @return 
	 */
	private List<DocumentMatchMetaModel> getMatchedDocs(List<DocumentMatchModel> matchModels) {
		List<DocumentMatchMetaModel> matchedDocs = new ArrayList<DocumentMatchMetaModel>();

		for (DocumentMatchModel dmm : matchModels) {
			for (DocumentMatch dm : dmm.getMatches()) {
				DocumentMatchMetaModel metaModel = new DocumentMatchMetaModel();
				metaModel.setId( dm.getMatchedDocumentId() );
				metaModel.setSerial( dm.getMatchedDocumentSerial() );
				metaModel.setType( dm.getMatchedDocumentType() );

				if (!matchedDocs.contains(metaModel)) matchedDocs.add(metaModel);
			}
		}
		return matchedDocs;
	}

	private void setLineMatchingFinishedFlag(DocumentMatchModel dmm) {
		PaymentItem item = (PaymentItem)dmm.getActualDoc();
		if (lineAmountExceedsMatchTotal(dmm))  {
			item.setMatchingFinished(false);
		} else {
			item.setMatchingFinished(true);
		}
	}

	private void removeDeletedMatches() {
		for (DocumentMatch dm : getDeletedMatches()) {
			getEntityManager().remove(dm);
		}
		getEntityManager().flush();
	}

	public List<DocumentMatch> findMatches(Object doc) {
		DocumentMatchMetaModel model = getMatchMetaData(doc);
		log.info("Finding matches for #0", model);
		return DocumentMatchHelper.findMatches(model);
	}
	
	@Override
	public DocumentMatchFilterModel getDocumentMatchPopupFilters(Object doc) {
		PaymentItem paymentItem = (PaymentItem) doc;
		
		DocumentMatchFilterModel model = new DocumentMatchFilterModel();
		if (paymentItem.getOwner().getContact() != null) {
			model.setContact(paymentItem.getOwner().getContact());
			model.setDisableContactSelect(Boolean.TRUE);
		}
		if (FinanceAction.Credit.equals(paymentItem.getOwner().getAction())) {
			model.setAction( TradeAction.Sale);
		} else {
			model.setAction( TradeAction.Purchase);
		}
		model.setWorkBunch(paymentItem.getWorkBunch());
		model.setProcessType(paymentItem.getOwner().getProcessType());

		return model;
	}

	@Override
	public DocumentMatch createDocumentMatch(DocumentMatchResultModel model) {
		DocumentMatch match = new DocumentMatch();
		match.setDocumentSerial(model.getSerial());
		match.setDocumentType(model.getDocumentType());
		match.setMatchedDocumentSerial(model.getSerial());
        match.setMatchedDocumentId(model.getId());
		match.setMatchedDocumentType(model.getDocumentType());
		match.setAmount( new MoneySet(model.getRemainingAmount(), model.getCurrency()) );
		match.setMatchDate(new Date());
		return match;
	}
	
	protected EntityManager getEntityManager() {
		if (entityManager == null || !entityManager.isOpen()) {
			entityManager = (EntityManager)Component.getInstance("entityManager");
		}
		return entityManager;
	}

	public List<DocumentMatch> getDeletedMatches() {
		if (deletedMatches == null) {
			deletedMatches = new ArrayList<DocumentMatch>();
		}
		return deletedMatches;
	}

	public void setDeletedMatches(List<DocumentMatch> deletedMatches) {
		this.deletedMatches = deletedMatches;
	}

}
