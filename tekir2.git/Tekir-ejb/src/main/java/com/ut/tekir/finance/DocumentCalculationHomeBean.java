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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.log.Log;

import com.ut.tekir.entities.CurrencyPair;
import com.ut.tekir.entities.CurrencyRate;
import com.ut.tekir.entities.DocumentCurrencyRateBase;
import com.ut.tekir.entities.DocumentItemBase;
import com.ut.tekir.entities.ForeignDocumentBase;
import com.ut.tekir.entities.Money;
import com.ut.tekir.framework.BaseConsts;
import com.ut.tekir.framework.CurrencyManager;
import com.ut.tekir.util.Utils;


/**
 * @author sinan.yumak
 * 
 */
@Name("documentCalculationHome")
@Scope(ScopeType.EVENT)
public class DocumentCalculationHomeBean implements DocumentCalculationHome {

    @Logger
    private Log log;

	@In
	private CurrencyManager currencyManager;

	@Override
	public void calculate() {
		// TODO Auto-generated method stub
		
	}

	public void calculateDocumentTotal(ForeignDocumentBase document) {
		log.info("calculating Document Total...");
		BigDecimal totalAmount = BigDecimal.ZERO;
		String ccy;
		for (DocumentItemBase item : document.getDocumentItemList()) {
			ccy = item.getAmount().getCurrency();
			if (Utils.isSystemCurrency(ccy)) {
				item.getAmount().setLocalAmount(item.getAmount().getValue());
			} else {
				item.getAmount().setLocalAmount( item.getAmount().getValue().multiply( findCurrencyAsk(document, ccy) ) );
			}
			totalAmount = totalAmount.add(item.getAmount().getLocalAmount());
		}
		
		totalAmount = totalAmount.setScale(2, RoundingMode.HALF_UP);
		
		document.setTotalAmount(new Money(totalAmount));
		log.info("Calculated total amount #0", totalAmount);
	}

	private BigDecimal findCurrencyAsk(ForeignDocumentBase document, String currency) {
		BigDecimal result = BigDecimal.ZERO;
		for (DocumentCurrencyRateBase it : document.getDocumentCurrencyRateList()) {
			if (it.getCurrencyPair().getHardCurrency().getCode().equals(currency)) {
				result = it.getAsk();
				break;
			}
		}

		CurrencyRate rate;
		if (result == null) {
			rate = currencyManager.getCurrencyRate(document.getDate(), currency, BaseConsts.SYSTEM_CURRENCY_CODE);
			if (rate != null) {
				result = rate.getAsk();
			}
		}
		return result;
	}

	@Override
	public void fillCurrencyRates(ForeignDocumentBase document) {
		log.info("Filling Currency Rate List...");
		
		for ( DocumentItemBase item : document.getDocumentItemList() ) {
			String currency = item.getAmount().getCurrency();

			if (!Utils.isSystemCurrency(currency) && !document.currencyRateListContains(currency)) {
				document.addToCurrencyRateList( createCurrencyRate( currency, document.getDate() ) );
			}
		}
		
		DocumentCurrencyRateBase cr;
		for (int i=0;i<document.getDocumentCurrencyRateList().size();i++) {
			cr = document.getDocumentCurrencyRateList().get(i);
			
			if (!document.itemListContains(cr.getCurrencyPair().getHardCurrency().getCode())) {
				document.removeFromCurrencyRateList(i);
			}
		}
	}

	private DocumentCurrencyRateBase createCurrencyRate(String currency, Date documentDate) {
		CurrencyPair cp = currencyManager.getCurrencyPair(currency, BaseConsts.SYSTEM_CURRENCY_CODE);
		CurrencyRate cr = currencyManager.getCurrencyRate(documentDate, cp);

		return new DocumentCurrencyRateBase(cp, cr){};
	}

}
