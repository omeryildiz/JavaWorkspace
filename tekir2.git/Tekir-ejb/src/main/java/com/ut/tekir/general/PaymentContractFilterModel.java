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

package com.ut.tekir.general;

import java.util.Date;

import com.ut.tekir.entities.CardOwnerType;
import com.ut.tekir.entities.PaymentOwnerType;
import com.ut.tekir.entities.PaymentType;

/**
 * @author sinan.yumak
 * 
 */
public class PaymentContractFilterModel {

	private Long id;
	private boolean active;
	private String code;
	private String name;
	private String info;
	private Date beginDate;
	private Date endDate;
	private PaymentOwnerType ownerType;
	private PaymentType paymentType;
	private CardOwnerType cardOwnerType;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public Date getBeginDate() {
		return beginDate;
	}

	public void setBeginDate(Date beginDate) {
		this.beginDate = beginDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public PaymentOwnerType getOwnerType() {
		return ownerType;
	}

	public void setOwnerType(PaymentOwnerType ownerType) {
		this.ownerType = ownerType;
	}

	public PaymentType getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(PaymentType paymentType) {
		this.paymentType = paymentType;
	}

	public CardOwnerType getCardOwnerType() {
		return cardOwnerType;
	}

	public void setCardOwnerType(CardOwnerType cardOwnerType) {
		this.cardOwnerType = cardOwnerType;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

}
