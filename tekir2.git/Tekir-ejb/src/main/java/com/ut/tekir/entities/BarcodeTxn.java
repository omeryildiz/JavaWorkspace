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


/**
 * Yazdırılacak barkod bilgilerini tutan modelimizdir.
 * @author sinan.yumak
 *
 */
@Entity
@Table(name="BARCODE_TXN")
public class BarcodeTxn implements Serializable {

	private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE,generator="genericSeq")
    @Column(name="ID")
	private Long id;

    /**
     * barkodu yazdırılacak olan ürün.
     */
    @ManyToOne
    @JoinColumn(name="PRODUCT_ID")
    private Product product;
    
    /**
     * barkodun kaç tane yazdırılacağı bilgisi.
     */
    @Column(name="UNIT")
    private Integer unit = 0;
    
    /**
     * Eğer aktif olarak işaretlenmişse yazdır manası geliyor.
     */
    @Column(name="IS_ACTIVE")
    private Boolean active = Boolean.TRUE;
    
	@Override
    public int hashCode() {
        int hash = 0;
        hash += (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }
	
	@Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof BarcodeTxn)) {
            return false;
        }
        BarcodeTxn other = (BarcodeTxn)object;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) return false;
        return true;
    }
	
	@Override
    public String toString() {
        return "com.ut.tekir.entities.BarcodeTxn[id=" + id + "]";
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public Integer getUnit() {
		return unit;
	}

	public void setUnit(Integer unit) {
		this.unit = unit;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}
	
}
