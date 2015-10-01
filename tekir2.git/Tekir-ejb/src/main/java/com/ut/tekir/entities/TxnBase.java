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
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.validator.Length;
import org.hibernate.validator.Valid;

/**
 * Txn tabloları için kullanacağımız ortak sınıftır.
 * @author "sinan.yumak"
 *
 */
@MappedSuperclass
public class TxnBase extends AuditBase implements Serializable {
	private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE,generator="genericSeq")
    @Column(name="ID")
    private Long id;

    @Column(name="TXN_DATE")
    @Temporal(value=TemporalType.DATE)
    private Date date;
    
    @Column(name="FINANCE_ACTION")
    @Enumerated(EnumType.ORDINAL)
    private FinanceAction action;
    
    @Column(name="ISACTIVE")
    private Boolean active = Boolean.TRUE;
    
    @Column(name="CODE", length=15)
    @Length(max=15)
    private String code;
    
    @Column(name="INFO")
    private String info;
    
    @Column(name="SERIAL", length=10)
    @Length(max=10)
    private String serial;

    @Column(name="REFERENCE", length=10)
    @Length(max=10)
    private String reference;
        
    @Column(name="DOCUMENT_TYPE")
    @Enumerated(EnumType.ORDINAL)
    private DocumentType documentType;
    
    @Column(name="DOCUMENT_ID")
    private Long documentId;

    @Column(name="ADVERSE_CODE", length=20)
    @Length(max=20)
    private String adverseCode;
    
    @Column(name="ADVERSE_NAME", length=250)
    @Length(max=250)
    private String adverseName;

    /**
     * Gerçekte ödenecek tutardır.
     */
    @Embedded
    @Valid
    @AttributeOverrides( {
        @AttributeOverride(name="currency", column=@Column(name="CCY")),
        @AttributeOverride(name="value",    column=@Column(name="CCYVAL")),
        @AttributeOverride(name="localAmount", column=@Column(name="LCYVAL"))
    })
    private MoneySet amount = new MoneySet();

    @Column(name="REPAID_STATUS")
    private boolean repaidStatus = false;
    
	/**
	 * Eğer satırın geri ödemesi yapıldı ise, hangi fatura veya 
	 * hangi transfer fişi ile yapıldığı bilgisini tutar.
	 */
	@Column(name="REFERENCE_ID")
	private Long referenceId;
	
    @Column(name="REFERENCE_DOC_TYPE")
    @Enumerated(EnumType.ORDINAL)
    private DocumentType referenceDocumentType;
    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (this.getId() != null ? this.getId().hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof TxnBase)) {
            return false;
        }
        TxnBase other = (TxnBase)object;
        if (this.getId() != other.getId() && (this.getId() == null || !this.id.equals(other.id))) return false;
        return true;
    }

    @Override
    public String toString() {
        return "com.ut.tekir.entities.TxnBase[id=" + getId() + "]";
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public String getAdverseCode() {
		return adverseCode;
	}

	public void setAdverseCode(String adverseCode) {
		this.adverseCode = adverseCode;
	}

	public String getAdverseName() {
		return adverseName;
	}

	public void setAdverseName(String adverseName) {
		this.adverseName = adverseName;
	}

	public MoneySet getAmount() {
		return amount;
	}

	public void setAmount(MoneySet amount) {
		this.amount = amount;
	}

	public boolean isRepaidStatus() {
		return repaidStatus;
	}

	public void setRepaidStatus(boolean repaidStatus) {
		this.repaidStatus = repaidStatus;
	}

	public Long getReferenceId() {
		return referenceId;
	}

	public void setReferenceId(Long referenceId) {
		this.referenceId = referenceId;
	}

	public DocumentType getReferenceDocumentType() {
		return referenceDocumentType;
	}

	public void setReferenceDocumentType(DocumentType referenceDocumentType) {
		this.referenceDocumentType = referenceDocumentType;
	}

}
