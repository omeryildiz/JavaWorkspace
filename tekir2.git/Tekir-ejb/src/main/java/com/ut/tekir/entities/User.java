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
import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
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
import org.hibernate.annotations.Cascade;
import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;
import org.hibernate.validator.Valid;

/**
 * Entity class User
 * 
 * @author haky
 */
@Entity
@Table(name="USERS")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE,generator="genericSeq")
    @Column(name="ID")
    private Long id;
    
    @Column(name="USER_NAME", length=10, nullable=false, unique=true)
    @NotNull
    @Length(max=10, min=1)
    private String userName;
    
    @Column(name="PASSWORD", length=40, nullable=false)
    @NotNull
    @Length(max=40, min=1)
    private String pass;
    
    @Column(name="FULL_NAME", length=50)
    @Length(max=50)
    private String fullName;
    
    @Column(name="DEPARTMENT", length=40)
    @Length(max=40)
    private String department;
    
    @Column(name="TITLE", length=40)
    @Length(max=40)
    private String title;
    
    @Column(name="PHONE_DESK", length=20)
    @Length(max=20)
    private String phoneDesk;
    
    @Column(name="PHONE_MOBILE", length=20)
    @Length(max=20)
    private String phoneMobile;
    
    @Column(name="EMAIL")
    private String email;
    
    @Column(name="ASSISTANT", length=40)
    @Length(max=40)
    private String assistant;
    
    @Column(name="MANAGER", length=40)
    @Length(max=40)
    private String manager;
    
    @Column(name="SYSTEM")
    private Boolean system;
    
    @Column(name="ISACTIVE")
    private Boolean active = Boolean.TRUE;

    @ManyToOne
    @JoinColumn(name="CONTACT_ID")
    private Contact contact;
    
    @ManyToOne
    @JoinColumn(name="ACCOUNT_ID")
    private Account account;
    
    @ManyToOne
    @JoinColumn(name="WAREHOUSE_ID")
    private Warehouse warehouse;
    
    @OneToMany(mappedBy="user", cascade=CascadeType.ALL)
    @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    private List<UserRole> roles = new ArrayList<UserRole>();

    /**
     * Kullanıcının yapabileceği max. indirim bilgilerini tutar.
     */
    @Embedded
    @Valid
    @AttributeOverrides( {
    	@AttributeOverride(name="percentage", column=@Column(name="DISCOUNT_PERCENTAGE")),
    	@AttributeOverride(name="rate", column=@Column(name="DISCOUNT_RATE")),
        @AttributeOverride(name="currency", column=@Column(name="DISCOUNT_CCY")),
        @AttributeOverride(name="value",    column=@Column(name="DISCOUNT_VALUE")),
        @AttributeOverride(name="localAmount",    column=@Column(name="DISCOUNT_LCYVAL"))
    })
	private DiscountOrExpense discount = new DiscountOrExpense();

    /**
     * kullanıcıya has indirim olup olmayacağı bilgisini tutar.
     */
    @Column(name="HAS_DISCOUNT")
    private Boolean hasDiscount = Boolean.FALSE;
    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (this.getId() != null ? this.getId().hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof User)) {
            return false;
        }
        User other = (User)object;
        if (this.getId() != other.getId() && (this.getId() == null || !this.id.equals(other.id))) return false;
        return true;
    }

    @Override
    public String toString() {
        return "com.ut.tekir.entities.User[id=" + getId() + "]";
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    public Contact getContact() {
		return contact;
	}

	public void setContact(Contact contact) {
		this.contact = contact;
	}

	public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPhoneDesk() {
        return phoneDesk;
    }

    public void setPhoneDesk(String phoneDesk) {
        this.phoneDesk = phoneDesk;
    }

    public String getPhoneMobile() {
        return phoneMobile;
    }

    public void setPhoneMobile(String phoneMobile) {
        this.phoneMobile = phoneMobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAssistant() {
        return assistant;
    }

    public void setAssistant(String assistant) {
        this.assistant = assistant;
    }

    public String getManager() {
        return manager;
    }

    public void setManager(String manager) {
        this.manager = manager;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Boolean getSystem() {
        return system;
    }

    public void setSystem(Boolean system) {
        this.system = system;
    }

    public List<UserRole> getRoles() {
        return roles;
    }

    public void setRoles(List<UserRole> roles) {
        this.roles = roles;
    }

	public DiscountOrExpense getDiscount() {
		if (discount == null) {
			discount = new DiscountOrExpense();
		}
		return discount;
	}

	public void setDiscount(DiscountOrExpense discount) {
		this.discount = discount;
	}

	public Boolean getHasDiscount() {
		return hasDiscount;
	}

	public void setHasDiscount(Boolean hasDiscount) {
		this.hasDiscount = hasDiscount;
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public Warehouse getWarehouse() {
		return warehouse;
	}

	public void setWarehouse(Warehouse warehouse) {
		this.warehouse = warehouse;
	}
	
	
}
