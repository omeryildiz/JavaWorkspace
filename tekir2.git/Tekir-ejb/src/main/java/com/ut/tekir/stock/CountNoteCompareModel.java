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

import com.ut.tekir.entities.CountNoteItem;
import com.ut.tekir.entities.FinanceAction;
import com.ut.tekir.entities.Product;
import com.ut.tekir.entities.TradeAction;

/**
 * 
 * @author sinan.yumak
 */
public class CountNoteCompareModel {

	private CountNoteItem item;
	private double counted = 0;
	private double found = 0;
	private String unit;
	private boolean process = true;
	
	public CountNoteCompareModel() {
		super();
	}
	
	public CountNoteCompareModel(CountNoteItem item) {
		super();
		this.item = item;
		this.counted = item.getQuantity();
		this.found = item.getExist();
		this.unit = item.getProduct().getUnit();
	}

	public CountNoteCompareModel(double counted, double found, Product product) {
		super();
		this.counted = counted;
		this.found = found;
		this.unit = product.getUnit();
	}


	public CountNoteCompareModel(double found, Product product, CountNoteItem item) {
		super();
		this.found = found;
		this.item = item;
		this.unit = product.getUnit();
	}

	public double getCounted() {
		return counted;
	}

	public void setCounted(double counted) {
		this.counted = counted;
	}

	public double getFound() {
		return found;
	}

	public void setFound(double found) {
		this.found = found;
	}

	public double getDifference() {
		return counted - found;
	}

	public double getAbsoluteDifference() {
		return getDifference() > 0 ? getDifference() : getDifference() * -1;
	}

	public boolean hasDifference() {
		return getDifference() != 0 ? true: false;
	}
	
	public TradeAction getTradeAction() {
		return getDifference() > 0 ? TradeAction.Purchase: TradeAction.Sale;
	}
	
	public FinanceAction getFinanceAction() {
		return getDifference() > 0 ? FinanceAction.Debit : FinanceAction.Credit;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public boolean isProcess() {
		return process;
	}

	public void setProcess(boolean process) {
		this.process = process;
	}

	public CountNoteItem getItem() {
		return item;
	}

	public void setItem(CountNoteItem item) {
		this.item = item;
	}

}
