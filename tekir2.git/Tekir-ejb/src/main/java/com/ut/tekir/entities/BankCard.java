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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;


/**
 * Banka kard kodları
 * BIN database
 *
 * @author volkan
 *
 */
@Entity
@Table(name="BANK_CARD")
public class BankCard extends AuditBase implements Serializable {

	private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE,generator="genericSeq")
    @Column(name="ID")
	private Long id;

    /**
     * BIN kodu, 6 hanedir. Ilk kod kart sistemini belirtir. Geri kalan 5 hane banka+urun kodudur.
     * 4- visa
     * 5- master
     *
     */
    @Column(name="CODE", length=6, unique=true, nullable=false)
    @Length(max=6, min=1)
    @NotNull
	private String code;

    @Column(name="NAME", length=50)
    @Length(max=50)
    private String name;
    
    @Column(name="INFO")
	private String info;
    
    @Column(name="ISACTIVE")
	private Boolean active = Boolean.TRUE;

    @Column(name="FUNDING_TYPE")
    @Enumerated(EnumType.ORDINAL)
    private CardFundingType fundingType;

    @Column(name="CARD_TYPE")
    @Enumerated(EnumType.ORDINAL)
	private CardType cardType;

    @ManyToOne
    @JoinColumn(name="BANK_ID")
	@ForeignKey(name="FK_BANKCARD_BANK_ID")
    private Bank bank;
    
    @Column(name="BANK_NAME", length=50)
    @Length(max=50)
    private String bankName;

    @ManyToOne
	@JoinColumn(name="COUNTRY_ID")
	@ForeignKey(name="FK_BANKCARD_COUNTRY_ID")
	private Country country;

    @Transient
    private boolean selected = true;
    
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
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

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
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
        if (!(object instanceof BankCard)) {
            return false;
        }
        BankCard other = (BankCard)object;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) return false;
        return true;
    }
	
	@Override
    public String toString() {
        return "com.ut.tekir.entities.BankCard[id=" + id + "]";
    }

	public Bank getBank() {
		return bank;
	}

	public void setBank(Bank bank) {
		this.bank = bank;
	}

    public String getCaption(){
        return "[" + getCode() + "] " + getName();
    }

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	
}
