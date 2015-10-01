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
import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;

/**
 * Entity class Currency
 * 
 * @author haky
 */
@Entity
@Table(name="CURRENCIES")
public class Currency implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE,generator="genericSeq")
    @Column(name="ID")
    private Long id;
	
    @Column(name="CODE", nullable=false, unique=true, length=3)
    @NotNull
    @Length(max=3, min=1)
    private String code;
    
    @Column(name="NAME", length=20)
    @Length(max=20)
    private String name;
    
    @Column(name="COUNTRY", length=30)
    @Length(max=30)
    private String country;
    
    @Column(name="PIP")
    private Integer pip = 0;
    
    @Column(name="SYSTEM")
    private Boolean system;
    
    @Column(name="ISACTIVE")
    private Boolean active = Boolean.TRUE;

    @Column(name="DECIMAL_CODE", length=4)
    @Length(max=4)
    private String decimalCode;
    
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Integer getPip() {
        return pip;
    }

    public void setPip(Integer pip) {
        this.pip = pip;
    }

    public Boolean getSystem() {
        return system;
    }

    public void setSystem(Boolean system) {
        this.system = system;
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
        if (!(object instanceof Currency)) {
            return false;
        }
        Currency other = (Currency)object;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) return false;
        return true;
    }

    @Override
    public String toString() {
        return getCode();
    }

	public String getDecimalCode() {
		return decimalCode;
	}

	public void setDecimalCode(String decimalCode) {
		this.decimalCode = decimalCode;
	}

}
