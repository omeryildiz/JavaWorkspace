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

import com.ut.tekir.entities.BankToContactTransfer;
import com.ut.tekir.util.Utils;

/**
 * @author sinan.yumak
 *
 */
@Stateful
@Name("bankToContactSuggestion")
@Scope(ScopeType.CONVERSATION)
@AutoCreate
public class BankToContactSuggestionBean implements BankToContactSuggestion {

	@Logger
	Log log;
	@In
	EntityManager entityManager;

	private BankToContactSuggestionFilterModel filterModel;
	
	@SuppressWarnings("unchecked")
	private List<BankToContactTransfer> suggest() {
		List<BankToContactTransfer> result = new ArrayList<BankToContactTransfer>();
		try {
			Query query = entityManager.createQuery( criteriaForBankToContactTransfer() );

			setParameters(query);
			
			result = query.getResultList();
		} catch (Exception e) {
			log.error("Cari transferleri sorgulanırken hata meydana geldi. Sebebi #0", e);
		}
		return result;
	}

	private String criteriaForBankToContactTransfer() {
		StringBuilder query = new StringBuilder();
		
		query.append("select btc from BankToContactTransfer btc where " +
				     "btc.active = true ");

		if (filterModel.getContact() != null) query.append("and btc.contact=:contact ");
		if (filterModel.getAction() != null) query.append("and btc.action=:action ");
		if (filterModel.getBeginDate() != null) query.append("and btc.date >=:beginDate ");
		if (filterModel.getEndDate() != null) query.append("and btc.date <=:endDate ");
		if (filterModel.getMatchingFinished() != null) query.append("and btc.matchingFinished=:matchingFinished ");
		if (Utils.isNotEmpty(filterModel.getReference())) query.append("and btc.reference like :reference ");
		if (Utils.isNotEmpty(filterModel.getSerial())) query.append("and btc.serial like :serial ");

		query.append("order by btc.date asc");

		return query.toString();
	}

	private void setParameters(Query query) {
		if (filterModel.getContact() != null) query.setParameter("contact",filterModel.getContact());
		if (filterModel.getAction() != null) query.setParameter("action",filterModel.getAction());
		if (filterModel.getBeginDate() != null) query.setParameter("beginDate",filterModel.getBeginDate());
		if (filterModel.getEndDate() != null) query.setParameter("endDate",filterModel.getEndDate());
		if (filterModel.getMatchingFinished() != null) query.setParameter("matchingFinished",filterModel.getMatchingFinished());
		if (Utils.isNotEmpty(filterModel.getReference())) query.setParameter("reference", filterModel.getReference());
		if (Utils.isNotEmpty(filterModel.getSerial())) query.setParameter("serial", filterModel.getSerial());
	}

	public List<BankToContactTransfer> suggest(BankToContactSuggestionFilterModel model) {
		setFilterModel(model);
		return suggest();
	}

	@Override
	public void setFilterModel(BankToContactSuggestionFilterModel filterModel) {
		this.filterModel = filterModel;
	}

	@Remove
    @Destroy
    public void destroy() {
    }


}
