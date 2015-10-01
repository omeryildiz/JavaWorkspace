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

package com.ut.tekir.finance;

import java.util.List;

import javax.ejb.Local;

import com.ut.tekir.entities.WorkBunch;
import com.ut.tekir.framework.IEntityBase;

/**
 *
 * @author sinan.yumak
 */
@Local
public interface WorkBunchHome<E> extends IEntityBase<E> {

	void init();
	void manualFlush();
    
    WorkBunch getWorkBunch();
    void setWorkBunch(WorkBunch workBunch);
    /**
     * FINANCE_TXN, PRODUCT_TXN ve BANK_TXN tablolarindan ilgili Work Bunch'a 
     * ait history listesini doner 
     * 
     * @param wbid
     * @return list of Work Bunch History
     */
    List<WorkBunchViewModel> workBunchHistory(Long wbid);
}
