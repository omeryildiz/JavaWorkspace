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

import java.math.BigDecimal;
import java.util.Date;

import com.ut.tekir.entities.Contact;
import com.ut.tekir.entities.DocumentType;
import com.ut.tekir.entities.MoneySet;
import com.ut.tekir.entities.TradeAction;
import com.ut.tekir.framework.DefaultDocumentFilterModel;

/**
 * Döküman eşlemeleri sorgu sonuçlarını tutacak model sınıfıdır.
 * 
 * @author sinan.yumak
 * 
 */
public class DocumentMatchResultModel extends DefaultDocumentFilterModel {
	private Long id;
	private Date date;
	private TradeAction action;
	private String currency;
	private BigDecimal value;
	private BigDecimal localAmount;
	private DocumentType documentType;
	private String contactName;
	private String contactCode;
	private BigDecimal usedAmount = BigDecimal.ZERO;//eşlemelerde kullanılmış toplam tutar bilgisini tutar.
	
	public String getCaption() {
        return "[" + getContactCode() + "] " + getContactName();
	}
	
	public BigDecimal getRemainingAmount() {
		return value.subtract(usedAmount);
	}
	
	public MoneySet getAmount() {
		return new MoneySet(value, localAmount, currency);
	}
	
	public void setContact(Contact contact) {
		this.contactCode = contact.getCode();
		this.contactName = contact.getFullname();
	}
	
	public void setAmount(MoneySet money) {
		this.value = money.getValue();
		this.currency = money.getCurrency();
		this.localAmount = money.getLocalAmount();
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public TradeAction getAction() {
		return action;
	}

	public void setAction(TradeAction action) {
		this.action = action;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public BigDecimal getValue() {
		return value;
	}

	public void setValue(BigDecimal value) {
		this.value = value;
	}

	public DocumentType getDocumentType() {
		return documentType;
	}

	public void setDocumentType(DocumentType documentType) {
		this.documentType = documentType;
	}

	public String getContactName() {
		return contactName;
	}

	public void setContactName(String contactName) {
		this.contactName = contactName;
	}

	public String getContactCode() {
		return contactCode;
	}

	public void setContactCode(String contactCode) {
		this.contactCode = contactCode;
	}

	public BigDecimal getUsedAmount() {
		return usedAmount;
	}

	public void setUsedAmount(BigDecimal usedAmount) {
		this.usedAmount = usedAmount;
	}

	public BigDecimal getLocalAmount() {
		return localAmount;
	}

	public void setLocalAmount(BigDecimal localAmount) {
		this.localAmount = localAmount;
	}

}
