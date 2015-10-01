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

package com.ut.tekir.framework;

import java.math.BigDecimal;

/**
*
*	İrsaliye Listesini doldurmak için kullanılan alanlardan oluşan model.
*
* @author bilga
*/

public class ContactReportSummaryModel {

	private String ccy;
	private BigDecimal ccyval;
	private BigDecimal lcyval;
	
	
	public ContactReportSummaryModel(){}
	
	public ContactReportSummaryModel(Object[] obj) {
		this.ccy = (String) obj[0];
		this.ccyval = (BigDecimal) obj[1];
		this.lcyval = (BigDecimal) obj[2];
	}

	public String getCcy() {
		return ccy;
	}

	public void setCcy(String ccy) {
		this.ccy = ccy;
	}

	public BigDecimal getCcyval() {
		return ccyval;
	}

	public void setCcyval(BigDecimal ccyval) {
		this.ccyval = ccyval;
	}

	public BigDecimal getLcyval() {
		return lcyval;
	}

	public void setLcyval(BigDecimal lcyval) {
		this.lcyval = lcyval;
	}
}
