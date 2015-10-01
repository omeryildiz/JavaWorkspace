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
@Table(name="BANK_TO_CONTACT_TRANSFER")
public class BankToContactTransfer extends DocumentBase implements Serializable{

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
	@JoinColumn(name="CONTACT_ID")
	private Contact contact;
	
	@Embedded
    @Valid
    @AttributeOverrides( {
        @AttributeOverride(name="currency", column=@Column(name="CCY")),
        @AttributeOverride(name="value",    column=@Column(name="CCYVAL")),
        @AttributeOverride(name="localAmount", column=@Column(name="LCYVAL"))
    })
	private MoneySet amount = new MoneySet();
	
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
	
	@Column(name="ACTION")
	@Enumerated(EnumType.ORDINAL)
	private FinanceAction action;

    /**
     * İade gider pusulası bilgisini tutar.
     */
    @ManyToOne
    @JoinColumn(name="EXPENSE_NOTE_ID")
    private ExpenseNote expenseNote;
    
	@ManyToOne
	@JoinColumn(name="WORKBUNCH_ID")
	@ForeignKey(name="FK_BANKTOCONTACTTRANSFER_WORKBUNCHID")
	private WorkBunch workBunch;    
	
    @Enumerated(EnumType.ORDINAL)
    @Column(name="PROCESS_TYPE")
    private AdvanceProcessType processType = AdvanceProcessType.Normal;

	@Column(name="MATCHING_FINISHED")
	private Boolean matchingFinished = Boolean.FALSE;
    
	@Override
	public DocumentType getDocumentType() {
		return DocumentType.BankToContactTransfer;
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

	public Contact getContact() {
		return contact;
	}

	public void setContact(Contact contact) {
		this.contact = contact;
	}

	public MoneySet getAmount() {
		return amount;
	}

	public void setAmount(MoneySet amount) {
		this.amount = amount;
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

	public FinanceAction getAction() {
		return action;
	}

	public void setAction(FinanceAction action) {
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
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof BankToContactTransfer)) {
            return false;
        }
        BankToContactTransfer other = (BankToContactTransfer)object;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) return false;
        return true;
    }
	
	@Override
    public String toString() {
        return "com.ut.tekir.entities.BankToContactTransfer[id=" + id + "]";
    }

	public ExpenseNote getExpenseNote() {
		return expenseNote;
	}

	public void setExpenseNote(ExpenseNote expenseNote) {
		this.expenseNote = expenseNote;
	}

	public WorkBunch getWorkBunch() {
		return workBunch;
	}

	public void setWorkBunch(WorkBunch workBunch) {
		this.workBunch = workBunch;
	}

	public AdvanceProcessType getProcessType() {
		return processType;
	}

	public void setProcessType(AdvanceProcessType processType) {
		this.processType = processType;
	}

	public Boolean getMatchingFinished() {
		return matchingFinished;
	}

	public void setMatchingFinished(Boolean matchingFinished) {
		this.matchingFinished = matchingFinished;
	}

}
