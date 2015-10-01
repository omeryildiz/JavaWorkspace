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
import javax.persistence.Transient;

/**
 * Entity class CurrencyPair
 * 
 * @author haky
 */
@Entity
@Table(name="CURRENCY_PAIR")
public class CurrencyPair implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE,generator="genericSeq")
    @Column(name="ID")
    private Long id;
	
    @Column(name="INFO")
    private String info;
    
    @ManyToOne
    @JoinColumn(name="HARD_CURRENCY_ID")
    private Currency hardCurrency;
    
    @ManyToOne
    @JoinColumn(name="WEAK_CURRENCY_ID")
    private Currency weakCurrency;
    
    @Column(name="SYSTEM")
    private Boolean system;
    
    @Column(name="ISACTIVE")
    private Boolean active = Boolean.TRUE;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public Boolean getSystem() {
        return system;
    }

    public void setSystem(Boolean system) {
        this.system = system;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Currency getHardCurrency() {
        return hardCurrency;
    }

    public void setHardCurrency(Currency hardCurrency) {
        this.hardCurrency = hardCurrency;
    }

    public Currency getWeakCurrency() {
        return weakCurrency;
    }

    public void setWeakCurrency(Currency weakCurrency) {
        this.weakCurrency = weakCurrency;
    }

    @Transient
    public String getCaption(){
        return getHardCurrency().getCode() + "-" + getWeakCurrency().getCode();
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
        if (!(object instanceof CurrencyPair)) {
            return false;
        }
        CurrencyPair other = (CurrencyPair)object;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) return false;
        return true;
    }

    @Override
    public String toString() {
        return "com.ut.tekir.entities.CurrencyPair[id=" + id + "]";
    }

}
