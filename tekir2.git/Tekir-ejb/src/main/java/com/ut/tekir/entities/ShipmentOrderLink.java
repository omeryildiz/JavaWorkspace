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
/**
 * Sipariş ile irsaliye arasında bağ oluşturur. 
 * 
 * Sipariş ve İrsaliy earasında ki bağ many-to-many'dir 
 * ama JPA seviyesinde yapmak yerine tek yönlü olarak irsaliye ürerinden bağlıyoruz... 
 * 
 * @author haky
 *
 */
@Entity
@Table(name="SHIPMENT_ORDER_LINK")
public class ShipmentOrderLink implements Serializable{

	private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE,generator="genericSeq")
    @Column(name="ID")
    private Long id;
	
	@ManyToOne
	@JoinColumn(name="SHIPMENT_NOTE_ID")
	private ShipmentNote shipmentNote;
	
	@ManyToOne
	@JoinColumn(name="ORDER_NOTE_ID")
	private OrderNote orderNote;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public ShipmentNote getShipmentNote() {
		return shipmentNote;
	}

	public void setShipmentNote(ShipmentNote shipmentNote) {
		this.shipmentNote = shipmentNote;
	}

	public OrderNote getOrderNote() {
		return orderNote;
	}

	public void setOrderNote(OrderNote orderNote) {
		this.orderNote = orderNote;
	}

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ShipmentOrderLink)) {
            return false;
        }
        ShipmentOrderLink other = (ShipmentOrderLink)object;
        if (this.getId() != other.getId() && (this.getId() == null || !this.id.equals(other.id))) return false;
        return true;
    }

    @Override
    public String toString() {
        return "com.ut.tekir.entities.ShipmentOrderLink[id=" + getId() + "]";
    }

}
