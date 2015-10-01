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

import com.google.common.base.Strings;
import com.ut.tekir.entities.Payment;
import com.ut.tekir.entities.PaymentItem;

/**
 * @author sinan.yumak
 *
 */
@Stateful
@Name("paymentSuggestion")
@Scope(ScopeType.CONVERSATION)
@AutoCreate
public class PaymentSuggestionBean implements PaymentSuggestion {

	@Logger
	Log log;
	@In
	EntityManager entityManager;

	private PaymentSuggestionFilterModel filterModel;
	
	@Override
	@SuppressWarnings("unchecked")
	public List<Payment> suggestPayment() {
		try {
			Query query = entityManager.createQuery( criteriaForPayment() );

			setParameters(query);
			
			return query.getResultList();
		} catch (Exception e) {
			log.error("Faturalar sorgulanırken hata meydana geldi. Sebebi #0", e);
		}
		return new ArrayList<Payment>();
	}

	@SuppressWarnings("unchecked")
	private List<PaymentItem> suggestPaymentItem() {
		try {
			Query query = entityManager.createQuery( criteriaForPaymentItem() );
			
			setParameters(query);
			
			return query.getResultList();
		} catch (Exception e) {
			log.error("Faturalar sorgulanırken hata meydana geldi. Sebebi #0", e);
		}
		return new ArrayList<PaymentItem>();
	}

	private void setParameters(Query query) {
		if (filterModel.getContact() != null) query.setParameter("contact",filterModel.getContact());
		if (filterModel.getAction() != null) query.setParameter("action",filterModel.getAction());
		if (filterModel.getBeginDate() != null) query.setParameter("beginDate",filterModel.getBeginDate());
		if (filterModel.getEndDate() != null) query.setParameter("endDate",filterModel.getEndDate());
		if (filterModel.getProcessType() != null) query.setParameter("processType",filterModel.getProcessType());
		if (!Strings.isNullOrEmpty(filterModel.getSerial())) query.setParameter("serial",filterModel.getSerial());
		if (!Strings.isNullOrEmpty(filterModel.getReference())) query.setParameter("reference",filterModel.getReference());
		if (filterModel.getMatchingFinished() != null) query.setParameter("matchingFinished",filterModel.getMatchingFinished());
	}
	
	private String criteriaForPayment() {
		StringBuilder query = new StringBuilder();
		
		query.append("select distinct p from Payment p " +
					 "inner join p.items as its where " +
					 "p.active = true ");

		if (filterModel.getContact() != null) query.append("and p.contact=:contact ");
		if (filterModel.getAction() != null) query.append("and p.action=:action ");
		if (filterModel.getBeginDate() != null) query.append("and p.date >=:beginDate ");
		if (filterModel.getEndDate() != null) query.append("and p.date <=:endDate ");
		if (!Strings.isNullOrEmpty(filterModel.getSerial())) query.append("and p.serial like :serial");
		if (!Strings.isNullOrEmpty(filterModel.getReference())) query.append("and p.reference like :reference");
		if (filterModel.getMatchingFinished() != null) query.append("and its.matchingFinished=:matchingFinished ");
	
		query.append("order by p.date asc");

		return query.toString();
	}

	private String criteriaForPaymentItem() {
		StringBuilder query = new StringBuilder();
		
		query.append("select p from PaymentItem p where " +
					 "p.owner.active = true ");
		
		if (filterModel.getContact() != null) query.append("and p.owner.contact=:contact ");
		if (filterModel.getAction() != null) query.append("and p.owner.action=:action ");
		if (filterModel.getBeginDate() != null) query.append("and p.owner.date >=:beginDate ");
		if (filterModel.getEndDate() != null) query.append("and p.owner.date <=:endDate ");
		if (filterModel.getProcessType() != null) query.append("and p.owner.processType=:processType ");
		if (!Strings.isNullOrEmpty(filterModel.getSerial())) query.append("and p.owner.serial like :serial");
		if (!Strings.isNullOrEmpty(filterModel.getReference())) query.append("and p.owner.reference like :reference");
		if (filterModel.getMatchingFinished() != null) query.append("and p.matchingFinished=:matchingFinished ");

		query.append("order by p.owner.date asc");
		
		return query.toString();
	}

	public List<Payment> suggestPayment(PaymentSuggestionFilterModel model) {
		setFilterModel(model);
		return suggestPayment();
	}

	public List<PaymentItem> suggestPaymentItem(PaymentSuggestionFilterModel model) {
		setFilterModel(model);
		return suggestPaymentItem();
	}
	
    @Remove
    @Destroy
    public void destroy() {
    }

	public void setFilterModel(PaymentSuggestionFilterModel filterModel) {
		this.filterModel = filterModel;
	}

}
