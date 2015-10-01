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
import javax.persistence.Table;

/**
 * Ürün/hizmet için stok grubu bilgisini tutan sınıfımızdır.
 * (Bilim optik için marka bilgisini tutuyor olacak.) 
 * @author sinan.yumak
 */
@Entity
@Table(name="PRODUCT_GROUP")
public class ProductGroup implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE,generator="genericSeq")
    @Column(name="ID")
    private Long id;
	
    @Column(name="CODE")
    private String code;

    @Column(name="NAME")
    private String name;
    
    @Column(name="INFO")
    private String info;
    
    @Column(name="IS_ACTIVE")
    private Boolean active = Boolean.TRUE;
    
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

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
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
        if (!(object instanceof ProductGroup)) {
            return false;
        }
        ProductGroup other = (ProductGroup)object;
        if (this.getId() != other.getId() && (this.getId() == null || !this.id.equals(other.id))) return false;
        return true;
    }

    @Override
    public String toString() {
        return "com.ut.tekir.entities.ProductGroup[id=" + getId() + "]";
    }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
