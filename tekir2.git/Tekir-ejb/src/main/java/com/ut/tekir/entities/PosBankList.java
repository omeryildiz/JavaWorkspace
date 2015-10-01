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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.ForeignKey;

/**
 * Posların banka eşlemelerini tutar.
 * @author sinan.yumak
 *
 */
@Entity
@Table(name="POS_BANK_LIST")
public class PosBankList extends AuditBase implements Serializable {
	private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE,generator="genericSeq")
    @Column(name="ID")
	private Long id;
	
    @ManyToOne
    @JoinColumn(name="OWNER_ID")
    @ForeignKey(name="FK_POSTCARDLIST_ONWERID")
    private Pos owner;

    @ManyToOne
    @JoinColumn(name="BANK_ID")
    @ForeignKey(name="FK_POSTBANKLIST_BANKID")
    private Bank bank;

    @Column(name="ISACTIVE")
	private Boolean active = Boolean.TRUE;

    @Column(name="BEGIN_DATE")
    @Temporal(TemporalType.DATE)
	private Date beginDate;

    @Column(name="END_DATE")
    @Temporal(TemporalType.DATE)
	private Date endDate;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	
	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
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

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }
	
	@Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof PosBankList)) {
            return false;
        }
        PosBankList other = (PosBankList)object;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) return false;
        return true;
    }
	
	@Override
    public String toString() {
        return "com.ut.tekir.entities.PosBankList[id=" + id + "]";
    }

	public Pos getOwner() {
		return owner;
	}

	public void setOwner(Pos owner) {
		this.owner = owner;
	}

	public Bank getBank() {
		return bank;
	}

	public void setBank(Bank bank) {
		this.bank = bank;
	}

}
