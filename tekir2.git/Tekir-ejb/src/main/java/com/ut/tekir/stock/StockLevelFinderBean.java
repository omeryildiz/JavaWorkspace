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

import java.util.ArrayList;
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

import com.ut.tekir.entities.Product;
import com.ut.tekir.entities.TradeAction;
import com.ut.tekir.entities.Warehouse;

/**
 * İstenen ürünün depoda ne kadar kaldığını bulan
 * sınıftır.
 * @author sinan.yumak
 *
 */
@Stateless
@Name("stockLevelFinder")
@Scope(ScopeType.EVENT)
@AutoCreate
public class StockLevelFinderBean implements StockLevelFinder {

	private static final long serialVersionUID = 1L;

	@In
	EntityManager entityManager;
	@Logger
	Log log;

	@SuppressWarnings("unchecked")
	private List<StockLevel> getStockLevelList(Product aProduct, Warehouse warehouse) {
		List<StockLevel> resultList = new ArrayList<StockLevel>();
		
		List<Object[]> queryList = entityManager.createQuery("select sum(p.quantity.value),p.action from ProductTxn p " +
								  "where p.product= :product and " +
								  "p.warehouse= :warehouse " +
								  "group by p.action")
								  .setParameter("product", aProduct)
								  .setParameter("warehouse", warehouse)
								  .getResultList();
		
		StockLevel sl = null;
		TradeAction action = null;
		for (Object[] item : queryList) {
			
			action = (TradeAction)item[1];
			if (action != null) {
				sl = new StockLevel();
				
				sl.setCount((Double)item[0]);
				sl.setAction(action);
				resultList.add(sl);
			}
		}
		return resultList;
	}
	
	private double getLevel(List<StockLevel> levelList, TradeAction action) {
		log.debug("Getting level for action : {0}", action);
		
		for (StockLevel sl : levelList) {
			if (sl.getAction().equals(action)) return sl.getCount();
		}
		log.debug("Level not found for action:{0}. Returning zero.", action);
		return 0d;
	}
	
	@Override
	public double findActiveLevel(Product aProduct, Warehouse warehouse) {
		List<StockLevel> levelList = getStockLevelList(aProduct, warehouse);
		
		double purchaseLevel = getLevel(levelList, TradeAction.Purchase);
		double saleLevel = getLevel(levelList, TradeAction.Sale);
		double reservedLevel = getLevel(levelList, TradeAction.Reserved);
		double deliveredLevel = getLevel(levelList, TradeAction.Delivered);
		double saleReturnLevel = getLevel(levelList, TradeAction.SaleReturn);
		double purchaseReturnLevel = getLevel(levelList, TradeAction.PurchseReturn);
		
		
		double activeLevel = ( purchaseLevel + deliveredLevel + saleReturnLevel ) - 
							 ( saleLevel + reservedLevel + purchaseReturnLevel ); 
		
		log.info("Product :{0} - Active level :{1}", aProduct.getCaption(), activeLevel);
	
		return activeLevel;
	}

	/**
	 * Rezerve ve teslim edilen seviyelerin düşüldüğü stok seviyesini döndürür.
	 */
	public double findStockLevel(Product aProduct, Warehouse warehouse) {
		List<StockLevel> levelList = getStockLevelList(aProduct, warehouse);
		
		double purchaseLevel = getLevel(levelList, TradeAction.Purchase);
		double saleLevel = getLevel(levelList, TradeAction.Sale);
		double saleReturnLevel = getLevel(levelList, TradeAction.SaleReturn);
		double purchaseReturnLevel = getLevel(levelList, TradeAction.PurchseReturn);
		
		
		double stockLevel = ( purchaseLevel + saleReturnLevel ) - ( saleLevel + purchaseReturnLevel ); 
		
		log.info("Product :{0} - stock level :{1}", aProduct.getCaption(), stockLevel);
		
		return stockLevel;
	}

	@Override
	public double findReservedLevel(Product aProduct, Warehouse warehouse) {
		// TODO Auto-generated method stub
		return 0d;
	}

	class StockLevel {
		private TradeAction action;
		private Double count;
		public TradeAction getAction() {
			return action;
		}
		public void setAction(TradeAction action) {
			this.action = action;
		}
		public Double getCount() {
			return count;
		}
		public void setCount(Double count) {
			this.count = count;
		}
	}
}

