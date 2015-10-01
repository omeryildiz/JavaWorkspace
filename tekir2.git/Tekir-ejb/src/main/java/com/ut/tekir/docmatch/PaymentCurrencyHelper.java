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

package com.ut.tekir.docmatch;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.jboss.seam.Component;
import org.jboss.seam.log.Log;
import org.jboss.seam.log.Logging;

import com.ut.tekir.configuration.SystemConfiguration;
import com.ut.tekir.entities.CurrencyPair;
import com.ut.tekir.entities.CurrencyRate;
import com.ut.tekir.entities.CurrencyRateType;
import com.ut.tekir.entities.DocumentMatch;
import com.ut.tekir.entities.Money;
import com.ut.tekir.entities.MoneySet;
import com.ut.tekir.entities.Payment;
import com.ut.tekir.entities.PaymentCurrencyRate;
import com.ut.tekir.entities.PaymentItem;
import com.ut.tekir.framework.BaseConsts;
import com.ut.tekir.framework.CurrencyManager;

//TODO:performans açısından birçok şey yapılabilir.

/**
 * Tahsilat ve tediye fişlerinin döviz kuru, çevrim vb. işlerini
 * yapan sınıftır.
 * 
 * @author sinan.yumak
 *
 */
public class PaymentCurrencyHelper implements Serializable {
	private static final long serialVersionUID = 1L;

	private Log log = Logging.getLog(PaymentCurrencyHelper.class);
	private CurrencyManager currencyManager;
	
	private Payment payment;//tahsilat veya tediye
	private List<List<DocumentMatch>> matches;
	
	public PaymentCurrencyHelper() {
		initCurrencyManager();
	}

	public PaymentCurrencyHelper(Payment payment) {
		this();
		this.payment = payment;
	}

	public PaymentCurrencyHelper(Payment payment, List<List<DocumentMatch>> matches) {
		this(payment);
		this.matches = matches;
	}

	private void initCurrencyManager() {
		currencyManager = (CurrencyManager)Component.getInstance("currencyManager");
	}

	public static PaymentCurrencyHelper instance() {
		return new PaymentCurrencyHelper();
	}

	public static PaymentCurrencyHelper instance(Payment payment) {
		return new PaymentCurrencyHelper(payment);
	}

	public static PaymentCurrencyHelper instance(Payment payment, List<List<DocumentMatch>> matches) {
		return new PaymentCurrencyHelper(payment, matches);
	}

	public BigDecimal convertToLocale(BigDecimal value, String currency) {
		return !currency.equals(BaseConsts.SYSTEM_CURRENCY_CODE) ? value.multiply(getCurrencyRate(currency))
																 : value;
	}

	public BigDecimal convertToLocale(MoneySet money) {
		return !money.getCurrency().equals(BaseConsts.SYSTEM_CURRENCY_CODE) ? money.getValue().multiply(getCurrencyRate(money))
																			: money.getValue();
	}

	private BigDecimal getCurrencyRate(String currency) {
		return findCurrencyRate(currency, payment.getRateType());
	}

	private BigDecimal getCurrencyRate(MoneySet money) {
		return findCurrencyRate(money.getCurrency(), payment.getRateType());
	}

	private BigDecimal findCurrencyRate(String currency, CurrencyRateType rateType) {
		BigDecimal result = null;
		
		result = getRateFromDocumentCurrencyRates(currency, rateType);

		if (result == null) {
			result = getRateFromTCMB(currency, rateType);
		}
		return result;
	}

	private BigDecimal getRateFromDocumentCurrencyRates(String currency, CurrencyRateType rateType) {
		for (PaymentCurrencyRate pcr :payment.getCurrencyRates()) {
			if (pcr.getCurrencyPair().getHardCurrency().getCode().equals(currency)) {
				if (rateType.equals(CurrencyRateType.Ask)) {
					return pcr.getAsk().setScale(4, RoundingMode.HALF_UP);
				} else {
					return pcr.getBid().setScale(4, RoundingMode.HALF_UP);
				}
			}
		}
		return null;
	}

	private BigDecimal getRateFromTCMB(String currency, CurrencyRateType rateType) {
		CurrencyRate rate = currencyManager.getCurrencyRate(payment.getDate(), currency, BaseConsts.SYSTEM_CURRENCY_CODE);
		if (rate != null) {
			if (rateType.equals(CurrencyRateType.Ask)) {
				return rate.getAsk().setScale(4, RoundingMode.HALF_UP);
			} else {
				return rate.getBid().setScale(4, RoundingMode.HALF_UP);
			}
		}
		return null;
	}
	
	public Money convertToCurrency(MoneySet money, String currency) {
		Money result = new Money(currency);
		result.setValue( convertToCurrency(money.getLocalAmount(), money.getCurrency(), currency) );
		return result;
	}

	public BigDecimal convertToCurrency(MoneySet money, String actualCcy, String destCcy) {
		BigDecimal convertedAmount = BigDecimal.ZERO;
		if (actualCcy != SystemConfiguration.CURRENCY_CODE) {
			convertedAmount = convertToLocale(money.getValue(), actualCcy);
		}
		if (destCcy != SystemConfiguration.CURRENCY_CODE) {
			convertedAmount = convertToCurrency(convertedAmount, SystemConfiguration.CURRENCY_CODE, destCcy);
		}
		return convertedAmount;
	}

    public BigDecimal convertToCurrency( BigDecimal value, String actualCcy, String destCcy){
    	log.info("Converting to : #0 #1", value, actualCcy);
    	if (!destCcy.equals(SystemConfiguration.CURRENCY_CODE)) {
    		BigDecimal currencyRate = getCurrencyRate(destCcy);
			if (currencyRate != null) {
				value = value.divide(currencyRate, 2, RoundingMode.HALF_UP);
			}
    	}
    	log.info("Converted: #0 #1", value, destCcy);
    	return value.setScale(2, RoundingMode.HALF_UP);
    }

	public void setLocalAmountOf(MoneySet money) {
		//FIXME: scale i 2 yerine parametrik hale getirmeli.
		money.setValue( money.getValue().setScale(2, RoundingMode.HALF_UP) );
		BigDecimal convertedValue = convertToLocale(money.getValue(), money.getCurrency());
		
		convertedValue = convertedValue.setScale(2, RoundingMode.HALF_UP);
		money.setLocalAmount(convertedValue);
	}

	public void fillCurrencyRates() {
		addItemCurrencies();
		addDocumentMatchCurrencies();
		
		//listeyi tersten kontrol ediyoruz.
		PaymentCurrencyRate pcr = null;
		for (int i=0;i<payment.getCurrencyRates().size();i++) {
			pcr = payment.getCurrencyRates().get(i);
			
			if (!(itemsContains(pcr.getCurrencyPair().getHardCurrency().getCode()) || itemsContains(pcr.getCurrencyPair().getWeakCurrency().getCode()) ||
					matchesContains(pcr.getCurrencyPair().getHardCurrency().getCode()) || matchesContains(pcr.getCurrencyPair().getWeakCurrency().getCode()))) {
				payment.getCurrencyRates().remove(i);
			}
		}
	}

	public void addItemCurrencies() {
		String itemCurrency;
		for (PaymentItem item : payment.getItems()) {
			itemCurrency = item.getAmount().getCurrency();
			if (!itemCurrency.equals(BaseConsts.SYSTEM_CURRENCY_CODE) && !ratesContains(itemCurrency) ) {
				addToRates( itemCurrency );
			}
		}
	}
	
	public void addDocumentMatchCurrencies() {
		String itemCurrency;
		for (List<DocumentMatch> dml : matches) {
			for (DocumentMatch dm : dml) {
				itemCurrency = dm.getAmount().getCurrency();
				if ( !itemCurrency.equals(BaseConsts.SYSTEM_CURRENCY_CODE) && !ratesContains(itemCurrency) ) {
					addToRates(itemCurrency);
				}
			}
		}
	}
	
	private boolean itemsContains(String currency) {
		for (PaymentItem pi : payment.getItems()) {
			if(pi.getAmount().getCurrency().equals(currency)) return true;
		}
		return false;
	}

	private boolean matchesContains(String currency) {
		for (List<DocumentMatch> dml : matches) {
			for (DocumentMatch dm : dml) {
				if(dm.getAmount().getCurrency().equals(currency)) return true;
			}
		}
		return false;
	}
	
	private boolean ratesContains(String currency) {
		for (PaymentCurrencyRate pcr : payment.getCurrencyRates()) {
			if(pcr.getCurrencyPair().getHardCurrency().getCode().equals(currency) || 
					pcr.getCurrencyPair().getWeakCurrency().getCode().equals(currency)) {
				return true;
			}
		}
		return false;
	}

	private void addToRates(String itemCurrency) {
		CurrencyPair cp = currencyManager.getCurrencyPair(itemCurrency, BaseConsts.SYSTEM_CURRENCY_CODE);
		CurrencyRate cr = currencyManager.getCurrencyRate(payment.getDate(), cp);
		
		PaymentCurrencyRate pcr = new PaymentCurrencyRate();
		pcr.setCurrencyPair(cp);

		if (cr != null) {
			pcr.setAsk( cr.getAsk().setScale(4, RoundingMode.HALF_UP));
			pcr.setBid( cr.getBid().setScale(4, RoundingMode.HALF_UP));
		}
		pcr.setPayment(payment);
		payment.getCurrencyRates().add(pcr);
	}

	public void setPayment(Payment payment) {
		this.payment = payment;
	}

}
