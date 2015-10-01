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

import org.hibernate.validator.Length;

/**
 * Entity class FundTransfer
 * 
 * @author burak
 */
@Entity
@Table(name="REPO")
public class Repo extends DocumentBase implements Serializable  {
	
	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.TABLE,generator="genericSeq")
    @Column(name="ID")
	private Long id;
	
	@Column(name="REPO_BEGIN_DATE")
	@Temporal(TemporalType.DATE)
	private Date repoBeginDate;
	
	@Column(name="REPO_END_DATE")
	@Temporal(TemporalType.DATE)
	private Date repoEndDate;
	
	@Column(name="RATE", precision=5, scale=2)
	private BigDecimal rate;
	
	@Column(name="PROFIT", precision=5, scale=2)
	private BigDecimal profit;
	
	@Column(name="TAX", precision=5, scale=2)
	private BigDecimal tax;
	
	@Column(name="CCY", length=3)
	@Length(max=3)
    private String currency;

	@Column(name="AMOUNT", precision=10, scale=2)
	private BigDecimal amount = BigDecimal.ZERO;
	
	@ManyToOne
	@JoinColumn(name="BANK_ID")
	private Bank bank;
	
	@ManyToOne
	@JoinColumn(name="BANK_BRANCH_ID")
	private BankBranch bankBranch;
	
	@ManyToOne
	@JoinColumn(name="BANK_ACCOUNT_ID")
	private BankAccount bankAccount;
	
	@ManyToOne
	@JoinColumn(name="REPO_BANK_ID")
	private Bank repoBank;
	
	@ManyToOne
	@JoinColumn(name="REPO_BANK_BRANCH_ID")
	private BankBranch repoBankBranch;
	
	@ManyToOne
	@JoinColumn(name="REPO_BANK_ACCOUNT_ID")
	private BankAccount repoBankAccount;

    @Override
	public DocumentType getDocumentType() {
		return DocumentType.Repo;
	}

	@Override
    public int hashCode() {
        int hash = 0;
        hash += (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }
	
	@Override
    public boolean equals(Object object) {
        if (!(object instanceof Repo)) {
            return false;
        }
        Repo other = (Repo)object;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) return false;
        return true;
    }
	
	@Override
    public String toString() {
        return "com.ut.tekir.entities.Repo[id=" + id + "]";
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}


	public Bank getBank() {
		return bank;
	}

	public void setBank(Bank bank) {
		this.bank = bank;
	}

	public BankBranch getBankBranch() {
		return bankBranch;
	}

	public void setBankBranch(BankBranch bankBranch) {
		this.bankBranch = bankBranch;
	}

	public BankAccount getBankAccount() {
		return bankAccount;
	}

	public void setBankAccount(BankAccount bankAccount) {
		this.bankAccount = bankAccount;
	}

	public BigDecimal getRate() {
		return rate;
	}

	public void setRate(BigDecimal rate) {
		this.rate = rate;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public BigDecimal getProfit() {
		return profit;
	}

	public void setProfit(BigDecimal profit) {
		this.profit = profit;
	}

	public BigDecimal getTax() {
		return tax;
	}

	public void setTax(BigDecimal tax) {
		this.tax = tax;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public Date getRepoBeginDate() {
		return repoBeginDate;
	}

	public void setRepoBeginDate(Date repoBeginDate) {
		this.repoBeginDate = repoBeginDate;
	}

	public Date getRepoEndDate() {
		return repoEndDate;
	}

	public void setRepoEndDate(Date repoEndDate) {
		this.repoEndDate = repoEndDate;
	}

	public Bank getRepoBank() {
		return repoBank;
	}

	public void setRepoBank(Bank repoBank) {
		this.repoBank = repoBank;
	}

	public BankBranch getRepoBankBranch() {
		return repoBankBranch;
	}

	public void setRepoBankBranch(BankBranch repoBankBranch) {
		this.repoBankBranch = repoBankBranch;
	}

	public BankAccount getRepoBankAccount() {
		return repoBankAccount;
	}

	public void setRepoBankAccount(BankAccount repoBankAccount) {
		this.repoBankAccount = repoBankAccount;
	}

}
