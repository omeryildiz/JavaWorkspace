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
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.hibernate.validator.Length;

/**
 * @author sinan.yumak
 *
 */
@Entity
@Table(name="POS_COMMISION_DETAIL")
public class PosCommisionDetail implements Serializable{
    
	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.TABLE,generator="genericSeq")
    @Column(name="ID")
	private Long id;

	@ManyToOne
	@JoinColumn(name="OWNER_ID")
	private PosCommision owner;

	/**
	 * komisyon ay bilgisi.
	 */
	@Column(name="MONTH")
	private Integer month = 0;

	/**
	 * komisyon oran bilgisini yüzde olarak tutar.
	 */
	@Column(name="RATE")
	private Double rate = 0.0d;
	
	/**
	 * komisyon valör bilgisini gün olarak tutar.
	 */
	@Column(name="VALOR")
	private Integer valor = 0;

	/**
	 * komisyonun aktiflik bilgisini tutar.
	 */
	@Column(name="IS_ACTIVE")
	private Boolean active = Boolean.TRUE;

    @Column(name="IS_CAMPAIN")
	private Boolean campain = Boolean.FALSE;

    @Column(name="TYPE_NAME", length=20)
    @Length(max=20)
    private String typeName;

	/**
	 * satırın ön tanımlı olup olmadığı bilgisini tutar. Bir komisyon 
	 * tanımında sadece bir adet ön tanımlı detay olmalıdır.
	 */
	@Column(name="DEFAULT_DETAIL")
	private Boolean defaultDetail = Boolean.FALSE;
	
	@Override
    public int hashCode() {
        int hash = 0;
        hash += (this.getId() != null ? this.getId().hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof PosCommisionDetail)) {
            return false;
        }
        PosCommisionDetail other = (PosCommisionDetail)object;
        if (this.getId() != other.getId() && (this.getId() == null || !this.getId().equals(other.getId()))) return false;
        return true;
    }

    @Override
    public String toString() {
        return "com.ut.tekir.entities.PosCommisionDetail[id=" + getId() + "]";
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public PosCommision getOwner() {
		return owner;
	}

	public void setOwner(PosCommision owner) {
		this.owner = owner;
	}

	public Integer getMonth() {
		return month;
	}

	public void setMonth(Integer month) {
		this.month = month;
	}

	public Double getRate() {
		return rate;
	}

	public void setRate(Double rate) {
		this.rate = rate;
	}

	public Integer getValor() {
		return valor;
	}

	public void setValor(Integer valor) {
		this.valor = valor;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

    public Boolean getCampain() {
        return campain;
    }

    public void setCampain(Boolean campain) {
        this.campain = campain;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

	public Boolean getDefaultDetail() {
		return defaultDetail;
	}

	public void setDefaultDetail(Boolean defaultDetail) {
		this.defaultDetail = defaultDetail;
	}
    
}
