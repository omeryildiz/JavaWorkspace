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
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;

import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.TemporalType;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.log.Log;

import com.ut.tekir.entities.Currency;
import com.ut.tekir.entities.CurrencyPair;
import com.ut.tekir.entities.CurrencyRate;
import com.ut.tekir.entities.Money;
import com.ut.tekir.entities.MoneySet;
import com.ut.tekir.util.StringUtils;

/**
 * Uygulama içindeki döviz ile ilgili işlemleri kapsar
 * @author haky
 */
@Stateful()
@Name( "currencyManager")
@Scope(value=ScopeType.APPLICATION)
@AutoCreate
public class CurrencyManagerBean implements CurrencyManager {
    
    @Logger
    protected Log log;
    
    @PersistenceContext(type=PersistenceContextType.EXTENDED)
    protected EntityManager em;
    
    @In
    protected FacesMessages facesMessages;
    
    @In
    protected CalendarManager calendarManager;
    
    //FIXME: Bu değer parametrelerden ( optionManager?! ) setlenecek
    private String localeCurrency = BaseConsts.SYSTEM_CURRENCY_CODE;
    
    @Create
    public void initManager(){
        
    }
    
    @Remove @Destroy
    public void destroy() {
    }
    
    public String getLocaleCurrency() {
        return localeCurrency;
    }
    
    public void setLocaleCurrency(String localeCurrency) {
        this.localeCurrency = localeCurrency;
    }
    
    /**
     * Verilen tarihteki kurları döndürür
     * 
     * @param date
     * @return
     */
    @SuppressWarnings("unchecked")
	public List<CurrencyRate> getCurrencyRates( Date date ){
        return em.createQuery( "select c from CurrencyRate c where date = :date ")
        .setParameter( "date", date, TemporalType.DATE)
        .setHint("org.hibernate.cacheable", true)
        .getResultList();
    }
    
    /**
     * Sistem Tarihinin Kurlarını döndürür
     * @return
     */
    public List<CurrencyRate> getTodayCurrencyRates(){
        return getCurrencyRates( calendarManager.getCurrentDate());
    }
    
    
    /**
     * Verilen iso string koduna göre Döviz modelini döndürür. Eğer bulamazsa null döner.
     * @param ccy
     * @return
     */
    public Currency getCurrency( String ccy ){
        if( StringUtils.isEmpty(ccy) ){
            
            return null;
        }
        
        try{
            return (Currency) em.createQuery("select c from Currency c where code = :ccy")
            .setParameter("ccy", ccy)
            .setHint("org.hibernate.cacheable", true)
            .getSingleResult();
        } catch ( Exception e ){
            log.debug("Hata : ", e );
            log.debug("Döviz tanımı bulunamadı : #0", ccy );
            return null;
        }
    }
    
    /**
     * Verilen iki ccy koduna denk currencyPair'i bulur eğer bulamaz ise geriye null döner.
     */
    public CurrencyPair getCurrencyPair( String ccy1, String ccy2 ){
    
        Currency cc1 = getCurrency( ccy1 );
        Currency cc2 = getCurrency( ccy2 );
        
        return getCurrencyPair( cc1, cc2 );
    }
    
    /**
     * Verilen iki döviz koduna göre Döviz Çiftini döndürür. Bulamazsa null döner.
     * @param ccy1
     * @param ccy2
     * @return
     */
    public CurrencyPair getCurrencyPair( Currency ccy1, Currency ccy2 ){
        //Dövizlerin birinin boş olması ya da aynı olmaları durumunda geriye null döner.
        if ( ccy1 == null || ccy2 == null || ccy1.equals(ccy2)){
            return null;
        }
        
        try{
            return (CurrencyPair) em.createQuery("select c from CurrencyPair c where ( hardCurrency = :ccy1 and weakCurrency = :ccy2 ) or ( hardCurrency = :ccy2 and weakCurrency = :ccy1 )")
            .setParameter("ccy1", ccy1)
            .setParameter("ccy2", ccy2)
            .setHint("org.hibernate.cacheable", true)
            .getSingleResult();
        } catch ( Exception e ){
            log.debug("Hata : ", e );
            log.debug("Döviz çifti bulunamadı : #0-#1", ccy1, ccy2 );
            return null;
        }
    }
    
    /**
     * Verilen tarihteki verilen döviz çifti için kur bilgisi döndürür.
     */
    public CurrencyRate getCurrencyRate( Date date, CurrencyPair ccyPair ){
        //Gelen parametrelerden biri nullsa geriye null döner
        if ( date == null || ccyPair == null ){
            return null;
        }
        
        try{
            return (CurrencyRate) em.createQuery( "select c from CurrencyRate c where date = :date and currencyPair = :ccyPair ")
            .setParameter( "date", date, TemporalType.DATE)
            .setParameter( "ccyPair", ccyPair )
            .setHint("org.hibernate.cacheable", true)
            .getSingleResult();
        } catch ( Exception e ){
            log.debug("Hata : ", e );
            log.debug("Kur bulunamadı : #0-#1 ", ccyPair, date );
            return null;
        }
    }
    
    /**
     * Verilen tarih için verilen döviz ile yerel döviz arasındaki kur bilgisini döndürür.
     * 
     * Örneğin USD verilirse USD-YTL kur bilgisi döner.
     * 
     * @param date kur tarihi
     * @param ccy döviz türü
     * @return
     */
    public CurrencyRate getLocalCurrencyRate( Date date, String ccy ){
        return getCurrencyRate(date, getCurrencyPair( ccy, getLocaleCurrency() ));
    }
    
    /**
     * Sistem tarihi için verilen döviz ile yerel döviz arasındaki kur bilgisini döndürür. 
     * @param ccy
     * @return
     */
    public CurrencyRate getLocalCurrencyTodayRate( String ccy ){
        return getCurrencyRate(calendarManager.getCurrentDate(), getCurrencyPair( ccy, getLocaleCurrency() ));
    }
    
    /**
     * Verilen tarih ve döviz çifti için kur bilgisi döndürür.
     * 
     * @param date
     * @param ccy1
     * @param ccy2
     * @return
     */
    public CurrencyRate getCurrencyRate( Date date, String ccy1, String ccy2 ){
        CurrencyPair ccyPair = getCurrencyPair( ccy1, ccy2 );
        return getCurrencyRate( date, ccyPair );
    }
    
    /**
     * Yerel Parabirimi ve Sistem tarihi için alış fiyatı döndürür
     * @param ccy
     * @return
     */
    public BigDecimal getLocalAskRate( String ccy ){
        if( getLocaleCurrency().equals(ccy)) return BigDecimal.ONE;
        CurrencyRate cr = getLocalCurrencyTodayRate( ccy );
        return ( cr == null ) ? BigDecimal.ZERO : cr.getAsk();
    }
    
    /**
     * Yerel Parabirimi ve Sistem tarihi için alış fiyatı döndürür
     * 
     * @param ccy
     * @return
     */
    public BigDecimal getLocalBidRate( String ccy ){
        if( getLocaleCurrency().equals(ccy)) return BigDecimal.ONE;
        CurrencyRate cr = getLocalCurrencyTodayRate( ccy );
        return ( cr == null ) ? BigDecimal.ZERO : cr.getBid();
    }
    
    /**
     * Verilen parabiri tutarı verilen tarih kurunu kullanarak yerel para birimine çevirir.
     * 
     * @param amt Çevrilecek tutar
     * @param ccy Çevrilecek olan para birimi
     * @param date Kur tarihi
     * @return
     */
    public BigDecimal convertToLocal( BigDecimal amt, String ccy, Date date ){
        if( getLocaleCurrency().equals(ccy)) return amt;
        CurrencyRate cr = getCurrencyRate( date, ccy, getLocaleCurrency());
        return ( cr == null ) ? BigDecimal.ZERO : amt.multiply( cr.getAsk() );
    }
    
    /**
     * Verilen parabiri tutarını sistem tarihi kurunu kullanarak yerel para birimine çevirir.
     * 
     * @param amt Çevrilecek tutar
     * @param ccy Çevrilecek olan para birimi
     * @param date Kur tarihi
     * @return
     */
    public BigDecimal convertToLocal( BigDecimal amt, String ccy ){
        return convertToLocal( amt, ccy, calendarManager.getCurrentDate());
    }

    /**
     * Verilen para tutarını verilen tarih kuru ile verilen para birimine çevirir.
     * 
     * @param money Çevrilecek tutar
     * @param ccy Çevrilecek olan para birimi
     * @param date Kur tarihi
     * @return
     */
    public Money convert( Money money, String ccy, Date date ){
        log.debug("Çevrim değerleri #0, #1, #2", money, ccy, date );
        
        Money m = new Money(ccy);
        m.setValue( convert(money.getValue(), money.getCurrency(), ccy, date) );
        
        log.debug("Döviz çevrim işlemi #0 => #1", money, m );
        return m;
    }

    public BigDecimal convert( BigDecimal value, String actualCcy, String destCcy, Date date ){
        
        if( actualCcy.equals( destCcy ) ){
            //Dövizler aynı ise değer aynıdır...
            log.debug("Dövizler aynı. Kur : 1 ");
            return value;
        }
        
        //Parametrelerden biri null ise sonuç null
        if ( value == null || destCcy == null || date == null ){
            log.debug("Parametreler eksik : #0, #1, #2", value, destCcy, date );
            facesMessages.add( "Parametreler eksik. Döviz çevrimi yapılamadı.");
            throw new RuntimeException("Parametreler eksik."); 
        }
        
        CurrencyPair ccyPair = getCurrencyPair( actualCcy, destCcy);
        if ( ccyPair == null ){
            log.debug("Döviz çifti bulunamadı : #0-#1", actualCcy, destCcy );
            facesMessages.add( "Döviz çifti bulunamadı. Döviz çevrimi yapılamadı.");
            throw new RuntimeException("Döviz çifti bulunamadı."); 
        }

        CurrencyRate ccyRate = getCurrencyRate( date, ccyPair );
        if ( ccyRate == null ){
            log.debug("Kur bulunamadı : #0-#1", ccyPair, date );
            facesMessages.add( "Verilen tarih için kur bulunamadı. Döviz çevrimi yapılamadı.", 3);
            throw new RuntimeException("Verilen tarih için kur bulunamadı."); 
        }

        //FIXME: ask veya bid kullanım bilgisi almalı.
        if( ccyPair.getHardCurrency().getCode().equals(actualCcy)){
            //Çarpacağız
        	return value.multiply(ccyRate.getAsk());
        } else {
            //Böleceğiz
        	return value.divide( ccyRate.getAsk(),2,RoundingMode.HALF_UP);
        }
    }
        
	public void setCurrencyValue(MoneySet money, String destCcy, Date date) {
		money.setValue( convert(money.getValue(), destCcy, money.getCurrency(), date) );
	}

    /**
     * Verilen para tutarını verilen tarih kuru ile verilen para birimine çevirir.
     * 
     * @param money Çevrilecek tutar
     * @param ccy Çevrilecek olan para birimi
     * @param date Kur tarihi
     * @return
     */
    public Money convert( Money money, Currency ccy, Date date ){
        return convert( money, ccy.getCode(), date );
    }
    
    /**
     * Verilen değeri günün tarihinde locale döviz türüne çevirir.
     *
     */
    public Money convertLocale( Money money ){
        return convert( money, getLocaleCurrency(), calendarManager.getCurrentDate() );
    }
    
    /**
     * Verilen değeri günün tarihinde locale döviz türüne çevirir.
     *
     */
    public Money convertLocale( Money money, Date date ){
        return convert( money, getLocaleCurrency(), date );
    }
    
    /**
     * Verilen parabiriminin yerel parabirimi olup/olmadığını kontrol eder
     * 
     * @param currency
     * @return
     */
    public boolean isLocalCurrency(String currency) {
    	return (currency != null && currency.equals(BaseConsts.SYSTEM_CURRENCY_CODE));
    }
    
    /**
     * Günün kurlarının girilip girilmediği bigisini döner
     * @return eğer günlük kur girişi varsa true aksi halde false
     */
    public boolean isTodayRatesRecorded(){
        return getTodayCurrencyRates().size() > 0;
    }

	public void setLocalAmountOf(MoneySet money, Date date) {
		BigDecimal convertedValue = convertToLocal(money.getValue(), money.getCurrency(), date);
		
		convertedValue = convertedValue.setScale(2, RoundingMode.HALF_UP);
		money.setLocalAmount(convertedValue);
	}

}
