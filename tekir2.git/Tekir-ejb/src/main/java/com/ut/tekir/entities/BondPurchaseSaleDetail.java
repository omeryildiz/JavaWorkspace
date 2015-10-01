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

/**
 * @author sinan.yumak
 *
 */
@Entity
@Table(name="BOND_PURCHASE_SALE_DETAIL")
public class BondPurchaseSaleDetail implements Serializable {
	
	private static final long serialVersionUID = 1L;
	    
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE,generator="genericSeq")
    @Column(name="ID")
    private Long id;

    @ManyToOne
    @JoinColumn(name="PORTFOLIO_ID")
    private Portfolio portfolio;
    
	@Column(name="NOMINAL", precision=10, scale=2)
	private BigDecimal nominal = BigDecimal.ZERO;

	@Column(name="VAL", precision=10, scale=2)
	private BigDecimal val = BigDecimal.ZERO;

	@Column(name="TAX", precision=10, scale=2)
	private BigDecimal tax = BigDecimal.ZERO;

	@ManyToOne
	@JoinColumn(name="BOND_ID")
	private BondPurchaseSale bondPurchaseSale;
	
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof BondPurchaseSaleDetail)) {
            return false;
        }
        BondPurchaseSaleDetail other = (BondPurchaseSaleDetail) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.ut.pusula.muhasebe.entities.BondPurchaseSaleDetail[id=" + id + "]";
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public BigDecimal getNominal() {
		return nominal;
	}

	public void setNominal(BigDecimal nominal) {
		this.nominal = nominal;
	}

	public BigDecimal getVal() {
		return val;
	}

	public void setVal(BigDecimal val) {
		this.val = val;
	}

	public BondPurchaseSale getBondPurchaseSale() {
		return bondPurchaseSale;
	}

	public void setBondPurchaseSale(BondPurchaseSale bondPurchaseSale) {
		this.bondPurchaseSale = bondPurchaseSale;
	}

	public Portfolio getPortfolio() {
		return portfolio;
	}

	public void setPortfolio(Portfolio portfolio) {
		this.portfolio = portfolio;
	}

	public BigDecimal getTax() {
		return tax;
	}

	public void setTax(BigDecimal tax) {
		this.tax = tax;
	}
}
