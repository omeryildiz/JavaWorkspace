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

import javax.ejb.Stateless;
import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.log.Log;

import com.ut.tekir.entities.Contact;
import com.ut.tekir.entities.PriceItem;
import com.ut.tekir.entities.PriceItemDetail;
import com.ut.tekir.entities.PriceList;
import com.ut.tekir.entities.Product;
import com.ut.tekir.entities.ProductGroup;
import com.ut.tekir.entities.ProductTxn;
import com.ut.tekir.entities.TradeAction;

/**
 * @author sinan.yumak
 *
 */
@Stateless
@Name("priceProvider")
@Scope(ScopeType.EVENT)
@AutoCreate
public class PriceProviderBean implements PriceProvider {

	@In
	EntityManager entityManager;
	@Logger
	Log log;
	
	public BigDecimal getPurchasePrice(Product product, Contact contact, Double unit, Date date) {
		//FIXME:implement me!
		return BigDecimal.ZERO;
	}

	public BigDecimal getSalePrice(Product product, Contact contact, Double unit, Date date) {
		//FIXME:implement me!
		return BigDecimal.ZERO;
	}
	
	public void findWithBarcode(String barcode, Contact contact, Double unit, Date date) {
		//FIXME:implement me!
	}

	@SuppressWarnings("unchecked")
	public List<PriceItem> getPriceItemList(PriceList priceList, Date requestedDate) {
		log.info("Querying price items with priceList :{0}, date :{1}", priceList.getListName(), requestedDate);
		List<PriceItem> itemList =  null;
		try {
			itemList =  (List<PriceItem>)entityManager.createQuery("select p from PriceItem p where  " +
												"p.owner = :priceList and " +
												"p.beginDate <= :requestedDate " +
												"order by beginDate desc")
												.setParameter("priceList", priceList)
												.setParameter("requestedDate", requestedDate)
												.getResultList();
			
		} catch (Exception e) {
			log.error("Error when querying price list with priceList :{0},requestedDate :{1}", priceList.getListName(), requestedDate);
		}
		return itemList;
	}

	public PriceItem findDefaultPriceItem() {
		log.info("Querying default price item..");

		PriceItem result = null;
		try {
			//TODO: fiyat listesi bulunurken önce ön tanımlı fiyat listesi aranmalı,
			//daha sonra markaya göre aranmalı.. şeklinde düzenlenmeli.
			result =  (PriceItem)entityManager.createQuery("select p from PriceItem p where  " +
												"p.defaultItem = :defaultItem")
												.setParameter("defaultItem", Boolean.TRUE)
												.getSingleResult();
			
		} catch (Exception e) {
			log.error("Error when querying default price item. Reason :{0}",e);
		}
		return result;
	}
	
	
	public PriceItem findPriceItem(PriceList priceList, Date requestedDate) {
		log.info("Querying price item with priceList :{0}, date :{1}", priceList.getListName(), requestedDate);
		
		PriceItem result = null;
		try {
			List<PriceItem> itemList =  getPriceItemList(priceList, requestedDate);
			
			if (itemList == null || itemList.size() == 0) {
				log.warn("Unable to find a price list");
			} else {
				result = itemList.get(0);
				log.info("Found price item :{0}", result.info());
			}
		} catch (Exception e) {
			log.error("Error when querying price list with priceList :{0},requestedDate :{1}", priceList.getListName(), requestedDate);
		}
		return result;
	}
	
	public Product findProductWithBarcode(String barcode) {
		log.info("Querying product with barcode :{0}", barcode);
		
		Product result = null;
		try {
			result = (Product) entityManager.createQuery("select p from Product p where  " +
												"p.barcode1 like :barcode or " +
												"p.barcode2 like :barcode or " +
												"p.barcode3 like :barcode")
												.setParameter("barcode", barcode)
												.getSingleResult();

			log.info("Found product :{0}", result.getName());
		} catch (Exception e) {
			log.error("Unable to find product/service with barcode = {0}",barcode);
		}
		return result;
	}

	public List<PriceItem> findPriceItems(ProductGroup aGroup) {
		return findPriceItems(aGroup, null);
	}
	
	public List<PriceItem> findPriceItems(Contact aContact) {
		return findPriceItems(aContact, null);
	}
	
	@SuppressWarnings("unchecked")
	public List<PriceItem> findPriceItems(Contact aContact, Date aDate) {
		Date requestedDate = new Date();
		if (aDate != null) {
			requestedDate = aDate;
		}
		
		List<PriceItem> itemList =  null;
		try {
			String contactQuery = "";
			if(aContact != null) {
				contactQuery = "pi.contact.id=" + aContact.getId() + " and ";
			} else {
				contactQuery = "pi.contact.id is null and ";
			}
			
			itemList = (List<PriceItem>)entityManager.createQuery("select pi from PriceItem pi where " +
																  contactQuery +
																  "pi.beginDate <= :requestedDate " +
																  "ORDER BY beginDate DESC")
																.setParameter("requestedDate", requestedDate)
																.getResultList();
			
		} catch (Exception e) {
			log.error("Error when querying price list with contact :{0},requestedDate :{1}", aContact.getFullname(), requestedDate);
		}
		return itemList;
	}

	@SuppressWarnings("unchecked")
	public List<PriceItem> findPriceItems(ProductGroup aGroup, Date aDate) {
		Date requestedDate = new Date();
		if (aDate != null) {
			requestedDate = aDate;
		}
		
		List<PriceItem> itemList =  null;
		try {
	    	String groupQuery = "";
	    	if(aGroup != null) {
	    		groupQuery = "pi.group.id=" + aGroup.getId() + " and ";
	    	} else {
	    		groupQuery = "pi.group.id is null and ";
	    	}
	    	
	    	itemList = (List<PriceItem>)entityManager.createQuery("select pi from PriceItem pi where " +
			    							     groupQuery +
			    							     "pi.beginDate <= :requestedDate " +
			    							     "ORDER BY beginDate DESC")
			    							     .setParameter("requestedDate", requestedDate)
			    							     .getResultList();
			
		} catch (Exception e) {
			log.error("Error when querying price list with product group :{0},requestedDate :{1}", aGroup.getName(), requestedDate);
		}
		return itemList;
	}

	public PriceItemDetail findSalePriceItemDetailForProduct(Product aProduct) throws Exception {
		return findPriceItemDetailForProduct(TradeAction.Sale, aProduct, null);
	}

	public PriceItemDetail findSalePriceItemDetailForProduct(Long productId) throws Exception {
		return findPriceItemDetailForProduct(TradeAction.Sale, productId, null);
	}

	public PriceItemDetail findPurchasePriceItemDetailForProduct(Product aProduct, Contact aContact) throws Exception {
		return findPriceItemDetailForProduct(TradeAction.Purchase, aProduct, aContact);
	}
	
	/**
	 * @param action, istenen fiyat listesinin türü
	 * @param aProduct, istenen ürün
	 * @param aContact, istenen tedarikçi
	 * @return price item detail, bulunan fiyat listesi satırı
	 * @throws Exception
	 */
	private PriceItemDetail findPriceItemDetailForProduct(TradeAction action, Product aProduct, Contact aContact) throws Exception {
		log.info("Getting default price item for product group. id:{0}", aProduct.getCaption());
		
		List<PriceItem> resultList; 
		if (action.equals(TradeAction.Sale)) {
			resultList = findPriceItems(aProduct.getGroup());
		} else {
			resultList = findPriceItems(aContact);
		}

		if (resultList == null || resultList.size() == 0) {
			
			StringBuilder errorString = new StringBuilder();
			if (action.equals(TradeAction.Sale)) {
				ProductGroup pg = aProduct.getGroup();
				if (pg == null) {
					errorString.append("Fiyat listesi bulunamadı.");
				} else {
					errorString.append("'")
							   .append(pg.getCode())
							   .append("' markası için fiyat listesi bulunamadı.");
				}
			} else {
				if (aContact == null) {
					errorString.append("Fiyat listesi bulunamadı.");
				} else {
					errorString.append("'")
							   .append(aContact.getFullname())
							   .append("' tedarikçisi için fiyat listesi bulunamadı.");
				}
			}
			throw new PriceNotFoundException(errorString.toString());
		}
		PriceItemDetail detail = null;
		for (PriceItem parent : resultList) {
			detail = parent.findItemWithProduct(aProduct);

			if (detail != null) break;
		}
		
		if (detail == null) {
			StringBuilder sb = new StringBuilder();
			
			for (PriceItem parent : resultList) {
				sb.append("'")
				  .append(parent.getCode())
				  .append("'");
			}
			sb.append(" kodlu fiyat listeleri içerisinde ");
			sb.append(aProduct.getName());
			sb.append(" ürünü ile ilgili fiyat bulunamadı!");
			
			throw new PriceNotFoundException(sb.toString());
		}
		return detail;
	}
	
	private PriceItemDetail findPriceItemDetailForProduct(TradeAction action, Long aProduct, Contact aContact) throws Exception {
		log.info("Getting default price item for product group. id:{0}", aProduct);
		
		Product prod = (Product)entityManager.createQuery("select c from Product c where id =:id").setParameter("id", aProduct).getSingleResult();
		
		List<PriceItem> resultList; 
		if (action.equals(TradeAction.Sale)) {
			resultList = findPriceItems(prod.getGroup());
		} else {
			resultList = findPriceItems(aContact);
		}

		if (resultList == null || resultList.size() == 0) {
			
			StringBuilder errorString = new StringBuilder();
			if (action.equals(TradeAction.Sale)) {
				ProductGroup pg = prod.getGroup();
				if (pg == null) {
					errorString.append("Fiyat listesi bulunamadı.");
				} else {
					errorString.append("'")
							   .append(pg.getCode())
							   .append("' markası için fiyat listesi bulunamadı.");
				}
			} else {
				if (aContact == null) {
					errorString.append("Fiyat listesi bulunamadı.");
				} else {
					errorString.append("'")
							   .append(aContact.getFullname())
							   .append("' tedarikçisi için fiyat listesi bulunamadı.");
				}
			}
			throw new PriceNotFoundException(errorString.toString());
		}
		PriceItemDetail detail = null;
		for (PriceItem parent : resultList) {
			detail = parent.findItemWithProductId(aProduct);

			if (detail != null) break;
		}
		
		if (detail == null) {
			StringBuilder sb = new StringBuilder();
			
			for (PriceItem parent : resultList) {
				sb.append("'")
				  .append(parent.getCode())
				  .append("'");
			}
			sb.append(" kodlu fiyat listeleri içerisinde ");
			sb.append(prod.getName());
			sb.append(" ürünü ile ilgili fiyat bulunamadı!");
			
			throw new PriceNotFoundException(sb.toString());
		}
		return detail;
	}

	
	
	public Double getPrice(Long productId,TradeAction trade,String definition){
		
		return (Double)entityManager.createQuery("select "+definition+"(c.unitPrice.value/c.quantity.value) as deger from ProductTxn c where c.product.id =:product_id and action =:trade_action")
						.setParameter("product_id", productId)
						.setParameter("trade_action", trade)
						.getSingleResult();
		  
		 
	}
	
	@SuppressWarnings("unchecked")
	public List<Double> getPrice(Long productId, String order ,TradeAction trade){
		
		if(order != null && order.equals("asc")){
			return entityManager.createQuery("select (c.unitPrice.value/c.quantity.value) as deger from ProductTxn c where c.product.id =:product_id and c.action =:trade_action order by c.createDate asc")
			.setParameter("product_id", productId)
			.setParameter("trade_action", trade)
			.getResultList();
		}
		else{
			return entityManager.createQuery("select (c.unitPrice.value/c.quantity.value) as deger from ProductTxn c where c.product.id =:product_id and c.action =:trade_action order by c.createDate desc")
			.setParameter("product_id", productId)
			.setParameter("trade_action", trade)
			.getResultList();
		}
		
	}
	
	
	@SuppressWarnings("unchecked")
	public List<String> getCurrency(Long productId, String order ,TradeAction trade){
		
		if(order != null && order.equals("asc")){
			return entityManager.createQuery("select c.unitPrice.currency from ProductTxn c where c.product.id =:product_id and action =:trade_action order by c.createDate asc")
			.setParameter("product_id", productId)
			.setParameter("trade_action", trade)
			.getResultList();
		}
		else{
			return entityManager.createQuery("select c.unitPrice.currency from ProductTxn c where c.product.id =:product_id and action =:trade_action order by c.createDate desc")
			.setParameter("product_id", productId)
			.setParameter("trade_action", trade)
			.getResultList();
		}
		
	}
	
public String getCurrency(Long productId,TradeAction trade,String definition){
		
		return (String)entityManager.createQuery("select c.unitPrice.currency from ProductTxn c where c.product.id =:product_id and action =:trade_action and c.unitPrice.value/c.quantity.value = (select "+definition+"(c.unitPrice.value/c.quantity.value) as deger from ProductTxn c where c.product.id =:product_id and action =:trade_action)")
						.setParameter("product_id", productId)
						.setParameter("trade_action", trade)
						.getSingleResult();
		  
		 
	}
	
}
