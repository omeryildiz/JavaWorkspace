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
 * Entity class Interest Slice
 * 
 * Faiz oranları detayları
 * 
 * @author dumlupinar
 */
@Entity
@Table(name="INTEREST_RATE_DETAIL")
public class InterestRateDetail implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE,generator="genericSeq")
    @Column(name="ID")
    private Long id;

    @ManyToOne
    @JoinColumn(name="OWNER_ID")
    private InterestRate owner;

    /**
     * Geçerli olduğu tarih, master tablosundaki ile aynı olmalı
     * GUI katmanında görünmeyecek
     */
    @Column(name="EFFECTIVE_DATE")
    @Temporal(TemporalType.DATE)
    private Date date;
    
    /**
     * Para birimi
     */
    @Column(name="CURRENCY_CODE")
    private String currencyCode;
 
    /**
     * Faiz Oranı
     */
    @Column(name="RATE", nullable=false, precision=5, scale=2)
    private Float rate = 0f;
    
    /**
     * Süre, GUI katmanı için
     * 
     * 21g -> 21 gün
     * 6a -> 6 ay
     * 1y -> 1 yıl
     * 
     */
    @Column(name="DURATION")
    private String duration;
    
    /**
     * Temel süre, BL katmanı için, temel süre GÜN' dür
     * Duration bilgisi ne olursa olsun Gün e çevrilip buraya yazılacak
     * 
     * 1 yıl 360 gün olarak değerlendirilir
     * 1 ay 30 gün olarak değerlendirilir
     */
    @Column(name="BASE_DURATION")
    private Integer baseDuration = 0;
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public InterestRate getOwner() {
		return owner;
	}

	public void setOwner(InterestRate owner) {
		this.owner = owner;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public Integer getBaseDuration() {
		return baseDuration;
	}

	public void setBaseDuration(Integer baseDuration) {
		this.baseDuration = baseDuration;
	}

	public String getCurrencyCode() {
		return currencyCode;
	}

	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}

	public Float getRate() {
		return rate;
	}

	public void setRate(Float rate) {
		this.rate = rate;
	}

	@Override
    public int hashCode() {
        int hash = 0;
        hash += (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof InterestRateDetail)) {
            return false;
        }
        InterestRateDetail other = (InterestRateDetail)object;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) return false;
        return true;
    }

    @Override
    public String toString() {
		return "com.ut.tekir.entities.InterestRateDetail[id=" + id + "]";
    }

}
