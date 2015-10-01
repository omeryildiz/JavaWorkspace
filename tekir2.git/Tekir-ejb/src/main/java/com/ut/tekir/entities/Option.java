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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.validator.Length;

/**
 * Uygulama için tanımlanmış olan seçeneklerin kullanıcı ya da sistem için değerlerini saklar.
 * @author haky
 */
@Entity
@Table(name="OPTIONS")
public class Option implements java.io.Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE,generator="genericSeq")
    @Column(name="ID")
    private Long id;
    
    @Column(name="USER_NAME", length=20)
    @Length(max=20)
    private String user;
    
    @Column(name="OPKEY", length=255)
    @Length(max=255)
    private String key;

    @Column(name="OP_VALUE")
    private String value;
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
    
    @Transient
    public String getAsString(){
        return value;
    }
    
    public void setAsString(String val ){
        value = val;
    }
    
    @Transient
    public Integer getAsInteger(){
        return Integer.parseInt( value );
    }
    
    public void setAsInteger(Integer val ){
        value = val.toString();
    }
    
	@Transient
	@SuppressWarnings("deprecation")
    public Date getAsDate(){
        return new Date(value);
    }
    
    public void setAsDate(Date val) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
    	value = sdf.format(val);
    }

    @Transient
    public Boolean getAsBoolean(){
        return Boolean.parseBoolean(value);
    }
    
    public void setAsBoolean(Boolean bool){
        value = bool.toString();
    }

    @Transient
    public BigDecimal getAsBigDecimal(){
    	return new BigDecimal(value);
    }
    
    public void setAsBigDecimal(BigDecimal aValue){
    	//TODO:scale i 2 mi almalı parametreye mi bağlamalı?
    	value = aValue.setScale(2, RoundingMode.HALF_UP).toString();
    }

    /**
     * İstenen enuma çevirme işlemlerini yapar.
     * @param enumClazz, hedef enum tipi
     * @return enum, çevirimi yapılmış enum.
     */
    @SuppressWarnings("unchecked")
    @Transient
	public <T extends Enum> T getAsEnum( Class<T> enumClazz ) {
    	
    	T o = null;
    	try {
			o = (T)Enum.valueOf(enumClazz, value);
		} catch (Exception e) {
			//verilen değer enumeration içerisinde olmadığından
			//null dönecektir.
		}
    	return o;
    }

    @SuppressWarnings("unchecked")
	public void setAsEnum( Enum enumVal ){
    	value = enumVal.name();
    }
    
    //TODO: DateTime, Time, v.b. alanlar yazılacak.

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Option)) {
            return false;
        }
        Option other = (Option) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.ut.tekir.entities.Option[id=" + id + "]";
    }

    /**
     * @return the key
     */
    public String getKey() {
        return key;
    }

    /**
     * @param key the key to set
     */
    public void setKey(String key) {
        this.key = key;
    }

}
