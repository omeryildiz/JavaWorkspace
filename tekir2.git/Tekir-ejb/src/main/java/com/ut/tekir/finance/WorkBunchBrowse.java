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

import javax.ejb.Local;

import com.ut.tekir.framework.IBrowserBase;

/**
 * @author cenk.canarslan
 *
 * @param <E>
 * @param <F>
 */
@Local
public interface WorkBunchBrowse<E, F> extends IBrowserBase<E, F> {

    void refreshResults();
    void pdf();
    void detailedPdf();

}
