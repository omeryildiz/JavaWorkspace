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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;

/**
 * Uygulama içinde kullanılacak olan seçenekleri tanımlar.
 * Her seçenek namespace kuralları içinde bir key sahibi olacak : 
 * base.DefaultCCY
 * base.DefaultLang
 * base.DefaultCountry
 * payment.AutoSerial
 * payment.SerialBegin
 * payment.NumberBegin
 * v.b.
 * @author haky
 */
@Entity
@Table(name="OPTION_DEFINITION")
public class OptionDefinition implements java.io.Serializable {

    private static final long serialVersionUID = 1L;

    public enum DataType{ String, Integer, Float, Date, Time, DateTime, Boolean, ControlType, BigDecimal }

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE,generator="genericSeq")
    @Column(name="ID")
    private Long id;
    
    @Column(name="OD_KEY", nullable=false, unique=true, length=50)
    @NotNull
    @Length(max=50, min=1)
    private String key;
    
    @Column(name="USER_BASE")
    private Boolean userBase;
    
    @Column(name="DEFAULT_VALUE")
    private String defaultValue;
    
    @Column(name="VALUE_LIST")
    private String valueList;
    
    @Column(name="DATA_TYPE")
    @Enumerated(EnumType.ORDINAL)
    private DataType dataType;
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Boolean getUserBase() {
        return userBase;
    }

    public void setUserBase(Boolean userBase) {
        this.userBase = userBase;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getValueList() {
        return valueList;
    }

    public void setValueList(String valueList) {
        this.valueList = valueList;
    }

    public DataType getDataType() {
        return dataType;
    }

    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof OptionDefinition)) {
            return false;
        }
        OptionDefinition other = (OptionDefinition) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.ut.tekir.entities.OptionDefinition[id=" + id + "]";
    }
    
}
