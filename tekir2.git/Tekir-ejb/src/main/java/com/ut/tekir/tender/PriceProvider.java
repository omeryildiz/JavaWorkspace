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

package com.ut.tekir.tender;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.ejb.Local;

import com.ut.tekir.entities.Contact;
import com.ut.tekir.entities.PriceItem;
import com.ut.tekir.entities.PriceItemDetail;
import com.ut.tekir.entities.PriceList;
import com.ut.tekir.entities.Product;
import com.ut.tekir.entities.ProductGroup;
import com.ut.tekir.entities.ProductTxn;
import com.ut.tekir.entities.TradeAction;

/**
 * 
 * @author sinan.yumak
 *
 */
@Local
public interface PriceProvider {

	/**
	 * Verilen ürün / hizmet için alış fiyatı döndürür.
	 * @param product
	 * @param contact
	 * @param date
	 * @param unit
	 * @return price
	 */
	BigDecimal getPurchasePrice(Product product, Contact contact, Double unit, Date date);
	
	/**
	 * Verilen ürün / hizmet için satış fiyatı döndürür.
	 * @param product
	 * @param contact
	 * @param date
	 * @param unit
	 * @return price
	 */
	BigDecimal getSalePrice(Product product, Contact contact, Double unit, Date date);

	List<PriceItem> getPriceItemList(PriceList priceList, Date requestedDate);

	PriceItem findPriceItem(PriceList priceList, Date requestedDate);

	/**
	 * Ön tanımlı fiyat listesi tanımını döndürür.
	 * @return price item
	 */
	PriceItem findDefaultPriceItem();

	List<PriceItem> findPriceItems(ProductGroup aGroup);
	
	List<PriceItem> findPriceItems(ProductGroup aGroup, Date aDate);	
	
	List<PriceItem> findPriceItems(Contact aContact);
	
	List<PriceItem> findPriceItems(Contact aContact, Date aDate);
	
	PriceItemDetail findSalePriceItemDetailForProduct(Product aProduct) throws Exception;
	
	PriceItemDetail findSalePriceItemDetailForProduct(Long aProduct) throws Exception;
	
	PriceItemDetail findPurchasePriceItemDetailForProduct(Product aProduct, Contact aContact) throws Exception;
	
	Double getPrice(Long productId,TradeAction trade,String definition);
	
	List<Double> getPrice(Long productId, String order ,TradeAction trade);
	
	List<String> getCurrency(Long productId, String order ,TradeAction trade);
	
	String getCurrency(Long productId,TradeAction trade,String definition);
	
}