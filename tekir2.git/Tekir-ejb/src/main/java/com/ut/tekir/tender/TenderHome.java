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

import java.util.List;

import javax.ejb.Local;

import com.ut.tekir.entities.TenderDetail;
import com.ut.tekir.framework.IEntityBase;

/**
 * @author sinan.yumak
 *
 */
@Local
public interface TenderHome<E> extends IEntityBase<E> {

	void init();
	
	void createNew();
	
	void createNewDetail();
	
	void deleteLine(Integer ix);
	
	void selectProduct();
	
	Boolean calculateEverything();
	
	Boolean calculateSelectedDetail();

	TenderDetail getSelectedDetail();

	void setSelectedDetail(TenderDetail selectedDetail);

	TenderDetail getSelectedDiscountOrExpense();

	void setSelectedDiscountOrExpense(TenderDetail selectedDiscountOrExpense);
	
	Boolean getIsEdit();
	
	Boolean getIsDiscountOrExpenseEdit();
	
	void addToItems();
	 
	void addDocumentDiscountOrExpenseToItems();
	
	void endDetailEdit(); 

	void editDetail(Integer rowKey);
	
	void deleteDiscountOrExpenseLine(Integer rowKey);
	
	void endDiscountOrExpenseEdit();
		
	void editDiscountOrExpenseItem(Integer rowKey);
	
	void addToDiscountOrExpenseItems();
	
	void editDocumentDiscountOrExpenseItem(Integer rowKey);
	
	void endDocumentDiscountOrExpenseEdit();
	
	void createNewItemExpense();
	
	void createNewItemDiscount();
		
	void createNewItemFee();
	
	void refreshCurrencyRate();
	
	void createDocumentDiscountLine();

	void createDocumentExpenseLine();

	void createDocumentFeeLine();
	
	List<TenderDetail> getSelectedChildList();
	
	void setSelectedChildList(List<TenderDetail> selectedChildList);
	
}