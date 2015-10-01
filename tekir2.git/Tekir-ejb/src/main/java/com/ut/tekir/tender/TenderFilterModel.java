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

package com.ut.tekir.tender;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Date;

import com.ut.tekir.entities.TenderStatus;
import com.ut.tekir.framework.DefaultDocumentFilterModel;

/**
 * @author sinan.yumak
 * 
 */
public class TenderFilterModel extends DefaultDocumentFilterModel {
	private Long id;
    private TenderStatus status;
	private Integer referenceSuffix;
	private String contactName;
	private String contactCode;
	private String info1;
	private String info2;
	private Date date;
	private BigDecimal totalAmountValue;
	private String totalAmountCurrency;
	private BigDecimal totalDiscountValue;
	private String totalDiscountCurrency;
	private BigDecimal totalFeeValue;
	private String totalFeeCurrency;
	private BigDecimal totalExpenseValue;
	private String totalExpenseCurrency;
	private BigDecimal totalTaxValue;
	private String totalTaxCurrency;

	public static NumberFormat getNumberFormat() {
		NumberFormat f = NumberFormat.getInstance();
		f.setMaximumFractionDigits(2);
		f.setMinimumFractionDigits(2);
		return f;
	}

    public String totalAmountString(){
        return getNumberFormat().format(getTotalAmountValue()) + " " + getTotalAmountCurrency();
    }

    public String totalDiscountString(){
    	return getNumberFormat().format(getTotalDiscountValue()) + " " + getTotalDiscountCurrency();
    }

    public String totalExpenseString(){
    	return getNumberFormat().format(getTotalExpenseValue()) + " " + getTotalExpenseCurrency();
    }

    public String totalFeeString(){
    	return getNumberFormat().format(getTotalFeeValue()) + " " + getTotalFeeCurrency();
    }

    public String totalTaxString(){
    	return getNumberFormat().format(getTotalTaxValue()) + " " + getTotalTaxCurrency();
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public BigDecimal getTotalDiscountValue() {
		return totalDiscountValue;
	}

	public void setTotalDiscountValue(BigDecimal totalDiscountValue) {
		this.totalDiscountValue = totalDiscountValue;
	}

	public String getTotalDiscountCurrency() {
		return totalDiscountCurrency;
	}

	public void setTotalDiscountCurrency(String totalDiscountCurrency) {
		this.totalDiscountCurrency = totalDiscountCurrency;
	}

	public BigDecimal getTotalFeeValue() {
		return totalFeeValue;
	}

	public void setTotalFeeValue(BigDecimal totalFeeValue) {
		this.totalFeeValue = totalFeeValue;
	}

	public String getTotalFeeCurrency() {
		return totalFeeCurrency;
	}

	public void setTotalFeeCurrency(String totalFeeCurrency) {
		this.totalFeeCurrency = totalFeeCurrency;
	}

	public BigDecimal getTotalExpenseValue() {
		return totalExpenseValue;
	}

	public void setTotalExpenseValue(BigDecimal totalExpenseValue) {
		this.totalExpenseValue = totalExpenseValue;
	}

	public String getTotalExpenseCurrency() {
		return totalExpenseCurrency;
	}

	public void setTotalExpenseCurrency(String totalExpenseCurrency) {
		this.totalExpenseCurrency = totalExpenseCurrency;
	}

	public BigDecimal getTotalTaxValue() {
		return totalTaxValue;
	}

	public void setTotalTaxValue(BigDecimal totalTaxValue) {
		this.totalTaxValue = totalTaxValue;
	}

	public String getTotalTaxCurrency() {
		return totalTaxCurrency;
	}

	public void setTotalTaxCurrency(String totalTaxCurrency) {
		this.totalTaxCurrency = totalTaxCurrency;
	}

	public BigDecimal getTotalAmountValue() {
		return totalAmountValue;
	}

	public void setTotalAmountValue(BigDecimal totalAmountValue) {
		this.totalAmountValue = totalAmountValue;
	}

	public String getTotalAmountCurrency() {
		return totalAmountCurrency;
	}

	public void setTotalAmountCurrency(String totalAmountCurrency) {
		this.totalAmountCurrency = totalAmountCurrency;
	}

	public Integer getReferenceSuffix() {
		return referenceSuffix;
	}

	public void setReferenceSuffix(Integer referenceSuffix) {
		this.referenceSuffix = referenceSuffix;
	}

	public TenderStatus getStatus() {
		return status;
	}

	public void setStatus(TenderStatus status) {
		this.status = status;
	}

	public String getContactCaption() {
		if (contactCode != null && contactCode.length() != 0 ) return "[" + getContactCode() + "] " + getContactName();
		return null;
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

	public String getInfo1() {
		return info1;
	}

	public void setInfo1(String info1) {
		this.info1 = info1;
	}

	public String getInfo2() {
		return info2;
	}

	public void setInfo2(String info2) {
		this.info2 = info2;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

}
