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

package com.ut.tekir.general;

import com.ut.tekir.entities.ProductGroup;
import com.ut.tekir.framework.IEntityHome;

/**
 * @author sinan.yumak
 */
public interface ProductGroupHome<E> extends IEntityHome<E>{
    
	ProductGroup getProductGroup();
    void setProductGroup(ProductGroup productGroup);
    
    void initProductGroupList();
    
}
