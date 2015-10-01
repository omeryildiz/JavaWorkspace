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

package com.ut.tekir.framework;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.log.Log;


/**
*
* ** !! NATIVE QUERY KULLANILMISTIR !!  
* ** Farklı veritabanları için Düzenleme Yapılmalıdır.
* 
* Faturası kesilmemiş İrsaliye Listesini Görüntülemek için düzenlendi.
*
* @author yigit
* 
*/

@Stateful()
@Name( "shipmentNoteManager")
@Scope(value=ScopeType.APPLICATION)
@AutoCreate
public class ShipmentNoteManagerBean implements ShipmentNoteManager {

	@Logger
    protected Log log;
	
	@PersistenceContext(type=PersistenceContextType.EXTENDED)
    protected EntityManager em;
	
	
	@Create @Begin(join=true,flushMode = org.jboss.seam.annotations.FlushModeType.MANUAL)
    public void initManager(){
        
    }
    
    @Remove @Destroy
    public void destroy() {
    }
	
    private List<ShipNoteModel> shipmentNotes = null;
    private List<TransShipNoteModel> transShipNotes = null;
    private List<TransShipNoteModel> notLoadedTransNotes = null;
    
    @SuppressWarnings("unchecked")
	public List<ShipNoteModel> getShipmentNote(){
		List<ShipNoteModel> results = em.createNativeQuery("select contact_id as contactId,id,serial,reference,code,info,txndate ," +
														" DATEDIFF('day',txndate,CURRENT_DATE) as expiringDay," +
														" contact.name as contact" +
														" from shipment_note, contact" +
														" where contactId=contact.id and " +
														" trade_action=1 and " +
														" invoice_id is null " +
														" order by expiringDay desc").getResultList();
		
		return results;
	
	}
    
    @SuppressWarnings("unchecked")
	public List<TransShipNoteModel> getTransShipmentNote(){
		List<TransShipNoteModel> results = em.createNativeQuery("select contact_id as contactId,id,serial,reference,code,info,txndate ," +
														" DATEDIFF('day',txndate,CURRENT_DATE) as expiringDay," +
														" totalvalue ," +
														" contact.name as contact" +
														" from transport_shipment_note, contact" +
														" where contactId=contact.id and " +
														" invoice_id is null " +
														" order by expiringDay desc").getResultList();
		
		return results;
	
	}
    
    @SuppressWarnings("unchecked")
	public List<TransShipNoteModel> getNotLoadedTransNote(){
		List<TransShipNoteModel> results = em.createNativeQuery("select contact_id as contactId,id,serial,reference,code,info,txndate ," +
														" DATEDIFF('day',txndate,CURRENT_DATE) as expiringDay," +
														" totalvalue ," +
														" contact.name as contact" +
														" from transport_shipment_note, contact" +
														" where contactId=contact.id and " +
														" loading_note_id is null " +
														" order by expiringDay desc").getResultList();
		
		return results;
	
	}
    
    @SuppressWarnings("unchecked")
    public List<ShipNoteModel> buildShipmentNotes(){
    	
    	
    	List elements = getShipmentNote();
    	if(elements != null){
    		for(int i=0; i<elements.size(); i++){
    			ShipNoteModel note = new ShipNoteModel((Object[])elements.get(i));
    			shipmentNotes.add(note);
    		}
    	}
    	return shipmentNotes;
    }
    
    
    @SuppressWarnings("unchecked")
    public List<TransShipNoteModel> buildTransportShipmentNotes(){
    	
    	
    	List elements = getTransShipmentNote();
    	if(elements != null){
    		for(int i=0; i<elements.size(); i++){
    			TransShipNoteModel transNote = new TransShipNoteModel((Object[])elements.get(i));
    			transShipNotes.add(transNote);
    		}
    	}
    	return transShipNotes;
    }
    
    @SuppressWarnings("unchecked")
    public List<TransShipNoteModel> buildNotLoadedTransNotes(){
    	
    	
    	List elements = getNotLoadedTransNote();
    	if(elements != null){
    		for(int i=0; i<elements.size(); i++){
    			TransShipNoteModel notLoaded = new TransShipNoteModel((Object[])elements.get(i));
    			notLoadedTransNotes.add(notLoaded);
    		}
    	}
    	return notLoadedTransNotes;
    }
    
	public EntityManager getEm() {
		return em;
	}

	public void setEm(EntityManager em) {
		this.em = em;
	}

	public List<ShipNoteModel> getShipmentNotes() {
		
		if( shipmentNotes == null ){
			shipmentNotes = new ArrayList<ShipNoteModel>();
			shipmentNotes = buildShipmentNotes();
		}
		
		return shipmentNotes;
	}

	public void setShipmentNotes(List<ShipNoteModel> shipmentNotes) {
		this.shipmentNotes = shipmentNotes;
	}
	
	
	public void refreshShipmentNotes(){
		shipmentNotes = null;
	}

	public List<TransShipNoteModel> getTransShipNotes() {
		
		if( transShipNotes == null ){
			transShipNotes = new ArrayList<TransShipNoteModel>();
			transShipNotes = buildTransportShipmentNotes();
		}
		
		return transShipNotes;
	}

	public void setTransShipNotes(List<TransShipNoteModel> transShipNotes) {
		this.transShipNotes = transShipNotes;
	}
	
	public void refreshTransShipNotes(){
		transShipNotes = null;
	}

	public List<TransShipNoteModel> getNotLoadedTransNotes() {
		
		if( notLoadedTransNotes == null ){
			notLoadedTransNotes = new ArrayList<TransShipNoteModel>();
			notLoadedTransNotes = buildNotLoadedTransNotes();
		}
		
		return notLoadedTransNotes;
	}

	public void setNotLoadedTransNotes(List<TransShipNoteModel> notLoadedTransNotes) {
		this.notLoadedTransNotes = notLoadedTransNotes;
	}
	public void refreshNotLoadedTransNotes(){
		notLoadedTransNotes = null;
	}
}