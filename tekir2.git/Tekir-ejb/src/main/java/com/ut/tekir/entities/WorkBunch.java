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
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.validator.Length;

/**
 * İş takibi bilgilerini tutan model sınıfımızdır.
 * 
 * @author sinan.yumak
 *
 */
@Entity
@Table(name="WORK_BUNCH")
public class WorkBunch implements Serializable{

	private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    @Column(name="ID")
    private Long id;

    @ManyToOne
    @JoinColumn(name="CONTACT_ID")
    @ForeignKey(name="FK_WORKBUNCH_CONTACTID")
    private Contact contact;
    
    @Column(name="INFO")
    private String info;

    @Column(name="CODE",length=20,unique=true,nullable=false)
    @Length(max=20, min=1)
    private String code;

    @Column(name="NAME",length=40)
    private String name;

	@Enumerated(EnumType.ORDINAL)
    @Column(name="WORKBUNCH_STATUS")
    private WorkBunchStatus workBunchStatus;
    
	@Temporal(TemporalType.DATE)
    @Column(name="BEGIN_DATE")
    private Date beginDate;

	@Temporal(TemporalType.DATE)
    @Column(name="END_DATE")
    private Date endDate;
    
    @Column(name="ISACTIVE")
    private Boolean active = Boolean.TRUE;

	public WorkBunch() {
		super();
	}

/*
	public WorkBunch(WorkBunch parent) {
    	this.parent = parent;
	}
	public void addChild() {
		getChildList().add(new WorkBunch(this));
	}
 */
    
	@Override
    public int hashCode() {
        int hash = 0;
        hash += (this.getId() != null ? this.getId().hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof WorkBunch)) {
            return false;
        }
        WorkBunch other = (WorkBunch)object;
        if (this.getId() != other.getId() && (this.getId() == null || !this.id.equals(other.id))) return false;
        return true;
    }

    @Override
    public String toString() {
        return "com.ut.tekir.entities.WorkBunch[id=" + getId() + "]";
    }

    public String getCaption(){
        return "[" + getCode() + "] " + getName();
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

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Date getBeginDate() {
		return beginDate;
	}

	public void setBeginDate(Date beginDate) {
		this.beginDate = beginDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

    public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

    public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

    public WorkBunchStatus getWorkBunchStatus() {
		return workBunchStatus;
	}

	public void setWorkBunchStatus(WorkBunchStatus workBunchStatus) {
		this.workBunchStatus = workBunchStatus;
	}

}
