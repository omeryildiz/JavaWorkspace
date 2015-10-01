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

import com.ut.tekir.entities.Unit;
import com.ut.tekir.framework.IEntityHome;

/**
 *
 * @author haky
 */
public interface UnitHome<E> extends IEntityHome<E>{
    
    
    Unit getUnit();
    void setUnit(Unit unit);
    
    void initUnitList();

    
}
