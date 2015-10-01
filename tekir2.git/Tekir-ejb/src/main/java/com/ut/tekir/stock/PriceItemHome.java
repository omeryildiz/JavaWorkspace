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

import java.math.BigDecimal;

import javax.ejb.Local;

import com.ut.tekir.entities.PriceItemDetail;
import com.ut.tekir.entities.TradeAction;
import com.ut.tekir.framework.IEntityBase;

/**
 *
 * @author sinan.yumak
 */
@Local
public interface PriceItemHome<E> extends IEntityBase<E> {

    void createNewLine();

    void bringAllProducts();

    String copyProducts();

    BigDecimal getPrice();

    void setPrice(BigDecimal price);

    String getCode();

    boolean getDiscountType();

    void setDiscountType(boolean discountType);

    void setCode(String code);

    boolean getIncrease();

    void setIncrease(boolean increase);

    Double getDiscountRate();

    void setDiscountRate(Double discountRate);

    void deleteLine(PriceItemDetail item);

    void deleteLine(Integer ix);

    void init();

    void manualFlush();

    void toggleSendToSpool();

	boolean isSendToSpool();
	void setSendToSpool(boolean sendToSpool);

	void sendToBarcodeSpool();
	
	TradeAction getActionType();
	void setActionType(TradeAction actionType);

}
