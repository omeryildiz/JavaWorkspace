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

import java.io.Serializable;
import java.util.Date;

import com.ut.tekir.entities.Bank;
import com.ut.tekir.entities.Pos;
import com.ut.tekir.framework.DefaultDocumentFilterModel;


/**
 * @author rustem.topcu
 *
 */
public class CreditCardPosCommissionFilterModel extends DefaultDocumentFilterModel implements Serializable {

	private static final long serialVersionUID = 1L;

	private Bank bank;
	private Pos pos;
	private Date beginDate;
	private Date endDate;
	private Date maturityBeginDate;
	private Date maturityEndDate;
	private Boolean repaidStatus;

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

	public Bank getBank() {
		return bank;
	}

	public void setBank(Bank bank) {
		this.bank = bank;
	}

	public Pos getPos() {
		return pos;
	}

	public void setPos(Pos pos) {
		this.pos = pos;
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

	public Boolean getRepaidStatus() {
		return repaidStatus;
	}

	public void setRepaidStatus(Boolean repaidStatus) {
		this.repaidStatus = repaidStatus;
	}

}
