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

import javax.ejb.Local;

import com.ut.tekir.entities.CountNote;
import com.ut.tekir.entities.ProductCategory;
import com.ut.tekir.entities.ProductGroup;
import com.ut.tekir.framework.IEntityBase;

/**
 *
 * @author sinan.yumak
 */
@Local
public interface CountNoteHome<E> extends IEntityBase<E> {
    
	void init();

	CountNote getCountNote();
    void setCountNote(CountNote CountNote);

    void createNewLine();
    void deleteLine( Integer ix );
    void manualFlush();
    
	List<CountNoteCompareModel> getCompareList();
	void setCompareList(List<CountNoteCompareModel> compareList);
	
	void setBarcodeInput();
	void setProductInput();
	
	void addItemAction();

	void setInputModel(CountNoteInputModel inputModel);

	CountNoteInputModel getInputModel();
	
	void makeUpTheDifference();
	void openForEditing();
	
	int getRowCount();
	void setRowCount(int rowCount);

	void addUncountedProducts();

	ProductGroup getGroup();
	void setGroup(ProductGroup group);
	
	ProductCategory getCategory();
	void setCategory(ProductCategory category);

	void toggleChecboxes();

	boolean isCheckboxSelected();
	void setCheckboxSelected(boolean checkboxSelected);

	void addWithBarcode();
	
	void addItemByScanner();

	void applyFilter();
}