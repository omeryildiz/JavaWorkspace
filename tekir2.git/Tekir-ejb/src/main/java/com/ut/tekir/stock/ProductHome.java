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

import com.ut.tekir.entities.Product;
import com.ut.tekir.framework.IEntityBase;
import javax.ejb.Local;

/**
 *
 * @author haky
 */
@Local
public interface ProductHome<E> extends IEntityBase<E> {

    void init();
    
    Product getProduct();
    void setProduct(Product product);
    
    void manualFlush();
    
    void createNewLine();
    void deleteLine(Integer ix);
    
    void createNewDetailLine();
    void deleteDetailLine(Integer ix);
    
    void createDefaultUnitLine();
    Boolean isDefault();
    
    void takeMetricalConvert();
    
    void sendToBarcodeSpool();
    
	int getCount();
	void setCount(int count);
	
	void copyNameToLabelName();
	
	void copyBuyTaxesToSellTaxes();
	
	void generateBarcode();
}
