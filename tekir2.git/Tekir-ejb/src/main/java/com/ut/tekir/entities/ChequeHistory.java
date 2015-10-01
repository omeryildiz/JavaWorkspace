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
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.validator.Length;

/**
 * Cheque Detail
 * 
 * @author dumlupinar
 */
@Entity
@Table(name = "CHEQUE_HISTORY")
public class ChequeHistory implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE,generator="genericSeq")
    @Column(name = "ID")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "OWNER_ID")
    private Cheque owner;

    @Column(name = "PROCESS_DATE")
    @Temporal(TemporalType.DATE)
    private Date date = new Date();

    @Column(name = "STATUS")
    @Enumerated(EnumType.ORDINAL)
    private ChequeStatus status;

    @Column(name = "INFO")
    private String info;
    
    /**
     * Kaydın geldiği kaynak
     */
    @Column(name = "SOURCE")
    @Enumerated(EnumType.ORDINAL)
    private DocumentType source;

    /**
     * Kaydın geldiği kaynağın id si
     */
    @Column(name = "SOURCEID")
    private Long sourceId;

    @Column(name="SERIAL", length=10)
    @Length(max=10, min=1)
    private String serial;

    /* Hangi banka hesabinda islem goruyor */
    @ManyToOne
    @JoinColumn(name="BANK_ACCOUNT_ID")
    private BankAccount bankAccount;

    @ManyToOne
    @JoinColumn(name="CONTACT_ID")
    private Contact contact;

    @ManyToOne
    @JoinColumn(name="ACCOUNT_ID")
    private Account account;

    @ManyToOne
    @JoinColumn(name="WORK_BUNCH_ID")
    @ForeignKey(name="FK_CHEQUEHISTORY_WORKBUNCHID")
    private WorkBunch workBunch;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Cheque getOwner() {
        return owner;
    }

    public void setOwner(Cheque owner) {
        this.owner = owner;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public ChequeStatus getStatus() {
        return status;
    }

    public void setStatus(ChequeStatus status) {
        this.status = status;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public DocumentType getSource() {
        return source;
    }

    public void setSource(DocumentType source) {
        this.source = source;
    }

    public Long getSourceId() {
        return sourceId;
    }

    public void setSourceId(Long sourceId) {
        this.sourceId = sourceId;
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
        if (!(object instanceof ChequeHistory)) {
            return false;
        }
        ChequeHistory other = (ChequeHistory) object;
        if (this.getId() != other.getId() && (this.getId() == null || !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    @Override
    public String toString() {
        return "com.ut.tekir.entities.ChequeHistory[id=" + getId() + "]";
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

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

	public WorkBunch getWorkBunch() {
		return workBunch;
	}

	public void setWorkBunch(WorkBunch workBunch) {
		this.workBunch = workBunch;
	}

}
