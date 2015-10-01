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
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;

/**
 * Entity class ShipmentNote
 * 
 * @author haky
 */
@Entity
@Table(name="SHIPMENT_NOTE")
public class ShipmentNote extends DocumentBase implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE,generator="genericSeq")
    @Column(name="ID")
    private Long id;
    
    @ManyToOne
    @JoinColumn(name="CONTACT_ID")
    private Contact contact;
    
    @ManyToOne
    @JoinColumn(name="WAREHOUSE_ID")
    private Warehouse warehouse;
    
    
    @OneToMany(mappedBy="owner", cascade=CascadeType.ALL)
    @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    private List<ShipmentItem> items = new ArrayList<ShipmentItem>();
    
    @OneToMany(mappedBy="shipmentNote", cascade=CascadeType.ALL)
    @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    private List<ShipmentOrderLink> orderLinks = new ArrayList<ShipmentOrderLink>();
    
    @ManyToOne
    @JoinColumn(name="INVOICE_ID")
    private Invoice invoice;
    
    @Column(name="TRADE_ACTION")
    @Enumerated(EnumType.ORDINAL)
    private TradeAction action;
    
    @Column(name="WRITE_INVOICE")
    private Boolean writeInvoice = Boolean.TRUE;

    @Override
	public DocumentType getDocumentType() {
		return action.equals(TradeAction.Purchase) ? DocumentType.PurchaseShipmentNote : DocumentType.SaleShipmentNote;
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

    public Warehouse getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(Warehouse warehouse) {
        this.warehouse = warehouse;
    }

	public List<ShipmentItem> getItems() {
        return items;
    }

    public void setItems(List<ShipmentItem> items) {
        this.items = items;
    }

    public Invoice getInvoice() {
        return invoice;
    }

    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
    }

    public TradeAction getAction() {
        return action;
    }

    public void setAction(TradeAction action) {
        this.action = action;
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
        if (!(object instanceof ShipmentNote)) {
            return false;
        }
        ShipmentNote other = (ShipmentNote)object;
        if (this.getId() != other.getId() && (this.getId() == null || !this.id.equals(other.id))) return false;
        return true;
    }

    @Override
    public String toString() {
        return "com.ut.tekir.entities.ShipmentNote[id=" + getId() + "]";
    }

	/**
	 * @return the orderLinks
	 */
	public List<ShipmentOrderLink> getOrderLinks() {
		return orderLinks;
	}

	/**
	 * @param orderLinks the orderLinks to set
	 */
	public void setOrderLinks(List<ShipmentOrderLink> orderLinks) {
		this.orderLinks = orderLinks;
	}

	public Boolean getWriteInvoice() {
		return writeInvoice;
	}

	public void setWriteInvoice(Boolean writeInvoice) {
		this.writeInvoice = writeInvoice;
	}
    
}
