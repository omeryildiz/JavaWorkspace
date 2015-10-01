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
 * Çek Bankadan Ödeme Bordrosu
 *
 * @author yigit
 */
@Entity
@Table(name = "CHEQUE_TO_BANK_PAY_PAYROLL")
public class ChequeToBankPaymentPayroll extends DocumentBase implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE,generator="genericSeq")
    @Column(name = "ID")
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "BANK_ID")
	@ForeignKey(name="FK_CHEQUETOBANKPAYPAYROLL_BANKID")
    private Bank bank;

    @ManyToOne
    @JoinColumn(name = "BANK_BRANCH_ID")
	@ForeignKey(name="FK_CHEQUETOBANKPAYPAYROLL_BANKBRANCHID")
    private BankBranch bankBranch;
    
    @ManyToOne
    @JoinColumn(name = "BANK_ACCOUNT_ID")
	@ForeignKey(name="FK_CHEQUETOBANKPAYPAYROLL_BANKACCOUNTID")
    private BankAccount bankAccount;
    
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
    @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    private List<ChequeToBankPaymentPayrollDetail> details = new ArrayList<ChequeToBankPaymentPayrollDetail>();

	@Override
	public DocumentType getDocumentType() {
		return DocumentType.ChequeBankPaymentPayroll;
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

    public List<ChequeToBankPaymentPayrollDetail> getDetails() {
        return details;
    }

    public void setDetails(List<ChequeToBankPaymentPayrollDetail> details) {
        this.details = details;
    }

    @Transient
    public DocumentTotal getTotals() {
        DocumentTotal tot = new DocumentTotal();

        for (ChequeToBankPaymentPayrollDetail item : getDetails()) {
            tot.add(new MoneySet(item.getCheque().getMoney()));
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
        if (!(object instanceof ChequeToBankPaymentPayroll)) {
            return false;
        }
        ChequeToBankPaymentPayroll other = (ChequeToBankPaymentPayroll) object;
        if (this.getId() != other.getId() && (this.getId() == null || !this.getId().equals(other.getId()))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.ut.tekir.entities.ChequeToBankPaymentPayroll[id=" + getId() + "]";
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
}
