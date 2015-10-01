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
import java.util.Date;

import com.ut.tekir.entities.DocumentType;
import com.ut.tekir.entities.Pos;

/**
 * Pos geri ödeme ekranında kullanılacak olan view modelimiz...
 * 
 * @author sinan.yumak
 * 
 */
public class CreditCardPosCommisionViewModel {

	private Date txnDate;
	private Date maturityDate;
	private DocumentType documentType;
	private Long documentId;
	private String bankName;
	private Pos pos;
	private BigDecimal sale;
	private BigDecimal commission;
	private BigDecimal netPrice;
	private Boolean repaidStatus;
	private Boolean selected;

	public Date getTxnDate() {
		return txnDate;
	}

	public void setTxnDate(Date txnDate) {
		this.txnDate = txnDate;
	}

	public Date getMaturityDate() {
		return maturityDate;
	}

	public void setMaturityDate(Date maturityDate) {
		this.maturityDate = maturityDate;
	}

	public DocumentType getDocumentType() {
		return documentType;
	}

	public void setDocumentType(DocumentType documentType) {
		this.documentType = documentType;
	}

	public Long getDocumentId() {
		return documentId;
	}

	public void setDocumentId(Long documentId) {
		this.documentId = documentId;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public Pos getPos() {
		return pos;
	}

	public void setPos(Pos pos) {
		this.pos = pos;
	}

	public BigDecimal getSale() {
		return sale;
	}

	public void setSale(BigDecimal sale) {
		this.sale = sale;
	}

	public BigDecimal getCommission() {
		return commission;
	}

	public void setCommission(BigDecimal commission) {
		this.commission = commission;
	}

	public BigDecimal getNetPrice() {
		return netPrice;
	}

	public void setNetPrice(BigDecimal netPrice) {
		this.netPrice = netPrice;
	}

	public Boolean getRepaidStatus() {
		return repaidStatus;
	}

	public void setRepaidStatus(Boolean repaidStatus) {
		this.repaidStatus = repaidStatus;
	}

	public Boolean getSelected() {
		return selected;
	}

	public void setSelected(Boolean selected) {
		this.selected = selected;
	}
}
