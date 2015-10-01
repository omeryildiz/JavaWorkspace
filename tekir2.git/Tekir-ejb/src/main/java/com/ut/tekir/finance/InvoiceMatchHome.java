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

import com.ut.tekir.entities.Contact;
import com.ut.tekir.entities.DocumentMatch;
import com.ut.tekir.entities.DocumentType;
import com.ut.tekir.entities.TradeAction;
import com.ut.tekir.framework.IBrowserBase;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import javax.ejb.Local;

/**
 *
 * @author yigit
 */
@Local
public interface InvoiceMatchHome  <E, F> extends IBrowserBase<E, F> {

    boolean updateInvoiceDocumentMatch(Map<String, BigDecimal> updateList,DocumentType documentType,List<DocumentMatch> matchList);
    void selectInvoice(int index);
    String getInvoiceMatchObserver();
    void setInvoiceMatchObserver(String invoiceMatchObserver);
    Contact getContact();
    void setContact(Contact contact);
    TradeAction getTradeAction();
    void setTradeAction(TradeAction tradeAction);
    Boolean getDisableContactSelect();
    void setDisableContactSelect(Boolean disableContactSelect);
    
}
