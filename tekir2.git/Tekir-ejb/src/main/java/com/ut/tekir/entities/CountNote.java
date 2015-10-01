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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import javax.persistence.Transient;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.ForeignKey;

import com.ut.tekir.framework.TekirRuntimeException;
import com.ut.tekir.stock.CountNoteInputModel;

/**
 * Sayım fişi master bilgilerini tutan sınıftır.
 * @author sinan.yumak
 *
 */
@Entity
@Table(name="COUNT_NOTE")
public class CountNote extends DocumentBase implements Serializable {

	private static final long serialVersionUID = 1L;
	
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE,generator="genericSeq")
    @Column(name="ID")
    private Long id;

    /**
     * Sayım yapılan depo bilgisidir.
     */
    @ManyToOne
    @JoinColumn(name="WAREHOUSE_ID")
    @ForeignKey(name="FK_COUNTNOTE_WAREHOUSEID")
    private Warehouse warehouse;

    @Column(name="STATUS")
    @Enumerated(EnumType.ORDINAL)
    private CountStatus status = CountStatus.Open;
    
    /**
     * Miktarlı sayım olup olmadığı bilgisini tutar.
     */
    @Column(name="HAS_QUANTITY")
    private boolean hasQuantity = true;

    /**
     * Onaylı sayım olup olmadığı bilgisini tutar.
     */
    @Column(name="APPROVED")
    private boolean approved = false;
    
    @OneToMany(mappedBy="owner", cascade=CascadeType.ALL)
    @Cascade(value=org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    private List<CountNoteItem> items = new ArrayList<CountNoteItem>();

    @Transient
    private Map<Product, Integer> itemMap = new HashMap<Product, Integer>();

	public Map<Product, Integer> getItemMap() {
    	if (itemMap.isEmpty()) {
    		initItemMap();
    	} 
    	return itemMap;
	}

	public void setItemMap(Map<Product, Integer> itemMap) {
		this.itemMap = itemMap;
	}

	public void initItemMap() {
    	for (CountNoteItem item : items) {
    		if ( ! itemMap.containsKey(item.getProduct()) ) {
    			itemMap.put(item.getProduct(), item.getQuantity());
    		}
    	}
	}
    
    public void addToItems(CountNoteInputModel model) {
    	Product product = model.getProduct();
    	if (product == null) {
    		model.clearBarcode();
    		throw new TekirRuntimeException("Ürün boş bırakılamaz!");
    	}
    	if ( getItemMap().containsKey( product ) ) {
    		if ( model.isAddition() ) {
    			itemMap.put(product, itemMap.get( product ) + model.getQuantity());
    		} else {
    			itemMap.put(product, itemMap.get( product ) - model.getQuantity());
    		}
    	} else {
    		itemMap.put( product, model.getQuantity() );

    		createNewItem(model);
    	}
    }

    public void updateItemQuantities() {
    	for ( CountNoteItem item : items ) {
    		item.setQuantity( getItemMap().get( item.getProduct() ) );
    	}
    }

    private void createNewItem(CountNoteInputModel model) {
    	CountNoteItem item = new CountNoteItem(model.getProduct(), 
    										   model.getQuantity(), 
    										   model.getPrice(),
    										   model.getBarcode(),
    										   model.getUser(),
    										   model.getInfo());

    	item.setOwner(this);
    	this.items.add( item );
    }
        
    @Override
	public DocumentType getDocumentType() {
		return DocumentType.CountNote;
	}

    @Override
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public List<CountNoteItem> getItems() {
		return items;
	}

	public void setItems(List<CountNoteItem> items) {
		this.items = items;
	}

	public Warehouse getWarehouse() {
		return warehouse;
	}

	public void setWarehouse(Warehouse warehouse) {
		this.warehouse = warehouse;
	}

	public CountStatus getStatus() {
		return status;
	}

	public void setStatus(CountStatus status) {
		this.status = status;
	}

	public boolean isHasQuantity() {
		return hasQuantity;
	}

	public void setHasQuantity(boolean hasQuantity) {
		this.hasQuantity = hasQuantity;
	}

	public boolean isApproved() {
		return approved;
	}

	public void setApproved(boolean approved) {
		this.approved = approved;
	}

}
