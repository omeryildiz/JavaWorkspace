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
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.validator.Length;

@Entity
@Table(name="BANK_ACCOUNTS")
public class BankAccount extends AuditBase implements Serializable {

	private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE,generator="genericSeq")
    @Column(name="ID")
	private Long id;
	
	@ManyToOne
	@JoinColumn(name="BANK_BRANCH_ID")
	private BankBranch bankBranch;

    @Column(name="NAME", length=50)
    @Length(max=50)
	private String name;

    @Column(name="ACCOUNT_DEPOSIT_TYPE")
    @Enumerated(EnumType.ORDINAL)
    private AccountDepositType accountDepositType = AccountDepositType.TimeDeposit;

    @Column(name="ACCOUNT_NO", length=25)
    @Length(max=25)
	private String accountNo;
	
    @Column(name="IBAN", length=50)
    @Length(max=50)
	private String iban;
	
	@Column(name="CCY", length=3)
	@Length(max=3)
    private String currency;
	
    @Column(name="ISACTIVE")
	private Boolean active = Boolean.TRUE;
	
    @Column(name="OPEN_DATE")
    @Temporal(TemporalType.DATE)
	private Date openDate;
	
    @Column(name="CLOSE_DATE")
    @Temporal(TemporalType.DATE)
	private Date closeDate;
	
	/*Ek kodlama alnları?! Mesela şube ya da servis bağantısı için ne yapmalı...*/
    @Column(name="EXCODE1")
	private String excode1;
	
    @Column(name="EXCODE2")
	private String excode2;
	
	/**
	 *  Interest rate
	 */
    @Column(name="RATE", precision=5, scale=2)
	private Float rate = 0f;
	
    /**
     * Holds period of time for time deposit.
     */
    @Column(name="TERM")
	private Integer term = 0;
	
    @Column(name="ACCOUNT_OWNER_TYPE")
    @Enumerated(EnumType.ORDINAL)
    private AccountOwnerType accountOwnerType = AccountOwnerType.Mine;
    
    @Column(name="WEIGHT")
    private Integer weight = 10;
    
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public BankBranch getBankBranch() {
		return bankBranch;
	}
	
	public void setBankBranch(BankBranch bankBranch) {
		this.bankBranch = bankBranch;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getAccountNo() {
		return accountNo;
	}
	
	public void setAccountNo(String accountNo) {
		this.accountNo = accountNo;
	}
	
	public String getIban() {
		return iban;
	}
	
	public void setIban(String iban) {
		this.iban = iban;
	}
	
	public String getCurrency() {
		return currency;
	}
	
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	
	public Boolean getActive() {
		return active;
	}
	
	public void setActive(Boolean active) {
		this.active = active;
	}
	
	public Date getOpenDate() {
		return openDate;
	}
	
	public void setOpenDate(Date openDate) {
		this.openDate = openDate;
	}
	
	public Date getCloseDate() {
		return closeDate;
	}
	
	public void setCloseDate(Date closeDate) {
		this.closeDate = closeDate;
	}
	
	public String getExcode1() {
		return excode1;
	}
	
	public void setExcode1(String excode1) {
		this.excode1 = excode1;
	}
	
	public String getExcode2() {
		return excode2;
	}
	
	public void setExcode2(String excode2) {
		this.excode2 = excode2;
	}
	
	public Float getRate() {
		return rate;
	}
	
	public void setRate(Float rate) {
		this.rate = rate;
	}
		  
	public Integer getTerm() {
		return term;
	}

	public void setTerm(Integer term) {
		this.term = term;
	}

	public AccountDepositType getAccountDepositType() {
		return accountDepositType;
	}

	public void setAccountDepositType(AccountDepositType accountDepositType) {
		this.accountDepositType = accountDepositType;
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
        if (!(object instanceof BankAccount)) {
            return false;
        }
        BankAccount other = (BankAccount)object;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) return false;
        return true;
    }
	
	@Override
    public String toString() {
        return "com.ut.tekir.entities.BankAccount[id=" + id + "]";
    }

	public AccountOwnerType getAccountOwnerType() {
		return accountOwnerType;
	}

	public void setAccountOwnerType(AccountOwnerType accountOwnerType) {
		this.accountOwnerType = accountOwnerType;
	}

	public Integer getWeight() {
		return weight;
	}

	public void setWeight(Integer weight) {
		this.weight = weight;
	}

}
