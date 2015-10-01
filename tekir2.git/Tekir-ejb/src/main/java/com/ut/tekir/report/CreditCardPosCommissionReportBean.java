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

package com.ut.tekir.report;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateful;
import javax.persistence.Query;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.Conversation;

import com.ut.tekir.entities.BankToContactTransfer;
import com.ut.tekir.entities.BankTransferType;
import com.ut.tekir.entities.DocumentType;
import com.ut.tekir.entities.FinanceAction;
import com.ut.tekir.entities.MoneySet;
import com.ut.tekir.finance.ContactToBankTransferHome;
import com.ut.tekir.framework.BaseConsts;
import com.ut.tekir.framework.BrowserBase;
import com.ut.tekir.general.GeneralSuggestion;


/**
 * 
 * @author rustem.topcu
 */
@Stateful
@Name("creditCardPosCommissionReport")
@Scope(value=ScopeType.SESSION)
public class CreditCardPosCommissionReportBean extends BrowserBase<CreditCardPosCommisionViewModel, CreditCardPosCommissionFilterModel> implements CreditCardPosCommissionReport <CreditCardPosCommisionViewModel, CreditCardPosCommissionFilterModel>{

	@In
	GeneralSuggestion generalSuggestion;
	
	@Create
	@Begin(join=true, flushMode = org.jboss.seam.annotations.FlushModeType.MANUAL)
	public void init() {
		filterModel = new CreditCardPosCommissionFilterModel();
	}

	@Override
	@SuppressWarnings("unchecked")
	public void search() {
		if (entityList != null) {
			entityList.clear();
		} else {
			entityList = new ArrayList<CreditCardPosCommisionViewModel>();
		}
		
		StringBuilder firstInnerQuery = new StringBuilder();
		
		firstInnerQuery.append("select txn1.TXN_DATE, txn1.MATURITY_DATE, txn1.DOCUMENT_TYPE, txn1.DOCUMENT_ID, txn1.POS_ID, " +
							   "sum(txn1.LCYVAL * case when txn1.FINANCE_ACTION = 0 then 1 end ) as Satis, 0 as Kom, txn1.REPAID_STATUS, pos.BANK_ID as POS_BANK_ID " +
							   "from POS_TXN txn1 " +
							   "inner join POS pos on pos.ID = txn1.POS_ID " +
							   "where txn1.FINANCE_ACTION = 0 ");
		
		if (filterModel.getBeginDate() != null) {
			firstInnerQuery.append("and txn1.TXN_DATE >= :txnBeginDate ");
		}

		if (filterModel.getEndDate() != null) {
			firstInnerQuery.append("and txn1.TXN_DATE <= :txnEndDate ");
		}

		if (filterModel.getMaturityBeginDate() != null) {
			firstInnerQuery.append("and txn1.MATURITY_DATE >= :maturityBeginDate ");
		}
		
		if (filterModel.getMaturityEndDate() != null) {
			firstInnerQuery.append("and txn1.MATURITY_DATE <=:maturityEndDate ");
		}

		if (filterModel.getPos() != null) {
			firstInnerQuery.append("and txn1.POS_ID = :posId ");
		}

		if (filterModel.getRepaidStatus() != null) {
			firstInnerQuery.append("and txn1.REPAID_STATUS = :repaidStatus ");
		}

		if (filterModel.getBank() != null) {
			firstInnerQuery.append("and pos.BANK_ID = :bank ");
		}
		
		firstInnerQuery.append("group by txn1.TXN_DATE, txn1.MATURITY_DATE, txn1.DOCUMENT_TYPE, txn1.DOCUMENT_ID,  txn1.POS_ID, txn1.FINANCE_ACTION, txn1.REPAID_STATUS, pos.BANK_ID");

		log.debug("Created first inner query :{0}", firstInnerQuery.toString());
		
		
		StringBuilder secondInnerQuery = new StringBuilder();

		secondInnerQuery.append("select txn2.TXN_DATE, txn2.MATURITY_DATE, txn2.DOCUMENT_TYPE, txn2.DOCUMENT_ID,  txn2.POS_ID, 0 as Satis, " +
							    "sum(txn2.LCYVAL * case when txn2.FINANCE_ACTION = 1 then -1 end ) as Kom, txn2.REPAID_STATUS, pos.BANK_ID as POS_BANK_ID " +
							    "from POS_TXN txn2 " +
							    "inner join POS pos on pos.ID = txn2.POS_ID " +
							    "where txn2.FINANCE_ACTION = 1 ");

		if (filterModel.getBeginDate() != null) {
			secondInnerQuery.append("and txn2.TXN_DATE >= :txnBeginDate ");
		}
		
		if (filterModel.getEndDate() != null) {
			secondInnerQuery.append("and txn2.TXN_DATE <= :txnEndDate ");
		}
		
		if (filterModel.getMaturityBeginDate() != null) {
			secondInnerQuery.append("and txn2.MATURITY_DATE >= :maturityBeginDate ");
		}
		
		if (filterModel.getMaturityEndDate() != null) {
			secondInnerQuery.append("and txn2.MATURITY_DATE <=:maturityEndDate ");
		}
		
		if (filterModel.getPos() != null) {
			secondInnerQuery.append("and txn2.POS_ID = :posId ");
		}
		
		if (filterModel.getRepaidStatus() != null) {
			secondInnerQuery.append("and txn2.REPAID_STATUS = :repaidStatus ");
		}
		
		if (filterModel.getBank() != null) {
			secondInnerQuery.append("and pos.BANK_ID = :bank ");
		}

		secondInnerQuery.append("group by txn2.TXN_DATE, txn2.MATURITY_DATE, txn2.DOCUMENT_TYPE, txn2.DOCUMENT_ID,  txn2.POS_ID,txn2.FINANCE_ACTION, txn2.REPAID_STATUS, pos.BANK_ID ");
		
		log.debug("Created second inner query :{0}", secondInnerQuery.toString());
		
		
		StringBuilder outerQuery = new StringBuilder();
		
		outerQuery.append("select txn3.TXN_DATE, txn3.MATURITY_DATE, txn3.DOCUMENT_TYPE, txn3.DOCUMENT_ID, bn3.NAME, txn3.POS_ID, " +
						  "sum(txn3.Satis) as SATIS, sum(txn3.Kom) as KOMISYON, sum(txn3.Satis + txn3.Kom) as NETODENECEK, txn3.REPAID_STATUS, txn3.POS_BANK_ID " +
						  "from ( ");
	
		outerQuery.append("(")
				  .append(firstInnerQuery.toString())
				  .append(") ")
				  .append("union all")
				  .append("(")
				  .append(secondInnerQuery.toString())
				  .append(") ) as txn3 ");
		
		outerQuery.append("inner join BANKS bn3 on bn3.ID = txn3.POS_BANK_ID ")
				  .append("group by txn3.TXN_DATE, txn3.MATURITY_DATE, txn3.DOCUMENT_TYPE, txn3.DOCUMENT_ID, bn3.NAME, txn3.POS_ID, txn3.REPAID_STATUS");


		log.debug("Created Outer Query :{0}", outerQuery.toString());
		
		Query query = entityManager.createNativeQuery(outerQuery.toString());

		if (filterModel.getBeginDate() != null) query.setParameter("txnBeginDate", filterModel.getBeginDate());
		if (filterModel.getEndDate() != null) query.setParameter("txnEndDate", filterModel.getEndDate());
		if (filterModel.getMaturityBeginDate() != null) query.setParameter("maturityBeginDate", filterModel.getMaturityBeginDate());
		if (filterModel.getMaturityEndDate() != null) query.setParameter("maturityEndDate", filterModel.getMaturityEndDate());
		if (filterModel.getPos() != null) query.setParameter("posId", filterModel.getPos().getId());
		if (filterModel.getRepaidStatus() != null) query.setParameter("repaidStatus", filterModel.getRepaidStatus().booleanValue());
		if (filterModel.getBank() != null) query.setParameter("bank", filterModel.getBank().getId());
			
			
		List<Object[]> resultList = query.getResultList();


		for (Object[] item : resultList) {
			CreditCardPosCommisionViewModel model = new CreditCardPosCommisionViewModel();
			
			model.setTxnDate((Date)item[0]);
			model.setMaturityDate((Date)item[1]);
			model.setDocumentType(DocumentType.fromString((Integer)item[2]));
			model.setDocumentId((new BigDecimal((BigInteger)item[3]).longValue()));
			model.setBankName((String)item[4]);
			
			Long posId = (new BigDecimal((BigInteger)item[5])).longValue();
			model.setPos(generalSuggestion.findPos(posId));

			model.setSale((BigDecimal)item[6]);
			model.setCommission((BigDecimal)item[7]);
			model.setNetPrice((BigDecimal)item[8]);
			model.setRepaidStatus((Boolean)item[9]);
			
			entityList.add(model);
		}
		
	}
	
	@SuppressWarnings("unchecked")
	public String createTransferDocumentFromSelectedItems() {
		log.info("Creating transfer document from selected items...");
		manualFlush();
		String result = BaseConsts.FAIL;

		try {
			
			ContactToBankTransferHome<BankToContactTransfer> bth = (ContactToBankTransferHome<BankToContactTransfer>)Component.getInstance("contactToBankTransferHome", true);

			bth.createNew();
			
			BankToContactTransfer transferDocument = bth.getEntity();
			transferDocument.setTransferType(BankTransferType.Havale);
			transferDocument.setAction(FinanceAction.Credit);

			BigDecimal totalAmount = BigDecimal.ZERO;
			for (CreditCardPosCommisionViewModel item : entityList) {
				if (item.getSelected() && !item.getRepaidStatus()) {
					totalAmount = totalAmount.add(item.getNetPrice());
				}
			}
			
			transferDocument.setAmount(new MoneySet(totalAmount, totalAmount, BaseConsts.SYSTEM_CURRENCY_CODE));


			for (CreditCardPosCommisionViewModel model : entityList) {
				if (model.getSelected() && !model.getRepaidStatus()) {
					bth.getPosCommissionViewList().add(model);
				}
			}

			result = BaseConsts.SUCCESS;
		} catch (Exception e) {
			log.error("Transfer fişi hazırlanırken hata meydana geldi. Sebebi #0", e);
			facesMessages.add("Transfer fişi hazırlanırken hata meydana geldi. Sebebi :{0}", e.getMessage());
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public void xls(){
		Map params = new HashMap();
		
		if(filterModel.getBank()!=null){
			params.put("pBankId", filterModel.getBank().getId());
		}
		
		if(filterModel.getPos()!=null){
			params.put("pPosId", filterModel.getPos().getId());
		}
		
		if(filterModel.getBeginDate() != null){
			params.put("pBDate", filterModel.getBeginDate());
		}
		
		if(filterModel.getEndDate() != null){
			params.put("pEDate", filterModel.getEndDate());
			
		}
		
		execXls("kk_pos_komisyon_excel", "Pos_Komisyon_Listesi" , params);
		
	}
	
	@SuppressWarnings("unchecked")
	public void pdf(){
		Map params = new HashMap();
		
		if(filterModel.getBank()!=null){
			params.put("pBankId", filterModel.getBank().getId());
		}
		
		if(filterModel.getPos()!=null){
			params.put("pPosId", filterModel.getPos().getId());
		}
		
		if(filterModel.getBeginDate() != null){
			params.put("pBDate", filterModel.getBeginDate());
		}
		
		if(filterModel.getEndDate() != null){
			params.put("pEDate", filterModel.getEndDate());
			
		}
		
		execPdf("kk_pos_komisyon_excel", "Pos_Komisyon_Listesi" , params);
		
	}
	
	@SuppressWarnings("unchecked")
	public void commissionPdf(){
		Map params = new HashMap();
		
		if(filterModel.getBank()!=null){
			params.put("pBankId", filterModel.getBank().getId());
		}
		
		if(filterModel.getPos()!=null){
			params.put("pPosId", filterModel.getPos().getId());
		}
		
		if(filterModel.getBeginDate() != null){
			params.put("pBDate", filterModel.getBeginDate());
		}
		
		if(filterModel.getEndDate() != null){
			params.put("pEDate", filterModel.getEndDate());
			
		}
		
		if(filterModel.getMaturityBeginDate() != null){
			params.put("pMBDate", filterModel.getMaturityBeginDate());
		}
		
		if(filterModel.getMaturityEndDate() != null){
			params.put("pMEDate", filterModel.getMaturityEndDate());
			
		}
		
		execPdf("kk_pos_komisyon", "Pos_Komisyon_Raporu" , params);
		
	}

	public void manualFlush() {
		Conversation.instance().changeFlushMode(org.jboss.seam.annotations.FlushModeType.MANUAL);
	}

}
