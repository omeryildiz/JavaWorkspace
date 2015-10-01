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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.validator.Length;

/**
 * Entity class FinanceTxn
 * 
 * @author haky
 */
@Entity
@Table(name="FINANCE_TXN")
public class FinanceTxn extends AuditBase implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE,generator="genericSeq")
    @Column(name="ID")
    private Long id;
    
    @ManyToOne
    @JoinColumn(name="CONTACT_ID")
    private Contact contact;
    
    @Column(name="TXN_DATE")
    @Temporal(value=TemporalType.DATE)
    private Date date;
    
    @Column(name="FINANCE_ACTION")
    @Enumerated(EnumType.ORDINAL)
    private FinanceAction action;
    
    @Embedded
    @AttributeOverrides( {
        @AttributeOverride(name="currency", column=@Column(name="CCY")),
        @AttributeOverride(name="value",    column=@Column(name="CCYVAL")),
        @AttributeOverride(name="localAmount", column=@Column(name="LCYVAL"))
    })
    private MoneySet amount = new MoneySet();
    
    @Column(name="ISACTIVE")
    private Boolean active = Boolean.TRUE;
    
    @Column(name="CODE", length=15)
    @Length(max=15, message="{validator.max}")
    private String code;
    
    @Column(name="INFO")
    private String info;
    
    @Column(name="SERIAL", length=10)
    @Length(max=10, message="{validator.max}")
    private String serial;

    @Column(name="REFERENCE", length=10)
    @Length(max=10, message="{validator.max}")
    private String reference;

    @Column(name="DOCUMENT_TYPE")
    @Enumerated(EnumType.ORDINAL)
    private DocumentType documentType;
    
    @Column(name="DOCUMENTID")
    private Long documentId;
    
    @Column(name="ADVERSE_CODE", length=20)
    @Length(max=20)
    private String adverseCode;
    
    @Column(name="ADVERSE_NAME", length=250)
    @Length(max=250)
    private String adverseName;
    
    @Column(name="PROCESS_TYPE")
    @Enumerated(EnumType.ORDINAL)
    private AdvanceProcessType processType = AdvanceProcessType.Normal;
    
    @ManyToOne
    @JoinColumn(name="WORK_BUNCH_ID")
    @ForeignKey(name="FK_FINANCETXN_WORKBUNCHID")
    private WorkBunch workBunch;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public FinanceAction getAction() {
        return action;
    }

    public void setAction(FinanceAction action) {
        this.action = action;
    }

    public MoneySet getAmount() {
        return amount;
    }

    public void setAmount(MoneySet amount) {
        this.amount = amount;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public DocumentType getDocumentType() {
        return documentType;
    }

    public void setDocumentType(DocumentType documentType) {
        this.documentType = documentType;
    }

    public Long getDocumentId() {
        return documentId;
    }

    public void setDocumentId(Long documentId) {
        this.documentId = documentId;
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
        if (!(object instanceof FinanceTxn)) {
            return false;
        }
        FinanceTxn other = (FinanceTxn)object;
        if (this.getId() != other.getId() && (this.getId() == null || !this.id.equals(other.id))) return false;
        return true;
    }

    @Override
    public String toString() {
        return "com.ut.tekir.entities.FinanceTxn[id=" + getId() + "]";
    }

	public void setAdverseCode(String adverseCode) {
		this.adverseCode = adverseCode;
	}

	public String getAdverseCode() {
		return adverseCode;
	}

	public void setAdverseName(String adverseName) {
		this.adverseName = adverseName;
	}

	public String getAdverseName() {
		return adverseName;
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
    
}
