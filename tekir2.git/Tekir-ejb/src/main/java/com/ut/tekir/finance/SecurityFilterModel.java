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

import com.ut.tekir.entities.SecurityType;
import com.ut.tekir.framework.DefaultDocumentFilterModel;

public class SecurityFilterModel extends DefaultDocumentFilterModel {
	private String isin;
	private String code;
	private Date beginIssueDate;
	private Date endIssueDate;
	private Date beginMaturityDate;
	private Date endMaturityDate;
	private SecurityType securityType;
	
	public String getIsin() {
		return isin;
	}
	public void setIsin(String isin) {
		this.isin = isin;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	
	public SecurityType getSecurityType() {
		return securityType;
	}
	public void setSecurityType(SecurityType securityType) {
		this.securityType = securityType;
	}
	public Date getBeginIssueDate() {
		return beginIssueDate;
	}
	public void setBeginIssueDate(Date beginIssueDate) {
		this.beginIssueDate = beginIssueDate;
	}
	public Date getEndIssueDate() {
		return endIssueDate;
	}
	public void setEndIssueDate(Date endIssueDate) {
		this.endIssueDate = endIssueDate;
	}
	public Date getBeginMaturityDate() {
		return beginMaturityDate;
	}
	public void setBeginMaturityDate(Date beginMaturityDate) {
		this.beginMaturityDate = beginMaturityDate;
	}
	public Date getEndMaturityDate() {
		return endMaturityDate;
	}
	public void setEndMaturityDate(Date endMaturityDate) {
		this.endMaturityDate = endMaturityDate;
	}
}
