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

import java.util.Date;
import java.util.List;

import javax.ejb.Local;

import com.ut.tekir.entities.CurrencyRate;
import com.ut.tekir.framework.IEntityHome;

/**
 *
 * @author haky
 */
@Local
public interface CurrencyRateHome extends IEntityHome<CurrencyRate> {
    

    List<CurrencyRate> getCurrencyRateList();
    void setCurrencyRateList(List<CurrencyRate> currencyRateList);
    
    Date getRateDate();
    void setRateDate(Date rateDate);

    void showRates();
    String saveTCMB();
    
    Boolean kurKontrol();
    
    Boolean getIslemYapilmisMi();

	void setIslemYapilmisMi(Boolean islemYapilmisMi);
    
}
