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

package com.ut.tekir.stock;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateful;

import net.sf.jasperreports.engine.JRException;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.core.Conversation;

import com.ut.tekir.entities.MoneySet;
import com.ut.tekir.entities.OrderItem;
import com.ut.tekir.entities.OrderNote;
import com.ut.tekir.entities.ProductType;
import com.ut.tekir.entities.Quantity;
import com.ut.tekir.entities.ShipmentItem;
import com.ut.tekir.entities.ShipmentNote;
import com.ut.tekir.entities.ShipmentOrderLink;
import com.ut.tekir.entities.TradeAction;
import com.ut.tekir.framework.BaseConsts;
import com.ut.tekir.framework.CalendarManager;
import com.ut.tekir.framework.EntityBase;
import com.ut.tekir.framework.JasperHandlerBean;
import com.ut.tekir.framework.SequenceManager;
import com.ut.tekir.framework.SequenceType;

/**
 *
 * @author haky
 */
//@Stateful
//@Name("saleShipmentHome")
@Scope(value = ScopeType.CONVERSATION)
public class SaleShipmentHomeBean extends EntityBase<ShipmentNote> implements SaleShipmentHome<ShipmentNote>{

	private List<OrderNote> orderNotes =new ArrayList<OrderNote>();
	
	@In(create = true)
    SequenceManager sequenceManager;
    
	@In
    CalendarManager calendarManager;
    
	@In
    ProductTxnAction productTxnAction;
	
	@In(create=true)
    StockSuggestion stockSuggestion;
	
	@In
	JasperHandlerBean jasperReport;

    @Create 
    @Begin(flushMode = org.jboss.seam.annotations.FlushModeType.MANUAL)
    public void init() {
    }

    @Out(required = false)
    public ShipmentNote getSaleShipment() {
        return getEntity();
    }

    @In(required = false)
    public void setSaleShipment(ShipmentNote shipmentNote) {
        setEntity(shipmentNote);
    }

    @Override
    public void createNew() {
        log.debug("Yeni ShipmentNote");

        entity = new ShipmentNote();
        entity.setAction(TradeAction.Sale);
        entity.setActive(true);
        entity.setSerial(sequenceManager.getNewSerialNumber(SequenceType.SERIAL_SHIPMENT_SALE));
        //entity.setReference(sequenceManager.getNewSerialNumber(SequenceType.REFERENCE_SHIPMENT_SALE));
        entity.setDate(calendarManager.getCurrentDate());
        entity.setTime(new java.util.Date());
        //FIXME:burada ne olması gerekiyor? Hata yı engellemek için 
        //ataması yapıldı.
        stockSuggestion.setProductType(ProductType.Product);
        stockSuggestion.setDisableProductCombo(true);
    }

    @Transactional
    @Override
    public String save() {

        //TODO: Hatalara dil desteği eklenecek
        Boolean hata = false;

        try {
            
            if (entity.getItems().size() == 0) {
                facesMessages.add("En az bir detay girmelisiniz!");
                hata = true;
            }

            //TODO: Metinlere dildesteği eklenecek
            for (ShipmentItem it : entity.getItems()) {

                if (it.getProduct() == null) {
                    facesMessages.add("Stok seçmelisiniz!");
                    hata = true;
                }

                if (it.getQuantity().getValue() <= 0) {
                    facesMessages.add("Sıfırdan büyük bir değer girmelisiniz");
                    hata = true;
                }

            }
            

            if (hata) {
                throw new RuntimeException("Hata!");
            } else {
            	
            	for (ShipmentItem it : entity.getItems()) {
            		
            		if (it.getOrderItemId() != null) {
            			OrderItem oi = entityManager.find(OrderItem.class, it.getOrderItemId());
            			if (oi != null) {
        					oi.setClosedQuantity(it.getQuantity().getValue() + oi.getClosedQuantity());
        					
        					if (oi.getClosedQuantity() > oi.getQuantity().getValue()) {
        						oi.setClosedQuantity(oi.getQuantity().getValue());
        					}
        					
            				entityManager.persist(oi);
            			}
            		}
            	}
            }


        } catch (Exception e) {
            log.error("Hata :", e);
            return BaseConsts.FAIL;
        }

        
        String res = super.save();

        if (BaseConsts.SUCCESS.equals(res)) {
            productTxnAction.saveShipmentNote(entity);
        }

        return res;
        
    }

    @Override
    public String delete() {

        productTxnAction.deleteShipmentNote(entity);

        return super.delete();
    }

    public void createNewLine() {
        manualFlush();
        if (entity == null) {
            return;
        }

        log.debug("EntityManager.FlushMode : #0", entityManager.getFlushMode());

        ShipmentItem it = new ShipmentItem();
        it.setOwner(entity);
        entity.getItems().add(it);
        log.debug("yeni item eklendi");
    }

    public void deleteLine(ShipmentItem item) {
        manualFlush();
        if (entity == null) {
            return;
        }
        log.debug("Kayıt Silinecek : {0}", item);
        entity.getItems().remove(item);
    }

    public void deleteLine(Integer ix) {
        manualFlush();
        if (entity == null) {
            return;
        }
        log.debug("Kayıt Silinecek IX : {0}", ix);
        log.debug("Toplam Kayıt : {0}", entity.getItems().size());
        Object o = entity.getItems().remove(ix.intValue());
        log.debug("Silinen : {0}", o);
        log.debug("Toplam Kayıt : {0}", entity.getItems().size());
    }

    @SuppressWarnings("unchecked")
	public void buildOrderNotes() {

        log.info("OrderNote lar sorgulanıyor...");

        orderNotes = entityManager.createQuery("SELECT DISTINCT n " +
        																  "FROM OrderNote n " +
        																  "INNER JOIN n.items i " +
        																  "where n.action= :action " +
        																  "   and n.contact= :contact " +
        																  "   and i.quantity.value != i.closedQuantity " +
        																  "ORDER BY n.serial ")
																	.setParameter("action", TradeAction.Sale)
																	.setParameter("contact", entity.getContact())
																	.setHint("org.hibernate.cacheable", false)
																.getResultList();
        
        log.info("Siparişler #0", orderNotes);
        
        List<OrderNote> delItems = new ArrayList<OrderNote>();
        for (OrderNote orItem : orderNotes ) {
            
        	for(ShipmentOrderLink shOrlink: entity.getOrderLinks() ) {
        		
        		if(shOrlink.getOrderNote() == orItem){
        			
        		delItems.add(orItem);
        		break;
        		}
        		
        	}
        	
        }
 
        for (OrderNote orItem: delItems) {
        	
        	orderNotes.remove(orItem);
        }

    }
    
    public void selectOrderNote (int rowKey) {
    	
    	OrderNote selectedPurchaseOrder = orderNotes.get(rowKey);
    
    	ShipmentOrderLink shipmentOrderLink = new ShipmentOrderLink();
    	shipmentOrderLink.setOrderNote( selectedPurchaseOrder );
    	shipmentOrderLink.setShipmentNote( entity );
    	
    	entity.getOrderLinks().add( shipmentOrderLink);
    	orderNotes.remove( selectedPurchaseOrder );
    	
    	for (OrderItem item: selectedPurchaseOrder.getItems() ) {
    		
    		ShipmentItem shItem = new ShipmentItem();
    		
    		shItem.setOrderItemId( item.getId() );
    		shItem.setAmount( item.getAmount());
    		shItem.setInfo( item.getInfo());
    		shItem.setTaxAmount(item.getTaxAmount());
    		shItem.setTaxRate(item.getTaxRate());
    		shItem.setLineCode( item.getLineCode());
    		shItem.setOwner(entity);
    		shItem.setProduct( item.getProduct());
    		shItem.setTotalAmount( item.getTotalAmaount());
    		shItem.setUnitPrice(new MoneySet(item.getUnitPrice()));
    		
    		Quantity quantity = new Quantity();
    		quantity.setUnit(item.getQuantity().getUnit());
    		quantity.setValue(item.getQuantity().getValue() - item.getClosedQuantity());
    		shItem.setQuantity(quantity);
    		
    		entity.getItems().add(shItem);
    	}
 
    }
    
    public void removeOrderNote(int rowKey) {

    	OrderNote selectedNote = entity.getOrderLinks().get(rowKey).getOrderNote();

    	List<ShipmentItem> delItems = new ArrayList<ShipmentItem>();
    	
    	for ( ShipmentItem shItem: entity.getItems() ) {
    	
	    	for ( OrderItem orItem: selectedNote.getItems()) {
	    		
	    		if (shItem.getOrderItemId() == orItem.getId()) {
	    			
	    			delItems.add(shItem);
	    			break;
	    		}
	    		
	    	}
	    	
    	}
    	
    	for (ShipmentItem item: delItems ) {
    		
    		entity.getItems().remove(item);
    	}
    	
    	entity.getOrderLinks().remove(rowKey);
    	
    	orderNotes.add(selectedNote);
    }
    
    public void selectProduct(Integer ix) {
        manualFlush();
        if (entity == null) {
            return;
        }
        log.debug("İşlenecek IX : {0}", ix);

        ShipmentItem o = entity.getItems().get(ix.intValue());
        if (o != null && o.getProduct() != null) {
            o.getQuantity().setUnit(o.getProduct().getUnit());
        }
    }

    public void manualFlush() {
        Conversation.instance().changeFlushMode(org.jboss.seam.annotations.FlushModeType.MANUAL);
    }

	public List<OrderNote> getOrderNotes() {
		 
		if (orderNotes == null) {
	            buildOrderNotes();
		}
		return orderNotes;
	}
	
	public void setOrderNotes(List<OrderNote> orderNotes) {
		this.orderNotes = orderNotes;
	}

	@SuppressWarnings("unchecked")
	public void print() {
		try {

			log.info("Shipment Print");

			Map params = new HashMap();
			params.put("invoice", entity.getId());
			
			List<ShipmentItem>itemProductList=new ArrayList<ShipmentItem>(); 
			
			if(entity.getItems() != null){
				for (int i = 0; i < entity.getItems().size(); i++) {
					if(entity.getItems().get(i).getProduct().getProductType()==ProductType.Product){
						itemProductList.add(entity.getItems().get(i));
					}
				}
			}
			jasperReport.printObjectToPDF( "satis_irsaliyesi_" + entity.getReference(), "satis_irsaliyesi", params, itemProductList);

		} catch (JRException ex) {
			log.error("Invoice print error", ex);
			facesMessages.add("İrsaliye yazdırılamadı!");
		} catch (FileNotFoundException e) {
			log.error("Invoice template not found", e);
			facesMessages.add("İrsaliye yazdırma şablonu bulunamadı!");
		}
	}
}
