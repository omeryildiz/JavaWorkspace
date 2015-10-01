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

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.log.Log;

import com.ut.tekir.entities.Bank;
import com.ut.tekir.entities.CardOwnerType;
import com.ut.tekir.entities.Foundation;
import com.ut.tekir.entities.PaymentContract;
import com.ut.tekir.entities.PaymentOwnerType;
import com.ut.tekir.entities.PaymentType;
import com.ut.tekir.entities.Pos;
import com.ut.tekir.entities.PosBankList;

/**
 * @author sinan.yumak
 *
 */
@Name("contractFinder")
@Scope(ScopeType.EVENT)
@AutoCreate
public class ContractFinderBean implements ContractFinder {
	private static final long serialVersionUID = 1L;

    @Logger
    private Log log;
    @In
    private EntityManager entityManager;

	@Override
	@SuppressWarnings("unchecked")
	public PaymentContract findContract(Foundation aFoundation, Date operationDate) {
		log.info("Finding contract for :{0}", aFoundation.getId());
		
		List<PaymentContract> contractList = null;
		PaymentContract result = null;
		
		contractList = entityManager.createQuery("select pc from PaymentContract pc where " +
								  "pc.active=true and " +
								  "pc.ownerId=:ownerId and " +
								  "pc.ownerType=:ownerType and " +
								  "pc.beginDate <=:operationDate and pc.endDate >=:operationDate " +
								  "order by pc.beginDate desc")
								  .setParameter("ownerId", aFoundation.getId())
								  .setParameter("ownerType", PaymentOwnerType.Foundation)
								  .setParameter("operationDate", operationDate)
								  .getResultList();
		
		if (contractList != null && contractList.size() >0 ) {
			result = contractList.get(0);
			log.info("Found contract code:{0}, name:{1}", result.getCode(),result.getName());
		} else {
			throw new RuntimeException(aFoundation.getName() + " kurumu için tanımlı sözleşme bulunamadı!");
		}
		return result;
	}

	@Override
	@SuppressWarnings("unchecked")
	public PaymentContract findContract(Pos aPos,PaymentType paymentType, Date operationDate, Bank bank) {
		log.info("Finding contract for pos:{0}, paymentType :{1}, operationDate: {2}, bank name:{3} ", aPos.getId(), paymentType, 
																									   operationDate, bank.getName());

		boolean isPartner  = false;
		for (PosBankList posBank : aPos.getPosBankList()) {
			if (posBank.getBank().getId().equals(bank.getId())) {
				isPartner = true;
				break;
			}
		}
		
		CardOwnerType cardOwnerType = CardOwnerType.International;

		if (isPartner) {
			cardOwnerType = CardOwnerType.Own;
		} else {
			cardOwnerType = CardOwnerType.Domestic;
		}
		
		List<PaymentContract> contractList = null;
		PaymentContract result = null;
		
		contractList = entityManager.createQuery("select pc from PaymentContract pc where " +
												 "pc.active=true and " +
												 "pc.paymentType=:paymentType and " +
												 "pc.ownerId=:ownerId and " +
												 "pc.ownerType=:ownerType and " +
												 "pc.cardOwnerType=:cardOwnerType and " +
												 "pc.beginDate <=:operationDate and pc.endDate >=:operationDate " +
												 "order by pc.beginDate desc")
												 .setParameter("ownerId", aPos.getId())
												 .setParameter("ownerType", PaymentOwnerType.Pos)
												 .setParameter("cardOwnerType", cardOwnerType)
												 .setParameter("paymentType", paymentType)
												 .setParameter("operationDate", operationDate)
												 .getResultList();

		if (contractList != null && contractList.size() >0 ) {
			result = contractList.get(0);
			log.info("Found contract code:{0}, name:{1}", result.getCode(),result.getName());
		} else {
			throw new RuntimeException(aPos.getName() + " posu için tanımlı sözleşme bulunamadı!");
		}
		return result;
	}
	
}
