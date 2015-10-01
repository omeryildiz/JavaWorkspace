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

package com.ut.tekir.finance.print;

import java.math.BigDecimal;

/**
 *
 * @author bilga
 */

public class ReconciliationContactModel {

    private String fullname;
    private String company;
    private BigDecimal totallcyval;
    private String currency;
    
    public ReconciliationContactModel(){}
    
    public ReconciliationContactModel(Object[] obj){
    	this.setFullname((String) obj[0]);
    	this.setCompany((String) obj[1]);
    	this.setCurrency((String) obj[2]);
    	this.setTotallcyval((BigDecimal) obj[3]);
    }

	public void setFullname(String fullname) {
		this.fullname = fullname;
	}

	public String getFullname() {
		return fullname;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getCompany() {
		return company;
	}

	public void setTotallcyval(BigDecimal totallcyval) {
		this.totallcyval = totallcyval;
	}

	public BigDecimal getTotallcyval() {
		return totallcyval;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

}
