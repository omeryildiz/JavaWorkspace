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

import com.ut.tekir.framework.IEntityHome;

/**
 *
 * @author Nebioglu
 */
public interface ProvinceHome<Province> extends IEntityHome<Province> {
    
    Province getProvince();
    void setProvince(Province province);
    
    void initProvinceList();
    
}
