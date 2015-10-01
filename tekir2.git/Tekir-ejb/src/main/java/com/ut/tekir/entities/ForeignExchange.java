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

import org.hibernate.validator.Valid;

/**
 * 
 * @author selman
 *
 */
@Entity
@Table(name="TKR_SWIFT")
public class ForeignExchange extends DocumentBase implements Serializable{
	    
	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.TABLE,generator="genericSeq")
    @Column(name="ID")
	private Long id;
	
	@ManyToOne
	@JoinColumn(name="BANK_ID")
	private Bank bank;

	@ManyToOne
	@JoinColumn(name="BANK_BRANCH_ID")
	private BankBranch bankBranch;
	
	@ManyToOne
	@JoinColumn(name="FROM_BANK_ACCOUNT_ID")
	private BankAccount fromBankAccount;
	
	@ManyToOne
	@JoinColumn(name="TO_BANK_ACCOUNT_ID")
	private BankAccount toBankAccount;
	
	@Embedded
	@AttributeOverrides({ 
		@AttributeOverride(name="currency", column=@Column(name="FROM_AMOUNT_CCY", length=3)),
		@AttributeOverride(name="value", column=@Column(name="FROM_AMOUNT", precision=10, scale=2)),
		@AttributeOverride(name="localAmount", column=@Column(name="FROM_AMOUNT_LCYVAL", precision=10, scale=2))
	})
	@Valid
	private MoneySet fromAmount = new MoneySet();

	@Embedded
	@AttributeOverrides({ 
		@AttributeOverride(name="currency", column=@Column(name="TO_AMOUNT_CCY", length=3)),
		@AttributeOverride(name="value", column=@Column(name="TO_AMOUNT", precision=10, scale=2)),
		@AttributeOverride(name="localAmount", column=@Column(name="TO_AMOUNT_LCYVAL", precision=10, scale=2))
	})
	@Valid
	private MoneySet toAmount = new MoneySet();

	@Embedded
	@AttributeOverrides({ 
		@AttributeOverride(name="currency", column=@Column(name="COST_CCY", length=3)),
		@AttributeOverride(name="value", column=@Column(name="COST_AMT", precision=10, scale=2)),
		@AttributeOverride(name="localAmount", column=@Column(name="COST_LCYVAL", precision=10, scale=2))
	})
	@Valid
	private MoneySet cost = new MoneySet();

	@Column(name = "CUR_RATE", precision = 12, scale = 5)
	private BigDecimal currencyRate = BigDecimal.ZERO;
	
	@Override
	public DocumentType getDocumentType() {
		return DocumentType.ForeignExchange;
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
        if (!(object instanceof ForeignExchange)) {
            return false;
        }
        ForeignExchange other = (ForeignExchange)object;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) return false;
        return true;
    }
	
	@Override
    public String toString() {
        return "com.ut.tekir.entities.ForeignExchange[id=" + id + "]";
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

	public BankAccount getFromBankAccount() {
		return fromBankAccount;
	}

	public void setFromBankAccount(BankAccount fromBankAccount) {
		this.fromBankAccount = fromBankAccount;
	}

	public BankAccount getToBankAccount() {
		return toBankAccount;
	}

	public void setToBankAccount(BankAccount toBankAccount) {
		this.toBankAccount = toBankAccount;
	}

	public MoneySet getCost() {
		return cost;
	}

	public void setCost(MoneySet cost) {
		this.cost = cost;
	}

	public BigDecimal getCurrencyRate() {
		return currencyRate;
	}

	public void setCurrencyRate(BigDecimal currencyRate) {
		this.currencyRate = currencyRate;
	}

	public BankBranch getBankBranch() {
		return bankBranch;
	}

	public void setBankBranch(BankBranch bankBranch) {
		this.bankBranch = bankBranch;
	}

	public void setFromAmount(MoneySet fromAmount) {
		this.fromAmount = fromAmount;
	}

	public MoneySet getFromAmount() {
		return fromAmount;
	}

	public void setToAmount(MoneySet toAmount) {
		this.toAmount = toAmount;
	}

	public MoneySet getToAmount() {
		return toAmount;
	}

}
