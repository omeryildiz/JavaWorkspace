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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;

/**
 * Cheque Stub
 * 
 * @author dumlupinar
 */
@Entity
@Table(name="CHEQUE_STUB")
public class ChequeStub implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE,generator="genericSeq")
    @Column(name="ID")
	private Long id;

    @Column(name="CODE", length=10, nullable=false, unique=true)
    @NotNull
    @Length(max=10, min=1)
	private String code;
	
    @Column(name="BANK_NAME", length=30)
    @Length(max=30)
	private String bankName;
    
    @Column(name="BANK_BRANCH", length=30)
    @Length(max=30)
    private String bankBranch;
    
    @Column(name="ACCOUNT_NO", length=20)
    @Length(max=20)
    private String accountNo;

    @ManyToOne
	@JoinColumn(name="BANK_ACCOUNT_ID")
	private BankAccount bankAccount;

    @Column(name="IBAN", length=50)
    @Length(max=50)
    private String iban;
	
	@Column(name="PAYMENT_PLACE", length=30)
	@Length(max=30)
	private String paymentPlace;

	@Column(name="INFO")
	private String info;

    /**
	 * Çekin asıl sahibi, firma ise unvanının tamamı yazılabilmeli
	 */
	@Column(name="CHEQUE_OWNER", length=255)
	@Length(max=255)
	private String chequeOwner;


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

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getBankBranch() {
		return bankBranch;
	}

	public void setBankBranch(String bankBranch) {
		this.bankBranch = bankBranch;
	}

	public String getAccountNo() {
		return accountNo;
	}

	public void setAccountNo(String accountNo) {
		this.accountNo = accountNo;
	}

	public String getPaymentPlace() {
		return paymentPlace;
	}

	public void setPaymentPlace(String paymentPlace) {
		this.paymentPlace = paymentPlace;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	@Override
    public int hashCode() {
        int hash = 0;
        hash += (this.getId() != null ? this.getId().hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ChequeStub)) {
            return false;
        }
        ChequeStub other = (ChequeStub)object;
        if (this.getId() != other.getId() && (this.getId() == null || !this.getId().equals(other.getId()))) return false;
        return true;
    }

    @Override
    public String toString() {
        return "com.ut.tekir.entities.ChequeStub[id=" + getId() + "]";
    }

    public String getChequeOwner() {
        return chequeOwner;
    }

    public void setChequeOwner(String chequeOwner) {
        this.chequeOwner = chequeOwner;
    }

    public String getIban() {
        return iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

    public BankAccount getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(BankAccount bankAccount) {
        this.bankAccount = bankAccount;
    }

}
