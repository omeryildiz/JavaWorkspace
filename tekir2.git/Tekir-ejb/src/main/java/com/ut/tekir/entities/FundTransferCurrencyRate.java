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
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 *
 * @author yigit
 */
@Entity
@Table(name="FUND_TRANSFER_CCY_RATE")
public class FundTransferCurrencyRate {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE,generator="genericSeq")
    @Column(name="ID")
    private Long id;

    @ManyToOne
    @JoinColumn(name="FUND_TRANSFER_ID")
    private FundTransfer fundTransfer;

    @ManyToOne
    @JoinColumn(name="CURRENCY_PAIR_ID")
    private CurrencyPair currencyPair;

    @Column(name="BID", precision=10, scale=2)
    private BigDecimal bid = BigDecimal.ZERO;

    @Column(name="ASK", precision=10, scale=2)
    private BigDecimal ask = BigDecimal.ZERO;

    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return the fundTransfer
     */
    public FundTransfer getFundTransfer() {
        return fundTransfer;
    }

    /**
     * @param fundTransfer the fundTransfer to set
     */
    public void setFundTransfer(FundTransfer fundTransfer) {
        this.fundTransfer = fundTransfer;
    }

    /**
     * @return the currencyPair
     */
    public CurrencyPair getCurrencyPair() {
        return currencyPair;
    }

    /**
     * @param currencyPair the currencyPair to set
     */
    public void setCurrencyPair(CurrencyPair currencyPair) {
        this.currencyPair = currencyPair;
    }

    /**
     * @return the bid
     */
    public BigDecimal getBid() {
        return bid;
    }

    /**
     * @param bid the bid to set
     */
    public void setBid(BigDecimal bid) {
        this.bid = bid;
    }

    /**
     * @return the ask
     */
    public BigDecimal getAsk() {
        return ask;
    }

    /**
     * @param ask the ask to set
     */
    public void setAsk(BigDecimal ask) {
        this.ask = ask;
    }

}
