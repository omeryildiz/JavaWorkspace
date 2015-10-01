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

import com.ut.tekir.entities.ord.TekirOrderNote;
import com.ut.tekir.entities.shp.TekirShipmentNote;

/**
 * İrsaliyenin bağlı olduğu siparişleri ve siparişlerin bağlı
 * olduğu irsaliyeleri tutan model sınıfımızdır.
 * @author sinan.yumak
 *
 */
@Entity
@Table(name="ORDER_SHIPMENT_LINK")
public class OrderShipmentLink implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.TABLE,generator="genericSeq")
    @Column(name="ID")
    private Long id;

	@ManyToOne
	@JoinColumn(name="ORDER_NOTE_ID")
	private TekirOrderNote orderNote;

	@ManyToOne
	@JoinColumn(name="SHIPMENT_NOTE_ID")
	private TekirShipmentNote shipmentNote;
	
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (this.getId() != null ? this.getId().hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof OrderShipmentLink)) {
            return false;
        }
        OrderShipmentLink other = (OrderShipmentLink)object;
        if (this.getId() != other.getId() && (this.getId() == null || !this.getId().equals(other.getId()))) return false;
        return true;
    }

    @Override
    public String toString() {
        return "com.ut.tekir.entities.OrderShipmentLink[id=" + getId() + "]";
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public TekirOrderNote getOrderNote() {
		return orderNote;
	}

	public void setOrderNote(TekirOrderNote orderNote) {
		this.orderNote = orderNote;
	}

	public TekirShipmentNote getShipmentNote() {
		return shipmentNote;
	}

	public void setShipmentNote(TekirShipmentNote shipmentNote) {
		this.shipmentNote = shipmentNote;
	}

}
