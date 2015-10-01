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
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.ForeignKey;

/**
 *
 * Senet Bankadan Ödeme Bordrosu
 *
 * @author sinan.yumak
 */
@Entity
@Table(name = "PROMISSORY_TO_BANK_PAY_PAYROLL")
public class PromissoryToBankPaymentPayroll extends DocumentBase implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE,generator="genericSeq")
    @Column(name = "ID")
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "BANK_ID")
	@ForeignKey(name="FK_PROMISSORYTOBANKPAYPAYROLL_BANKID")
    private Bank bank;

    @ManyToOne
    @JoinColumn(name = "BANK_BRANCH_ID")
	@ForeignKey(name="FK_PROMISSORYTOBANKPAYPAYROLL_BANKBRANCHID")
    private BankBranch bankBranch;
    
    @ManyToOne
    @JoinColumn(name = "BANK_ACCOUNT_ID")
	@ForeignKey(name="FK_PROMISSORYTOBANKPAYPAYROLL_BANKACCOUNTID")
    private BankAccount bankAccount;
    
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
    @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    private List<PromissoryToBankPaymentPayrollDetail> details = new ArrayList<PromissoryToBankPaymentPayrollDetail>();

    @Override
	public DocumentType getDocumentType() {
		return DocumentType.PromissoryBankPaymentPayroll;
	}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BankAccount getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(BankAccount bankAccount) {
        this.bankAccount = bankAccount;
    }

    @Transient
    public DocumentTotal getTotals() {
        DocumentTotal tot = new DocumentTotal();

        for (PromissoryToBankPaymentPayrollDetail item : getDetails()) {
            tot.add(new MoneySet(item.getPromissory().getMoney()));
        }

        return tot;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (this.getId() != null ? this.getId().hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof PromissoryToBankPaymentPayroll)) {
            return false;
        }
        PromissoryToBankPaymentPayroll other = (PromissoryToBankPaymentPayroll) object;
        if (this.getId() != other.getId() && (this.getId() == null || !this.getId().equals(other.getId()))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.ut.tekir.entities.PromissoryToBankPaymentPayroll[id=" + getId() + "]";
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

	public List<PromissoryToBankPaymentPayrollDetail> getDetails() {
		return details;
	}

	public void setDetails(List<PromissoryToBankPaymentPayrollDetail> details) {
		this.details = details;
	}
}
