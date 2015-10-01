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

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;
import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;


/**
 * Fiyat listesi bağlantılarını tutan sınıftır. Bu sınıf 
 * fiyat listelerini gruplamak için kullanılacak.
 * @author sinan.yumak
 *
 */
@Entity
@Table(name="PRICE_LIST")
public class PriceList implements Serializable {

	private static final long serialVersionUID = 1L;
	
    @Id
	@GeneratedValue(strategy = GenerationType.TABLE,generator="genericSeq")
    @Column(name="ID")
    private Long id;

    @Column(name="LIST_NAME")
    private String listName;

    @Column(name="IS_ACTIVE")
    private Boolean active = Boolean.TRUE;

    @Column(name="INFO")
    private String info;

    @Column(name="CODE", nullable=false, length=10)
    @NotNull
    @Length(max=10, min=1)
    private String code;

    @OneToMany(mappedBy = "owner", cascade=CascadeType.ALL )
    @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    private List<PriceItem> items = new ArrayList<PriceItem>();

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }
	
	@Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof PriceList)) {
            return false;
        }
        PriceList other = (PriceList)object;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) return false;
        return true;
    }
	
	@Override
    public String toString() {
        return "com.ut.tekir.entities.PriceList[id=" + id + "]";
    }

	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}

	public String getListName() {
		return listName;
	}

	public void setListName(String listName) {
		this.listName = listName;
	}

	public List<PriceItem> getItems() {
		return items;
	}

	public void setItems(List<PriceItem> items) {
		this.items = items;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
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

}
