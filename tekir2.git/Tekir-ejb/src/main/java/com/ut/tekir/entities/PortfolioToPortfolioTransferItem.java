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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.validator.Length;

/**
 * Entity class PortfolioToPortfolioTransferItem
 * 
 * @author bilge
 */
@Entity
@Table(name="PORTFOLIO_TO_PORTFOLIO_TRNITM")
public class PortfolioToPortfolioTransferItem implements Serializable {

	private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE,generator="genericSeq")
    @Column(name="ID")
    private Long id;

    @ManyToOne
    @JoinColumn(name="OWNER_ID")
    private PortfolioToPortfolioTransfer owner;
    
    @ManyToOne
    @JoinColumn(name="SECURITY_ID")
    private Security security;
    
	@Column(name="CCY", length=3)
	@Length(max=3)
    private String currency;

    @Column(name="INFO")
    private String info;
    
    @Column(name="LINE_CODE", length=10)
    private String lineCode;
    
    @Column(name="NOMINAL", precision=10 ,scale=2)
    private BigDecimal nominal = BigDecimal.ZERO;
        
    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PortfolioToPortfolioTransfer getOwner() {
        return owner;
    }

    public void setOwner(PortfolioToPortfolioTransfer owner) {
        this.owner = owner;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getLineCode() {
        return lineCode;
    }

    public void setLineCode(String lineCode) {
        this.lineCode = lineCode;
    }

	public void setNominal(BigDecimal nominal) {
		this.nominal = nominal;
	}


	public BigDecimal getNominal() {
		return nominal;
	}


	public void setSecurity(Security security) {
		this.security = security;
	}


	public Security getSecurity() {
		return security;
	}


	public void setCurrency(String currency) {
		this.currency = currency;
	}


	public String getCurrency() {
		return currency;
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
        if (!(object instanceof PortfolioToPortfolioTransferItem)) {
            return false;
        }
        PortfolioToPortfolioTransferItem other = (PortfolioToPortfolioTransferItem)object;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) return false;
        return true;
    }
   
    @Override
    public String toString() {
        return "com.ut.tekir.entities.PortfolioToPortfolioTransferItem[id=" + id + "]";
    }

}
