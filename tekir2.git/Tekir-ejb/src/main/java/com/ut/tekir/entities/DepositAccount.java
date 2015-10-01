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

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.validator.Valid;

/**
 * Entity class FundTransfer
 * 
 * @author huseyin
 */
@Entity
@Table(name="DEPOSIT_ACCOUNT")
public class DepositAccount extends DocumentBase implements Serializable  {
	
	private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE,generator="genericSeq")
    @Column(name="ID")
    private Long id;
	
    @Column(name="DEPOSIT_BEGIN_DATE")
    @Temporal(TemporalType.DATE)
	private Date depositBeginDate;
    
    @Column(name="DEPOSIT_END_DATE")
    @Temporal(TemporalType.DATE)
	private Date depositEndDate;
	
    @Column(name="RATE", precision=10, scale=2)
	private BigDecimal rate = BigDecimal.ZERO;

    @Embedded
    @Valid
    @AttributeOverrides( {
    	@AttributeOverride(name="currency", column=@Column(name="AMOUNT_CCY")),
    	@AttributeOverride(name="value",    column=@Column(name="AMOUNT_VALUE")),
    	@AttributeOverride(name="localAmount", column=@Column(name="AMOUNT_LCYVAL"))
    })
    private MoneySet amount = new MoneySet();
    
    @Column(name="PROFIT", precision=10, scale=2)
    private BigDecimal profit = BigDecimal.ZERO;
    
    @Column(name="TAX", precision=10, scale=2)
	private BigDecimal tax = BigDecimal.ZERO;

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
	@JoinColumn(name="DEPOSIT_BANK_ID")
	private Bank depositBank;
	
	@ManyToOne
	@JoinColumn(name="DEPOSIT_BANK_BRANCH_ID")
	private BankBranch depositBankBranch;
	
	@ManyToOne
	@JoinColumn(name="DEPOSIT_BANK_ACCOUNT_ID")
	private BankAccount depositBankAccount;

    @Override
	public DocumentType getDocumentType() {
		return DocumentType.DepositAccount;
	}
	
	@Override
    public int hashCode() {
        int hash = 0;
        hash += (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }
	
	@Override
    public boolean equals(Object object) {
        if (!(object instanceof DepositAccount)) {
            return false;
        }
        DepositAccount other = (DepositAccount)object;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) return false;
        return true;
    }
	
	@Override
    public String toString() {
        return "com.ut.tekir.entities.DepositAccount[id=" + id + "]";
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getDepositBeginDate() {
		return depositBeginDate;
	}

	public void setDepositBeginDate(Date depositBeginDate) {
		this.depositBeginDate = depositBeginDate;
	}

	public Date getDepositEndDate() {
		return depositEndDate;
	}

	public void setDepositEndDate(Date depositEndDate) {
		this.depositEndDate = depositEndDate;
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

	public Bank getDepositBank() {
		return depositBank;
	}

	public void setDepositBank(Bank depositBank) {
		this.depositBank = depositBank;
	}

	public BankBranch getDepositBankBranch() {
		return depositBankBranch;
	}

	public void setDepositBankBranch(BankBranch depositBankBranch) {
		this.depositBankBranch = depositBankBranch;
	}

	public BankAccount getDepositBankAccount() {
		return depositBankAccount;
	}

	public void setDepositBankAccount(BankAccount depositBankAccount) {
		this.depositBankAccount = depositBankAccount;
	}

	public BigDecimal getRate() {
		return rate;
	}

	public void setRate(BigDecimal rate) {
		this.rate = rate;
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

	public MoneySet getAmount() {
		return amount;
	}

	public void setAmount(MoneySet amount) {
		this.amount = amount;
	}

}


