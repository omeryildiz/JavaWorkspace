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

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.ut.tekir.entities.ChequeStatus;
import com.ut.tekir.entities.Money;

public class ChequeSumModel implements Serializable{

    private static final long serialVersionUID = 1L;

    private Long id;
    private String referenceNo;
    private String bankName;
    private String bankBranch;
    private String accountNo;
    private String serialNo;
    private Date maturityDate;
    private Money money = new Money();
    private String chequeOwner;
    private String contactCode;
    private String contactName;
    private Long contactId;
    private ChequeStatus previousStatus;
    private ChequeStatus lastStatus;
    private String info;
    private Boolean checked = Boolean.FALSE;

    public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getReferenceNo() {
		return referenceNo;
	}
	public void setReferenceNo(String referenceNo) {
		this.referenceNo = referenceNo;
	}
	public String getBankName() {
		return bankName;
	}
	public void setBankName(String bankName) {
		this.bankName = bankName;
	}
	public String getBankBranch() {
		return bankBranch;
	}
	public void setBankBranch(String bankBranch) {
		this.bankBranch = bankBranch;
	}
	public String getAccountNo() {
		return accountNo;
	}
	public void setAccountNo(String accountNo) {
		this.accountNo = accountNo;
	}
	public String getSerialNo() {
		return serialNo;
	}
	public void setSerialNo(String serialNo) {
		this.serialNo = serialNo;
	}
	public Date getMaturityDate() {
		return maturityDate;
	}
	public void setMaturityDate(Date maturityDate) {
		this.maturityDate = maturityDate;
	}
	public Money getMoney() {
		return money;
	}
	public void setMoney(Money money) {
		this.money = money;
	}
	public String getChequeOwner() {
		return chequeOwner;
	}
	public void setChequeOwner(String chequeOwner) {
		this.chequeOwner = chequeOwner;
	}
	
	public ChequeStatus getPreviousStatus() {
		return previousStatus;
	}
	public void setPreviousStatus(ChequeStatus previousStatus) {
		this.previousStatus = previousStatus;
	}
	public ChequeStatus getLastStatus() {
		return lastStatus;
	}
	public void setLastStatus(ChequeStatus lastStatus) {
		this.lastStatus = lastStatus;
	}
	public Boolean getChecked() {
		return checked;
	}
	public void setChecked(Boolean checked) {
		this.checked = checked;
	}	
    
	public void setMoneyCurrency( String ccy ){
		money.setCurrency(ccy);
	}

	public String getMoneyCurrency(){
		return money.getCurrency();
	}
	
	public void setMoneyValue( BigDecimal val ){
		money.setValue(val);
	}
	
	public BigDecimal getMoneyValue(){
		return money.getValue();
	}
	public String getInfo() {
		return info;
	}
	public void setInfo(String info) {
		this.info = info;
	}
	public String getContactName() {
		return contactName;
	}
	public void setContactName(String contactName) {
		this.contactName = contactName;
	}
	public Long getContactId() {
		return contactId;
	}
	public void setContactId(Long contactId) {
		this.contactId = contactId;
	}

    public String getContactCode() {
        return contactCode;
    }

    public void setContactCode(String contactCode) {
        this.contactCode = contactCode;
    }
}
