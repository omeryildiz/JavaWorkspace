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

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import org.hibernate.validator.Length;
import org.hibernate.validator.Valid;

/**
 * Address DataType
 * 
 * @author volkan
 */
@Embeddable
public class ContactPerson implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	@Column(name = "FIRST_NAME", length = 20)
	@Length(max = 20)
	private String firstName;

	@Column(name = "LAST_NAME", length = 30)
	@Length(max = 30)
	private String lastName;

	@Column(name = "TITLE", length = 30)
	@Length(max = 30)
	private String title;

	@Column(name = "EMAIL")
	private String email;
	
	@Embedded
	@Valid
	@AttributeOverrides( {
			@AttributeOverride(name = "countryCode", column = @Column(name = "PHONE_COUNTRYCODE")),
			@AttributeOverride(name = "areaCode", column = @Column(name = "PHONE_AREACODE")),
			@AttributeOverride(name = "number", column = @Column(name = "PHONE_NUMBER")),
			@AttributeOverride(name = "extention", column = @Column(name = "PHONE_EXTENTION")) })
	private Phone phone = new Phone();

	@Embedded
	@Valid
	@AttributeOverrides( {
			@AttributeOverride(name = "countryCode", column = @Column(name = "FAX_COUNTRYCODE")),
			@AttributeOverride(name = "areaCode", column = @Column(name = "FAX_AREACODE")),
			@AttributeOverride(name = "number", column = @Column(name = "FAX_NUMBER")),
			@AttributeOverride(name = "extention", column = @Column(name = "FAX_EXTENTION")) })
	private Phone fax = new Phone();

	public ContactPerson() {
		this.firstName = "";
		this.lastName = "";
		this.title = "";
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Phone getPhone() {
		if (phone == null) {
			phone = new Phone();
		}
		return phone;
	}

	public void setPhone(Phone phone) {
		this.phone = phone;
	}

	public Phone getFax() {
		if (fax == null) {
			fax = new Phone();
		}
		return fax;
	}

	public void setFax(Phone fax) {
		this.fax = fax;
	}

}
