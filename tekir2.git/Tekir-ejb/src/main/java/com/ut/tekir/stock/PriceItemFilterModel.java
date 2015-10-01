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

import java.util.Date;

import com.ut.tekir.entities.PriceList;
import com.ut.tekir.entities.ProductGroup;
import com.ut.tekir.entities.TradeAction;

/**
 * 
 * @author sinan.yumak
 */
public class PriceItemFilterModel {

	private Long id;
	private boolean active;
	private boolean defaultItem;
	private String code;
	private String ownerCode;
	private String ownerListName;
	private Date beginDate;
	private Date endDate;
	private String info;
	private PriceList priceList;
	private ProductGroup group;
	private TradeAction action;
	
	public PriceList getPriceList() {
		return priceList;
	}

	public void setPriceList(PriceList priceList) {
		this.priceList = priceList;
	}
	
	public boolean isDefaultItem() {
		return defaultItem;
	}

	public void setDefaultItem(boolean defaultItem) {
		this.defaultItem = defaultItem;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public Date getBeginDate() {
		return beginDate;
	}

	public void setBeginDate(Date beginDate) {
		this.beginDate = beginDate;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getOwnerCode() {
		return ownerCode;
	}

	public void setOwnerCode(String ownerCode) {
		this.ownerCode = ownerCode;
	}

	public String getOwnerListName() {
		return ownerListName;
	}

	public void setOwnerListName(String ownerListName) {
		this.ownerListName = ownerListName;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public ProductGroup getGroup() {
		return group;
	}

	public void setGroup(ProductGroup group) {
		this.group = group;
	}

	public TradeAction getAction() {
		return action;
	}

	public void setAction(TradeAction action) {
		this.action = action;
	}

}
