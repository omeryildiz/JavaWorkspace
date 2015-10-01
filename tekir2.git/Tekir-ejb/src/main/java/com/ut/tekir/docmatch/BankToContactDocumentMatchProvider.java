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

import java.util.List;

import javax.persistence.EntityManager;

import org.jboss.seam.Component;
import org.jboss.seam.log.Log;
import org.jboss.seam.log.Logging;

import com.ut.tekir.docmatch.annotation.MatchProvider;
import com.ut.tekir.entities.BankToContactTransfer;
import com.ut.tekir.entities.DocumentMatch;
import com.ut.tekir.entities.DocumentType;

/**
 * @author sinan.yumak
 *
 */
@MatchProvider(types={DocumentType.BankToContactTransfer, DocumentType.ContactToBankTransfer})
public class BankToContactDocumentMatchProvider extends DocumentMatchProviderBase {
	private static final long serialVersionUID = 1L;

	private Log log = Logging.getLog(BankToContactDocumentMatchProvider.class);
	private EntityManager entityManager;

	@Override
	public DocumentMatch createDocumentMatch(DocumentMatchResultModel model) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void delete(Object doc) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void delete(List<DocumentMatchModel> matchModels) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void deleteMatch(DocumentMatch dm) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void deleteMatch(List<DocumentMatch> matches) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public List<DocumentMatch> findMatches(Object doc) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public DocumentMatchFilterModel getDocumentMatchPopupFilters(Object doc) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public DocumentMatchMetaModel getMatchMetaData(Object doc) {
		BankToContactTransfer btc = (BankToContactTransfer)doc;
		DocumentMatchMetaModel model = new DocumentMatchMetaModel();
		model.setId(btc.getId());
		model.setSerial(btc.getSerial());
		model.setType(btc.getDocumentType());
		model.setAmount(btc.getAmount());
		return model;
	}


	@Override
	public void save(List<DocumentMatchModel> matchModels) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void save(Object actualDoc, List<DocumentMatch> matches) {
		// TODO Auto-generated method stub
		
	}

	
	protected EntityManager getEntityManager() {
		if (entityManager == null || !entityManager.isOpen()) {
			entityManager = (EntityManager)Component.getInstance("entityManager");
		}
		return entityManager;
	}

}
