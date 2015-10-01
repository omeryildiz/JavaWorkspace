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

import com.ut.tekir.entities.DocumentType;
import com.ut.tekir.entities.FinanceAction;
import com.ut.tekir.entities.TaxReturnType;
import com.ut.tekir.entities.TaxType;
import com.ut.tekir.entities.WorkBunch;

import java.util.Date;

/**
 *
 * @author yigit
 */
public class TaxTxnFilterModel {

    private String serial;
    private String reference;
    private TaxType taxType;
    private String code;
    private Date beginDate;
    private Date endDate;
    private TaxReturnType taxReturnType;
    private DocumentType documentType;
    private FinanceAction action;
    private WorkBunch workBunch;

    /**
     * @return the serial
     */
    public String getSerial() {
        return serial;
    }

    /**
     * @param serial the serial to set
     */
    public void setSerial(String serial) {
        this.serial = serial;
    }

    /**
     * @return the reference
     */
    public String getReference() {
        return reference;
    }

    /**
     * @param reference the reference to set
     */
    public void setReference(String reference) {
        this.reference = reference;
    }

    /**
     * @return the taxType
     */
    public TaxType getTaxType() {
        return taxType;
    }

    /**
     * @param taxType the taxType to set
     */
    public void setTaxType(TaxType taxType) {
        this.taxType = taxType;
    }

    /**
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * @param code the code to set
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * @return the beginDate
     */
    public Date getBeginDate() {
        return beginDate;
    }

    /**
     * @param beginDate the beginDate to set
     */
    public void setBeginDate(Date beginDate) {
        this.beginDate = beginDate;
    }

    /**
     * @return the endDate
     */
    public Date getEndDate() {
        return endDate;
    }

    /**
     * @param endDate the endDate to set
     */
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    
    public TaxReturnType getTaxReturnType() {
        return taxReturnType;
    }

    /**
     * @param documentType the documentType to set
     */
    public void setTaxReturnType(TaxReturnType taxReturnType) {
        this.taxReturnType = taxReturnType;
    }

    /**
     * @return the action
     */
    public FinanceAction getAction() {
        return action;
    }

    /**
     * @param action the action to set
     */
    public void setAction(FinanceAction action) {
        this.action = action;
    }

	public DocumentType getDocumentType() {
		return documentType;
	}

	public void setDocumentType(DocumentType documentType) {
		this.documentType = documentType;
	}

	public WorkBunch getWorkBunch() {
		return workBunch;
	}

	public void setWorkBunch(WorkBunch workBunch) {
		this.workBunch = workBunch;
	}

}
