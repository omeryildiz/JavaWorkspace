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

import java.util.List;
import java.util.Map;

import com.ut.tekir.entities.PriceItemDetail;
import com.ut.tekir.entities.Product;
import com.ut.tekir.entities.ProductTxn;
import com.ut.tekir.framework.IBrowserBase;
import javax.ejb.Local;

/**
 *
 * @author haky
 */
@Local
public interface ProductTxnReport<E, F> extends IBrowserBase<E,F> {

    void pdf();
    void xls();
    void getPrices();
    List<ProductTxn> getPurchaseEntityList();
	List<ProductTxn> getSaleEntityList();
	List<Map> getWarehouseResult();
	List<Product> getProductList();
	List<PriceItemDetail> getSalePriceListResult();
	List<PriceItemDetail> getPurchasePriceListResult();
	Integer getExid();
	void setExid(Integer exid);
	
}
