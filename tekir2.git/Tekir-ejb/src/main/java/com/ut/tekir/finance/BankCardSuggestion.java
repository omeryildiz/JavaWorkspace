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

import com.ut.tekir.entities.Bank;
import com.ut.tekir.entities.BankCard;
import com.ut.tekir.entities.CardFundingType;
import com.ut.tekir.entities.CardType;
import com.ut.tekir.entities.Country;

/**
 *
 * @author sinan.yumak
 */
@Local
public interface BankCardSuggestion {

    List<BankCard> suggestBankCard(Object event);
    
	List<BankCard> getBankCardList();

	void setBankCardList(List<BankCard> bankCard);
    
    void selectBankCardList();
    
    String getCode();
    void setCode(String code);

    String getName();
	void setName(String name);
	
    void destroy();
    
	void setObserverString(String observerString);
	
	void sendSelectedCardsToHomeBean();
	
	Bank getBank();
	void setBank(Bank bank);

	Country getCountry();
	void setCountry(Country country);

	CardFundingType getFundingType();
	void setFundingType(CardFundingType fundingType);

	CardType getCardType();
	void setCardType(CardType cardType);
}
