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
import java.util.Date;
import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CascadeType;
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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.Cascade;
import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;
import org.hibernate.validator.Valid;

/**
 * Cheque
 * 
 * @author dumlupinar , volkan
 */
@Entity
@Table(name = "CHEQUE")
public class Cheque implements Serializable {

	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE,generator="genericSeq")
	@Column(name = "ID")
	private Long id;

	/**
	 * Çek numarası
	 */
	@Column(name = "REFERENCE_NO", length = 20, nullable = false)
	@NotNull
	@Length(max = 20, min = 1)
	private String referenceNo;

	/**
	 * Firma/Müşteri çeki
	 */
	@Column(name = "IS_CLIENT_CHEQUE")
	private Boolean clientCheque = Boolean.FALSE;

	@Column(name = "BANK_NAME", length = 30)
	@Length(max = 30)
	private String bankName;

	@Column(name = "BANK_BRANCH", length = 30)
	@Length(max = 30)
	private String bankBranch;

	@Column(name = "ACCOUNT_NO", length = 25)
	@Length(max = 25)
	private String accountNo;

	@Column(name = "IBAN", length = 50)
	@Length(max = 50)
	private String iban;

	/**
	 * Belge-fis numarası
	 */
	@Column(name = "SERIAL_NO", length = 30)
	@Length(max = 30)
	private String serialNo;

	/**
	 * Kayıt tarihi
	 */
	@Column(name = "ENTRY_DATE")
	@Temporal(TemporalType.DATE)
	private Date entryDate = new Date();

	/**
	 * Düzenleme tarihi
	 */
	@Column(name = "ISSUE_DATE")
	@Temporal(TemporalType.DATE)
	private Date issueDate = new Date();

	/**
	 * Keşide - Vade tarihi
	 */
	@Column(name = "MATURITY_DATE")
	@Temporal(value = TemporalType.DATE)
	private Date maturityDate;

	/**
	 * Dövizli çek alınabilir olsun
	 */
	@Embedded
    @Valid
    @AttributeOverrides( {
        @AttributeOverride(name="currency", column=@Column(name="CCY")),
        @AttributeOverride(name="value",    column=@Column(name="CCYVAL")),
        @AttributeOverride(name="localAmount", column=@Column(name="LCYVAL"))
    })
	private MoneySet money = new MoneySet();
	
	/**
	 * Cirolu olup olmadığı
	 */
	@Column(name = "ISENDORSEMENT")
	private Boolean endorsement = Boolean.FALSE;
	
	/**
	 * Çekin asıl sahibi, firma ise unvanının tamamı yazılabilmeli
	 */
	@Column(name = "CHEQUE_OWNER", length = 255)
	@Length(max = 255)
	private String chequeOwner;
	
	/**
	 * Ödeyecek/Keşideci
	 */
	@ManyToOne
	@JoinColumn(name = "CONTACT_ID")
	private Contact contact;
	
	/**
	 * Ödeme/Keşide yeri
	 */
	@Column(name = "PAYMENT_PLACE", length = 30)
	@Length(max = 30)
	
	private String paymentPlace;
	
	@Column(name = "INFO")
	private String info;
	
	@Column(name = "PREVIOUS_STATUS")
	@Enumerated(EnumType.ORDINAL)
	private ChequeStatus previousStatus = ChequeStatus.Portfoy;
	
	@Column(name = "LAST_STATUS")
	@Enumerated(EnumType.ORDINAL)
	private ChequeStatus lastStatus = ChequeStatus.Portfoy;
	
	@Column(name = "LAST_STATUS_DATE")
	@Temporal(value = TemporalType.DATE)
	private Date statusDate;
	
	/**
	 * Portfoy amacli bulundugu kasayi tutar
	 */
	@ManyToOne
	@JoinColumn(name = "ACCOUNT_ID")
	private Account account;
	
	/**
	 * sadece firma çeklerinde kullanılacağından ManyToOne bağlanmadı...
	 */
	@Column(name = "BANK_ACCOUNT_ID")
	private Long bankAccountId;
	
	/**
	 * sadece firma çeklerinde kullanılacağından ManyToOne bağlanmadı...
	 */
	@Column(name = "BANK_BRANCH_ID")
	private Long bankBranchId;
	
	@OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private List<ChequeHistory> history = new ArrayList<ChequeHistory>();
	
	@Transient
	private Boolean checked = Boolean.FALSE;

    /**
     * siparis de alinan kaparo tutarlarinin takibi icin
     */
    @Column(name="TRADEIN")
    private Boolean tradein = Boolean.FALSE;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getReferenceNo() {
		return referenceNo;
	}

	public void setReferenceNo(String referenceNo) {
		this.referenceNo = referenceNo;
	}

	public Boolean getClientCheque() {
		return clientCheque;
	}

	public void setClientCheque(Boolean clientCheque) {
		this.clientCheque = clientCheque;
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

	public String getSerialNo() {
		return serialNo;
	}

	public void setSerialNo(String serialNo) {
		this.serialNo = serialNo;
	}

	public Boolean getEndorsement() {
		return endorsement;
	}

	public void setEndorsement(Boolean endorsement) {
		this.endorsement = endorsement;
	}

	public String getChequeOwner() {
		return chequeOwner;
	}

	public void setChequeOwner(String chequeOwner) {
		this.chequeOwner = chequeOwner;
	}

	public Date getEntryDate() {
		return entryDate;
	}

	public void setEntryDate(Date entryDate) {
		this.entryDate = entryDate;
	}

	public Date getIssueDate() {
		return issueDate;
	}

	public void setIssueDate(Date issueDate) {
		this.issueDate = issueDate;
	}

	public Date getMaturityDate() {
		return maturityDate;
	}

	public void setMaturityDate(Date maturityDate) {
		this.maturityDate = maturityDate;
	}

	public Contact getContact() {
		return contact;
	}

	public void setContact(Contact contact) {
		this.contact = contact;
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

	public ChequeStatus getPreviousStatus() {
		return previousStatus;
	}

	public void setPreviousStatus(ChequeStatus previousStatus) {
		this.previousStatus = previousStatus;
	}

	public ChequeStatus getLastStatus() {
		return lastStatus;
	}

	public void setLastStatus(ChequeStatus lastStatus) {
		this.lastStatus = lastStatus;
	}

	public List<ChequeHistory> getHistory() {
		return history;
	}

	public void setHistory(List<ChequeHistory> history) {
		this.history = history;
	}

	public Boolean getChecked() {
		return checked;
	}

	public void setChecked(Boolean checked) {
		this.checked = checked;
	}

	@Transient
	public String getCaption() {
		return "[" + getSerialNo() + "] " + getChequeOwner();
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (this.getId() != null ? this.getId().hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		// TODO: Warning - this method won't work in the case the id fields are
		// not set
		if (!(object instanceof Cheque)) {
			return false;
		}
		Cheque other = (Cheque) object;
		if (this.getId() != other.getId()
				&& (this.getId() == null || !this.getId().equals(other.getId()))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "com.ut.tekir.entities.PaymentItemCheque[id=" + getId() + "]";
	}

	public String getIban() {
		return iban;
	}

	public void setIban(String iban) {
		this.iban = iban;
	}

	public MoneySet getMoney() {
		return money;
	}

	public void setMoney(MoneySet money) {
		this.money = money;
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public Date getStatusDate() {
		return statusDate;
	}

	public void setStatusDate(Date statusDate) {
		this.statusDate = statusDate;
	}

	/**
	 * @return the bankAccountId
	 */
	public Long getBankAccountId() {
		return bankAccountId;
	}

	/**
	 * @param bankAccountId
	 *            the bankAccountId to set
	 */
	public void setBankAccountId(Long bankAccountId) {
		this.bankAccountId = bankAccountId;
	}

	/**
	 * @return the bankBranchId
	 */
	public Long getBankBranchId() {
		return bankBranchId;
	}

	/**
	 * @param bankBranchId
	 *            the bankBranchId to set
	 */
	public void setBankBranchId(Long bankBranchId) {
		this.bankBranchId = bankBranchId;
	}

	public Boolean getTradein() {
		return tradein;
	}

	public void setTradein(Boolean tradein) {
		this.tradein = tradein;
	}

}
