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

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Date;

import com.ut.tekir.entities.AdvanceProcessType;
import com.ut.tekir.entities.WorkBunch;
import com.ut.tekir.framework.DefaultDocumentFilterModel;

/**
 * Bu model hem arama kriterlerini tutmak hem de join yapılan 
 * kolonlardaki bilgileri tutmak için kullanılmaktadır.
 * 
 * @author haky
 */
public class PaymentFilterModel extends DefaultDocumentFilterModel{
	private Long id;
	private Date date;
	private String info;
	private String contactCode;
	private String contactName;
	private String accountCode;
	private String totalAmountCurrency;
	private BigDecimal totalAmountValue;
    private Boolean person;
    private String company;
    private WorkBunch workBunch;
    private AdvanceProcessType processType;
    private BigDecimal itemAmountValue;
    
    
	public WorkBunch getWorkBunch() {
		return workBunch;
	}

	public void setWorkBunch(WorkBunch workBunch) {
		this.workBunch = workBunch;
	}

	public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public Boolean getPerson() {
        return person;
    }

    public void setPerson(Boolean person) {
        this.person = person;
    }

	public String getTotalAmount() {
		NumberFormat f = NumberFormat.getInstance();
		f.setMaximumFractionDigits(2);
		f.setMinimumFractionDigits(2);
		
		return f.format(getTotalAmountValue()) + " " + getTotalAmountCurrency();
	}

	public String getContactCode() {
		return contactCode;
	}

	public void setContactCode(String contactCode) {
		this.contactCode = contactCode;
	}
	
	public String getContactName() {
		return contactName;
	}
	
	public void setContactName(String contactName) {
		this.contactName = contactName;
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
	
	@Override
	public String getInfo() {
		return info;
	}
	
	@Override
	public void setInfo(String info) {
		this.info = info;
	}

	public String getAccountCode() {
		return accountCode;
	}
	
	public void setAccountCode(String accountCode) {
		this.accountCode = accountCode;
	}

	public String getTotalAmountCurrency() {
		return totalAmountCurrency;
	}

	public void setTotalAmountCurrency(String totalAmountCurrency) {
		this.totalAmountCurrency = totalAmountCurrency;
	}

	public BigDecimal getTotalAmountValue() {
		return totalAmountValue;
	}

	public void setTotalAmountValue(BigDecimal totalAmountValue) {
		this.totalAmountValue = totalAmountValue;
	}

	public AdvanceProcessType getProcessType() {
		return processType;
	}

	public void setProcessType(AdvanceProcessType processType) {
		this.processType = processType;
	}

	public BigDecimal getItemAmountValue() {
		return itemAmountValue;
	}

	public void setItemAmountValue(BigDecimal itemAmountValue) {
		this.itemAmountValue = itemAmountValue;
	}

}
