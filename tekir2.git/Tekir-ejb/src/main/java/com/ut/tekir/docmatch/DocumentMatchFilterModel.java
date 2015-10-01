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

package com.ut.tekir.docmatch;

import java.util.Date;

import com.ut.tekir.entities.AdvanceProcessType;
import com.ut.tekir.entities.Contact;
import com.ut.tekir.entities.TradeAction;
import com.ut.tekir.entities.WorkBunch;
import com.ut.tekir.framework.DefaultDocumentFilterModel;

/**
 * Döküman eşleme arama popuplarında filtreleri tutan model sınıfımızdır.
 * 
 * @author sinan.yumak
 * 
 */
public class DocumentMatchFilterModel extends DefaultDocumentFilterModel {
	private Long id;
	private String integrationSerial;
	private Date date;
	private Contact contact;
	private String info;
	private TradeAction action;
	private Boolean disableContactSelect = Boolean.FALSE;
	private WorkBunch workBunch;
	private AdvanceProcessType processType;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getIntegrationSerial() {
		return integrationSerial;
	}

	public void setIntegrationSerial(String integrationSerial) {
		this.integrationSerial = integrationSerial;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Contact getContact() {
		return contact;
	}

	public void setContact(Contact contact) {
		this.contact = contact;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public TradeAction getAction() {
		return action;
	}

	public void setAction(TradeAction action) {
		this.action = action;
	}

	public Boolean getDisableContactSelect() {
		return disableContactSelect;
	}

	public void setDisableContactSelect(Boolean disableContactSelect) {
		this.disableContactSelect = disableContactSelect;
	}

	public WorkBunch getWorkBunch() {
		return workBunch;
	}

	public void setWorkBunch(WorkBunch workBunch) {
		this.workBunch = workBunch;
	}

	public AdvanceProcessType getProcessType() {
		return processType;
	}

	public void setProcessType(AdvanceProcessType processType) {
		this.processType = processType;
	}

}
