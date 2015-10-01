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

import org.hibernate.validator.Length;

/**
 * Entity class BondTxn
 * 
 * @author bilge
 */
@Entity
@Table(name="BOND_TXN")
public class BondTxn extends AuditBase implements Serializable {

	private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE,generator="genericSeq")
    @Column(name="ID")
	private Long id;

    @ManyToOne
    @JoinColumn(name="PORTFOLIO_ID")
    private Portfolio portfolio;
    
    @ManyToOne
    @JoinColumn(name="SECURITY_ID")
    private Security security;
       
    @Column(name="TXNDATE")
    @Temporal(value=TemporalType.DATE)
    private Date date;
    
    @Column(name="NOMINAL", precision=10, scale=2)
    private BigDecimal nominal = BigDecimal.ZERO;
    
    @Column(name="ACTION")
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
    
    @Column(name="DOCUMENTID")
    private Long documentId;
    
    public Long getId() {
        return this.id;
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

	public void setPortfolio(Portfolio portfolio) {
		this.portfolio = portfolio;
	}

	public Portfolio getPortfolio() {
		return portfolio;
	}

	public void setAction(FinanceAction action) {
		this.action = action;
	}


	public FinanceAction getAction() {
		return action;
	}

	public void setSecurity(Security security) {
		this.security = security;
	}


	public Security getSecurity() {
		return security;
	}

	public void setNominal(BigDecimal nominal) {
		this.nominal = nominal;
	}

	public BigDecimal getNominal() {
		return nominal;
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
        if (!(object instanceof BondTxn)) {
            return false;
        }
        BondTxn other = (BondTxn)object;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) return false;
        return true;
    }

    @Override
    public String toString() {
        return "com.ut.tekir.entities.BondTxn[id=" + id + "]";
    }

}
