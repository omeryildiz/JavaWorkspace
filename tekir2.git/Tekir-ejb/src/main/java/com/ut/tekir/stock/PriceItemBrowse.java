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

import com.ut.tekir.framework.IBrowserBase;
import javax.ejb.Local;

/**
 *
 * @author sinan.yumak
 */
@Local
public interface PriceItemBrowse<E, F> extends IBrowserBase<E, F> {

    void refreshResults();
    void pdf();
    void detailedPdf();
    void xls();
}
