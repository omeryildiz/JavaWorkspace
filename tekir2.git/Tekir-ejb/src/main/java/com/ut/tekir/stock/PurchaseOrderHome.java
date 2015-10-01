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

import javax.ejb.Local;

import com.ut.tekir.entities.OrderItem;
import com.ut.tekir.entities.OrderNote;
import com.ut.tekir.framework.IEntityBase;

/**
 *
 * @author sinan
 *
 */
@Local
public interface PurchaseOrderHome<E> extends IEntityBase<E> {

	void init();

	OrderNote getPurchaseOrder();

	void setPurchaseOrder(OrderNote orderNote);

	void createNew();

	String save();

	void createNewLine();

	void deleteLine(OrderItem item);

	void deleteLine(Integer ix);

	void selectProduct(Integer ix);

	void manualFlush();

}