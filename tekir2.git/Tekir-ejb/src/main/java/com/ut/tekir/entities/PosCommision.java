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

/**
 * Poslar için komisyon oranlarını tutan modelimizdir.
 * @author sinan.yumak
 *
 */
@Entity
@Table(name="POS_COMMISION")
public class PosCommision extends AuditBase implements Serializable {
    
	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.TABLE,generator="genericSeq")
    @Column(name="ID")
	private Long id;

	@Column(name="IS_ACTIVE")
	private Boolean active = Boolean.TRUE;

	@OneToMany(mappedBy="owner", cascade=CascadeType.ALL)
    @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    private List<PosCommisionDetail> detailList = new ArrayList<PosCommisionDetail>();

    /**
     * komisyonun bağlı olduğu pos bilgisini tutar.
     */
    @ManyToOne
    @JoinColumn(name="POS_ID")
    private Pos pos;

    /**
     * komisyon geçerlilik başlangıç tarihi
     */
    @Column(name="START_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date startDate;
    
    /**
     * komisyon geçerlilik bitiş tarihi
     */
    @Column(name="END_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date endDate;
    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (this.getId() != null ? this.getId().hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof PosCommision)) {
            return false;
        }
        PosCommision other = (PosCommision)object;
        if (this.getId() != other.getId() && (this.getId() == null || !this.getId().equals(other.getId()))) return false;
        return true;
    }

    @Override
    public String toString() {
        return "com.ut.tekir.entities.PosCommision[id=" + getId() + "]";
    }

    public PosCommisionDetail getDefaultDetail() {
    	for (PosCommisionDetail item : detailList) {
    		if (item.getDefaultDetail()) return item;
    	}
    	return null;
    }

    public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public List<PosCommisionDetail> getDetailList() {
		return detailList;
	}

	public void setDetailList(List<PosCommisionDetail> detailList) {
		this.detailList = detailList;
	}

	public Pos getPos() {
		return pos;
	}

	public void setPos(Pos pos) {
		this.pos = pos;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
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
	
}
