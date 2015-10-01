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

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.validator.Valid;

@Entity
@Table(name="BANK_TO_BANK_TRANSFER")
public class BankToBankTransfer extends DocumentBase implements Serializable{

	private static final long serialVersionUID = 1L;
	
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE,generator="genericSeq")
    @Column(name="ID")
	private Long id;
	
	@ManyToOne
	@JoinColumn(name="FROM_BANK_ID")
	private Bank fromBank;
	
	@ManyToOne
	@JoinColumn(name="TO_BANK_ID")
	private Bank toBank;
	
	@ManyToOne
	@JoinColumn(name="FROM_BANK_BRANCH_ID")
	private BankBranch fromBankBranch;
	
	@ManyToOne
	@JoinColumn(name="TO_BANK_BRANCH_ID")
	private BankBranch toBankBranch;
	
	@ManyToOne
	@JoinColumn(name="FROM_BANK_ACCOUNT_ID")
	private BankAccount fromBankAccount;
	
	@ManyToOne
	@JoinColumn(name="TO_BANK_ACCOUNT_ID")
	private BankAccount toBankAccount;
	
	@Embedded
	@Valid
	@AttributeOverrides({ 
		@AttributeOverride(name="localAmount", column=@Column(name="FROMLCYVAL")),
		@AttributeOverride(name="currency", column=@Column(name="FROMCCY")),
		@AttributeOverride(name="value", column=@Column(name="FROMAMT"))
	})
	private MoneySet fromAmount = new MoneySet();
	
	@Embedded
	@Valid
	@AttributeOverrides({ 
		@AttributeOverride(name="localAmount", column=@Column(name="TOLCYVAL")),
		@AttributeOverride(name="currency", column=@Column(name="TOCCY")),
		@AttributeOverride(name="value", column=@Column(name="TOAMT"))
	})
	private MoneySet toAmount = new MoneySet();
	
	@Embedded
	@Valid
	@AttributeOverrides({ 
		@AttributeOverride(name="localAmount", column=@Column(name="COSTLCYAMT")),
		@AttributeOverride(name="currency", column=@Column(name="COSTCCY")),
		@AttributeOverride(name="value", column=@Column(name="COSTAMT"))
	})
	private MoneySet cost = new MoneySet();

	@Column(name="TRANSFER_TYPE")
	@Enumerated(EnumType.ORDINAL)
	private BankTransferType transferType;
	
	@ManyToOne
	@JoinColumn(name="WORK_BUNCH_ID")
	@ForeignKey(name="FK_BANKTOBANKTXN_WORKBUNCHID")
	private WorkBunch workBunch;
	
	@Override
	public DocumentType getDocumentType() {
		return DocumentType.BankToBankTransfer;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Bank getFromBank() {
		return fromBank;
	}

	public void setFromBank(Bank fromBank) {
		this.fromBank = fromBank;
	}

	public Bank getToBank() {
		return toBank;
	}

	public void setToBank(Bank toBank) {
		this.toBank = toBank;
	}

	public BankBranch getFromBankBranch() {
		return fromBankBranch;
	}

	public void setFromBankBranch(BankBranch fromBankBranch) {
		this.fromBankBranch = fromBankBranch;
	}

	public BankBranch getToBankBranch() {
		return toBankBranch;
	}

	public void setToBankBranch(BankBranch toBankBranch) {
		this.toBankBranch = toBankBranch;
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

	public MoneySet getFromAmount() {
		return fromAmount;
	}

	public void setFromAmount(MoneySet fromAmount) {
		this.fromAmount = fromAmount;
	}

	public MoneySet getToAmount() {
		return toAmount;
	}

	public void setToAmount(MoneySet toAmount) {
		this.toAmount = toAmount;
	}

	public MoneySet getCost() {
		return cost;
	}

	public void setCost(MoneySet cost) {
		this.cost = cost;
	}

	public BankTransferType getTransferType() {
		return transferType;
	}

	public void setTransferType(BankTransferType transferType) {
		this.transferType = transferType;
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
        if (!(object instanceof BankToBankTransfer)) {
            return false;
        }
        BankToBankTransfer other = (BankToBankTransfer)object;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) return false;
        return true;
    }
	
	@Override
    public String toString() {
        return "com.ut.tekir.entities.BankToBankTransfer[id=" + id + "]";
    }

	public WorkBunch getWorkBunch() {
		return workBunch;
	}

	public void setWorkBunch(WorkBunch workBunch) {
		this.workBunch = workBunch;
	}
	
}
