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

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.persistence.EntityManager;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.Events;
import org.jboss.seam.log.Log;
import org.jboss.seam.persistence.HibernateSessionProxy;

import com.ut.tekir.entities.Bank;
import com.ut.tekir.entities.BankCard;
import com.ut.tekir.entities.CardFundingType;
import com.ut.tekir.entities.CardType;
import com.ut.tekir.entities.Country;

/**
 *
 * @author sinan.yumak
 */
@Stateful
@Name("bankCardSuggestion")
@Scope(ScopeType.CONVERSATION)
@AutoCreate
public class BankCardSuggestionBean implements BankCardSuggestion {

    @Logger
    Log log;
    @In
    Events events;
    @In
    EntityManager entityManager;
 
	private List<BankCard> bankCardList;
    private String code;
    private String name;
    private String observerString;
	private CardFundingType fundingType;
	private CardType cardType;
    private Bank bank;
	private Country country;

    @SuppressWarnings("unchecked")
	public List<BankCard> suggestBankCard(Object event){
        String pref = event.toString();

        log.debug("suggest card: {0}", pref );
        
        return entityManager.createQuery("select bc from BankCard bc where bc.code like :code or bc.name like :name" )
                .setParameter("code", pref + "%")
                .setParameter("name", "%" + pref + "%")
                .setMaxResults(30)
                .setHint("org.hibernate.cacheable", true)
                .getResultList();
    }
    
    @SuppressWarnings("unchecked")
	public void selectBankCardList(){
        
    	HibernateSessionProxy session = (HibernateSessionProxy) entityManager.getDelegate();
        Criteria crit = session.createCriteria( BankCard.class );
        
        crit.createAlias("this.country", "country", Criteria.LEFT_JOIN);
        crit.createAlias("this.bank", "bank", Criteria.LEFT_JOIN);
        
        if( getCode() != null && getCode().length() > 0 ){
            crit.add( Restrictions.like("code", getCode() + "%" ));
        }
        
        if( getName() != null && getName().length() > 0 ){
            crit.add( Restrictions.like("name", getName() + "%" ));
        }

        if( getCardType() != null){
        	crit.add( Restrictions.eq("cardType",getCardType()));
        }

        if( getFundingType() != null){
        	crit.add( Restrictions.eq("fundingType",getFundingType()));
        }

        if( getBank() != null){
        	crit.add( Restrictions.eq("bank",getBank()));
        }

        if( getCountry() != null){
        	crit.add( Restrictions.eq("country",getCountry()));
        }
        
        //FIXME: projection eklemeli.
        crit.setMaxResults(30);
        crit.setCacheable(true);
        bankCardList= crit.list();
    }
    
    @Remove @Destroy
    public void destroy() {
    }

	public List<BankCard> getBankCardList() {
        return bankCardList;
    }

	public void setBankCardList(List<BankCard> bankCardList) {
        this.bankCardList = bankCardList;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public void sendSelectedCardsToHomeBean() {
		log.info("Sending cards to home...");
		if (observerString != null) {
			events.raiseEvent(observerString, getSelectedCards());
		}
	}

	private List<BankCard> getSelectedCards() {
		List<BankCard> selectedCardList = new ArrayList<BankCard>();
		for( BankCard bc : bankCardList ) {
			if (bc.isSelected()) {
				selectedCardList.add(bc);
			}
		}
		return selectedCardList;
	}

	public void setObserverString(String observerString) {
		this.observerString = observerString;
	}

	public CardFundingType getFundingType() {
		return fundingType;
	}

	public void setFundingType(CardFundingType fundingType) {
		this.fundingType = fundingType;
	}

	public CardType getCardType() {
		return cardType;
	}

	public void setCardType(CardType cardType) {
		this.cardType = cardType;
	}

	public Bank getBank() {
		return bank;
	}

	public void setBank(Bank bank) {
		this.bank = bank;
	}

	public Country getCountry() {
		return country;
	}

	public void setCountry(Country country) {
		this.country = country;
	}

}
