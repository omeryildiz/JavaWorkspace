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

package com.ut.tekir.report;

import com.ut.tekir.entities.DocumentType;
import com.ut.tekir.entities.TaxTxn;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author yigit
 */

@Local
public interface TaxTxnReport {

    void init();
    void destroy();
    TaxTxnFilterModel getFilterModel();
    List<TaxTxn> getResultList();
    void search();
    void pdf();
    void xls();
    DocumentType[] getDocumentTypes();
    int getExid() ;	
    void setExid(int exid);
}
