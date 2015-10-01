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
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;

import com.ut.tekir.tender.PaymentTableGroupModel;

/**
 * Sipariş,kaparo vb. ekranlarda cari tahsilat bilgilerini
 * tutan sınıfımızdır.
 * @author sinan.yumak
 *
 */
@Entity
@Table(name="PAYMENT_TABLE")
public class PaymentTable extends AuditBase implements Serializable {
    
	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.TABLE,generator="genericSeq")
    @Column(name="ID")
	private Long id;

    @OneToMany(mappedBy="owner", cascade=CascadeType.ALL)
    @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    private List<PaymentTableDetail> detailList = new ArrayList<PaymentTableDetail>();

    public Map<PaymentTableGroupModel, BigDecimal> getAsGroupped() {
		Map<PaymentTableGroupModel, BigDecimal> result = new HashMap<PaymentTableGroupModel, BigDecimal>();
    	
		PaymentTableGroupModel model;
    	for (PaymentTableDetail item : detailList) {
			model = new PaymentTableGroupModel(item);

			if (!result.containsKey(model)) {
				result.put(model, item.getAmount().getLocalAmount());
			} else {
				result.put(model, result.get(model).add(item.getAmount().getLocalAmount()));
			}
    	}
    	return result;
    }
    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (this.getId() != null ? this.getId().hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof PaymentTable)) {
            return false;
        }
        PaymentTable other = (PaymentTable)object;
        if (this.getId() != other.getId() && (this.getId() == null || !this.getId().equals(other.getId()))) return false;
        return true;
    }

    @Override
    public String toString() {
        return "com.ut.tekir.entities.PaymentTable[id=" + getId() + "]";
    }

    public PaymentTable clone() {
    	PaymentTable clonedpt = new PaymentTable();
    	clonedpt.setCreateDate(getCreateDate());
    	clonedpt.setCreateUser(getCreateUser());
    	clonedpt.setUpdateDate(getUpdateDate());
    	clonedpt.setUpdateUser(getUpdateUser());
    	
    	for (PaymentTableDetail ptd : getDetailList()) {
    		PaymentTableDetail clonedptd = ptd.clone();

    		clonedptd.setOwner(clonedpt);
    		clonedpt.getDetailList().add(clonedptd);
    	}
    	return clonedpt;
    }
    
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public List<PaymentTableDetail> getDetailList() {
		return detailList;
	}

	public void setDetailList(List<PaymentTableDetail> detailList) {
		this.detailList = detailList;
	}

}
