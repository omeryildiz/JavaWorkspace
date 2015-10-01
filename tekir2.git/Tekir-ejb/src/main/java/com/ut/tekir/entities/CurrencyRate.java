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

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * Entity class CurrencyRate
 * 
 * @author haky
 */
@Entity
@Table(name="CURRENCY_RATE")
public class CurrencyRate implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE,generator="genericSeq")
    @Column(name="ID")
    private Long id;
    
    @Column(name="RATE_DATE")
    @Temporal(value=TemporalType.DATE)
    private Date date;

    @ManyToOne
    @JoinColumn(name="CURRENCY_PAIR")
    private CurrencyPair currencyPair;
    
    @Column(name="BID", precision=10, scale=4)
    private BigDecimal bid = BigDecimal.ZERO;
    
    @Column(name="ASK", precision=10, scale=4)
    private BigDecimal ask = BigDecimal.ZERO;
    
    @Column(name="BANKNOTE_BUYING", precision=10, scale=4)
    private BigDecimal bankNoteBuying = BigDecimal.ZERO;
    
    @Column(name="BANKNOTE_SELLING", precision=10, scale=4)
    private BigDecimal bankNoteSelling  = BigDecimal.ZERO;

	public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
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

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (this.getId() != null ? this.getId().hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof CurrencyRate)) {
            return false;
        }
        CurrencyRate other = (CurrencyRate)object;
        if (this.getId() != other.id && (this.getId() == null || !this.id.equals(other.id))) return false;
        return true;
    }

    @Override
    public String toString() {
        return "com.ut.tekir.entities.CurrencyRate[id=" + getId() + "]";
    }

	public void setBankNoteBuying(BigDecimal bankNoteBuying) {
		this.bankNoteBuying = bankNoteBuying;
	}

	public BigDecimal getBankNoteBuying() {
		return bankNoteBuying;
	}

	public void setBankNoteSelling(BigDecimal bankNoteSelling) {
		this.bankNoteSelling = bankNoteSelling;
	}

	public BigDecimal getBankNoteSelling() {
		return bankNoteSelling;
	}
        
}
