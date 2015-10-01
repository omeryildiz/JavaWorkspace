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
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;

/**
 * Entity class BondPurchaseSale
 * 
 * @author huseyin
 */


@Entity
@Table(name="BOND_PURCHASE_SALE")
public class BondPurchaseSale extends DocumentBase implements Serializable  {
	
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
	@JoinColumn(name="BANK_ACCOUNT_ID")
	private BankAccount bankAccount;
	
	@ManyToOne
	@JoinColumn(name="PORTFOLIO_ID")
	private Portfolio portfolio;
	
	@ManyToOne
	@JoinColumn(name="SECURITY_ID")
	private Security security;

	@Column(name="NOMINAL", precision=10, scale=2)
	private BigDecimal nominal = BigDecimal.ZERO;

	@Column(name="AMOUNT", precision=10, scale=2)
	private BigDecimal amount = BigDecimal.ZERO;
	
	@Column(name="TAX", precision=10, scale=2)
	private BigDecimal tax = BigDecimal.ZERO;
	
	@Column(name="ACTION")
	@Enumerated(EnumType.ORDINAL)
	private TradeAction action;
	
    @OneToMany(mappedBy="bondPurchaseSale", cascade=CascadeType.ALL)
    @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    private List<BondPurchaseSaleDetail> details = new ArrayList<BondPurchaseSaleDetail>();

	@Override
	public DocumentType getDocumentType() {
		return action.equals(TradeAction.Purchase) ? DocumentType.BondPurchase : DocumentType.BondSale ;
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

	public Portfolio getPortfolio() {
		return portfolio;
	}

	public void setPortfolio(Portfolio portfolio) {
		this.portfolio = portfolio;
	}

	public Security getSecurity() {
		return security;
	}

	public void setSecurity(Security security) {
		this.security = security;
	}

	public BigDecimal getNominal() {
		return nominal;
	}

	public void setNominal(BigDecimal nominal) {
		this.nominal = nominal;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public BigDecimal getTax() {
		return tax;
	}

	public void setTax(BigDecimal tax) {
		this.tax = tax;
	}

	public TradeAction getAction() {
		return action;
	}

	public void setAction(TradeAction action) {
		this.action = action;
	}

	@Override
    public int hashCode() {
        int hash = 0;
        hash += (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }
	
	@Override
    public boolean equals(Object object) {
        if (!(object instanceof BondPurchaseSale)) {
            return false;
        }
        BondPurchaseSale other = (BondPurchaseSale)object;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) return false;
        return true;
    }
	
	@Override
    public String toString() {
        return "com.ut.tekir.entities.BondPurchaseSale[id=" + id + "]";
    }
	
	public List<BondPurchaseSaleDetail> getDetails() {
		return details;
	}

	public void setDetails(List<BondPurchaseSaleDetail> details) {
		this.details = details;
	}

}


