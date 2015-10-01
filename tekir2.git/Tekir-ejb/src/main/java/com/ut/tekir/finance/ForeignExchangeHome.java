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

import java.math.BigDecimal;

import javax.ejb.Local;

import com.ut.tekir.entities.ForeignExchange;
import com.ut.tekir.framework.IEntityBase;

/**
 * @author sinan.yumak
 *
 */
@Local
public interface ForeignExchangeHome<E> extends IEntityBase<E> {

	void init();

	ForeignExchange getForeignExchange();

	void setForeignExchange(ForeignExchange foreignExchange);

	void createNew();

	void clearFromBankAccount();

	void clearToBankAccount();

	void selectBankAccount(Integer ix);

	void initFromAmountCurrency();
	
	void initToAmountCurrency();

	void manualFlush();

	BigDecimal getYatirilanMiktar();

	void setYatirilanMiktar(BigDecimal yatirilanMiktar);

	BigDecimal getCekilenMasraf();

	void setCekilenMasraf(BigDecimal cekilenMasraf);
	
	String hardWeakCurrency();

}