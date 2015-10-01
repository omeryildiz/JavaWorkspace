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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Cascade;
import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;
import org.hibernate.validator.Valid;

@Entity
@Table(name="BANK_BRACNHES")
public class BankBranch extends AuditBase implements Serializable {

	private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE,generator="genericSeq")
    @Column(name="ID")
	private Long id;
	
    @Column(name="CODE", length=20, unique=true, nullable=false)
    @Length(max=20, min=1)
    @NotNull
	private String code;

    @Column(name="NAME", length=50)
    @Length(max=50)
    private String name;
    
    @Column(name="INFO")
	private String info;
    
    @Column(name="ISACTIVE")
	private Boolean active = Boolean.TRUE;

    @Column(name="WORK_BEGIN")
    @Temporal(TemporalType.DATE)
    private Date workBegin;
	
    @Column(name="WORK_END")
    @Temporal(TemporalType.DATE)
    private Date workEnd;
	
	@ManyToOne
	@JoinColumn(name="BANK_ID")
	private Bank bank;
	
    @Column(name="EFT_CODE", length=20)
    @Length(max=20)
	private String eftCode;

    @Column(name="CHEQUE_CODE", length=20)
    @Length(max=20)
    private String chequeCode;
	
	@Embedded
	@Valid
	private Address address = new Address();

    @ManyToOne
    @JoinColumn(name="ORGANIZATION_ID")
    private Organization organization;

	@Embedded
	private ContactPerson contactPerson = new ContactPerson();
	/* s
    @OneToMany(mappedBy = "bankBranch", cascade=CascadeType.ALL )
    @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    private List<BankAccount> items = new ArrayList<BankAccount>();
	 */
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public Organization getOrganization() {
		return organization;
	}

	public void setOrganization(Organization organization) {
		this.organization = organization;
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

	public Date getWorkBegin() {
		return workBegin;
	}

	public void setWorkBegin(Date workBegin) {
		this.workBegin = workBegin;
	}

	public Date getWorkEnd() {
		return workEnd;
	}

	public void setWorkEnd(Date workEnd) {
		this.workEnd = workEnd;
	}

	public Bank getBank() {
		return bank;
	}

	public void setBank(Bank bank) {
		this.bank = bank;
	}

	public String getEftCode() {
		return eftCode;
	}

	public void setEftCode(String eftCode) {
		this.eftCode = eftCode;
	}

	public String getChequeCode() {
		return chequeCode;
	}

	public void setChequeCode(String chequeCode) {
		this.chequeCode = chequeCode;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public ContactPerson getContactPerson() {
		return contactPerson;
	}

	public void setContactPerson(ContactPerson contactPerson) {
		this.contactPerson = contactPerson;
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
        if (!(object instanceof BankBranch)) {
            return false;
        }
        BankBranch other = (BankBranch)object;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) return false;
        return true;
    }
	
	@Override
    public String toString() {
        return "com.ut.tekir.entities.BankBranch[id=" + id + "]";
    }
/*
	public List<BankAccount> getItems() {
		return items;
	}

	public void setItems(List<BankAccount> items) {
		this.items = items;
	}
*/
}
