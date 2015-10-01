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

package com.ut.tekir.stock;

import java.io.Serializable;
import java.util.Date;

import com.ut.tekir.entities.Warehouse;
import com.ut.tekir.entities.WorkBunch;
import com.ut.tekir.framework.DefaultDocumentFilterModel;

/**
 *
 * @author haky
 */
public class ShipmentFilterModel extends DefaultDocumentFilterModel implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private Long id;
    private String contactName;
    private String contactCode;
    private String info;
    private String info1;
    private String info2;
    private Date date;
    private String fromContactName;
    private String fromContactCode;
    private String toContactName;
    private String toContactCode;
    private Boolean person;
    private String company;
    
    private Warehouse warehouse;
    private Warehouse fromWarehouse;
    private Warehouse toWarehouse;
    private Boolean invoiced;
    private WorkBunch workBunch;
    
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
    
    public Warehouse getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(Warehouse warehouse) {
        this.warehouse = warehouse;
    }

	public void setContactName(String contactName) {
		this.contactName = contactName;
	}

	public String getContactName() {
		return contactName;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public String getInfo() {
		return info;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Date getDate() {
		return date;
	}

	public void setContactCode(String contactCode) {
		this.contactCode = contactCode;
	}

	public String getContactCode() {
		return contactCode;
	}

	public void setFromContactName(String fromContactName) {
		this.fromContactName = fromContactName;
	}

	public String getFromContactName() {
		return fromContactName;
	}

	public void setFromContactCode(String fromContactCode) {
		this.fromContactCode = fromContactCode;
	}

	public String getFromContactCode() {
		return fromContactCode;
	}

	public void setToContactName(String toContactName) {
		this.toContactName = toContactName;
	}

	public String getToContactName() {
		return toContactName;
	}

	public void setToContactCode(String toContactCode) {
		this.toContactCode = toContactCode;
	}

	public String getToContactCode() {
		return toContactCode;
	}

	public void setFromWarehouse(Warehouse fromWarehouse) {
		this.fromWarehouse = fromWarehouse;
	}

	public Warehouse getFromWarehouse() {
		return fromWarehouse;
	}

	public void setToWarehouse(Warehouse toWarehouse) {
		this.toWarehouse = toWarehouse;
	}

	public Warehouse getToWarehouse() {
		return toWarehouse;
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

	public Boolean getInvoiced() {
		return invoiced;
	}

	public void setInvoiced(Boolean invoiced) {
		this.invoiced = invoiced;
	}

	public WorkBunch getWorkBunch() {
		return workBunch;
	}

	public void setWorkBunch(WorkBunch workBunch) {
		this.workBunch = workBunch;
	}

}

