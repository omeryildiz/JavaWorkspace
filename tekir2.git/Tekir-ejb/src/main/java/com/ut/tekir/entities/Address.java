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
import javax.persistence.Embeddable;

import org.hibernate.validator.Length;

import com.ut.tekir.configuration.SystemConfiguration;

/**
 * Address DataType
 * @author haky
 */
@Embeddable
public class Address implements Serializable {
    
    private static final long serialVersionUID = 1L;

    @Column(name="STREET", length=250)
    @Length(max=250)
    private String street;

    @Column(name="PROVINCE", length=40)
    @Length(max=40)
    private String province;
    
    @Column(name="CITY", length=40)
    @Length(max=40)
    private String city;
    
    @Column(name="COUNTRY", length=40)
    @Length(max=40)
    private String country = SystemConfiguration.COUNTRY_NAME;
    
    @Column(name="ZIP", length=10)
    @Length(max=10)
    private String zip;
    
    public Address() {
    }
    
    public Address(String street, String province, String city, String country, String zip) {
		super();
		this.street = street;
		this.province = province;
		this.city = city;
		this.country = country;
		this.zip = zip;
	}

	public String description() {
		StringBuffer sb = new StringBuffer();
    	if (getStreet() != null && getStreet().length() != 0) {
			sb.append("\n");
			sb.append(getStreet());
		}
		if (getZip() != null && getZip().length() != 0) {
			sb.append("\n");
			sb.append(getZip());
		}
		if (getProvince() != null && getProvince().length() != 0) {
			sb.append("\n");
			sb.append(getProvince());
			sb.append("/");
		}
		if (getCity() != null && getCity().length() != 0) {
			sb.append(getCity());
		}
		if (getCountry() != null  && getCountry().length() != 0) {
			sb.append("\n");
			sb.append("-");
			sb.append(getCountry());
		}
		return sb.toString();
    }
    
    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }
    
}
