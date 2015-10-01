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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import org.jboss.seam.Component;
import org.jboss.seam.log.Log;
import org.jboss.seam.log.Logging;

import com.ut.tekir.entities.ChequeStatus;
import com.ut.tekir.entities.Contact;
import com.ut.tekir.framework.BaseConsts;


//TODO: eksiklikleri implemente etmek gerek.

/**
 * Cari limit kontrolünün yapılacağı sınıftır.
 * 
 * @author sinan.yumak
 *
 */
public class ContactLimitController {
	private enum LimitType {RISK,CREDIT};
	private enum PaperType {Cheque, PromissoryNote};
	
	private Log log = Logging.getLog(ContactLimitController.class);
	private EntityManager entityManager;
	private Map<Object,String> messages;

	public void checkLimits(Contact contact, BigDecimal operationAmt) {
		checkCreditLimit(contact, operationAmt);
		checkRiskLimit(contact, operationAmt);
	}
	
	public void checkCreditLimit(Contact contact, BigDecimal operationAmt) {
		check(contact, operationAmt, LimitType.CREDIT);
	}

	public void checkRiskLimit(Contact contact, BigDecimal operationAmt) {
		check(contact, operationAmt, LimitType.RISK);
	}
	
	private void check(Contact contact, BigDecimal operationAmt, LimitType limitType) {
		log.info("Checking #0 limit with contact :#1, amount: #2", limitType, contact.getCaption(), operationAmt);
		if ( totalAmountExceedsLimit(contact, operationAmt, limitType) ) {
			throw new RuntimeException( getExceptionMessage(contact, operationAmt, limitType));
		}
	}
	
	private boolean totalAmountExceedsLimit(Contact contact, BigDecimal operationAmt, LimitType limitType) {
		BigDecimal totalBalance = getTotalBalance(contact, limitType);
		BigDecimal contactLimit = getContactLimit(contact, limitType);
		return totalBalance.add(operationAmt).compareTo(contactLimit) == 1;
	}
	
	private BigDecimal getContactLimit(Contact contact, LimitType limitType) {
		return limitType.equals(LimitType.CREDIT) ? contact.getDebitLimit() : contact.getRiskLimit();
	}
	
	private String getExceptionMessage(Contact contact, BigDecimal operationAmt, LimitType limitType) {
		StringBuilder result = new StringBuilder();

		result.append(getContactLimit(contact, limitType));
		result.append(" " + BaseConsts.SYSTEM_CURRENCY_CODE);
		if (LimitType.CREDIT.equals(limitType)) {
			result.append(" olan kredi limiti aşıldı.");
		} else {
			result.append(" olan risk limiti aşıldı.");
		}
		return result.toString();
	}
	
	private BigDecimal getTotalBalance(Contact contact, LimitType limitType) {
		if (LimitType.RISK.equals(limitType)) return getFinanceTxnBalance(contact).add(getPromissoryBalance(contact))
																				  .add(getChequeBalance(contact));

		return getFinanceTxnBalance(contact);
	}
	
	private BigDecimal getFinanceTxnBalance(Contact contact) {
		//TODO:ileride tarih aralığı koymak gerekebilir.
		log.info("Querying finance txn balance with contact #0", contact.getCaption());
		BigDecimal result = BigDecimal.ZERO;
		try {
			result = (BigDecimal)getEntityManager().createQuery("select sum(case ft.action when 0 then ft.amount.localAmount " +
															  "when 1 then -ft.amount.localAmount end ) from FinanceTxn ft where " +
															  "ft.contact.id =:contactId and " +
															  "ft.active = true")
															  .setParameter("contactId", contact.getId())
															  .getSingleResult();

			if (result == null) {
				return BigDecimal.ZERO;
			}
			log.info("Finance txn balance: #0", result);
		} catch (Exception e) {
			log.info("Cari borç bakiyesi sorgulanırken hata meydana geldi! Sebebi #0", e);
		}
		return result;
	}
	
	private BigDecimal getPromissoryBalance(Contact contact) {
		return queryPaper(contact, PaperType.PromissoryNote);
	}

	private BigDecimal getChequeBalance(Contact contact) {
		return queryPaper(contact, PaperType.Cheque);
	}

	private BigDecimal queryPaper(Contact contact, PaperType paperType) {
		log.info("Querying #0 balance with contact #1", paperType, contact.getCaption());
		BigDecimal result = null;
		try {
			result = (BigDecimal)getEntityManager().createQuery("select sum(p.money.localAmount) from " +
																paperType.name() + " p where " +
																"p.contact.id =:contactId and " +
																"p.lastStatus not in (:statusList)")
																.setParameter("contactId", contact.getId())
																.setParameter("statusList", statusList())
																.getSingleResult();

			if (result == null) {
				result = BigDecimal.ZERO;
			}
			log.info("#0 balance: #1", paperType, result);
		} catch (Exception e) {
			log.info("#0 bakiyesi sorgulanırken hata meydana geldi! Sebebi #1", paperType, e);
		}
		return result;
	}
	
	private List<ChequeStatus> statusList() {
		List<ChequeStatus> statusList = new ArrayList<ChequeStatus>();
		statusList.add(ChequeStatus.BankaTahsilEdildi);
		statusList.add(ChequeStatus.Portfoy);
		statusList.add(ChequeStatus.KasaOdeme);
		statusList.add(ChequeStatus.KasaTahsilat);
		statusList.add(ChequeStatus.BankaOdeme);
		return statusList;
	}
	
	public EntityManager getEntityManager() {
		if (entityManager == null || !entityManager.isOpen()) {
			entityManager = (EntityManager)Component.getInstance("entityManager");
		}
		return entityManager;
	}
	
	@SuppressWarnings("unchecked")
	public Map<Object, String> getMessages() {
		if (messages == null) {
			messages = (Map<Object, String>)Component.getInstance("org.jboss.seam.international.messages");
		}
		return messages;
	}
	
	public static ContactLimitController instance() {
		return new ContactLimitController();
	}
	
}
