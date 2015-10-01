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

import com.ut.tekir.entities.Foundation;

/**
 * @author sinan.yumak
 * 
 */
public class FoundationRefundingFilterModel {

	private Date maturityBeginDate;
	private Date maturityEndDate;

	/*
	 * İşlem tarihi filtreleri...
	 */
	private Date txnBeginDate;
	private Date txnEndDate;
	private Foundation foundation;
	private Boolean repaidStatus;

	public Foundation getFoundation() {
		return foundation;
	}

	public void setFoundation(Foundation foundation) {
		this.foundation = foundation;
	}

	public Boolean getRepaidStatus() {
		return repaidStatus;
	}

	public void setRepaidStatus(Boolean repaidStatus) {
		this.repaidStatus = repaidStatus;
	}

	public Date getMaturityBeginDate() {
		return maturityBeginDate;
	}

	public void setMaturityBeginDate(Date maturityBeginDate) {
		this.maturityBeginDate = maturityBeginDate;
	}

	public Date getMaturityEndDate() {
		return maturityEndDate;
	}

	public void setMaturityEndDate(Date maturityEndDate) {
		this.maturityEndDate = maturityEndDate;
	}

	public Date getTxnBeginDate() {
		return txnBeginDate;
	}

	public void setTxnBeginDate(Date txnBeginDate) {
		this.txnBeginDate = txnBeginDate;
	}

	public Date getTxnEndDate() {
		return txnEndDate;
	}

	public void setTxnEndDate(Date txnEndDate) {
		this.txnEndDate = txnEndDate;
	}

}
