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

import javax.ejb.Local;

import com.ut.tekir.entities.WorkBunch;
import com.ut.tekir.framework.IEntityHome;

/**
 * 
 * @author sinan.yumak
 */
@Local
public interface FollowUpHome<E> extends IEntityHome<E> {
/*
	WorkBunch getWorkBunch();
	void setWorkBunch(WorkBunch workBunch);

	void initFollowUpList();


    void deleteLine(int rowKey);
    void createNewLine();
    void createSubCat();
    void createIdenticalCat();
    
    String getCodePaths(WorkBunch fu);
*/    
}
