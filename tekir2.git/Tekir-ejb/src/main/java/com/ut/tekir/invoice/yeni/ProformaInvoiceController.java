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

package com.ut.tekir.invoice.yeni;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.jboss.seam.Component;
import org.jboss.seam.log.Log;
import org.jboss.seam.log.Logging;

import com.ut.tekir.docmatch.DocumentMatchMetaModel;
import com.ut.tekir.docmatch.DocumentMatchProvider;
import com.ut.tekir.docmatch.DocumentMatchResultModel;
import com.ut.tekir.docmatch.MatchProviderRegistry;
import com.ut.tekir.entities.DocumentMatch;
import com.ut.tekir.entities.DocumentType;
import com.ut.tekir.entities.inv.ProformaStatus;
import com.ut.tekir.entities.inv.TekirInvoice;

/**
 * Proforma fatura ile ilgili işlerin yapılmasından sorumlu sınıftır.
 * 
 * @author sinan.yumak
 *
 */
public class ProformaInvoiceController implements Serializable {
	private static final long serialVersionUID = 1L;

	private Log log = Logging.getLog(ProformaInvoiceController.class);
	private EntityManager entityManager;
	private DocumentMatchProvider matchProvider;

	private DocumentMatch createdMatch;
	
	public TekirInvoice createInvoice(TekirInvoice inv) {
		log.info("Creating proforma invoice with invoice id: #0, code: #1", inv.getId(),inv.getSerial());
		
		TekirInvoice clonedInv = inv.clone();
		clonedInv.setDocumentType(DocumentType.SaleShipmentInvoice);
		clonedInv.setInfo(inv.getSerial() + " no'lu proforma faturadan üretilmiştir.");


		createdMatch = getMatchProvider().createDocumentMatch( getInvoiceAsResultModel(inv) );
		return clonedInv;
	}
	
	public void save(TekirInvoice inv) {
		saveMatch(inv);

		setProformaStatus(inv, ProformaStatus.Invoiced);
	}

	private void saveMatch(TekirInvoice invoice) {
		List<DocumentMatch> matches = getMatchProvider().findMatches(invoice);
		if (matches.size() == 0 && createdMatch != null) {
			List<DocumentMatch> match = new ArrayList<DocumentMatch>();
			match.add(createdMatch);
			getMatchProvider().save( invoice, match);
		} else {
			//FIXME: kayıt varsa ne yapmalı?
			//kayıt varsa, hiçbir şey yapma!
		}
	}
	
	private DocumentMatchResultModel getInvoiceAsResultModel(TekirInvoice invoice) {
		DocumentMatchResultModel model = new DocumentMatchResultModel();
		model.setSerial(invoice.getSerial());
		model.setDocumentType(invoice.getDocumentType());
		model.setId(invoice.getId());
		model.setValue(invoice.getGrandTotal().getLocalAmount());
		return model;
	}
	
	public void delete(TekirInvoice inv) {
		setProformaStatus(inv, ProformaStatus.Open);
		
		getMatchProvider().delete(inv);
	}
	
	private void setProformaStatus(Object actualDoc, ProformaStatus status) {
		List<DocumentMatch> matches = getMatchProvider().findMatches(actualDoc);
		if (matches != null && matches.size() != 0) {
			setStatus(new DocumentMatchMetaModel(matches.get(0).getMatchedDocumentId()), status);
		}
	}
	
	private void setStatus(DocumentMatchMetaModel model, ProformaStatus status) {
		getEntityManager().createQuery("update TekirInvoice inv set inv.proformaStatus= :proformaStatus where " +
									   "id=:id")
									   .setParameter("id", model.getId())
									   .setParameter("proformaStatus", status)
									   .executeUpdate();
	}
	
	public EntityManager getEntityManager() {
		if (entityManager == null || !entityManager.isOpen()) {
			entityManager = (EntityManager)Component.getInstance("entityManager");
		}
		return entityManager;
	}
	
	public static ProformaInvoiceController instance() {
		return new ProformaInvoiceController();
	}

	public DocumentMatchProvider getMatchProvider() {
		if (matchProvider == null) {
			matchProvider = MatchProviderRegistry.getProvider(DocumentType.SaleInvoice);
		}
		return matchProvider;
	}

}
