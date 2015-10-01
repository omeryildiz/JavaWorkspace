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

import com.ut.tekir.entities.DocumentType;
import com.ut.tekir.entities.FinanceAction;
import com.ut.tekir.entities.TradeAction;

/**
 * @author cenk.canarslan
 *
 */
public class WorkBunchViewModel {
	private Long docId;
	private String serial;
	private String reference;
	private String info;
	private String code;
	private Date date;
	private DocumentType documentType;
	private FinanceAction financeAction; 
	private TradeAction tradeAction;

	public Long getDocId() {
		return docId;
	}
	public void setDocId(Long docId) {
		this.docId = docId;
	}
	public String getSerial() {
		return serial;
	}
	public void setSerial(String serial) {
		this.serial = serial;
	}
	public String getReference() {
		return reference;
	}
	public void setReference(String reference) {
		this.reference = reference;
	}
	public String getInfo() {
		return info;
	}
	public void setInfo(String info) {
		this.info = info;
	}
	public DocumentType getDocumentType() {
		return documentType;
	}
	public void setDocumentType(DocumentType documentType) {
		this.documentType = documentType;
	}
	public FinanceAction getFinanceAction() {
		return financeAction;
	}
	public void setFinanceAction(FinanceAction financeAction) {
		this.financeAction = financeAction;
	}
	public TradeAction getTradeAction() {
		return tradeAction;
	}
	public void setTradeAction(TradeAction tradeAction) {
		this.tradeAction = tradeAction;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	
}
