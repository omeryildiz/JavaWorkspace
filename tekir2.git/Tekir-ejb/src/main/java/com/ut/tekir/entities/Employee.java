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
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;
import org.hibernate.validator.Valid;

@Entity
@Table(name="EMPLOYEE")
public class Employee implements Serializable{
	
	private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE,generator="genericSeq")
    @Column(name="ID")
    private Long id;
	
	@Column(name="PERSONNEL_NO", nullable=false, unique=true, length=20)
	@NotNull
	@Length(max=20, min=1)
	private String personnelNo; //kurum sicilno
	
	@Column(name="SSN", length=11)
	private String ssn;  //tckimlikno
	
	@Column(name="PASSPORT_NO", length=11)
	@Length(max=11)
	private String passportNo;
	
	@Column(name="SSK_NO", length=20)
	@Length(max=20)
	private String sskNo;
	
	@Column(name="FIRST_NAME", length=30)
	@Length(max=30)
	private String firstName;
	
	@Column(name="LAST_NAME", length=20)
	@Length(max=20)
	private String lastName;
	
	@Embedded
	@Valid
	private Phone phone = new Phone();
	
	@Embedded
	@Valid
    private Address address=new Address();
	
	@Column(name="EMAIL")
	private String email;
	
	@Column(name="ISACTIVE")
	private Boolean active = Boolean.TRUE;
	
	@ManyToOne
	@JoinColumn(name="DEPARTMENT_ID")
	private Department department;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSsn() {
		return ssn;
	}

	public void setSsn(String ssn) {
		this.ssn = ssn;
	}

	public String getPassportNo() {
		return passportNo;
	}

	public void setPassportNo(String passportNo) {
		this.passportNo = passportNo;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public Phone getPhone() {
		return phone;
	}

	public void setPhone(Phone phone) {
		this.phone = phone;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Department getDepartment() {
		return department;
	}

	public void setDepartment(Department department) {
		this.department = department;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public String getSskNo() {
		return sskNo;
	}

	public void setSskNo(String sskNo) {
		this.sskNo = sskNo;
	}

	public String getPersonnelNo() {
		return personnelNo;
	}

	public void setPersonnelNo(String personnelNo) {
		this.personnelNo = personnelNo;
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
        if (!(object instanceof Employee)) {
            return false;
        }
        Employee other = (Employee)object;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) return false;
        return true;
    }
	
	@Override
    public String toString() {
        return "com.ut.tekir.entities.Employee[id=" + id + "]";
    }
	
	/**
	 * Employee popuptan gelen degeri kullanan converter icin gerekli
	 * @see EmployeeCaptionConverter
	 */
	@Transient
	public String getCaption(){
	    return "[" + getPersonnelNo() + "] " + getFirstName() + " " + getLastName();
	}
	
}
