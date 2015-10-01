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

package com.ut.tekir.finance;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.log.Log;

import com.ut.tekir.entities.inv.TekirInvoice;
import com.ut.tekir.invoice.yeni.InvoiceSuggestionFilterModel;
import com.ut.tekir.util.Utils;

/**
 * @author sinan.yumak
 *
 */
@Stateful
@Name("invoiceSuggestion")
@Scope(ScopeType.CONVERSATION)
@AutoCreate
public class InvoiceSuggestionBean implements InvoiceSuggestion {

	@Logger
	Log log;
	@In
	EntityManager entityManager;

	private InvoiceSuggestionFilterModel filterModel;
	
	@Override
	@SuppressWarnings("unchecked")
	public List<TekirInvoice> suggestInvoice() {
		try {
			StringBuffer queryString = new StringBuffer();

			queryString.append("select distinct inv from TekirInvoice inv " +
					"left outer join inv.items as its " +
					"where inv.active = true ");

			if (filterModel.getContact() != null) queryString.append("and inv.contact=:contact ");
			if (filterModel.getTradeAction() != null) queryString.append("and inv.tradeAction=:tradeAction ");
			if (filterModel.getRetInvoiceStatus() != null) queryString.append("and inv.returnInvoiceStatus in (:retInvoiceStatus) ");
			if (filterModel.getBeginDate() != null) queryString.append("and inv.date >=:beginDate ");
			if (filterModel.getEndDate() != null) queryString.append("and inv.date <=:endDate ");
			if (Utils.isNotEmpty(filterModel.getReference())) queryString.append("and inv.reference like :reference ");
			if (Utils.isNotEmpty(filterModel.getSerial())) queryString.append("and inv.serial like :serial ");
			if (filterModel.getProduct() != null) queryString.append("and its.product = :product ");
			if (filterModel.getMatchingFinished() != null) queryString.append("and inv.matchingFinished = :matchingFinished ");

			queryString.append("order by inv.date asc");
			
			Query query = entityManager.createQuery(queryString.toString());
			
			if (filterModel.getContact() != null) query.setParameter("contact", filterModel.getContact());
			if (filterModel.getTradeAction() != null) query.setParameter("tradeAction", filterModel.getTradeAction());
			if (filterModel.getRetInvoiceStatus() != null) query.setParameter("retInvoiceStatus", filterModel.getRetInvoiceStatus());
			if (filterModel.getBeginDate() != null) query.setParameter("beginDate", filterModel.getBeginDate());
			if (filterModel.getEndDate() != null) query.setParameter("endDate", filterModel.getEndDate());
			if (Utils.isNotEmpty(filterModel.getReference())) query.setParameter("reference", filterModel.getReference() + "%");
			if (Utils.isNotEmpty(filterModel.getSerial())) query.setParameter("serial", filterModel.getSerial() + "%");
			if (filterModel.getProduct() != null ) query.setParameter("product", filterModel.getProduct());
			if (filterModel.getMatchingFinished() != null ) query.setParameter("matchingFinished", filterModel.getMatchingFinished());
			
			return query.getResultList();
		} catch (Exception e) {
			log.error("Faturalar sorgulanırken hata meydana geldi. Sebebi #0", e);
		}
		return new ArrayList<TekirInvoice>();
	}

	public List<TekirInvoice> suggestInvoice(InvoiceSuggestionFilterModel model) {
		setFilterModel(model);
		return suggestInvoice();
	}
	
    @Remove
    @Destroy
    public void destroy() {
    }

	public void setFilterModel(InvoiceSuggestionFilterModel filterModel) {
		this.filterModel = filterModel;
	}

}
