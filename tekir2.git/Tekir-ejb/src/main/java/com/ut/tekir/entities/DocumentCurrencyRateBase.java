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

package com.ut.tekir.entities;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

/**
 * Ortak döküman kur oran bilgilerini tutar.
 * 
 * @author sinan.yumak
 * 
 */
@MappedSuperclass
public abstract class DocumentCurrencyRateBase {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE,generator="genericSeq")
	@Column(name = "ID")
	private Long id;

	@ManyToOne
	@JoinColumn(name = "CURRENCY_PAIR_ID")
	private CurrencyPair currencyPair;

	@Column(name = "BID", precision = 10, scale = 2)
	private BigDecimal bid = BigDecimal.ZERO;

	@Column(name = "ASK", precision = 10, scale = 2)
	private BigDecimal ask = BigDecimal.ZERO;

	public DocumentCurrencyRateBase() {
		super();
	}

	public DocumentCurrencyRateBase(CurrencyPair currencyPair, CurrencyRate currencyRate) {
		super();
		this.currencyPair = currencyPair;
		if ( currencyRate != null ) {
			this.ask = currencyRate.getAsk();
			this.bid = currencyRate.getBid();
		}
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public CurrencyPair getCurrencyPair() {
		return currencyPair;
	}

	public void setCurrencyPair(CurrencyPair currencyPair) {
		this.currencyPair = currencyPair;
	}

	public BigDecimal getBid() {
		return bid;
	}

	public void setBid(BigDecimal bid) {
		this.bid = bid;
	}

	public BigDecimal getAsk() {
		return ask;
	}

	public void setAsk(BigDecimal ask) {
		this.ask = ask;
	}

}
