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

package com.ut.tekir.framework;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.ut.tekir.entities.Currency;
import com.ut.tekir.entities.CurrencyPair;
import com.ut.tekir.entities.CurrencyRate;
import com.ut.tekir.entities.Money;
import com.ut.tekir.entities.MoneySet;

/**
 *
 * @author haky
 */
public interface CurrencyManager {
    Money convert(Money money, Currency ccy, Date date);

    Money convert(Money money, String ccy, Date date);

    BigDecimal convert( BigDecimal value, String actualCcy, String destCcy, Date date );
    
    void destroy();

    /**
     * Verilen iki ccy koduna denk currencyPair'i bulur eğer bulamaz ise geriye null döner.
     */
    CurrencyPair getCurrencyPair(String ccy1, String ccy2);

    CurrencyRate getCurrencyRate(Date date, CurrencyPair ccyPair);

    CurrencyRate getCurrencyRate(Date date, String ccy1, String ccy2);
    CurrencyRate getLocalCurrencyRate( Date date, String ccy );
    CurrencyRate getLocalCurrencyTodayRate( String ccy );

    List<CurrencyRate> getCurrencyRates(Date date);
    List<CurrencyRate> getTodayCurrencyRates();

    String getLocaleCurrency();
    
    BigDecimal getLocalAskRate( String ccy );
    BigDecimal getLocalBidRate( String ccy );
    BigDecimal convertToLocal( BigDecimal amt, String ccy, Date date );
    BigDecimal convertToLocal( BigDecimal amt, String ccy );

    void initManager();

    void setLocaleCurrency(String localeCurrency);
    
    Money convertLocale( Money money, Date date );
    Money convertLocale( Money money );
    Currency getCurrency( String ccy );

    boolean isTodayRatesRecorded();

    void setLocalAmountOf(MoneySet money, Date aDate);
    void setCurrencyValue(MoneySet money, String destCcy, Date date);
}
